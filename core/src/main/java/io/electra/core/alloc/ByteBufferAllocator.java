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

package io.electra.core.alloc;

import io.electra.core.DatabaseConstants;
import io.electra.core.pool.ByteBufferPool;
import io.electra.core.pool.Pool;
import io.electra.core.pool.PooledByteBuffer;

import java.nio.ByteBuffer;

/**
 * The central entry point if you want to allocate {@link ByteBuffer}. To gain more control over the buffers
 * and their pools we are using our {@link PooledByteBuffer}.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class ByteBufferAllocator {

    /**
     * The minimum size of the allocated byte buffers.
     */
    private static int min;

    /**
     * The maximum size of the allocated byte buffers.
     */
    private static int max;

    /**
     * The amount of allocated byte buffers.
     */
    private static int times;

    /**
     * The accumulated amount of allocated bytes.
     */
    private static long capacity;

    /**
     * A pool for buffers with a size of four. Often needed for single integer reads.
     */
    private static Pool<PooledByteBuffer> minimumBufferPool = new ByteBufferPool(DatabaseConstants.INTEGER_BYTE_SIZE, true);

    /**
     * A pool for buffers with the size of an index.
     */
    private static Pool<PooledByteBuffer> indexBufferPool = new ByteBufferPool(DatabaseConstants.INDEX_BLOCK_SIZE, true);

    /**
     * A pool for buffers with the size of a full data block.
     */
    private static Pool<PooledByteBuffer> fullBufferPool = new ByteBufferPool(DatabaseConstants.DATA_BLOCK_SIZE, false);

    /**
     * Allocate a byte buffer with the given size. May be pooled by default.
     *
     * @param size The size.
     * @return The byte buffer.
     */
    public static PooledByteBuffer allocate(int size) {
        return allocate(size, true);
    }

    /**
     * Allocate a byre buffer with the given size and optional pooling.
     *
     * @param size The size.
     * @param allowPooling If pooling should be allowed.
     *
     * @return The byte buffer.
     */
    public static PooledByteBuffer allocate(int size, boolean allowPooling) {
        if (size < 0) {
            throw new IllegalArgumentException("Someone tried to allocate a byte buffer with a size lower than zero.");
        }

        times++;
        capacity += size;

        if (size > max) {
            max = size;
        }

        if (size < min) {
            min = size;
        }

        if (allowPooling) {
            if (size == 4) {
                return minimumBufferPool.acquire();
            } else if (size == DatabaseConstants.INDEX_BLOCK_SIZE) {
                return indexBufferPool.acquire();
            } else if (size == DatabaseConstants.DATA_BLOCK_SIZE) {
                return fullBufferPool.acquire();
            }
        }

        return new PooledByteBuffer(ByteBuffer.allocate(size), null);
    }

    /**
     * Get the accumulated size of all allocated byte buffers.
     *
     * @return The accumulated size.
     */
    public static long getCapacity() {
        return capacity;
    }

    /**
     * Get the amount of allocated buffers.
     *
     * @return The amount.
     */
    public static int getTimes() {
        return times;
    }

    /**
     * Get the max size of all allocated byte buffers.
     *
     * @return The max size.
     */
    public static int getMax() {
        return max;
    }

    /**
     * Get the min size of all allocated byte buffers.
     *
     * @return The min size.
     */
    public static int getMin() {
        return min;
    }
}
