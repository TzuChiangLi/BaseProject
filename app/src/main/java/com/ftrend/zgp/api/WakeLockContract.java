package com.ftrend.zgp.api;

import android.animation.AnimatorSet;
import android.view.View;

import com.ftrend.zgp.base.BaseView;

/**
 * @author liziqiang@ftrend.cn
 */

public interface WakeLockContract {
    interface WakePresenter {
        /**
         * @param pwd 密码
         */
        void enter(String pwd);

        /**
         * @param views 动画控件
         */
        void start(View... views);

        /**
         * 注销
         */
        void onDestory();
    }

    interface WakeLockView extends BaseView<WakeLockContract.WakePresenter> {
        /**
         * @param msg 显示提示
         */
        void show(String msg);

        /**
         * @param alphaSet       透明度动画
         * @param translationSet 位置动画
         */
        void start(AnimatorSet alphaSet, AnimatorSet translationSet);

        /**
         * 成功
         */
        void success();
    }
}
