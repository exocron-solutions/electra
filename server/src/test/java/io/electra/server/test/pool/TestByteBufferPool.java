package io.electra.server.test.pool;

import io.electra.server.pool.ByteBufferPool;
import io.electra.server.pool.PooledByteBuffer;
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
