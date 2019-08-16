package com.ftrend.zgp.example;

import com.ftrend.zgp.utils.http.HttpUtil;
import com.ftrend.zgp.base.KeyWord;

public class ExampleSubscribe extends BaseExampleSubscribe {
    private ExampleApi api;
    private static ExampleSubscribe INSTANCE;

    /**
     * @return 返回请求工具类的单例
     */
    public static ExampleSubscribe getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExampleSubscribe();
        }
        return INSTANCE;
    }

    public ExampleSubscribe() {
        api = HttpUtil.getInstance().create(ExampleApi.class);
    }

    public void getHotKey(final ExampleCallBack<KeyWord> callBack) {
        toDetachAndSubscribe(api.example(), callBack);
    }
}
