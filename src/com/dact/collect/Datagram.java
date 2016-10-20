package com.dact.collect;

import java.util.Date;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.pojo.ShuiInfo;
import com.dact.pojo.ValueInfo;
import com.dact.util.DBtool;
import com.dact.util.PackageProcessor;

import sun.awt.SunHints.Value;



public class Datagram {
	public Datagram(PackageProcessor p,BaseInfo base,ShuiInfo shui,ValueInfo value) {
		value.setWia_shortaddress(p.bytesToString(2, 3));
		System.out.println("进入到0183之后短地址是什么？：" + value.getWia_shortaddress() + " " + value.getIpaddress());
		value.setWia_longaddress(
				MapInfo.getAddressmap().get(value.getWia_shortaddress() + " " + value.getIpaddress()));
		System.out.println("进入到0183之后长地址是什么？：" + value.getWia_longaddress());
		value.setTypeofwatch(MapInfo.getTypemap().get(value.getWia_longaddress()));
		DBtool dbtool = new DBtool();
		if (p.bytesToString(8, 9).equals("7400")) {

			if (p.bytesToString(10, 10).equals("01")) {
				if (value.getTypeofwatch().equals("000e")) {
					System.out.println("这条数据是水表的数据：");
					System.out
							.println("--------------------或许是水表的读数：--------------------" + p.bytesToFloatSmall(11, 14));
					shui.setShuiInfo(MapInfo.getShui_map().get(value.getWia_longaddress()));
					shui.setShuiliuliang(p.bytesToFloatSmall(11, 14));
					 String sente = "insert into [shui_data](typeserial,tag, value,reachtime)values('" + shui.getShuiInfo()
							+ "',0," + shui.getShuiliuliang() + ",getdate())";
					System.out.println(sente + "当前的ip地址是" + base.getIpaddress());
					
					dbtool.executeUpdate(sente);

					// 更新表头值
					sente = "update [shui_opc] set value = " + shui.getShuiliuliang()
							+ ",reachtime = getdate() where typeserial = '" + shui.getShuiInfo() + "_bt' and tag = 0";
					System.out.println(sente + "opc相关--当前的ip地址是" + base.getIpaddress());
					dbtool.executeUpdate(sente);
					// opc相关
					if (shui.getShuiInfo().equals("sia0001")) {
						shui.setShuiliuliang(shui.getShuiliuliang() + 42959);
					} else if (shui.getShuiInfo().equals("sia0002")) {
						shui.setShuiliuliang(shui.getShuiliuliang() + 44165);
					} else if (shui.getShuiInfo().equals("sia0003")) {
						shui.setShuiliuliang(shui.getShuiliuliang() + (float) 1696.7);
					} else if (shui.getShuiInfo().equals("sia0004")) {
						shui.setShuiliuliang(shui.getShuiliuliang() + 1821);
					} else if (shui.getShuiInfo().equals("sia0005")) {
						shui.setShuiliuliang(shui.getShuiliuliang() + (float) 357.6);
					} else if (shui.getShuiInfo().equals("sia0006")) {
						shui.setShuiliuliang(shui.getShuiliuliang() + 3280);
					} else if (shui.getShuiInfo().equals("sia0007")) {
						shui.setShuiliuliang(shui.getShuiliuliang() + 608);
					}

					// 更新瞬时值
					sente = "with table1 as(select DATEDIFF(HOUR,reachtime,GETDATE()) as hours,value from [shui_opc] where typeserial = '"
							+ shui.getShuiInfo() + "') ";
					sente += "update [shui_opc] set value = (select (" + shui.getShuiliuliang()
							+ "-table1.value)/table1.hours from table1)  where typeserial = '" + shui.getShuiInfo()
							+ "_0' and tag = 0";
					System.out.println(sente + "opc相关--当前的ip地址是" + base.getIpaddress());
					dbtool.executeUpdate(sente);
					// 更新累积值
					sente = "update [shui_opc] set value = " + shui.getShuiliuliang()
							+ ",reachtime = getdate() where typeserial = '" + shui.getShuiInfo() + "' and tag = 0";
					System.out.println(sente + "opc相关--当前的ip地址是" + base.getIpaddress());
					dbtool.executeUpdate(sente);

				} else {

					System.out.println("这条数据是modbus数据：");
					int tagtouse = 0;
					int used = 0;
					String slaveID = null;
					slaveID = p.bytesToString(11, 11);

					value.setWia_longaddress(value.getWia_longaddress() + " " + slaveID);
					System.out.println("当前的长地址其实并不对，有没有：" + value.getWia_longaddress());
					value.setLastime(MapInfo.getWirelessio_currentime().get(value.getWia_longaddress()));
					System.out.println("不应该取得不到啊：" + value.getLastime());

					if (value.getLastime() != null) {
						value.setCurrentime(new Date());
						value.setInterval(getIntervalSeconds(value.getLastime(), value.getCurrentime()));
						System.out.println("modbus当前的时间间隔到底是什么" + value.getInterval());

						if (value.getInterval() >= 30) {
							MapInfo.getWirelessio_currentime().put(value.getWia_longaddress(), value.getCurrentime());
							infoArr = MapInfo.getWirelessio_map().get(value.getWia_longaddress()).split(",");
							sente = "insert into [" + infoArr[1] + "_data]values('" + infoArr[0] + "',";
							for (int i = 2; i < infoArr.length; i++) {

								eachArr = infoArr[i].split(" ");
								System.out.println(
										"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
												+ eachArr[3]);
								if (eachArr[3].contains("int")) {
									float tep_int = p.bytesToFloat(Integer.parseInt(eachArr[1]),
											Integer.parseInt(eachArr[2]));
									String sente = "insert into [" + infoArr[1]
											+ "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',"
											+ (i - 2) + "," + tep_int + ",getdate())";
									System.out.println("完全进不来吗" + eachArr[3]);
									System.out.println(sente + "当前的ip地址是" + base.getIpaddress());
									dbtool.executeUpdate(sente);
								}
								if (eachArr[3].contains("long")) {
									long tep_int = p.bytesToLong(Integer.parseInt(eachArr[1]),
											Integer.parseInt(eachArr[2]));
									if (i - 2 == 0) {
										tep_int = (long) (tep_int * 0.0000001);
									} else if (i - 2 == 1) {
										tep_int = (long) (tep_int * 0.1);
									} else {
									}
									String sente = "insert into [" + infoArr[1]
											+ "_data](typeserial,tag, value,reachtime) values('" + infoArr[0] + "',"
											+ (i - 2) + "," + tep_int + ",getdate())";
									System.out.println("完全进不来吗" + eachArr[3]);
									System.out.println(sente + "当前的ip地址是" + base.getIpaddress());
									dbtool.executeUpdate(sente);
									sente = "update [shui_opc] set value = " + tep_int
											+ ",reachtime = getdate() where typeserial =  '" + infoArr[0] + "_"
											+ (i - 2) + "'";
									System.out.println(sente);
									dbtool.executeUpdate(sente);
								}
							}
						}
					}

				}

			} else if (p.bytesToString(10, 10).equals("02")) {
				System.out.println("这条数据是740002数据：");
				if (value.getTypeofwatch().equals("000e")) {
					System.out.println("这条数据是水表电压的数据：");

					shui.setShuiInfo( MapInfo.getShui_map().get(value.getWia_longaddress()));
					int dianya_tmp = p.doublebytesToInt(11, 12);
					dianya = (float) dianya_tmp / 100;
					;
					String sente = "insert into [dianya_data](typeserial,tag, value,reachtime)values('" + shui.getShuiInfo() + "',0,"
							+ dianya + ",getdate())";
					System.out.println(sente + "当前的ip地址是" + base.getIpaddress());
					dbtool.executeUpdate(sente);

					try {
						Thread.sleep(3000);
					} catch (Exception e) {

					}

				}

			} else if (p.bytesToString(10, 10).equals("07")) {
				System.out.println("开润开封协议");

				System.out.println("--------------------或许是开润表的读数：--------------------" + p.bytesToFloatSmall(11, 14));
				String runInfo = MapInfo.getShui_map().get(value.getWia_longaddress());
				if (runInfo == null) {
					break;
				}
				int tag = p.bytesToInt(12, 12);
				float kairun = 0;
				int d0, d1, d2, d3, d4, d5, d6, d7 = 0;

				switch (tag) {
				case 0:
					System.out.println("流量");

					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);
					d2 = p.bytesToTen(15, 15);
					d3 = p.bytesToTen(16, 16);

					kairun = d2 * 10000 + d1 * 100 + d0;
					for (int i = 0; i < d3 - 5; i++) {
						kairun = (float) (kairun * 0.1);
					}
					break;
				case 1:
					System.out.println("流速");

					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);
					d2 = p.bytesToTen(15, 15);

					kairun = d2 * 10000 + d1 * 100 + d0;
					break;
				case 2:
					System.out.println("流量百分比");
					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);

					kairun = d1 * 100 + d0;

					break;
				case 3:
					System.out.println("流体电阻值");
					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);

