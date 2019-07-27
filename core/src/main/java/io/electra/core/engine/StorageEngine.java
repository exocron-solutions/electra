package io.electra.core.engine;

import io.electra.core.model.Index;
import java.io.Closeable;
import java.util.concurrent.Future;

/**
 * The engine is the part that puts together index and data and will perform sequence operations
 * like searching for an index and then searching for the corresponding data.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface StorageEngine extends Closeable {

  /**
   * Save the given value for the given key hash.
   *
   * @param keyHash The hash of the key.
   * @param value The value.
   * @return The future of the index.
   */
  Future<Index> save(int keyHash, byte[] value);
}
