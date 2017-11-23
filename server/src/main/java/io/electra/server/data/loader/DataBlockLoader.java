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

package io.electra.server.data.loader;

import com.google.common.cache.CacheLoader;
import io.electra.server.DatabaseConstants;
import io.electra.server.data.DataBlock;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DataBlockLoader extends CacheLoader<Integer, DataBlock> {

    private final SeekableByteChannel channel;

    public DataBlockLoader(SeekableByteChannel channel) {
        this.channel = channel;
    }

    @Override
    public DataBlock load(Integer index) throws Exception {
        int position = getPositionByIndex(index);

        if (position < 0) {
            throw new IllegalArgumentException("Illegal reading position: " + position);
        }

        try {
            channel.position(position);

            ByteBuffer nextPositionByteBuffer = ByteBuffer.allocate(4);
            channel.read(nextPositionByteBuffer);

            nextPositionByteBuffer.flip();

            int nextPosition = nextPositionByteBuffer.getInt();

            ByteBuffer contentLengthByteBuffer = ByteBuffer.allocate(4);
            channel.read(contentLengthByteBuffer);
            contentLengthByteBuffer.flip();

            if (!contentLengthByteBuffer.hasRemaining()) {
                return null;
            }

            int contentLength = contentLengthByteBuffer.getInt();

            ByteBuffer contentByteBuffer = ByteBuffer.allocate(contentLength);
            channel.read(contentByteBuffer);
            contentByteBuffer.flip();

            return new DataBlock(position / DatabaseConstants.DATA_BLOCK_SIZE, contentByteBuffer.array(), nextPosition);
        } catch (IOException | BufferUnderflowException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getPositionByIndex(int index) {
        return index * (DatabaseConstants.DATA_BLOCK_SIZE);
    }
}
