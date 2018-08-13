package com.redhat.syseng.soleng.rhpam.processmigration.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redhat.syseng.soleng.rhpam.processmigration.model.MigrationObject;
import com.redhat.syseng.soleng.rhpam.processmigration.model.MigrationPlan;
import com.redhat.syseng.soleng.rhpam.processmigration.model.MigrationPlanTableObject;
import com.redhat.syseng.soleng.rhpam.processmigration.persistence.Persistence;
import com.redhat.syseng.soleng.rhpam.processmigration.util.MigrationUtils;
import com.redhat.syseng.soleng.rhpam.processmigration.util.PATCH;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.kie.server.api.model.admin.MigrationReportInstance;

@ApplicationScoped
@Path("/")
public class MigrationServiceApplication {

    @GET
    @Path("/plans/{planId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPlan(@PathParam("planId") String planId) throws NamingException {
        System.out.println("!!!!!!!!!!!!!!!! getPlan" + planId);
        //List<MigrationReportInstance> reports =migrateInstance(plan);
        //System.out.println("!!!!!!!!!!!!!!!!!!!Executing MigrationPlan result: " + reports.toString());
        String returnJson = Persistence.getInstance().retrievePlan(planId);
        System.out.println("!!!!!!!!!!!!!!!!!!!getPlan: " + returnJson);
        //String returnJson = "{\"planId\": " + planId + ", \"plan\" " + plan + "}";
        if (null == returnJson || returnJson.equals("")) {
            returnJson = "{\"status\": \"couldn't find related record\" }";

        }
        return Response.ok(returnJson).build();
    }

    @GET
    @Path("/plans")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllPlans() throws NamingException {
        //System.out.println("!!!!!!!!!!!!!!!! getAllPlans");
        //List<MigrationReportInstance> reports =migrateInstance(plan);
        //System.out.println("!!!!!!!!!!!!!!!!!!!Executing MigrationPlan result: " + reports.toString());
        String returnJson = Persistence.getInstance().retrievePlan(null);
        if (null == returnJson || returnJson.equals("")) {
            returnJson = "{\"status\": \"couldn't find related record\" }";

        }
        return Response.ok(returnJson).build();
    }

    @POST
    @Path("/plans")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response submitPlan(MigrationPlan plan) throws NamingException {
        System.out.println("!!!!!!!!!!!!!!!! submitPlan" + plan);
        //List<MigrationReportInstance> reports =migrateInstance(plan);
        //System.out.println("!!!!!!!!!!!!!!!!!!!Executing MigrationPlan result: " + reports.toString());
        Gson gson = new GsonBuilder().create();
        String planInString = gson.toJson(plan);
        System.out.println("!!!!!!!!!!!!!!!! planInString" + planInString);
        planInString = planInString.replaceAll("\"", "&quote;");
        System.out.println("!!!!!!!!!!!!!!!! planInString" + planInString);

        int planId = Persistence.getInstance().addPlan(planInString);
        String returnJson = "{\"planId\": " + planId + "}";
        return Response.ok(returnJson).build();
    }

    @DELETE
    @Path("/plans/{planId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response deletePlan(@PathParam("planId") String planId) throws NamingException {
        System.out.println("!!!!!!!!!!!!!!!! deletePlan" + planId);
        //List<MigrationReportInstance> reports =migrateInstance(plan);
        //System.out.println("!!!!!!!!!!!!!!!!!!!Executing MigrationPlan result: " + reports.toString());
        Persistence.getInstance().deletePlan(planId);
        String returnJson = "{\"status\": \"deleted\" }";
        return Response.ok(returnJson).build();
    }

    @PATCH
    @Path("/plans/{planId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updatePlan(@PathParam("planId") String planId, MigrationPlan plan) throws NamingException {
        System.out.println("!!!!!!!!!!!!!!!! updatePlan" + plan);
        Gson gson = new GsonBuilder().create();
        String planInString = gson.toJson(plan);
        planInString = planInString.replaceAll("\"", "&quote;");
        Persistence.getInstance().updatePlan(planId, planInString);
        String returnJson = "{\"status\": \"updated\" }";
        return Response.ok(returnJson).build();
    }

