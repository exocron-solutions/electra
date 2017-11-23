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

package io.electra.server.index;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class Index {

    private final int keyHash;
    private boolean empty;
    private int dataFilePosition;
    private int indexFilePosition;

    public Index(int keyHash, boolean empty, int position) {
        this.keyHash = keyHash;
        this.empty = empty;
        this.dataFilePosition = position;
    }

    public int getDataFilePosition() {
        return dataFilePosition;
    }

    public void setDataFilePosition(int dataFilePosition) {
        this.dataFilePosition = dataFilePosition;
    }

    int getKeyHash() {
        return keyHash;
    }

    boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    int getIndexFilePosition() {
        return indexFilePosition;
    }

    void setIndexFilePosition(int indexFilePosition) {
        this.indexFilePosition = indexFilePosition;
    }

    @Override
    public String toString() {
        return "Index{" +
                "keyHash=" + keyHash +
                ", empty=" + empty +
                ", dataFilePosition=" + dataFilePosition +
                ", indexFilePosition=" + indexFilePosition +
                '}';
    }
}
