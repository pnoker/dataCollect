package com.dact.dateType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.util.LogWrite;
import com.dact.util.PackageProcessor;
import com.dact.util.Sqlserver;

public class Datagram {
	public void excuteDatagram(PackageProcessor p, BaseInfo base, LogWrite logWrite) {
		Sqlserver dBtool = new Sqlserver();
		ResultSet rs = null;
		String wia_longaddress, wia_shortaddress, deviceType, shuiInfo, dltInfo, aiInfo, piInfo, hartaddress = "";
		String[] infoArr, eachArr;
		int interval = 0, serial;
		float shuiliuliang, dianya, firstvalue, secondvalue, thirdvalue, fourthvalue = 0, zerochannel = 0,
				onechannel = 0, pivalue;
		Date lastime, currentime;
		Date currentime_eight = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		try {
			String eight_str = format.format(currentime_eight);
			String[] eight_arr = eight_str.split(" ");
			eight_arr[1] = "08:00:00";
			StringBuffer sb = new StringBuffer();
			sb.append(eight_arr[0] + " ");
			sb.append(eight_arr[1]);
			String eight_after = sb.toString();
			currentime_eight = format2.parse(eight_after);

		} catch (Exception e) {
			e.printStackTrace();
		}
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
					logWrite.write("该条数据为水表数据");
					// rateUtil.rate(wia_shortaddress, wia_longaddress,
					// base.getIpaddress(), serial, logWrite);

					shuiInfo = MapInfo.shui_map.get(wia_longaddress);
					shuiliuliang = p.bytesToFloatSmall(11, 14);
					logWrite.write("水表数据：" + shuiInfo + "=" + shuiliuliang);
					String sente = "insert into [shui_data](typeserial,tag, value,reachtime)values('" + shuiInfo
							+ "',0," + shuiliuliang + ",getdate())";
					logWrite.write("向数据库表shui_data中添加一条数据：" + shuiInfo + "=" + shuiliuliang);
					try {
						logWrite.write("执行sql：" + sente);
						dBtool.executeUpdate(sente);
					} catch (SQLException e) {
						logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
					}

				}
				/* 无线表 */
				else {
					logWrite.write("这是一条无线IO数据");
					String slaveID = null;
					/* 从站地址 */
					slaveID = p.bytesToString(11, 11);

					wia_longaddress = wia_longaddress + " " + slaveID;
					logWrite.write("从站地址：" + slaveID);
					lastime = MapInfo.wirelessio_currentime.get(wia_longaddress);
					// rateUtil.rate(wia_shortaddress, wia_longaddress,
					// base.getIpaddress(), serial, logWrite);

					if (lastime != null) {
						currentime = new Date();
						interval = getIntervalSeconds(lastime, currentime);

						if (interval >= 1800) {
							logWrite.write("30分钟才存储一个modbus数据");
							MapInfo.wirelessio_currentime.put(wia_longaddress, currentime);
							infoArr = MapInfo.wirelessio_map.get(wia_longaddress).split(",");
							String sente = "insert into [" + infoArr[1] + "_data]values('" + infoArr[0] + "',";
							String updatesente = "update [value_opc] set liuliang =";
							for (int i = 2; i < infoArr.length; i++) {
								eachArr = infoArr[i].split(" ");
								if (eachArr[3].contains("int") && (p.bytesToString(13, 14).equals("8581"))) {
									int tep_int = p.bytesToInt(Integer.parseInt(eachArr[1]),
											Integer.parseInt(eachArr[2]));
									sente = sente + "" + tep_int + ",";
									updatesente += tep_int;
									sente = sente + "getdate())";
									updatesente += ",reachtime = getdate() where typeserial = '" + infoArr[0] + "'";
									try {
										logWrite.write("执行sql：" + sente);
										dBtool.executeUpdate(sente);
										logWrite.write("执行sql：" + updatesente);
										dBtool.executeUpdate(updatesente);
									} catch (SQLException e) {
										logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
									}
								} else if ((eachArr[3].contains("int")) && (p.bytesToString(13, 14).equals("0800"))) {
									float tep_float = p.bytesToIntMiddle(Integer.parseInt(eachArr[1]),
											Integer.parseInt(eachArr[2]));
									sente = sente + "" + tep_float + ",";
									updatesente += tep_float;
									sente = sente + "getdate())";
									updatesente += ",reachtime = getdate() where typeserial = '" + infoArr[0] + "'";
									try {
										logWrite.write("执行sql：" + sente);
										dBtool.executeUpdate(sente);
										logWrite.write("执行sql：" + updatesente);
										dBtool.executeUpdate(updatesente);
									} catch (SQLException e) {
										logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
									}
								} else if ((eachArr[3].contains("int")) && (p.bytesToString(13, 14).equals("000c"))) {
									sente = "insert into [" + infoArr[1].split("#")[0] + "_data]values('" + infoArr[0].split("#")[0]
											+ "',";
									updatesente = "update [value_opc] set liuliang =";

									float tep_float = p.bytesToFloatMiddle(Integer.parseInt(eachArr[1].split("#")[0]),
											Integer.parseInt(eachArr[2].split("#")[0]));
									sente = sente + "" + tep_float + ",";
									updatesente += tep_float;
									sente = sente + "getdate())";
									updatesente += ",reachtime = getdate() where typeserial = '"
											+ infoArr[0].split("#")[0] + "'";
									try {
										logWrite.write("执行sql：" + sente);
										dBtool.executeUpdate(sente);
										logWrite.write("执行sql：" + updatesente);
										dBtool.executeUpdate(updatesente);
									} catch (SQLException e) {
										logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
									}

									sente = "insert into [" + infoArr[1].split("#")[1] + "_data]values('" + infoArr[0].split("#")[1]
											+ "',";
									updatesente = "update [value_opc] set liuliang =";

									tep_float = p.bytesToFloatMiddle(Integer.parseInt(eachArr[1].split("#")[1]),
											Integer.parseInt(eachArr[2].split("#")[1]));
									sente = sente + "" + tep_float + ",";
									updatesente += tep_float;
									sente = sente + "getdate())";
									updatesente += ",reachtime = getdate() where typeserial = '"
											+ infoArr[0].split("#")[1] + "'";
									try {
										logWrite.write("执行sql：" + sente);
										dBtool.executeUpdate(sente);
										logWrite.write("执行sql：" + updatesente);
										dBtool.executeUpdate(updatesente);
									} catch (SQLException e) {
										logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
									}

								} else if (eachArr[3].contains("int")) {
									float tep_float = p.bytesToIntMiddle(Integer.parseInt(eachArr[1]),
											Integer.parseInt(eachArr[2]));
									sente = sente + "" + tep_float + ",";
									updatesente += tep_float;
									sente = sente + "getdate())";
									updatesente += ",reachtime = getdate() where typeserial = '" + infoArr[0] + "'";
									try {
										logWrite.write("执行sql：" + sente);
										dBtool.executeUpdate(sente);
										logWrite.write("执行sql：" + updatesente);
										dBtool.executeUpdate(updatesente);
									} catch (SQLException e) {
										logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
									}
								} else if (eachArr[3].contains("float")) {
									float tep_float = p.bytesToFloat3(Integer.parseInt(eachArr[1]),
											Integer.parseInt(eachArr[2]));
									sente = sente + "" + tep_float + ",";
									updatesente += tep_float;
									sente = sente + "getdate())";
									updatesente += ",reachtime = getdate() where typeserial = '" + infoArr[0] + "'";
									try {
										logWrite.write("执行sql：" + sente);
										dBtool.executeUpdate(sente);
										logWrite.write("执行sql：" + updatesente);
										dBtool.executeUpdate(updatesente);
									} catch (SQLException e) {
										logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
									}
								} else if (eachArr[3].contains("int01")) {
									float tep_float = p.bytesToInt(Integer.parseInt(eachArr[1]),
											Integer.parseInt(eachArr[2]));
									sente = sente + "" + tep_float + ",";
									updatesente += tep_float;
									sente = sente + "getdate())";
									updatesente += ",reachtime = getdate() where typeserial = '" + infoArr[0] + "'";
									try {
										logWrite.write("执行sql：" + sente);
										dBtool.executeUpdate(sente);
										logWrite.write("执行sql：" + updatesente);
										dBtool.executeUpdate(updatesente);
									} catch (SQLException e) {
										logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
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
				lastime = MapInfo.ai_currentime.get(wia_longaddress);
				currentime = new Date();
				interval = getIntervalSeconds(lastime, currentime);
				if (interval > 30) {
					aiInfo = MapInfo.ai_map.get(wia_longaddress);
					if (p.bytesToString(10, 10).equals("01")) {
						zerochannel = p.bytesToFloat3(12, 15);
						onechannel = 0;
					} else if (p.bytesToString(10, 10).equals("02")) {
						onechannel = p.bytesToFloat3(12, 15);
						zerochannel = 0;
					} else if (p.bytesToString(10, 10).equals("03")) {
						zerochannel = p.bytesToFloat3(12, 15);
						onechannel = p.bytesToFloat3(17, 20);
					}
					String sente = "insert into [ai_data]values('" + aiInfo + "','" + zerochannel + "','" + onechannel
							+ "',getdate())";
					try {
						logWrite.write("执行sql：" + sente);
						dBtool.executeUpdate(sente);
					} catch (SQLException e) {
						logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
					}

				}
			}
			/* PI数据类型，04 */
			else if (p.bytesToString(10, 10).equals("04")) {
				logWrite.write("PI数据类型");
				// rateUtil.rate(wia_shortaddress, wia_longaddress,
				// base.getIpaddress(), serial, logWrite);
				lastime = MapInfo.pi_currentime.get(wia_longaddress);
				if (lastime != null) {
					currentime = new Date();
					interval = getIntervalSeconds(lastime, currentime);
					if (interval > 30) {

						piInfo = MapInfo.pi_map.get(wia_longaddress);

						pivalue = p.bytesToFloatSmall(11, 14);
						String sente = "insert into [pi_data]values('" + piInfo + "'," + pivalue + ",getdate())";
						try {
							logWrite.write("执行sql：" + sente);
							dBtool.executeUpdate(sente);
						} catch (SQLException e) {
							logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
						}
					}
				}
			}
			/* DLT数据类型 */
			else if (p.bytesToString(10, 10).equals("03")) {
				logWrite.write("DLT数据类型 ");

				System.out.println("这条数据是DLT数据：");

				int sure_dlt = 0;
				boolean flag_dlt = true;
				int i_dlt = 11;
				while (flag_dlt) {
					if (p.bytesToString(i_dlt, i_dlt).equals("68")) {
						sure_dlt = i_dlt;
						flag_dlt = false;
					}
					i_dlt++;

				}

				System.out.println("sure_dlt:" + sure_dlt);

				int dataLength = Integer.parseInt(p.bytesToString(sure_dlt + 9, sure_dlt + 9), 16);
				System.out.println("dataLength:" + dataLength);
				String dltFlag = p.bytesToString(sure_dlt + 1, sure_dlt + 6);
				System.out.println("dltFlag:" + dltFlag);
				int index = -1;
				dltInfo = MapInfo.dlt_map.get(wia_longaddress);
				System.out.println("dltInfo:" + dltInfo);
				if (dltInfo != null) {
					index = dltInfo.indexOf(dltFlag);
				}
				if (index != -1) {
					int nameIndex = dltInfo.indexOf(" ", index);
					int typeIndex = dltInfo.indexOf(" ", nameIndex + 1);
					String dltName = dltInfo.substring(nameIndex + 1, typeIndex);
					String type = dltInfo.substring(typeIndex + 1, typeIndex + 3);

					int typeLength = 0;
					if (type.equals("07")) {
						typeLength = 4;
						System.out.println("07规约");
					} else if (type.equals("97")) {
						typeLength = 2;
						System.out.println("97规约");
					}

					String dataPart = p.bytesToString(sure_dlt + 10, sure_dlt + 10 + dataLength - 1);
					System.out.println("dataPart:" + dataPart);
					String flagPart = p.bytesToString(sure_dlt + 10, sure_dlt + typeLength + 9);
					System.out.println("flagPart:" + flagPart);
					String valuePart = p.bytesToString(sure_dlt + typeLength + 10, sure_dlt + 10 + dataLength - 1);
					System.out.println("valuePart:" + valuePart);

					long x = 0;
					long y = 0;
					boolean save = true;

					float result = 0;

					int valueLength = dataLength - typeLength;
					System.out.println("valueLength:" + valueLength);

					for (int i = 0, j = -2; i < valueLength * 2; i += 2, j += 2) {
						String part = valuePart.substring(i, i + 2);
						System.out.println("part:" + part);
						x = Long.parseLong(part, 16);
						y = Long.parseLong("33", 16);
						try {
							Long trueValue = Long.parseLong(Long.toHexString(x - y));
							System.out.println("i:" + i + ",j:" + j + ",trueValue:" + trueValue);
							result += trueValue * Math.pow(10, j);
						} catch (Exception e) {
							System.out.println("规约设置错误");
							save = false;
						}
					}

					if (save) {
						String sente = "insert into ele_data values('" + dltName + "'," + result + ",getdate(),'"
								+ flagPart + "')";
						String updatesente = "update [ele_opc] set value = " + result
								+ ",reachtime = getdate() where typeserial = '" + dltName + "' and flag = '" + flagPart
								+ "'";
						String select_sente = "select  * from [anqingcollect].[dbo].[ele_opc] where typeserial = '"
								+ dltName + "' and flag = '" + flagPart + "'";
						try {
							rs = dBtool.executeQuery(select_sente);
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						Date temp = new Date();
						Date current_temp = new Date();
						int interval_temp = 0;
						try {
							while (rs.next()) {
								temp = rs.getTimestamp("reachtime");
								interval_temp = getIntervalSeconds(temp, current_temp);
							}
						} catch (SQLException e1) {
							e1.printStackTrace();
						}

						int interval_temp_eight = getIntervalSeconds(currentime_eight, current_temp);
						System.out.println("系统最后一次上数" + temp);
						System.out.println("现在" + current_temp);
						System.out.println("电表间隔值" + interval_temp);
						if ((interval_temp > 25 * 60)) {
							System.out.println("3分钟才存储一个电表数据");
							try {
								logWrite.write("执行sql：" + sente);
								dBtool.executeUpdate(sente);
								logWrite.write("执行sql：" + updatesente);
								dBtool.executeUpdate(updatesente);
							} catch (SQLException e) {
								logWrite.write("【 Error!】Datagram.excuteDatagram.1：" + e.getMessage());
							}
						}

					}
				} else {
					System.out.println("配置文件中找不到从栈地址");
				}
			}
		}
		/* hart类型数据 */
		else {
			logWrite.write("hart类型数据");
			// rateUtil.rate(wia_shortaddress, wia_longaddress,
			// base.getIpaddress(), serial, logWrite);
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
					String commandnum = p.bytesToString(sure + 6, sure + 6);
					logWrite.write("命令号：" + commandnum + "号");
					String sente = "";
					if (commandnum.equals("03")) {
						infoArr = MapInfo.hart_map.get(wia_longaddress).split(" ");
						firstvalue = p.bytesToFloat3(sure + 15, sure + 18);
						logWrite.write("主变量值是：" + firstvalue);
						secondvalue = p.bytesToFloat3(sure + 20, sure + 23);
						logWrite.write("第二变量值是：" + secondvalue);
						if (infoArr[2].equals("false") && infoArr[3].equals("false")) {
							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('"
									+ infoArr[0] + "',1,'" + secondvalue + "',getdate())";
						} else if (infoArr[2].equals("true") && infoArr[3].equals("false")) {
							thirdvalue = p.bytesToFloat3(sure + 25, sure + 28);
							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('"
									+ infoArr[0] + "',0,'" + firstvalue + "',getdate())";
						} else if (infoArr[2].equals("true") && infoArr[3].equals("true")) {
							fourthvalue = p.bytesToFloat3(sure + 30, sure + 33);
							sente = "insert into [" + infoArr[1] + "_data](typeserial,tag, value,reachtime) values('"
									+ infoArr[0] + "',0,'" + firstvalue + "',getdate())";
						}
					}
					try {
						logWrite.write("执行sql：" + sente);
						dBtool.executeUpdate(sente);
					} catch (SQLException e) {
						logWrite.write("【 Error!】Datagram.excuteDatagram.19：" + e.getMessage());
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
