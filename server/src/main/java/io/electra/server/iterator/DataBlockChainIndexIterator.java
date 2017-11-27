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

package io.electra.server.iterator;

import io.electra.server.data.DataStorage;

import java.util.Iterator;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DataBlockChainIndexIterator implements Iterator<Integer> {

    private final DataStorage dataStorage;
    private int blockIndex;
    private int nextBlockIndex = -1;
    private boolean firstRun = true;

    public DataBlockChainIndexIterator(DataStorage dataStorage, int blockIndex) {
        this.dataStorage = dataStorage;
        this.blockIndex = blockIndex;
    }

    @Override
    public boolean hasNext() {
        return firstRun || (nextBlockIndex = dataStorage.getNextBlock(blockIndex)) != -1;

    }

    @Override
    public Integer next() {
        if (firstRun) {
            firstRun = false;
            return blockIndex;
        }

        if (nextBlockIndex == -1) {
            nextBlockIndex = dataStorage.getNextBlock(blockIndex);
        }

        blockIndex = nextBlockIndex;

        nextBlockIndex = -1;

        return blockIndex;
    }
}
