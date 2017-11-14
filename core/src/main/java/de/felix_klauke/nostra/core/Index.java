/*
 * MIT License
 *
 * Copyright (c) 2017 Felix Klauke
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

package de.felix_klauke.nostra.core;

import java.util.Arrays;

/**
 * @author Felix Klauke <serious@business.felix-klauke.de>
 */
class Index {

    private int keyHash;
    private int dataBlockCount;
    private int[] dataBlockIndices;

    Index(int keyHash, int dataBlockCount, int[] dataBlockIndices) {
        this.keyHash = keyHash;
        this.dataBlockCount = dataBlockCount;
        this.dataBlockIndices = dataBlockIndices;
    }

    int getKeyHash() {
        return keyHash;
    }

    int getDataBlockCount() {
        return dataBlockCount;
    }

    int[] getDataBlockIndices() {
        return dataBlockIndices;
    }

    @Override
    public String toString() {
        return "Index{" +
                "keyHash=" + keyHash +
                ", dataBlockCount=" + dataBlockCount +
                ", dataBlockIndices=" + Arrays.toString(dataBlockIndices) +
                '}';
    }
}
