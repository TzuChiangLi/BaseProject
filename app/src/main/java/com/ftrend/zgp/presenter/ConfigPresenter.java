package com.ftrend.zgp.presenter;

import android.text.TextUtils;

import com.ftrend.zgp.api.ConfigContract;
import com.ftrend.zgp.model.Config;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.log.LogUtil;
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
        String[] type = {"打印设置", ""};
        String[] item = {"", "结算成功自动打印交易小票"};
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
        if (ZgParams.saveAppParams("printerConfig", PrintConfig.toJson(printConfig))) {
            mView.show("参数已保存");
        } else {
            mView.show("参数写入失败");
        }
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }
}
