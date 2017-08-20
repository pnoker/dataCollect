package com.dact.util;

import java.util.Date;

/**
 * @author Pnoker
 * @description 打印工具类
 */
public class PrintUtil {
    private DateUtil date;

    public PrintUtil() {
        this.date = new DateUtil();
    }

    public void printDetail(String ipaddress, String detail) {
        while (ipaddress.length() < 15) {
            ipaddress += " ";
        }
        System.out.println(date.getCompleteTime(new Date()) + " " + ipaddress + " --> " + detail);
    }

    public void printMessage(String message) {
        System.out.println("<---- " + date.getCompleteTime(new Date()) + " ----> " + message);
    }

}
