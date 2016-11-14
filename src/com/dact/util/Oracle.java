package com.dact.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Oracle {
	public static String user = "aqsh";
	public static String pwd = "aqsh";
	
	private Connection connection = null;
	public Statement statement = null;
	private ResultSet result = null;

	public Oracle() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@221.180.150.131:8008:xywdb11";
			connection = DriverManager.getConnection(url,user,pwd);
			statement = connection.createStatement();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		} catch (ClassNotFoundException ex) {
			System.out.println(ex.getMessage());
		}
	}

	public ResultSet executeQuery(String sql) throws SQLException {
		try {
			result = statement.executeQuery(sql);
		} catch (SQLException se) {
			System.out.println("ERROR:" + se.getMessage());
		}
		return result;
	}

	public int executeUpdate(String sql) throws SQLException {
		int updatenum = 0;
		try {
			updatenum = statement.executeUpdate(sql);
			return updatenum;
		} catch (SQLException se) {
			System.out.println("ERROR:" + se.getMessage());
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
		} catch (SQLException se) {
			System.out.println("ERROR:" + se.getMessage());
		}
	}
	public static void main(String[] args) {
		Oracle oracle = new Oracle();
		
	}

}
