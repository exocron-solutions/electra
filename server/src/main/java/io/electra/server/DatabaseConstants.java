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

/**
 * Storage of central important constants that have to be available always and everywhere.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DatabaseConstants {

    /**
     * The size of the blocks our data is organized in. Remember that the pure content of the size will have a length of
     * DATA_BLOCK_SIZ - {@link #CONTENT_LENGTH_OFFSET} - {@link #NEXT_POSITION_OFFSET}.
     */
    public static int DATA_BLOCK_SIZE = 128;

    /**
     * The amount of bytes needed to store the content length.
     */
    public static int CONTENT_LENGTH_OFFSET = 4;

    /**
     * The amount of bytes needed to store the next position pointer.
     */
    public static int NEXT_POSITION_OFFSET = 4;

    /**
     * The size of one index block.
     */
    public static int INDEX_BLOCK_SIZE = 9;
}
