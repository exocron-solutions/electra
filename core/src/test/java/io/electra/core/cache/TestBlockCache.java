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
import io.electra.core.data.DataBlock;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class TestBlockCache extends ElectraTest {

    private Cache<Integer, DataBlock> cache = new BlockCache(-1, null, 1);
    private Cache<Integer, DataBlock> cacheWithExpire = new BlockCache(5, TimeUnit.MILLISECONDS, 1);

    @Test
    public void test() {
        execute();
    }

    @Order(1)
    public void testPut() {
        cache.put(0, new DataBlock(0, new byte[] {0}, 1));

        assertEquals(1, cache.size());
    }

    @Order(2)
    public void testGet() {
        DataBlock dataBlock = cache.get(0);

        assertNotNull(dataBlock);
        assertEquals(0, dataBlock.getCurrentPosition());
        assertEquals(1, dataBlock.getContent().length);
        assertEquals(0, dataBlock.getContent()[0]);
        assertEquals(1, dataBlock.getNextPosition());
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
        cacheWithExpire.put(0, new DataBlock(0, new byte[] {0}, 1));

        assertNotNull(cacheWithExpire.get(0));

        Thread.sleep(10);

        assertNull(cacheWithExpire.get(0));
    }

    @Order(6)
    public void testInvalidate() {
        cache.put(0, new DataBlock(0, new byte[] {0}, 1));

        assertNotNull(cache.get(0));

        cache.invalidate(0);

        assertNull(cache.get(0));
    }
}