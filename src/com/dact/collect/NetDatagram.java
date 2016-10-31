package com.dact.collect;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.util.DBtool;
import com.dact.util.DateUtil;
import com.dact.util.PackageProcessor;
import com.dact.util.PrintUtil;

/**
 * @author Pnoker
 * @Description 网络报文，更新拓扑图
 */
public class NetDatagram {
	private String networkinfo;

	public void excuteNetDatagram(PackageProcessor p, BaseInfo base, String networkinfo) {
		DBtool dBtool = new DBtool();
		PrintUtil printUtil = new PrintUtil();
		DateUtil dateUtil = new DateUtil();
		String wia_longaddress, wia_shortaddress, typeofwatch = "";

		wia_longaddress = p.bytesToString(2, 9);
		wia_shortaddress = p.bytesToString(10, 11);
		printUtil.printTitle("网关:" + base.getIpaddress() + ",长地址：" + wia_longaddress);
		printUtil.printTitle("网关:" + base.getIpaddress() + ",短地址：" + wia_shortaddress);

		int tmpcount = Integer.parseInt(p.bytesToString(22, 22));
		typeofwatch = p.bytesToString(23 + tmpcount, 24 + tmpcount);
		printUtil.printTitle("网关:" + base.getIpaddress() + ",邻居个数：" + tmpcount);
		printUtil.printTitle("网关:" + base.getIpaddress() + ",设备类型：" + typeofwatch);
		MapInfo.addressmap.put(wia_shortaddress + " " + base.getIpaddress(), wia_longaddress);
		MapInfo.typemap.put(wia_longaddress, typeofwatch);

		if (!((wia_shortaddress.equals("0100")) || (wia_shortaddress.equals("0000")) || (wia_longaddress.equals("b120000000417a00")) || (wia_longaddress.equals("007a410000000a7d"))
				|| (wia_longaddress.equals("007a410000000ab2")) || (wia_longaddress.equals("007a410000000a91"))))
			this.networkinfo = networkinfo + wia_longaddress + ",";
		String sente = "update [Network_tuopu] set manydevices = '" + this.networkinfo + "'where ipaddress = '" + base.getIpaddress() + "'";
		printUtil.printTitle("网关:" + base.getIpaddress() + ",更新网关的网络拓扑信息为：" + networkinfo);
		try {
			printUtil.printTitle("执行sql：" + sente);
			dBtool.executeUpdate(sente);
			dBtool.free();
		} catch (SQLException e) {
			printUtil.printTitle("【 Error!】NetDatagram.excuteNetDatagram：" + e.getMessage());
		}
	}

	public String getNetworkinfo() {
		return networkinfo;
	}

	public void setNetworkinfo(String networkinfo) {
		this.networkinfo = networkinfo;
	}

}
