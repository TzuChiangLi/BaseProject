package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.ftrend.zgp.utils.ZgParams;

/**
 * 启动闪屏
 *
 * @author liziqiang@ftrend.cn
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //根据本地参数来确定跳转页面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //后面根据首页再做延时操作
                Intent intent;
                if (TextUtils.isEmpty(ZgParams.getPosCode())) {
                    intent = new Intent(SplashActivity.this, RegisterActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 2 * 1000);

    }


}
