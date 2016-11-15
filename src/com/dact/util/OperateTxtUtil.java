package com.dact.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class OperateTxtUtil {
	public ArrayList<String> readLine(String path) {
		ArrayList<String> line = new ArrayList<String>();
		try {
			FileInputStream fileInputStream = new FileInputStream(path);
			InputStreamReader fileReader = new InputStreamReader(fileInputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
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

	public static void main(String[] args) {
		ArrayList<String> line = new ArrayList<String>();
		File file = new File("D:/sia/confiles/gatewayconf.txt");
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String temp;
			while ((temp = bufferedReader.readLine()) != null) {
				line.add(temp.trim());
				System.out.println(temp.trim());
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
