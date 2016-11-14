package com.dact.dateType;

import java.sql.SQLException;
import java.util.Date;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.util.LogWrite;
import com.dact.util.Oracle;
import com.dact.util.PackageProcessor;
import com.dact.util.UUIDBuild;

public class Datagram {
	public void excuteDatagram(PackageProcessor p, BaseInfo base, LogWrite logWrite) {
		Oracle oraCon = new Oracle();
		String wia_longaddress, wia_shortaddress, deviceType, dltInfo, shuiInfo;
		String[] infoArr, eachArr;
		int interval = 0;
		float shuiliuliang;
		Date lastime, currentime;

		wia_shortaddress = p.bytesToString(2, 3);
		wia_longaddress = MapInfo.addressmap.get(wia_shortaddress + " " + base.getIpaddress());
		deviceType = MapInfo.typemap.get(wia_longaddress);

		logWrite.write("长地址：" + wia_longaddress);
		logWrite.write("短地址：" + wia_shortaddress);

		if (p.bytesToString(8, 9).equals("7400")) {
			if (p.bytesToString(10, 10).equals("01")) {
				if (deviceType.equals("0e00")) {
					System.out.println("这条数据是水表的数据：");
					logWrite.write("这条数据是水表的数据：");
					System.out.println("--------------------或许是水表的读数：--------------------" + p.bytesToFloatSmall(11, 14));
					shuiInfo = MapInfo.shui_map.get(wia_longaddress);
					shuiliuliang = p.bytesToFloatSmall(11, 14);
					String sente = "insert into shui_data values('" + UUIDBuild.getUUID() + "','" + shuiInfo + "',0," + shuiliuliang + ",to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'))";
					logWrite.write(sente);
					try {
						oraCon.executeUpdate(sente);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sente = "update shui_opc set total = " + shuiliuliang + " where typeserial = '" + MapInfo.weihao_map.get(shuiInfo) + "'";
					logWrite.write(sente);
					try {
						oraCon.executeUpdate(sente);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {

					System.out.println("这条数据是modbus数据：");
					logWrite.write("这条数据是modbus数据：");
					String slaveID = null;
					slaveID = p.bytesToString(11, 11);

					wia_longaddress = wia_longaddress + " " + slaveID;
					System.out.println("当前的长地址其实并不对，有没有：" + wia_longaddress);
					lastime = MapInfo.wirelessio_currentime.get(wia_longaddress);
					System.out.println("不应该取得不到啊：" + lastime);

					if (lastime != null) {
						currentime = new Date();
						interval = getIntervalSeconds(lastime, currentime);
						System.out.println("modbus当前的时间间隔到底是什么" + interval);

						if (interval >= 0) {
							MapInfo.wirelessio_currentime.put(wia_longaddress, currentime);
							infoArr = MapInfo.wirelessio_map.get(wia_longaddress).split(",");
							String sente = "insert into " + infoArr[1] + "_data values('" + infoArr[0] + "',";
							for (int i = 2; i < infoArr.length; i++) {

								eachArr = infoArr[i].split(" ");
								System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + eachArr[3]);
								if (eachArr[3].contains("int")) {
									float tep_int = p.bytesToFloat(Integer.parseInt(eachArr[1]), Integer.parseInt(eachArr[2]));
									if (i == 2) {
										sente = "insert into " + infoArr[1] + "_data values('" + UUIDBuild.getUUID() + "','" + infoArr[0] + "'," + tep_int
												+ ",-1,to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'))";
										logWrite.write(sente);
										System.out.println(sente);
										try {
											oraCon.executeUpdate(sente);
										} catch (SQLException e) {
											e.printStackTrace();
										}

										sente = "update wireless01_opc set instant = " + tep_int + " where typeserial = '" + MapInfo.weihao_map.get(infoArr[0]) + "'";
										System.out.println(sente);
										logWrite.write(sente);
										try {
											oraCon.executeUpdate(sente);
										} catch (SQLException e) {
											e.printStackTrace();
										}
									} else if (i == 3) {
										sente = "insert into " + infoArr[1] + "_data values('" + UUIDBuild.getUUID() + "','" + infoArr[0] + "',-1," + tep_int
												+ ",to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'))";
										logWrite.write(sente);
										System.out.println(sente);
										try {
											oraCon.executeUpdate(sente);
										} catch (SQLException e) {
											e.printStackTrace();
										}
										sente = "update wireless01_opc set total = " + tep_int + " where typeserial = '" + MapInfo.weihao_map.get(infoArr[0]) + "'";
										System.out.println(sente);
										logWrite.write(sente);
										try {
											oraCon.executeUpdate(sente);
										} catch (SQLException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
				}
			} else if (p.bytesToString(10, 10).equals("03")) {
				logWrite.write("DLT数据：");
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
				logWrite.write("sure_dlt=" + sure_dlt);
				String flagPart = p.bytesToString(sure_dlt + 10, sure_dlt + 13);
				logWrite.write("flagPart=" + flagPart);

				dltInfo = MapInfo.dlt_map.get(wia_longaddress);
				logWrite.write("dltInfo=" + dltInfo);
				if (flagPart.equals("33333333")) {
					String valuePart = p.bytesToString(sure_dlt + 14, sure_dlt + 17);
					logWrite.write("valuePart:" + valuePart);
					float result = getResult08(valuePart);
					logWrite.write("组合有功总电能:" + result);
				} else if (flagPart.equals("33333433")) {
					String valuePart = p.bytesToString(sure_dlt + 14, sure_dlt + 17);
					logWrite.write("valuePart:" + valuePart);
					float result = getResult08(valuePart);
					logWrite.write("正向有功总电能:" + result);
					String sente = "insert into dlt_data values('" + UUIDBuild.getUUID() + "','" + dltInfo + "'," + result + ",-1,-1,-1,-1,to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'))";
					logWrite.write(sente);
					try {
						oraCon.executeUpdate(sente);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					sente = "update dlt_opc set zxyg = " + result + " where typeserial = '" + MapInfo.weihao_map.get(dltInfo) + "'";
					logWrite.write(sente);
					try {
						oraCon.executeUpdate(sente);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else if (flagPart.equals("33333533")) {
					String valuePart = p.bytesToString(sure_dlt + 14, sure_dlt + 17);
					logWrite.write("valuePart:" + valuePart);
					float result = getResult08(valuePart);
					logWrite.write("反向有功总电能:" + result);
					String sente = "insert into dlt_data values('" + UUIDBuild.getUUID() + "','" + dltInfo + "',-1," + result + ",-1,-1,-1,to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'))";
					logWrite.write(sente);
					try {
						oraCon.executeUpdate(sente);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					sente = "update dlt_opc set fxyg = " + result + " where typeserial = '" + MapInfo.weihao_map.get(dltInfo) + "'";
					logWrite.write(sente);
					try {
						oraCon.executeUpdate(sente);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else if (flagPart.equals("33333633")) {
					String valuePart = p.bytesToString(sure_dlt + 14, sure_dlt + 17);
					logWrite.write("valuePart:" + valuePart);
					float result = getResult08(valuePart);
					logWrite.write("正向无功总电能:" + result);
					String sente = "insert into dlt_data values('" + UUIDBuild.getUUID() + "','" + dltInfo + "',-1,-1," + result + ",-1,-1,to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'))";
					logWrite.write(sente);
					try {
						oraCon.executeUpdate(sente);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					sente = "update dlt_opc set zxwg = " + result + " where typeserial = '" + MapInfo.weihao_map.get(dltInfo) + "'";
					logWrite.write(sente);
					try {
						oraCon.executeUpdate(sente);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else if (flagPart.equals("33333733")) {
					String valuePart = p.bytesToString(sure_dlt + 14, sure_dlt + 17);
					logWrite.write("valuePart:" + valuePart);
					float result = getResult08(valuePart);
					logWrite.write("反向无功总电能:" + result);
					String sente = "insert into dlt_data values('" + UUIDBuild.getUUID() + "','" + dltInfo + "',-1,-1,-1," + result + ",-1,to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'))";
					logWrite.write(sente);
					try {
						oraCon.executeUpdate(sente);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					sente = "update dlt_opc set fxwg = " + result + " where typeserial = '" + MapInfo.weihao_map.get(dltInfo) + "'";
					logWrite.write(sente);
					try {
						oraCon.executeUpdate(sente);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else if (flagPart.equals("33333935")) {
					String valuePart = p.bytesToString(sure_dlt + 14, sure_dlt + 15);
					logWrite.write("valuePart:" + valuePart);
					float result = getResult06(valuePart);
					logWrite.write("总功率因数:" + result);
					String sente = "insert into dlt_data values('" + UUIDBuild.getUUID() + "','" + dltInfo + "',-1,-1,-1,-1," + result + ",to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'))";
					logWrite.write(sente);
					try {
						oraCon.executeUpdate(sente);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					sente = "update dlt_opc set fxwg = " + result + " where typeserial = '" + MapInfo.weihao_map.get(dltInfo) + "'";
					logWrite.write(sente);
					try {
						oraCon.executeUpdate(sente);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {
					logWrite.write("未识别的标识");
				}
			}
		}
		try {
			oraCon.free();
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

	public float getResult08(String valuePart) {
		long x = 0;
		long y = 0;

		float result = 0;

		String part1 = valuePart.substring(0, 2);
		x = Long.parseLong(part1, 16);
		y = Long.parseLong("33", 16);
		Long trueValue1 = Long.parseLong(Long.toHexString(x - y));
		System.out.println(trueValue1);
		result += trueValue1 * 0.01;

		String part2 = valuePart.substring(2, 4);
		x = Long.parseLong(part2, 16);
		y = Long.parseLong("33", 16);
		Long trueValue2 = Long.parseLong(Long.toHexString(x - y));
		System.out.println(trueValue2);
		result += trueValue2;

		String part3 = valuePart.substring(4, 6);
		x = Long.parseLong(part3, 16);
		y = Long.parseLong("33", 16);
		Long trueValue3 = Long.parseLong(Long.toHexString(x - y));
		System.out.println(trueValue3);
		result += trueValue3 * 100;

		String part4 = valuePart.substring(6, 8);
		x = Long.parseLong(part4, 16);
		y = Long.parseLong("33", 16);
		Long trueValue4 = Long.parseLong(Long.toHexString(x - y));
		System.out.println(trueValue4);
		result += trueValue4 * 10000;

		return result;
	}

	public float getResult06(String valuePart) {
		long x = 0;
		long y = 0;

		float result = 0;

		String part1 = valuePart.substring(0, 2);
		x = Long.parseLong(part1, 16);
		y = Long.parseLong("33", 16);
		Long trueValue1 = Long.parseLong(Long.toHexString(x - y));
		System.out.println(trueValue1);
		result += trueValue1 * 0.01;

		String part2 = valuePart.substring(2, 4);
		x = Long.parseLong(part2, 16);
		y = Long.parseLong("33", 16);
		Long trueValue2 = Long.parseLong(Long.toHexString(x - y));
		System.out.println(trueValue2);
		result += trueValue2;

		return result;
	}
}
