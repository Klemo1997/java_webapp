package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbHandler {

    public Connection connection;
    public Statement st;

    public DbHandler() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ubp_db","root","JumpUpAndDown");
        st = connection.createStatement();
    }
}
