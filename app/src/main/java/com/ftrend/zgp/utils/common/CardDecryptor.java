package com.ftrend.zgp.utils.common;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 磁卡卡号解密
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/24
 */
public class CardDecryptor {

    private static List<byte[]> subKey = new ArrayList<>();

    public static String cardDecryStr(String str, String key) {
        //处理密码，取前8字节，不足8字节补零
        byte[] keyByte = new byte[8];
        initBytes(keyByte);
        byte[] tempBytes = key.getBytes();
        int len = Math.min(keyByte.length, tempBytes.length);
        System.arraycopy(tempBytes, 0, keyByte, 0, len);
        //生成中间密码
        initSubKey();
        makekey(keyByte, subKey);

        //处理密文，只保留数字
        StringBuilder sb = new StringBuilder(str);
        for (int i = sb.length() - 1; i >= 0; i--) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                sb.deleteCharAt(i);
            }
        }
        String mids = sb.toString().toUpperCase();

        String sTail = mids.length() > 24 ? mids.substring(24) : "";
        if (mids.length() > 24) {
            mids = mids.substring(0, 24);
        }
        byte[] strByte = new byte[8];
        byte[] midsByte = mids.getBytes();
        for (int j = 0; j < 8; j++) {
            byte midi = (byte) (midsByte[j * 3] - (byte) '0');
            byte midc = (byte) (midi * 100);

            midi = (byte) (midsByte[j * 3 + 1] - (byte) '0');
            midc = (byte) (midc + midi * 10);

            midi = (byte) (midsByte[j * 3 + 2] - (byte) '0');
            midc = (byte) (midc + midi);

            strByte[j] = midc;
        }
        byte[] outByte = new byte[8];
        desData(strByte, outByte);
        /*byte[] outByte = decode(keyByte, strByte);//.getBytes();*/

        String strResult = new String(outByte).trim();
        byte[] resultBytes = strResult.getBytes();
        sb = new StringBuilder();
        for (int i = 0; i < resultBytes.length; i++) {
            byte midc = resultBytes[i];
            if (midc >= 100) {
                sb.append((char) (midc - 100 + 48));
            } else {
                sb.append((char) (midc / 10 + 48));
                sb.append((char) (midc % 10 + 48));
            }
        }
        sb.append(sTail);

        return sb.toString();
    }

    private static void makekey(byte[] inKey, List<byte[]> outKey) {

    }

    private static void desData(byte[] inData, byte[] outData) {
        // inData, outData 都为8Bytes，否则出错
    }

    private static void initBytes(byte[] bytes) {
        initBytes(bytes, (byte) 0);
    }

    private static void initBytes(byte[] bytes, byte initValue) {
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = initValue;
        }
    }

    private static void initSubKey() {
        subKey.clear();
        for (int i = 0; i < 16; i++) {
            byte[] bytes = new byte[6];
            initBytes(bytes);
            subKey.add(bytes);
        }
    }


    private final static String TRANSFORMATION = "DES/CBC/PKCS5Padding";//DES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
    private final static String IVPARAMETERSPEC = "01020304";////初始化向量参数，AES 为16bytes. DES 为8bytes.
    private final static String ALGORITHM = "DES";//DES是加密方式

    /**
     * DES算法，加密
     *
     * @param data 待加密字符串
     * @param key  加密私钥，长度不能够小于8位
     * @return 加密后的字节数组
     */
    public static byte[] encode(byte[] key, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            IvParameterSpec iv = new IvParameterSpec(IVPARAMETERSPEC.getBytes());
            Key rawKey = getRawKey(key);
            cipher.init(Cipher.ENCRYPT_MODE, rawKey, iv);
            return cipher.doFinal(data);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * DES算法，解密
     *
     * @param data 待解密字符串
     * @param key  解密私钥，长度不能够小于8位
     * @return 解密后的字节数组
     */
    public static byte[] decode(byte[] key, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            IvParameterSpec iv = new IvParameterSpec(IVPARAMETERSPEC.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, getRawKey(key), iv);
            return cipher.doFinal(data);
        } catch (Exception e) {
            return null;
        }
    }

    // 对密钥进行处理
    private static Key getRawKey(byte[] key) throws Exception {
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        return keyFactory.generateSecret(dks);
    }
}
