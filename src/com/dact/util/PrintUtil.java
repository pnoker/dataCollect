package com.dact.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintUtil {
	private DateUtil date;

	public PrintUtil(){
		this.date = new DateUtil();
	}
	public void printTitle(String title) {
		System.out.println(date.getTime() + " --> " + title);
	}

	public String printHexDatagram(byte[] b, int length) {
		StringBuffer sbuf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sbuf.append(hex.toUpperCase());
		}
		printTitle("接收到数据报文:" + sbuf.toString());
		return sbuf.toString();
	}
}
