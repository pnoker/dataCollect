package com.dact.collect;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.pojo.ValueInfo;
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
	volatile boolean stop = false;

	public ReceiverDatagram(BaseInfo base) {
		try {
			this.base = base;
			this.datagramSocket = new DatagramSocket(base.getLocalport());
			this.datagramSend = new DatagramPacket(sendCode, sendCode.length, InetAddress.getByName(base.getIpaddress()),
					base.getPort());
			this.datagramReceive = new DatagramPacket(buf, 1024);
			this.print = new PrintUtil();
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		}
	}

	public DatagramSocket SendCode() {
		try {
			print.printTitle("发送4A9B命令，开始采数");
			datagramSocket.send(datagramSend);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return datagramSocket;
	}

	public void run() {
		SendCode();
		while (!stop) {
			try {
				datagramSocket.receive(datagramReceive);
				byte[] receive = datagramReceive.getData();
				p = new PackageProcessor(receive);
				String hexDatagram = print.printHexDatagram(receive, receive.length);
				String datastart = p.bytesToString(0, 1);
				switch (datastart) {
				case "0101":
					print.printTitle("网络报文：" + hexDatagram);
					NetDatagram netDatagram = new NetDatagram(p,base);
					break;
				case "010f":
					print.printTitle("健康报文：" + p.bytesToString(2, 3));
					HealthDatagram healthDatagram = new HealthDatagram();
				case "0183":
					print.printTitle("数据报文：" + hexDatagram);
					Datagram datagram = new Datagram(p,base);
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			datagramReceive.setLength(1024);
		}
		datagramSocket.close();
	}
}
