package com.dact.init;

import java.sql.SQLException;
import java.util.ArrayList;

import com.dact.pojo.MapInfo;
import com.dact.util.DBtool;
import com.dact.util.OperateTxtUtil;

public class Wireless {
	public void initWireless() {
		System.out.println("<---初始化无线IO信息--->");
		DBtool dBtool = new DBtool();
		OperateTxtUtil operateTxtUtil = new OperateTxtUtil();
		ArrayList<String> wirelessio = new ArrayList<String>();
		wirelessio = operateTxtUtil.readLine("D:/sia/confiles/wirelessIO.txt");
		String address, table = null;
		ArrayList<String> datatable = new ArrayList<String>();
		datatable = operateTxtUtil.readLine("D:/sia/confiles/datatable.txt");
		for (int m = 0; m < wirelessio.size(); m++) {
			address = wirelessio.get(m).split("\\t")[0];
			table = wirelessio.get(m).split("\\t")[1];
			MapInfo.wirelessio_map.put(address, table);
			if (!datatable.contains(table.split(",")[1] + "_data")) {
				String sente = "create table " + table.split(",")[1] + "_data (serial int identity(1,1), typeserial nvarchar(50) ,tag int ,value float ,reachtime datetime)";
				try {
					dBtool.executeUpdate(sente);
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
				operateTxtUtil.writeLine("D:/sia/confiles/datatable.txt", table.split(",")[1] + "_data");
			}
		}
	}
}
