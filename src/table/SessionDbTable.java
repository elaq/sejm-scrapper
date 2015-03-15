package table;

import db.DbConnector;

import java.sql.SQLException;


public class SessionDbTable implements DbTable {

    static final String SESSION_TABLE_NAME = "SessionData";
    private DbConnector dbConnector;

    public SessionDbTable(DbConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    public void createTable() throws SQLException {
        String sessionDbColumns =  "(term INTEGER, " + " session_id INTEGER, " + " voting_id INTEGER, " + " date_hour VARCHAR(255), " +
                " topic VARCHAR, " + "subtopic VARCHAR, " + " PRIMARY KEY (term, session_id, voting_id))";
        dbConnector.createTable(SESSION_TABLE_NAME, sessionDbColumns);
    }

    public void insertRow(String data) throws SQLException {
        dbConnector.insertRow(data, SESSION_TABLE_NAME);
    }

}
