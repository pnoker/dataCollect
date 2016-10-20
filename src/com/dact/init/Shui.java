package com.dact.init;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import com.dact.pojo.MapInfo;
import com.dact.util.DBtool;

public class Shui {
	public void init_shui() {
		DBtool dbtool = new DBtool();
		String[] tableArr = null;
		ArrayList<String> tmp1 = (new DataTable().init_datatable());
		try {
			BufferedReader bw = new BufferedReader(
					new InputStreamReader(new FileInputStream("D:\\sia\\confiles\\shuibiao.txt"), "UTF-8"));
			String line = null;
			while ((line = bw.readLine()) != null) {
				System.out.println("!!!!" + line);
				String[] lineArr = line.split("\\t");
				MapInfo.getShui_map().put(lineArr[0], lineArr[1]);
				System.out.println("!!!!" + lineArr[1]);
				Date date = new Date();
				System.out.println(date);
				MapInfo.getShui_currentime().put(lineArr[0], date);
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
