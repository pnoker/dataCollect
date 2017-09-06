package com.dact.dateType;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.util.LogWrite;
import com.dact.util.MySQLUtils;
import com.dact.util.PackageProcessor;

import java.sql.SQLException;

public class Datagram {
    public void excuteDatagram(PackageProcessor p, BaseInfo base, LogWrite logWrite) {
        MySQLUtils dBtool = new MySQLUtils();
        String wia_longaddress, wia_shortaddress, deviceType, shuiInfo;
        int interval = 0, serial;
        float shuiliuliang, dianya, firstvalue, secondvalue, thirdvalue, fourthvalue = 0;

        wia_shortaddress = p.bytesToString(2, 3);
        wia_longaddress = MapInfo.addressmap.get(wia_shortaddress + " " + base.getIpaddress());
        deviceType = MapInfo.typemap.get(wia_longaddress);

        //当前数据报文的序列号
        serial = p.bytesToIntSmall(4, 7);

        logWrite.write("长地址：" + wia_longaddress);
        logWrite.write("短地址：" + wia_shortaddress);

        if (p.bytesToString(8, 9).equals("7400")) {
            if (p.bytesToString(10, 10).equals("01")) {
                logWrite.write("水表");

                shuiInfo = MapInfo.shui_map.get(wia_longaddress);
                shuiliuliang = p.bytesToFloat(11, 14);
                logWrite.write("数据：" + shuiInfo + "=" + shuiliuliang);
                String sente = "insert into [collect_data](typeserial,value,reachtime)values('" + shuiInfo + "'," + shuiliuliang + ",getdate())";
                logWrite.write("向数据库表collect_data中添加一条数据：" + shuiInfo + "=" + shuiliuliang);
                try {
                    logWrite.write("执行sql：" + sente);
                    dBtool.executeUpdate(sente);
                } catch (SQLException e) {
                    logWrite.write(e.getMessage());
                }
            }
        }
        try {
            dBtool.free();
        } catch (SQLException e) {
            logWrite.write(e.getMessage());
        }
    }
}
