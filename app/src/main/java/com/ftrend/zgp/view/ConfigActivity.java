package com.ftrend.zgp.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.ConfigAdapter;
import com.ftrend.zgp.api.ConfigContract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.Config;
import com.ftrend.zgp.presenter.ConfigPresenter;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.suke.widget.SwitchButton;

import java.util.List;

import butterknife.BindView;

/**
 * @author liziqiang@ftrend.cn
 */
public class ConfigActivity extends BaseActivity implements ConfigContract.ConfigView, OnTitleBarListener {
    @BindView(R.id.cfg_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.cfg_rv)
    RecyclerView mRecyclerView;
    private ConfigContract.ConfigPresenter mPresenter;
    private ConfigAdapter mAdapter;

    @Override
    public void onNetWorkChange(boolean isOnline) {
        if (mTitleBar == null) {
            mTitleBar = findViewById(R.id.rtn_top_bar);
        }
        mTitleBar.setRightIcon(isOnline ? R.drawable.online : R.drawable.offline);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.config_activity;
    }

    @Override
    protected void initData() {
        mAdapter = new ConfigAdapter(R.layout.config_rv_item, null);
        mRecyclerView.setAdapter(mAdapter);
        mPresenter.loadCfgItem();
    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = ConfigPresenter.createPresenter(this);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        mTitleBar.setRightIcon(ZgParams.isIsOnline() ? R.drawable.online : R.drawable.offline);
        mTitleBar.setOnTitleBarListener(this);
    }

    @Override
    public void show(String msg) {
        MessageUtil.show(msg);
    }

    @Override
    public void initCfgItem(List<Config> configList) {
        if (configList.isEmpty()) {
            mAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.rv_item_empty, (ViewGroup) mRecyclerView.getParent(), false));
        } else {
            mAdapter.setNewData(configList);
            addItemChildListener();
        }
    }


    private void addItemChildListener() {
        mAdapter.setListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                switch ((String) view.getTag()) {
                    case "结算成功自动打印交易小票":
                        mPresenter.print(isChecked);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void setPresenter(ConfigContract.ConfigPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
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
