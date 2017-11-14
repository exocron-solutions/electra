/*
 * MIT License
 *
 * Copyright (c) 2017 Felix Klauke
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

package de.felix_klauke.nostra.core;

import com.google.common.base.Charsets;
import de.felix_klauke.nostra.core.btree.BTree;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
class IndexStorage {

    private static final Charset DEFAULT_CHARSET = Charsets.UTF_8;

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

    Index get(String key) {
        return indexTree.search(Arrays.hashCode(key.getBytes(DEFAULT_CHARSET)));
    }

    void save(Index index) {
        indexTree.insert(index.getKeyHash(), index);
    }
}
