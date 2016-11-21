package com.dact.init;

import java.util.ArrayList;
import java.util.Date;

import com.dact.pojo.MapInfo;
import com.dact.util.OperateTxtUtil;

public class Pi {
	public void initPi() {
		OperateTxtUtil operateTxtUtil = new OperateTxtUtil();
		ArrayList<String> pi = new ArrayList<String>();
		pi = operateTxtUtil.readLine("D:/sia/confiles/pi.txt");
		for (int m = 0; m < pi.size(); m++) {
			MapInfo.pi_map.put(pi.get(m).split("\\t")[0], pi.get(m).split("\\t")[1]);
			MapInfo.pi_currentime.put(pi.get(m).split("\\t")[0], new Date());
		}
	}
}
