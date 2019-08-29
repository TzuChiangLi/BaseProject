package com.ftrend.zgp.view;

import android.view.View;

import com.ftrend.zgp.R;
import com.ftrend.zgp.base.BaseActivity;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import butterknife.BindView;

/**
 * 结算选择付款方式
 *
 * @author liziqiang@ftrend.cn
 */
public class PayActivity extends BaseActivity implements OnTitleBarListener {
    @BindView(R.id.pay_top_bar)
    TitleBar mTitleBar;

    @Override
    protected int getLayoutID() {
        return R.layout.pay_activity;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initTitleBar() {
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
