package io.electra.core.filesystem;

import io.electra.core.exception.FileSystemAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class DuplexAsynchronousFileChannelFileSystemAccessorTest {

    private static final String TEST_FILE = "test.acc";
    private static final int TEST_CONTENT = 15;
    private FileSystemAccessor fileSystemAccessor;

    @BeforeEach
    void setUp() throws FileSystemAccessException {
        fileSystemAccessor = new DuplexAsynchronousFileChannelFileSystemAccessor(Paths.get(TEST_FILE));

        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(TEST_CONTENT);
        byteBuffer.flip();
        fileSystemAccessor.write(4, byteBuffer);
    }

    @Test
    void testClose() throws IOException {
        fileSystemAccessor.close();
    }

    @Test
    void testCreationWithInvalidFileAccess() {
        Executable runnable = () -> new DuplexAsynchronousFileChannelFileSystemAccessor(Paths.get("/"));
        assertThrows(FileSystemAccessException.class, runnable);
    }

    @AfterEach
    void tearDown() throws IOException {
        Path path = Paths.get(TEST_FILE);
        Files.delete(path);
    }

    @Test
    void testWrite() throws ExecutionException, InterruptedException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(5);

        byteBuffer.flip();

        Future<Integer> write = fileSystemAccessor.write(0, byteBuffer);

        byteBuffer.rewind();

        assertEquals(4, write.get().intValue());
    }

    @Test
    void testRead() throws ExecutionException, InterruptedException {
        Future<ByteBuffer> read = fileSystemAccessor.read(4, 4);
        ByteBuffer result = read.get();
        assertEquals(TEST_CONTENT, result.getInt());
    }

    @Test
    void testClear() throws FileSystemAccessException {
        fileSystemAccessor.clear();
    }

    @Test
    void testClearWithClosedChannelException() {
        Executable executable = () -> {
            fileSystemAccessor.close();
            fileSystemAccessor.clear();
        };

        assertThrows(FileSystemAccessException.class, executable);
    }
}