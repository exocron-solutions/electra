package io.electra.server;

import io.electra.server.rest.RestServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by JackWhite20 on 27.11.2017.
 */
public class ElectraBootstrap {

    private static Logger logger = LoggerFactory.getLogger(ElectraBootstrap.class);

    private static final Path indexFilePath = Paths.get(DatabaseConstants.DEFAULT_INDEX_FILE_PATH);

    private static final Path dataFilePath = Paths.get(DatabaseConstants.DEFAULT_DATA_FILE_PATH);

    private static RestServer restServer;

    public static void main(String[] args) {
        logger.info("Starting electra");

        Database database = DatabaseFactory.createDatabase(dataFilePath, indexFilePath);

        restServer = new RestServer(database);
        restServer.start();

        logger.info("Electra started");
    }
}
