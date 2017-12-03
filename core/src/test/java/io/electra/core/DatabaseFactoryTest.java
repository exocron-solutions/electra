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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DatabaseFactory.class, DefaultDatabaseImpl.class})
public class DatabaseFactoryTest {

    private Path dataFilePath;
    private Path indexFilePath;
    private DefaultDatabaseImpl database;

    @Before
    public void setUp() throws Exception {
        dataFilePath = Mockito.mock(Path.class);
        indexFilePath = Mockito.mock(Path.class);
        database = Mockito.mock(DefaultDatabaseImpl.class);

        PowerMockito.mock(DefaultDatabaseImpl.class);
        PowerMockito.whenNew(DefaultDatabaseImpl.class).withAnyArguments().thenReturn(database);
    }

    @Test(expected = AssertionError.class)
    public void testCreation() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        new DatabaseFactory();
    }

    @Test
    public void createDatabase() throws Exception {
        // Given

        // When

        Database database = DatabaseFactory.createDatabase(dataFilePath, indexFilePath);

        // Then
        Assert.assertNotNull(database);
        Assert.assertEquals(this.database, database);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDatabaseWithEqualFiles() throws Exception {
        // Given
        dataFilePath = indexFilePath;

        // When

        Database database = DatabaseFactory.createDatabase(dataFilePath, indexFilePath);

        // Then
        Assert.fail();
    }
}