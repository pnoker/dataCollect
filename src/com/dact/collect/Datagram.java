package com.dact.collect;

import java.sql.SQLException;
import java.util.Date;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.util.DBtool;
import com.dact.util.PackageProcessor;
import com.dact.util.PrintUtil;

public class Datagram {
	public void excuteDatagram(PackageProcessor p, BaseInfo base) {
		DBtool dBtool = new DBtool();
		PrintUtil printUtil = new PrintUtil();
		String wia_longaddress, wia_shortaddress, typeofwatch, networkinfo, shuiInfo, hartaddress = "";
		String[] infoArr, eachArr;
		int interval = 0;
		float shuiliuliang, dianya, firstvalue, secondvalue, thirdvalue, fourthvalue = 0;
		Date lastime, currentime;

		wia_shortaddress = p.bytesToString(2, 3);
		wia_longaddress = MapInfo.addressmap.get(wia_shortaddress + " " + base.getIpaddress());
		typeofwatch = MapInfo.typemap.get(wia_longaddress);

		printUtil.printTitle("网关:" + base.getIpaddress() + ",长地址：" + wia_longaddress);
		printUtil.printTitle("网关:" + base.getIpaddress() + ",短地址：" + wia_shortaddress);
		printUtil.printTitle("网关:" + base.getIpaddress() + ",设备类型：" + typeofwatch);

		if (p.bytesToString(8, 9).equals("7400")) {

			if (p.bytesToString(10, 10).equals("01")) {
				if (typeofwatch.equals("000e")) {
					shuiInfo = MapInfo.shui_map.get(wia_longaddress);
					shuiliuliang = p.bytesToFloatSmall(11, 14);
					printUtil.printTitle("网关:" + base.getIpaddress() + ",水表数据：" + shuiInfo + "=" + shuiliuliang);
					String sente = "insert into [shui_data](typeserial,tag, value,reachtime)values('" + shuiInfo + "',0," + shuiliuliang + ",getdate())";
					printUtil.printTitle("网关:" + base.getIpaddress() + ",向数据库表shui_data中添加一条数据：" + shuiInfo + "=" + shuiliuliang);
					try {
						printUtil.printTitle("执行sql：" + sente);
						dBtool.executeUpdate(sente);
					} catch (SQLException e) {
						printUtil.printTitle("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
					}

					sente = "update [shui_opc] set value = " + shuiliuliang + ",reachtime = getdate() where typeserial = '" + shuiInfo + "_bt' and tag = 0";
					printUtil.printTitle("网关:" + base.getIpaddress() + ",更新当前数据库表shui_opc中的表头值：" + shuiInfo + "=" + shuiliuliang);
					try {
						printUtil.printTitle("执行sql：" + sente);
						dBtool.executeUpdate(sente);
					} catch (SQLException e) {
						printUtil.printTitle("【 Error!】Datagram.excuteDatagram.2：" + e.getMessage());
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
					printUtil.printTitle("网关:" + base.getIpaddress() + ",更新当前数据库表shui_opc中的瞬时值：" + shuiInfo + "=" + shuiliuliang);
					try {
						printUtil.printTitle("执行sql：" + sente);
						dBtool.executeUpdate(sente);
					} catch (SQLException e) {
						printUtil.printTitle("【 Error!】Datagram.excuteDatagram.3：" + e.getMessage());
					}
					sente = "update [shui_opc] set value = " + shuiliuliang + ",reachtime = getdate() where typeserial = '" + shuiInfo + "' and tag = 0";
					printUtil.printTitle("网关:" + base.getIpaddress() + ",更新当前数据库表shui_opc中的累计值：" + shuiInfo + "=" + shuiliuliang);
					try {
						printUtil.printTitle("执行sql：" + sente);
						dBtool.executeUpdate(sente);
					} catch (SQLException e) {
						printUtil.printTitle("【 Error!】Datagram.excuteDatagram.4：" + e.getMessage());
					}

				} else {
					int tagtouse = 0;
					int used = 0;
					String slaveID = null;
					slaveID = p.bytesToString(11, 11);

					wia_longaddress = wia_longaddress + " " + slaveID;
					lastime = MapInfo.wirelessio_currentime.get(wia_longaddress);

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
									printUtil.printTitle("网关:" + base.getIpaddress() + ",向数据库表" + infoArr[1] + "_data中添加一条数据：" + infoArr[0] + "=" + tep_int);
									try {
										printUtil.printTitle("执行sql：" + sente);
										dBtool.executeUpdate(sente);
									} catch (SQLException e) {
										printUtil.printTitle("【 Error!】Datagram.excuteDatagram.5：" + e.getMessage());
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
										printUtil.printTitle("执行sql：" + sente);
										dBtool.executeUpdate(sente);
									} catch (SQLException e) {
										printUtil.printTitle("【 Error!】Datagram.excuteDatagram.6：" + e.getMessage());
									}
									sente = "update [shui_opc] set value = " + tep_int + ",reachtime = getdate() where typeserial =  '" + infoArr[0] + "_" + (i - 2) + "'";
									try {
										printUtil.printTitle("执行sql：" + sente);
										dBtool.executeUpdate(sente);
									} catch (SQLException e) {
										printUtil.printTitle("【 Error!】Datagram.excuteDatagram.7：" + e.getMessage());
									}
								}
							}
						}
					}

				}

			} else if (p.bytesToString(10, 10).equals("02")) {
				if (typeofwatch.equals("000e")) {
					shuiInfo = MapInfo.shui_map.get(wia_longaddress);
					int dianya_tmp = p.doublebytesToInt(11, 12);
					dianya = (float) dianya_tmp / 100;
					printUtil.printTitle("网关:" + base.getIpaddress() + ",水表电压数据：" + shuiInfo + "=" + dianya);

					String sente = "insert into [dianya_data](typeserial,tag, value,reachtime)values('" + shuiInfo + "',0," + dianya + ",getdate())";
					try {
						printUtil.printTitle("执行sql：" + sente);
						dBtool.executeUpdate(sente);
					} catch (SQLException e) {
						printUtil.printTitle("【 Error!】Datagram.excuteDatagram.8：" + e.getMessage());
					}
				}

			} else if (p.bytesToString(10, 10).equals("07")) {
				printUtil.printTitle("网关:" + base.getIpaddress() + ",开润开封协议");
				String runInfo = MapInfo.shui_map.get(wia_longaddress);
				if (runInfo == null) {
					
				}
				int tag = p.bytesToInt(12, 12);
				float kairun = 0;
				int d0, d1, d2, d3, d4, d5, d6, d7 = 0;

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
					printUtil.printTitle("网关:" + base.getIpaddress() + ",流量：" + kairun);
					break;
				case 1:
					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);
					d2 = p.bytesToTen(15, 15);

