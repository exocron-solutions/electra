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

package io.electra.server.loader;

import com.google.common.cache.CacheLoader;
import com.google.common.primitives.Bytes;
import io.electra.server.data.DataBlock;
import io.electra.server.data.DataStorage;
import io.electra.server.index.Index;
import io.electra.server.index.IndexStorage;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DatabaseValueLoader extends CacheLoader<Integer, byte[]> {

    private final IndexStorage indexStorage;
    private final DataStorage dataStorage;

    public DatabaseValueLoader(IndexStorage indexStorage, DataStorage dataStorage) {
        this.indexStorage = indexStorage;
        this.dataStorage = dataStorage;
    }

    @Override
    public byte[] load(Integer keyHash) throws Exception {
        Index index = indexStorage.getIndex(keyHash);
        DataBlock dataBlock = dataStorage.readDataBlockAtIndex(index.getDataFilePosition());

        byte[] result = dataBlock.getContent();

        while (dataBlock.getNextPosition() != -1) {
            dataBlock = dataStorage.readDataBlockAtIndex(dataBlock.getNextPosition());
            result = Bytes.concat(result, dataBlock.getContent());
        }

        return result;
    }
}
