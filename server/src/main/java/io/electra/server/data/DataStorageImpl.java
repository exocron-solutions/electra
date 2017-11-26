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

package io.electra.server.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.electra.server.DatabaseConstants;
import io.electra.server.alloc.ByteBufferAllocator;
import io.electra.server.pool.PooledByteBuffer;
import net.openhft.koloboke.collect.map.IntIntMap;
import net.openhft.koloboke.collect.map.hash.HashIntIntMaps;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DataStorageImpl implements DataStorage {

    private final AsynchronousFileChannel channel;
    private final Cache<Integer, DataBlock> dataBlockCache;
    private final IntIntMap nextBlockCache;

    DataStorageImpl(AsynchronousFileChannel channel) {
        this.channel = channel;

        dataBlockCache = CacheBuilder.newBuilder()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .build();

        nextBlockCache = HashIntIntMaps.newUpdatableMap();
    }

    @Override
    public void save(int[] allocatedBlocks, byte[] bytes) {
        for (int i = 0; i < allocatedBlocks.length; i++) {
            int currentBlock = allocatedBlocks[i];

            int startPosition = i * DatabaseConstants.DATA_BLOCK_SIZE;
            int endPosition = (i + 1) * DatabaseConstants.DATA_BLOCK_SIZE - DatabaseConstants.NEXT_POSITION_OFFSET - DatabaseConstants.CONTENT_LENGTH_OFFSET;
            endPosition = endPosition >= bytes.length ? bytes.length : endPosition;

            byte[] currentBlockContent = Arrays.copyOfRange(bytes, startPosition, endPosition);

            int nextBlock = i == allocatedBlocks.length - 1 ? -1 : allocatedBlocks[i + 1];

            PooledByteBuffer byteBuffer = ByteBufferAllocator.allocate(DatabaseConstants.DATA_BLOCK_SIZE);
            byteBuffer.putInt(nextBlock);
            byteBuffer.putInt(currentBlockContent.length);
            byteBuffer.put(currentBlockContent);

            DataBlock dataBlock = new DataBlock(currentBlock, currentBlockContent, nextBlock);
            dataBlockCache.put(currentBlock, dataBlock);
            nextBlockCache.addValue(currentBlock, nextBlock);

            byteBuffer.flip();

            try {
                channel.write(byteBuffer.nio(), currentBlock * DatabaseConstants.DATA_BLOCK_SIZE, byteBuffer, new CompletionHandler<Integer, PooledByteBuffer>() {
                    @Override
                    public void completed(Integer result, PooledByteBuffer attachment) {
                        byteBuffer.release();
                    }

                    @Override
                    public void failed(Throwable exc, PooledByteBuffer attachment) {
                        byteBuffer.release();
                    }
                });
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public DataBlock readDataBlockAtIndex(int index) {
        DataBlock dataBlock = dataBlockCache.getIfPresent(index);

        if (dataBlock != null) {
            return dataBlock;
        }

        int position = index * DatabaseConstants.DATA_BLOCK_SIZE;
        PooledByteBuffer nextPositionByteBuffer = ByteBufferAllocator.allocate(4);
        Future<Integer> nextPositionReading = channel.read(nextPositionByteBuffer.nio(), index * DatabaseConstants.DATA_BLOCK_SIZE);

        return readDataBlock(nextPositionReading, nextPositionByteBuffer, position);
    }

    private DataBlock readDataBlock(Future<Integer> nextPositionReading, PooledByteBuffer nextPositionByteBuffer, int position) {
        PooledByteBuffer contentLengthByteBuffer = ByteBufferAllocator.allocate(4);
        PooledByteBuffer contentBuffer = null;

        try {
            int bytesRead = channel.read(contentLengthByteBuffer.nio(), position + 4).get();
            contentLengthByteBuffer.flip();

            if (bytesRead == -1) {
                return null;
            }

            contentBuffer = ByteBufferAllocator.allocate(contentLengthByteBuffer.getInt());
            channel.read(contentBuffer.nio(), position + 4 + 4).get();

            if (!nextPositionReading.isDone()) {
                nextPositionReading.get();
            }

            nextPositionByteBuffer.flip();

            return new DataBlock(position, contentBuffer.array(), nextPositionByteBuffer.getInt());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            contentLengthByteBuffer.release();

            if (contentBuffer != null) {
                contentBuffer.release();
            }

            contentLengthByteBuffer.release();
        }

        return null;
    }

    @Override
    public int readNextBlockAtIndex(int blockIndex) {
        int next = nextBlockCache.getOrDefault(blockIndex, -1);

        if (next == -1) {
            return next;
        }

        PooledByteBuffer byteBuffer = ByteBufferAllocator.allocate(4);

        try {
            int bytesRead = channel.read(byteBuffer.nio(), blockIndex * DatabaseConstants.DATA_BLOCK_SIZE).get();

            if (bytesRead < 4) {
                return -1;
            }

            byteBuffer.flip();

            int nextBlock = byteBuffer.getInt();

            nextBlockCache.addValue(blockIndex, nextBlock);

            return nextBlock;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            byteBuffer.release();
        }

        return -1;
    }

    @Override
    public void writeNextBlockAtIndex(int blockIndex, int nextBlockIndex) {
        PooledByteBuffer byteBuffer = ByteBufferAllocator.allocate(4);

        nextBlockCache.addValue(blockIndex, nextBlockIndex);

        DataBlock dataBlock = dataBlockCache.getIfPresent(blockIndex);
        if (dataBlock != null) {
            dataBlock.setNextPosition(nextBlockIndex);
        }

        byteBuffer.putInt(nextBlockIndex);
        byteBuffer.flip();

        channel.write(byteBuffer.nio(), blockIndex * DatabaseConstants.DATA_BLOCK_SIZE, byteBuffer, new CompletionHandler<Integer, PooledByteBuffer>() {
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

    @Override
    public void close() {
        try {
            channel.force(true);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
