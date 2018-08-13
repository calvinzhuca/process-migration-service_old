package com.redhat.syseng.soleng.rhpam.processmigration.util;

import com.redhat.syseng.soleng.rhpam.processmigration.model.MigrationObject;
import com.redhat.syseng.soleng.rhpam.processmigration.model.MigrationPlan;
import com.redhat.syseng.soleng.rhpam.processmigration.model.MigrationPlanTableObject;
import com.redhat.syseng.soleng.rhpam.processmigration.model.MigrationPlanUnit;
import com.redhat.syseng.soleng.rhpam.processmigration.persistence.Persistence;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import org.kie.server.api.model.admin.MigrationReportInstance;
import org.kie.server.client.admin.impl.ProcessAdminServicesClientImpl;
import org.kie.server.client.impl.KieServicesClientImpl;
import org.kie.server.client.impl.KieServicesConfigurationImpl;

public class MigrationUtils {

    private static Logger logger = Logger.getLogger(MigrationUtils.class.getName());

    //String kieHost = System.getenv("KIE_HOST");        
    //int kiePort = Integer.valueOf(System.getenv("KIE_PORT"));        
//    public static String kieHost = "myapp-kieserver-calvin-test.rhdp.ocp.cloud.lab.eng.bos.redhat.com";
//    public static int kiePort = 80;
//    public static String kieContextRoot = "";
    private static String kieHost;
    private static String kiePort;
    private static String kieContextRoot;
    private static String kieUsername;
    private static String kiePassword;
    public static String protocol = "http";
    public static String jmsProtocol = "http-remoting";

    private static final String JMS_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String JMS_QUEUE_REQUEST = "jms/queue/KIE.SERVER.REQUEST";
    private static final String JMS_QUEUE_RESPONSE = "jms/queue/KIE.SERVER.RESPONSE";
    private static final String KIE_SERVICE_URL = protocol + "://" + getKieHost() + ":" + getKiePort() + "/" + getKieContextRoot() + "services/rest/server";
    private static final String KIE_JMS_SERVICE_URL = jmsProtocol + "://" + getKieHost() + ":" + getKiePort();

    public static String getKieHost() {
        if (kieHost == null) {
            getSystemEnvForKie();
        }
        return kieHost;
    }

    public static String getKiePort() {
        if (kiePort == null) {
            getSystemEnvForKie();
        }
        return kiePort;
    }

    public static String getKieUsername() {
        if (kieUsername == null) {
            getSystemEnvForKie();
        }
        return kieUsername;
    }

    public static String getKiePassword() {
        if (kiePassword == null) {
            getSystemEnvForKie();
        }
        return kiePassword;
    }

    public static String getKieContextRoot() {
        if (kieContextRoot == null) {
            kieContextRoot = System.getenv("KIE_CONTEXT_ROOT");
            //in OCP, this won't be defined, so set to empty string. 
            if (kieContextRoot == null) {
                kieContextRoot = "";
            }
        }
        return kieContextRoot;
    }

    private static void getSystemEnvForKie() {
        kieUsername = System.getenv("KIE_SERVER_USER");
        kiePassword = System.getenv("KIE_SERVER_PWD");

        //because in OCP template's environment variable is in this format ${MYAPP}_KIESERVER_SERVICE_HOST
        //so need to loop through all and find the matching one
        Map<String, String> envs = System.getenv();
        for (String envName : envs.keySet()) {
//                System.out.format("%s=%s%n", envName, envs.get(envName));
            if (envName.contains("KIESERVER_SERVICE_HOST")) {
                kieHost = envs.get(envName);
                //System.out.println("!!!!!!!!!!!!!!!!!!!!! kieHost " + kieHost);
            } else if (envName.contains("KIESERVER_SERVICE_PORT")) {
                kiePort = envs.get(envName);
                //System.out.println("!!!!!!!!!!!!!!!!!!!!! kiePort " + kiePort);
            }
        }

    }

    private static void logInfo(String message) {
        Logger.getLogger(MigrationUtils.class.getName()).log(Level.INFO, message);
    }

    public static List<MigrationReportInstance> migrateInstance(MigrationPlan plan, List<Long> processInstancesId) throws NamingException {
        ProcessAdminServicesClientImpl client = setupProcessAdminServicesClient(plan, KIE_SERVICE_URL, MigrationUtils.getKieUsername(), MigrationUtils.getKiePassword(), plan.isRest());
        MigrationPlanUnit unit = plan.getMigrationPlanUnit();
        List<MigrationReportInstance> reports = client.migrateProcessInstances(unit.getContainerId(), processInstancesId, unit.getTargetContainerId(), unit.getTargetProcessId(), unit.getNodeMapping());
        return reports;
    }

    public static ProcessAdminServicesClientImpl setupProcessAdminServicesClient(MigrationPlan plan, String url, String username, String password, boolean isRest) throws NamingException {

        KieServicesConfigurationImpl config = null;
        if (isRest) {
            //REST config for sync mode to KIE server
            logInfo("!!!!!!!!!!!!!! sync mode using REST client");
            config = new KieServicesConfigurationImpl(url, username, password);

        } else {
            //JMS config for aysnc mode to KIE server
            logInfo("!!!!!!!!!!!!!! Async mode using JMS client");

            logInfo(" kieJmsServiceUrl: " + KIE_JMS_SERVICE_URL);
            logInfo(" username: " + username);
            logInfo(" password: " + password);

            java.util.Properties env = new java.util.Properties();
            env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
            env.put(javax.naming.Context.PROVIDER_URL, KIE_JMS_SERVICE_URL);
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

    public static void scheduleMigration(String migrationId, MigrationObject migrationObject, MigrationPlanTableObject planObject) throws ParseException {
        String inputTime = migrationObject.getExecution().getExecuteTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date desiredDate = df.parse(inputTime);

        long now = System.currentTimeMillis();
        long delay = desiredDate.getTime() - now;

        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    //boolean Programisrunning = false;
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!running the scheduler");

                    List<MigrationReportInstance> reports = MigrationUtils.migrateInstance(planObject.getMigrationPlan(), migrationObject.getProcessInstancesId());

                    Persistence.getInstance().updateMigrationRecord(migrationId, migrationObject.getPlanId(), migrationObject.getProcessInstancesId(), reports);

                    //call back
                    ResteasyClient client = new ResteasyClientBuilder().build();
                    ResteasyWebTarget target = client.target(migrationObject.getExecution().getCallbackUrl());
                    Response response = target.request().post(Entity.text(reports));
                    
                } catch (NamingException ex) {
                    Logger.getLogger(MigrationUtils.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }, delay, TimeUnit.MILLISECONDS); // run in "delay" millis            
    }

}
