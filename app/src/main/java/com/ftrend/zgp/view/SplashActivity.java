package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.ftrend.zgp.model.AppParams;
import com.ftrend.zgp.model.AppParams_Table;
import com.ftrend.zgp.utils.ZgParams;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import static com.raizlabs.android.dbflow.sql.language.Method.count;

/**
 * 启动闪屏
 *
 * @author liziqiang@ftrend.cn
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化本地参数
        initAppParams();
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

    /**
     * 初始化本地参数
     */
    private void initAppParams() {
        AppParams appParams;
        long count = SQLite.select(count(AppParams_Table.paramName)).from(AppParams.class).count();
        if (count == 0) {
            appParams = new AppParams("serverUrl", "");
            appParams.insert();
            appParams = new AppParams("posCode", "");
            appParams.insert();
            appParams = new AppParams("regCode", "");
            appParams.insert();
            appParams = new AppParams("devSn", "");
            appParams.insert();
            appParams = new AppParams("initFlag", "0");
            appParams.insert();
            appParams = new AppParams("lastUser", "");
            appParams.insert();
            appParams = new AppParams("lastDep", "");
            appParams.insert();
            appParams = new AppParams("printerConfig", "{}");
            appParams.insert();
            appParams = new AppParams("cardConfig", "{}");
            appParams.insert();
        }
    }

}
