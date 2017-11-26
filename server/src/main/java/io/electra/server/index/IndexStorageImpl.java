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

import com.google.common.collect.Queues;
import io.electra.server.DatabaseConstants;
import io.electra.server.alloc.ByteBufferAllocator;
import io.electra.server.btree.BTree;
import io.electra.server.exception.MalformedIndexException;
import io.electra.server.pool.PooledByteBuffer;
import net.openhft.koloboke.collect.map.IntObjMap;
import net.openhft.koloboke.collect.map.hash.HashIntObjMaps;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class IndexStorageImpl implements IndexStorage {

    /**
     * Contains all indices of the currently free blocks.
     */
    private final Queue<Integer> emptyIndices = Queues.newPriorityQueue();

    /**
     * The channel to read abd write the file.
     */
    private final AsynchronousFileChannel channel;

    /**
     * All currently loaded indices.
     * <p>
     * NOTE: Currently we use an enhanced koloboke map. Alternative would be the {@link TreeMap} or a B+ Tree
     * like {@link BTree}.
     */
    private IntObjMap<Index> currentIndices;

    /**
     * The currently last known index position index in the index file.
     */
    private int lastIndexPosition = 0;

    /**
     * The index that points to the first empty data block.
     */
    private Index emptyDataIndex;

    IndexStorageImpl(AsynchronousFileChannel channel) {
        this.channel = channel;

        currentIndices = HashIntObjMaps.newMutableMap();
        readIndices();
    }

    private void readIndices() {
        PooledByteBuffer byteBuffer = ByteBufferAllocator.allocate(DatabaseConstants.INDEX_BLOCK_SIZE);

        Future<Integer> read = channel.read(byteBuffer.nio(), 0);

        try {
            if (read.get() < DatabaseConstants.INDEX_BLOCK_SIZE) {
                initializeIndexFile();
                emptyDataIndex = new Index(-1, true, 0);
            } else {
                byteBuffer.flip();
                int keyHash = byteBuffer.getInt();
                boolean empty = byteBuffer.get() == 1;
                int position = byteBuffer.getInt();

                emptyDataIndex = new Index(keyHash, empty, position);
                emptyDataIndex.setIndexFilePosition(0);

                processReadIndices();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        byteBuffer.release();
    }

    private void processReadIndices() {
        try {
            for (int i = 1; i < channel.size() / DatabaseConstants.INDEX_BLOCK_SIZE; i++) {
                PooledByteBuffer byteBuffer = ByteBufferAllocator.allocate(DatabaseConstants.INDEX_BLOCK_SIZE);
                Future<Integer> readFuture = channel.read(byteBuffer.nio(), i * DatabaseConstants.INDEX_BLOCK_SIZE);
                int result = readFuture.get();

                if (result == DatabaseConstants.INDEX_BLOCK_SIZE) {
                    byteBuffer.flip();

                    int keyHash = byteBuffer.getInt();

                    if (keyHash == 0 || keyHash == -1) {
                        continue;
                    }

                    boolean empty = byteBuffer.get() == 1;
                    int position = byteBuffer.getInt();

                    Index index = new Index(keyHash, empty, position);
                    index.setIndexFilePosition(i);

                    if (index.isEmpty()) {
                        emptyIndices.offer(index.getIndexFilePosition());
                    } else {
                        currentIndices.put(keyHash, index);
                    }

                    byteBuffer.release();
                } else if (result > 0 && result < DatabaseConstants.INDEX_BLOCK_SIZE) {
                    byteBuffer.release();
                    throw new MalformedIndexException("Got a malformed index.");
                }
            }

            lastIndexPosition = Math.toIntExact(channel.size() / DatabaseConstants.INDEX_BLOCK_SIZE);
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void initializeIndexFile() {
        Index index = new Index(-1, false, 0);
        index.setIndexFilePosition(0);
        writeIndex(0, index);
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
        PooledByteBuffer byteBuffer = ByteBufferAllocator.allocate(DatabaseConstants.INDEX_BLOCK_SIZE);

        byteBuffer.putInt(index.getKeyHash());
        byteBuffer.put((byte) (index.isEmpty() ? 1 : 0));
        byteBuffer.putInt(index.getDataFilePosition());
        byteBuffer.flip();

        channel.write(byteBuffer.nio(), position * DatabaseConstants.INDEX_BLOCK_SIZE, byteBuffer, new CompletionHandler<Integer, PooledByteBuffer>() {
            @Override
            public void completed(Integer result, PooledByteBuffer attachment) {
                byteBuffer.release();
            }

            @Override
            public void failed(Throwable exc, PooledByteBuffer attachment) {
                byteBuffer.release();
            }
        });
    }

    private int allocateFreeBlock() {
        return emptyIndices.isEmpty() ? lastIndexPosition++ : emptyIndices.poll();
    }
}
