package com.dact.dateType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.util.Sqlserver;
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
		Sqlserver dBtool = new Sqlserver();
		DateUtil dateUtil = new DateUtil();
		String sql = "";
		String shortAddress = p.bytesToString(2, 3);
		/* 短地址为0100，表示是该网关的健康报文 */
		if (shortAddress.equals("0100")) {
			boolean isnew = true;
			MapInfo.gateway_currentime.put(base.getIpaddress(), (new Date()).getTime());
			sql = "select * from health where type = '网关' and name = '" + base.getIpaddress() + "'";
			try {
				ResultSet rs = dBtool.executeQuery(sql);
				while (rs.next()) {
					isnew = false;
				}
			} catch (SQLException e) {
				logWrite.write(e.getMessage());
			}
			if (isnew) {
				sql = "insert into health (name,type,signal,reachtime) values ('" + base.getIpaddress() + "','网关',36,getdate())";
				try {
					dBtool.executeUpdate(sql);
				} catch (SQLException e) {
					logWrite.write(e.getMessage());
				}
			} else {
				sql = "update health set signal = 100 ,reachtime = getdate() where name = '" + base.getIpaddress() + "' and type = '网关'";
				try {
					dBtool.executeUpdate(sql);
				} catch (SQLException e) {
					logWrite.write(e.getMessage());
				}
			}
		} else if (shortAddress.equals("0000")) {
			// 备用短地址，不做任何操作
		} else {// 其他短地址，即：节点的短地址
			String longAddress = MapInfo.addressmap.get(shortAddress + " " + base.getIpaddress());
			if ((longAddress.equals("007a410000000a7e")) || (longAddress.equals("e025000000417a00")) || (longAddress.equals("0326000000417a00")) || (longAddress.equals("d225000000417a00"))) {
				longAddress = null;
			}
			if (longAddress != null) {// 长地址不为空
				boolean isnew = true;
				int signal = p.bytesToInt(5, 5);
				if (signal > 127) {
					signal = 127 - signal;
				}
				logWrite.write("长地址和短地址对应关系---> " + longAddress + " -> " + shortAddress);
				sql = "select * from health where type = '适配器' and name = '" + longAddress + "'";
				try {
					ResultSet rs = dBtool.executeQuery(sql);
					while (rs.next()) {
						isnew = false;
					}
				} catch (SQLException e) {
					logWrite.write(e.getMessage());
				}
				if (isnew) {
					sql = "insert into health (name,type,signal,reachtime) values ('" + longAddress + "','适配器'," + signal + ",getdate())";
					try {
						dBtool.executeUpdate(sql);
					} catch (SQLException e) {
						logWrite.write(e.getMessage());
					}
				} else {
					sql = "update health set signal = " + signal + " ,reachtime = getdate() where name = '" + longAddress + "' and type = '适配器'";
					try {
						dBtool.executeUpdate(sql);
					} catch (SQLException e) {
						logWrite.write(e.getMessage());
					}
				}
				updata = true;
			}
		}
		return updata;
	}
}
