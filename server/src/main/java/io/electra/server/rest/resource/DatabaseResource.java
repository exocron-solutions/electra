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

import io.electra.core.Database;
import io.electra.core.DefaultDatabaseImpl;
import org.apache.commons.codec.Charsets;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Arrays;

/**
 * Created by JackWhite20 on 27.11.2017.
 */
@Path("/database")
public class DatabaseResource {

    private Database database;

    public DatabaseResource(Database database) {
        this.database = database;
    }

    @GET
    @Path("/get/{key}")
    public Response get(@PathParam("key") String key) {
        return Response.ok().entity(database.get(key)).build();
    }

    @POST
    @Path("/get")
    public Response get(byte[] key) {
        return Response.ok().entity(((DefaultDatabaseImpl) database).get(Arrays.hashCode(key))).build();
    }

    @GET
    @Path("/put/{key}/{value}")
    public Response put(@PathParam("key") String key, @PathParam("value") String value) {
        database.save(key, value);
        return Response.ok().entity("Ok").build();
    }

    @POST
    @Path("/put")
    public Response put(byte[] key, byte[] value) {
        ((DefaultDatabaseImpl) database).save(Arrays.hashCode(key), value);
        return Response.ok().entity("Ok").build();
    }

    @DELETE
    @Path("/remove/{key}")
    public Response remove(@PathParam("key") String key) {
        database.remove(key);
        return Response.ok().entity("Ok").build();
    }

    @DELETE
    @Path("/remove")
    public Response remove(byte[] key) {
        ((DefaultDatabaseImpl) database).remove(Arrays.hashCode(key));
        return Response.ok().entity("Ok").build();
    }

    @PUT
    @Path("/update/{key}/{newValue}")
    public Response update(@PathParam("key") String key, @PathParam("newValue") String newValue) {
        database.update(key, newValue.getBytes(Charsets.UTF_8));
        return Response.ok().entity("Ok").build();
    }

    @POST
    @Path("/update")
    public Response update(byte[] key, byte[] newValue) {
        ((DefaultDatabaseImpl) database).update(Arrays.hashCode(key), newValue);
        return Response.ok().entity("Ok").build();
    }

    @PUT
    @Path("/create/{storageName}")
    public Response create(@PathParam("storageName") String storageName) {
        // TODO: 16.12.2017 Create the actual storage
        return Response.ok().build();
    }

    @DELETE
    @Path("/delete/{storageName}")
    public Response delete(@PathParam("storageName") String storageName) {
        // TODO: 16.12.2017 Delete the actual storage
        return Response.ok().build();
    }
}
