package com.ftrend.zgp.app;

import android.app.Application;

import com.ftrend.zgp.utils.LogUtil;
import com.ftrend.zgp.utils.ToastUtil;

/**
 * @content 初始化相关工具组件
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Toast样式
        ToastUtil.initStyle();
        //设置日志输出为打印日志，出现error时保存到log.txt文件中
        LogUtil.setShowLog(true);
        LogUtil.setSaveError(true);
    }

}
