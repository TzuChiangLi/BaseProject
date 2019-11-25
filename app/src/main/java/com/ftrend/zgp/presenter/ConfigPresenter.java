package com.ftrend.zgp.presenter;

import android.text.TextUtils;

import com.ftrend.zgp.api.ConfigContract;
import com.ftrend.zgp.model.Config;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.printer.PrintConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public class ConfigPresenter implements ConfigContract.ConfigPresenter {
    private ConfigContract.ConfigView mView;

    private ConfigPresenter(ConfigContract.ConfigView mView) {
        this.mView = mView;
    }

    public static ConfigPresenter createPresenter(ConfigContract.ConfigView mView) {
        return new ConfigPresenter(mView);
    }


    @Override
    public void loadCfgItem() {
        String[] type = {"多设置项分组预留位置，可删除", ""};
        String[] item = {"", "打印小票"};
        List<Config> configList = new ArrayList<>();
        for (int i = 0; i < type.length; i++) {
            Config config = new Config();
            if (TextUtils.isEmpty(type[i])) {
                config.setType(false);
                config.setCfgName(item[i]);
            } else {
                config.setType(true);
                config.setCfgName(type[i]);
            }
            configList.add(config);
        }
        //获取打印设置状态
        PrintConfig printConfig = ZgParams.getPrinterConfig();
        configList.get(1).setOn(printConfig.isPrintTrade() ? true : false);
        //更新界面
        mView.initCfgItem(configList);
    }

    @Override
    public void print(boolean flag) {
        PrintConfig printConfig = ZgParams.getPrinterConfig();
        printConfig.setPrintTrade(flag);
        mView.show(String.format("小票打印：%s", flag ? "开启" : "禁用"));
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }
}
