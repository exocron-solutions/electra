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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.electra.server.DatabaseConstants;
import io.electra.server.data.loader.DataBlockLoader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DataStorageImpl implements DataStorage {

    private final LoadingCache<Integer, DataBlock> dataBlockCache;
    private final LoadingCache<Integer, Integer> nextBlockCache;

    private final SeekableByteChannel channel;

    DataStorageImpl(SeekableByteChannel channel) {
        this.channel = channel;

        dataBlockCache = CacheBuilder.newBuilder()
                .recordStats()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .build(new DataBlockLoader(channel));

        nextBlockCache = CacheBuilder.newBuilder()
                .recordStats()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public Integer load(Integer key) throws Exception {
                        channel.position(key * DatabaseConstants.DATA_BLOCK_SIZE);

                        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
                        channel.read(byteBuffer);
                        byteBuffer.flip();

                        return byteBuffer.hasRemaining() ? byteBuffer.getInt() : -1;
                    }
                });
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

            ByteBuffer byteBuffer = ByteBuffer.allocate(DatabaseConstants.DATA_BLOCK_SIZE);
            byteBuffer.putInt(nextBlock);
            byteBuffer.putInt(currentBlockContent.length);
            byteBuffer.put(currentBlockContent);

            byteBuffer.flip();

            DataBlock dataBlock = new DataBlock(currentBlock, currentBlockContent, nextBlock);
            dataBlockCache.put(currentBlock, dataBlock);
            nextBlockCache.put(currentBlock, nextBlock);

            try {
                channel.position(currentBlock * DatabaseConstants.DATA_BLOCK_SIZE);
                channel.write(byteBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public DataBlock readDataBlockAtIndex(int index) {
        try {
            return dataBlockCache.get(index);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int getPositionByIndex(int index) {
        return index * (DatabaseConstants.DATA_BLOCK_SIZE);
    }

    @Override
    public int readNextBlockAtIndex(int blockIndex) {
        try {
            return nextBlockCache.get(blockIndex);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public void writeNextBlockAtIndex(int blockIndex, int nextBlockIndex) {
        try {
            channel.position(blockIndex * DatabaseConstants.DATA_BLOCK_SIZE);

            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            byteBuffer.putInt(nextBlockIndex);
            byteBuffer.flip();

            channel.write(byteBuffer);

            nextBlockCache.put(blockIndex, nextBlockIndex);

            DataBlock dataBlock = dataBlockCache.getIfPresent(blockIndex);
            if (dataBlock != null) {
                dataBlock.setNextPosition(nextBlockIndex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
