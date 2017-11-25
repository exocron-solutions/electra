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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DatabaseBootstrap {

    private static final Path indexFilePath = Paths.get("index.lctr");
    private static final Path dataFilePath = Paths.get("data.lctr");

    public static void main(String[] args) {
        Database database = new DatabaseImpl(dataFilePath, indexFilePath);

        int n = 100000;

        long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            database.save("Key" + i, "Value" + i);
        }
        System.out.println("Saving " + n + " entries took " + (System.currentTimeMillis() - start) + "ms. ");

        start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            database.get("Key" + i);
        }
        System.out.println("Reading " + n + " entries took " + (System.currentTimeMillis() - start) + "ms. ");

        start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            database.remove("Key" + i);
        }
        System.out.println("Deleting " + n + " entries took " + (System.currentTimeMillis() - start) + "ms. ");

        System.out.println("Total allocated: " + ByteBufferAllocator.getCapacity() + " Average: " + ByteBufferAllocator.getCapacity() / ByteBufferAllocator.getTimes());

        try {
            Files.delete(dataFilePath);
            Files.delete(indexFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        database.close();
    }
}
