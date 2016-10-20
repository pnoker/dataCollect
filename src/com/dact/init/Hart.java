package com.dact.init;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

import com.dact.pojo.MapInfo;
import com.dact.util.DBtool;

public class Hart {
	public void init_hart() {
		DBtool dbtool = new DBtool();
		String[] tableArr = null;
		ArrayList<String> tmp1 = (new DataTable()).init_datatable();
		try {
			BufferedReader bw = new BufferedReader(
					new InputStreamReader(new FileInputStream("D:\\sia\\confiles\\hart.txt"), "UTF-8"));
			String line = null;
			while ((line = bw.readLine()) != null) {
				String[] lineArr = line.split("\\t");
				MapInfo.getHart_map().put(lineArr[0], lineArr[1]);
				Date date = new Date();
				MapInfo.getHart_currentime().put(lineArr[0], date);
				tableArr = lineArr[1].split(" ");
				if (tableArr[2].equals("false") && tableArr[3].equals("false")) {
					String sente = "create table dbo.[" + tableArr[1] + "_data]"
							+ "(serial int identity(1,1), typeserial nvarchar(50) ,tag int ,value float ,reachtime datetime)";
					if (!tmp1.contains(tableArr[1] + "_data")) {
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
						System.out.println("无需进行初始化");
					}
				} else if (tableArr[2].equals("true") && tableArr[3].equals("false")) {
					String sente = "create table dbo.[" + tableArr[1] + "_data]"
							+ "(serial int identity(1,1), typeserial nvarchar(50) ,tag int ,value float ,reachtime datetime)";
					if (!tmp1.contains(tableArr[1] + "_data")) {
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
						System.out.println("无需进行初始化");
					}
				} else if (tableArr[2].equals("true") && tableArr[3].equals("true")) {
					String sente = "create table dbo.[" + tableArr[1] + "_data]"
							+ "(serial int identity(1,1), typeserial nvarchar(50) ,tag int ,value float ,reachtime datetime)";
					if (!tmp1.contains(tableArr[1] + "_data")) {
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
						System.out.println("无需进行初始化");
					}

				}
			}
			bw.close();
			dbtool.free();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
