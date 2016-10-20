package com.dact.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

import com.dact.collect.ReceiverDatagram;
import com.dact.init.Shui;
import com.dact.init.Weihao;
import com.dact.init.Wireless;
import com.dact.init.Initial;
import com.dact.pojo.MapInfo;


public class MainCollect {
	public static void main(String[] args) {
		System.out.println("===============================");
		System.out.println("当前执行的操作 : " + "mainthread");
		System.out.println("===============================");
		ArrayList<String> gatewaylist = new ArrayList<String>();
		Initial initial = new Initial();
		initial.init();
		try {
			BufferedReader bw = new BufferedReader(new FileReader(new File("D:\\sia\\confiles\\gatewayconf.txt")));
			String line = null;
			while ((line = bw.readLine()) != null) {
				gatewaylist.add(line);
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("----------------------------怎么还断了呢吗----------------------------------------------------");
		System.out.println(gatewaylist.size());

		for (int i = 0; i < gatewaylist.size(); i++) {
			System.out.println(gatewaylist.get(i));
			String ip_port[] = gatewaylist.get(i).split(",");
			System.out
					.println("----------------------------到底执行了吗----------------------------------------------------");
			Date date = new Date();
			MapInfo.getGateway_currentime().put(ip_port[0], date);
			new Thread(new ReceiverDatagram()).start();

			try {
				Thread.sleep(1731 * i);
			} catch (Exception e) {

			}
		}
	}

}
