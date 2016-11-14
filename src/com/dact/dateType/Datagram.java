package com.dact.dateType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.util.Sqlserver;
import com.dact.util.LogWrite;
import com.dact.util.PackageProcessor;
import com.dact.util.RateUtil;

public class Datagram {
	public void excuteDatagram(PackageProcessor p, BaseInfo base, LogWrite logWrite) {
		Sqlserver dBtool = new Sqlserver();
		RateUtil rateUtil = new RateUtil();
		String wia_longaddress, wia_shortaddress, deviceType, shuiInfo, hartaddress = "";
		String[] infoArr, eachArr;
		int interval = 0, serial;
		float shuiliuliang, dianya, firstvalue, secondvalue, thirdvalue, fourthvalue = 0;
		Date lastime, currentime;

		wia_shortaddress = p.bytesToString(2, 3);
		wia_longaddress = MapInfo.addressmap.get(wia_shortaddress + " " + base.getIpaddress());
		deviceType = MapInfo.typemap.get(wia_longaddress);
		/* 当前数据报文的序列号 */
		serial = p.bytesToIntSmall(4, 7);

		logWrite.write("长地址：" + wia_longaddress);
		logWrite.write("短地址：" + wia_shortaddress);

		/* 无线IO类型,7400 */
		if (p.bytesToString(8, 9).equals("7400")) {
			/* modbus数据类型，01 */
			if (p.bytesToString(10, 10).equals("01")) {
				/* 水表 */
				if (deviceType.equals("0e00")) {
					logWrite.write("水表");
					rateUtil.rate(wia_shortaddress, wia_longaddress, base.getIpaddress(), serial, logWrite);

					shuiInfo = MapInfo.shui_map.get(wia_longaddress);
					shuiliuliang = p.bytesToFloatSmall(11, 14);
					logWrite.write("水表数据：" + shuiInfo + "=" + shuiliuliang);
					String sente = "insert into [shui_data](typeserial,tag, value,reachtime)values('" + shuiInfo + "',0," + shuiliuliang + ",getdate())";
					logWrite.write("向数据库表shui_data中添加一条数据：" + shuiInfo + "=" + shuiliuliang);
					try {
						logWrite.write("执行sql：" + sente);
						dBtool.executeUpdate(sente);
					} catch (SQLException e) {
						logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
					}

					sente = "update [shui_opc] set value = " + shuiliuliang + ",reachtime = getdate() where typeserial = '" + shuiInfo + "_bt' and tag = 0";
					logWrite.write("更新当前数据库表shui_opc中的表头值：" + shuiInfo + "=" + shuiliuliang);
					try {
						logWrite.write("执行sql：" + sente);
						dBtool.executeUpdate(sente);
					} catch (SQLException e) {
						logWrite.write("【 Error!】Datagram.excuteDatagram.2：" + e.getMessage());
					}
					if (shuiInfo.equals("sia0001")) {
						shuiliuliang += 42959;
					} else if (shuiInfo.equals("sia0002")) {
						shuiliuliang += 44165;
					} else if (shuiInfo.equals("sia0003")) {
						shuiliuliang += 1696.7;
					} else if (shuiInfo.equals("sia0004")) {
						shuiliuliang += 1821;
					} else if (shuiInfo.equals("sia0005")) {
						shuiliuliang += 357.6;
					} else if (shuiInfo.equals("sia0006")) {
						shuiliuliang += 3280;
					} else if (shuiInfo.equals("sia0007")) {
						shuiliuliang += 608;
					}
					sente = "with table1 as(select DATEDIFF(HOUR,reachtime,GETDATE()) as hours,value from [shui_opc] where typeserial = '" + shuiInfo + "') ";
					sente += "update [shui_opc] set value = (select (" + shuiliuliang + "-table1.value)/table1.hours from table1)  where typeserial = '" + shuiInfo + "_0' and tag = 0";
					logWrite.write("更新当前数据库表shui_opc中的瞬时值：" + shuiInfo + "=" + shuiliuliang);
					try {
						logWrite.write("执行sql：" + sente);
						dBtool.executeUpdate(sente);
					} catch (SQLException e) {
						logWrite.write("【 Error!】Datagram.excuteDatagram.3：" + e.getMessage());
					}
					sente = "update [shui_opc] set value = " + shuiliuliang + ",reachtime = getdate() where typeserial = '" + shuiInfo + "' and tag = 0";
					logWrite.write("更新当前数据库表shui_opc中的累计值：" + shuiInfo + "=" + shuiliuliang);
					try {
						logWrite.write("执行sql：" + sente);
						dBtool.executeUpdate(sente);
					} catch (SQLException e) {
						logWrite.write("【 Error!】Datagram.excuteDatagram.4：" + e.getMessage());
					}
				}
				/* 无线表 */
				else {
					logWrite.write("无线表");
					String slaveID = null;
					/* 从站地址 */
					slaveID = p.bytesToString(11, 11);

					wia_longaddress = wia_longaddress + " " + slaveID;
					logWrite.write("从站地址：" + slaveID);
					lastime = MapInfo.wirelessio_currentime.get(wia_longaddress);
					rateUtil.rate(wia_shortaddress, wia_longaddress, base.getIpaddress(), serial, logWrite);

					if (lastime != null) {
						currentime = new Date();
						interval = getIntervalSeconds(lastime, currentime);

						if (interval >= 30) {
							MapInfo.wirelessio_currentime.put(wia_longaddress, currentime);
							infoArr = MapInfo.wirelessio_map.get(wia_longaddress).split(",");
							String sente = "insert into [" + infoArr[1] + "_data]values('" + infoArr[0] + "',";
							for (int i = 2; i < infoArr.length; i++) {

								eachArr = infoArr[i].split(" ");
								if (eachArr[3].contains("int")) {
									float tep_int = p.bytesToFloat(Integer.parseInt(eachArr[1]), Integer.parseInt(eachArr[2]));
									sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "'," + (i - 2) + "," + tep_int + ",getdate())";
									logWrite.write("向数据库表" + infoArr[1] + "_data中添加一条数据：" + infoArr[0] + "=" + tep_int);
									try {
										logWrite.write("执行sql：" + sente);
										dBtool.executeUpdate(sente);
									} catch (SQLException e) {
										logWrite.write("【 Error!】Datagram.excuteDatagram.5：" + e.getMessage());
									}
								}
								if (eachArr[3].contains("long")) {
									long tep_int = p.bytesToLong(Integer.parseInt(eachArr[1]), Integer.parseInt(eachArr[2]));
									if (i - 2 == 0) {
										tep_int = (long) (tep_int * 0.0000001);
									} else if (i - 2 == 1) {
										tep_int = (long) (tep_int * 0.1);
									} else {
									}
									sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "'," + (i - 2) + "," + tep_int + ",getdate())";
									try {
										logWrite.write("执行sql：" + sente);
										dBtool.executeUpdate(sente);
									} catch (SQLException e) {
										logWrite.write("【 Error!】Datagram.excuteDatagram.6：" + e.getMessage());
									}
									sente = "update [shui_opc] set value = " + tep_int + ",reachtime = getdate() where typeserial =  '" + infoArr[0] + "_" + (i - 2) + "'";
									try {
										logWrite.write("执行sql：" + sente);
										dBtool.executeUpdate(sente);
									} catch (SQLException e) {
										logWrite.write("【 Error!】Datagram.excuteDatagram.7：" + e.getMessage());
									}
								}
							}
						}
					}
				}
			}
			/* AI数据类型，02 */
			else if (p.bytesToString(10, 10).equals("02")) {
				logWrite.write("AI数据类型");
				if (deviceType.equals("0e00")) {
					logWrite.write("设备类型为，0e00，水表");
					rateUtil.rate(wia_shortaddress, wia_longaddress, base.getIpaddress(), serial, logWrite);

					shuiInfo = MapInfo.shui_map.get(wia_longaddress);
					int dianya_tmp = p.doublebytesToInt(11, 12);
					dianya = (float) dianya_tmp / 100;
					logWrite.write("水表电压数据：" + shuiInfo + "=" + dianya);

					String sente = "insert into [dianya_data](typeserial,tag, value,reachtime)values('" + shuiInfo + "',0," + dianya + ",getdate())";
					try {
						logWrite.write("执行sql：" + sente);
						dBtool.executeUpdate(sente);
					} catch (SQLException e) {
						logWrite.write("【 Error!】Datagram.excuteDatagram.8：" + e.getMessage());
					}
				} else if (deviceType.equals("0c00")) {
					logWrite.write("设备类型为，0c00，无线表");
					rateUtil.rate(wia_shortaddress, wia_longaddress, base.getIpaddress(), serial, logWrite);

					shuiInfo = MapInfo.shui_map.get(wia_longaddress);
					int dianya_tmp = p.doublebytesToInt(11, 12);
					float tem1 = p.bytesToFloatSmall(12, 15);
					float tem2 = p.bytesToFloatSmall(17, 20);
					dianya = (float) dianya_tmp / 100;
					logWrite.write("通道0数据，温度数据：" + shuiInfo + "=" + tem1);
					logWrite.write("通道1数据，温度数据：" + shuiInfo + "=" + tem2);
					String sente = "insert into [dianya_data](typeserial,tag, value,reachtime)values('" + shuiInfo + "',0," + dianya + ",getdate())";
					try {
						logWrite.write("执行sql：" + sente);
						dBtool.executeUpdate(sente);
					} catch (SQLException e) {
						logWrite.write("【 Error!】Datagram.excuteDatagram.8：" + e.getMessage());
					}
				}

			}
			/* 开润开封数据类型 */
			else if (p.bytesToString(10, 10).equals("07")) {
				logWrite.write("开润开封数据类型");
				rateUtil.rate(wia_shortaddress, wia_longaddress, base.getIpaddress(), serial, logWrite);

				String runInfo = MapInfo.shui_map.get(wia_longaddress);
				if (runInfo == null) {

				}
				int tag = p.bytesToInt(12, 12);
				float kairun = 0;
				int d0 = 0, d1 = 0, d2 = 0, d3 = 0, d4 = 0;

				switch (tag) {
				case 0:
					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);
					d2 = p.bytesToTen(15, 15);
					d3 = p.bytesToTen(16, 16);

					kairun = d2 * 10000 + d1 * 100 + d0;
					for (int i = 0; i < d3 - 5; i++) {
						kairun = (float) (kairun * 0.1);
					}
					logWrite.write("流量：" + kairun);
					break;
				case 1:
					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);
					d2 = p.bytesToTen(15, 15);

					kairun = d2 * 10000 + d1 * 100 + d0;
					logWrite.write("流速：" + kairun);
					break;
				case 2:
					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);

					kairun = d1 * 100 + d0;
					logWrite.write("流量百分比：" + kairun);
					break;
				case 3:
					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);

