package io.electra.server.test.cache;

import io.electra.server.cache.Cache;
import io.electra.server.cache.DataCache;
import io.electra.server.test.ElectraTest;
import io.electra.server.test.Order;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class TestDataCache extends ElectraTest {

    private Cache<Integer, byte[]> cache = new DataCache(-1, null, 1);
    private Cache<Integer, byte[]> cacheWithExpire = new DataCache(5, TimeUnit.MILLISECONDS, 1);

    @Test
    public void test() {
        execute();
    }

    @Order(1)
    public void testPut() {
        cache.put(0, new byte[] {2, 1, 3});

        assertEquals(1, cache.size());
    }

    @Order(2)
    public void testGet() {
        byte[] bytes = cache.get(0);

        assertNotNull(bytes);
        assertEquals(3, bytes.length);
        assertEquals(2, bytes[0]);
        assertEquals(1, bytes[1]);
        assertEquals(3, bytes[2]);
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
        cacheWithExpire.put(0, new byte[] {9});

        assertNotNull(cacheWithExpire.get(0));

        Thread.sleep(10);

        assertNull(cacheWithExpire.get(0));
    }

    @Order(6)
    public void testInvalidate() {
        cache.put(0, new byte[] {2, 1, 3});

        assertNotNull(cache.get(0));

        cache.invalidate(0);

        assertNull(cache.get(0));
    }
}