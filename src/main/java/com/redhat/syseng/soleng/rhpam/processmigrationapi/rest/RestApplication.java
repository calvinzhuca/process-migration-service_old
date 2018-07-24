package com.redhat.syseng.soleng.rhpam.processmigrationapi.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

@ApplicationPath("/")
public class RestApplication extends Application {
	@GET
	@Produces("text/plain")
	public Response doGet() {
		return Response.ok("Hello from RestApplication!").build();
	}
}