					kairun = d2 * 10000 + d1 * 100 + d0;
					printUtil.printTitle("网关:" + base.getIpaddress() + ",流速：" + kairun);
					break;
				case 2:
					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);

					kairun = d1 * 100 + d0;
					printUtil.printTitle("网关:" + base.getIpaddress() + ",流量百分比：" + kairun);
					break;
				case 3:
					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);

					kairun = d1 * 100 + d0;
					printUtil.printTitle("网关:" + base.getIpaddress() + ",流体电阻值：" + kairun);
					break;
				case 4:
					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);
					d2 = p.bytesToTen(15, 15);
					d3 = p.bytesToTen(16, 16);
					d4 = p.bytesToTen(17, 17);
					kairun = d4 * 1000000 + d3 * 1000000 + d2 * 10000 + d1 * 100 + d0;
					printUtil.printTitle("网关:" + base.getIpaddress() + ",正向总量：" + kairun);
					break;
				case 5:
					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);
					d2 = p.bytesToTen(15, 15);
					d3 = p.bytesToTen(16, 16);
					d4 = p.bytesToTen(17, 17);

					kairun = d4 * 1000000 + d3 * 1000000 + d2 * 10000 + d1 * 100 + d0;
					printUtil.printTitle("网关:" + base.getIpaddress() + ",反向总量：" + kairun);
					break;
				case 6:
					printUtil.printTitle("网关:" + base.getIpaddress() + ",报警状态：" + kairun);
					break;
				case 7:
					printUtil.printTitle("网关:" + base.getIpaddress() + ",管道直径：" + kairun);
					break;

				default:
					printUtil.printTitle("网关:" + base.getIpaddress() + ",没有符合任何命令：" + kairun);
					break;
				}

				String sente = "insert into [kairun_data](typeserial,tag, value,reachtime)values('" + runInfo + "'," + tag + "," + kairun + ",getdate())";
				try {
					printUtil.printTitle("执行sql：" + sente);
					dBtool.executeUpdate(sente);
				} catch (SQLException e) {
					printUtil.printTitle("【 Error!】Datagram.excuteDatagram.9：" + e.getMessage());
				}

				sente = "update [shui_opc] set value = " + kairun + ",reachtime = getdate() where typeserial = '" + runInfo + "_" + tag + "'";
				try {
					printUtil.printTitle("执行sql：" + sente);
					dBtool.executeUpdate(sente);
				} catch (SQLException e) {
					printUtil.printTitle("【 Error!】Datagram.excuteDatagram.10：" + e.getMessage());
				}

			} else if (p.bytesToString(10, 10).equals("04")) {
				printUtil.printTitle("网关:" + base.getIpaddress() + ",PI数据");

			}

		} else {
			printUtil.printTitle("网关:" + base.getIpaddress() + ",hart数据");
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
					// hart设备长地址
					hartaddress = p.bytesToString(sure + 1, sure + 5);
					printUtil.printTitle("网关:" + base.getIpaddress() + ",hart设备长地址：" + hartaddress);
					// 命令号
					String commandnum = p.bytesToString(sure + 6, sure + 6);
					printUtil.printTitle("网关:" + base.getIpaddress() + ",命令号：" + commandnum + "号");
					if (commandnum.equals("03")) {

						infoArr = MapInfo.hart_map.get(wia_longaddress).split(" ");
						firstvalue = p.bytesToFloat3(sure + 15, sure + 18);
						printUtil.printTitle("网关:" + base.getIpaddress() + ",主变量值是：" + firstvalue);
						int secondunit = p.bytesToInt(sure + 19, sure + 19);
						secondvalue = p.bytesToFloat3(sure + 20, sure + 23);
						printUtil.printTitle("网关:" + base.getIpaddress() + ",第二变量值是：" + secondvalue);
						if (infoArr[2].equals("false") && infoArr[3].equals("false")) {
							String sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',0,'" + firstvalue + "',getdate())";
							try {
								printUtil.printTitle("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								printUtil.printTitle("【 Error!】Datagram.excuteDatagram.11：" + e.getMessage());
							}
							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',1,'" + secondvalue + "',getdate())";
							try {
								printUtil.printTitle("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								printUtil.printTitle("【 Error!】Datagram.excuteDatagram.12：" + e.getMessage());
							}

						}

						else if (infoArr[2].equals("true") && infoArr[3].equals("false")) {
							thirdvalue = p.bytesToFloat3(sure + 25, sure + 28);
							String sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',0,'" + firstvalue + "',getdate())";
							try {
								printUtil.printTitle("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								printUtil.printTitle("【 Error!】Datagram.excuteDatagram.13：" + e.getMessage());
							}
							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',1,'" + secondvalue + "',getdate())";
							try {
								printUtil.printTitle("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								printUtil.printTitle("【 Error!】Datagram.excuteDatagram.14：" + e.getMessage());
							}
							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',2,'" + thirdvalue + "',getdate())";
							try {
								printUtil.printTitle("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								printUtil.printTitle("【 Error!】Datagram.excuteDatagram.15：" + e.getMessage());
							}
						}

						else if (infoArr[2].equals("true") && infoArr[3].equals("true")) {

							thirdvalue = p.bytesToFloat3(sure + 25, sure + 28);
							fourthvalue = p.bytesToFloat3(sure + 30, sure + 33);
							String sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',0,'" + firstvalue + "',getdate())";
							try {
								printUtil.printTitle("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								printUtil.printTitle("【 Error!】Datagram.excuteDatagram.16：" + e.getMessage());
							}

							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',1,'" + secondvalue + "',getdate())";
							try {
								printUtil.printTitle("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								printUtil.printTitle("【 Error!】Datagram.excuteDatagram.17：" + e.getMessage());
							}

							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',2," + thirdvalue + ",getdate())";
							try {
								printUtil.printTitle("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								printUtil.printTitle("【 Error!】Datagram.excuteDatagram.18：" + e.getMessage());
							}

							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',3,'" + fourthvalue + "',getdate())";
							try {
								printUtil.printTitle("执行sql：" + sente);
								dBtool.executeUpdate(sente);
							} catch (SQLException e) {
								printUtil.printTitle("【 Error!】Datagram.excuteDatagram.19：" + e.getMessage());
							}

							// opc相关
							if ((infoArr[0].equals("T1线")) || (infoArr[0].equals("T2线")) || (infoArr[0].equals("食堂用天然气"))) {
								sente = "update [hart01_opc] set flow1 = " + firstvalue + ",flow2 = " + secondvalue + ",flow3 = " + thirdvalue + ",total = " + fourthvalue + " where typeserial = '"
										+ MapInfo.weihao_map.get(infoArr[0]) + "'";
								printUtil.printTitle("网关:" + base.getIpaddress() + ",更新当前数据库表hart01_opc中的累计值：" + MapInfo.weihao_map.get(infoArr[0]) + "=" + firstvalue + "," + secondvalue + ","
										+ thirdvalue + "," + fourthvalue);
								try {
									printUtil.printTitle("执行sql：" + sente);
									dBtool.executeUpdate(sente);
								} catch (SQLException e) {
									printUtil.printTitle("【 Error!】Datagram.excuteDatagram.20：" + e.getMessage());
								}
							} else if ((infoArr[0].equals("焦化蜡油线")) || (infoArr[0].equals("20线"))) {
								sente = "update [hart02_opc] set density = " + firstvalue + ",temp = " + secondvalue + ",flow = " + thirdvalue + ",total = " + fourthvalue + " where typeserial = '"
										+ MapInfo.weihao_map.get(infoArr[0]) + "'";
								printUtil.printTitle("网关:" + base.getIpaddress() + ",更新当前数据库表hart02_opc中的累计值：" + MapInfo.weihao_map.get(infoArr[0]) + "=" + firstvalue + "," + secondvalue + ","
										+ thirdvalue + "," + fourthvalue);
								try {
									printUtil.printTitle("执行sql：" + sente);
									dBtool.executeUpdate(sente);
								} catch (SQLException e) {
									printUtil.printTitle("【 Error!】Datagram.excuteDatagram.21：" + e.getMessage());
								}
							} else if ((infoArr[0].equals("27-2线"))) {
								sente = "update [hart03_opc] set flow1 = " + firstvalue + ",temp = " + secondvalue + ",flow2 = " + thirdvalue + ",total = " + fourthvalue + " where typeserial = '"
										+ MapInfo.weihao_map.get(infoArr[0]) + "'";
								printUtil.printTitle("网关:" + base.getIpaddress() + ",更新当前数据库表hart03_opc中的累计值：" + MapInfo.weihao_map.get(infoArr[0]) + "=" + firstvalue + "," + secondvalue + ","
										+ thirdvalue + "," + fourthvalue);
								try {
									printUtil.printTitle("执行sql：" + sente);
									dBtool.executeUpdate(sente);
								} catch (SQLException e) {
									printUtil.printTitle("【 Error!】Datagram.excuteDatagram.22：" + e.getMessage());
								}
							}

						}
					}
				}
			}

		}
		try {
			dBtool.free();
		} catch (SQLException e) {
			printUtil.printTitle("【 Error!】Datagram.excuteDatagram.23：" + e.getMessage());
		}
	}

	public int getIntervalSeconds(Date lastime, Date currentime) {
		long sl = lastime.getTime();
		long el = currentime.getTime();
		long ei = el - sl;
		return (int) (ei / (1000));
	}
}
