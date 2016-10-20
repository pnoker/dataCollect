package com.dact.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;

import com.dact.pojo.MapInfo;

public class Ai {
	public void init_ai() {
		try {
			BufferedReader bw = new BufferedReader(new FileReader(new File("D:\\sia\\confiles\\ai.txt")));
			String line = null;
			while ((line = bw.readLine()) != null) {
				String[] lineArr = line.split("\\t");
				MapInfo.getAi_map().put(lineArr[0], lineArr[1]);
				Date date = new Date();
				System.out.println(date);
				MapInfo.getAi_currentime().put(lineArr[0], date);

			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
