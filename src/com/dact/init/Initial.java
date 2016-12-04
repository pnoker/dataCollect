package com.dact.init;

public class Initial {
	public void init() {
		Wireless wireless = new Wireless();
		wireless.initWireless();
		Pi pi = new Pi();
		pi.initPi();
		Dlt dlt = new Dlt();
		dlt.initDlt();
	}
}
