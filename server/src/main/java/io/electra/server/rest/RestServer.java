/*
 * MIT License
 *
 * Copyright (c) 2017 Felix Klauke, JackWhite20
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.electra.server.rest;

import io.electra.core.Database;
import io.electra.server.ElectraServerConstants;
import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
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

        deploymentInfo.setDeploymentName("Electra REST server");

        deploymentInfo.setContextPath("/");

        server.deploy(deploymentInfo);

        Undertow.Builder builder = Undertow.builder().addHttpListener(ElectraServerConstants.REST_PORT, ElectraServerConstants.REST_HOST);

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
