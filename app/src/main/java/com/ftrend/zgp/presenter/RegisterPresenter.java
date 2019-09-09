package com.ftrend.zgp.presenter;

import android.Manifest;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.ftrend.log.LogUtil;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.task.ServerWatcherThread;
import com.qw.soul.permission.SoulPermission;

import java.util.Map;

/**
 * 设备注册界面P层
 *
 * @author liziqiang@ftrend.cn
 */
public class RegisterPresenter extends RestCallback implements Contract.RegisterPresenter {
    private Contract.RegisterView mView;


    private RegisterPresenter(Contract.RegisterView mView) {
        this.mView = mView;
    }

    public static RegisterPresenter createPresenter(Contract.RegisterView mView) {
        return new RegisterPresenter(mView);
    }


    @Override
    public void register(String url, String posCode, String regCode) {
        ZgParams.setServerUrl(url);
        SoulPermission.getInstance().checkSinglePermission(Manifest.permission.READ_PHONE_STATE);

        //启动后台服务心跳检测线程
        ServerWatcherThread watcherThread = new ServerWatcherThread();
        watcherThread.start();

        ZgParams.saveAppParams("serverUrl", url);
        ZgParams.saveAppParams("posCode", posCode);
        ZgParams.saveAppParams("regCode", regCode);
        ZgParams.saveAppParams("devSn", PhoneUtils.getSerial());
        ZgParams.saveAppParams("initFlag", "0");


        RestSubscribe.getInstance().devReg(posCode, regCode, PhoneUtils.getSerial(),
                String.format("%s %s", DeviceUtils.getManufacturer(), DeviceUtils.getModel()),
                this);
    }

    @Override
    public void onSuccess(Map<String, Object> body) {
        super.onSuccess(body);
        LogUtil.d("----body:" + body.size());
        mView.registerSuccess();
    }


    @Override
    public void onStart() {
        LogUtil.d("----onStart");
    }


    @Override
    public void onFailed(String errorCode, String errorMsg) {
        LogUtil.d("----onFailed:" + errorCode + errorMsg);
    }

    @Override
    public void onHttpError(int errorCode, String errorMsg) {
        LogUtil.d("----onHttpError:" + errorCode + errorMsg);
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }


}
