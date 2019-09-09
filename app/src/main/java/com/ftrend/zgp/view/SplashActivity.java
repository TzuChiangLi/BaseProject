package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.ftrend.zgp.model.AppParams;
import com.ftrend.zgp.model.AppParams_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import static com.raizlabs.android.dbflow.sql.language.Method.count;

/**
 * 启动闪屏
 *
 * @author liziqiang@ftrend.cn
 */
public class SplashActivity extends AppCompatActivity {
//    @BindView(R.id.img)
//    ImageView mImg;
//    private int i =1;
//    @OnClick(R.id.img)
//    public void change(){
//        switch (i){
//            case 1:
//                mImg.setImageResource(R.mipmap.splash);
//                i++;
//                break;
//            case 2:
//                i++;
//                mImg.setImageResource(R.mipmap.splash2);
//                break;
//            case 3:
//                mImg.setImageResource(R.mipmap.splash3);
//                i=1;
//                break;
//        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initAppParams();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //后面根据首页再做延时操作
                Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
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
            appParams = new AppParams("printerConfig", "");
            appParams.insert();
            appParams = new AppParams("cardConfig", "");
            appParams.insert();
        }
    }

}
