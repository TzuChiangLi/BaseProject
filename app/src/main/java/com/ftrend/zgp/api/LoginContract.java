package com.ftrend.zgp.api;

import android.content.Context;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.User;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface LoginContract {
    interface LoginPresenter {
        /**
         * 初始化可登录专柜数据
         */
        void initDepData(Context context);

        /**
         * 初始化可登录用户数据
         */
        void initUserData();

        /**
         * 验证用户信息
         */
        void checkUserInfo(String userCode, String userPwd, String depCode);

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface LoginView extends BaseView<LoginContract.LoginPresenter> {
        /**
         * 返回专柜信息
         *
         * @param depData 专柜信息
         */
        void setDepData(List<Dep> depData);

        /**
         * 返回可登录用户信息
         *
         * @param userData 可登录用户
         */
        void setUserData(List<User> userData);

        /**
         * 登录失败
         */
        void loginFailed(String failedMsg);

        /**
         * 登录成功
         */
        void loginSuccess(User user, Dep dep);

    }

}
