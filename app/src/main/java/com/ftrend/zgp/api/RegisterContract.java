package com.ftrend.zgp.api;

import android.content.Context;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.Menu;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface RegisterContract {
    interface RegisterPresenter {

        void register(String url, String posCode, String regCode);

        /**
         * 销毁，防止泄露
         */
        void onDestory();

    }

    interface RegisterView extends BaseView<RegisterContract.RegisterPresenter> {

        /**
         * 注册成功
         */
        void registerSuccess();

        /**
         * 错误原因回调
         *
         * @param error 错误原因
         */
        void registerError(String error);

    }

}
