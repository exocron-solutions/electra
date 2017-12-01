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

import io.electra.core.Database;
import io.electra.core.DatabaseConstants;
import io.electra.core.DatabaseFactory;
import io.electra.core.alloc.ByteBufferAllocator;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class ElectraBenchmarkRead {

    private static final Path indexFilePath = Paths.get(DatabaseConstants.DEFAULT_INDEX_FILE_PATH);
    private static final Path dataFilePath = Paths.get(DatabaseConstants.DEFAULT_DATA_FILE_PATH);

    public static void main(String[] args) {
        Database database = DatabaseFactory.createDatabase(dataFilePath, indexFilePath);

        int n = 100000;

        long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            System.out.println(new String(database.get("Key" + i)));
        }

        System.out.println("Reading " + n + " entries took " + (System.currentTimeMillis() - start) + "ms. ");
        System.out.println("Total allocated: " + ByteBufferAllocator.getCapacity() + " Average: " + ByteBufferAllocator.getCapacity() / ByteBufferAllocator.getTimes());
    }

}
