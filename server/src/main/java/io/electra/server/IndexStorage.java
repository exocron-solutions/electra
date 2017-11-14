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

import io.electra.server.btree.BTree;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
class IndexStorage {

    private final BTree<Integer, Index> indexTree = new BTree<>();

    private IndexStorage(Path indexPath) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(Files.readAllBytes(indexPath));

        while (byteBuffer.hasRemaining()) {
            int keyHash = byteBuffer.getInt();
            int dataBlockCount = byteBuffer.getInt();

            int[] dataBlockIndices = new int[dataBlockCount];

            for (int i = 0; i < dataBlockCount; i++) {
                dataBlockIndices[i] = byteBuffer.getInt();
            }

            Index index = new Index(keyHash, dataBlockCount, dataBlockIndices);
            indexTree.insert(index.getKeyHash(), index);
        }
    }

    static IndexStorage createIndexStorage(Path indexPath) {
        if (!Files.exists(indexPath)) {
            try {
                Files.createFile(indexPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            return new IndexStorage(indexPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    Index get(int keyHash) {
        return indexTree.search(keyHash);
    }

    void save(Index index) {
        // TODO: 14.11.2017 Index btree needs to be written on disk when writing/deleting from it
        indexTree.insert(index.getKeyHash(), index);
    }

    void close() {
        // TODO: 14.11.2017 Close channel
    }
}
