package com.ftrend.zgp.base;

/**
 * @author LZQ
 * MVP中的V层基类，Activity或者Fragment继承后需要注入Presenter
 */
public interface BaseView<T> {
    /**
     * 注入presenter
     *
     * @param presenter 注入presenter
     */
    void setPresenter(T presenter);
}
