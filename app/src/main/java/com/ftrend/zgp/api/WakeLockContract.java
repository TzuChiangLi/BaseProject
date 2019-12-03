package com.ftrend.zgp.api;

import android.animation.AnimatorSet;
import android.view.View;

import com.ftrend.zgp.base.BaseView;

/**
 * @author liziqiang@ftrend.cn
 */

public interface WakeLockContract {
    interface WakePresenter {
        void enter(String pwd);
        void start(View...views);
        void onDestory();
    }

    interface WakeLockView extends BaseView<WakeLockContract.WakePresenter> {
        void show(String msg);
        void start(AnimatorSet alphaSet,AnimatorSet translationSet);
        void success();
    }
}
