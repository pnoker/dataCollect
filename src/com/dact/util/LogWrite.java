package com.dact.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

public class LogWrite {
	private BufferedWriter fw = null;
	private String path = "D:/dataCollect/log/";
	private String ipaddress;
	private String fromIp;
	private DateUtil date;
	private PrintUtil printUtil;

	public LogWrite(String ipaddress) {
		printUtil = new PrintUtil();
		this.ipaddress = ipaddress;
		this.fromIp = ipaddress;
		while (this.fromIp.length() < 15) {
			this.fromIp += " ";
		}
		this.date = new DateUtil();
		if (!(new File(path + ipaddress).isDirectory())) {
			new File(path + ipaddress).mkdirs();
		}
	}

	public void write(String detail) {
		DateUtil dateUtil = new DateUtil();
		String file = path + ipaddress + "/" + dateUtil.getDayTime(new Date()) + ".txt";
		try {
			FileOutputStream fos = new FileOutputStream(file, true);
			fw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			fw.write(date.getCompleteTime(new Date()) + " " + fromIp + " --> " + detail);
			printUtil.printDetail(fromIp, detail);
			fw.newLine();
			fw.flush();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void close() {

		try {
			fw.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
