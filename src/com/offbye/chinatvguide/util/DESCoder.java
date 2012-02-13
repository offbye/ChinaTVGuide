package com.offbye.chinatvguide.util;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


public class DESCoder {
	private final static String KEY = "mytvkeyy"; // 字节数必须是8的倍数

	public static byte[] desEncrypt(byte[] plainText) throws Exception {
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(KEY.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, key, sr);
		byte data[] = plainText;
		byte encryptedData[] = cipher.doFinal(data);
		return encryptedData;
	}

	public static byte[] desDecrypt(byte[] encryptText) throws Exception {
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(KEY.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, key, sr);
		byte encryptedData[] = encryptText;
		byte decryptedData[] = cipher.doFinal(encryptedData);
		return decryptedData;
	}

	public static String encrypt(String input) throws Exception {
		return base64Encode(desEncrypt(input.getBytes()));
	}

	public static String decrypt(String input) throws Exception {
		byte[] result = base64Decode(input);
		return new String(desDecrypt(result));
	}

	public static String base64Encode(byte[] s) {
		if (s == null)
			return null;
		try {
            return new String(Base64.encode(s), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;     
        }
	}

	public static byte[] base64Decode(String s) throws IOException {
		if (s == null) {
			return null;
		}
		byte[] b = Base64.decode(s);
		return b;
	}

	public static void main(String args[]) {
		try {
			System.out.println(DESCoder.encrypt("汉字"));
			System.out.println(DESCoder.decrypt(DESCoder
					.encrypt("nihaodddd")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
