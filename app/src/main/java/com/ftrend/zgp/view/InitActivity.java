package com.ftrend.zgp.view;

import com.ftrend.zgp.R;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.utils.log.LogUtil;

/**
 * 首次初始化
 *
 * @author liziqiang@ftrend.cn
 */
public class InitActivity extends BaseActivity {


    @Override
    protected int getLayoutID() {
        return R.layout.init_activity;
    }

    @Override
    protected void initData() {
        LogUtil.d("initData");
    }

    @Override
    protected void initView() {
        LogUtil.d("initData");
    }

    @Override
    protected void initTitleBar() {
        LogUtil.d("initData");
    }
}
