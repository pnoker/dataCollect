package com.dact.util;

public class PrintUtil {
	private DateUtil date;

	public PrintUtil(){
		this.date = new DateUtil();
	}
	public void printTitle(String title) {
		System.out.println(date.getCompleteTime() + " --> " + title);
	}

	public String getHexDatagram(byte[] b, int length) {
		StringBuffer sbuf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sbuf.append(hex.toUpperCase());
		}
		return sbuf.toString();
	}
	public static void main(String[] args) {
		DateUtil date=new DateUtil();
		System.out.println(date.getCompleteTime() + "-->" + "接收到数据报文");
	}
}
