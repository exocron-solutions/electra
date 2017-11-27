package io.electra.server.rest;

import io.electra.server.Database;
import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by JackWhite20 on 27.11.2017.
 */
public class RestServer {

    private static Logger logger = LoggerFactory.getLogger(RestServer.class);

    private static RestServer restServer;

    private UndertowJaxrsServer server;

    private Database database;

    public RestServer(Database database) {
        restServer = this;

        this.database = database;
    }

    public void start() {
        logger.info("Starting REST server");

        server = new UndertowJaxrsServer();

        ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setApplicationClass(ElectraApplication.class.getName());

        DeploymentInfo deploymentInfo = server.undertowDeployment(deployment, "/");
        deploymentInfo.setClassLoader(RestServer.class.getClassLoader());
        deploymentInfo.setDeploymentName("Electra REST service");
        deploymentInfo.setContextPath("/");

        server.deploy(deploymentInfo);

        Undertow.Builder builder = Undertow.builder().addHttpListener(8080, "0.0.0.0");

        server.start(builder);

        logger.info("REST server started");
    }

    public Database getDatabase() {
        return database;
    }

    public static RestServer getRestServer() {
        return restServer;
    }
}
