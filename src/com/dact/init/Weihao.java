package com.dact.init;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.dact.pojo.MapInfo;
import com.dact.util.OperateTxtUtil;


public class Weihao {
	public void initWeihao() {
		System.out.println("<---初始化位号信息--->");
		OperateTxtUtil readTxtUtil = new OperateTxtUtil();
		ArrayList<String> weihao = new ArrayList<String>();
		weihao = readTxtUtil.readLine("D:/sia/confiles/weihao.txt");
		for (int m = 0; m < weihao.size(); m++) {
			MapInfo.weihao_map.put(weihao.get(m).split("\\t")[0], weihao.get(m).split("\\t")[1]);
		}
	}
}
