package com.ftrend.zgp.utils.common;

import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * 加密工具类
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/3
 */
public class EncryptUtil {

    /**
     * 字符串MD5加密
     *
     * @param pwd
     * @return
     */
    public final static String md5(String pwd) {
        //用于加密的字符
        char md5String[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            //使用平台的默认字符集将此 String 编码为 byte序列，并将结果存储到一个新的 byte数组中
            byte[] btInput = pwd.getBytes();

            //信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。
            MessageDigest mdInst = MessageDigest.getInstance("MD5");

            //MessageDigest对象通过使用 update方法处理数据， 使用指定的byte数组更新摘要
            mdInst.update(btInput);

            // 摘要更新之后，通过调用digest（）执行哈希计算，获得密文
            byte[] md = mdInst.digest();

            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {   //  i = 0
                byte byte0 = md[i];  //95
                str[k++] = md5String[byte0 >>> 4 & 0xf];    //    5
                str[k++] = md5String[byte0 & 0xf];   //   F
            }

            //返回经过加密后的字符串
            return new String(str);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 用户密码解密
     *
     * @param src
     * @return
     */
    public static String pwdDecrypt(String src) {
        if (src == null || src.isEmpty()) {
            return src;
        }
        if (src.startsWith("11")) {
            return src.substring(2);
        }

        int i, len;
        byte midc, midb;

        byte[] Str = src.getBytes(Charset.forName("ISO-8859-1"));
        byte[] mids = new byte[Str.length * 2 / 3];
        for (i = 0; i < Str.length / 3; i++) {
            byte[] temp = new byte[]{Str[i * 3], Str[i * 3 + 1], Str[i * 3 + 2]};
            byte[] temp2 = GetFromReadStr(temp);
            mids[i * 2] = temp2[0];
            mids[i * 2 + 1] = temp2[1];
        }
        len = mids.length;
        byte[] s1 = new byte[len];
        for (i = 0; i < len / 2; i++) {
            s1[i] = mids[i * 2];
            s1[len - 1 - i] = mids[i * 2 + 1];
        }

        midb = 28;
        mids = new byte[s1.length];
        for (i = 0; i < s1.length; i++) {
            midc = (byte) (s1[i] ^ midb);
            mids[i] = midc;
            midb = (byte) (((s1[i] & 0xFF ^ midb) + midb) ^ 256);
        }
        return new String(CutRightZero(mids));
    }

    /**
     * IC卡支付密码解密
     *
     * @param src
     * @return
     */
    public static String cardPwdDecrypt(String src) {
        if (src == null || src.isEmpty()) {
            return src;
        }
        String mids1 = src.trim().toUpperCase();
        if (mids1.length() % 2 != 0) {
            return "";
        }

        String mids = "";
        byte midb = 37;
        byte midc;
        int midi;
        while (mids1.length() > 0) {
            String mids2 = mids1.substring(0, 2);
            mids1 = mids1.substring(2);
            //高字节
            char c = mids2.charAt(0);
            if (c >= 'A' && c <= 'F') {
                midi = (byte) c - (byte) 'A' + 10;
            } else {
                midi = (byte) c - (byte) '0';
            }
            midc = (byte) (midi * 16);
            //低字节
            c = mids2.charAt(1);
            if (c >= 'A' && c <= 'F') {
                midi = (byte) c - (byte) 'A' + 10;
            } else {
                midi = (byte) c - (byte) '0';
            }
            midc += midi;
            //异或解密
            mids += (char) (midc ^ midb);
            midb = (byte) (((midc ^ midb) + midb) % 256);
        }
        return mids;
    }

    private static byte[] CutRightZero(byte[] AStr) {
        int len = AStr.length;
        while ((len > 0) && (AStr[len - 1] == 0)) len--;
        byte[] result = new byte[len];
        System.arraycopy(AStr, 0, result, 0, len);
        return result;
    }

    private static byte[] GetFromReadStr(byte[] src) {
        int a, b, c;
        int s1, s2, s3, s4;

        byte[] AStr = new byte[]{50, 45, 40};
        if (src.length > 0) AStr[0] = src[0];
        if (src.length > 1) AStr[1] = src[1];
        if (src.length > 2) AStr[2] = src[2];

        a = AStr[0] - 50;
        b = AStr[1] - 45;
        c = AStr[2] - 40;
        s1 = a;
        s2 = b / 4;
        s3 = b % 4;
        s4 = c;

        byte[] result = new byte[2];
        result[0] = (byte) (s1 * 8 + s2);
        result[1] = (byte) (s3 * 64 + s4);
        return result;
    }
}
