
package com.offbye.chinatvguide.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DES {
    private static final String DESKey = "mytvkeyy"; // 字节数必须是8的倍数

    private static byte[] iv1 = {
            (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0xAB,
            (byte) 0xCD, (byte) 0xEF
    };

    public static void main(String[] args) {
        System.out.print("xyz");
        DES des = new DES();
        System.out.print(des.encrypt("cctv10"));
    }

    public static byte[] desEncrypt(byte[] plainText) throws Exception {
        // SecureRandom sr = new SecureRandom();
        // sr.setSeed(iv);

        // IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        IvParameterSpec iv = new IvParameterSpec(iv1);

        DESKeySpec dks = new DESKeySpec(DESKey.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte data[] = plainText;
        byte encryptedData[] = cipher.doFinal(data);
        return encryptedData;
    }

    public static String encrypt(String input) {
        String result = "input";
        try {
            result = base64Encode(desEncrypt(input.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String base64Encode(byte[] s) {
        if (s == null)
            return null;
        return Base64.encodeToString(s, Base64.DEFAULT);
    }
    
    public static String decrypt(String input) throws Exception {
        byte[] result = Base64.decode(input, Base64.DEFAULT);
        return new String(desDecrypt(result));
    }
    
    
    public static byte[] desDecrypt(byte[] encryptText) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(DESKey.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key, sr);
        byte encryptedData[] = encryptText;
        byte decryptedData[] = cipher.doFinal(encryptedData);
        return decryptedData;
    }

}
