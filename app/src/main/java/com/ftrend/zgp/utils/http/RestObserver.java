package com.ftrend.zgp.utils.http;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;


/**
 * 被观察者的封装
 * 笔记：如果不需要泛型，Observer后面是实体类那实现类不需要跟泛型
 *
 * @author liziqiang@ftrend.cn
 */
public class RestObserver<T> extends AbstractObserver implements Observer<RestResponse<T>> {

    private HttpCallBack<T> mCallBack;

    public RestObserver(HttpCallBack<T> callBack) {
        mCallBack = callBack;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mCallBack.onStart();
    }

    @Override
    public void onNext(RestResponse<T> response) {
        if (response.succeed()) {
            mCallBack.onSuccess(response.getBody());
        } else {
            mCallBack.onFailed(response.getHead().getRetFlag(), response.getHead().getRetMsg());
        }
    }

    @Override
    public void onError(Throwable e) {

        int errorCode = (e instanceof HttpException) ? ((HttpException) e).code() : 999;
        String errorMessage = parseHttpError(e);
        mCallBack.onHttpError(errorCode, errorMessage);
    }

    @Override
    public void onComplete() {
        mCallBack.onFinish();

    }
}
