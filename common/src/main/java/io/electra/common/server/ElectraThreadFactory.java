package io.electra.common.server;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class ElectraThreadFactory implements ThreadFactory {

    private final AtomicInteger id = new AtomicInteger(0);

    private String name;

    public ElectraThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(name + " #" + id.getAndIncrement());

        return thread;
    }
}
