package com.ftrend.zgp;

import android.support.v4.view.ViewPager;

import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.utils.log.LogUtil;

import butterknife.BindView;

/**
 * @author LZQ
 * @content 首次初始化，想法是ViewPager+fragment来控制初始化流程
 */
public class InitActivity extends BaseActivity {
    @BindView(R.id.init_view_pager)
    ViewPager mViewPager;


    @Override
    protected int getLayoutID(int i) {
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