					kairun = d1 * 100 + d0;

					break;
				case 4:
					System.out.println("正向总量");
					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);
					d2 = p.bytesToTen(15, 15);
					d3 = p.bytesToTen(16, 16);
					d4 = p.bytesToTen(17, 17);
					System.out.println("zhengxiang:" + d4 + "/" + d3 + "/" + d2 + "/" + d1 + "/" + d0 + "/");
					kairun = d4 * 1000000 + d3 * 1000000 + d2 * 10000 + d1 * 100 + d0;

					break;
				case 5:
					System.out.println("反向总量");
					d0 = p.bytesToTen(13, 13);
					d1 = p.bytesToTen(14, 14);
					d2 = p.bytesToTen(15, 15);
					d3 = p.bytesToTen(16, 16);
					d4 = p.bytesToTen(17, 17);

					kairun = d4 * 1000000 + d3 * 1000000 + d2 * 10000 + d1 * 100 + d0;

					break;
				case 6:
					System.out.println("报警状态");

					break;
				case 7:
					System.out.println("管道直径");

					break;

				default:
					System.out.println("没有符合任何命令");
					break;
				}

				String sente = "insert into [kairun_data](typeserial,tag, value,reachtime)values('" + runInfo + "'," + tag
						+ "," + kairun + ",getdate())";
				System.out.println(sente + "当前的ip地址是" + base.getIpaddress());
				dbtool.executeUpdate(sente);

				sente = "update [shui_opc] set value = " + kairun + ",reachtime = getdate() where typeserial = '"
						+ runInfo + "_" + tag + "'";
				System.out.println(sente + "opc相关--当前的ip地址是" + base.getIpaddress());
				dbtool.executeUpdate(sente);

			} else if (p.bytesToString(10, 10).equals("04")) {
				System.out.println("这条数据是PI数据：");

			}

		} else {
			System.out.println("这条数据是hart数据：");
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
			value.setLastime(  MapInfo.getHart_currentime().get(value.getWia_longaddress()));
			if (value.getLastime() != null) {
				value.setCurrentime(new Date());
				value.setInterval(getIntervalSeconds(value.getLastime(), value.getCurrentime()));

				if (value.getInterval() > 10) {
					// hart设备长地址
					hartaddress = p.bytesToString(sure + 1, sure + 5);
					System.out.println("hart设备长地址是：" + hartaddress);
					// 命令号
					String commandnum = p.bytesToString(sure + 6, sure + 6);
					System.out.println("当前命令号是：" + commandnum + "号");
					if (commandnum.equals("03")) {

						infoArr = MapInfo.getHart_map().get(wia_longaddress).split(" ");
						System.out.println("*************************************************************："
								+ MapInfo.getHart_map().get(wia_longaddress));
						firstvalue = p.bytesToFloat3(sure + 15, sure + 18);
						System.out.println("主变量值是:" + firstvalue);
						int secondunit = p.bytesToInt(sure + 19, sure + 19);
						secondvalue = p.bytesToFloat3(sure + 20, sure + 23);
						System.out.println("第二变量值是:" + secondvalue);
						System.out.println("================================");

						if (infoArr[2].equals("false") && infoArr[3].equals("false")) {
							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('"
									+ infoArr[0] + "',0,'" + firstvalue + "',getdate())";
							dbtool.executeUpdate(sente);
							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('"
									+ infoArr[0] + "',1,'" + secondvalue + "',getdate())";
							dbtool.executeUpdate(sente);

						}

						else if (infoArr[2].equals("true") && infoArr[3].equals("false")) {
							thirdvalue = p.bytesToFloat3(sure + 25, sure + 28);
							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('"
									+ infoArr[0] + "',0,'" + firstvalue + "',getdate())";
							dbtool.executeUpdate(sente);
							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('"
									+ infoArr[0] + "',1,'" + secondvalue + "',getdate())";
							dbtool.executeUpdate(sente);
							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('"
									+ infoArr[0] + "',2,'" + thirdvalue + "',getdate())";
							dbtool.executeUpdate(sente);
						}

						else if (infoArr[2].equals("true") && infoArr[3].equals("true")) {

							thirdvalue = p.bytesToFloat3(sure + 25, sure + 28);
							fourthvalue = p.bytesToFloat3(sure + 30, sure + 33);
							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('"
									+ infoArr[0] + "',0,'" + firstvalue + "',getdate())";
							dbtool.executeUpdate(sente);

							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('"
									+ infoArr[0] + "',1,'" + secondvalue + "',getdate())";
							dbtool.executeUpdate(sente);

							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('"
									+ infoArr[0] + "',2," + thirdvalue + ",getdate())";
							dbtool.executeUpdate(sente);

							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('"
									+ infoArr[0] + "',3,'" + fourthvalue + "',getdate())";
							dbtool.executeUpdate(sente);

							// opc相关
							if ((infoArr[0].equals("T1线")) || (infoArr[0].equals("T2线"))
									|| (infoArr[0].equals("食堂用天然气"))) {
								sente = "update [hart01_opc] set flow1 = " + firstvalue + ",flow2 = " + secondvalue
										+ ",flow3 = " + thirdvalue + ",total = " + fourthvalue + " where typeserial = '"
										+ MapInfo.getWeihao_map().get(infoArr[0]) + "'";
								System.out.println(sente + "opc相关--当前的ip地址是" + ipaddress);
								dbtool.executeUpdate(sente);
							} else if ((infoArr[0].equals("焦化蜡油线")) || (infoArr[0].equals("20线"))) {
								sente = "update [hart02_opc] set density = " + firstvalue + ",temp = " + secondvalue
										+ ",flow = " + thirdvalue + ",total = " + fourthvalue + " where typeserial = '"
										+ MapInfo.getWeihao_map().get(infoArr[0]) + "'";
								System.out.println(sente + "opc相关--当前的ip地址是" + ipaddress);
								dbtool.executeUpdate(sente);
							} else if ((infoArr[0].equals("27-2线"))) {
								sente = "update [hart03_opc] set flow1 = " + firstvalue + ",temp = " + secondvalue
										+ ",flow2 = " + thirdvalue + ",total = " + fourthvalue + " where typeserial = '"
										+ MapInfo.getWeihao_map().get(infoArr[0]) + "'";
								System.out.println(sente + "opc相关--当前的ip地址是" + ipaddress);
								dbtool.executeUpdate(sente);
							}

						}
						System.out.println(sente + "当前的ip地址是" + ipaddress);
					}
				}
			}

		}
	}
	public int getIntervalSeconds(Date lastime, Date currentime) {

		System.out.println("为啥会是空指针呢" + lastime);
		long sl = lastime.getTime();
		long el = currentime.getTime();
		long ei = el - sl;
		return (int) (ei / (1000));
	}
}
