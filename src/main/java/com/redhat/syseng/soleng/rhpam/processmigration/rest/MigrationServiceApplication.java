package com.redhat.syseng.soleng.rhpam.processmigration.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redhat.syseng.soleng.rhpam.processmigration.model.MigrationObject;
import com.redhat.syseng.soleng.rhpam.processmigration.model.MigrationPlan;
import com.redhat.syseng.soleng.rhpam.processmigration.model.MigrationPlanTableObject;
import com.redhat.syseng.soleng.rhpam.processmigration.model.MigrationPlanUnit;
import com.redhat.syseng.soleng.rhpam.processmigration.persistence.Persistence;
import com.redhat.syseng.soleng.rhpam.processmigration.util.MigrationUtils;
import com.redhat.syseng.soleng.rhpam.processmigration.util.PATCH;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.InitialContext;
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
import org.kie.server.client.admin.impl.ProcessAdminServicesClientImpl;
import org.kie.server.client.impl.KieServicesClientImpl;
import org.kie.server.client.impl.KieServicesConfigurationImpl;

@ApplicationScoped
@Path("/")
public class MigrationServiceApplication {
    private static final String JMS_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String JMS_QUEUE_REQUEST = "jms/queue/KIE.SERVER.REQUEST";
    private static final String JMS_QUEUE_RESPONSE = "jms/queue/KIE.SERVER.RESPONSE";    
    private static String kieServiceUrl = MigrationUtils.protocol + "://" + MigrationUtils.getKieHost() + ":" + MigrationUtils.getKiePort() + "/" + MigrationUtils.getKieContextRoot() + "services/rest/server";


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
        if (null == returnJson || returnJson.equals("")){
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
        if (null == returnJson || returnJson.equals("")){
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
        planInString = planInString.replaceAll("\"","&quote;");
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
        planInString = planInString.replaceAll("\"","&quote;");
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
        String returnJson = "{\"result\":[" + Persistence.getInstance().retrieveMigration(null) + "]}";
        return Response.ok(returnJson).build();
    }

    @GET
    @Path("/migrations/{migrationId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMigration(@PathParam("migrationId") String migrationId) throws NamingException {
        System.out.println("!!!!!!!!!!!!!!!! getMigration" + migrationId);
        String returnJson = "{\"result\":[" + Persistence.getInstance().retrieveMigration(migrationId) + "]}";
        return Response.ok(returnJson).build();
    }
    
    
    
    @POST
    @Path("/migrations")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response submitMigration(MigrationObject migrationObject) throws NamingException {
        String planId = migrationObject.getPlanId();
        String planinJson = Persistence.getInstance().retrievePlan(planId);
        Gson gson = new Gson();
        MigrationPlanTableObject planObject = gson.fromJson(planinJson, MigrationPlanTableObject.class);
        List<MigrationReportInstance> reports =migrateInstance(planObject.getMigrationPlan());
        System.out.println("!!!!!!!!!!!!!!!!!!!Executing MigrationPlan result: " + reports.toString());
        int migrationId = Persistence.getInstance().addMigration(planId);
        String returnJson = "{\"migrationId\": " + migrationId + ", \"MigrationReports\":" + reports + "}";
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
        Persistence.getInstance().deleteMigration(migrationId);
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
        Persistence.getInstance().updateMigration(migrationId, migrationObject.getPlanId());
        String returnJson = "{\"status\": \"updated\" }";
        return Response.ok(returnJson).build();
    }      
    
    public static List<MigrationReportInstance> migrateInstance(MigrationPlan plan) throws NamingException {

        ProcessAdminServicesClientImpl client = setupProcessAdminServicesClient(plan, kieServiceUrl, MigrationUtils.getKieUsername(), MigrationUtils.getKiePassword());
        MigrationPlanUnit unit = plan.getMigrationPlanUnit();
        List<MigrationReportInstance> reports = client.migrateProcessInstances(unit.getContainerId(), unit.getProcessInstancesId(), unit.getTargetContainerId(), unit.getTargetProcessId(), unit.getNodeMapping());
        return reports;
    }
    
    
    private static void logInfo(String message) {
        Logger.getLogger(MigrationServiceApplication.class.getName()).log(Level.INFO, message);
    }    
    
    
    public static ProcessAdminServicesClientImpl setupProcessAdminServicesClient(MigrationPlan plan, String url, String username, String password) throws NamingException {

        String provider_url = System.getenv("KIE_JMS_PROVIDER_URL");

        KieServicesConfigurationImpl config = null;
        if (!Boolean.valueOf(plan.isAsync())) {
            //REST config 
            config = new KieServicesConfigurationImpl(url, username, password);
        } else {
            //JMS config for Aysnc mode

            java.util.Properties env = new java.util.Properties();
            env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
            env.put(javax.naming.Context.PROVIDER_URL, provider_url);
            env.put(javax.naming.Context.SECURITY_PRINCIPAL, username);
            env.put(javax.naming.Context.SECURITY_CREDENTIALS, password);
            InitialContext ctx = new InitialContext(env);

            ConnectionFactory conn = (ConnectionFactory) ctx.lookup(JMS_CONNECTION_FACTORY);
            Queue respQueue = (Queue) ctx.lookup(JMS_QUEUE_RESPONSE);
            Queue reqQueue = (Queue) ctx.lookup(JMS_QUEUE_REQUEST);

            config = new KieServicesConfigurationImpl(conn, reqQueue, respQueue, username, password);

        }

        ProcessAdminServicesClientImpl client = new ProcessAdminServicesClientImpl(config);
        KieServicesClientImpl kieServicesClientImpl = new KieServicesClientImpl(config);
        client.setOwner(kieServicesClientImpl);

        return client;

    }    
}
