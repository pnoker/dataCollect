package com.dact.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWrite {
	private BufferedWriter fw = null;
	private String path = "D:/dataCollect/log/";
	private String fromIp;
	private DateUtil date;

	public LogWrite(String ipaddress) {
		DateUtil dateUtil = new DateUtil();
		this.fromIp = ipaddress;
		while (this.fromIp.length() < 15) {
			this.fromIp += " ";
		}
		this.date = new DateUtil();
		if (!(new File(path + ipaddress).isDirectory())) {
			new File(path + ipaddress).mkdirs();
		}
		String file = path + ipaddress + "/" + dateUtil.getDayTime(new Date()) + ".txt";
		try {
			FileOutputStream fos = new FileOutputStream(file, true);
			fw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void write(String detail) {
		try {
			fw.write(date.getCompleteTime(new Date()) + " " + fromIp + " --> " + detail);
			fw.newLine();
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {

		try {
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		LogWrite logWrite = new LogWrite("12");
		logWrite.write("nihao");
	}
}
