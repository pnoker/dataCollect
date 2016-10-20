package com.dact.init;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.dact.pojo.MapInfo;

public class Weihao {
	public void init_weihao() {
		try {

			BufferedReader bw = new BufferedReader(
					new InputStreamReader(new FileInputStream("D:\\sia\\confiles\\weihao.txt"), "UTF-8"));
			String line = null;
			// 因为不知道有几行数据，所以先存入list集合中
			while ((line = bw.readLine()) != null) {
				System.out.println("!!!!" + line);
				String[] lineArr = line.split("\\t");
				System.out.println("!!!!" + lineArr.length);
				MapInfo.getWeihao_map().put(lineArr[0], lineArr[1]);
			}
			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
