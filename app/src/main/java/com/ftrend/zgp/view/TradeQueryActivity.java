package com.ftrend.zgp.view;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.TrdQryAdapter;
import com.ftrend.zgp.api.TrdQryContract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.presenter.TrdQryPresenter;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import java.util.List;

import butterknife.BindView;

public class TradeQueryActivity extends BaseActivity implements TrdQryContract.TrdQryView, OnTitleBarListener {
    @BindView(R.id.trade_qry_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.trade_qry_rv_list)
    RecyclerView mRecyclerView;
    private TrdQryContract.TrdQryPresenter mPresenter;
    private TrdQryAdapter mAdapter;

    @Override
    public void onNetWorkChange(boolean isOnline) {
        if (mTitleBar == null) {
            mTitleBar = findViewById(R.id.rtn_prod_top_bar);
        }
        mTitleBar.setRightIcon(isOnline ? R.drawable.online : R.drawable.offline);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.trade_query_activity;
    }

    @Override
    protected void initData() {
        mPresenter.initTradeList();
    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = TrdQryPresenter.createPresenter(this);
        }
    }

    @Override
    protected void initTitleBar() {
        mTitleBar.setOnTitleBarListener(this);
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        mTitleBar.setRightIcon(ZgParams.isIsOnline() ? R.drawable.online : R.drawable.offline);
    }

    @Override
    public void onLeftClick(View v) {
        if (ClickUtil.onceClick()) {
            return;
        }
        finish();
    }

    @Override
    public void onTitleClick(View v) {

    }

    @Override
    public void onRightClick(View v) {

    }

    @Override
    public void showTradeList(List<Trade> trdList) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TrdQryAdapter(R.layout.trade_qry_rv_item, trdList);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mPresenter.queryTradeProd(position);
            }
        });
    }

    @Override
    public void goTradeProdActivity(String lsNo) {
        Intent intent = new Intent(TradeQueryActivity.this,TradeProdActivity.class);
        startActivity(intent);
    }

    @Override
    public void setPresenter(TrdQryContract.TrdQryPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }
}
