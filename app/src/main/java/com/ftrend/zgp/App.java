package com.ftrend.zgp;

import android.app.Application;

import com.ftrend.zgp.model.User;
import com.ftrend.zgp.model.User_Table;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.test.TestDataImporter;
import com.qw.soul.permission.SoulPermission;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import static com.raizlabs.android.dbflow.sql.language.Method.count;

/**
 * 初始化相关工具组件
 *
 * @author liziqiang@ftrend.cn
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
        //test
        long count = SQLite.select(count(User_Table.userCode)).from(User.class).count();
        if (count == 0) {
            TestDataImporter.importAll();
        }
        //加载全局参数
        ZgParams.loadParams();

        //region 吐司初始化
        //初始化Toast样式
        MessageUtil.init(this);
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