package com.dact.init;

import java.awt.image.DataBufferUShort;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import com.dact.pojo.MapInfo;
import com.dact.util.DBtool;
import com.dact.util.DateUtil;
import com.dact.util.OperateTxtUtil;
import com.dact.util.PrintUtil;

public class Shui {
	public void initShui() {
		PrintUtil printUtil = new PrintUtil();
		DateUtil dateUtil = new DateUtil();
		printUtil.printTitle("<---初始化水表信息--->");
		OperateTxtUtil operateTxtUtil = new OperateTxtUtil();
		ArrayList<String> shuibiao = new ArrayList<String>();
		shuibiao = operateTxtUtil.readLine("D:/sia/confiles/shuibiao.txt");
		for (int m = 0; m < shuibiao.size(); m++) {
			MapInfo.shui_map.put(shuibiao.get(m).split("\\t")[0], shuibiao.get(m).split("\\t")[1]);
			Date date = new Date();
			MapInfo.shui_currentime.put(shuibiao.get(m).split("\\t")[0], date);

			printUtil.printTitle(shuibiao.get(m).split("\\t")[0] + "," + shuibiao.get(m).split("\\t")[1]);
			printUtil.printTitle(shuibiao.get(m).split("\\t")[0] + "," + dateUtil.getCompleteTime(date));
		}
	}
}
