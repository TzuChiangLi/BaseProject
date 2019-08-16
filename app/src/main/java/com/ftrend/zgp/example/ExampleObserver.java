package com.ftrend.zgp.example;


import com.ftrend.zgp.utils.LogUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * 被观察者的封装
 *
 * @author LZQ
 */
public class ExampleObserver<T> implements Observer<KeyWord> {
    private ExampleCallBack<T> mCallBack;

    public ExampleObserver(ExampleCallBack<T> callBack) {
        mCallBack = callBack;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mCallBack.onStart();
    }

    @Override
    public void onNext(KeyWord tKeyWord) {
        mCallBack.onSuccess(tKeyWord.getData(), tKeyWord.getErrorMsg());
        LogUtil.d("----Observer："+tKeyWord.getData().get(0).getName());
    }

    @Override
    public void onError(Throwable e) {
        //此处需要对异常处理进行封装分类
        LogUtil.d("----Observer："+e.getMessage());
        mCallBack.onError();

    }

    @Override
    public void onComplete() {
        mCallBack.onFinish();

    }
}
