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
        System.out.println("pwdEncrypt: " + pwd + " => " + encrypted);
        System.out.println("pwdDecrypt: " + encrypted + " => " + EncryptUtill.pwdDecrypt(encrypted));
        encrypted = "7A>AJY;@;";
        System.out.println("pwdDecrypt: " + encrypted + " => " + EncryptUtill.pwdDecrypt(encrypted));
        encrypted = "79O?<FI76";
        System.out.println("pwdDecrypt: " + encrypted + " => " + EncryptUtill.pwdDecrypt(encrypted));
        encrypted = "7APAH8H3-";
        System.out.println("pwdDecrypt: " + encrypted + " => " + EncryptUtill.pwdDecrypt(encrypted));

//        System.out.println(Charset.availableCharsets());
    }
}