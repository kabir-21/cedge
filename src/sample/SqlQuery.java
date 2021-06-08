package sample;

import java.sql.*;

public class SqlQuery {
    private String query;

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public ResultSet sql() throws ClassNotFoundException, SQLException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","data123");
        Statement st = con.createStatement();
        return st.executeQuery(this.query);
    }
}
