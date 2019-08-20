package com.ftrend.zgp.example;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 统一订阅的基类
 *
 * @author LZQ
 */
public class BaseExampleSubscribe {
    /**
     * 订阅
     *
     * @param observable
     * @param callBack
     * @param <T>
     */
    protected <T> void toDetachAndSubscribe(Observable<? extends KeyWord> observable, ExampleCallBack<T> callBack) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ExampleObserver<>(callBack));
    }

}
