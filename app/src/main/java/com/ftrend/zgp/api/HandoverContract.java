package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.HandoverRecord;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface HandoverContract {
    interface HandoverPresenter {
        /**
         * 初始化界面
         */
        void initView();

        /**
         * 交班
         */
        void doHandover();

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface HandoverView extends BaseView<HandoverContract.HandoverPresenter> {
        /**
         * 采用列表的方式显示交班信息
         *
         * @param recordList 交班信息
         */
        void showHandoverRecord(List<HandoverRecord> recordList);


        /**
         * 交班成功并返回
         */
        void success();

        /**
         * 单机模式不允许交班
         */
        void showOfflineTip();

    }
}
