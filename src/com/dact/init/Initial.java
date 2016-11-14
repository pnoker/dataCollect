package com.dact.init;

public class Initial {
	public void init() {
		System.out.println("<---初始化位号--->");
		Weihao weihao = new Weihao();
		weihao.initWeihao();
		System.out.println("<---初始化无线IO--->");
		Wireless wireless = new Wireless();
		wireless.initWireless();
		System.out.println("<---初始化水表--->");
		Shui shui = new Shui();
		shui.initShui();
		System.out.println("<---初始化DLT--->");
		Dlt dlt = new Dlt();
		dlt.initDlt();
	}
}
