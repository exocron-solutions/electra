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

package io.electra.core.index;

import com.google.common.collect.Sets;
import io.electra.core.DatabaseConstants;
import io.electra.core.factory.ElectraThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Executors;

/**
 * The central entry point to create instances of the {@link IndexStorage}.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class IndexStorageFactory {

    /**
     * The logger to log index storage instantiation.
     */
    private static Logger logger = LoggerFactory.getLogger(IndexStorageFactory.class);

    /**
     * Create a new {@link IndexStorage} by its underlying file.
     *
     * @param indexFilePath The path of the underlying file.
     * @return The index storage instance.
     */
    public static IndexStorage createIndexStorage(Path indexFilePath) {
        try {
            if (!Files.exists(indexFilePath)) {
                logger.info("Index file could not be found. Creating new one...");
                Files.createFile(indexFilePath);
                logger.info("Created a new data file.");
            }

            logger.info("Opening channel to index file and creating index storage.");
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(indexFilePath, Sets.newHashSet(StandardOpenOption.READ, StandardOpenOption.WRITE), Executors.newCachedThreadPool(new ElectraThreadFactory(DatabaseConstants.INDEX_WORKER_PREFIX)));
            IndexStorage indexStorage = new IndexStorageImpl(channel);
            logger.info("Index storage created.");
            return indexStorage;
        } catch (IOException e) {
            logger.error("An error occured while creating the index storage.", e);
        }

        return null;
    }
}
