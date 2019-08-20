package com.ftrend.zgp.base;

import com.ftrend.zgp.utils.http.HttpCallBack;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 统一订阅的基类
 *
 * @author liziqiang@ftrend.cn
 */
public class BaseSubscribe {
    /**
     * 订阅
     *
     * @param observable //     * @param callBack   , HttpCallBack<T> callBack
     * @param <T>
     */
    protected <T> void toDetachAndSubscribe(Observable observable, final HttpCallBack<T> callBack) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver(callBack));
    }


}
