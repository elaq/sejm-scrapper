package db;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DbConnector {

    private static Logger log = Logger.getLogger(DbConnector.class);

    static final String JDBC_DRIVER = "org.sqlite.JDBC";
    String dbName, dbUrl;

    public DbConnector(String dbName) {
        this.dbName = dbName;
        this.dbUrl = "jdbc:sqlite:" + dbName + ".db";
    }

    public void createTable(String tableName, String dbColumns) throws SQLException {
        Connection conn = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(dbUrl);
            Statement stmt = conn.createStatement();
            log.info("Creating table " + tableName + " in database " + dbName + "...");
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + dbColumns;
            stmt.executeUpdate(sql);
            log.info("Executing SQL: " + sql);
            log.info("Table " + tableName + " created successfully!");
        } catch (SQLException e) {
            log.info(e);
        } catch (ClassNotFoundException e) {
            log.info(e);
        } finally {
            if (conn != null)
                conn.close();
        }
    }


    public void insertRow(String data, String tableName) throws SQLException {
        Connection conn = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(dbUrl);
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO " + tableName + " VALUES(" + data + ")";
            log.info ("Executing SQL: " + sql);
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            log.info(e);
        } catch (ClassNotFoundException e) {
            log.info(e);
        } finally {
            if (conn != null)
                conn.close();
        }
    }

}
