package com.ftrend.zgp;

import android.util.Base64;

import com.blankj.utilcode.util.GsonUtils;
import com.ftrend.zgp.utils.common.CardDecryptor;
import com.ftrend.zgp.utils.common.EncryptUtill;
import com.ftrend.zgp.utils.sunmi.VipCardParams;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void pwdDecrypt() {
        System.out.println("用户密码: 李雪勤 => " + EncryptUtill.pwdDecrypt("6=R>D<H?."));
        System.out.println("用户密码: 蒋丽 => " + EncryptUtill.pwdDecrypt("79>>H*H0Z"));
        System.out.println("用户密码: 高丽 => " + EncryptUtill.pwdDecrypt("71S><CH;."));
        System.out.println("用户密码: 授权卡号 => " + EncryptUtill.pwdDecrypt("6A<>0/I;4"));
        System.out.println("用户密码: 退货账号 => " + EncryptUtill.pwdDecrypt("7ANAL?;?)"));
        System.out.println("用户密码: 宋昆林 => " + EncryptUtill.pwdDecrypt("7C?A>7"));
        System.out.println("用户密码: 周显云 => " + EncryptUtill.pwdDecrypt("7-:>D,H;5"));
        System.out.println("用户密码: 杨莉 => " + EncryptUtill.pwdDecrypt("7AUAL;;?."));
    }

    @Test
    public void test() {
        VipCardParams vipCardParams = new VipCardParams();
//        vipCardParams.setVipAndCardFixLen(false);
        vipCardParams.setCardPreCode("FN");
//        vipCardParams.setVipCodeMaxLen(5);
        System.out.println(GsonUtils.toJson(vipCardParams));
        String code = "12345678";
        String cardCode = code;
        if (code.length() > vipCardParams.getVipCodeMaxLen()) {
            cardCode = code.substring(code.length() - vipCardParams.getVipCodeMaxLen());
        } else if (vipCardParams.isVipAndCardFixLen()) {
            String preCode = vipCardParams.getCardPreCode() + "00000000000000000000";
            cardCode = preCode.substring(0, vipCardParams.getVipCodeMaxLen() - code.length()) + code;
        }
        System.out.println(cardCode);
    }

    @Test
    public void test2() {
        System.out.println("1234567890".getBytes().length);
        byte b = (byte) 'a';
        byte[] data = new byte[]{b, b, b, b, b, 0, 0, 0};
        String s = new String(data).trim();
        System.out.println(s);
        System.out.println(s.length());
    }

    @Test
    public void testDes() {
        byte[] bytes = CardDecryptor.encode("12345678".getBytes(), "123456789012345".getBytes());
        System.out.println(Base64.encodeToString(bytes, Base64.DEFAULT));
        bytes = CardDecryptor.decode("12345678".getBytes(), "142234007043130132191006".getBytes());
        System.out.println(Base64.encodeToString(bytes, Base64.DEFAULT));

        String cardCode = CardDecryptor.cardDecryStr("142234007043130132191006", "12345678");
        System.out.println(cardCode);
    }
}