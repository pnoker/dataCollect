package com.dact.pojo;

import java.util.Date;

public class ValueInfo {
	private String wia_longaddress;
	private String wia_shortaddress;
	private String typeofwatch;
	private String networkinfo;
	private String ipaddress;
	private Date lastime;
	private Date currentime;
	private int interval;

	public String getWia_longaddress() {
		return wia_longaddress;
	}

	public void setWia_longaddress(String wia_longaddress) {
		this.wia_longaddress = wia_longaddress;
	}

	public String getWia_shortaddress() {
		return wia_shortaddress;
	}

	public void setWia_shortaddress(String wia_shortaddress) {
		this.wia_shortaddress = wia_shortaddress;
	}

	public String getTypeofwatch() {
		return typeofwatch;
	}

	public void setTypeofwatch(String typeofwatch) {
		this.typeofwatch = typeofwatch;
	}

	public String getNetworkinfo() {
		return networkinfo;
	}

	public void setNetworkinfo(String networkinfo) {
		this.networkinfo = networkinfo;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public Date getLastime() {
		return lastime;
	}

	public void setLastime(Date lastime) {
		this.lastime = lastime;
	}

	public Date getCurrentime() {
		return currentime;
	}

	public void setCurrentime(Date currentime) {
		this.currentime = currentime;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

}
