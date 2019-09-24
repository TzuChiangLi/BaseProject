package com.ftrend.zgp.view;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.ShopAdapter;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.presenter.OrderOutPresenter;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liziqiang@ftrend.cn
 */
public class OrderOutActivity extends BaseActivity implements Contract.OrderOutView, OnTitleBarListener {
    @BindView(R.id.out_order_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.out_order_rv)
    RecyclerView mRecyclerView;
    private Contract.OrderOutPresenter mPresenter;
    private ShopAdapter<Trade> mAdapter;

    @Override
    public void onNetWorkChange(boolean isOnline) {

    }

    @Override
    protected int getLayoutID() {
        return R.layout.order_out_activity;
    }

    @Override
    protected void initData() {
        mPresenter.initView();
    }

    @Override
    protected void initView() {
        if (mPresenter==null){
            mPresenter = OrderOutPresenter.createPresenter(this);
        }
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
    public void close() {
        finish();
    }

    @Override
    public void setPresenter(Contract.OrderOutPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    @Override
    public void initOutOrder(List<Trade> tradeList) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ShopAdapter<>(R.layout.order_out_rv_item, tradeList, 4);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
    }
}
