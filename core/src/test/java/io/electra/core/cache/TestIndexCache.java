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

package io.electra.core.cache;

import io.electra.core.ElectraTest;
import io.electra.core.Order;
import io.electra.core.index.Index;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class TestIndexCache extends ElectraTest {

    private Cache<Integer, Index> cache = new IndexCache(-1, null, 1);
    private Cache<Integer, Index> cacheWithExpire = new IndexCache(1, TimeUnit.MILLISECONDS, 1);

    @Test
    public void test() {
        execute();
    }

    @Order(1)
    public void testPut() {
        cache.put(0, new Index(0, true, 1646));

        assertEquals(1, cache.size());
    }

    @Order(2)
    public void testGet() {
        Index index = cache.get(0);

        assertNotNull(index);
        assertEquals(0, index.getKeyHash());
        assertTrue(index.isEmpty());
        assertEquals(1646, index.getDataFilePosition());
    }

    @Order(3)
    public void testGetNonExistentKey() {
        assertNull(cache.get(1));
    }

    @Order(4)
    public void testClear() {
        cache.clear();

        assertEquals(0, cache.size());
    }

    @Order(5)
    public void testExpire() throws InterruptedException {
        cacheWithExpire.put(0, new Index(0, true, 1646));

        assertNotNull(cacheWithExpire.get(0));

        Thread.sleep(10);

        assertNull(cacheWithExpire.get(0));
    }

    @Order(6)
    public void testInvalidate() {
        cache.put(0, new Index(0, true, 1646));

        assertNotNull(cache.get(0));

        cache.invalidate(0);

        assertNull(cache.get(0));
    }
}