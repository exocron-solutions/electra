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

package io.electra.server.alloc;

import io.electra.server.DatabaseConstants;
import io.electra.server.pool.ByteBufferPool;
import io.electra.server.pool.Pool;
import io.electra.server.pool.PooledByteBuffer;

import java.nio.ByteBuffer;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class ByteBufferAllocator {

    private static int min;
    private static int max;
    private static int times;
    private static long capacity;

    private static Pool<PooledByteBuffer> byteBufferPool = new ByteBufferPool(4, true);
    private static Pool<PooledByteBuffer> byteBufferPool1 = new ByteBufferPool(DatabaseConstants.INDEX_BLOCK_SIZE, true);
    private static Pool<PooledByteBuffer> byteBufferPool2 = new ByteBufferPool(DatabaseConstants.DATA_BLOCK_SIZE, false);

    public static PooledByteBuffer allocate(int size) {
        return allocate(size, true);
    }

    public static PooledByteBuffer allocate(int size, boolean allowPooling) {
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
                return byteBufferPool.acquire();
            } else if (size == DatabaseConstants.INDEX_BLOCK_SIZE) {
                return byteBufferPool1.acquire();
            } else if (size == DatabaseConstants.DATA_BLOCK_SIZE) {
                return byteBufferPool2.acquire();
            }
        }

        return new PooledByteBuffer(ByteBuffer.allocate(size), null);
    }

    static long getCapacity() {
        return capacity;
    }

    static int getTimes() {
        return times;
    }

    public static int getMax() {
        return max;
    }

    public static int getMin() {
        return min;
    }
}
