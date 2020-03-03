package com.ftrend.zgp.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.HelperAdapter;
import com.ftrend.zgp.api.HelperContract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.Helper;
import com.ftrend.zgp.presenter.HelperPresenter;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.log.LogUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import java.util.List;

import butterknife.BindView;

/**
 * 操作指南
 *
 * @author liziqiang@ftrend.cn
 */
public class HelperActivity extends BaseActivity implements HelperContract.HelperView, OnTitleBarListener {
    @BindView(R.id.help_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.help_rv)
    RecyclerView mRecyclerView;
    private HelperContract.HelperPresenter mPresenter;
    private HelperAdapter mAdapter;

    @Override
    public void onNetWorkChange(boolean isOnline) {
        if (mTitleBar == null) {
            mTitleBar = findViewById(R.id.help_top_bar);
        }
        mTitleBar.setRightIcon(isOnline ? R.drawable.online : R.drawable.offline);
    }


    @Override
    protected int getLayoutID() {
        return R.layout.helper_activity;
    }

    @Override
    protected void initData() {
        mPresenter.initHelper();
    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = HelperPresenter.createPresenter(this);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void initTitleBar() {
        mTitleBar.setOnTitleBarListener(this);
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        mTitleBar.setRightIcon(ZgParams.isIsOnline() ? R.drawable.online : R.drawable.offline);
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

    @Override
    public void showHelper(List<Helper> helper) {
        mAdapter = new HelperAdapter(R.layout.helper_item, helper);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setPresenter(HelperContract.HelperPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }
}
