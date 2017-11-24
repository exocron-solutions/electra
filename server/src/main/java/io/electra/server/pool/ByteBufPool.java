package io.electra.server.pool;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JackWhite20 on 24.11.2017.
 */
public class ByteBufPool {

    private static final int INITIAL_SIZE = 10;

    private static final int CAPACITY = 128;

    private static final List<ByteBuf> byteBufPool = new ArrayList<>(INITIAL_SIZE);

    public static ByteBuf pooled() {
        synchronized (byteBufPool) {
            if (byteBufPool.size() != 0) {
                return byteBufPool.remove(byteBufPool.size() - 1);
            } else {
                return new ByteBuf(ByteBuffer.allocate(CAPACITY));
            }
        }
    }

    public static void release(ByteBuf byteBuf) {
        byteBuf.clear();

        synchronized (byteBufPool) {
            byteBufPool.add(byteBuf);
        }
    }

    public static void clear() {
        byteBufPool.clear();
    }
}
