package com.ftrend.zgp.view;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.task.DataDownloadTask;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;

import java.util.Locale;

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
                                if (TextUtils.isEmpty(ZgParams.getPosCode())) {
                                    //未注册，进入注册界面
                                    Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if (!ZgParams.getInitFlag().equals("1")) {
                                    //已注册但未完成初始化，进入初始化界面
                                    Intent intent = new Intent(SplashActivity.this, InitActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    //已注册，自动更新数据并进入登录界面
                                    DataDownloadTask.taskStart(false, new DataDownloadTask.ProgressHandler() {
                                        @Override
                                        public void handleProgress(int percent, boolean isFailed, String msg) {
                                            System.out.println(String.format(Locale.getDefault(), "基础数据下载进度：%d%% %s", percent, msg));
                                            if (percent >= 100 || isFailed) {
                                                //重新读取配置参数
                                                ZgParams.loadParams();
                                                //跳转登录页
                                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                }
                            }
                        }, 1000);
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
