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

/**
 * Storage of central important constants that have to be available always and everywhere.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DatabaseConstants {

    /**
     * The size of the blocks our data is organized in. Remember that the pure content of the size will have a length of
     * DATA_BLOCK_SIZE - {@link #CONTENT_LENGTH_OFFSET} - {@link #NEXT_POSITION_OFFSET}.
     */
    public static final int DATA_BLOCK_SIZE = 128;

    /**
     * Represents how many bytes are needed to store an integer.
     */
    public static final int INTEGER_BYTE_SIZE = 4;

    /**
     * The amount of bytes needed to store the content length.
     */
    public static final int CONTENT_LENGTH_OFFSET = 4;

    /**
     * The amount of bytes needed to store the next position pointer.
     */
    public static final int NEXT_POSITION_OFFSET = 4;

    /**
     * The size of one index block.
     */
    public static final int INDEX_BLOCK_SIZE = 9;

    /**
     * The default path for a basic data file.
     */
    public static final String DEFAULT_DATA_FILE_PATH = "data.lctr";

    /**
     * The default path for a basic index file.
     */
    public static final String DEFAULT_INDEX_FILE_PATH = "index.lctr";

    public DatabaseConstants() {
        throw new AssertionError();
    }

    /**
     * The prefix for all threads working on actual data.
     */
    public static final String DATA_WORKER_PREFIX = "Electra Data Worker #";

    /**
     * The prefix for all threads working on indices.
     */
    public static final String INDEX_WORKER_PREFIX = "Electra Index Worker #";
}
