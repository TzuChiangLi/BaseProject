package com.ftrend.zgp.view;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.ftrend.zgp.R;
import com.ftrend.zgp.base.BaseActivity;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 购物车
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopListActivity extends BaseActivity implements OnTitleBarListener {
    @BindView(R.id.shop_list_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.shop_list_btn_pay)
    Button mPayBtn;

    @Override
    protected int getLayoutID() {
        return R.layout.shop_list_activity;
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


    @OnClick(R.id.shop_list_btn_pay)
    public void doPay() {
        Intent intent = new Intent(ShopListActivity.this, PayActivity.class);
        startActivity(intent);
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
