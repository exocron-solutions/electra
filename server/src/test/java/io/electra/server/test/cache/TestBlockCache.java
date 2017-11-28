package io.electra.server.test.cache;

import io.electra.server.cache.BlockCache;
import io.electra.server.cache.Cache;
import io.electra.server.data.DataBlock;
import io.electra.server.test.ElectraTest;
import io.electra.server.test.Order;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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