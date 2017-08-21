package com.dact.pool;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Test {
    public static void main(String[] args) {
        Pool pool = Pool.getInstance("anqingcollect", "sa", "yangfan", "1433", "127.0.0.1", "sqlserver");
        Connection conn = pool.getConnection(2000);
        if (conn != null) {
            System.out.println("获取了一个连接。。。。。。。");
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM shui_opc ");
                System.out.println("1111111111111111");
                while (rs.next()) {
                    System.out.println("rs.nest====" + rs.getString(1));
                    System.out.println("rs.nest====" + rs.getString(2));
                    System.out.println("rs.nest====" + rs.getString(3));

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pool.freeConnection(conn);
            }
        }


    }
}