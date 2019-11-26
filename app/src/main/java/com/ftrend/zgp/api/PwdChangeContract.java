package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;

/**
 * @author liziqiang@ftrend.cn
 */

public interface PwdChangeContract {
    interface PwdChangePresenter {
        /**
         * 修改密码
         */
        void modify(String old, String newPwd, String confirm);

        /**
         * 销毁
         */
        void onDestory();
    }

    interface PwdChangeView extends BaseView<PwdChangeContract.PwdChangePresenter> {
        /**
         * @param msg 显示状态
         */
        void show(String msg);

        /**
         * @param msg 修改成功
         */
        void showSuccess(String msg);

        /**
         * @param err 修改失败
         */
        void showError(String err);
    }
}
