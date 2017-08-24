package com.dact.dateType;

import com.dact.pojo.BaseInfo;
import com.dact.pojo.MapInfo;
import com.dact.util.LogWrite;
import com.dact.util.PackageProcessor;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

public class ReceiverDatagram implements Runnable {
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramSend;
    private DatagramPacket datagramReceive;
    private byte[] buf = new byte[1024];
    private byte[] sendCode = {(byte) 0x01, (byte) 0x0B, (byte) 0xFF, (byte) 0xFF, (byte) 0x4A, (byte) 0x9B};
    private PackageProcessor p;
    private BaseInfo base;
    private String networkinfo = "";
    private LogWrite logWrite;
    volatile boolean stop = false;
    volatile boolean restart = false;
    private Map<String, Long> firstTime = new HashMap<String, Long>();

    public ReceiverDatagram(BaseInfo base) {
        try {
            this.base = base;
            this.datagramSocket = new DatagramSocket(base.getLocalport());
            this.datagramSend = new DatagramPacket(sendCode, sendCode.length, InetAddress.getByName(base.getIpaddress()), base.getPort());
            this.datagramReceive = new DatagramPacket(buf, 1024);
            this.logWrite = new LogWrite(base.getIpaddress());
        } catch (SocketException e) {
            logWrite.write(e.getMessage());
        } catch (UnknownHostException e) {
            logWrite.write(e.getMessage());
        }
    }

    /**
     * 判断健康报文的时间间隔，如果该网关的当前时间减去上次更新的网关时间，算出来的时间间隔大于10分钟，就置stop为true，跳出接收数据，
     * 重新发送采数命令4A9B
     */
    public void heartBeat() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                logWrite.write("<---- 检测节点健康报文时间间隔 ---->");
                long second = (new Date()).getTime();
                for (Entry<String, Long> entry : firstTime.entrySet()) {
                    long interval = (second - entry.getValue()) / (1000 * 60);
                    if (interval >= 5) {// 某个节点的健康报文时间间隔大于5分钟就打印出来
                        logWrite.write("< ----网关下节点:（长地址）" + entry.getKey() + " ，本次健康报文时间间隔为" + interval + "分钟 ---->");
                    }
                    if (interval >= 10) {
                        logWrite.write("< ---- 设置 stop = true  ---->");
                        stop = true;
                    }
                }
                logWrite.write("<---- 检测网关健康报文时间间隔 ---->");
                try {
                    long interval = (second - MapInfo.gateway_currentime.get(base.getIpaddress())) / (1000 * 60);
                    if (interval >= 5) {// 网关的健康报文时间间隔大于5分钟就打印出来
                        logWrite.write("< ----网关:" + base.getIpaddress() + " ，本次健康报文时间间隔为" + interval + "分钟 ---->");
                    }
                    if (interval >= 10) {
                        logWrite.write("< ---- 设置 stop = true  ---->");
                        stop = true;
                    }
                } catch (Exception e) {
                    System.out.println("MapInfo.gateway_currentime.get(" + base.getIpaddress() + "):" + e.getMessage());
                }
            }
        }, 1000 * 10, 1000 * 60 * 2);
    }

    /**
     * 打印十六进制的报文，不足两位，前面补零
     */
    public String getHexDatagram(byte[] b, int length) {
        StringBuffer sbuf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            //不足两位前面补零处理
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sbuf.append(hex.toUpperCase());
        }
        return sbuf.toString();
    }

    public void run() {
        logWrite.write("<----------当前网关:" + base.getIpaddress() + ",启动线程---------->");
        NetDatagram netDatagram = new NetDatagram();
        HealthDatagram healthDatagram = new HealthDatagram();
        Datagram datagram = new Datagram();
        try {
            datagramSocket.send(datagramSend);
        } catch (IOException e) {
            logWrite.write(e.getMessage());
        }

        /*接收完每条报文就进行判断健康报文时间间隔,这是一个线程，
        每分钟检测一次当前各个节点的时间间隔，
        如果有某个节点的时间间隔大于10分钟，就向网关重新发送采数命令*/
        heartBeat();
        while (!restart) {
            while (!stop) {
                try {
                    datagramSocket.setSoTimeout(1000 * 60 * 3);
                    datagramSocket.receive(datagramReceive);
                    byte[] receive = datagramReceive.getData();
                    p = new PackageProcessor(receive);
                    String hexDatagram = getHexDatagram(datagramReceive.getData(), datagramReceive.getLength());
                    String datastart = p.bytesToString(0, 1);
                    switch (datastart) {

                        //0101节点加入信息
                        case "0101":
                            logWrite.write("网络报文:" + hexDatagram);
                            logWrite.writeEasy("网络报文:", hexDatagram);
                            netDatagram.excuteNetDatagram(p, base, networkinfo, logWrite);
                            networkinfo = netDatagram.getNetworkinfo();
                            break;

                        //节点测试信息
                        case "010f":
                            logWrite.write("健康报文:" + hexDatagram);
                            logWrite.writeEasy("健康报文:", hexDatagram);
                            if (healthDatagram.excuteHealthDatagram(p, base, logWrite)) {
                                String shortAddress = p.bytesToString(2, 3);
                                String longAddress = MapInfo.addressmap.get(shortAddress + " " + base.getIpaddress());
                                Date date = new Date();
                                //更新该节点的最后一次健康报文到达的时间戳
                                firstTime.put(longAddress, date.getTime());
                            }
                            break;

                        //节点数据信息
                        case "0183":
                            logWrite.write("数据报文:" + hexDatagram);
                            logWrite.writeEasy("数据报文:", hexDatagram);
                            datagram.excuteDatagram(p, base, logWrite);
                            break;

                        default:
                            logWrite.write("其他报文:" + hexDatagram);
                            logWrite.writeEasy("其他报文:", hexDatagram);
                    }
                } catch (Exception e) {
                    logWrite.write("datagramSocket.receive 堵塞/连接超时：" + e.getMessage());
                    try {
                        logWrite.write("<-'-'-'-重新发送采数命令:010BFFFF4A9B-'-'-'->");
                        datagramSocket.send(datagramSend);
                    } catch (IOException ex) {
                        logWrite.write(ex.getMessage());
                    }
                }
                datagramReceive.setLength(1024);
            }
            logWrite.write("<-'-'-'-网关（" + base.getIpaddress() + "）下某节点健康报文超时，重新发送采数命令:010BFFFF4A9B-'-'-'->");
            try {
                datagramSocket.send(datagramSend);
            } catch (IOException e) {
                logWrite.write(e.getMessage());
            }
            stop = false;
            firstTime.clear();
            logWrite.write("<-'-'-'-重新设置本次超时时间间隔为10分钟-'-'-'->");
        }
        datagramSocket.close();
        logWrite.close();
        logWrite.write("<----------当前网关:" + base.getIpaddress() + ",关闭Socket---------->");
        logWrite.write("<----------当前网关:" + base.getIpaddress() + ",关闭LogWrite---------->");
    }
}
