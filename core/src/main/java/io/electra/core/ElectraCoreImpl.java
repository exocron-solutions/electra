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

import io.electra.core.config.ElectraCoreConfig;
import net.openhft.koloboke.collect.map.ObjObjMap;
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class ElectraCoreImpl implements ElectraCore {

    private final ObjObjMap<String, Database> currentLoadedDatabases;
    private final ElectraCoreConfig config;

    ElectraCoreImpl(ElectraCoreConfig config) {
        this.config = config;
        currentLoadedDatabases = HashObjObjMaps.newMutableMap();
    }

    @Override
    public Database getDatabase(String storageId) {
        if (!currentLoadedDatabases.containsKey(storageId)) {
            Path indexPath = Paths.get(config.getDbPath() + storageId + "/index.lctr");
            Path dataPath = Paths.get(config.getDbPath() + storageId + "/data.lctr");

            Database database = DatabaseFactory.createDatabase(dataPath, indexPath);
            currentLoadedDatabases.put(storageId, database);
            return database;
        }

        return currentLoadedDatabases.get(storageId);
    }

    @Override
    public void closeDatabase(Database database) {
        database.close();
        currentLoadedDatabases.values().remove(database);
    }

    @Override
    public void closeDatabase(String storageId) {
        Database database = currentLoadedDatabases.get(storageId);
        closeDatabase(database);
    }

    @Override
    public void deleteDatabase(String storageId) {
        Database database = currentLoadedDatabases.get(storageId);
        closeDatabase(database);

        Path indexPath = Paths.get(config.getDbPath() + storageId + "/index.lctr");
        Path dataPath = Paths.get(config.getDbPath() + storageId + "/data.lctr");

        try {
            Files.deleteIfExists(dataPath);
            Files.deleteIfExists(indexPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
