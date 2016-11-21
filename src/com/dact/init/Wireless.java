package com.dact.init;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.dact.pojo.MapInfo;
import com.dact.util.Sqlserver;
import com.dact.util.OperateTxtUtil;

public class Wireless {
	public void initWireless() {
		Sqlserver dBtool = new Sqlserver();
		OperateTxtUtil operateTxtUtil = new OperateTxtUtil();
		ArrayList<String> wirelessio = new ArrayList<String>();
		wirelessio = operateTxtUtil.readLine("D:/sia/confiles/wirelessIO.txt");
		String address, table = null;
		String[] tables, item;
		ArrayList<String> datatable = new ArrayList<String>();
		datatable = operateTxtUtil.readLine("D:/sia/confiles/datatable.txt");
		for (int m = 0; m < wirelessio.size(); m++) {
			address = wirelessio.get(m).split("\\t")[0];
			table = wirelessio.get(m).split("\\t")[1];
			MapInfo.wirelessio_map.put(address, table);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -30);
			Date date = cal.getTime();
			MapInfo.wirelessio_currentime.put(address, date);
			tables = table.split(",");
			String sente = "create table " + table.split(",")[1] + "_data (serial int identity(1,1), typeserial nvarchar(50) ,";
			for (int i = 2; i < tables.length; i++) {
				item = tables[i].split(" ");
				if (item[3].contains("float")) {
					sente += "" + item[0].substring(1, item[0].length()) + " " + item[3] + ",";
				} else if (item[3].contains("int")) {
					sente += "" + item[0].substring(1, item[0].length()) + " " + item[3].substring(0, item[3].length() - 1) + ",";
				}
			}
			sente += "reachtime datetime)";
			if (!datatable.contains(table.split(",")[1] + "_data")) {
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
