package com.dact.collect;

import java.sql.SQLException;
import java.util.Date;

import com.dact.pojo.BaseInfo;
import com.dact.util.DBtool;
import com.dact.util.DateUtil;
import com.dact.util.PackageProcessor;
import com.dact.util.PrintUtil;

/**
 * @author Pnoker
 * @description 更新健康报文到达时间
 */
public class HealthDatagram {
	public void excuteHealthDatagram(PackageProcessor p, BaseInfo base) {
		DBtool dBtool = new DBtool();
		PrintUtil printUtil = new PrintUtil();
		DateUtil dateUtil = new DateUtil();
		if (p.bytesToString(2, 3).equals("0100")) {
			String sente = "update [network_restart] set status = 1,reachtime = getdate() where ipaddress = '" + base.getIpaddress() + "'";
			printUtil.printTitle("更新网关：" + base.getIpaddress() + "的健康报文时间戳为：" + dateUtil.getCompleteTime(new Date()));
			try {
				printUtil.printTitle("执行sql：" + sente);
				dBtool.executeUpdate(sente);
				dBtool.free();
			} catch (SQLException e) {
				printUtil.printTitle("【 Error!】HealthDatagram.excuteHealthDatagram：" + e.getMessage());
			}
		}
	}
}
