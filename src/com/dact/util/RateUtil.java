package com.dact.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.dact.pojo.MapInfo;

public class RateUtil {
	public void rate(String wia_shortaddress, String wia_longaddress, String ipAddress, int serial, LogWrite logWrite) {

		int lose = 0, num = 0;
		float rate = 0;
		if (MapInfo.serial.get(wia_shortaddress + " " + ipAddress) != null) {
			int last_serial = MapInfo.serial.get(wia_shortaddress + " " + ipAddress);
			int now_serial = serial;
			/* 当前序列号和上一次一样，数据包发送重复 */
			if (now_serial == last_serial) {
				logWrite.write("<-----本次接收数据报文同上次重复……");
				if (MapInfo.number.get(wia_shortaddress + " " + ipAddress) != null) {
					num = MapInfo.number.get(wia_shortaddress + " " + ipAddress) + 1;
				} else {
					num++;
				}
				MapInfo.number.put(wia_shortaddress + " " + ipAddress, num);
			}
			/* 当前序列号大于上一次，正常 */
			else if (now_serial > last_serial) {
				logWrite.write("当前设备的长地址（长地址加上从栈地址）为：" + wia_longaddress);
				logWrite.write("上一次序列号：" + last_serial);
				logWrite.write("当前序列号：" + now_serial);
				lose = now_serial - last_serial - 1;
				/* 将接收到的数据包个数加1 */
				if (MapInfo.number.get(wia_shortaddress + " " + ipAddress) != null) {
					num = MapInfo.number.get(wia_shortaddress + " " + ipAddress) + 1;
				} else {
					num++;
				}
				MapInfo.number.put(wia_shortaddress + " " + ipAddress, num);
			}
			/* 当前序列号小于上次，可能是重新编号，此时应将基础值base和总接收包数重置 */
			else if (now_serial < last_serial) {
				num++;
				MapInfo.base.put(wia_shortaddress + " " + ipAddress, serial);
				MapInfo.number.put(wia_shortaddress + " " + ipAddress, num);
			}
		} else {
			num++;
			MapInfo.base.put(wia_shortaddress + " " + ipAddress, serial);
			MapInfo.number.put(wia_shortaddress + " " + ipAddress, num);
		}
		MapInfo.serial.put(wia_shortaddress + " " + ipAddress, serial);
		logWrite.write("截至到上一次，丢包个数为：" + lose);
		if (lose < 0) {
			lose = 0;
		}

		/*
		 * if (MapInfo.number.get(wia_shortaddress + " " + ipAddress) != null) {
		 * num = MapInfo.number.get(wia_shortaddress + " " + ipAddress) + 1; }
		 * else { num++; } MapInfo.number.put(wia_shortaddress + " " +
		 * ipAddress, num);
		 */

		int begin = MapInfo.base.get(wia_shortaddress + " " + ipAddress);
		int lose_total = serial - begin + 1 - num;
		if (lose_total < 0) {
			lose_total = 0;
		} else if (lose_total > 10) {// 当丢包个数大于10，就重新计算
			lose_total = 0;
			num = 1;
			MapInfo.base.put(wia_shortaddress + " " + ipAddress, serial);
			MapInfo.number.put(wia_shortaddress + " " + ipAddress, num);
		}

		begin = MapInfo.base.get(wia_shortaddress + " " + ipAddress);
		if (begin == serial) {
			rate = 100;
		} else {
			try {
				rate = ((float) num / ((float) serial - (float) begin + 1)) * 100;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		if (rate > 100) {
			rate = 100;
		}
		int total = serial - begin + 1;
		logWrite.write("总计，丢包个数为：" + lose_total);
		logWrite.write("成功率：(" + num + " / (" + serial + " - " + begin + " + 1)) * 100 = " + num + " / " + total + " = "
				+ rate + " %");

		updataRate(wia_longaddress, serial, rate, lose_total, logWrite);
	}

	public void updataRate(String wia_longaddress, int serial, float rate, int lose, LogWrite logWrite) {
		Sqlserver dBtool = new Sqlserver();
		/* 判断是否重复，如果数据库有就进行update操作，没有就进行insert操作 */
		boolean isnew = true;
		String sql = "select * from Adapter_server_final where longaddress = '" + wia_longaddress + "'";
		try {
			ResultSet rs = dBtool.executeQuery(sql);
			while (rs.next()) {
				isnew = false;
			}
		} catch (SQLException e) {
			logWrite.write("【 Error!】Datagram.excuteDatagram.0：" + e.getMessage());
		}
		if (wia_longaddress.equals("null")) {
			isnew = false;
		}
		if (isnew) {
			sql = "insert into  Adapter_server_final (longaddress,datagram_serial,dvalue,rate,reachtime) values ('"
					+ wia_longaddress + "'," + serial + "," + lose + "," + rate + ",getdate())";
			try {
				logWrite.write("执行sql：" + sql);
				dBtool.executeUpdate(sql);
			} catch (SQLException e) {
				logWrite.write("【 Error!】Datagram.excuteDatagram.0：" + e.getMessage());
			}
		} else {
			sql = "update Adapter_server_final set datagram_serial = " + serial + ",dvalue = " + lose + ",rate = "
					+ rate + " ,reachtime = getdate() where longaddress = '" + wia_longaddress + "'";
			try {
				logWrite.write("执行sql：" + sql);
				dBtool.executeUpdate(sql);
			} catch (SQLException e) {
				logWrite.write("【 Error!】Datagram.excuteDatagram.0：" + e.getMessage());
			}
		}
		try {
			dBtool.free();
		} catch (SQLException e) {
			logWrite.write(e.getMessage());
		}
	}
}
