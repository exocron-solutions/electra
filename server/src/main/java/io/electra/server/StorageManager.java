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
import com.google.common.collect.Sets;
import com.google.common.primitives.Bytes;
import io.electra.server.data.DataBlock;
import io.electra.server.data.DataStorage;
import io.electra.server.index.Index;
import io.electra.server.index.IndexStorage;
import io.electra.server.iterator.DataBlockChainIndexIterator;

import java.util.TreeSet;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
class StorageManager {

    private final IndexStorage indexStorage;
    private final DataStorage dataStorage;

    private TreeSet<Integer> freeBlocks;

    StorageManager(IndexStorage indexStorage, DataStorage dataStorage) {
        this.indexStorage = indexStorage;
        this.dataStorage = dataStorage;

        freeBlocks = Sets.newTreeSet(() -> new DataBlockChainIndexIterator(dataStorage, indexStorage.getCurrentEmptyIndex().getDataFilePosition()));
    }

    private int calculateNeededBlocks(int contentLength) {
        return (int) Math.ceil(contentLength / (double) (DatabaseConstants.DATA_BLOCK_SIZE));
    }

    void save(int keyHash, byte[] bytes) {
        int blocksNeeded = calculateNeededBlocks(bytes.length);

        int[] allocatedBlocks = new int[blocksNeeded];

        for (int i = 0; i < allocatedBlocks.length; i++) {
            int nextFreeBlock = freeBlocks.pollFirst();

            if (freeBlocks.isEmpty()) {
                freeBlocks.add(nextFreeBlock + 1);
            }

            allocatedBlocks[i] = nextFreeBlock;
        }
        indexStorage.getCurrentEmptyIndex().setDataFilePosition(freeBlocks.first());

        int firstBlock = allocatedBlocks[0];
        Index index = new Index(keyHash, false, firstBlock);

        indexStorage.saveIndex(index);

        dataStorage.save(allocatedBlocks, bytes);
    }

    void close() {
        dataStorage.close();
        indexStorage.close();
    }

    void remove(int keyHash) {
        Index index = indexStorage.getIndex(keyHash);
        Index currentEmptyIndex = indexStorage.getCurrentEmptyIndex();

        if (index == null) {
            return;
        }

        Integer[] affectedBlocks = Iterators.toArray(new DataBlockChainIndexIterator(dataStorage, index.getDataFilePosition()), Integer.class);

        for (int i = affectedBlocks.length - 1; i >= 0; i--) {
            int currentAffectedBlockIndex = affectedBlocks[i];

            if (currentAffectedBlockIndex < currentEmptyIndex.getDataFilePosition()) {
                freeBlocks.add(currentEmptyIndex.getDataFilePosition());
                dataStorage.writeNextBlockAtIndex(currentAffectedBlockIndex, currentEmptyIndex.getDataFilePosition());
                currentEmptyIndex.setDataFilePosition(currentAffectedBlockIndex);
                continue;
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
    }

    byte[] get(int keyHash) {
        Index index = indexStorage.getIndex(keyHash);

        if (index == null) {
            return null;
        }

        DataBlock dataBlock = dataStorage.readDataBlockAtIndex(index.getDataFilePosition());

        byte[] result = dataBlock.getContent();

        while (dataBlock.getNextPosition() != -1) {
            dataBlock = dataStorage.readDataBlockAtIndex(dataBlock.getNextPosition());
            result = Bytes.concat(result, dataBlock.getContent());
        }

        return result;
    }
}
