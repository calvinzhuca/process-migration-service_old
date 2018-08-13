/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.syseng.soleng.rhpam.processmigration.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/")
public class CallbackExampleApp {

    @POST
    @Path("/callback")
    @Consumes({MediaType.WILDCARD})
    public void returnAsyncMigrationResult(String reports)  {
        //This is just a sample code for callback demo
        System.out.println("####################### returnAsyncMigrationResult: " + reports);

    }

}