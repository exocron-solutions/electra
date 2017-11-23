package io.electra.server.pool;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JackWhite20 on 23.11.2017.
 */
public class ByteBufferPool {

    private static final int INITIAL_SIZE = 4;

    private static final int CAPACITY = 128;

    private static final List<PooledByteBuffer> pooledBuffers = new ArrayList<>(INITIAL_SIZE);

    static {
        for (int i = 0; i < INITIAL_SIZE; i++) {
            pooledBuffers.add(new PooledByteBuffer(ByteBuffer.allocate(CAPACITY)));
        }
    }

    public static PooledByteBuffer free() {
        synchronized (pooledBuffers) {
            for (PooledByteBuffer pooledByteBuffer : pooledBuffers) {
                if (!pooledByteBuffer.isUsed()) {
                    pooledByteBuffer.used = true;
                    return pooledByteBuffer;
                }
            }

            PooledByteBuffer pooledByteBuffer = new PooledByteBuffer(ByteBuffer.allocate(CAPACITY));
            pooledByteBuffer.used = true;

            pooledBuffers.add(pooledByteBuffer);

            return pooledByteBuffer;
        }
    }

    static void release(PooledByteBuffer pooledBuffer) {
        pooledBuffer.clear();
        synchronized (pooledBuffers) {
            pooledBuffer.used = false;
        }
    }
}
