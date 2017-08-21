package com.dact.dateType;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.pool.Pool;
import com.dact.util.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * @author Pnoker
 * @Description 网络报文，更新拓扑图
 */
public class NetDatagram {

    private Pool pool = Pool.getInstance("anqingcollect", "sa", "yangfan", "1433", "127.0.0.1", "sqlserver");

    private String networkinfo = "";

    public String getNetworkinfo() {
        return networkinfo;
    }

    public void setNetworkinfo(String networkinfo) {
        this.networkinfo = networkinfo;
    }

    /**
     * 去重操作，将重复的长地址去掉
     */
    public String noRepeat(String repeat) {
        String noRepeat = "";
        String[] repeats = repeat.split(",");
        ArrayList<String> list = new ArrayList<String>();
        for (int m = 0; m < repeats.length; m++) {
            if (!list.contains(repeats[m])) {
                list.add(repeats[m]);
                noRepeat += repeats[m] + ",";
            }
        }
        return noRepeat;
    }

    /**
     * 处理网络报文，01 01
     * 为该网关的网络报文，网络报文包含节点的长地址、短地址、标签、设备类型、数据率、上下行GraphID、邻居,网络报文携带了长地址和短地址的对应关系，
     * 由于数据报文只有短地址，于是只能通过网络报文提供的对应关系确定设备
     */
    public void excuteNetDatagram(PackageProcessor p, BaseInfo base, String networkinfo, LogWrite logWrite) {
        Connection conn = pool.getConnection(10000);
        PrintUtil printUtil = new PrintUtil();
        String wia_longaddress = "", wia_shortaddress = "", deviceType = "";
        wia_longaddress = p.bytesToString(2, 9);
        wia_shortaddress = p.bytesToString(10, 11);

        logWrite.write("长地址：" + wia_longaddress);
        logWrite.write("短地址：" + wia_shortaddress);

        int neighbor = Integer.parseInt(p.bytesToString(22, 22));
        deviceType = p.bytesToString(23 + neighbor * 2, 24 + neighbor * 2);

        logWrite.write("邻居个数：" + neighbor);
        logWrite.write("设备类型：" + deviceType);


        MapInfo.addressmap.put(wia_shortaddress + " " + base.getIpaddress(), wia_longaddress);
        MapInfo.typemap.put(wia_longaddress, deviceType);

        if (!((wia_shortaddress.equals("0100")) || (wia_shortaddress.equals("0000")) || (wia_longaddress.equals("007a410000000a7e")) || (wia_longaddress.equals("007a4100000025e0")))) {
            this.networkinfo = networkinfo + wia_longaddress + ",";
            this.networkinfo = noRepeat(this.networkinfo);
        }
        String sente = "update [Network_tuopu] set manydevices = '" + this.networkinfo + "' where ipaddress = '" + base.getIpaddress() + "'";
        logWrite.write("更新网关的网络拓扑信息为：" + this.networkinfo);
        try {
            logWrite.write("执行sql：" + sente);
            printUtil.printDetail(base.getIpaddress(), "执行sql：" + sente);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sente);
            pool.freeConnection(conn);
        } catch (SQLException e) {
            logWrite.write("【 Error!】NetDatagram.excuteNetDatagram：" + e.getMessage());
        }
    }
}
