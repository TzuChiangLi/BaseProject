package com.ftrend.zgp.utils.http.base_test;

import com.ftrend.zgp.utils.http.BaseResponse;
import com.ftrend.zgp.utils.http.HttpCallBack;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LoginObserver<Login> implements Observer<Login> {
    private HttpCallBack<Login> mCallBack;

    public LoginObserver(HttpCallBack<Login> mCallBack) {
        this.mCallBack = mCallBack;
    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(Login login) {
    }


    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
