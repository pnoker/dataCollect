package com.dact.dateType;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.util.DBtool;
import com.dact.util.LogWrite;
import com.dact.util.PackageProcessor;

public class RateDatagram {
	public void excuteRateDatagram(PackageProcessor p, BaseInfo base, LogWrite logWrite) {
		DBtool dBtool = new DBtool();
		int count = p.bytesToInt(2, 2);
		float receive_rate = 0;
		boolean isnew = true;
		String wia_longaddress = "", wia_shortaddress = "", sente = "";
		for (int i = 0; i < count; i++) {

			wia_shortaddress = p.bytesToString(2 + 2 * i, 3 + 2 * i);
			receive_rate = Float.parseFloat(p.bytesToString(4 + 2 * i, 5 + 2 * i)) / 1000;
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
			if (isnew) {
				sente = "insert into Adapter_gateway (longaddress,rate,reachtime) values ('" + wia_longaddress + "',"
						+ receive_rate + ",getdate())";
				try {
					dBtool.executeUpdate(sente);
				} catch (SQLException e) {
					logWrite.write(e.getMessage());
				}
			} else {
				sente = "update Adapter_gateway set rate = " + receive_rate
						+ ",reachtime = getdate() where longaddress = '" + wia_longaddress + "'";
				try {
					dBtool.executeUpdate(sente);
				} catch (SQLException e) {
					logWrite.write(e.getMessage());
				}
			}
		}
	}
}
