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

import com.google.common.base.Charsets;
import io.electra.server.data.DataStorage;
import io.electra.server.data.DataStorageFactory;
import io.electra.server.index.IndexStorage;
import io.electra.server.index.IndexStorageFactory;
import io.electra.server.storage.StorageManager;

import java.nio.file.Path;
import java.util.Arrays;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DefaultDatabaseImpl implements Database {

    private final StorageManager storageManager;

    DefaultDatabaseImpl(Path dataFilePath, Path indexFilePath) {
        IndexStorage indexStorage = IndexStorageFactory.createIndexStorage(indexFilePath);
        DataStorage dataStorage = DataStorageFactory.createDataStorage(dataFilePath);

        storageManager = new StorageManagerImpl(indexStorage, dataStorage);
    }


    @Override
    public void save(String key, byte[] bytes) {
        int keyHash = Arrays.hashCode(key.getBytes(Charsets.UTF_8));
        storageManager.save(keyHash, bytes);
    }

    @Override
    public void save(String key, String value) {
        save(key, value.getBytes(Charsets.UTF_8));
    }

    @Override
    public byte[] get(String key) {
        int keyHash = Arrays.hashCode(key.getBytes(Charsets.UTF_8));

        return storageManager.get(keyHash);
    }

    @Override
    public void remove(String key) {
        int keyHash = Arrays.hashCode(key.getBytes(Charsets.UTF_8));

        storageManager.remove(keyHash);
    }

    @Override
    public void close() {
        storageManager.close();
    }
}
