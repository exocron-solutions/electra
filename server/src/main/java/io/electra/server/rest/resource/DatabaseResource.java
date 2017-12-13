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

package io.electra.server.rest.resource;

import io.electra.core.DefaultDatabaseImpl;
import io.electra.server.rest.RestServer;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Arrays;

/**
 * Created by JackWhite20 on 27.11.2017.
 */
@Path("/database")
public class DatabaseResource {

    @GET
    @Path("/get/{key}")
    public Response get(@PathParam("key") String key) {
        return Response.ok().entity(RestServer.getRestServer().getDatabase().get(key)).build();
    }

    @POST
    @Path("/get")
    public Response get(byte[] key) {
        return Response.ok().entity(((DefaultDatabaseImpl) RestServer.getRestServer().getDatabase()).get(Arrays.hashCode(key))).build();
    }

    @GET
    @Path("/put/{key}/{value}")
    public Response put(@PathParam("key") String key, @PathParam("value") String value) {
        RestServer.getRestServer().getDatabase().save(key, value);
        return Response.ok().entity("Ok").build();
    }

    @POST
    @Path("/put")
    public Response put(byte[] key, byte[] value) {
        ((DefaultDatabaseImpl) RestServer.getRestServer().getDatabase()).save(Arrays.hashCode(key), value);
        return Response.ok().entity("Ok").build();
    }

    @DELETE
    @Path("/remove/{key}")
    public Response remove(@PathParam("key") String key) {
        RestServer.getRestServer().getDatabase().remove(key);
        return Response.ok().entity("Ok").build();
    }

    @DELETE
    @Path("/remove")
    public Response remove(byte[] key) {
        ((DefaultDatabaseImpl) RestServer.getRestServer().getDatabase()).remove(Arrays.hashCode(key));
        return Response.ok().entity("Ok").build();
    }
}
