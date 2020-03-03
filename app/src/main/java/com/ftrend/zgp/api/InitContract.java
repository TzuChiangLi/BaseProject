package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;

/**
 * @author liziqiang@ftrend.cn
 */

public interface InitContract {
    interface InitPresenter {
        /**
         * 开始动画
         */
        void startAnimator();

        /**
         * 开始同步数据
         *
         * @param step 步骤：1 - 下载基础数据；2 - 下载实时流水
         */
        void startInitData(int step);

        /**
         * 完成同步
         */
        void finishInitData();

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface InitView extends BaseView<InitContract.InitPresenter> {

        /**
         * 开始同步数据
         */
        void startUpdate();

        /**
         * 同步数据进度
         *
         * @param step     步骤：1 - 下载基础数据；2 - 下载实时流水
         * @param progress 进度
         */
        void updateProgress(int step, int progress);

        /**
         * 停止动画
         */
        void stopUpdate();

        /**
         * 完成同步
         *
         * @param posCode 机器编号
         * @param dep     可登录专柜
         * @param user    可登录用户
         */
        void finishUpdate(String posCode, String dep, String user);
    }

}
