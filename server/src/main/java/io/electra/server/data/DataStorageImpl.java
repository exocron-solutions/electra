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

import io.electra.server.DatabaseConstants;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DataStorageImpl implements DataStorage {

    private final SeekableByteChannel channel;

    public DataStorageImpl(SeekableByteChannel channel) {
        this.channel = channel;
    }

    @Override
    public void save(int[] allocatedBlocks, byte[] bytes) {
        for (int i = 0; i < allocatedBlocks.length; i++) {
            int currentBlock = allocatedBlocks[i];

            int startPosition = i * DatabaseConstants.DATA_BLOCK_SIZE;
            int endPosition = (i + 1) * DatabaseConstants.DATA_BLOCK_SIZE - DatabaseConstants.NEXT_POSITION_OFFSET - DatabaseConstants.CONTENT_LENGTH_OFFSET;
            endPosition = endPosition >= bytes.length ? bytes.length : endPosition;

            byte[] currentBlockContent = Arrays.copyOfRange(bytes, startPosition, endPosition);

            int nextBlock = i == allocatedBlocks.length - 1 ? -1 : allocatedBlocks[i + 1];

            ByteBuffer byteBuffer = ByteBuffer.allocate(DatabaseConstants.DATA_BLOCK_SIZE);
            byteBuffer.putInt(currentBlockContent.length);
            byteBuffer.put(currentBlockContent);
            byteBuffer.putInt(nextBlock);

            byteBuffer.flip();

            try {
                channel.position(currentBlock * DatabaseConstants.DATA_BLOCK_SIZE);
                channel.write(byteBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public DataBlock readDataBlockAtPosition(int position) {
        try {
            channel.position(position);

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

            ByteBuffer nextPositionByteBuffer = ByteBuffer.allocate(4);
            channel.read(nextPositionByteBuffer);
            nextPositionByteBuffer.flip();

            int nextPosition = nextPositionByteBuffer.getInt();

            return new DataBlock(position / DatabaseConstants.DATA_BLOCK_SIZE, contentByteBuffer.array(), nextPosition);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public DataBlock readDataBlockAtIndex(int index) {
        return readDataBlockAtPosition(getPositionByIndex(index));
    }

    @Override
    public int getPositionByIndex(int index) {
        return index * (DatabaseConstants.DATA_BLOCK_SIZE);
    }

    @Override
    public void updateNextBlock(DataBlock dataBlock, DataBlock deletedBlock) {
        try {
            channel.position(dataBlock.getCurrentPosition() * DatabaseConstants.DATA_BLOCK_SIZE + dataBlock.getContent().length);

            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            byteBuffer.putInt(deletedBlock.getCurrentPosition());
            byteBuffer.flip();

            channel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
