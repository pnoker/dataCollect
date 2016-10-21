package com.dact.collect;

import java.sql.SQLException;

import com.dact.pojo.BaseInfo;
import com.dact.util.DBtool;
import com.dact.util.PackageProcessor;
import com.dact.util.PrintUtil;

/**
 * @author Pnoker
 * @description 更新健康报文到达时间
 */
public class HealthDatagram {
	public HealthDatagram(PackageProcessor p, BaseInfo base) {
		DBtool dBtool = new DBtool();
		PrintUtil printUtil = new PrintUtil();
		if (p.bytesToString(2, 3).equals("0100")) {
			String sente = "update [network_restart] set status = 1,reachtime = getdate() where ipaddress = '"
					+ base.getIpaddress() + "'";
			printUtil.printTitle(sente);
			try {
				dBtool.executeUpdate(sente);
				dBtool.free();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
