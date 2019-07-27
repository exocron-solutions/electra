package io.electra.core;

import io.electra.core.engine.SimpleStorageEngine;
import io.electra.core.engine.StorageEngine;
import io.electra.core.exception.EngineInitializationException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class ElectraDatabaseImpl implements ElectraDatabase {

  private final StorageEngine storageEngine;

  ElectraDatabaseImpl(Path databaseFolder) throws EngineInitializationException {
    Path indexPath = databaseFolder.resolve("index.lctr");
    Path dataPath = databaseFolder.resolve("data.lctr");

    storageEngine = new SimpleStorageEngine(dataPath, indexPath);
  }

  @Override
  public void close() throws IOException {
    storageEngine.close();
  }
}
