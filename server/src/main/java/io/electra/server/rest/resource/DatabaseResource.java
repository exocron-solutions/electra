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
import io.electra.core.ElectraCore;
import net.openhft.koloboke.collect.map.ObjObjMap;
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import org.apache.commons.codec.Charsets;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Arrays;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 * @author Felix Klauke <fklauke@itemis.de>
 */
@Path("/database")
public class DatabaseResource {

    /**
     * The core module of the database engine.
     */
    private final ElectraCore electraCore;

    /**
     * Create a new instance of the database resource.
     *
     * @param electraCore The electra core.
     */
    public DatabaseResource(ElectraCore electraCore) {
        this.electraCore = electraCore;
    }

    /**
     * Get the database of the given storage id.
     *
     * @param storageId The storage id.
     * @return The database.
     */
    private Database getDatabase(String storageId) {
        return electraCore.getDatabase(storageId);
    }

    @GET
    @Path("/{storageId}/get/{key}")
    public Response get(@PathParam("storageId") String storageId, @PathParam("key") String key) {
        Database database = getDatabase(storageId);
        return Response.ok().entity(database.get(key)).build();
    }

    @POST
    @Path("/{storageId}/get")
    public Response get(@PathParam("storageId") String storageId, byte[] key) {
        Database database = getDatabase(storageId);
        return Response.ok().entity(((DefaultDatabaseImpl) database).get(Arrays.hashCode(key))).build();
    }

    @GET
    @Path("/{storageId}/put/{key}/{value}")
    public Response put(@PathParam("storageId") String storageId, @PathParam("key") String key, @PathParam("value") String value) {
        Database database = getDatabase(storageId);
        database.save(key, value);
        return Response.ok().entity("Ok").build();
    }

    @POST
    @Path("/{storageId}/put")
    public Response put(@PathParam("storageId") String storageId, byte[] key, byte[] value) {
        Database database = getDatabase(storageId);
        ((DefaultDatabaseImpl) database).save(Arrays.hashCode(key), value);
        return Response.ok().entity("Ok").build();
    }

    @DELETE
    @Path("/{storageId}/remove/{key}")
    public Response remove(@PathParam("storageId") String storageId, @PathParam("key") String key) {
        Database database = getDatabase(storageId);
        database.remove(key);
        return Response.ok().entity("Ok").build();
    }

    @DELETE
    @Path("/{storageId}/remove")
    public Response remove(@PathParam("storageId") String storageId, byte[] key) {
        Database database = getDatabase(storageId);
        ((DefaultDatabaseImpl) database).remove(Arrays.hashCode(key));
        return Response.ok().entity("Ok").build();
    }

    @PUT
    @Path("/{storageId}/update/{key}/{newValue}")
    public Response update(@PathParam("storageId") String storageId, @PathParam("key") String key, @PathParam("newValue") String newValue) {
        Database database = getDatabase(storageId);
        database.update(key, newValue.getBytes(Charsets.UTF_8));
        return Response.ok().entity("Ok").build();
    }

    @POST
    @Path("/{storageId}/update")
    public Response update(@PathParam("storageId") String storageId, byte[] key, byte[] newValue) {
        Database database = getDatabase(storageId);
        ((DefaultDatabaseImpl) database).update(Arrays.hashCode(key), newValue);
        return Response.ok().entity("Ok").build();
    }

    @PUT
    @Path("/{storageId}/create/{storageName}")
    public Response create(@PathParam("storageId") String storageId, @PathParam("storageName") String storageName) {
        getDatabase(storageId);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{storageId}/delete/{storageName}")
    public Response delete(@PathParam("storageId") String storageId, @PathParam("storageName") String storageName) {
        electraCore.deleteDatabase(storageId);
        return Response.ok().build();
    }
}
