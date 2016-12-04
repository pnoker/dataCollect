package com.dact.main;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.dact.dateType.ReceiverDatagram;
import com.dact.init.Initial;
import com.dact.pojo.BaseInfo;
import com.dact.util.OperateTxtUtil;
import com.dact.util.RepairNumber;

public class MainCollect {
	public static void main(String[] args) {
		System.out.println("<---初始化操作--->");
		Initial initial = new Initial();
		initial.init();

		System.out.println("<---读取网关信息--->");
		OperateTxtUtil readTxtUtil = new OperateTxtUtil();
		ArrayList<String> gatewaylist = new ArrayList<String>();
		gatewaylist = readTxtUtil.readLine("D:/sia/confiles/gatewayconf.txt");

		System.out.println("<---开启采数线程--->");
		for (int m = 0; m < gatewaylist.size(); m++) {
			BaseInfo base = new BaseInfo();
			base.setIpaddress(gatewaylist.get(m).split(",")[0]);// ipaddress
			base.setPort(Integer.parseInt(gatewaylist.get(m).split(",")[1]));// port
			base.setLocalport(Integer.parseInt(gatewaylist.get(m).split(",")[2]));// localport

			ReceiverDatagram receiverDatagram = new ReceiverDatagram(base);
			Thread thread = new Thread(receiverDatagram);
			thread.start();
			try {
				Thread.sleep(3000);// 3秒后启动下一个线程
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
