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

import com.google.common.primitives.Bytes;
import io.electra.server.data.DataBlock;
import io.electra.server.data.DataStorage;
import io.electra.server.data.DataStorageFactory;
import io.electra.server.index.Index;
import io.electra.server.index.IndexStorage;
import io.electra.server.index.IndexStorageFactory;

import java.nio.file.Path;
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
            DataBlock dataBlock = dataStorage.readDataBlockAtIndex(currentFreeBlock);

            indexStorage.getCurrentEmptyIndex().setPosition(dataBlock == null ? currentFreeBlock + 1 : dataBlock.getNextPosition());
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

    @Override
    public void remove(String key) {

    }

    @Override
    public void close() {

    }

    private int calculateNeededBlocks(int contentLength) {
        return (int) Math.ceil(contentLength / (double) (DatabaseConstants.DATA_BLOCK_SIZE));
    }
}
