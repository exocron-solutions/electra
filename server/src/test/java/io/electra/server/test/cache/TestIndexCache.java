package io.electra.server.test.cache;

import io.electra.server.cache.Cache;
import io.electra.server.cache.IndexCache;
import io.electra.server.index.Index;
import io.electra.server.test.ElectraTest;
import io.electra.server.test.Order;
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