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

import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import io.electra.core.DatabaseConstants;
import io.electra.core.alloc.ByteBufferAllocator;
import io.electra.core.cache.BlockCache;
import io.electra.core.cache.BlockChainCache;
import io.electra.core.cache.Cache;
import io.electra.core.iterator.DataBlockChainIndexIterator;
import io.electra.core.pool.PooledByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * The default implementation of the {@link DataStorage}.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DataStorageImpl implements DataStorage {

    /**
     * The logger to log all data actions.
     */
    private final Logger logger = LoggerFactory.getLogger(DataStorageImpl.class);

    /**
     * The channel to the underlying data file.
     */
    private final AsynchronousFileChannel channel;

    /**
     * The cache for data blocks.
     */
    private final Cache<Integer, DataBlock> dataBlockCache;

    /**
     * The cache for block chains.
     */
    private final Cache<Integer, Integer> blockChainCache;

    /**
     * Create a new instance of the data storage by its underlying channel.
     *
     * @param channel The channel.
     */
    DataStorageImpl(AsynchronousFileChannel channel) {
        this.channel = channel;

        logger.info("Initializing data caches...");
        dataBlockCache = new BlockCache(1, TimeUnit.MINUTES, 10000);
        blockChainCache = new BlockChainCache(1, TimeUnit.MINUTES, 10000);
        logger.info("Data caches initialized.");
    }

    @Override
    public void save(int[] allocatedBlocks, byte[] bytes) {
        for (int i = 0; i < allocatedBlocks.length; i++) {
            int currentBlock = allocatedBlocks[i];

            byte[] currentBlockContent = extractBlockContent(bytes, i);
            saveBlock(currentBlock, currentBlockContent, i == allocatedBlocks.length - 1 ? -1 : allocatedBlocks[i + 1]);
        }
    }

    /**
     * Extract the given chunk out of the bytes.
     *
     * @param bytes        The bytes.
     * @param currentBlock The current chunk.
     * @return The extracted chunk.
     */
    private byte[] extractBlockContent(byte[] bytes, int currentBlock) {
        int startPosition = currentBlock * DatabaseConstants.DATA_BLOCK_SIZE;
        int endPosition = (currentBlock + 1) * DatabaseConstants.DATA_BLOCK_SIZE - DatabaseConstants.NEXT_POSITION_OFFSET - DatabaseConstants.CONTENT_LENGTH_OFFSET;
        endPosition = endPosition >= bytes.length ? bytes.length : endPosition;

        return Arrays.copyOfRange(bytes, startPosition, endPosition);
    }

    /**
     * Save the given content at the given block with the given next block.
     *
     * @param currentBlock        The block.
     * @param currentBlockContent The content.
     * @param nextBlock           The next block in the chain.
     */
    private void saveBlock(int currentBlock, byte[] currentBlockContent, int nextBlock) {
        PooledByteBuffer byteBuffer = ByteBufferAllocator.allocate(DatabaseConstants.DATA_BLOCK_SIZE);
        byteBuffer.putInt(nextBlock);
        byteBuffer.putInt(currentBlockContent.length);
        byteBuffer.put(currentBlockContent);

        DataBlock dataBlock = new DataBlock(currentBlock, currentBlockContent, nextBlock);
        dataBlockCache.put(currentBlock, dataBlock);
        blockChainCache.put(currentBlock, nextBlock);

        byteBuffer.flip();

        writeBuffer(byteBuffer, currentBlock * DatabaseConstants.DATA_BLOCK_SIZE);
    }

    @Override
    public DataBlock getDataBlock(int index) {
        DataBlock dataBlock = dataBlockCache.get(index);

        if (dataBlock != null) {
            return dataBlock;
        }

        int position = index * DatabaseConstants.DATA_BLOCK_SIZE;
        PooledByteBuffer byteBuffer = ByteBufferAllocator.allocate(4, false);

        try {
            channel.read(byteBuffer.nio(), position).get();
            byteBuffer.flip();

            int nextPosition = byteBuffer.getInt();
            byteBuffer.release();

            byteBuffer = ByteBufferAllocator.allocate(4, false);
            channel.read(byteBuffer.nio(), position + 4).get();
            byteBuffer.flip();

            int length = byteBuffer.getInt();
            byteBuffer.release();

            PooledByteBuffer contentBuffer = ByteBufferAllocator.allocate(length, false);
            channel.read(contentBuffer.nio(), position + 4 + 4).get();
            contentBuffer.flip();

            dataBlock = new DataBlock(index, contentBuffer.array(), nextPosition);
            dataBlockCache.put(index, dataBlock);
            return dataBlock;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error while reading a data block.", e);
        }

        return null;
    }

    @Override
    public int getNextBlock(int blockIndex) {
        Integer next = blockChainCache.get(blockIndex);

        if (next == null) {
            return -1;
        }

        PooledByteBuffer byteBuffer = ByteBufferAllocator.allocate(4);

        try {
            int bytesRead = channel.read(byteBuffer.nio(), blockIndex * DatabaseConstants.DATA_BLOCK_SIZE).get();

            if (bytesRead < 4) {
                return -1;
            }

            byteBuffer.flip();

            int nextBlock = byteBuffer.getInt();

            blockChainCache.put(blockIndex, nextBlock);

            return nextBlock;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error while reading part of a block chain.", e);
        } finally {
            byteBuffer.release();
        }

        return -1;
    }

    @Override
    public void setNextBlock(int blockIndex, int nextBlockIndex) {
        PooledByteBuffer byteBuffer = ByteBufferAllocator.allocate(4);

        blockChainCache.put(blockIndex, nextBlockIndex);

        DataBlock dataBlock = dataBlockCache.get(blockIndex);
        if (dataBlock != null) {
            dataBlock.setNextPosition(nextBlockIndex);
        }

        byteBuffer.putInt(nextBlockIndex);
        byteBuffer.flip();

        writeBuffer(byteBuffer, blockIndex * DatabaseConstants.DATA_BLOCK_SIZE);
    }

    /**
     * Write the given byte buffer to disk.
     *
     * @param byteBuffer The byte buffer.
     * @param position   The position in the channel.
     */
    private void writeBuffer(PooledByteBuffer byteBuffer, int position) {
        channel.write(byteBuffer.nio(), position, byteBuffer, new CompletionHandler<Integer, PooledByteBuffer>() {
            @Override
            public void completed(Integer result, PooledByteBuffer attachment) {
                byteBuffer.release();
            }

            @Override
            public void failed(Throwable exc, PooledByteBuffer attachment) {
                logger.error("Error while writing buffer to disk.", exc);
                byteBuffer.release();
            }
        });
    }

    @Override
    public void close() {
        try {
            channel.force(false);
            channel.close();
        } catch (IOException e) {
            logger.error("Error while data resource closing.", e);
        }

        dataBlockCache.clear();
    }

    @Override
    public int[] getBlockChain(int dataFilePosition) {
        return Streams.stream(new DataBlockChainIndexIterator(this, dataFilePosition)).mapToInt(Integer::intValue).toArray();
    }

    @Override
    public TreeSet<Integer> readNextBlockChain(int dataFilePosition) {
        return Sets.newTreeSet(() -> new DataBlockChainIndexIterator(this, dataFilePosition));
    }
}
