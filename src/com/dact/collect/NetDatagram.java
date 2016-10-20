package com.dact.collect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.pojo.ValueInfo;
import com.dact.util.PackageProcessor;

public class NetDatagram {
	public NetDatagram(PackageProcessor p,BaseInfo base) {
		ValueInfo.setWia_longaddress(p.bytesToString(2, 9));
		ValueInfo.setWia_shortaddress(p.bytesToString(10, 11));
		int tmpcount = Integer.parseInt(p.bytesToString(22, 22));
		System.out.println("!!!!!!!!!!!!!!!!当前的邻居个数是!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + tmpcount);
		ValueInfo.setTypeofwatch(p.bytesToString(23 + tmpcount, 24 + tmpcount));
		System.out.println("!!!!!!!!!!!!!!!!当前的设备类型是!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + ValueInfo.getTypeofwatch());

		System.out.println("0101命令，wia-pa网络长地址是：" + ValueInfo.getWia_longaddress());
		System.out.println("0101命令，wia-pa网络短地址是：" + ValueInfo.getWia_shortaddress() + " " + ValueInfo.getIpaddress());

		MapInfo.getAddressmap().put(ValueInfo.getWia_shortaddress() + " " + ValueInfo.getIpaddress(),
				ValueInfo.getWia_longaddress());
		MapInfo.getTypemap().put(ValueInfo.getWia_longaddress(), ValueInfo.getTypeofwatch());

		Iterator iter = MapInfo.getAddressmap().entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			System.out.println("遍历的key是：" + entry.getKey());
			System.out.println("遍历的value是：" + entry.getValue());
		}
		if (!((ValueInfo.getWia_shortaddress().equals("0100")) || (ValueInfo.getWia_shortaddress().equals("0000"))
				|| (ValueInfo.getWia_longaddress().equals("b120000000417a00"))
				|| (ValueInfo.getWia_longaddress().equals("007a410000000a7d"))
				|| (ValueInfo.getWia_longaddress().equals("007a410000000ab2"))
				|| (ValueInfo.getWia_longaddress().equals("007a410000000a91"))))
			ValueInfo.setNetworkinfo(ValueInfo.getNetworkinfo() + ValueInfo.getWia_longaddress() + ",");
	}
}
