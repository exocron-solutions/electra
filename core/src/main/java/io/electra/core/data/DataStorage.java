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
}
