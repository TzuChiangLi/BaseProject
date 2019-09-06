package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //后面根据首页再做延时操作
                Intent intent = new Intent(SplashActivity.this, InitActivity.class);//HomeActivity
                startActivity(intent);
                finish();
            }
        }, 2 * 1000);

    }

}
