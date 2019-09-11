package com.ftrend.zgp;

import android.app.Application;
import android.content.Context;

import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.log.LogUtil;
import com.qw.soul.permission.SoulPermission;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * 初始化相关工具组件
 *
 * @author liziqiang@ftrend.cn
 */
public class App extends Application {

    // 全局Context对象
    private static Context context = null;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        FlowManager.init(this);
        //加载全局参数
        ZgParams.loadParams();
        //region 打印日志初始化
        //设置日志输出为打印日志，出现error时保存到log.txt文件中
        LogUtil.setShowLog(true);
        LogUtil.setSaveError(true);
        //endregion
        //region 权限初始化
        //权限申请初始化
        SoulPermission.init(this);
        //是否打印日志
        SoulPermission.setDebug(true);
        //老的系统默认权限直接授予
        SoulPermission.skipOldRom(true);
        //endregion
    }

    public static Context getContext() {
        return context;
    }
}
