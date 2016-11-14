package com.dact.dateType;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.util.LogWrite;
import com.dact.util.Oracle;
import com.dact.util.PackageProcessor;

public class RateDatagram {
	public void excuteRateDatagram(PackageProcessor p, BaseInfo base, LogWrite logWrite) {
		Oracle dBtool = new Oracle();
		int count = p.bytesToInt(2, 2);
		int receive_rate = 0;
		String wia_longaddress = "", wia_shortaddress = "", sente = "";
		for (int i = 0; i < count; i++) {
			boolean isnew = true;
			// 0111 07 0B00
			// 1027 1100 1027 3100 1027 3600 1027 3A00 1027 4500 1027 4800 1027
			// AEEA
			wia_shortaddress = p.bytesToString(3 + 4 * i, 4 + 4 * i);
			receive_rate = p.bytesToIntSmall(5 + 4 * i, 6 + 4 * i) / 100;
			wia_longaddress = MapInfo.addressmap.get(wia_shortaddress + " " + base.getIpaddress());
			sente = "select * from Adapter_gateway where longaddress = '" + wia_longaddress + "'";
			try {
				ResultSet rs = dBtool.executeQuery(sente);
				while (rs.next()) {
					isnew = false;
				}
			} catch (SQLException e) {
				logWrite.write(e.getMessage());
			}
			if (wia_longaddress == null) {
				isnew = false;
			}
			if (isnew) {
				sente = "insert into Adapter_gateway (longaddress,rate,reachtime) values ('" + wia_longaddress + "'," + receive_rate + ",getdate())";
				try {
					dBtool.executeUpdate(sente);
				} catch (SQLException e) {
					logWrite.write(e.getMessage());
				}
			} else {
				sente = "update Adapter_gateway set rate = " + receive_rate + ",reachtime = getdate() where longaddress = '" + wia_longaddress + "'";
				try {
					dBtool.executeUpdate(sente);
				} catch (SQLException e) {
					logWrite.write(e.getMessage());
				}
			}
		}
	}
}
