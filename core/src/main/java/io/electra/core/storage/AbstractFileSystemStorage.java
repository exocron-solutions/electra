package io.electra.core.storage;

import io.electra.core.exception.FileSystemAccessException;
import io.electra.core.filesystem.DuplexAsynchronousFileChannelFileSystemAccessor;
import io.electra.core.filesystem.FileSystemAccessor;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public abstract class AbstractFileSystemStorage implements Storage {

  /**
   * The underlying file system accessor that will handle low level file access.
   */
  private final FileSystemAccessor fileSystemAccessor;

  /**
   * Create a new file system based storage.
   *
   * @param filePath The path of the target file.
   */
  public AbstractFileSystemStorage(Path filePath) throws FileSystemAccessException {
    this.fileSystemAccessor = new DuplexAsynchronousFileChannelFileSystemAccessor(filePath);
  }

  @Override
  public void clear() throws FileSystemAccessException {
    getFileSystemAccessor().clear();

    doClear();
  }

  @Override
  public void close() throws IOException {
    getFileSystemAccessor().close();

    doClose();
  }

  /**
   * Executed via {@link #clear()}.
   */
  protected abstract void doClear();

  /**
   * Executed via {@link #close()}.
   */
  protected abstract void doClose();

  /**
   * Get the file system accessor.
   *
   * @return The file system accessor.
   */
  public FileSystemAccessor getFileSystemAccessor() {
    return fileSystemAccessor;
  }
}
