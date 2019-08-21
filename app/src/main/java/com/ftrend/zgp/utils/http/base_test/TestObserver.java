package com.ftrend.zgp.utils.http.base_test;

import com.ftrend.zgp.utils.http.BaseResponse;
import com.ftrend.zgp.utils.http.HttpCallBack;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 备注：此处User是传入HttpCallBack需要的具体实体类
 *
 * @author liziqiang@ftrend.cn
 */
public class TestObserver<User> implements Observer<BaseResponse<User>> {
    private HttpCallBack<User> mCallBack;

    public TestObserver(HttpCallBack<User> mCallBack) {
        this.mCallBack = mCallBack;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mCallBack.onStart();
    }

    @Override
    public void onNext(BaseResponse<User> userBaseResponse) {
        mCallBack.onSuccess(userBaseResponse.getBody(), userBaseResponse.getHead());
    }


    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
