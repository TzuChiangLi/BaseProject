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
         * @param position 设置界面列表索引
         */
        void config(int position);

        /**
         * 测试网络
         * @param position 位置索引
         */
        void changeServerUrl(int position,String url);

        /**
         * @param flag 打印小票
         */
        void print(boolean flag);

        /**
         * @param position 选项 0-收钱吧 1-储值卡 2-现金
         */
        void payType( boolean isChecked, int position);

        /**
         * 销毁
         */
        void onDestory();
    }

    interface ConfigView extends BaseView<ConfigContract.ConfigPresenter> {
        /**
         * @param msg 显示状态
         */
        void show(String msg);

        /**
         * @param msg 文本
         */
        void showError(String msg);

        /**
         * @param configList 设置项
         */
        void initCfgItem(List<Config> configList);

        /**
         * 显示关于界面
         */
        void goIntroActivity();

        /**
         * 刷新界面
         */
        void updateConfig(int position);
    }
}
