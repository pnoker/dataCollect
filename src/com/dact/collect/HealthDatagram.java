package com.dact.collect;

import java.sql.SQLException;
import java.util.Date;

import com.dact.pojo.BaseInfo;
import com.dact.util.DBtool;
import com.dact.util.DateUtil;
import com.dact.util.LogWrite;
import com.dact.util.PackageProcessor;
import com.dact.util.PrintUtil;

/**
 * @author Pnoker
 * @description 处理健康报文，可以用于判断该网关是否正常
 */
public class HealthDatagram {
	private boolean updata = false;

	/**
	 * 处理健康报文，01 83 01 00 为该网关的健康报文，接收到该条报文时，就表名该网关正常，并更新最后一条健康报文到达的时间戳
	 * 
	 * @param p
	 * @param base
	 * @return updata,是否更新健康报文时间戳，false：否，true：需要更新健康报文的时间戳
	 */
	public boolean excuteHealthDatagram(PackageProcessor p, BaseInfo base, LogWrite logWrite) {
		DBtool dBtool = new DBtool();
		PrintUtil printUtil = new PrintUtil();
		DateUtil dateUtil = new DateUtil();
		if (p.bytesToString(2, 3).equals("0100")) {
			String sente = "update [network_restart] set status = 1,reachtime = getdate() where ipaddress = '" + base.getIpaddress() + "'";
			logWrite.write("更新网关：" + base.getIpaddress() + "的健康报文时间戳为：" + dateUtil.getCompleteTime(new Date()));
			printUtil.printDetail(base.getIpaddress(), "更新网关：" + base.getIpaddress() + "的健康报文时间戳为：" + dateUtil.getCompleteTime(new Date()));
			updata = true;
			try {
				logWrite.write("执行sql：" + sente);
				printUtil.printDetail(base.getIpaddress(), "执行sql：" + sente);
				dBtool.executeUpdate(sente);
				dBtool.free();
			} catch (SQLException e) {
				logWrite.write("【 Error!】HealthDatagram.excuteHealthDatagram：" + e.getMessage());
				printUtil.printDetail(base.getIpaddress(), "【 Error!】HealthDatagram.excuteHealthDatagram：" + e.getMessage());
			}
		}
		return updata;
	}
}
