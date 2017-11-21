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

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DatabaseImpl implements Database {

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

    @Override
    public byte[] get(String key) {
        Index index = indexStorage.getIndex(Arrays.hashCode(key.getBytes()));
        DataBlock dataBlock = dataStorage.readDataBlockAtIndex(index.getPosition());

        byte[] result = dataBlock.getContent();

        while (dataBlock.getNextPosition() != -1) {
            dataBlock = dataStorage.readDataBlockAtIndex(dataBlock.getNextPosition());
            result = Bytes.concat(result, dataBlock.getContent());
        }

        return result;
    }

    private static final Path indexFilePath = Paths.get("index.lctr");

    @Override
    public void close() {

    }

    private int calculateNeededBlocks(int contentLength) {
        return (int) Math.ceil(contentLength / (double) (DatabaseConstants.DATA_BLOCK_SIZE));
    }

    public static void main(String[] args) {
        Database database = new DatabaseImpl(dataFilePath, indexFilePath);

        database.save("Test", "Hey");
        database.save("Test1", "Ich bin auch ein Test");
        database.save("Test2", "Ich bin auch ein Test, naja wenn auch nur so ein bisschen aber das muss jeder selber wissen.");
        database.save("Test3", "Ich bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein Test");
        database.save("Test4", "Ich bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein TestIch bin auch ein Test");

        database.remove("Test1");

        database.remove("Test3");

        try {
            Files.delete(dataFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(String key, byte[] bytes) {
        int keyHash = Arrays.hashCode(key.getBytes());
        int blocksNeeded = calculateNeededBlocks(bytes.length);

        int[] allocatedBlocks = new int[blocksNeeded];

        System.out.println("NEW SAVE -------------------------------------------------------------------- NEW SAVE");
        System.out.println("Current empty index:    " + indexStorage.getCurrentEmptyIndex());

        for (int i = 0; i < allocatedBlocks.length; i++) {
            int currentFreeBlock = indexStorage.getCurrentEmptyIndex().getPosition();

            allocatedBlocks[i] = currentFreeBlock;
            int dataBlock = dataStorage.readNextBlockAtIndex(currentFreeBlock);

            indexStorage.getCurrentEmptyIndex().setPosition(dataBlock == -1 ? currentFreeBlock + 1 : dataBlock);
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
    }

    @Override
    public void remove(String key) {
        Index index = indexStorage.getIndex(Arrays.hashCode(key.getBytes()));
        Index currentEmptyIndex = indexStorage.getCurrentEmptyIndex();

        Integer[] affectedBlocks = Iterators.toArray(new DataBlockChainIndexIterator(dataStorage, index.getPosition()), Integer.class);
        Integer[] freeBlocks = Iterators.toArray(new DataBlockChainIndexIterator(dataStorage, currentEmptyIndex.getPosition()), Integer.class);

        for (int i = affectedBlocks.length - 1; i >= 0; i--) {
            int currentAffectedBlockIndex = affectedBlocks[i];

            System.out.println("----------------------------------------------------------------");
            System.out.println("Current block to delete: " + currentAffectedBlockIndex);

            if (currentAffectedBlockIndex < currentEmptyIndex.getPosition()) {
                freeBlocks = addPos(freeBlocks, 0, currentEmptyIndex.getPosition());
                dataStorage.writeNextBlockAtIndex(currentAffectedBlockIndex, currentEmptyIndex.getPosition());
                currentEmptyIndex.setPosition(currentAffectedBlockIndex);
            }

            for (int j = freeBlocks.length - 1; j >= 0; j--) {
                int currentFreeBlockIndex = freeBlocks[j];

                if (currentFreeBlockIndex < currentAffectedBlockIndex) {
                    System.out.println("Writing link: " + currentFreeBlockIndex + " to " + currentAffectedBlockIndex);
                    dataStorage.writeNextBlockAtIndex(currentFreeBlockIndex, currentAffectedBlockIndex);

                    if (j != freeBlocks.length - 1) {
                        dataStorage.writeNextBlockAtIndex(currentAffectedBlockIndex, freeBlocks[j + 1]);
                        System.out.println("Insert link: " + currentAffectedBlockIndex + " to " + freeBlocks[j + 1]);
                    }

                    freeBlocks = addPos(freeBlocks, j + 1, currentAffectedBlockIndex);

                    break;
                }
            }
        }

        indexStorage.removeIndex(index);
    }

    private Integer[] addPos(Integer[] a, int pos, int num) {
        Integer[] result = new Integer[a.length + 1];
        System.arraycopy(a, 0, result, 0, pos - 1 + 1);
        result[pos] = num;
        System.arraycopy(a, pos, result, pos + 1, a.length - pos);
        return result;
    }
}
