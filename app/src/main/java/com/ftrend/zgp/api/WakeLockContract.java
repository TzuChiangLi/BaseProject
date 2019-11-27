package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;

/**
 * @author liziqiang@ftrend.cn
 */

public interface WakeLockContract {
    interface WakePresenter {
        void enter(String pwd);

        void onDestory();
    }

    interface WakeLockView extends BaseView<WakeLockContract.WakePresenter> {
        void show(String msg);

        void success();
    }
}
