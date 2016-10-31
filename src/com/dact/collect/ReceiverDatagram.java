package com.dact.collect;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import com.dact.pojo.BaseInfo;
import com.dact.util.LogWrite;
import com.dact.util.PackageProcessor;
import com.dact.util.PrintUtil;

public class ReceiverDatagram implements Runnable {
	private DatagramSocket datagramSocket;
	private DatagramPacket datagramSend;
	private DatagramPacket datagramReceive;
	private byte[] buf = new byte[1024];
	private byte[] sendCode = { (byte) 0x01, (byte) 0x0B, (byte) 0xFF, (byte) 0xFF, (byte) 0x4A, (byte) 0x9B };
	private PackageProcessor p;
	private PrintUtil print;
	private BaseInfo base;
	private String networkinfo = "";
	private long first;
	private LogWrite logWrite;
	volatile boolean stop = false;
	volatile boolean restart = false;

	public ReceiverDatagram(BaseInfo base) {
		try {
			this.base = base;
			this.datagramSocket = new DatagramSocket(base.getLocalport());
			this.datagramSend = new DatagramPacket(sendCode, sendCode.length, InetAddress.getByName(base.getIpaddress()), base.getPort());
			this.datagramReceive = new DatagramPacket(buf, 1024);
			this.print = new PrintUtil();
			this.first = (new Date()).getTime();
			this.logWrite = new LogWrite(base.getIpaddress());
		} catch (SocketException e) {
			print.printDetail(base.getIpaddress(), "【 Error!】ReceiverDatagram。1" + e.getMessage());
		} catch (UnknownHostException e) {
			print.printDetail(base.getIpaddress(), "【 Error!】ReceiverDatagram.2" + e.getMessage());
		}
	}

	/**
	 * 判断健康报文的时间间隔，如果该网关的当前时间减去上次更新的网关时间，算出来的时间间隔大于10分钟，就置stop为true，跳出接收数据，
	 * 重新发送采数命令4A9B
	 */
	public void heartBeat() {
		long second = (new Date()).getTime();
		long interval = (second - first) / (1000 * 60 * 10);
		if ((int) interval > 10) {
			logWrite.write("<-'-'-'-本次健康报文时间间隔为" + interval + "分钟-'-'-'->");
			print.printDetail(base.getIpaddress(), "<----------网关:" + base.getIpaddress() + ",本次健康报文时间间隔为" + interval + "分钟---------->");
			this.stop = true;
		}
	}

	public void run() {
		print.printDetail(base.getIpaddress(), "<----------当前网关:" + base.getIpaddress() + ",启动线程---------->");
		NetDatagram netDatagram = new NetDatagram();
		HealthDatagram healthDatagram = new HealthDatagram();
		Datagram datagram = new Datagram();
		try {
			datagramSocket.send(datagramSend);
		} catch (IOException e) {
			print.printDetail(base.getIpaddress(), "【 Error!】ReceiverDatagram.run.1：" + e.getMessage());
		}
		while (!restart) {
			while (!stop) {
				try {
					datagramSocket.receive(datagramReceive);
					byte[] receive = datagramReceive.getData();
					p = new PackageProcessor(receive);
					String hexDatagram = print.getHexDatagram(datagramReceive.getData(), datagramReceive.getLength());
					String datastart = p.bytesToString(0, 1);
					switch (datastart) {
					case "0101":
						logWrite.write("网络报文:" + hexDatagram);
						print.printDetail(base.getIpaddress(), "网络报文:" + hexDatagram);
						netDatagram.excuteNetDatagram(p, base, networkinfo, logWrite);
						networkinfo = netDatagram.getNetworkinfo();
						break;
					case "010f":
						logWrite.write("健康报文:" + hexDatagram);
						print.printDetail(base.getIpaddress(), "健康报文:" + hexDatagram);
						if (healthDatagram.excuteHealthDatagram(p, base, logWrite)) {
							this.first = (new Date()).getTime();
						}
						break;
					case "0183":
						logWrite.write("数据报文:" + hexDatagram);
						print.printDetail(base.getIpaddress(), "数据报文:" + hexDatagram);
						datagram.excuteDatagram(p, base, logWrite);
						break;
					default:
						logWrite.write("其他报文:" + hexDatagram);
						print.printDetail(base.getIpaddress(), "其他报文:" + hexDatagram);
					}
				} catch (IOException e) {
					print.printDetail(base.getIpaddress(), "【 Error!】ReceiverDatagram.run.2：" + e.getMessage());
				}
				datagramReceive.setLength(1024);
				/* 接收完每条报文就进行判断健康报文时间间隔是问题 */
				heartBeat();
			}
			try {
				logWrite.write("<-'-'-'-网关（" + base.getIpaddress() + "）健康报文超时，重新发送采数命令:010BFFFF4A9B-'-'-'->");
				print.printDetail(base.getIpaddress(), "<-'-'-'-网关（" + base.getIpaddress() + "）健康报文超时，重新发送采数命令-'-'-'->");
				datagramSocket.send(datagramSend);
				this.stop = false;
				logWrite.write("<-'-'-'-设置本次超时时间间隔为10分钟-'-'-'->");
				print.printDetail(base.getIpaddress(), "设置本次超时时间间隔为10分钟");
				this.first += 1000 * 60 * 10;
			} catch (IOException e) {
				print.printDetail(base.getIpaddress(), "【 Error!】ReceiverDatagram.run.3：" + e.getMessage());
			}
		}
		print.printDetail(base.getIpaddress(), "<----------当前网关:" + base.getIpaddress() + ",关闭Socket---------->");
		print.printDetail(base.getIpaddress(), "<----------当前网关:" + base.getIpaddress() + ",关闭LogWrite---------->");
		datagramSocket.close();
		logWrite.close();
	}
}
