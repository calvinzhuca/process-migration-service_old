package com.redhat.syseng.soleng.rhpam.processmigration.util;


import com.redhat.syseng.soleng.rhpam.processmigration.model.MigrationPlan;
import javax.servlet.http.HttpServletRequest;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import java.io.StringWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.apache.http.client.utils.URIBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

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
                System.out.println("!!!!!!!!!!!!!!!!!!!!! kieHost " + kieHost);
            } else if (envName.contains("KIESERVER_SERVICE_PORT")) {
                kiePort = envs.get(envName);
                System.out.println("!!!!!!!!!!!!!!!!!!!!! kiePort " + kiePort);
            }
        }

    }

    private static ResteasyClient createRestClientWithCerts(boolean useOcpCertificate) {
        ResteasyClient client;

        if (useOcpCertificate) {
            //use the OCP certificate which exist here in every pod: /var/run/secrets/kubernetes.io/serviceaccount/ca.crt
            try (FileInputStream in = new FileInputStream("/var/run/secrets/kubernetes.io/serviceaccount/ca.crt")) {

                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Certificate cert = cf.generateCertificate(in);
                //logger.info("createRestClientWithCerts, created Certificate from /var/run/secrets/kubernetes.io/serviceaccount/ca.crt");

                // load the keystore that includes self-signed cert as a "trusted" entry
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                keyStore.setCertificateEntry("ocp-cert", cert);
                tmf.init(keyStore);
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, tmf.getTrustManagers(), null);
                //logger.info("createRestClientWithCerts, created SSLContext");

                //For proper HTTPS authentication
                ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
                clientBuilder.sslContext(ctx);
                client = clientBuilder.build();
            } catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException | KeyManagementException ex) {
                logger.log(Level.SEVERE, null, ex);
                throw new IllegalStateException(ex);
            }

            //use filter to add http header
            //RestClientRequestFilter filter = new RestClientRequestFilter();
            //client.register(filter);
        } else {
            client = new ResteasyClientBuilder().build();
        }
        return client;
    }

    private static URIBuilder getUriBuilder(Object... path) {

        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme(protocol);

        uriBuilder.setHost(getKieHost());
        uriBuilder.setPort(Integer.parseInt(getKiePort()));

        StringWriter stringWriter = new StringWriter();
        for (Object part : path) {
            stringWriter.append('/').append(String.valueOf(part));
        }
        uriBuilder.setPath(stringWriter.toString());
        return uriBuilder;
    }






    public static MigrationPlan getLoginInfo(HttpServletRequest request) {
        MigrationPlan customer = new MigrationPlan();
        return customer;
    }







    
}
