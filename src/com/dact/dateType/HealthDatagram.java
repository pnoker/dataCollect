package com.dact.dateType;

import java.sql.SQLException;
import java.util.Date;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.util.DBtool;
import com.dact.util.DateUtil;
import com.dact.util.LogWrite;
import com.dact.util.PackageProcessor;

/**
 * @author Pnoker
 * @description 处理健康报文，可以用于判断该网关是否正常
 */
public class HealthDatagram {
	/**
	 * 处理健康报文，01 0f 为该网关的健康报文，接收到该条报文时，就表名该网关正常，并更新最后一条健康报文到达的时间戳
	 * 
	 * @param p
	 * @param base
	 * @return updata,是否更新健康报文时间戳，false：否，true：需要更新健康报文的时间戳
	 */
	public boolean excuteHealthDatagram(PackageProcessor p, BaseInfo base, LogWrite logWrite) {
		boolean updata = false;
		DBtool dBtool = new DBtool();
		DateUtil dateUtil = new DateUtil();
		String shortAddress = p.bytesToString(2, 3);
		/* 短地址为0100，表示是该网关的健康报文 */
		if (shortAddress.equals("0100")) {
			String sente = "update [network_restart] set status = 1,reachtime = getdate() where ipaddress = '" + base.getIpaddress() + "'";
			MapInfo.gateway_currentime.put(base.getIpaddress(), (new Date()).getTime());
			try {
				logWrite.write("执行sql：" + sente);
				dBtool.executeUpdate(sente);
				dBtool.free();
			} catch (SQLException e) {
				logWrite.write("excuteHealthDatagram:" + e.getMessage());
			}
		} else if (shortAddress.equals("0000")) {
			// 备用短地址，不做任何操作
		} else {// 其他短地址，即：节点的短地址
			String longAddress = MapInfo.addressmap.get(shortAddress + " " + base.getIpaddress());
			if (longAddress != null) {// 长地址不为空
				logWrite.write("长地址和短地址对应关系---> " + longAddress + " -> " + shortAddress);
				updata = true;
			}
		}
		return updata;
	}
}
