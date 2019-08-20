package com.ftrend.zgp.utils.http.base_test;

import com.ftrend.zgp.base.BaseSubscribe;
import com.ftrend.zgp.utils.http.HttpApi;
import com.ftrend.zgp.utils.http.HttpCallBack;
import com.ftrend.zgp.utils.http.HttpUtil;

public class LoginSubsrcibe extends BaseSubscribe {
    private HttpApi api;
    private static LoginSubsrcibe INSTANCE;

    /**
     * @return 返回请求工具类的单例
     */
    public static LoginSubsrcibe getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LoginSubsrcibe();
        }
        return INSTANCE;
    }

    public LoginSubsrcibe() {
        api = HttpUtil.getInstance().create(HttpApi.class);
    }

    public void login(String username, String password, final HttpCallBack<Login> callBack) {
        toDetachAndSubscribe(api.login(username, password), callBack);
    }
}
