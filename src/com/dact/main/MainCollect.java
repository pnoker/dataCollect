package com.dact.main;

import com.dact.dateType.ReceiverDatagram;
import com.dact.init.Initial;
import com.dact.pojo.BaseInfo;
import com.dact.util.ExcutePro;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MainCollect {
    public static Map<String, String> gateWayList = new HashMap<String, String>();

    public static void main(String[] args) {
        System.out.println("<---开启采数线程--->");
        Initial initial = new Initial();
        initial.init();

        try {
            //遍历map，获取网关和端口信息
            gateWayList = ExcutePro.getProperties("gateway.properties");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        for (Entry<String, String> entry : gateWayList.entrySet()) {
            BaseInfo base = new BaseInfo();
            base.setIpaddress(entry.getKey());// ipaddress
            base.setPort(Integer.parseInt(entry.getValue().split("#")[0]));// port
            base.setLocalport(Integer.parseInt(entry.getValue().split("#")[1]));// localport

            ReceiverDatagram receiverDatagram = new ReceiverDatagram(base);
            Thread thread = new Thread(receiverDatagram);
            thread.start();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
