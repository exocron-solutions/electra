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
import io.electra.core.data.DataStorageFactory;
import io.electra.core.index.IndexStorage;
import io.electra.core.index.IndexStorageFactory;
import io.electra.core.storage.StorageManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.nio.file.Path;
import java.util.Arrays;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IndexStorageFactory.class, DataStorageFactory.class, StorageManagerFactory.class})
public class DefaultDatabaseImplTest {

    private Path dataFilePath;
    private Path indexFilePath;

    private IndexStorage indexStorage;
    private DataStorage dataStorage;
    private StorageManager storageManager;

    private Database database;

    @Before
    public void setUp() throws Exception {
        dataFilePath = Mockito.mock(Path.class);
        indexFilePath = Mockito.mock(Path.class);

        indexStorage = Mockito.mock(IndexStorage.class);
        dataStorage = Mockito.mock(DataStorage.class);
        storageManager = Mockito.mock(StorageManager.class);

        PowerMockito.mockStatic(IndexStorageFactory.class);
        PowerMockito.mockStatic(DataStorageFactory.class);
        PowerMockito.mockStatic(StorageManagerFactory.class);

        PowerMockito.when(IndexStorageFactory.createIndexStorage(indexFilePath)).thenReturn(indexStorage);
        PowerMockito.when(DataStorageFactory.createDataStorage(dataFilePath)).thenReturn(dataStorage);
        PowerMockito.when(StorageManagerFactory.createStorageManager(indexStorage, dataStorage)).thenReturn(storageManager);

        database = DatabaseFactory.createDatabase(dataFilePath, indexFilePath);
    }

    @Test
    public void save() throws Exception {
        // Given
        String testKey = "auügoäwaüiw+a0g8hwa0+tfghwa0+ghwa9gh8a";
        String testValue = "äipghwa#gho+wag9poawg";

        // When

        database.save(testKey, testValue);

        // Then
        Mockito.verify(storageManager).save(Arrays.hashCode(testKey.getBytes()), testValue.getBytes());
    }

    @Test
    public void get() throws Exception {
        // Given
        String testKey = "auügoäwaüiw+a0g8hwa0+tfghwa0+ghwa9gh8a";
        String testValue = "äipghwa#gho+wag9poawg";

        // When
        Mockito.when(storageManager.get(Arrays.hashCode(testKey.getBytes()))).thenReturn(testValue.getBytes());

        byte[] bytes = database.get(testKey);

        // Then
        Mockito.verify(storageManager).get(Arrays.hashCode(testKey.getBytes()));
        Assert.assertArrayEquals(testValue.getBytes(), bytes);
    }

    @Test
    public void remove() throws Exception {
        // Given
        String testKey = "auügoäwaüiw+a0g8hwa0+tfghwa0+ghwa9gh8a";

        // When

        database.remove(testKey);

        // Then
        Mockito.verify(storageManager).remove(Arrays.hashCode(testKey.getBytes()));
    }

    @Test
    public void close() throws Exception {
        // Given

        // When

        database.close();

        // Then
        Mockito.verify(storageManager).close();
    }
}