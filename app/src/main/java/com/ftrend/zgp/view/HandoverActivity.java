package com.ftrend.zgp.view;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.HandoverAdapter;
import com.ftrend.zgp.api.HandoverContract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.HandoverRecord;
import com.ftrend.zgp.presenter.HandoverPresenter;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liziqiang@ftrend.cn
 */
public class HandoverActivity extends BaseActivity implements HandoverContract.HandoverView, OnTitleBarListener {
    @BindView(R.id.handover_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.handover_btn_handover)
    Button mHandoverBtn;
    @BindView(R.id.handover_btn_cancel)
    Button mCancelBtn;
    @BindView(R.id.handover_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.handover_tv_title)
    TextView mTitleLabel;
    private HandoverAdapter mAdapter = null;
    private HandoverContract.HandoverPresenter mPresenter;
    // 是否查看交班报表模式
    private boolean isReport = false;

    @Override
    protected int getLayoutID() {
        return R.layout.handover_activity;
    }

    @Override
    protected void initData() {
        mPresenter.initView(isReport);
    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = HandoverPresenter.createPresenter(this);
        }
        mTitleBar.setOnTitleBarListener(this);
        Intent intent = getIntent();
        isReport = intent.getBooleanExtra("isReport", false);
        if (isReport) {
            // 交班报表查询模式
            mTitleLabel.setText("交班报表");
            mHandoverBtn.setText("打印");
            mCancelBtn.setText("返回");
        }
    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
    }


    @OnClick(R.id.handover_btn_handover)
    public void handover() {
        if (ClickUtil.onceClick()) {
            return;
        }
        if (isReport) {
            if (mAdapter != null && (!mAdapter.getData().isEmpty())) {
                mPresenter.print(mAdapter.getData());
            }
        } else {
            mPresenter.doHandover();
        }
    }

    @OnClick(R.id.handover_btn_cancel)
    public void cancel() {
        if (ClickUtil.onceClick()) {
            return;
        }
        finish();
    }

    @Override
    public void showHandoverRecord(List<HandoverRecord> recordList) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new HandoverAdapter(R.layout.handover_rv_item, recordList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void success() {
        finish();
    }

    @Override
    public void showOfflineTip() {
        MessageUtil.showError("当前状态为单机模式，无法交班！");
    }

    @Override
    public void setPresenter(HandoverContract.HandoverPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
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

    /**
     * 网络变化
     *
     * @param isOnline
     */
    @Override
    public void onNetWorkChange(boolean isOnline) {
        if (mTitleBar == null) {
            mTitleBar = findViewById(R.id.handover_top_bar);
        }
        mTitleBar.setRightIcon(isOnline ? R.drawable.online : R.drawable.offline);
    }

    @Override
    public void setCurrentModule() {
        LogUtil.setCurrentModule("交班");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
    }
}