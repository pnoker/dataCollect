package com.dact.dateType;

import com.dact.pojo.BaseInfo;
import com.dact.util.LogWrite;
import com.dact.util.PackageProcessor;

public class RateDatagram {
	public void excuteRateDatagram(PackageProcessor p, BaseInfo base, LogWrite logWrite) {
		int count = p.bytesToInt(2, 2);
		float receive_rate = 0;
		for (int i = 0; i < count; i++) {

			String wia_shortaddress = p.bytesToString(2 + 2 * i, 3 + 2 * i);
			receive_rate = Float.parseFloat(p.bytesToString(4 + 2 * i, 5 + 2 * i)) / 1000;
			wia_longaddress = Mainthread.addressmap.get(wia_shortaddress + " " + ipaddress);

			sente = "update [Adapter_gateway] set rate = " + receive_rate + ",reachtime = getdate() where longaddress = '" + wia_longaddress + "'";
			System.out.println(sente + "报文0111" + ipaddress);
			dbtool.executeUpdate(sente);

		}
	}
}
