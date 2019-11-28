package com.ftrend.zgp;

import com.ftrend.zgp.utils.common.EncryptUtil;

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
        System.out.println("用户密码: 李雪勤 => " + EncryptUtil.pwdDecrypt("6=R>D<H?."));
        System.out.println("用户密码: 蒋丽 => " + EncryptUtil.pwdDecrypt("79>>H*H0Z"));
        System.out.println("用户密码: 高丽 => " + EncryptUtil.pwdDecrypt("71S><CH;."));
        System.out.println("用户密码: 授权卡号 => " + EncryptUtil.pwdDecrypt("7CZAJ4"));
        System.out.println("用户密码: 退货账号 => " + EncryptUtil.pwdDecrypt("7CZAJ4"));
        System.out.println("用户密码: 宋昆林 => " + EncryptUtil.pwdDecrypt("7C?A>7"));
        System.out.println("用户密码: 周显云 => " + EncryptUtil.pwdDecrypt("7-:>D,H;5"));
        System.out.println("用户密码: 杨莉 => " + EncryptUtil.pwdDecrypt("7AUAL;;?."));
        System.out.println("刷卡密码 => " + EncryptUtil.pwdDecrypt("7AUAL;;?."));
        System.out.println("卡号密码 => " + EncryptUtil.pwdDecrypt("6A>><7I@_"));
    }

}