package io.electra.server.rest.resource;

import io.electra.server.rest.RestServer;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

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

    @GET
    @Path("/put/{key}/{value}")
    public Response put(@PathParam("key") String key, @PathParam("value") String value) {
        RestServer.getRestServer().getDatabase().save(key, value);
        return Response.ok().entity("Ok").build();
    }

    @DELETE
    @Path("/remove/{key}")
    public Response remove(@PathParam("key") String key) {
        RestServer.getRestServer().getDatabase().remove("key");
        return Response.ok().entity("Ok").build();
    }
}
