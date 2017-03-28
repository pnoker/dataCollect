package com.dact.util;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Pnoker
 * @mail peter-no@foxmail.com
 * @date 2016年11月6日
 * @description 补数，填补丢包造成的数据损失
 */

public class RepairNumber {
	private LogWrite logWrite;

	public RepairNumber() {
		this.logWrite = new LogWrite("RepairNumber");
	}

	public void repair() throws Exception {
		logWrite.write("<---------开始，检测数据是否丢失--------->");
		Sqlserver dBtool = new Sqlserver();
		Sqlserver dBtool2 = new Sqlserver();
		ResultSet rs = null;
		DateUtil dateUtil = new DateUtil();
		Map<String, String> config = null;
		try {
			config = ExcutePro.getProperties("config.properties");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		String sql = "";
		for (Entry<String, String> entry : config.entrySet()) {
			String value = entry.getValue();
			String[] temp = value.split("#");
			sql = "select top(1) * from " + temp[0] + " where typeserial = '" + temp[1] + "' and tag = " + temp[2]
					+ " order by reachtime desc";
			rs = dBtool.executeQuery(sql);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = "";
			float val;
			while (rs.next()) {
				time = rs.getTimestamp("reachtime").toString();
				val = rs.getFloat("value");
				Date date = sdf.parse(time);
				Date now = new Date();
				long intervel = (now.getTime() - date.getTime()) / 1000;
				logWrite.write("本次 " + temp[1] + " 的数据时间间隔为 " + (intervel) / 60 + " 分钟");
				if (intervel >= Long.parseLong(temp[3])) {
					logWrite.write("发现数据丢包了 ");
					date.setTime(date.getTime() + (1000 * Long.parseLong(temp[4])));
					sql = "insert into " + temp[0] + " (typeserial,tag,value,reachtime,isrepair) values ('" + temp[1]
							+ "'," + temp[2] + "," + val + ",'" + sdf.format(date) + "',1)";
					logWrite.write("填补数据: " + temp[1] + " , " + val + ", " + sdf.format(date));
					dBtool2.executeUpdate(sql);
					if (temp[1].contains("sia")) {
						if (temp[1].equals("sia0001")) {
							val += 42959;
						} else if (temp[1].equals("sia0002")) {
							val += 44165;
						} else if (temp[1].equals("sia0003")) {
							val += 1696.7;
						} else if (temp[1].equals("sia0004")) {
							val += 1821;
						} else if (temp[1].equals("sia0005")) {
							val += 357.6;
						} else if (temp[1].equals("sia0006")) {
							val += 3280;
						} else if (temp[1].equals("sia0007")) {
							val += 608;
						}
						sql = "update shui_opc set value = " + val + " ,reachtime = '" + sdf.format(date)
								+ "' where typeserial = '" + temp[1] + "'";
						dBtool2.executeUpdate(sql);
					} else if (temp[1].contains("wxio")) {
						sql = "update shui_opc set value = " + val + " ,reachtime = '" + sdf.format(date)
								+ "' where typeserial = '" + temp[1] + "_" + temp[2] + "'";
						dBtool2.executeUpdate(sql);
					}

				}
			}
		}
		rs.close();
		dBtool.free();
		dBtool2.free();
		logWrite.write("<---------结束，检测数据丢包程序--------->");
	}
}
