package com.dact.init;

import java.util.ArrayList;
import java.util.Date;

import com.dact.pojo.MapInfo;
import com.dact.util.OperateTxtUtil;

public class Dlt {
	public void initShui() {
		OperateTxtUtil operateTxtUtil = new OperateTxtUtil();
		ArrayList<String> shuibiao = new ArrayList<String>();
		shuibiao = operateTxtUtil.readLine("D:/sia/confiles/dlt.txt");
		for (int m = 0; m < shuibiao.size(); m++) {
			MapInfo.shui_map.put(shuibiao.get(m).split("\\t")[0], shuibiao.get(m).split("\\t")[1]);
		}
	}
}
