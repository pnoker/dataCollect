package com.dact.collect;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import com.dact.pojo.BaseInfo;
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
		} catch (SocketException e) {
			print.printTitle("【 Error!】ReceiverDatagram。1" + e.getMessage());
		} catch (UnknownHostException e) {
			print.printTitle("【 Error!】ReceiverDatagram.2" + e.getMessage());
		}
	}

	public void heartBeat() {
		long second = (new Date()).getTime();
		long interval = (second - first) / (1000 * 60 * 10);
		if ((int) interval > 10) {
			print.printTitle("<----------网关:" + base.getIpaddress() + ",本次健康报文时间间隔为" + interval + "分钟---------->");
			this.stop = true;
		}
	}

	public void run() {
		print.printTitle("<----------当前网关:" + base.getIpaddress() + ",启动线程---------->");
		NetDatagram netDatagram = new NetDatagram();
		HealthDatagram healthDatagram = new HealthDatagram();
		Datagram datagram = new Datagram();
		try {
			datagramSocket.send(datagramSend);
		} catch (IOException e) {
			print.printTitle("【 Error!】ReceiverDatagram.run.1：" + e.getMessage());
		}
		while (!restart) {
			while (!stop) {
				heartBeat();
				try {
					datagramSocket.receive(datagramReceive);
					byte[] receive = datagramReceive.getData();
					p = new PackageProcessor(receive);
					String hexDatagram = print.getHexDatagram(datagramReceive.getData(), datagramReceive.getLength());
					String datastart = p.bytesToString(0, 1);
					switch (datastart) {
					case "0101":
						print.printTitle("网关:" + base.getIpaddress() + ",网络报文:" + hexDatagram);
						netDatagram.excuteNetDatagram(p, base, networkinfo);
						networkinfo = netDatagram.getNetworkinfo();
						break;
					case "010f":
						print.printTitle("网关:" + base.getIpaddress() + ",健康报文:" + hexDatagram);
						this.first = (new Date()).getTime();
						healthDatagram.excuteHealthDatagram(p, base);
						break;
					case "0183":
						print.printTitle("网关:" + base.getIpaddress() + ",数据报文:" + hexDatagram);
						datagram.excuteDatagram(p, base);
						break;
					default:
						print.printTitle("网关:" + base.getIpaddress() + ",其他报文:" + hexDatagram);
					}
				} catch (IOException e) {
					print.printTitle("【 Error!】ReceiverDatagram.run.2：" + e.getMessage());
				}
				datagramReceive.setLength(1024);
			}
			try {
				print.printTitle("<-'-'-'-网关（" + base.getIpaddress() + "）健康报文超时，重新发送采数命令-'-'-'->");
				datagramSocket.send(datagramSend);
				this.stop = true;
				print.printTitle("网关:" + base.getIpaddress() + ",设置本次超时时间间隔为5分钟");
				this.first += 1000 * 60 * 5;
			} catch (IOException e) {
				print.printTitle("【 Error!】ReceiverDatagram.run.3：" + e.getMessage());
			}
		}
		print.printTitle("<----------d当前网关:" + base.getIpaddress() + ",关闭Socket---------->");
		datagramSocket.close();
	}
}
