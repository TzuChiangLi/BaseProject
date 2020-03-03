package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.Helper;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface HelperContract {
    interface HelperPresenter {
        /**
         * 初始化数据
         */
        void initHelper();

        /**
         * 销毁
         */
        void onDestroy();
    }

    interface HelperView extends BaseView<HelperContract.HelperPresenter> {
        /**
         * 显示数据
         */
        void showHelper(List<Helper> helper);
    }
}
