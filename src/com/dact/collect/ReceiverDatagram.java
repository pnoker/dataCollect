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
			this.datagramSend = new DatagramPacket(sendCode, sendCode.length,
					InetAddress.getByName(base.getIpaddress()), base.getPort());
			this.datagramReceive = new DatagramPacket(buf, 1024);
			this.print = new PrintUtil();
			this.first = (new Date()).getTime();
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		}
	}

	public void heartBeat() {
		long second = (new Date()).getTime();
		long interval = (second - first) / (1000 * 60 * 10);
		if ((int) interval > 10) {
			this.stop = true;
		}
	}

	public void run() {
		try {
			datagramSocket.send(datagramSend);
		} catch (IOException e) {
			System.out.println(e.getMessage());
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
						print.printTitle("网络报文:" + hexDatagram);
						NetDatagram netDatagram = new NetDatagram(p, base, networkinfo);
						networkinfo = netDatagram.getNetworkinfo();
						break;
					case "010f":
						print.printTitle("健康报文:" + hexDatagram);
						this.first = (new Date()).getTime();
						HealthDatagram healthDatagram = new HealthDatagram(p, base);
						break;
					case "0183":
						print.printTitle("数据报文:" + hexDatagram);
						Datagram datagram = new Datagram(p, base);
						break;
					default:
						print.printTitle("报    文:" + hexDatagram);
					}
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
				datagramReceive.setLength(1024);
			}
			try {
				print.printTitle("<-'-'-'-网关（" + base.getIpaddress() + "）健康报文超时，重新发送采数命令-'-'-'->");
				datagramSocket.send(datagramSend);
				this.stop = true;
				this.first += 1000 * 60 * 5;
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		datagramSocket.close();
	}
}
