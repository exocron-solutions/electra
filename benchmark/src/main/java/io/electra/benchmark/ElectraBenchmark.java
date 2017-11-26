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

package io.electra.benchmark;

import de.jackwhite20.orion.Orion;
import de.jackwhite20.orion.annotations.*;
import io.electra.server.Database;
import io.electra.server.DatabaseConstants;
import io.electra.server.DatabaseFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
@Benchmark(warmUpIterations = 0, iterations = 100000)
public class ElectraBenchmark {

    private static final Path indexFilePath = Paths.get(DatabaseConstants.DEFAULT_INDEX_FILE_PATH);
    private static final Path dataFilePath = Paths.get(DatabaseConstants.DEFAULT_DATA_FILE_PATH);

    private Database database;
    private int currentWrite, currentRead, currentDelete;

    public static void main(String[] args) {
        new Orion(new ElectraBenchmark()).run();
    }

    @Prepare
    public void setup() {
        database = DatabaseFactory.createGuavaValueCachedDatabase(dataFilePath, indexFilePath);
    }

    @MeasureTime
    @Order(0)
    public void testWrite() {
        database.save("Felix" + currentWrite, "Klauke" + currentWrite++);
    }

    @MeasureTime
    @Order(1)
    public void testRead() {
        byte[] bytes = database.get("Felix" + currentRead++);
        if (bytes == null) {
            System.out.println("FAIL");
        }
    }

    @MeasureTime
    @Order(2)
    public void testDelete() {
        database.remove("Felix" + currentDelete++);
    }

    @Cleanup
    public void cleanup() {
        try {
            Files.delete(dataFilePath);
            Files.delete(indexFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
