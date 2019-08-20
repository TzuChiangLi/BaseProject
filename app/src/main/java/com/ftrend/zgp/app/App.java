package com.ftrend.zgp.app;

import android.app.Application;

import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.toast.ToastUtil;
import com.qw.soul.permission.SoulPermission;

/**
 * @author LZQ
 * @content 初始化相关工具组件
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //region 吐司初始化
        //初始化Toast样式
        ToastUtil.init(this);
        //endregion
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


}
