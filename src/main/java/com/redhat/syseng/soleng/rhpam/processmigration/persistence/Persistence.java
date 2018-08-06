package com.redhat.syseng.soleng.rhpam.processmigration.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class Persistence {

    private static final String SQLITE_DB_URL = "jdbc:sqlite:/var/lib/sqlite/data/persistency.db";

    private static final Logger logger = Logger.getLogger(Persistence.class.getName());

    private static Persistence INSTANCE;


    private Persistence() {
        initialCreateTables();        
    }

    public static Persistence getInstance() {
        if (INSTANCE == null) {
            synchronized (Persistence.class) {
                    INSTANCE = new Persistence();
            }
        }
        return INSTANCE;
    }
    
    private void initialCreateTables() {
        Connection connection;
        Statement stmt;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLITE_DB_URL);
            logger.info("Opened database successfully");

            stmt = connection.createStatement();
            stmt.setQueryTimeout(30);  // set timeout to 30 sec.

            //create three tables needed.
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS PLAN_TABLE (plan_id integer primary key autoincrement,  migration_plan TEXT);");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS MIGRATION_TABLE (migration_id integer primary key autoincrement,  plan_id integer, submit_time TEXT);");
            
            stmt.close();
            connection.close();            
        } catch (ClassNotFoundException | SQLException e) {
            logger.info(e.getClass().getName() + ": " + e.getMessage());
            throw new IllegalStateException(e);
        }
            logger.info("created all needed tables successfully");
    }    


    public int addPlan(String migrationPlan) {
        Connection connection;
        Statement stmt;
        int planId = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLITE_DB_URL);
            //connection.setAutoCommit(false);
            //logger.info("Opened database successfully");

            stmt = connection.createStatement();
            stmt.setQueryTimeout(30);  // set timeout to 30 sec.

            String sqlString = "insert into PLAN_TABLE values(null, \"" + migrationPlan + "\");";
            logger.info("sqlString: " + sqlString);
            stmt.executeUpdate(sqlString);
            sqlString = "SELECT last_insert_rowid() AS planId;";
            ResultSet rs = stmt.executeQuery(sqlString);
            planId = Integer.parseInt(rs.getString("planId"));
            //logger.info("new planId: " + planId);
            
            //connection.commit();
            stmt.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            logger.info(e.getClass().getName() + ": " + e.getMessage());
            throw new IllegalStateException(e);
        }
        //logger.info("addPlan: " + planId + " migrationPlan: " + migrationPlan);
        return planId;
    }
    
    public int addMigration(String planId) {
        Connection connection;
        Statement stmt;
        int migrationId = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLITE_DB_URL);
            //connection.setAutoCommit(false);
            //logger.info("Opened database successfully");

            stmt = connection.createStatement();
            stmt.setQueryTimeout(30);  // set timeout to 30 sec.

            String sqlString = "insert into MIGRATION_TABLE values(null, \"" + planId + "\", DATETIME('now'));";
            //logger.info("sqlString: " + sqlString);
            stmt.executeUpdate(sqlString);
            sqlString = "SELECT last_insert_rowid() AS migration_id;";
            ResultSet rs = stmt.executeQuery(sqlString);
            migrationId = Integer.parseInt(rs.getString("migration_id"));
            //logger.info("new migrationId: " + migrationId);
            
            //connection.commit();
            stmt.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            logger.info(e.getClass().getName() + ": " + e.getMessage());
            throw new IllegalStateException(e);
        }
        return migrationId;
    }    

    public void deletePlan(String planId) {
        Connection connection;
        Statement stmt;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLITE_DB_URL);
            //connection.setAutoCommit(false);
            //logger.info("Opened database successfully");

            stmt = connection.createStatement();
            stmt.setQueryTimeout(30);  // set timeout to 30 sec.

            String sqlString = "delete from PLAN_TABLE where plan_id= \"" + planId + "\";";
            //logger.info("delete string: " + sqlString);            
            logger.info("delete string: " + sqlString);
            stmt.executeUpdate(sqlString);
            //connection.commit();
            stmt.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            logger.info(e.getClass().getName() + ": " + e.getMessage());
            throw new IllegalStateException("sqlite class is not found, could be classpath issue: " + e);
        } catch (SQLException e) {
            //no need to further throw exception here, it could be between tests the database info might be deleted already.
            logger.info(e.getClass().getName() + ": " + e.getMessage());
        }
        logger.info("deletePlan " + planId);
    }
    
    public void deleteMigration(int migrationId) {
        Connection connection;
        Statement stmt;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLITE_DB_URL);
            //connection.setAutoCommit(false);
            //logger.info("Opened database successfully");

            stmt = connection.createStatement();
            stmt.setQueryTimeout(30);  // set timeout to 30 sec.

            String sqlString = "delete from MIGRATION_TABLE where migration_id= \"" + migrationId + "\";";
            //logger.info("delete string: " + sqlString);
            stmt.executeUpdate(sqlString);
            //connection.commit();
            stmt.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            logger.info(e.getClass().getName() + ": " + e.getMessage());
            throw new IllegalStateException("sqlite class is not found, could be classpath issue: " + e);
        } catch (SQLException e) {
            //no need to further throw exception here, it could be between tests the database info might be deleted already.
            logger.info(e.getClass().getName() + ": " + e.getMessage());
        }
        logger.info("deleteMigration " + migrationId);
    }
    
    
    
    public void updatePlan(String planId, Object migrationPlan) {
        Connection connection;
        Statement stmt;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLITE_DB_URL);
            //connection.setAutoCommit(false);
            //logger.info("Opened database successfully");

            stmt = connection.createStatement();
            stmt.setQueryTimeout(30);  // set timeout to 30 sec.

            String sqlString = "update PLAN_TABLE set migration_plan = \"" + migrationPlan + "\" where plan_id= \"" + planId + "\";";
            logger.info("update string: " + sqlString);
            stmt.executeUpdate(sqlString);
            //connection.commit();
            stmt.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            logger.info(e.getClass().getName() + ": " + e.getMessage());
            throw new IllegalStateException("sqlite class is not found, could be classpath issue: " + e);
        } catch (SQLException e) {
            //no need to further throw exception here, it could be between tests the database info might be deleted already.
            logger.info(e.getClass().getName() + ": " + e.getMessage());
        }
        logger.info("updatePlan " + planId);
    }    
    
    public void updateMigration(String migrationId, String planId) {
        Connection connection;
        Statement stmt;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLITE_DB_URL);
            //connection.setAutoCommit(false);
            //logger.info("Opened database successfully");

            stmt = connection.createStatement();
            stmt.setQueryTimeout(30);  // set timeout to 30 sec.

            String sqlString = "update MIGRATION_TABLE set plan_id = \"" + planId + "\" where migration_id= \"" + migrationId + "\";";
            logger.info("update string: " + sqlString);
            stmt.executeUpdate(sqlString);
            //connection.commit();
            stmt.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            logger.info(e.getClass().getName() + ": " + e.getMessage());
            throw new IllegalStateException("sqlite class is not found, could be classpath issue: " + e);
        } catch (SQLException e) {
            //no need to further throw exception here, it could be between tests the database info might be deleted already.
            logger.info(e.getClass().getName() + ": " + e.getMessage());
        }
        logger.info("updateMigration " + planId);
    }     

    public String retrievePlan(String planId) {
        Connection connection;
        Statement stmt;
        String result = "";
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLITE_DB_URL);
            //logger.info("Opened database successfully");

            stmt = connection.createStatement();
            stmt.setQueryTimeout(30);  // set timeout to 30 sec.

            String sqlString = "SELECT * FROM PLAN_TABLE";
            if (null != planId){
                sqlString = "SELECT * FROM PLAN_TABLE where plan_id = \"" + planId + "\";";
            }
            
            logger.info("select string: " + sqlString);

            ResultSet rs = stmt.executeQuery(sqlString);

            while (rs.next()) {
                planId = rs.getString("plan_id");
                String migrationPlan = rs.getString("migration_plan");
                migrationPlan = migrationPlan.replaceAll("&quote;","\"");
                String tmpStr = "{\"planId\":\"" + planId + "\"," 
                        + "\"migrationPlan\":" + migrationPlan + "}";
                if (result == ""){
                    result = tmpStr;
                }else{
                    result = result + "," + tmpStr;
                }
            }            
            rs.close();
            stmt.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            logger.info(e.getClass().getName() + ": " + e.getMessage());
            throw new IllegalStateException(e);
        }
        //logger.info("retrievePlan: " + result);
        return result;

    }
    
    public String retrieveMigration(String migrationId) {
        Connection connection;
        Statement stmt;
        String result = "";
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLITE_DB_URL);
            //logger.info("Opened database successfully");

            stmt = connection.createStatement();
            stmt.setQueryTimeout(30);  // set timeout to 30 sec.

            String sqlString = "SELECT * FROM MIGRATION_TABLE";
            if (null != migrationId){
                sqlString = "SELECT * FROM MIGRATION_TABLE where migration_id = \"" + migrationId + "\";";
            }

            //logger.info("select string: " + sqlString);

            ResultSet rs = stmt.executeQuery(sqlString);

            while (rs.next()) {
                String tmpId = rs.getString("migration_id");
                String planId = rs.getString("plan_id");
                String submitTime = rs.getString("submit_time");
                String tmpStr = "{\"migrationId\":\"" + tmpId + "\"," 
                        + "\"planId\":\"" + planId + "\"," 
                        + "\"submitTime\":\"" + submitTime + "\"}";
                if (result == ""){
                    result = tmpStr;
                }else{
                    result = result + "," + tmpStr;
                }
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            logger.info(e.getClass().getName() + ": " + e.getMessage());
            throw new IllegalStateException(e);
        }
        //logger.info("retrieveMigration: " + result);
        return result;

    }    
}
