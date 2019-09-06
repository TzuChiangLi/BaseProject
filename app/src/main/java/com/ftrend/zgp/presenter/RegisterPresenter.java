package com.ftrend.zgp.presenter;

import android.Manifest;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.utils.http.HttpCallBack;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.qw.soul.permission.SoulPermission;

/**
 * 设备注册界面P层
 *
 * @author liziqiang@ftrend.cn
 */
public class RegisterPresenter implements Contract.RegisterPresenter, HttpCallBack {
    private Contract.RegisterView mView;


    private RegisterPresenter(Contract.RegisterView mView) {
        this.mView = mView;
    }

    public static RegisterPresenter createPresenter(Contract.RegisterView mView) {
        return new RegisterPresenter(mView);
    }


    @Override
    public void register(String url, String posCode, String regCode) {
        SoulPermission.getInstance().checkSinglePermission(Manifest.permission.READ_PHONE_STATE);
//        RestSubscribe.getInstance().devReg(posCode, regCode, PhoneUtils.getSerial(),
//                String.format("%s %s", DeviceUtils.getManufacturer(), DeviceUtils.getModel()),
//                );
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onSuccess(Object body) {

    }

    @Override
    public void onFailed(String errorCode, String errorMsg) {

    }

    @Override
    public void onHttpError(int errorCode, String errorMsg) {

    }

    @Override
    public void onFinish() {

    }
}
