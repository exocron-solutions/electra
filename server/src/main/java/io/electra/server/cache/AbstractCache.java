/*
 * MIT License
 *
 * Copyright (c) 2017 Felix Klauke, JackWhite20
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.electra.server.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
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
