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

import io.electra.server.btree.BTree;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class IndexStorageImpl implements IndexStorage {

    private final BTree<Integer, Index> currentIndices = new BTree<>();
    private final SeekableByteChannel channel;
    private Index currentEmptyIndex = new Index(0, true, 0);

    public IndexStorageImpl(SeekableByteChannel channel) {
        this.channel = channel;

        readIndices();
    }

    private void readIndices() {
        try {
            long contentSize = channel.size();
            ByteBuffer byteBuffer = ByteBuffer.allocate(Math.toIntExact(contentSize));
            channel.read(byteBuffer);
            byteBuffer.flip();

            while (byteBuffer.hasRemaining()) {
                Index index = readIndex(byteBuffer);

                if (index.isEmpty()) {
                    currentEmptyIndex = index;
                }

                saveIndex(index);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Index readIndex(ByteBuffer byteBuffer) {
        int keyHash = byteBuffer.getInt();
        boolean empty = byteBuffer.get() == 1;
        int position = byteBuffer.getInt();

        return new Index(keyHash, empty, position);
    }

    @Override
    public Index getCurrentEmptyIndex() {
        return currentEmptyIndex;
    }

    public void setCurrentEmptyIndex(Index currentEmptyIndex) {
        this.currentEmptyIndex = currentEmptyIndex;
    }

    @Override
    public void saveIndex(Index index) {
        currentIndices.insert(index.getKeyHash(), index);
    }

    @Override
    public Index getIndex(int keyHash) {
        return currentIndices.search(keyHash);
    }
}
