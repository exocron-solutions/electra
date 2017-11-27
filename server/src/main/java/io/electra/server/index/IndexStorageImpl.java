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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * The logger to log actions regarding indices.
     */
    private final Logger logger = LoggerFactory.getLogger(IndexStorageImpl.class);

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

    /**
     * Create a new index storage instance by its underlying channel.
     *
     * @param channel The channel.
     */
    IndexStorageImpl(AsynchronousFileChannel channel) {
        this.channel = channel;
        currentIndices = HashIntObjMaps.newMutableMap();

        logger.info("Beginning to read indices.");
        readIndices();
        logger.info("Found {} indices.", currentIndices.size());
    }

    /**
     * Read all first index from the disk and continue to read the whole file if a first index exists.
     */
    private void readIndices() {
        PooledByteBuffer byteBuffer = ByteBufferAllocator.allocate(DatabaseConstants.INDEX_BLOCK_SIZE);

        Future<Integer> read = channel.read(byteBuffer.nio(), 0);

        try {
            if (read.get() < DatabaseConstants.INDEX_BLOCK_SIZE) {
                logger.info("The index file seems to be empty. Initializing index file...");
                initializeIndexFile();
                logger.info("Index file initialized.");

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
            logger.error("Error while reading initial index.", e);
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
            logger.error("Error while reading all indices.", e);
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
        logger.info("Closing index resource.");

        logger.info("Writing important data to disk...");
        writeIndex(0, emptyDataIndex);
        logger.info("Important data was written to disk.");

        try {
            channel.close();
        } catch (IOException e) {
            logger.error("Couldn't close index resources properly. ", e);
        }

        logger.info("Closed index resources.");
    }

    @Override
    public Index createIndex(int keyHash, boolean empty, int firstBlock) {
        Index index = new Index(keyHash, false, firstBlock);
        saveIndex(index);
        return index;
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
                logger.error("Error whole writing an index to disk.", exc);
                byteBuffer.release();
            }
        });
    }

    private int allocateFreeBlock() {
        return emptyIndices.isEmpty() ? lastIndexPosition++ : emptyIndices.poll();
    }
}
