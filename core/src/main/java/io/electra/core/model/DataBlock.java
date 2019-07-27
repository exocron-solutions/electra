package io.electra.core.model;

import io.electra.core.exception.MalformedDataException;
import java.nio.ByteBuffer;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class DataBlock {

  /**
   * The size of one data block.
   */
  public static final int DATA_BLOCK_SIZE = 128;

  /**
   * The size of one data block.
   */
  public static final int DATA_BLOCK_CONTENT_SECTION_SIZE = 120;

  /**
   * The header of the data block.
   */
  private final DataBlockHeader dataBlockHeader;

  /**
   * The actual content of the data block.
   */
  private byte[] content;

  /**
   * Create a new data block by its header values.
   *
   * @param nextDataBlockIndex The index of the next data block or -1.
   * @param contentLength The length of the content of this block.
   */
  DataBlock(int nextDataBlockIndex, int contentLength) {
    this(new DataBlockHeader(nextDataBlockIndex, contentLength));
  }

  /**
   * Create a new data block by its header.
   *
   * @param dataBlockHeader The header of this data block.
   */
  private DataBlock(DataBlockHeader dataBlockHeader) {
    this.dataBlockHeader = dataBlockHeader;
  }

  /**
   * Create a new data block by its header.
   *
   * @param dataBlockHeader The header.
   * @return The data block instance.
   */
  public static DataBlock fromDataBlockHeader(DataBlockHeader dataBlockHeader) {
    return new DataBlock(dataBlockHeader);
  }

  /**
   * Create a new data block by its header and its content buffer.
   *
   * @param dataBlockHeader The header.
   * @param contentBuffer The buffer of the content.
   * @return The data block instance.
   */
  public static DataBlock fromDataBlockHeaderAndContentBuffer(DataBlockHeader dataBlockHeader,
      ByteBuffer contentBuffer) {
    if (dataBlockHeader.getContentLength() != contentBuffer.remaining()) {
      throw new MalformedDataException(
          "Block header content length field differs from actual content buffer");
    }

    DataBlock dataBlock = DataBlock.fromDataBlockHeader(dataBlockHeader);
    byte[] bytes = new byte[dataBlockHeader.getContentLength()];
    contentBuffer.get(bytes);
    dataBlock.setContent(bytes);
    return dataBlock;
  }

  /**
   * Get the length of the content of this block.
   *
   * @return The length of the content.
   */
  public int getContentLength() {
    return dataBlockHeader.getContentLength();
  }

  /**
   * Get the index of the next data block.
   *
   * @return The index of the next data block.
   */
  public int getNextDataBlockIndex() {
    return dataBlockHeader.getNextDataBlockIndex();
  }

  /**
   * Get the content of the data block.
   *
   * @return The content.
   */
  public byte[] getContent() {
    return content;
  }

  /**
   * Set the content of the data block.
   *
   * @param content The content.
   */
  public void setContent(byte[] content) {
    this.content = content;
  }

  /**
   * Convert the data block to a byte buffer. The byte buffer is ready for reading.
   *
   * @return The byte buffer.
   */
  public ByteBuffer toByteBuffer() {
    ByteBuffer byteBuffer = ByteBuffer.allocate(DATA_BLOCK_SIZE);
    dataBlockHeader.toByteBuffer(byteBuffer);
    byteBuffer.put(content);
    byteBuffer.flip();
    return byteBuffer;
  }
}
