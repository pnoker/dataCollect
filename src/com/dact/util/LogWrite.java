package com.dact.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

public class LogWrite {
    private BufferedWriter siaWriter = null;
    private BufferedWriter awWriter = null;
    private String siaPath = "D:/RunProgramer/logs/dataCollect/siaLog/";
    private String awPath = "D:/RunProgramer/logs/dataCollect/awLog/";
    private String ipaddress;
    private String fromIp;

    public LogWrite(String ipaddress) {
        this.ipaddress = ipaddress;
        this.fromIp = ipaddress;
        while (this.fromIp.length() < 15) {
            this.fromIp += " ";
        }
        if (!(new File(siaPath + ipaddress).isDirectory())) {
            new File(siaPath + ipaddress).mkdirs();
        }
        if (!(new File(awPath + ipaddress).isDirectory())) {
            new File(awPath + ipaddress).mkdirs();
        }
    }

    public void write(String detail) {
        DateUtil dateUtil = new DateUtil();
        String file = siaPath + ipaddress + "/" + dateUtil.getDayTime(new Date()) + ".log";
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            siaWriter = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            siaWriter.write(DateUtil.getCompleteTime(new Date()) + " " + fromIp + " --> " + detail);
            PrintUtil.printDetail(fromIp, detail);
            siaWriter.newLine();
            siaWriter.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void writeEasy(String type, String detail) {
        DateUtil dateUtil = new DateUtil();
        String file = awPath + ipaddress + "/" + dateUtil.getDayTime(new Date()) + ".log";
        String regex = "(.{2})";
        detail = detail.replaceAll(regex, "$1 ");
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            awWriter = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            awWriter.write(DateUtil.getCompleteTime(new Date()) + " " + fromIp + " --> " + type + detail);
            awWriter.newLine();
            awWriter.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void close() {
        try {
            if (null != siaWriter) {
                siaWriter.close();
            }
            if (null != awWriter) {
                awWriter.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
