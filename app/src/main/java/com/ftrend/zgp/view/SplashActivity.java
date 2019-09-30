package com.ftrend.zgp.view;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;

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
        SoulPermission.getInstance().checkAndRequestPermissions(
                Permissions.build(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                new CheckRequestPermissionsListener() {
                    @Override
                    public void onAllPermissionOk(Permission[] allPermissions) {
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

                    @Override
                    public void onPermissionDenied(Permission[] refusedPermissions) {
                        String refusedPermission = "";
                        for (int i = 0; i < refusedPermissions.length; i++) {
                            refusedPermission += String.format("%s%s\n", "以下权限申请失败：", refusedPermissions[i]);
                        }
                        MessageUtil.showWarning("请重新启动并赋予相关权限！");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1500);
                    }
                });


    }


}
