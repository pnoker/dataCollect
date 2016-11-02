package com.dact.dateType;

import java.sql.SQLException;
import java.util.Map.Entry;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.util.DBtool;
import com.dact.util.DateUtil;
import com.dact.util.LogWrite;
import com.dact.util.PackageProcessor;
import com.dact.util.PrintUtil;

/**
 * @author Pnoker
 * @Description 网络报文，更新拓扑图
 */
public class NetDatagram {
	private String networkinfo = "";

	public String getNetworkinfo() {
		return networkinfo;
	}

	public void setNetworkinfo(String networkinfo) {
		this.networkinfo = networkinfo;
	}

	/**
	 * 处理网络报文，01 01
	 * 为该网关的网络报文，网络报文包含节点的长地址、短地址、标签、设备类型、数据率、上下行GraphID、邻居,网络报文携带了长地址和短地址的对应关系，
	 * 由于数据报文只有短地址，于是只能通过网络报文提供的对应关系确定设备
	 * 
	 * @param p
	 * @param base
	 * @param networkinfo
	 * @param logWrite
	 */
	public void excuteNetDatagram(PackageProcessor p, BaseInfo base, String networkinfo, LogWrite logWrite) {
		DBtool dBtool = new DBtool();
		PrintUtil printUtil = new PrintUtil();
		DateUtil dateUtil = new DateUtil();
		String wia_longaddress = "", wia_shortaddress = "", deviceType = "";
		/*
		 * 网络报文:01 01/43 29 00 00 00 41 7A 00/11 00/0E 00/04/01/00 00/0C 00/0D
		 * 00/01/01
		 * 00/0E00/FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00/9A 6C
		 */
		wia_longaddress = p.bytesToString(2, 9);
		wia_shortaddress = p.bytesToString(10, 11);

		logWrite.write("长地址：" + wia_longaddress);
		logWrite.write("短地址：" + wia_shortaddress);

		int neighbor = Integer.parseInt(p.bytesToString(22, 22));
		deviceType = p.bytesToString(23 + neighbor * 2, 24 + neighbor * 2);

		logWrite.write("邻居个数：" + neighbor);
		logWrite.write("设备类型：" + deviceType);

		for (Entry<String, String> entry : MapInfo.addressmap.entrySet()) {
			String longaddress = entry.getValue();
			if (wia_longaddress.equals(longaddress)) {
				MapInfo.addressmap.remove(entry.getKey());
			}
		}

		MapInfo.addressmap.put(wia_shortaddress + " " + base.getIpaddress(), wia_longaddress);
		MapInfo.typemap.put(wia_longaddress, deviceType);

		if (!((wia_shortaddress.equals("0100")) || (wia_shortaddress.equals("0000")) || (wia_longaddress.equals("b120000000417a00")) || (wia_longaddress.equals("007a410000000a7d"))
				|| (wia_longaddress.equals("007a410000000ab2")) || (wia_longaddress.equals("007a410000000a91"))))
			this.networkinfo = networkinfo + wia_longaddress + ",";
		String sente = "update [Network_tuopu] set manydevices = '" + this.networkinfo + "' where ipaddress = '" + base.getIpaddress() + "'";
		logWrite.write("更新网关的网络拓扑信息为：" + this.networkinfo);
		printUtil.printDetail(base.getIpaddress(), "更新网关的网络拓扑信息为：" + this.networkinfo);
		try {
			logWrite.write("执行sql：" + sente);
			printUtil.printDetail(base.getIpaddress(), "执行sql：" + sente);
			dBtool.executeUpdate(sente);
			dBtool.free();
		} catch (SQLException e) {
			logWrite.write("【 Error!】NetDatagram.excuteNetDatagram：" + e.getMessage());
			printUtil.printDetail(base.getIpaddress(), "【 Error!】NetDatagram.excuteNetDatagram：" + e.getMessage());
		}
	}
}
