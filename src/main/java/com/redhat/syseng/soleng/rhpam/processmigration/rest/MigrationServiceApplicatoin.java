package com.redhat.syseng.soleng.rhpam.processmigration.rest;

import com.redhat.syseng.soleng.rhpam.processmigration.model.MigrationPlan;
import com.redhat.syseng.soleng.rhpam.processmigration.util.MigrationUtils;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.kie.server.api.model.admin.MigrationReportInstance;
import org.kie.server.client.admin.impl.ProcessAdminServicesClientImpl;
import org.kie.server.client.impl.KieServicesClientImpl;
import org.kie.server.client.impl.KieServicesConfigurationImpl;

@ApplicationScoped
@Path("/")
public class MigrationServiceApplicatoin {
    private static final String JMS_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String JMS_QUEUE_REQUEST = "jms/queue/KIE.SERVER.REQUEST";
    private static final String JMS_QUEUE_RESPONSE = "jms/queue/KIE.SERVER.RESPONSE";    
    private static String kieServiceUrl = MigrationUtils.protocol + "://" + MigrationUtils.getKieHost() + ":" + MigrationUtils.getKiePort() + "/" + MigrationUtils.getKieContextRoot() + "services/rest/server";

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
    
    
    public static void executePlan(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws NamingException, ServletException, IOException {
        System.out.println("-----------------------------executePlan started ");
        MigrationPlan plan = (MigrationPlan) httpRequest.getSession().getAttribute("migrationPlan");
        logInfo("!!!!!!!!!!!!!!!!!!!Executing MigrationPlan now:" + plan);
        List<MigrationReportInstance> reports =migrateInstance(plan);
        System.out.println("!!!!!!!!!!!!!!!!!!!Executing MigrationPlan result: " + reports.toString());

        httpRequest.getSession().removeAttribute("migrationPlan");
        httpRequest.getSession().removeAttribute("kieContainers");
        httpRequest.getSession().removeAttribute("processInfo");
        httpRequest.setAttribute("migrationReports", reports);
        RequestDispatcher requestDispatcher = httpRequest.getRequestDispatcher("/migrationReport.jsp");
        requestDispatcher.forward(httpRequest, httpResponse);        
        System.out.println("-----------------------------executePlan ended ");
    }
    
    public static List<MigrationReportInstance> migrateInstance(MigrationPlan plan) throws NamingException {

        ProcessAdminServicesClientImpl client = setupProcessAdminServicesClient(plan, kieServiceUrl, MigrationUtils.getKieUsername(), MigrationUtils.getKiePassword());
        List<MigrationReportInstance> reports = client.migrateProcessInstances(plan.getContainerId(), plan.getProcessInstancesId(), plan.getTargetContainerId(), plan.getTargetProcessId(), plan.getNodeMapping());
        return reports;
    }
    
    
    private static void logInfo(String message) {
        Logger.getLogger(MigrationServiceApplicatoin.class.getName()).log(Level.INFO, message);
    }    
    
    
    public static ProcessAdminServicesClientImpl setupProcessAdminServicesClient(MigrationPlan unit, String url, String username, String password) throws NamingException {

        String provider_url = System.getenv("KIE_JMS_PROVIDER_URL");

        KieServicesConfigurationImpl config = null;
        if (Boolean.valueOf(unit.getUseRest())) {
            //REST config
            config = new KieServicesConfigurationImpl(url, username, password);
        } else {
            //JMS config

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
