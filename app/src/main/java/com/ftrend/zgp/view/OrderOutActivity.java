package com.ftrend.zgp.view;

import android.view.View;

import com.ftrend.zgp.R;
import com.ftrend.zgp.base.BaseActivity;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liziqiang@ftrend.cn
 */
public class OrderOutActivity extends BaseActivity implements OnTitleBarListener {
    @BindView(R.id.out_order_top_bar)
    TitleBar mTitleBar;

    @Override
    public void onNetWorkChange(boolean isOnline) {

    }

    @Override
    protected int getLayoutID() {
        return R.layout.order_out_activity;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mTitleBar.setOnTitleBarListener(this);
    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
    }

    @Override
    public void onLeftClick(View v) {
        finish();
    }

    @Override
    public void onTitleClick(View v) {

    }

    @Override
    public void onRightClick(View v) {

    }

    @OnClick(R.id.out_order_btn_close)
    public void close(){
        finish();
    }
}
