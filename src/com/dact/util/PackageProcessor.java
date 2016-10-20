package com.dact.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PackageProcessor {
	private byte[] inpackage;

	public PackageProcessor(byte[] ipackage) {
		this.inpackage = ipackage;
	}

	public int bytesToInt(byte bytes0, byte bytes1) {
		int num = ((bytes0 << 8) & 0xFF00);
		num |= (bytes1 & 0xFF);
		return num;
	}

	public int bytesToInt(int start, int end) {
		int value = 0;
		int length = end - start;
		for (int i = length; i >= 0; i--) {
			int num = ((inpackage[start + length - i] & 0xff) << (8 * i));
			value |= num;
		}
		return value;
	}

	public int doublebytesToInt(int start, int end) {
		int num = ((inpackage[end] << 8) & 0xFF00);
		num |= (inpackage[start] & 0xFF);
		return num;
	}

	public Long bytesToLong(int startbit, int endbit) {
		long value = 0;
		String hex = bytesToString(startbit, endbit);
		System.out.println("WXIO-Long================" + hex);
		value = Long.valueOf(hex, 16);
		return value;
	}

	public int bytesToIntSmall(int start, int end) {
		int value = 0;
		int length = end - start;
		;
		for (int i = length; i >= 0; i--) {
			int num = ((inpackage[end + i - length] & 0xff) << (8 * i));
			value |= num;
		}
		return value;
	}

	public int bytesToIntMiddle(int start, int end) {
		int value = 0;
		int length = end - start;
		byte tmp1 = 0;
		tmp1 = inpackage[end];
		inpackage[end] = inpackage[end - 2];
		inpackage[end - 2] = tmp1;
		byte tmp2 = 0;
		tmp2 = inpackage[end - 1];
		inpackage[end - 1] = inpackage[end - 3];
		inpackage[end - 3] = tmp2;
		for (int i = length; i >= 0; i--) {
			int num = ((inpackage[start + length - i] & 0xff) << (8 * i));
			value |= num;
		}
		return value;
	}

	public float bytesToFloat(int startbit, int endbit) {
		String hex = bytesToString(startbit, endbit);
		System.out.println(hex);
		float value = Float.intBitsToFloat(Integer.valueOf(hex, 16));
		return value;
	}

	public int bytesToTen(int start, int end) {
		int value = 0;
		int length = end - start;
		for (int i = length; i >= 0; i--) {
			int num = (inpackage[start + length - i] & 0x00FF) >> 4;
			int num2 = (inpackage[start + length - i] & 0x0f);
			value |= num * 10 + num2;
		}
		return value;
	}

	public float bytesToFloatSmall(int startbit, int endbit) {
		float value = 0;
		try {
			byte[] s = { inpackage[startbit + 3], inpackage[startbit + 2], inpackage[startbit + 1],
					inpackage[startbit] };
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(s));
			value = dis.readFloat();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

	public float bytesToFloat3(int startbit, int endbit) {
		float value = 0;
		try {
			byte[] s = { inpackage[startbit], inpackage[startbit + 1], inpackage[startbit + 2],
					inpackage[startbit + 3] };
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(s));
			value = dis.readFloat();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (Double.isNaN(value)) {
			value = -1;
		}
		return value;
	}

	public float bytesToFloatMiddle(int startbit, int endbit) {
		float value = 0;
		try {
			byte[] s = { inpackage[startbit + 2], inpackage[startbit + 3], inpackage[startbit],
					inpackage[startbit + 1] };
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(s));
			value = dis.readFloat();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (Double.isNaN(value)) {
			value = -1;
		}
		return value;
	}

	public String bytesToString(int startbit, int endbit) {
		String result = "";
		for (int i = startbit; i <= endbit; i++) {
			String s = Integer.toHexString(inpackage[i]);
			if (s.length() < 2) {
				s = "0" + s;
			} else {
				s = s.substring(s.length() - 2, s.length());
			}
			result = result + s;
		}
		return result;
	}

	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	public static int byteArrayToInt(byte[] bytes) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (bytes[i] & 0x000000FF) << shift;
		}
		return value;
	}

	public static void main(String[] args) {
		byte[] test = { (byte) 0x70, (byte) 0x91, (byte) 0x00, (byte) 0x00 };
		PackageProcessor p = new PackageProcessor(test);
		System.out.println();
		System.out.println(p.bytesToTen(0, 0));
	}
}
