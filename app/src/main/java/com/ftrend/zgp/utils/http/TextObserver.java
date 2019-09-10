package com.ftrend.zgp.utils.http;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * 按纯文本的方式来处理HttpResponse的body数据
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/3
 * @see RestObserver
 */
public class TextObserver extends AbstractObserver implements Observer<String> {

    private HttpCallBack<String> mCallBack;

    public TextObserver(HttpCallBack<String> callBack) {
        mCallBack = callBack;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mCallBack.onStart();
    }

    @Override
    public void onNext(String s) {
        mCallBack.onSuccess(s);
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
