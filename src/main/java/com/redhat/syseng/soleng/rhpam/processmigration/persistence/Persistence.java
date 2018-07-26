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
                if (INSTANCE == null) {
                    INSTANCE = new Persistence();
                }
            }
        }
        return INSTANCE;
    }
    
    private void initialCreateTables() {
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(SQLITE_DB_URL);
            logger.info("Opened database successfully");

            stmt = connection.createStatement();
            stmt.setQueryTimeout(30);  // set timeout to 30 sec.

            //create three tables needed.
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS MIGRATION_PLAN_TABLE (plan_id integer primary key autoincrement,  migration_plan TEXT);");
            logger.info("created all needed tables successfully");
        } catch (ClassNotFoundException | SQLException e) {
            logger.info(e.getClass().getName() + ": " + e.getMessage());
            throw new IllegalStateException(e);
        }
    }    


    public int addMigrationPlan(Object migrationPlan) {

        Connection connection = null;
        Statement stmt = null;
        int planId = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLITE_DB_URL);
            //connection.setAutoCommit(false);
            //logger.info("Opened database successfully");

            stmt = connection.createStatement();
            stmt.setQueryTimeout(30);  // set timeout to 30 sec.

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS MIGRATION_PLAN_TABLE (plan_id integer primary key autoincrement,  migration_plan TEXT);");
            //String sqlString = "insert into MIGRATION_PLAN_TABLE (migration_plan) values(\"" + migrationPlan + "\");";
            String sqlString = "insert into MIGRATION_PLAN_TABLE values(null, \"" + migrationPlan + "\");";
            logger.info("sqlString: " + sqlString);
            stmt.executeUpdate(sqlString);
            sqlString = "SELECT last_insert_rowid() AS planId;";
            ResultSet rs = stmt.executeQuery(sqlString);
            planId = Integer.parseInt(rs.getString("planId"));
            logger.info("new planId: " + planId);
            
            //connection.commit();
            stmt.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            logger.info(e.getClass().getName() + ": " + e.getMessage());
            throw new IllegalStateException(e);
        }
        //logger.info("persistBindingInfo: planId" + planId + " migrationPlan: " + migrationPlan);
        return planId;
    }

    public void deleteMigrationPlan(String planId) {

        Connection connection = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLITE_DB_URL);
            //connection.setAutoCommit(false);
            //logger.info("Opened database successfully");

            stmt = connection.createStatement();
            stmt.setQueryTimeout(30);  // set timeout to 30 sec.

            String sqlString = "delete from MIGRATION_PLAN_TABLE where plan_id= \"" + planId + "\";";
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
        logger.info("deleteMigrationPlan " + planId);
    }
    
    public void updateMigrationPlan(String planId, Object migrationPlan) {

        Connection connection = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLITE_DB_URL);
            //connection.setAutoCommit(false);
            //logger.info("Opened database successfully");

            stmt = connection.createStatement();
            stmt.setQueryTimeout(30);  // set timeout to 30 sec.

            String sqlString = "update MIGRATION_PLAN_TABLE set migration_plan = \"" + migrationPlan + "\" where plan_id= \"" + planId + "\";";
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
        logger.info("updateMigrationPlan " + planId);
    }    

    public String retrieveMigrationPlan(String planId) {
        Connection connection = null;
        Statement stmt = null;
        String result = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(SQLITE_DB_URL);
            //logger.info("Opened database successfully");

            stmt = connection.createStatement();
            stmt.setQueryTimeout(30);  // set timeout to 30 sec.

            String sqlString = "SELECT migration_plan FROM MIGRATION_PLAN_TABLE where plan_id = \"" + planId + "\";";
            //logger.info("select string: " + sqlString);

            ResultSet rs = stmt.executeQuery(sqlString);

            while (rs.next()) {
                result = rs.getString("migration_plan");
                //logger.info("migration_plan = " + result);
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            logger.info(e.getClass().getName() + ": " + e.getMessage());
            throw new IllegalStateException(e);
        }
        //logger.info("retrieveBindingInfo: " + planId);
        return result;

    }
}
