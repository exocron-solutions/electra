package io.electra.core.engine;

import com.google.common.util.concurrent.Futures;
import io.electra.core.exception.EngineInitializationException;
import io.electra.core.model.Index;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class SimpleStorageEngineTest {

    private static final Path TEST_INDEX_FILE_PATH = Paths.get("test.index");
    private static final Path TEST_DATA_FILE_PATH = Paths.get("test.data");
    private static final int TEST_KEY_HASH = Integer.MAX_VALUE;
    private static final int TEST_KEY_HASH_PRESET = Integer.MIN_VALUE;
    private static final String TEST_CONTENT = "ugewoighewügh9w8gw+9ghw+gehwg+qwhe0g+qwe0+ghwe9qg´´0qwe0hgew9ghewqg0ewqgewuwghsaügoucr6d67guizfzrdtf687gizvhghctzfzgivutf79687gpzutcdif687gzft7d6r807ozuf767tg9z7d96rt7ßgf867d56rt79gf867rt79gfz86t798g7f860t79g87zf8607t9gzf8607t98hogupi79t8zguizf87t9ßg88f70tß98zg78f0tß98gu780ft98zhgu78t98guiz87ftg98zhugz78t9z8huoip798zh0ioug79ß8hüu";
    private SimpleStorageEngine storageEngine;

    @BeforeEach
    void setUp() throws EngineInitializationException {
        storageEngine = new SimpleStorageEngine(TEST_DATA_FILE_PATH, TEST_INDEX_FILE_PATH);
        Future<Index> future = storageEngine.save(TEST_KEY_HASH_PRESET, TEST_CONTENT.getBytes());

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testInitWithException() {
        Executable executable = () -> {
            storageEngine = new SimpleStorageEngine(Paths.get("/"), Paths.get("/"));
        };

        assertThrows(EngineInitializationException.class, executable);
    }

    @Test
    void testDoClose() throws IOException {
        storageEngine.close();
    }

    @Test
    void testSave() {
        String test = "ihafi";
        Futures.getUnchecked(storageEngine.save(TEST_KEY_HASH, test.getBytes()));

        Future<byte[]> contentFuture = storageEngine.get(TEST_KEY_HASH);
        byte[] content = Futures.getUnchecked(contentFuture);
        assertArrayEquals(test.getBytes(), content);
    }

    @Test
    void testGet() {
        Future<byte[]> future = storageEngine.get(TEST_KEY_HASH_PRESET);

        byte[] content = new byte[0];

        try {
            content = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertArrayEquals(TEST_CONTENT.getBytes(), content);
    }

    @Test
    void testSaveWithDuplicateKey() {
        Executable executable = () -> {
            Futures.getUnchecked(storageEngine.save(TEST_KEY_HASH_PRESET, TEST_CONTENT.getBytes()));
        };

        assertThrows(IllegalStateException.class, executable);
    }

    @Test
    void testSetupWithErrorInIndexReading() {
        Executable executable = () -> {
            storageEngine.close();
            storageEngine.readIndices();
        };

        assertThrows(EngineInitializationException.class, executable);
    }

    @Test
    void testSetupWithMultipleIndices() throws EngineInitializationException, IOException {
        String test = "ihafi";
        Futures.getUnchecked(storageEngine.save(TEST_KEY_HASH, test.getBytes()));

        Future<byte[]> contentFuture = storageEngine.get(TEST_KEY_HASH);
        byte[] content = Futures.getUnchecked(contentFuture);

        storageEngine.close();
        storageEngine = new SimpleStorageEngine(TEST_DATA_FILE_PATH, TEST_INDEX_FILE_PATH);
    }

    @Test
    void testGetOnNullIndex() {
        Future<byte[]> future = storageEngine.get(-1);

        assertNull(Futures.getUnchecked(future));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.delete(TEST_INDEX_FILE_PATH);
        Files.delete(TEST_DATA_FILE_PATH);
    }
}
