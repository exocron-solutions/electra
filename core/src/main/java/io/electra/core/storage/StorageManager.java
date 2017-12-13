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

package io.electra.core.storage;

import io.electra.core.Database;
import io.electra.core.StorageManagerImpl;
import io.electra.core.data.DataStorage;
import io.electra.core.index.IndexStorage;

/**
 * The storage manager meant to connect all pieces of the database. Usually the instance of the {@link Database}
 * should use an instances of the {@link StorageManager} to get access to the data. This is needed to prevent direct
 * access to the {@link IndexStorage} and {@link DataStorage}. The {@link StorageManager} will use the hashes of
 * data keys to access data. The {@link Database} should provide its keys as the hash code of the array of bytes
 * of its key.
 *
 * The default implementation of the {@link StorageManager} can be found at {@link StorageManagerImpl}.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public interface StorageManager {

    /**
     * Save the given bytes under the given key hash.
     *
     * @param keyHash The key hash.
     * @param bytes   The data.
     */
    void save(int keyHash, byte[] bytes);

    void update(int keyHash, byte[] bytes);

    /**
     * Close the storages and clean all resources up.
     */
    void close();

    /**
     * Remove the data of the given key hash.
     *
     * @param keyHash The key hash.
     */
    void remove(int keyHash);

    /**
     * Get the data behind the given key hash.
     *
     * @param keyHash The key hash.
     *
     * @return The data.
     */
    byte[] get(int keyHash);

    /**
     * Initialize the free block scanning.
     */
    void initializeFreeBlocks();
}
