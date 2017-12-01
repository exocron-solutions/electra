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

package io.electra.server.test.pool;

import io.electra.core.pool.ByteBufferPool;
import io.electra.core.pool.PooledByteBuffer;
import io.electra.server.test.ElectraTest;
import io.electra.server.test.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class TestByteBufferPool extends ElectraTest {

    private ByteBufferPool byteBufferPool;

    @Before
    public void before() {
        byteBufferPool = new ByteBufferPool(512, false);
    }

    @After
    public void after() {
        byteBufferPool.clear();
    }

    @Test
    public void test() {
        execute();
    }

    @Order(1)
    public void testByteBufferIsNonDirect() {
        assertEquals(false, byteBufferPool.acquire().isDirect());
    }

    @Order(2)
    public void testByteBufferIsDirect() {
        assertEquals(true, new ByteBufferPool(512, true).acquire().isDirect());
    }

    @Order(3)
    public void testByteBufferAcquire() {
        PooledByteBuffer acquire = byteBufferPool.acquire();

        assertNotNull(acquire);

        acquire.release();
    }

    @Order(4)
    public void testByteBufferSize() {
        PooledByteBuffer acquire = byteBufferPool.acquire();

        assertEquals(512, acquire.capacity());

        acquire.release();
    }

    @Order(5)
    public void testByteBufferPooling() {
        PooledByteBuffer acquire = byteBufferPool.acquire();
        int hashCode = acquire.hashCode();

        acquire.release();

        PooledByteBuffer next = byteBufferPool.acquire();
        assertEquals(hashCode, next.hashCode());
    }
}
