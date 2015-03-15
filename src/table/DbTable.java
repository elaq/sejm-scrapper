package table;

import java.sql.SQLException;


public interface DbTable {

    public void createTable() throws SQLException;

    public void insertRow(String data) throws SQLException;

}
