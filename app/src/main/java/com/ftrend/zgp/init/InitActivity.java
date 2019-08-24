package com.ftrend.zgp.init;

import android.support.v4.view.ViewPager;

import com.ftrend.zgp.R;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.utils.log.LogUtil;

import butterknife.BindView;

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
