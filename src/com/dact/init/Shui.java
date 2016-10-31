package com.dact.init;

import java.util.ArrayList;
import java.util.Date;

import com.dact.pojo.MapInfo;
import com.dact.util.OperateTxtUtil;

public class Shui {
	public void initShui() {
		OperateTxtUtil operateTxtUtil = new OperateTxtUtil();
		ArrayList<String> shuibiao = new ArrayList<String>();
		shuibiao = operateTxtUtil.readLine("D:/sia/confiles/shuibiao.txt");
		for (int m = 0; m < shuibiao.size(); m++) {
			MapInfo.shui_map.put(shuibiao.get(m).split("\\t")[0], shuibiao.get(m).split("\\t")[1]);
			Date date = new Date();
			MapInfo.shui_currentime.put(shuibiao.get(m).split("\\t")[0], date);
		}
	}
}
