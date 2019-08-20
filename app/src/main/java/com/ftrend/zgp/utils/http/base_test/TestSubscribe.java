package com.ftrend.zgp.utils.http.base_test;

import com.ftrend.zgp.base.BaseSubscribe;
import com.ftrend.zgp.utils.http.HttpApi;
import com.ftrend.zgp.utils.http.HttpCallBack;
import com.ftrend.zgp.utils.http.HttpUtil;

public class TestSubscribe extends BaseSubscribe {
    private HttpApi api;
    private static TestSubscribe INSTANCE;

    /**
     * @return 返回请求工具类的单例
     */
    public static TestSubscribe getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TestSubscribe();
        }
        return INSTANCE;
    }

    public TestSubscribe() {
        api = HttpUtil.getInstance().create(HttpApi.class);
    }

    public void getResponse(final HttpCallBack<User> callBack) {
        toDetachAndSubscribe(api.getResponse(), callBack);
    }

}
