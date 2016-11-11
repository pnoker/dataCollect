package com.dact.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

public class LogWrite {
	private BufferedWriter fw = null;
	private BufferedWriter fwEasy = null;
	private String path = "D:/dataCollect/SiaLog/";
	private String pathEasy = "D:/dataCollect/Log/";
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
		if (!(new File(pathEasy + ipaddress).isDirectory())) {
			new File(pathEasy + ipaddress).mkdirs();
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

	public void writeEasy(String type, String detail) {
		DateUtil dateUtil = new DateUtil();
		String file = pathEasy + ipaddress + "/" + dateUtil.getDayTime(new Date()) + ".txt";
		String regex = "(.{2})";
		detail = detail.replaceAll(regex, "$1 ");
		try {
			FileOutputStream fos = new FileOutputStream(file, true);
			fwEasy = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			fwEasy.write(date.getCompleteTime(new Date()) + " " + fromIp + " --> " + type + detail);
			fwEasy.newLine();
			fwEasy.flush();
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

	public static void main(String[] args) {
		String detail = "01830b00cd450000740010";
		String regex = "(.{2})";
		detail = detail.replaceAll(regex, "$1 ");
		System.out.println(detail);
	}
}
