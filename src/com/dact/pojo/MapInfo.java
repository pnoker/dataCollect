package com.dact.pojo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapInfo {
	public static Map<String, String> addressmap = new HashMap<String, String>();
	// 设备类型
	public static Map<String, String> typemap = new HashMap<String, String>();
	public static Map<String, String> hart_map = new HashMap<String, String>();
	public static Map<String, String> wirelessio_map = new HashMap<String, String>();
	public static Map<String, String> ai_map = new HashMap<String, String>();
	public static Map<String, String> shui_map = new HashMap<String, String>();
	public static Map<String, String> pi_map = new HashMap<String, String>();
	// 位号map
	public static Map<String, String> weihao_map = new HashMap<String, String>();
	public static Map<String, Long> gateway_currentime = new HashMap<String, Long>();
	public static Map<String, Date> wirelessio_currentime = new HashMap<String, Date>();
	public static Map<String, Date> hart_currentime = new HashMap<String, Date>();
	public static Map<String, Date> ai_currentime = new HashMap<String, Date>();
	public static Map<String, Date> pi_currentime = new HashMap<String, Date>();
	public static Map<String, Date> shui_currentime = new HashMap<String, Date>();

	// 超时重连
	public static Map<String, Long> first = new HashMap<String, Long>();

	// 序列号
	public static Map<String, Integer> serial = new HashMap<String, Integer>();

	// 成功率
	public static Map<String, Integer> number = new HashMap<String, Integer>();
	public static Map<String, Integer> base = new HashMap<String, Integer>();

	public static Map<String, String> getAddressmap() {
		return addressmap;
	}

	public static void setAddressmap(Map<String, String> addressmap) {
		MapInfo.addressmap = addressmap;
	}

	public static Map<String, String> getTypemap() {
		return typemap;
	}

	public static void setTypemap(Map<String, String> typemap) {
		MapInfo.typemap = typemap;
	}

	public static Map<String, String> getHart_map() {
		return hart_map;
	}

	public static void setHart_map(Map<String, String> hart_map) {
		MapInfo.hart_map = hart_map;
	}

	public static Map<String, String> getWirelessio_map() {
		return wirelessio_map;
	}

	public static void setWirelessio_map(Map<String, String> wirelessio_map) {
		MapInfo.wirelessio_map = wirelessio_map;
	}

	public static Map<String, String> getAi_map() {
		return ai_map;
	}

	public static void setAi_map(Map<String, String> ai_map) {
		MapInfo.ai_map = ai_map;
	}

	public static Map<String, String> getShui_map() {
		return shui_map;
	}

	public static void setShui_map(Map<String, String> shui_map) {
		MapInfo.shui_map = shui_map;
	}

	public static Map<String, String> getPi_map() {
		return pi_map;
	}

	public static void setPi_map(Map<String, String> pi_map) {
		MapInfo.pi_map = pi_map;
	}

	public static Map<String, String> getWeihao_map() {
		return weihao_map;
	}

	public static void setWeihao_map(Map<String, String> weihao_map) {
		MapInfo.weihao_map = weihao_map;
	}

	public static Map<String, Long> getGateway_currentime() {
		return gateway_currentime;
	}

	public static void setGateway_currentime(Map<String, Long> gateway_currentime) {
		MapInfo.gateway_currentime = gateway_currentime;
	}

	public static Map<String, Date> getWirelessio_currentime() {
		return wirelessio_currentime;
	}

	public static void setWirelessio_currentime(Map<String, Date> wirelessio_currentime) {
		MapInfo.wirelessio_currentime = wirelessio_currentime;
	}

	public static Map<String, Date> getHart_currentime() {
		return hart_currentime;
	}

	public static void setHart_currentime(Map<String, Date> hart_currentime) {
		MapInfo.hart_currentime = hart_currentime;
	}

	public static Map<String, Date> getAi_currentime() {
		return ai_currentime;
	}

	public static void setAi_currentime(Map<String, Date> ai_currentime) {
		MapInfo.ai_currentime = ai_currentime;
	}

	public static Map<String, Date> getPi_currentime() {
		return pi_currentime;
	}

	public static void setPi_currentime(Map<String, Date> pi_currentime) {
		MapInfo.pi_currentime = pi_currentime;
	}

	public static Map<String, Date> getShui_currentime() {
		return shui_currentime;
	}

	public static void setShui_currentime(Map<String, Date> shui_currentime) {
		MapInfo.shui_currentime = shui_currentime;
	}

}
