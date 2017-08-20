package com.dact.init;

import com.dact.pojo.MapInfo;
import com.dact.util.OperateTxtUtil;

import java.util.ArrayList;
import java.util.Date;

public class Dlt {
    public void initDlt() {
        OperateTxtUtil operateTxtUtil = new OperateTxtUtil();
        ArrayList<String> dlt = new ArrayList<>();
        dlt = operateTxtUtil.readLine("D:/sia/confiles/dlt.txt");
        for (int m = 0; m < dlt.size(); m++) {
            MapInfo.dlt_map.put(dlt.get(m).split("\\t")[0], dlt.get(m).split("\\t")[1]);
            MapInfo.dlt_currentime.put(dlt.get(m).split("\\t")[0], new Date());
        }
    }
}
