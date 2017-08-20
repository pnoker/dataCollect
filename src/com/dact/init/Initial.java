package com.dact.init;

public class Initial {
    /**
     * @author Pnoker
     * @time 2017-1-15
     */
    public void init() {
        Wireless wireless = new Wireless();// 初始化Wireless
        wireless.initWireless();
        Pi pi = new Pi();// 初始化Pi
        pi.initPi();
        Dlt dlt = new Dlt();
        dlt.initDlt();// 初始化Dlt
    }
}
