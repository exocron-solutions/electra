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

package io.electra.core;

import com.google.common.base.Charsets;
import io.electra.core.cache.Cache;
import io.electra.core.cache.DataCache;
import io.electra.core.data.DataStorage;
import io.electra.core.data.DataStorageFactory;
import io.electra.core.index.IndexStorage;
import io.electra.core.index.IndexStorageFactory;
import io.electra.core.storage.StorageManager;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * The default implementation of the {@link Database}.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class DefaultDatabaseImpl implements Database {

    /**
     * The top level cache that contains all data.
     */
    private final Cache<Integer, byte[]> dataCache;

    /**
     * The storage manager to manager data.
     */
    private final StorageManager storageManager;

    /**
     * Create a new database instance by its underlying files.
     *
     * @param dataFilePath  The data file.
     * @param indexFilePath The index file.
     */
    DefaultDatabaseImpl(Path dataFilePath, Path indexFilePath) {
        IndexStorage indexStorage = IndexStorageFactory.createIndexStorage(indexFilePath);
        DataStorage dataStorage = DataStorageFactory.createDataStorage(dataFilePath);

        dataCache = new DataCache(1, TimeUnit.MINUTES, 10000);

        storageManager = StorageManagerFactory.createStorageManager(indexStorage, dataStorage);
    }

    public void save(int keyHash, byte[] bytes) {
        storageManager.save(keyHash, bytes);

        dataCache.put(keyHash, bytes);
    }

    @Override
    public void save(String key, byte[] bytes) {
        int keyHash = Arrays.hashCode(key.getBytes(Charsets.UTF_8));

        save(keyHash, bytes);
    }

    @Override
    public void save(String key, String value) {
        save(key, value.getBytes(Charsets.UTF_8));
    }

    @Override
    public void update(String key, byte[] value) {
        int keyHash = Arrays.hashCode(key.getBytes(Charsets.UTF_8));
        update(keyHash, value);
    }

    private void update(int keyHash, byte[] value) {
        if (dataCache.get(keyHash) != null) {
            dataCache.put(keyHash, value);
        }

        storageManager.update(keyHash, value);
    }

    public byte[] get(int keyHash) {
        byte[] bytes = dataCache.get(keyHash);

        return bytes != null ? bytes : storageManager.get(keyHash);
    }

    @Override
    public byte[] get(String key) {
        int keyHash = Arrays.hashCode(key.getBytes(Charsets.UTF_8));

        return get(keyHash);
    }

    public void remove(int keyHash) {
        storageManager.remove(keyHash);

        dataCache.invalidate(keyHash);
    }

    @Override
    public void remove(String key) {
        int keyHash = Arrays.hashCode(key.getBytes(Charsets.UTF_8));

        remove(keyHash);
    }

    @Override
    public void close() {
        storageManager.close();
        dataCache.clear();
    }
}
