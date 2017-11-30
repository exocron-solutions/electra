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

/**
 * Contains the data read from disk.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DataBlock {

    /**
     * The current position of the block.
     */
    private final int currentPosition;

    /**
     * The content of the block.
     */
    private final byte[] content;

    /**
     * THe position of the next block in the block chain.
     */
    private int nextPosition;

    /**
     * Create a new data block instance by its underlying data.
     *
     * @param currentPosition The position of the data block.
     * @param content         The content.
     * @param nextPosition    The position of the next block.
     */
    public DataBlock(int currentPosition, byte[] content, int nextPosition) {
        this.currentPosition = currentPosition;
        this.content = content;
        this.nextPosition = nextPosition;
    }

    /**
     * Get the current position of the block.
     *
     * @return The current position.
     */
    public int getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Get the content of the block.
     *
     * @return The content.
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Get the position of the next block.
     *
     * @return The position of the next block.
     */
    public int getNextPosition() {
        return nextPosition;
    }

    /**
     * Set the position of the next block.
     *
     * @param nextPosition The position of the next block.
     */
    public void setNextPosition(int nextPosition) {
        this.nextPosition = nextPosition;
    }

    @Override
    public String toString() {
        return "DataBlock{" +
                ", nextPosition=" + nextPosition +
                '}';
    }
}