    @GET
    @Path("/migrations")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllMigrations() throws NamingException {
        System.out.println("!!!!!!!!!!!!!!!! getAllMigrations");
        String returnJson = "{\"result\":[" + Persistence.getInstance().retrieveMigrationRecord(null) + "]}";
        return Response.ok(returnJson).build();
    }

    @GET
    @Path("/migrations/{migrationId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMigration(@PathParam("migrationId") String migrationId) throws NamingException {
        System.out.println("!!!!!!!!!!!!!!!! getMigration" + migrationId);
        String returnJson = "{\"result\":[" + Persistence.getInstance().retrieveMigrationRecord(migrationId) + "]}";
        return Response.ok(returnJson).build();
    }

    @POST
    @Path("/migrations")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response submitMigration(MigrationObject migrationObject) throws NamingException, ParseException {
        String planId = migrationObject.getPlanId();
        String planinJson = Persistence.getInstance().retrievePlan(planId);
       Gson gson = new Gson();
        MigrationPlanTableObject planObject = gson.fromJson(planinJson, MigrationPlanTableObject.class);
        String migrationId;
        String returnJson;

        if (null != migrationObject.getExecution()) {
            //async, schedule the run
            migrationId = Persistence.getInstance().addMigrationRecord(planId, migrationObject.getProcessInstancesId(), migrationObject.getExecution().getExecuteTime(), migrationObject.getExecution().getCallbackUrl());
            MigrationUtils.scheduleMigration(migrationId, migrationObject, planObject);
            //only need to return migrationId for async, because the report will be stored later, plus callback...
            returnJson = "{\"migrationId\":\"" + migrationId + "\"}";
        } else {
            //sync, call KIE directly
            migrationId = Persistence.getInstance().addMigrationRecord(planId, migrationObject.getProcessInstancesId(), "", "");
            List<MigrationReportInstance> reports = MigrationUtils.migrateInstance(planObject.getMigrationPlan(), migrationObject.getProcessInstancesId());
            //logInfo("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!reports " + reports);
            Persistence.getInstance().updateMigrationRecord(migrationId, migrationObject.getPlanId(), migrationObject.getProcessInstancesId(), reports);

            System.out.println("!!!!!!!!!!!!!!!!!!!Executing MigrationPlan result: " + reports.toString());
            returnJson = "{\"migrationId\":\"" + migrationId + "\", \"MigrationReports\":\"" + reports + "\"}";

        }
        return Response.ok(returnJson).build();
    }

    @DELETE
    @Path("/migrations/{migrationId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response deleteMigration(@PathParam("migrationId") int migrationId) throws NamingException {
        System.out.println("!!!!!!!!!!!!!!!! deleteMigration" + migrationId);
        //List<MigrationReportInstance> reports =migrateInstance(plan);
        //System.out.println("!!!!!!!!!!!!!!!!!!!Executing MigrationPlan result: " + reports.toString());
        Persistence.getInstance().deleteMigrationRecord(migrationId);
        String returnJson = "{\"status\": \"deleted\" }";
        return Response.ok(returnJson).build();
    }

    @PATCH
    @Path("/migrations/{migrationId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updateMigration(@PathParam("migrationId") String migrationId, MigrationObject migrationObject) throws NamingException {
        System.out.println("!!!!!!!!!!!!!!!! updateMigration" + migrationObject);
        //List<MigrationReportInstance> reports =migrateInstance(plan);
        //System.out.println("!!!!!!!!!!!!!!!!!!!Executing MigrationPlan result: " + reports.toString());
        Persistence.getInstance().updateMigrationRecord(migrationId, migrationObject.getPlanId(), migrationObject.getProcessInstancesId(), null);
        String returnJson = "{\"status\": \"updated\" }";
        return Response.ok(returnJson).build();
    }

    private static void logInfo(String message) {
        Logger.getLogger(MigrationServiceApplication.class.getName()).log(Level.INFO, message);
    }

}
