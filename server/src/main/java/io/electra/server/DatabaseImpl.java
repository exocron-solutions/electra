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
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import io.electra.server.data.DataStorage;
import io.electra.server.data.DataStorageFactory;
import io.electra.server.index.Index;
import io.electra.server.index.IndexStorage;
import io.electra.server.index.IndexStorageFactory;
import io.electra.server.iterator.DataBlockChainIndexIterator;
import io.electra.server.loader.DatabaseValueLoader;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DatabaseImpl implements Database {

    private final IndexStorage indexStorage;
    private final DataStorage dataStorage;
    private final LoadingCache<Integer, byte[]> valueCache;
    private final TreeSet<Integer> freeBlocks;

    DatabaseImpl(Path dataFilePath, Path indexFilePath) {
        indexStorage = IndexStorageFactory.createIndexStorage(indexFilePath);
        dataStorage = DataStorageFactory.createDataStorage(dataFilePath);

        valueCache = CacheBuilder.newBuilder()
                .recordStats()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .build(new DatabaseValueLoader(indexStorage, dataStorage));

        freeBlocks = Sets.newTreeSet(() -> new DataBlockChainIndexIterator(dataStorage, indexStorage.getCurrentEmptyIndex().getDataFilePosition()));
    }

    @Override
    public void save(String key, String value) {
        save(key, value.getBytes());
    }

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

        for (int i = 0; i < allocatedBlocks.length; i++) {
            int nextFreeBlock = freeBlocks.pollFirst();

            if (freeBlocks.isEmpty()) {
                freeBlocks.add(nextFreeBlock + 1);
            }

            allocatedBlocks[i] = nextFreeBlock;
            indexStorage.getCurrentEmptyIndex().setDataFilePosition(nextFreeBlock + 1);
        }

        int firstBlock = allocatedBlocks[0];
        Index index = new Index(keyHash, false, firstBlock);

        indexStorage.saveIndex(index);

        dataStorage.save(allocatedBlocks, bytes);

        valueCache.put(keyHash, bytes);
    }

    @Override
    public void remove(String key) {
        int keyHash = Arrays.hashCode(key.getBytes());
        Index index = indexStorage.getIndex(keyHash);
        Index currentEmptyIndex = indexStorage.getCurrentEmptyIndex();

        Integer[] affectedBlocks = Iterators.toArray(new DataBlockChainIndexIterator(dataStorage, index.getDataFilePosition()), Integer.class);

        for (int i = affectedBlocks.length - 1; i >= 0; i--) {
            int currentAffectedBlockIndex = affectedBlocks[i];

            if (currentAffectedBlockIndex < currentEmptyIndex.getDataFilePosition()) {
                freeBlocks.add(currentEmptyIndex.getDataFilePosition());
                dataStorage.writeNextBlockAtIndex(currentAffectedBlockIndex, currentEmptyIndex.getDataFilePosition());
                currentEmptyIndex.setDataFilePosition(currentAffectedBlockIndex);
            }

            Integer lower = freeBlocks.lower(currentAffectedBlockIndex);

            if (lower != null) {
                dataStorage.writeNextBlockAtIndex(lower, currentAffectedBlockIndex);
            }

            Integer higher = freeBlocks.higher(currentAffectedBlockIndex);

            if (higher != null) {
                dataStorage.writeNextBlockAtIndex(currentAffectedBlockIndex, higher);
            }

            freeBlocks.add(currentAffectedBlockIndex);
        }

        indexStorage.removeIndex(index);

        valueCache.invalidate(keyHash);
    }
}
