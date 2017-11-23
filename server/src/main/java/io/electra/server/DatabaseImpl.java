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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.electra.server.data.DataStorage;
import io.electra.server.data.DataStorageFactory;
import io.electra.server.index.IndexStorage;
import io.electra.server.index.IndexStorageFactory;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DatabaseImpl implements Database {

    private final Cache<String, byte[]> cache;
    private final StorageManager storageManager;

    DatabaseImpl(Path dataFilePath, Path indexFilePath) {
        IndexStorage indexStorage = IndexStorageFactory.createIndexStorage(indexFilePath);
        DataStorage dataStorage = DataStorageFactory.createDataStorage(dataFilePath);

        storageManager = new StorageManager(indexStorage, dataStorage);

        cache = CacheBuilder.newBuilder()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .recordStats()
                .build();
    }


    @Override
    public void save(String key, byte[] bytes) {
        int keyHash = Arrays.hashCode(bytes);
        storageManager.save(keyHash, bytes);

        cache.put(key, bytes);
    }

    @Override
    public void save(String key, String value) {
        save(key, value.getBytes());
    }

    @Override
    public byte[] get(String key) {
        byte[] result = cache.getIfPresent(key);

        if (result != null) {
            return result;
        }

        int keyHash = Arrays.hashCode(key.getBytes());
        return storageManager.get(keyHash);
    }

    @Override
    public void remove(String key) {
        int keyHash = Arrays.hashCode(key.getBytes());
        storageManager.remove(keyHash);

        cache.invalidate(key);
    }

    @Override
    public void close() {
        storageManager.close();

        cache.cleanUp();
    }
}
