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

import com.google.common.base.Charsets;
import com.google.common.base.Strings;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class DatabaseImpl implements IDatabase {

    private static final Path TEST_INDEX_PATH = Paths.get("index.nstr");
    private static final Path TEST_DATA_PATH = Paths.get("data.nstr");
    private final IndexStorage indexStorage;
    private final DataStorage dataStorage;

    public DatabaseImpl(Path dataFilePath, Path indexFilePath) {
        indexStorage = IndexStorage.createIndexStorage(indexFilePath);
        dataStorage = DataStorage.createDataStorage(dataFilePath);
    }

    public static void main(String[] args) {
        IDatabase database = new DatabaseImpl(TEST_DATA_PATH, TEST_INDEX_PATH);

        long time = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            database.save("key" + i, Strings.repeat("value" + i, i));
        }
        System.out.println("Saving took " + (System.currentTimeMillis() - time) + "ms.");

        System.out.println(new String(database.get("key50")));
    }

    @Override
    public void save(String key, byte[] bytes) {
        int keyHash = Arrays.hashCode(key.getBytes(Charsets.UTF_8));
        save(keyHash, bytes);
    }

    private void save(int keyHash, byte[] bytes) {
        int[] dataBlockIndices = dataStorage.allocateDataBlocks(bytes);

        Index index = new Index(keyHash, dataBlockIndices.length, dataBlockIndices);
        indexStorage.save(index);

        System.out.println("Got new index: " + index);

        dataStorage.save(keyHash, dataBlockIndices, bytes);
    }

    @Override
    public void save(String key, String value) {
        save(key, value.getBytes());
    }

    @Override
    public byte[] get(String key) {
        Index index = indexStorage.get(key);
        return dataStorage.get(index.getDataBlockIndices());
    }

    @Override
    public void remove(String key) {

    }
}
