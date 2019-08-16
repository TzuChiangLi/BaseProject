package com.ftrend.zgp.example;

import android.content.Context;

import com.ftrend.zgp.utils.http.HttpApi;
import com.ftrend.zgp.utils.http.HttpUtil;
import com.ftrend.zgp.utils.http.KeyWord;

public class ExampleSubscribe extends BaseExampleSubscribe {
    private HttpApi api;
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
        api = HttpUtil.getInstance().create(HttpApi.class);
    }

    public void getHotKey(Context context, final ExampleCallBack<KeyWord> callBack) {
        toDetachAndSubscribe(api.example(), callBack);
    }
}
