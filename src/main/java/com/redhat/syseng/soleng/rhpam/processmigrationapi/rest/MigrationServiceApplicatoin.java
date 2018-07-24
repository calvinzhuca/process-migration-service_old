package com.redhat.syseng.soleng.rhpam.processmigrationapi.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/")
public class MigrationServiceApplicatoin {

    @GET
    @Produces("text/plain")
    @Path("/hello1/{containerId}")    
    public Response get1(@PathParam("containerId") String containerId) {
        System.out.println("!!!!!!!!!!!!!!!! here 1" + containerId);
        return Response.ok("Hello from MigrationServiceApplicatoin.get1!").build();
    }

    @GET
    @Produces("text/plain")
    @Path("/hello2")
    public Response get2() {
        System.out.println("!!!!!!!!!!!!!!!! here 2");
        return Response.ok("Hello from MigrationServiceApplicatoin.get2!").build();
    }
}
