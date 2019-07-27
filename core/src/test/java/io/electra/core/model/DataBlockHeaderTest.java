package io.electra.core.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.electra.core.exception.MalformedHeaderException;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class DataBlockHeaderTest {

  private static final int TEST_NEXT_BLOCK_INDEX = 200;
  private static final int TEST_CONTENT_LENGTH = 300;
  private DataBlockHeader dataBlockHeader;

  @BeforeEach
  void setUp() {
    dataBlockHeader = new DataBlockHeader(TEST_NEXT_BLOCK_INDEX, TEST_CONTENT_LENGTH);
  }

  @Test
  void testFromByteBuffer() {
    DataBlockHeader dataBlockHeader = DataBlockHeader.fromByteBuffer(getByteBuffer());

    assertEquals(TEST_CONTENT_LENGTH, dataBlockHeader.getContentLength());
    assertEquals(TEST_NEXT_BLOCK_INDEX, dataBlockHeader.getNextDataBlockIndex());
  }

  @Test
  void testGetNextDataBlockIndex() {
    assertEquals(TEST_NEXT_BLOCK_INDEX, dataBlockHeader.getNextDataBlockIndex());
  }

  @Test
  void testGetContentLength() {
    assertEquals(TEST_CONTENT_LENGTH, dataBlockHeader.getContentLength());
  }

  @Test
  void testToByteBuffer() {
    assertArrayEquals(getByteBuffer().array(), dataBlockHeader.toByteBuffer().array());
  }

  @Test
  void testFromByteBufferWithInvalidByteBuffer() {
    ByteBuffer byteBuffer = ByteBuffer.allocate(3);

    Executable executable = () -> DataBlockHeader.fromByteBuffer(byteBuffer);

    assertThrows(MalformedHeaderException.class, executable);
  }

  private ByteBuffer getByteBuffer() {
    ByteBuffer byteBuffer = ByteBuffer.allocate(8);
    byteBuffer.putInt(TEST_NEXT_BLOCK_INDEX);
    byteBuffer.putInt(TEST_CONTENT_LENGTH);
    byteBuffer.flip();

    return byteBuffer;
  }
}
