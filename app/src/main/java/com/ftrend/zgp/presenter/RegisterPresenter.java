package com.ftrend.zgp.presenter;

import android.Manifest;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.HttpCallBack;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.qw.soul.permission.SoulPermission;

import java.util.Map;

/**
 * 设备注册界面P层
 *
 * @author liziqiang@ftrend.cn
 */
public class RegisterPresenter implements Contract.RegisterPresenter {
    private Contract.RegisterView mView;
    private static String devSn = "";
    private String regCode = "", posCode = "";

    private RegisterPresenter(Contract.RegisterView mView) {
        this.mView = mView;
    }

    public static RegisterPresenter createPresenter(Contract.RegisterView mView) {
        return new RegisterPresenter(mView);
    }

    private RestResultHandler regHandler = new RestResultHandler() {
        @Override
        public void onSuccess(Map<String, Object> body) {
            //注册成功，设备注册参数写入数据库
            ZgParams.saveAppParams("serverUrl", ZgParams.getServerUrl());
            ZgParams.saveAppParams("posCode", posCode);
            ZgParams.saveAppParams("regCode", regCode);
            ZgParams.saveAppParams("devSn", devSn);
            ZgParams.saveAppParams("initFlag", "0");
            //注册成功后，刷新全局变量
            ZgParams.loadParams();
            //调用UI
            mView.registerSuccess();
        }

        @Override
        public void onFailed(String errorCode, String errorMsg) {
            // TODO: 2019/9/9 显示注册失败的提示信息
        }
    };


    @Override
    public void register(String url, final String posCode, final String regCode) {
        //保存注册码
        this.regCode = regCode;
        this.posCode = posCode;
        //在联网或者执行心跳之前更新BASE_URL
        ZgParams.setServerUrl(url);
        //检查权限
        SoulPermission.getInstance().checkSinglePermission(Manifest.permission.READ_PHONE_STATE);
        //获取SN码
        devSn = PhoneUtils.getSerial();

        //验证服务地址是否有效
        RestSubscribe.getInstance().ping(new HttpCallBack<String>() {
            @Override
            public void onSuccess(String body) {
                //后台服务可用，注册设备
                RestSubscribe.getInstance().devReg(posCode, regCode, devSn,
                        String.format("%s %s", DeviceUtils.getManufacturer(), DeviceUtils.getModel()),
                        new RestCallback(regHandler));
            }

            @Override
            public void onHttpError(int errorCode, String errorMsg) {
                // TODO: 2019/9/9 显示服务器地址无效的提示信息
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
            }

            @Override
            public void onFinish() {
            }
        });
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }


}
