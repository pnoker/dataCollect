package com.dact.util;

import java.util.Date;

/**
 * @author Pnoker
 * @description 打印工具类
 */
public class PrintUtil {
	private DateUtil date;

	public PrintUtil() {
		this.date = new DateUtil();
	}

	public void printDetail(String ipaddress, String detail) {
		while (ipaddress.length() < 15) {
			ipaddress += " ";
		}
		System.out.println(date.getCompleteTime(new Date()) + " " + ipaddress + " --> " + detail);
	}

	public void printMessage(String message) {
		System.out.println("<---- " + date.getCompleteTime(new Date()) + " ----> " + message);
	}

	/**
	 * 打印十六进制的报文，不足两位，前面补零
	 * 
	 * @param b
	 * @param length
	 * @return String
	 */
	public String getHexDatagram(byte[] b, int length) {
		StringBuffer sbuf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			/* 不足两位前面补零处理 */
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sbuf.append(hex.toUpperCase());
		}
		return sbuf.toString();
	}

	public static void main(String[] args) {
		String i = "110.112.126.19";
		String n = "10.112.141.212";
		while (i.length() < 15) {
			i += " ";
		}
		if (n.length() == 15)
			System.out.println("mm");
		System.out.println(i.length());
		System.out.println(n.length());
	}
}
