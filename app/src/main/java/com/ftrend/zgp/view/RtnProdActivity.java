package com.ftrend.zgp.view;

import android.view.View;

import com.ftrend.zgp.R;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.utils.ZgParams;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import butterknife.BindView;

/**
 * 退货
 *
 * @author liziqiang@ftrend.cn
 */
public class RtnProdActivity extends BaseActivity implements OnTitleBarListener {
    @BindView(R.id.rtn_prod_top_bar)
    TitleBar mTitleBar;

    @Override
    public void onNetWorkChange(boolean isOnline) {

    }

    @Override
    protected int getLayoutID() {
        return R.layout.rtn_prod_activity;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        mTitleBar.setRightIcon(ZgParams.isIsOnline() ? R.drawable.online : R.drawable.offline);
        mTitleBar.setOnTitleBarListener(this);
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
}
