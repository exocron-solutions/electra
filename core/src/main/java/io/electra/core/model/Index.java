package io.electra.core.model;

import io.electra.core.exception.MalformedIndexException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class Index {

  /**
   * How big an index will be in bytes in its byte buffer representation.
   */
  public static final int INDEX_BLOCK_SIZE = 9;

  /**
   * The hash of the this value belongs to.
   */
  private final int keyHash;

  /**
   * The index of the first data block.
   */
  private int blockIndex;

  /**
   * If the index is empty.
   */
  private boolean empty = false;

  /**
   * Create a new index by its underlying values.
   *
   * @param keyHash The hash of the this value belongs to.
   * @param blockIndex The block index.
   */
  public Index(int keyHash, int blockIndex) {
    this.keyHash = keyHash;
    this.blockIndex = blockIndex;
  }

  /**
   * Read all data from the given byte buffer and create a new index from it.
   *
   * @param byteBuffer The byte buffer.
   * @return The index instance.
   */
  public static Index fromByteBuffer(ByteBuffer byteBuffer) {
    try {
      int keyHash = byteBuffer.getInt();
      int dataBlockIndex = byteBuffer.getInt();

      boolean empty = byteBuffer.get() == 1;
      Index index = new Index(keyHash, dataBlockIndex);
      index.setEmpty(empty);
      return index;
    } catch (BufferUnderflowException e) {
      throw new MalformedIndexException("Error while reading index from byte buffer.", e);
    }
  }

  /**
   * Get the hash of the this value belongs to.
   *
   * @return The hash of the this value belongs to.
   */
  public int getKeyHash() {
    return keyHash;
  }

  /**
   * Get the index of the first block.
   *
   * @return The block index.
   */
  public int getBlockIndex() {
    return blockIndex;
  }

  /**
   * Set the index of the block.
   *
   * @param blockIndex The index of the block.
   */
  public void setBlockIndex(int blockIndex) {
    this.blockIndex = blockIndex;
  }

  /**
   * If the index is empty.
   *
   * @return If the index is empty.
   */
  public boolean isEmpty() {
    return empty;
  }

  /**
   * Set if the index is empty.
   *
   * @param empty If the index is empty.
   */
  public void setEmpty(boolean empty) {
    this.empty = empty;
  }

  /**
   * Write this index to byte buffer, that will be ready for reading.
   *
   * @return The byte buffer.
   */
  public ByteBuffer toByteBuffer() {
    ByteBuffer byteBuffer = ByteBuffer.allocate(INDEX_BLOCK_SIZE);
    byteBuffer.putInt(keyHash);
    byteBuffer.putInt(blockIndex);
    byteBuffer.put((byte) (empty ? 1 : 0));
    byteBuffer.flip();
    return byteBuffer;
  }
}
