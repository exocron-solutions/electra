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

package io.electra.server.data;

import com.google.common.collect.Sets;
import io.electra.server.DatabaseConstants;
import io.electra.server.factory.ElectraThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Executors;

/**
 * The factory to create data storage instances.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DataStorageFactory {

    /**
     * The logger to log the data storage initialization process.
     */
    private static Logger logger = LoggerFactory.getLogger(DataStorageFactory.class);

    /**
     * Create a new data storage instance by its underlying file.
     *
     * @param dataFilePath The underlying data file.
     * @return The data storage instance.
     */
    public static DataStorage createDataStorage(Path dataFilePath) {
        DataStorage dataStorage = null;

        try {
            if (!Files.exists(dataFilePath)) {
                logger.info("Data file could not be found. Creating new one...");
                Files.createFile(dataFilePath);
                logger.info("Created new data file.");
            }

            logger.info("Opening channel to data file and creating data storage.");
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(dataFilePath, Sets.newHashSet(StandardOpenOption.READ, StandardOpenOption.WRITE), Executors.newCachedThreadPool(new ElectraThreadFactory(DatabaseConstants.DATA_WORKER_PREFIX)));
            dataStorage = new DataStorageImpl(channel);
            logger.info("Data storage created.");
        } catch (IOException e) {
            logger.error("Error while creating data storage.", e);
        }

        return dataStorage;
    }
}
