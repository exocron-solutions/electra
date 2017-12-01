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

import io.electra.core.data.DataStorage;
import io.electra.core.index.IndexStorage;
import io.electra.core.storage.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The central entry point to create new instances of the {@link StorageManager}.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class StorageManagerFactory {

    /**
     * The logger that will log the storage manager initialization process.
     */
    private static Logger logger = LoggerFactory.getLogger(StorageManagerFactory.class);

    /**
     * Prohibit instantiation.
     */
    public StorageManagerFactory() {
        throw new AssertionError();
    }

    /**
     * Create a new instance of the {@link StorageManager} by its underlying storages.
     *
     * @param indexStorage The index storage.
     * @param dataStorage  The data storage.
     * @return The storage manager instance.
     */
    public static StorageManager createStorageManager(IndexStorage indexStorage, DataStorage dataStorage) {
        logger.info("Creating a new storage manager...");
        StorageManager storageManager = new StorageManagerImpl(indexStorage, dataStorage);

        logger.info("Created a new storage manager. Initializing free blocks.");

        storageManager.initializeFreeBlocks();
        logger.info("Initialized free blocks.");

        return storageManager;
    }
}
