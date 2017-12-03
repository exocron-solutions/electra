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

import com.google.common.collect.Sets;
import io.electra.core.data.DataStorage;
import io.electra.core.index.IndexStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.Arrays;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class StorageManagerImplTest {

    private IndexStorage indexStorage;
    private DataStorage dataStorage;
    private StorageManagerImpl storageManager;

    @Before
    public void setUp() throws Exception {
        indexStorage = Mockito.mock(IndexStorage.class);
        dataStorage = Mockito.mock(DataStorage.class);

        Mockito.when(indexStorage.getFirstEmptyDataBlock()).thenReturn(0);
        Mockito.when(dataStorage.readNextBlockChain(0)).thenReturn(Sets.newTreeSet(Arrays.asList(1, 2, 3)));

        storageManager = (StorageManagerImpl) StorageManagerFactory.createStorageManager(indexStorage, dataStorage);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void save() throws Exception {
        int keyHash = 28654765;

        storageManager.save(keyHash, "Felix".getBytes());

        Mockito.verify(indexStorage).getIndex(keyHash);
        Mockito.verify(indexStorage).createIndex(Matchers.eq(keyHash), Mockito.anyBoolean(), Mockito.anyInt());
    }
}