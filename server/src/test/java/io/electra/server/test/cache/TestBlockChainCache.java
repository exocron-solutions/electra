package io.electra.server.test.cache;

import io.electra.server.cache.BlockChainCache;
import io.electra.server.cache.Cache;
import io.electra.server.test.ElectraTest;
import io.electra.server.test.Order;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class TestBlockChainCache extends ElectraTest {

    private Cache<Integer, Integer> cache = new BlockChainCache(-1, null, 1);
    private Cache<Integer, Integer> cacheWithExpire = new BlockChainCache(1, TimeUnit.MICROSECONDS, 1);

    @Test
    public void test() {
        execute();
    }

    @Order(1)
    public void testPut() {
        cache.put(0, 100);

        assertEquals(1, cache.size());
    }

    @Order(2)
    public void testGet() {
        assertEquals(100, (int) cache.get(0));
    }

    @Order(3)
    public void testGetNonExistentKey() {
        assertNull(cache.get(1));
    }

    @Order(4)
    public void testCacheClear() {
        cache.clear();

        assertEquals(0, cache.size());
    }

    @Order(5)
    public void testExpire() throws InterruptedException {
        cacheWithExpire.put(0, 1);

        Thread.sleep(5);

        assertNull(cacheWithExpire.get(0));
    }
}