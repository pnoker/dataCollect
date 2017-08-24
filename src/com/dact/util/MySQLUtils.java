package com.dact.util;

import java.sql.*;

public class MySQLUtils {
    private Connection connection = null;
    public Statement statement = null;
    private ResultSet result = null;

    public MySQLUtils() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/bl_data_collect?useUnicode=true&characterEncoding=utf-8";
            String user = "root";
            String password = "123456";
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        try {
            result = statement.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public int executeUpdate(String sql) throws SQLException {
        int updatenum = 0;
        try {
            updatenum = statement.executeUpdate(sql);
            return updatenum;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return updatenum;
    }

    public void free() throws SQLException {
        try {
            if (result != null)
                result.close();
            if (statement != null)
                statement.close();
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
