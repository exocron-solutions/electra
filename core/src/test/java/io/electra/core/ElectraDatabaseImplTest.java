package io.electra.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
class ElectraDatabaseImplTest {

    private ElectraDatabase electraDatabase;

    @BeforeEach
    void setUp() {
        electraDatabase = new ElectraDatabaseImpl();
    }

    @Test
    void testClose() throws Exception {
        electraDatabase.close();
    }
}