package com.p3solutions.jdbc_parallel_connection_check.connection;

import java.sql.Connection;
import java.sql.DriverManager;

import com.p3solutions.jdbc_parallel_connection_check.beans.InputBean;

public class JDBCConnector {
    public Connection connection = null;

    public JDBCConnector(InputBean inputArgs) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlserver://" + inputArgs.getHost() + ":" + inputArgs.getPort() + ";database=" + inputArgs.getDbName() + "", inputArgs.getUserName(), inputArgs.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getClassName() {
        return JDBCConnector.class.getName();
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

}
