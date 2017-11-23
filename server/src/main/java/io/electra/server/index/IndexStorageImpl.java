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

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import io.electra.server.DatabaseConstants;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Queue;
import java.util.TreeMap;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class IndexStorageImpl implements IndexStorage {

    private final Queue<Integer> emptyIndices = Queues.newPriorityQueue();
    private final TreeMap<Integer, Index> currentIndices = Maps.newTreeMap();
    private final SeekableByteChannel channel;
    private int lastIndexPosition;
    private Index emptyDataIndex;

    IndexStorageImpl(SeekableByteChannel channel) {
        this.channel = channel;

        readIndices();
    }

    private void readIndices() {
        try {
            channel.position(0);
            long contentSize = channel.size();

            if (contentSize == 0) {
                initializeIndexFile();
                channel.position(0);
            }

            contentSize = channel.size();

            ByteBuffer byteBuffer = ByteBuffer.allocate(Math.toIntExact(contentSize));
            channel.read(byteBuffer);
            byteBuffer.flip();

            emptyDataIndex = readIndex(byteBuffer);

            while (byteBuffer.hasRemaining()) {
                Index index = readIndex(byteBuffer);
                index.setIndexFilePosition(byteBuffer.position() / DatabaseConstants.INDEX_BLOCK_SIZE);

                if (index.isEmpty()) {
                    emptyIndices.offer(index.getIndexFilePosition());
                }

                saveIndex(index);

                lastIndexPosition = index.getIndexFilePosition();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeIndexFile() {
        int position = allocateFreeBlock();
        Index index = new Index(-1, false, 0);
        index.setIndexFilePosition(position);

        writeIndex(0, index);
    }

    private Index readIndex(ByteBuffer byteBuffer) {
        int keyHash = byteBuffer.getInt();
        boolean empty = byteBuffer.get() == 1;
        int position = byteBuffer.getInt();

        return new Index(keyHash, empty, position);
    }

    @Override
    public Index getCurrentEmptyIndex() {
        return emptyDataIndex;
    }

    @Override
    public void saveIndex(Index index) {
        currentIndices.put(index.getKeyHash(), index);

        int freeBlock = allocateFreeBlock();
        index.setIndexFilePosition(freeBlock);
        writeIndex(freeBlock, index);
    }

    @Override
    public Index getIndex(int keyHash) {
        return currentIndices.get(keyHash);
    }

    @Override
    public void removeIndex(Index index) {
        emptyIndices.offer(index.getIndexFilePosition());

        index.setEmpty(true);
        writeIndex(index.getIndexFilePosition(), index);

        currentIndices.remove(index.getKeyHash());
    }

    @Override
    public void close() {
        writeIndex(0, emptyDataIndex);

        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeIndex(int position, Index index) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DatabaseConstants.INDEX_BLOCK_SIZE);
        byteBuffer.putInt(index.getKeyHash());
        byteBuffer.put((byte) (index.isEmpty() ? 1 : 0));
        byteBuffer.putInt(index.getDataFilePosition());
        byteBuffer.flip();

        try {
            channel.position(position * DatabaseConstants.INDEX_BLOCK_SIZE);
            channel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int allocateFreeBlock() {
        return emptyIndices.isEmpty() ? ++lastIndexPosition : emptyIndices.poll();
    }
}
