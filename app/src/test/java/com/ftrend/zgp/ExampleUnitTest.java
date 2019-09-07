package com.ftrend.zgp;

import com.ftrend.zgp.utils.common.EncryptUtill;

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
    public void pwdEncrypt() {
        String pwd = "123abc";
        String encrypted = EncryptUtill.pwdEncrypt(pwd);//7A>AJY;@;
        System.out.println("加密测试: " + pwd + " => " + encrypted);
        System.out.println("解密测试: " + encrypted + " => " + EncryptUtill.pwdDecrypt(encrypted));
        System.out.println();
        System.out.println("用户密码: 李雪勤 => " + EncryptUtill.pwdDecrypt("6=R>D<H?."));
        System.out.println("用户密码: 蒋丽 => " + EncryptUtill.pwdDecrypt("79>>H*H0Z"));
        System.out.println("用户密码: 高丽 => " + EncryptUtill.pwdDecrypt("71S><CH;."));
        System.out.println("用户密码: 授权卡号 => " + EncryptUtill.pwdDecrypt("6A<>0/I;4"));
        System.out.println("用户密码: 退货账号 => " + EncryptUtill.pwdDecrypt("7ANAL?;?)"));
        System.out.println("用户密码: 宋昆林 => " + EncryptUtill.pwdDecrypt("7C?A>7"));
        System.out.println("用户密码: 周显云 => " + EncryptUtill.pwdDecrypt("7-:>D,H;5"));

//        System.out.println(Charset.availableCharsets());
    }
}