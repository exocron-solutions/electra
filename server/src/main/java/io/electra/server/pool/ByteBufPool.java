package io.electra.server.pool;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JackWhite20 on 24.11.2017.
 */
public class ByteBufPool {

    /**
     * The initial size of the internal pool.
     */
    private static final int INITIAL_SIZE = 10;

    /**
     * The default byte buffer capacity.
     */
    private static final int CAPACITY = 128;

    /**
     * The internal byte buffer pool.
     */
    private static final List<ByteBuf> byteBufPool = new ArrayList<>(INITIAL_SIZE);

    /**
     * Returns a free byte buffer from the pool.
     *
     * @return A new usable byte buffer.
     */
    public static ByteBuf pooled() {
        synchronized (byteBufPool) {
            if (byteBufPool.size() != 0) {
                return byteBufPool.remove(byteBufPool.size() - 1);
            } else {
                return new ByteBuf(ByteBuffer.allocate(CAPACITY));
            }
        }
    }

    /**
     * Releases a byte buffer to the pool.
     *
     * @param byteBuf The byte buffer to release.
     */
    public static void release(ByteBuf byteBuf) {
        byteBuf.clear();

        synchronized (byteBufPool) {
            byteBufPool.add(byteBuf);
        }
    }

    /**
     * Cleans up all pooled byte buffers.
     */
    public static void clear() {
        byteBufPool.clear();
    }
}
