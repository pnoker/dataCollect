package com.dact.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataTable {
    public ArrayList<String> init_datatable() {
        ArrayList<String> tablelist = new ArrayList<String>();
        try {
            BufferedReader bw = new BufferedReader(new FileReader(new File("D:\\sia\\confiles\\datatable.txt")));
            String line = null;
            while ((line = bw.readLine()) != null) {
                tablelist.add(line);
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tablelist;
    }
}
