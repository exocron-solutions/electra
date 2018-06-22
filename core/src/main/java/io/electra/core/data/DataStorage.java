package io.electra.core.data;

import io.electra.core.index.Index;
import io.electra.core.model.DataRecord;
import io.electra.core.storage.Storage;

import java.util.concurrent.Future;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface DataStorage extends Storage {

    /**
     * Read a data record beginning with the data block the given index is pointing at.
     *
     * @param index The index.
     *
     * @return The future of the data record.
     */
    Future<DataRecord> readDataRecord(Index index);

    /**
     * Write the given data indexed by the given index.
     *
     * @param index The index.
     * @param data  The data.
     *
     * @return The future of the index.
     */
    Future<Index> writeData(Index index, byte[] data);
}
