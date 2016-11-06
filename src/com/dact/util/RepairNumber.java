package com.dact.util;

import java.sql.ResultSet;
import java.sql.SQLException;
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
		DBtool dBtool = new DBtool();
		DateUtil dateUtil = new DateUtil();
		Map<String, String> config = null;
		try {
			config = ExcutePro.getP("config.properties");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		String sql = "";
		for (Entry<String, String> entry : config.entrySet()) {
			String value = entry.getValue();
			String[] temp = value.split("#");
			sql = "select top(1) * from " + temp[0] + " where typeserial = '" + temp[1] + "' and tag = " + temp[2]
					+ " order by reachtime desc";
			ResultSet rs = dBtool.executeQuery(sql);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = "";
			float val;
			while (rs.next()) {
				time = rs.getTimestamp("reachtime").toString();
				val = rs.getFloat("value");
				Date date = sdf.parse(time);
				Date now = new Date();
				long intervel = (now.getTime() - date.getTime()) / (1000 * 60);
				logWrite.write("<--------- 本次 "+temp[1]+" 的数据时间间隔为 " + intervel + " 分钟");
				if (intervel >= 70) {
					logWrite.write("<--------- 发现数据丢包了 ");
					date.setTime(date.getTime() + (1000 * 60 * 60));
					sql = "insert into " + temp[0] + " (typeserial,tag,value,reachtime,isrepair) values ('" + temp[1]
							+ "'," + temp[2] + "," + val + ",'" + sdf.format(date) + "',1)";
					logWrite.write("<--------- 填补数据 " + val);
					dBtool.executeUpdate(sql);
				}
			}
			rs.close();
		}
		dBtool.free();
		logWrite.write("<---------结束，检测数据丢包程序--------->");
	}
}
