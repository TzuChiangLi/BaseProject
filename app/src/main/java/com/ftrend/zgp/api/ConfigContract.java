package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.Config;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface ConfigContract {
    interface ConfigPresenter {
        /**
         * 加载设置项
         */
        void loadCfgItem();

        /**
         * @param flag 打印小票
         */
        void print(boolean flag);

        void onDestory();
    }

    interface ConfigView extends BaseView<ConfigContract.ConfigPresenter> {
        /**
         * @param msg 显示状态
         */
        void show(String msg);
        /**
         * @param configList 设置项
         */
        void initCfgItem(List<Config> configList);
    }
}
