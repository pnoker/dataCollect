package com.dact.dateType;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.util.LogWrite;
import com.dact.util.PackageProcessor;

public class RateDatagram {
	public void excuteRateDatagram(PackageProcessor p, BaseInfo base, LogWrite logWrite) {
		int count = p.bytesToInt(2, 2);
		float receive_rate = 0;
		String wia_longaddress = "", wia_shortaddress = "", deviceType = "";
		for (int i = 0; i < count; i++) {

			wia_shortaddress = p.bytesToString(2 + 2 * i, 3 + 2 * i);
			receive_rate = Float.parseFloat(p.bytesToString(4 + 2 * i, 5 + 2 * i)) / 1000;
			wia_longaddress = MapInfo.addressmap.get(wia_shortaddress + " " + base.getIpaddress());

			sente = "update [Adapter_gateway] set rate = " + receive_rate + ",reachtime = getdate() where longaddress = '" + wia_longaddress + "'";
			System.out.println(sente + "报文0111" + base.getIpaddress());
			dbtool.executeUpdate(sente);

		}
	}
}
