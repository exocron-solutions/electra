package io.electra.core.index;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import com.google.common.util.concurrent.Futures;
import io.electra.core.exception.FileSystemAccessException;
import io.electra.core.exception.IndexScanException;
import io.electra.core.model.Index;
import io.electra.core.storage.AbstractFileSystemStorage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Future;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class IndexStorageImplTest {

  private static final int TEST_INDEX_BLOCK_INDEX = 1;
  private static final int TEST_INDEX_BLOCK_INDEX2 = 2;
  private static final int TEST_KEY_HASH = 90;
  private static final int TEST_DATA_BLOCK_INDEX = 200;
  private static final int TEST_KEY_HASH2 = 100;
  private static final int TEST_DATA_BLOCK_INDEX2 = 250;
  private static final String TEST_FILE = "test.acc";
  private IndexStorage indexStorage;

  @BeforeEach
  void setUp() throws FileSystemAccessException {
    indexStorage = new IndexStorageImpl(Paths.get(TEST_FILE));

    ByteBuffer byteBuffer = new Index(TEST_KEY_HASH, TEST_DATA_BLOCK_INDEX).toByteBuffer();
    Futures.getUnchecked(((AbstractFileSystemStorage) indexStorage).getFileSystemAccessor()
        .write(TEST_INDEX_BLOCK_INDEX * Index.INDEX_BLOCK_SIZE, byteBuffer));
    byteBuffer = new Index(TEST_KEY_HASH2, TEST_DATA_BLOCK_INDEX2).toByteBuffer();
    Futures.getUnchecked(((AbstractFileSystemStorage) indexStorage).getFileSystemAccessor()
        .write(TEST_INDEX_BLOCK_INDEX2 * Index.INDEX_BLOCK_SIZE, byteBuffer));
  }

  @AfterEach
  void tearDown() throws IOException {
    Files.delete(Paths.get(TEST_FILE));
  }

  @Test
  void testDoClear() {
    try {
      indexStorage.clear();
    } catch (FileSystemAccessException e) {
      fail(e);
    }
  }

  @Test
  void testDoClose() {
    try {
      indexStorage.close();
    } catch (IOException e) {
      fail(e);
    }
  }

  @Test
  void testReadIndex() {
    Future<Index> indexFuture = indexStorage.readIndex(TEST_INDEX_BLOCK_INDEX);
    Index index = Futures.getUnchecked(indexFuture);

    assertEquals(TEST_KEY_HASH, index.getKeyHash());
    assertEquals(TEST_DATA_BLOCK_INDEX, index.getBlockIndex());
  }

  @Test
  void testMultiInitialSetup() throws FileSystemAccessException {
    indexStorage = new IndexStorageImpl(Paths.get(TEST_FILE));
    Future<List<Index>> listFuture = null;

    try {
      listFuture = indexStorage.readIndices();
    } catch (IndexScanException e) {
      e.printStackTrace();
    }

    assertNotNull(listFuture);
    List<Index> unchecked = Futures.getUnchecked(listFuture);
    assertEquals(3, unchecked.size());
  }

  @Test
  void testWriteIndex() {
    Index index = new Index(TEST_KEY_HASH + 1, TEST_DATA_BLOCK_INDEX + 1);

    Future<Index> indexFuture = indexStorage.writeIndex(TEST_INDEX_BLOCK_INDEX, index);
    Futures.getUnchecked(indexFuture);

    index = Futures.getUnchecked(indexStorage.readIndex(TEST_INDEX_BLOCK_INDEX));

    assertEquals(TEST_KEY_HASH + 1, index.getKeyHash());
    assertEquals(TEST_DATA_BLOCK_INDEX + 1, index.getBlockIndex());
  }

  @Test
  void testMultiInitialSetupWithException() {
    Executable executable = () -> {
      indexStorage = new IndexStorageImpl(Paths.get(TEST_FILE));
      Future<List<Index>> listFuture = null;

      indexStorage.close();

      listFuture = indexStorage.readIndices();
    };

    assertThrows(IndexScanException.class, executable);
  }
}
