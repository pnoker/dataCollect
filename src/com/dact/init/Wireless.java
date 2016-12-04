package com.dact.init;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.dact.pojo.MapInfo;
import com.dact.util.Sqlserver;

public class Wireless {
	public void initWireless() {
		Sqlserver dbtool = new Sqlserver();
		String[] tableArr = null;
		String[] eachItem = null;
		DataTable dataTable = new DataTable();
		ArrayList<String> tmp2 = dataTable.init_datatable();

		try {

			BufferedReader bw = new BufferedReader(new FileReader(new File("D:\\sia\\confiles\\wirelessIO.txt")));
			String line = null;
			while ((line = bw.readLine()) != null) {
				String[] lineArr = line.split("\\t");
				MapInfo.wirelessio_map.put(lineArr[0], lineArr[1]);

				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, -30);

				Date date = cal.getTime();
				System.out.println("当前函数是init_modbus,想看看date" + date);
				System.out.println("当前函数是init_modbus，想看看长地址是" + lineArr[0]);
				MapInfo.wirelessio_currentime.put(lineArr[0], date);
				tableArr = lineArr[1].split(",");
				String sente = "create table " + tableArr[1] + "_data" + "(serial int identity(1,1), typeserial nvarchar(50) ,";
				for (int i = 2; i < tableArr.length; i++) {
					eachItem = tableArr[i].split(" ");
					if (eachItem[3].contains("float")) {
						sente = sente + "" + eachItem[0].substring(1, eachItem[0].length()) + " " + eachItem[3] + ",";
					} else if (eachItem[3].contains("int")) {
						sente = sente + "" + eachItem[0].substring(1, eachItem[0].length()) + " " + eachItem[3].substring(0, eachItem[3].length() - 1) + ",";
					}

				}
				sente = sente + "reachtime datetime)";
				System.out.println("当前的语句是" + sente);

				System.out.println("现在正在init_wireless函数中,当前的字符串是" + tableArr[1] + "_data");
				System.out.println("现在正在init_wireless函数中,tmp2是否包含" + tmp2.contains(tableArr[1] + "_data"));

				if (!tmp2.contains(tableArr[1] + "_data")) {
					System.out.println("现在正在init_wireless函数中,建表语句执行了吗");
					dbtool.executeUpdate(sente);
					BufferedWriter fw = null;
					try {
						FileOutputStream fos = new FileOutputStream("D:\\sia\\confiles\\datatable.txt", true);
						fw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));

						fw.write(tableArr[1] + "_data");
						fw.newLine();
						fw.flush();
						fw.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("现在正在init_wireless函数中,无需进行初始化");
				}

			}
			bw.close();
			dbtool.free();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
