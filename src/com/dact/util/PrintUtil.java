package com.dact.util;

import java.util.Date;

/**
 * @author Pnoker
 * @description 打印工具类
 */
public class PrintUtil {

    public static void printDetail(String ipaddress, String detail) {
        while (ipaddress.length() < 15) {
            ipaddress += " ";
        }
        System.out.println(DateUtil.getCompleteTime(new Date()) + " " + ipaddress + " --> " + detail);
    }

    public static void printMessage(String message) {
        System.out.println("<---- " + DateUtil.getCompleteTime(new Date()) + " ----> " + message);
    }

}
