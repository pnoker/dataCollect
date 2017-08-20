package com.dact.util;

import java.io.*;
import java.util.ArrayList;

public class OperateTxtUtil {
    public ArrayList<String> readLine(String path) {
        ArrayList<String> line = new ArrayList<String>();
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                line.add(temp.trim());
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return line;
    }

    public void writeLine(String path, String line) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