					kairun = d1 * 100 + d0;
					logWrite.write("流体电阻值：" + kairun);
					break;
				case 4:
					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);
					d2 = p.bytesToTen(15, 15);
					d3 = p.bytesToTen(16, 16);
					d4 = p.bytesToTen(17, 17);
					kairun = d4 * 1000000 + d3 * 1000000 + d2 * 10000 + d1 * 100 + d0;
					logWrite.write("正向总量：" + kairun);
					break;
				case 5:
					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);
					d2 = p.bytesToTen(15, 15);
					d3 = p.bytesToTen(16, 16);
					d4 = p.bytesToTen(17, 17);

					kairun = d4 * 1000000 + d3 * 1000000 + d2 * 10000 + d1 * 100 + d0;
					logWrite.write("反向总量：" + kairun);
					break;
				case 6:
					logWrite.write("报警状态：" + kairun);
					break;
				case 7:
					logWrite.write("管道直径：" + kairun);
					break;

				default:
					logWrite.write("没有符合任何命令：" + kairun);
					break;
				}

				String sente = "insert into [kairun_data](typeserial,tag, value,reachtime)values('" + runInfo + "'," + tag + "," + kairun + ",getdate())";
				try {
					logWrite.write("执行sql：" + sente);
					dBtool.executeUpdate(sente);
				} catch (SQLException e) {
					logWrite.write("【 Error!】Datagram.excuteDatagram.9：" + e.getMessage());
				}

				sente = "update [shui_opc] set value = " + kairun + ",reachtime = getdate() where typeserial = '" + runInfo + "_" + tag + "'";
				try {
					logWrite.write("执行sql：" + sente);
					dBtool.executeUpdate(sente);
				} catch (SQLException e) {
					logWrite.write("【 Error!】Datagram.excuteDatagram.10：" + e.getMessage());
				}
			}
			/* PI数据类型，04 */
			else if (p.bytesToString(10, 10).equals("04")) {
				logWrite.write("PI数据类型");
				rateUtil.rate(wia_shortaddress, wia_longaddress, base.getIpaddress(), serial, logWrite);
			}
			/* DLT数据类型 */
			else if (p.bytesToString(10, 10).equals("03")) {
				logWrite.write("DLT数据类型 ");
			}
		}
		/* hart类型数据 */
		else {
			logWrite.write("hart类型数据");
			rateUtil.rate(wia_shortaddress, wia_longaddress, base.getIpaddress(), serial, logWrite);

			int i = 8;
			int sure = 0;
			boolean flag = true;
			while (flag) {
				if (p.bytesToString(i, i).equals("86")) {
					sure = i;
					flag = false;
				}
				i++;
			}
			lastime = MapInfo.hart_currentime.get(wia_longaddress);
			if (lastime != null) {
				currentime = new Date();
				interval = getIntervalSeconds(lastime, currentime);

				if (interval > 10) {
					hartaddress = p.bytesToString(sure + 1, sure + 5);
					// 命令号
					String commandnum = p.bytesToString(sure + 6, sure + 6);
					logWrite.write("命令号：" + commandnum + "号");
					if (commandnum.equals("03")) {

						infoArr = MapInfo.hart_map.get(wia_longaddress).split(" ");
						firstvalue = p.bytesToFloat3(sure + 15, sure + 18);
						logWrite.write("主变量值是：" + firstvalue);
						secondvalue = p.bytesToFloat3(sure + 20, sure + 23);
						logWrite.write("第二变量值是：" + secondvalue);
						if (infoArr[2].equals("false") && infoArr[3].equals("false")) {
							String sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',0,'" + firstvalue + "',getdate())";
							try {
								logWrite.write("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								logWrite.write("【 Error!】Datagram.excuteDatagram.11：" + e.getMessage());
							}
							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',1,'" + secondvalue + "',getdate())";
							try {
								logWrite.write("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								logWrite.write("【 Error!】Datagram.excuteDatagram.12：" + e.getMessage());
							}

						}

						else if (infoArr[2].equals("true") && infoArr[3].equals("false")) {
							thirdvalue = p.bytesToFloat3(sure + 25, sure + 28);
							String sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',0,'" + firstvalue + "',getdate())";
							try {
								logWrite.write("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								logWrite.write("【 Error!】Datagram.excuteDatagram.13：" + e.getMessage());
							}
							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',1,'" + secondvalue + "',getdate())";
							try {
								logWrite.write("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								logWrite.write("【 Error!】Datagram.excuteDatagram.14：" + e.getMessage());
							}
							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',2,'" + thirdvalue + "',getdate())";
							try {
								logWrite.write("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								logWrite.write("【 Error!】Datagram.excuteDatagram.15：" + e.getMessage());
							}
						}

						else if (infoArr[2].equals("true") && infoArr[3].equals("true")) {

							thirdvalue = p.bytesToFloat3(sure + 25, sure + 28);
							fourthvalue = p.bytesToFloat3(sure + 30, sure + 33);
							String sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',0,'" + firstvalue + "',getdate())";
							try {
								logWrite.write("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								logWrite.write("【 Error!】Datagram.excuteDatagram.16：" + e.getMessage());
							}

							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',1,'" + secondvalue + "',getdate())";
							try {
								logWrite.write("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								logWrite.write("【 Error!】Datagram.excuteDatagram.17：" + e.getMessage());
							}

							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',2," + thirdvalue + ",getdate())";
							try {
								logWrite.write("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								logWrite.write("【 Error!】Datagram.excuteDatagram.18：" + e.getMessage());
							}

							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',3,'" + fourthvalue + "',getdate())";
							try {
								logWrite.write("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								logWrite.write("【 Error!】Datagram.excuteDatagram.19：" + e.getMessage());
							}
						}
					}
				}
			}

		}
		try {
			dBtool.free();
		} catch (SQLException e) {
			logWrite.write("【 Error!】Datagram.excuteDatagram.23：" + e.getMessage());
		}
	}

	public int getIntervalSeconds(Date lastime, Date currentime) {
		long sl = lastime.getTime();
		long el = currentime.getTime();
		long ei = el - sl;
		return (int) (ei / (1000));
	}
}
