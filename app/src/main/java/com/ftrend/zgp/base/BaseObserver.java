package com.ftrend.zgp.base;

import com.ftrend.zgp.utils.http.BaseResponse;
import com.ftrend.zgp.utils.http.HttpCallBack;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * 被观察者的封装
 * 笔记：如果不需要泛型，Observer后面是实体类那实现类不需要跟泛型
 *
 * @author liziqiang@ftrend.cn
 */
public class BaseObserver<T> implements Observer<BaseResponse<T>> {
    private HttpCallBack<T> mCallBack;

    public BaseObserver(HttpCallBack<T> callBack) {//HttpCallBack<T> callBack
        mCallBack = callBack;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mCallBack.onStart();
    }

    @Override
    public void onNext(BaseResponse<T> tBaseResponse) {
        //如果服务返回正常但是数据有错误，在返回的数据头上应该会有错误代码
        //onErrorMessage()
        mCallBack.onSuccess(tBaseResponse.getBody(), tBaseResponse.getHead());
    }

    @Override
    public void onError(Throwable e) {
        //此处需要对异常处理进行封装分类，如果是服务出现错误此处e应该有回调值
        ////onThrowableMessage()
        mCallBack.onError("");
    }

    @Override
    public void onComplete() {
        mCallBack.onFinish();

    }
}
