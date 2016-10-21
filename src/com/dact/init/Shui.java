package com.dact.init;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import com.dact.pojo.MapInfo;
import com.dact.util.DBtool;
import com.dact.util.OperateTxtUtil;

public class Shui {
	public void initShui() {
		System.out.println("<---初始化位号信息--->");
		OperateTxtUtil operateTxtUtil = new OperateTxtUtil();
		ArrayList<String> shuibiao = new ArrayList<String>();
		shuibiao = operateTxtUtil.readLine("D:/sia/confiles/shuibiao.txt");
		for (int m = 0; m < shuibiao.size(); m++) {
			MapInfo.getShui_map().put(shuibiao.get(m).split("\\t")[0], shuibiao.get(m).split("\\t")[1]);
		}
	}
}
