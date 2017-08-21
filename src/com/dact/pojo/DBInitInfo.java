package com.dact.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * 初始化，模拟加载所有的配置文件
 *
 * @author Ran
 */
public class DBInitInfo {
    public static List<DBbean> beans = null;

    static {
        beans = new ArrayList<DBbean>();
        // 这里数据 可以从xml 等配置文件进行获取  
        // 为了测试，这里我直接写死  
        DBbean dBbean = new DBbean();
        dBbean.setDriverName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dBbean.setUrl("jdbc:sqlserver://localhost:1433;databaseName=anqingcollect");
        dBbean.setUserName("sa");
        dBbean.setPassword("yangfan");

        dBbean.setMinConnections(0);
        dBbean.setMaxConnections(20);

        dBbean.setPoolName("testPool");
        beans.add(dBbean);
    }
} 