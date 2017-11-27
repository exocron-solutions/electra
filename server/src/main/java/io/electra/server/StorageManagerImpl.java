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

import com.google.common.collect.Sets;
import com.google.common.primitives.Bytes;
import io.electra.server.data.DataBlock;
import io.electra.server.data.DataStorage;
import io.electra.server.index.Index;
import io.electra.server.index.IndexStorage;
import io.electra.server.iterator.DataBlockChainIndexIterator;
import io.electra.server.storage.StorageManager;

import java.util.TreeSet;

/**
 * The default implementation of the {@link StorageManager}.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class StorageManagerImpl implements StorageManager {

    /**
     * The storage of all indices.
     */
    private final IndexStorage indexStorage;

    /**
     * The storage of all data.
     */
    private final DataStorage dataStorage;

    /**
     * Stores all blocks that are currently free for writing.
     */
    private TreeSet<Integer> freeBlocks;

    /**
     * Create a new instance of the {@link StorageManager} by its underlying components.
     *
     * @param indexStorage The index storage.
     * @param dataStorage  The data storage.
     */
    StorageManagerImpl(IndexStorage indexStorage, DataStorage dataStorage) {
        this.indexStorage = indexStorage;
        this.dataStorage = dataStorage;

        // Read all currently free indices.
        freeBlocks = Sets.newTreeSet(() -> new DataBlockChainIndexIterator(dataStorage, indexStorage.getCurrentEmptyIndex().getDataFilePosition()));
    }

    /**
     * Calculate the amount of data blocks that will be necessary for data with the given length.
     *
     * @param contentLength The length.
     *
     * @return The amount of blocks.
     */
    private int calculateNeededBlocks(int contentLength) {
        return (int) Math.ceil(contentLength / (double) (DatabaseConstants.DATA_BLOCK_SIZE));
    }

    @Override
    public void save(int keyHash, byte[] bytes) {
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

        indexStorage.createIndex(keyHash, false, firstBlock);
        dataStorage.save(allocatedBlocks, bytes);
    }

    @Override
    public void close() {
        dataStorage.close();
        indexStorage.close();
    }

    @Override
    public void remove(int keyHash) {
        Index index = indexStorage.getIndex(keyHash);
        Index currentEmptyIndex = indexStorage.getCurrentEmptyIndex();

        if (index == null) {
            return;
        }

        int[] affectedBlocks = dataStorage.getBlockChain(index.getDataFilePosition());

        for (int i = affectedBlocks.length - 1; i >= 0; i--) {
            int currentAffectedBlockIndex = affectedBlocks[i];

            if (currentAffectedBlockIndex < currentEmptyIndex.getDataFilePosition()) {
                updateEmptyIndex(currentAffectedBlockIndex);
                continue;
            }

            updateLeftReference(currentAffectedBlockIndex);
            updateRightReference(currentAffectedBlockIndex);

            freeBlocks.add(currentAffectedBlockIndex);
        }

        indexStorage.removeIndex(index);
    }

    /**
     * I the case we have find an empty index on the right of a new empty index we have to link them. For example:
     * <p>
     * Old data:
     * 0 1 2 3 4 5 6
     * x o o x o x x
     * <p>
     * The user wants to delete the third data block. In this case we have to link 3 -> 4.
     *
     * @param currentAffectedBlockIndex The new empty block.
     */
    private void updateRightReference(int currentAffectedBlockIndex) {
        Integer higher = freeBlocks.higher(currentAffectedBlockIndex);
        if (higher != null) {
            dataStorage.setNextBlock(currentAffectedBlockIndex, higher);
        }
    }

    /**
     * I the case we have find an empty index on the left of a new empty index we have to link them. For example:
     * <p>
     * Old data:
     * 0 1 2 3 4 5 6
     * x x o x x x x
     * <p>
     * The user wants to delete the third data block. In this case we have to link 2 -> 3.
     *
     * @param currentAffectedBlockIndex The new empty block.
     */
    private void updateLeftReference(int currentAffectedBlockIndex) {
        Integer lower = freeBlocks.lower(currentAffectedBlockIndex);

        if (lower != null) {
            dataStorage.setNextBlock(lower, currentAffectedBlockIndex);
        }
    }

    /**
     * Update the index that points to the first empty block. In this case we have to add the current first empty
     * data block as a regular free block. Then we have to to write the reference from the new first empty data block
     * to the old first empty data block. At last we can set the new empty index into the empty index container
     * variable.
     *
     * @param currentAffectedBlockIndex The new first empty data block.
     */
    private void updateEmptyIndex(int currentAffectedBlockIndex) {
        Index currentEmptyIndex = indexStorage.getCurrentEmptyIndex();
        freeBlocks.add(currentEmptyIndex.getDataFilePosition());
        dataStorage.setNextBlock(currentAffectedBlockIndex, currentEmptyIndex.getDataFilePosition());
        currentEmptyIndex.setDataFilePosition(currentAffectedBlockIndex);
    }

    @Override
    public byte[] get(int keyHash) {
        Index index = indexStorage.getIndex(keyHash);

        if (index == null) {
            return null;
        }

        DataBlock dataBlock = dataStorage.getDataBlock(index.getDataFilePosition());

        byte[] result = dataBlock.getContent();

        while (dataBlock.getNextPosition() != -1) {
            dataBlock = dataStorage.getDataBlock(dataBlock.getNextPosition());
            result = Bytes.concat(result, dataBlock.getContent());
        }

        return result;
    }
}
