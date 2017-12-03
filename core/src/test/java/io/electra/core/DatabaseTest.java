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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DatabaseTest {

    private Database database;

    @Before
    public void setUp() throws Exception {
        database = DatabaseFactory.createDatabase(Paths.get("data.test"), Paths.get("index.test"));
    }

    @Test
    public void save() throws Exception {
        String testKey = "iagphawgagfa";
        String testValue = "üioaofhnwa";

        database.save(testKey, testValue);

        assertNotNull(database.get(testKey));
        assertEquals(testValue, new String(database.get(testKey)));
    }

    @Test
    public void saveUpdate() throws Exception {
        String testKey = "iagphawgagfa";
        String testValue = "üioaofhnwa";
        String testValue2 = "pgaojeg+pawnf+iawge";

        database.save(testKey, testValue);

        assertNotNull(database.get(testKey));
        assertEquals(testValue, new String(database.get(testKey)));

        database.save(testKey, testValue2);

        assertNotNull(database.get(testKey));
        assertEquals(testValue2, new String(database.get(testKey)));
    }

    @Test
    public void remove() throws Exception {
        String testKey = "iagphawgagfa";
        String testValue = "üioaofhnwa";

        database.save(testKey, testValue);

        database.remove(testKey);

        assertTrue(database.get(testKey) == null);
    }

    @After
    public void tearDown() throws IOException {
        database.close();

        Files.delete(Paths.get("data.test"));
        Files.delete(Paths.get("index.test"));
    }
}