package io.electra.server.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by JackWhite20 on 27.11.2017.
 */
public class AbstractCache<Key, Value> implements Cache<Key, Value> {

    private final Map<Key, CacheEntry<Value>> cache;

    private long expire;

    private TimeUnit timeUnit;

    AbstractCache(Map<Key, CacheEntry<Value>> map, long expire, TimeUnit timeUnit, int expectedSize) {
        this.cache = map;
        this.expire = expire;
        this.timeUnit = timeUnit;

        // TODO: 27.11.2017 Cleanup delay
        if (expire > 0) {
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::cleanup, 1, 1, TimeUnit.MINUTES);
        }
    }

    private void cleanup() {
        for(Iterator<CacheEntry<Value>> it = cache.values().iterator(); it.hasNext(); ) {
            CacheEntry<Value> cacheEntry = it.next();

            long timestamp = cacheEntry.getExpireBy();
            if (System.currentTimeMillis() > timestamp) {
                it.remove();
            }
        }
    }

    private void remove(Key key) {
        synchronized (cache) {
            cache.remove(key);
        }
    }

    @Override
    public void put(Key key, Value value) {
        long expireBy = timeUnit != null ? System.currentTimeMillis() + timeUnit.toMillis(expire) : -1;

        synchronized (cache) {
            cache.put(key, new CacheEntry<>(expireBy, value));
        }
    }

    @Override
    public Value get(Key key) {
        synchronized (cache) {
            CacheEntry<Value> entry = cache.get(key);

            if (entry == null) {
                return null;
            }

            // Check expiration
            long timestamp = entry.getExpireBy();
            if (expire != - 1 && System.currentTimeMillis() > timestamp) {
                cache.remove(key);
                return null;
            }

            return entry.getValue();
        }
    }

    @Override
    public void invalidate(Key key) {
        remove(key);
    }

    @Override
    public int size() {
        synchronized (cache) {
            return cache.size();
        }
    }

    @Override
    public void clear() {
        synchronized (cache) {
            cache.clear();
        }
    }
}
