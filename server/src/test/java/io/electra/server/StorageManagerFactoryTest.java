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

import io.electra.server.data.DataStorage;
import io.electra.server.index.IndexStorage;
import io.electra.server.storage.StorageManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({StorageManagerFactory.class, StorageManagerImpl.class})
public class StorageManagerFactoryTest {

    private IndexStorage indexStorage;
    private DataStorage dataStorage;
    private StorageManagerImpl storageManager;

    @Before
    public void setUp() throws Exception {
        indexStorage = Mockito.mock(IndexStorage.class);
        dataStorage = Mockito.mock(DataStorage.class);

        storageManager = Mockito.mock(StorageManagerImpl.class);

        PowerMockito.mock(StorageManagerImpl.class);
        PowerMockito.whenNew(StorageManagerImpl.class).withAnyArguments().thenReturn(storageManager);
    }

    @Test(expected = AssertionError.class)
    public void testCreation() {
        new StorageManagerFactory();
    }

    @Test
    public void createStorageManager() throws Exception {
        StorageManager storageManager = StorageManagerFactory.createStorageManager(indexStorage, dataStorage);

        Assert.assertNotNull(storageManager);
        Assert.assertEquals(storageManager, storageManager);
    }
}