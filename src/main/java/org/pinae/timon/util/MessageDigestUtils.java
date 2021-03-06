package org.pinae.timon.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密处理函数库
 * 
 * @author Huiyugeng
 * 
 */
public class MessageDigestUtils {
	public static String MD5(String str) {
		return encrypt(str, "MD5");
	}

	public static String SHA1(String str) {
		return encrypt(str, "SHA1");
	}
	
	public static String SHA256(String str) {
		return encrypt(str, "SHA256");
	}
	
	public static String SHA512(String str) {
		return encrypt(str, "SHA512");
	}

	public static String encrypt(String str, String enc) {
		MessageDigest md = null;
		String result = null;
		byte[] bt = str.getBytes();
		try {
			md = MessageDigest.getInstance(enc);
			md.update(bt);
			result = bytes2Hex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		return result;
	}

	// 将字节数组转换成16进制的字符串
	private static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;

		for (byte bt : bts) {
			tmp = (Integer.toHexString(bt & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}
}
