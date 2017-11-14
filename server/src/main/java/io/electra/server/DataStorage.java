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

import com.google.common.collect.Queues;
import com.google.common.primitives.Bytes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Queue;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
class DataStorage {

    private static final int KEY_HASH_OFFSET = 4;
    private static final int KEY_HASH_DATA_LENGTH_OFFSET = 8;
    private static final int BLOCK_SIZE = 128;

    private final SeekableByteChannel channel;

    private final Queue<Integer> freeBlocks = Queues.newConcurrentLinkedQueue();
    private int freeBlock = 0;

    private DataStorage(SeekableByteChannel channel) {
        this.channel = channel;
    }

    static DataStorage createDataStorage(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
            return new DataStorage(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    void save(int keyHash, int[] dataBlockIndices, byte[] bytes) {
        try {
            for (int i = 0; i < dataBlockIndices.length; i++) {
                channel.position(dataBlockIndices[i] * BLOCK_SIZE);

                ByteBuffer byteBuffer = ByteBuffer.allocate(BLOCK_SIZE + KEY_HASH_DATA_LENGTH_OFFSET);
                byteBuffer.putInt(keyHash);
                int startPosition = i * (BLOCK_SIZE);
                int endPosition = (i + 1) * (BLOCK_SIZE);
                endPosition = endPosition < bytes.length ? endPosition : bytes.length;

                byte[] resultingData = Arrays.copyOfRange(bytes, startPosition, endPosition);

                byteBuffer.putInt(resultingData.length);
                byteBuffer.put(resultingData);
                byteBuffer.flip();

                channel.write(byteBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    byte[] get(int[] dataBlockIndices) {
        byte[] bytes = new byte[0];

        for (int currentDataBlockIndex : dataBlockIndices) {
            try {
                // The KEY_HASH_OFFSET is to skip the key hash which is also saved there
                // key hash
                // data length
                // data[]
                channel.position(currentDataBlockIndex * BLOCK_SIZE + KEY_HASH_OFFSET);

                ByteBuffer dataLengthBuffer = ByteBuffer.allocate(4);
                channel.read(dataLengthBuffer);
                dataLengthBuffer.flip();

                int valueLength = dataLengthBuffer.getInt();

                ByteBuffer valueBuffer = ByteBuffer.allocate(valueLength);
                channel.read(valueBuffer);
                valueBuffer.flip();

                bytes = Bytes.concat(bytes, valueBuffer.array());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bytes;
    }

    int[] allocateDataBlocks(byte[] bytes) {
        int neededBlocks = (int) Math.ceil(bytes.length / (double) (BLOCK_SIZE));

        int[] blocks = new int[neededBlocks];

        for (int i = 0; i < neededBlocks; i++) {
            blocks[i] = freeBlocks.isEmpty() ? freeBlock++ : freeBlocks.poll();
        }

        return blocks;
    }

    void close() {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
