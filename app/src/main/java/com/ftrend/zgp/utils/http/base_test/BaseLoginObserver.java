package com.ftrend.zgp.utils.http.base_test;

import com.ftrend.zgp.utils.http.HttpCallBack;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * 被观察者的封装
 *
 * @author liziqiang@ftrend.cn
 */
public class BaseLoginObserver implements Observer<Login> {
    private HttpCallBack mCallBack;

    public BaseLoginObserver(HttpCallBack callBack) {//HttpCallBack<T> callBack
        mCallBack = callBack;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mCallBack.onStart();
    }

    @Override
    public void onNext(Login login) {
        mCallBack.onSuccess(login, null);
    }

//    @Override
//    public void onNext(BaseResponse<T> tBaseResponse) {
//        mCallBack.onSuccess(tBaseResponse.getBody(), tBaseResponse.getHead());
//    }

    @Override
    public void onError(Throwable e) {
        //此处需要对异常处理进行封装分类
        mCallBack.onError(e.getMessage());

    }

    @Override
    public void onComplete() {
        mCallBack.onFinish();
    }
}
