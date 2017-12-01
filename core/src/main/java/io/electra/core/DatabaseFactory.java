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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * The central entry point to create database instances.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DatabaseFactory {

    public DatabaseFactory() {
        throw new AssertionError();
    }

    /**
     * The logger to log database instance creation.
     */
    private static Logger logger = LoggerFactory.getLogger(DatabaseFactory.class);

    /**
     * Create a new database based on its underlying files.
     *
     * @param dataFilePath  The data file path.
     * @param indexFilePath The index file path.
     * @return The database instance.
     */
    public static Database createDatabase(Path dataFilePath, Path indexFilePath) {
        if (dataFilePath.equals(indexFilePath)) {
            throw new IllegalArgumentException("Someone tried to use the same file for indices and data.");
        }

        logger.info("Creating a new database based on {} and {}.", dataFilePath, indexFilePath);
        Database database = new DefaultDatabaseImpl(dataFilePath, indexFilePath);
        logger.info("Database creation successful.");
        return database;
    }
}
