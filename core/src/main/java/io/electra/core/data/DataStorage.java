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

package io.electra.core.data;

/**
 * The central entrypoint to access the data file. No
 *
 * The default implementation can be found at {@link DataStorageImpl}.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 */
public interface DataStorage {

    /**
     * Save the given data in the given blocks.
     *
     * @param allocatedBlocks The blocks allocated for the given data.
     * @param bytes           The data.
     */
    void save(int[] allocatedBlocks, byte[] bytes);

    /**
     * Get the data block at the given index.
     *
     * @param index The index.
     * @return The data block.
     */
    DataBlock getDataBlock(int index);

    /**
     * Get the block index of the block the given block index's block is pointing to.
     *
     * @param blockIndex The current block.
     * @return The next block.
     */
    int getNextBlock(int blockIndex);

    /**
     * Update the index the block of the block index is pointing to.
     *
     * @param blockIndex     The source.
     * @param nextBlockIndex The target.
     */
    void setNextBlock(int blockIndex, int nextBlockIndex);

    /**
     * Save and close all resources.
     */
    void close();

    /**
     * Get the chain of blocks beginning at the given block.
     *
     * @param dataFilePosition The first block.
     * @return The chain of blocks linked by their next block value.
     */
    int[] getBlockChain(int dataFilePosition);
}
