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
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
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
    private final Cache<Integer, DataRecord> dataCache;

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


    @Override
    public void save(String key, byte[] bytes) {
        save(hashKey(key), bytes);
    }

    @Override
    public void save(String key, JSONObject jsonObject) {
        save(hashKey(key), jsonObject);
    }

    @Override
    public void save(String key, String value) {
        save(hashKey(key), value);
    }

    @Override
    public void update(String key, byte[] value) {
        update(hashKey(key), value);
    }

    @Override
    public void update(String key, JSONObject jsonObject) {
        update(hashKey(key), jsonObject);
    }

    @Override
    public byte[] get(String key) {
        return get(hashKey(key));
    }

    @Override
    public JSONObject getJson(String key) {
        return getJson(hashKey(key));
    }

    @Override
    public void remove(String key) {
        remove(hashKey(key));
    }

    @Override
    public void save(int keyHash, byte[] bytes) {
        DataRecord<byte[]> dataRecord = new RawDataRecord(bytes);
        saveDataRecord(keyHash, dataRecord);
    }

    private void saveDataRecord(int keyHash, DataRecord dataRecord) {
        dataCache.put(keyHash, dataRecord);
        storageManager.save(keyHash, dataRecord.getRawContent());
    }

    @Override
    public void save(int keyHash, JSONObject jsonObject) {
        DataRecord<JSONObject> dataRecord = new JsonDataRecord(jsonObject);
        saveDataRecord(keyHash, dataRecord);
    }

    @Override
    public void save(int keyHash, String value) {
        save(keyHash, value.getBytes(Charsets.UTF_8));
    }

    @Override
    public void update(int keyHash, byte[] value) {
        DataRecord dataRecord = dataCache.get(keyHash);

        if (dataRecord != null) {
            dataRecord.setRawData(value);
        }

        storageManager.update(keyHash, value);
    }

    @Override
    public void update(int keyHash, JSONObject jsonObject) {
        DataRecord dataRecord = dataCache.get(keyHash);

        if (dataRecord != null && dataRecord instanceof JsonDataRecord) {
            JSONObject recordData = (JSONObject) dataRecord.getData();

            Map<String, Object> objectMap = recordData.toMap();
            objectMap.putAll(jsonObject.toMap());

            ((JsonDataRecord) dataRecord).setData(new JSONObject(objectMap));
            storageManager.update(keyHash, dataRecord.getRawContent());
        }
    }

    @Override
    public byte[] get(int keyHash) {
        DataRecord dataRecord = dataCache.get(keyHash);

        if (dataRecord != null) {
            return dataRecord.getRawContent();
        }

        byte[] bytes = storageManager.get(keyHash);

        if (bytes == null) {
            return null;
        }

        dataRecord = new RawDataRecord(bytes);
        dataCache.put(keyHash, dataRecord);

        return dataRecord.getRawContent();
    }

    @Override
    public JSONObject getJson(int keyHash) {
        DataRecord dataRecord = dataCache.get(keyHash);

        if (dataRecord != null && dataRecord instanceof JsonDataRecord) {
            return ((JsonDataRecord) dataRecord).getData();
        }

        byte[] bytes = storageManager.get(keyHash);

        if (bytes == null) {
            return null;
        }

        try {
            dataRecord = new JsonDataRecord(new JSONObject(new String(bytes)));
        } catch (JSONException e) {
            return null;
        }

        dataCache.put(keyHash, dataRecord);

        return ((JsonDataRecord) dataRecord).getData();
    }

    @Override
    public void remove(int keyHash) {
        dataCache.invalidate(keyHash);
        storageManager.remove(keyHash);
    }

    @Override
    public void close() {
        storageManager.close();
        dataCache.clear();
    }

    private int hashKey(String key) {
        return Arrays.hashCode(key.getBytes(Charsets.UTF_8));
    }
}
