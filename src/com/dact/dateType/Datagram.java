package com.dact.dateType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.util.Sqlserver;
import com.dact.util.LogWrite;
import com.dact.util.PackageProcessor;
import com.dact.util.RateUtil;

public class Datagram {
	public void excuteDatagram(PackageProcessor p, BaseInfo base, LogWrite logWrite) {
		Sqlserver dBtool = new Sqlserver();
		RateUtil rateUtil = new RateUtil();
		String wia_longaddress, wia_shortaddress, deviceType, shuiInfo, hartaddress = "";
		int interval = 0, serial;
		float shuiliuliang, dianya, firstvalue, secondvalue, thirdvalue, fourthvalue = 0;

		wia_shortaddress = p.bytesToString(2, 3);
		wia_longaddress = MapInfo.addressmap.get(wia_shortaddress + " " + base.getIpaddress());
		deviceType = MapInfo.typemap.get(wia_longaddress);
		/* 当前数据报文的序列号 */
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
					logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
				}
			} else if (p.bytesToString(10, 10).equals("02")) {
				logWrite.write("水表");

				shuiInfo = MapInfo.shui_map.get(wia_longaddress);
				shuiliuliang = p.bytesToFloat(11, 14);
				logWrite.write("数据：" + shuiInfo + "=" + shuiliuliang);
				String sente = "insert into [collect_data](typeserial, value,reachtime)values('" + shuiInfo + "'," + shuiliuliang + ",getdate())";
				logWrite.write("向数据库表collect_data中添加一条数据：" + shuiInfo + "=" + shuiliuliang);
				try {
					logWrite.write("执行sql：" + sente);
					dBtool.executeUpdate(sente);
				} catch (SQLException e) {
					logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
				}
			} else if (p.bytesToString(10, 10).equals("00")) {
				logWrite.write("压力表");
				if (p.bytesToString(18, 19).equals("0000")) {
					logWrite.write("常规数据");
					shuiInfo = MapInfo.shui_map.get(wia_longaddress);
					shuiliuliang = p.bytesToFloat(26, 29);
					logWrite.write("数据：" + shuiInfo + "=" + shuiliuliang);
					String sente = "insert into [collect_data](typeserial, value,reachtime)values('" + shuiInfo + "'," + shuiliuliang + ",getdate())";
					logWrite.write("向数据库表collect_data中添加一条数据：" + shuiInfo + "=" + shuiliuliang);
					try {
						logWrite.write("执行sql：" + sente);
						dBtool.executeUpdate(sente);
					} catch (SQLException e) {
						logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
					}
				}
			} else if (p.bytesToString(10, 10).equals("04")) {
				logWrite.write("无线IO变送器数据");

				shuiInfo = MapInfo.shui_map.get(wia_longaddress);
				shuiliuliang = p.bytesToFloat(11, 14);
				logWrite.write("数据：" + shuiInfo + "=" + shuiliuliang);
				String sente = "insert into [collect_data](typeserial, value,reachtime)values('" + shuiInfo + "'," + shuiliuliang + ",getdate())";
				logWrite.write("向数据库表collect_data中添加一条数据：" + shuiInfo + "=" + shuiliuliang);
				try {
					logWrite.write("执行sql：" + sente);
					dBtool.executeUpdate(sente);
				} catch (SQLException e) {
					logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
				}
			}
		}

		else if (p.bytesToString(8, 9).equals("5a00")) {

			if (p.bytesToString(10, 10).equals("06")) {
				logWrite.write("06数据 ");
				shuiInfo = MapInfo.shui_map.get(wia_longaddress);
				shuiliuliang = p.bytesToFloat(11, 14);
				logWrite.write("数据：" + shuiInfo + "=" + shuiliuliang);
				String sente = "insert into [collect_data](typeserial, value,reachtime)values('" + shuiInfo + "'," + shuiliuliang + ",getdate())";
				logWrite.write("向数据库表collect_data中添加一条数据：" + shuiInfo + "=" + shuiliuliang);
				try {
					logWrite.write("执行sql：" + sente);
					dBtool.executeUpdate(sente);
				} catch (SQLException e) {
					logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
				}
			} else if (p.bytesToString(10, 10).equals("08")) {
				logWrite.write("08数据 ");
				shuiInfo = MapInfo.shui_map.get(wia_longaddress);
				shuiliuliang = p.bytesToFloat(11, 14);
				logWrite.write("数据：" + shuiInfo + "=" + shuiliuliang);
				String sente = "insert into [collect_data](typeserial, value,reachtime)values('" + shuiInfo + "'," + shuiliuliang + ",getdate())";
				logWrite.write("向数据库表collect_data中添加一条数据：" + shuiInfo + "=" + shuiliuliang);
				try {
					logWrite.write("执行sql：" + sente);
					dBtool.executeUpdate(sente);
				} catch (SQLException e) {
					logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
				}

			} else if (p.bytesToString(10, 10).equals("12")) {
				logWrite.write("12数据 ");
				shuiInfo = MapInfo.shui_map.get(wia_longaddress);
				shuiliuliang = p.bytesToFloat(11, 14);
				logWrite.write("数据：" + shuiInfo + "=" + shuiliuliang);
				String sente = "insert into [collect_data](typeserial, value,reachtime)values('" + shuiInfo + "'," + shuiliuliang + ",getdate())";
				logWrite.write("向数据库表collect_data中添加一条数据：" + shuiInfo + "=" + shuiliuliang);
				try {
					logWrite.write("执行sql：" + sente);
					dBtool.executeUpdate(sente);
				} catch (SQLException e) {
					logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
				}

			}

		} else if (p.bytesToString(8, 9).equals("5a01")) {

			if (p.bytesToString(10, 10).equals("00")) {
				logWrite.write("00数据 ");
				shuiInfo = MapInfo.shui_map.get(wia_longaddress);
				shuiliuliang = p.bytesToFloat(11, 14);
				logWrite.write("数据：" + shuiInfo + "=" + shuiliuliang);
				String sente = "insert into [collect_data](typeserial, value,reachtime)values('" + shuiInfo + "'," + shuiliuliang + ",getdate())";
				logWrite.write("向数据库表collect_data中添加一条数据：" + shuiInfo + "=" + shuiliuliang);
				try {
					logWrite.write("执行sql：" + sente);
					dBtool.executeUpdate(sente);
				} catch (SQLException e) {
					logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
				}
			}

		}
		try {
			dBtool.free();
		} catch (SQLException e) {
			logWrite.write("【 Error!】Datagram.excuteDatagram.23：" + e.getMessage());
		}
	}
}
