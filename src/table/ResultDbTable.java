package table;

import db.DbConnector;

import java.sql.SQLException;


public class ResultDbTable implements DbTable {

    static final String RESULT_TABLE_NAME = "VotingData";
    DbConnector dbConnector;

    public ResultDbTable(DbConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    public void createTable() throws SQLException {
        String resultDbColumns = "(term INTEGER, " + " session_id INTEGER, " + " voting_id INTEGER, " + " club VARCHAR(255), " +
                " members INTEGER, " + " voted INTEGER, " + " voted_for INTEGER, " + " voted_against INTEGER, " +
                " voted_empty INTEGER, " + " not_voted INTEGER, " + " PRIMARY KEY (term, session_id, voting_id, club))";
        dbConnector.createTable(RESULT_TABLE_NAME, resultDbColumns);
    }

    public void insertRow(String data) throws SQLException {
        dbConnector.insertRow(data, RESULT_TABLE_NAME);
    }

}
