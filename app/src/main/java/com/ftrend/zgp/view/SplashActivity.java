package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.ftrend.zgp.model.User;
import com.ftrend.zgp.model.User_Table;
import com.ftrend.zgp.utils.test.TestDataImporter;
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
        //test
        long count = SQLite.select(count(User_Table.userCode)).from(User.class).count();
        if (count == 0) {
            TestDataImporter.importAll();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //后面根据首页再做延时操作
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);//HomeActivity
                startActivity(intent);
                finish();
            }
        }, 2 * 1000);

    }
}
