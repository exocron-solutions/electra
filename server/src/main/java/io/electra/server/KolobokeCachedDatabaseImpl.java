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

import net.openhft.koloboke.collect.map.ObjObjMap;
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;

import java.nio.file.Path;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class KolobokeCachedDatabaseImpl extends DefaultDatabaseImpl {

    private final ObjObjMap<String, byte[]> cache;

    KolobokeCachedDatabaseImpl(Path dataFilePath, Path indexFilePath) {
        super(dataFilePath, indexFilePath);

        this.cache = HashObjObjMaps.newMutableMap();
    }

    @Override
    public byte[] get(String key) {
        byte[] value = cache.get(key);
        return value == null ? super.get(key) : value;
    }

    @Override
    public void save(String key, byte[] bytes) {
        super.save(key, bytes);
        cache.put(key, bytes);
    }

    @Override
    public void remove(String key) {
        super.remove(key);
        cache.remove(key);
    }
}
