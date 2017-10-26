package com.dact.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

/**
 * @author Pnoker
 * @description 线程池工具类
 */
public class PoolUtils {
    private int checkedOut;
    private Vector freeConnections = new Vector();
    private int maxConn;
    private int normalConn;
    private String password;
    private String url;
    private String user;
    /**
     * 空闲的连接数
     */
    private static int num = 0;
    /**
     * 当前的连接数
     */
    private static int numActive = 0;

    public PoolUtils(String password, String url, String user, int normalConn, int maxConn) {
        this.password = password;
        this.url = url;
        this.user = user;
        this.maxConn = maxConn;
        this.normalConn = normalConn;

        //初始normalConn个连接
        for (int i = 0; i < normalConn; i++) {
            Connection c = newConnection();
            if (c != null) {
                freeConnections.addElement(c);
                num++;
            }
        }
    }

    /**
     * 释放不用的连接到连接池
     *
     * @param con
     */
    public synchronized void freeConnection(Connection con) {
        freeConnections.addElement(con);
        num++;
        checkedOut--;
        numActive--;
        notifyAll();
    }

    /**
     * 获取一个可用连接
     *
     * @return
     */
    public synchronized Connection getConnection() {
        Connection con = null;
        //还有空闲的连接
        if (freeConnections.size() > 0) {
            num--;
            con = (Connection) freeConnections.firstElement();
            freeConnections.removeElementAt(0);
            try {
                if (con.isClosed()) {
                    con = getConnection();
                }
            } catch (SQLException e) {
                con = getConnection();
            }
            //没有空闲连接且当前连接小于最大允许值,最大值为0则不限制
        } else if (maxConn == 0 || checkedOut < maxConn) {
            con = newConnection();
        }
        //当前连接数加1
        if (con != null) {
            checkedOut++;
        }
        numActive++;
        return con;
    }

    /**
     * 获取一个连接,并加上等待时间限制,时间为毫秒
     *
     * @param timeout
     * @return
     */
    public synchronized Connection getConnection(long timeout) {
        long startTime = System.currentTimeMillis();
        Connection con;
        while ((con = getConnection()) == null) {
            try {
                wait(timeout);
            } catch (InterruptedException e) {
            }
            if ((System.currentTimeMillis() - startTime) >= timeout) {
                //超时返回
                return null;
            }
        }
        return con;
    }

    /**
     * 关闭所有连接
     */
    public synchronized void release() {
        Enumeration allConnections = freeConnections.elements();
        while (allConnections.hasMoreElements()) {
            Connection con = (Connection) allConnections.nextElement();
            try {
                con.close();
                num--;
            } catch (SQLException e) {
            }
        }
        freeConnections.removeAllElements();
        numActive = 0;
    }

    /**
     * 创建一个新连接
     *
     * @return
     */
    private Connection newConnection() {
        Connection con = null;
        try {
            //用户,密码都为空
            if (user == null) {
                con = DriverManager.getConnection(url);
            } else {
                con = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            return null;
        }
        return con;
    }

    /**
     * 返回当前空闲连接数
     *
     * @return
     */
    public int getNum() {
        return num;
    }

    /**
     * 返回当前连接数
     *
     * @return
     */
    public int getNumActive() {
        return numActive;
    }
}
