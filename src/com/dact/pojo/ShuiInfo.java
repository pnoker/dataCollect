package com.dact.pojo;

public class ShuiInfo {
	private String shuiInfo;
	private float shuiliuliang;
	private float dianya;
	private int shui_i;

	public ShuiInfo() {
		this.shuiInfo = "";
		this.shuiliuliang = 0;
		this.dianya = 0;
		this.shui_i = 10;
	}

	public String getShuiInfo() {
		return shuiInfo;
	}

	public void setShuiInfo(String shuiInfo) {
		this.shuiInfo = shuiInfo;
	}

	public float getShuiliuliang() {
		return shuiliuliang;
	}

	public void setShuiliuliang(float shuiliuliang) {
		this.shuiliuliang = shuiliuliang;
	}

	public float getDianya() {
		return dianya;
	}

	public void setDianya(float dianya) {
		this.dianya = dianya;
	}

	public int getShui_i() {
		return shui_i;
	}

	public void setShui_i(int shui_i) {
		this.shui_i = shui_i;
	}

}
