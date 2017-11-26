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

package io.electra.server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class GuavaCachedDatabaseImpl extends DefaultDatabaseImpl {

    private final LoadingCache<String, byte[]> cache;

    GuavaCachedDatabaseImpl(Path dataFilePath, Path indexFilePath) {
        super(dataFilePath, indexFilePath);

        this.cache = CacheBuilder.newBuilder()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .build(new CacheLoader<String, byte[]>() {
                    @Override
                    public byte[] load(String key) throws Exception {
                        return GuavaCachedDatabaseImpl.super.get(key);
                    }
                });
    }

    @Override
    public byte[] get(String key) {
        try {
            return cache.get(key);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void save(String key, byte[] bytes) {
        super.save(key, bytes);

        cache.put(key, bytes);
    }

    @Override
    public void remove(String key) {
        super.remove(key);

        cache.invalidate(key);
    }
}
