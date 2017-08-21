package com.dact.pool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Pool {

    static private Pool instance = null; //定义唯一实例

    private int maxConnect = 20;//最大连接数
    private int normalConnect = 0;//保持连接数
    private String password = "";//密码
    private String url = "";//连接URL
    private String user = "";//用户名
    private String driverName = "";//驱动类

    Driver driver = null;//驱动变量
    PoolUtils pool = null;//连接池实例变量

    //返回唯一实例
    static synchronized public Pool getInstance(String dbName, String userName, String passWord, String port, String urll, String dbtype) {
        if (instance == null) {
            instance = new Pool(dbName, userName, passWord, port, urll, dbtype);
        }
        return instance;
    }

    //将构造函数私有,不允许外界访问
    private Pool(String dbName, String userName, String passWord, String port, String urll, String dbtype) {
        user = userName;
        password = passWord;
        if (dbtype.equalsIgnoreCase("sqlserver")) {//sqlserver
            url = "jdbc:sqlserver://" + urll + ":" + port + ";DatabaseName=" + dbName + "";
            driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            loadDrivers(driverName);
            createPool();
        } else if (dbtype.equalsIgnoreCase("oracle")) {//oracle
            url = "jdbc:oracle:thin:@" + urll + ":" + port + ":" + dbName;
            driverName = "oracle.jdbc.driver.OracleDriver";
            loadDrivers(driverName);
            createPool();
        } else if (dbtype.equalsIgnoreCase("mysql")) {//mysql
            url = "jdbc:mysql://" + urll + ":" + port + "/" + dbName;
            driverName = "com.mysql.jdbc.Driver";
            loadDrivers(driverName);
            createPool();
        }
    }

    //装载和注册所有JDBC驱动程序
    private void loadDrivers(String dri) {

        String driverClassName = dri;
        try {
            driver = (Driver) Class.forName(driverClassName).newInstance();
            DriverManager.registerDriver(driver);
            System.out.println("成功注册JDBC驱动程序" + driverClassName);
        } catch (Exception e) {
            System.out.println("无法注册JDBC驱动程序:" + driverClassName + ",错误:" + e);
        }
    }

    //创建连接池
    private void createPool() {
        pool = new PoolUtils(password, url, user, normalConnect, maxConnect);
        if (pool != null) {
            System.out.println("创建连接池成功");
        } else {
            System.out.println("创建连接池失败");
        }
    }

    //获得一个可用的连接,如果没有则创建一个连接,且小于最大连接限制
    public Connection getConnection() {
        if (pool != null) {
            return pool.getConnection();
        }
        return null;
    }

    //获得一个连接,有时间限制
    public Connection getConnection(long time) {
        if (pool != null) {
            return pool.getConnection(time);
        }
        return null;
    }

    //将连接对象返回给连接池
    public void freeConnection(Connection con) {
        if (pool != null) {
            pool.freeConnection(con);
        }
    }

    //返回当前空闲连接数
    public int getnum() {
        return pool.getnum();
    }


    //返回当前连接数
    public int getnumActive() {
        return pool.getnumActive();
    }


    //关闭所有连接,撤销驱动注册
    public synchronized void release() {

        ///关闭连接
        pool.release();

        ///撤销驱动
        try {
            DriverManager.deregisterDriver(driver);
            System.out.println("撤销JDBC驱动程序 " + driver.getClass().getName());
        } catch (SQLException e) {
            System.out.println("无法撤销JDBC驱动程序的注册:" + driver.getClass().getName());
        }

    }

}
