/*
 * MIT License
 *
 * Copyright (c) 2017 Felix Klauke, JackWhite20
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.electra.server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterators;
import com.google.common.primitives.Bytes;
import io.electra.server.data.DataBlock;
import io.electra.server.data.DataStorage;
import io.electra.server.data.DataStorageFactory;
import io.electra.server.index.Index;
import io.electra.server.index.IndexStorage;
import io.electra.server.index.IndexStorageFactory;
import io.electra.server.iterator.DataBlockChainIndexIterator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DatabaseImpl implements Database {

    private final LoadingCache<Integer, byte[]> valueCache = CacheBuilder.newBuilder()
            .recordStats()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .build(new CacheLoader<Integer, byte[]>() {
                @Override
                public byte[] load(Integer keyHash) throws Exception {
                    return get(keyHash);
                }
            });

    private final IndexStorage indexStorage;
    private final DataStorage dataStorage;

    public DatabaseImpl(Path dataFilePath, Path indexFilePath) {
        indexStorage = IndexStorageFactory.createIndexStorage(indexFilePath);
        dataStorage = DataStorageFactory.createDataStorage(dataFilePath);
    }

    private static final Path dataFilePath = Paths.get("data.lctr");

    @Override
    public void save(String key, String value) {
        save(key, value.getBytes());
    }

    public static void main(String[] args) {
        Database database = new DatabaseImpl(dataFilePath, indexFilePath);

        int n = 1000000;

        long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            database.save("Key" + i, "Value" + i);
        }
        System.out.println("Saving " + n + " entries took " + (System.currentTimeMillis() - start) + "ms. ");

        start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            database.get("Key" + i);
        }
        System.out.println("Reading " + n + " entries took " + (System.currentTimeMillis() - start) + "ms. ");

        try {
            Files.delete(dataFilePath);
            Files.delete(indexFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        database.close();
    }

    private static final Path indexFilePath = Paths.get("index.lctr");

    @Override
    public byte[] get(String key) {
        int keyHash = Arrays.hashCode(key.getBytes());

        try {
            return valueCache.get(keyHash);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private byte[] get(int keyHash) {
        Index index = indexStorage.getIndex(keyHash);
        DataBlock dataBlock = dataStorage.readDataBlockAtIndex(index.getDataFilePosition());

        byte[] result = dataBlock.getContent();

        while (dataBlock.getNextPosition() != -1) {
            dataBlock = dataStorage.readDataBlockAtIndex(dataBlock.getNextPosition());
            result = Bytes.concat(result, dataBlock.getContent());
        }

        return result;
    }

    private int calculateNeededBlocks(int contentLength) {
        return (int) Math.ceil(contentLength / (double) (DatabaseConstants.DATA_BLOCK_SIZE));
    }

    @Override
    public void close() {
        dataStorage.close();
        indexStorage.close();
    }

    @Override
    public void save(String key, byte[] bytes) {
        int keyHash = Arrays.hashCode(key.getBytes());
        int blocksNeeded = calculateNeededBlocks(bytes.length);

        int[] allocatedBlocks = new int[blocksNeeded];

        System.out.println("NEW SAVE -------------------------------------------------------------------- NEW SAVE");
        System.out.println("Current empty index:    " + indexStorage.getCurrentEmptyIndex());

        for (int i = 0; i < allocatedBlocks.length; i++) {
            int currentFreeBlock = indexStorage.getCurrentEmptyIndex().getDataFilePosition();

            allocatedBlocks[i] = currentFreeBlock;
            int dataBlock = dataStorage.readNextBlockAtIndex(currentFreeBlock);

            indexStorage.getCurrentEmptyIndex().setDataFilePosition(dataBlock == -1 ? currentFreeBlock + 1 : dataBlock);
        }

        System.out.println("Allocated blocks:       " + Arrays.toString(allocatedBlocks));

        int firstBlock = allocatedBlocks[0];
        Index index = new Index(keyHash, false, firstBlock);

        indexStorage.saveIndex(index);

        System.out.println("KeyHash:                " + keyHash);
        System.out.println("First block:            " + firstBlock);
        System.out.println("Index:                  " + index);
        System.out.println("Allocated blocks:       " + Arrays.toString(allocatedBlocks));
        System.out.println("Current free index:     " + indexStorage.getCurrentEmptyIndex());

        dataStorage.save(allocatedBlocks, bytes);

        valueCache.put(keyHash, bytes);
    }

    @Override
    public void remove(String key) {
        int keyHash = Arrays.hashCode(key.getBytes());
        Index index = indexStorage.getIndex(keyHash);
        Index currentEmptyIndex = indexStorage.getCurrentEmptyIndex();

        Integer[] affectedBlocks = Iterators.toArray(new DataBlockChainIndexIterator(dataStorage, index.getDataFilePosition()), Integer.class);
        Integer[] freeBlocks = Iterators.toArray(new DataBlockChainIndexIterator(dataStorage, currentEmptyIndex.getDataFilePosition()), Integer.class);

        for (int i = affectedBlocks.length - 1; i >= 0; i--) {
            int currentAffectedBlockIndex = affectedBlocks[i];

            if (currentAffectedBlockIndex < currentEmptyIndex.getDataFilePosition()) {
                freeBlocks = addPos(freeBlocks, 0, currentEmptyIndex.getDataFilePosition());
                dataStorage.writeNextBlockAtIndex(currentAffectedBlockIndex, currentEmptyIndex.getDataFilePosition());
                currentEmptyIndex.setDataFilePosition(currentAffectedBlockIndex);
            }

            for (int j = freeBlocks.length - 1; j >= 0; j--) {
                int currentFreeBlockIndex = freeBlocks[j];

                if (currentFreeBlockIndex < currentAffectedBlockIndex) {
                    dataStorage.writeNextBlockAtIndex(currentFreeBlockIndex, currentAffectedBlockIndex);

                    if (j != freeBlocks.length - 1) {
                        dataStorage.writeNextBlockAtIndex(currentAffectedBlockIndex, freeBlocks[j + 1]);
                    }

                    freeBlocks = addPos(freeBlocks, j + 1, currentAffectedBlockIndex);

                    break;
                }
            }
        }

        indexStorage.removeIndex(index);

        valueCache.invalidate(keyHash);
    }

    private Integer[] addPos(Integer[] a, int pos, int num) {
        Integer[] result = new Integer[a.length + 1];
        System.arraycopy(a, 0, result, 0, pos - 1 + 1);
        result[pos] = num;
        System.arraycopy(a, pos, result, pos + 1, a.length - pos);
        return result;
    }
}
