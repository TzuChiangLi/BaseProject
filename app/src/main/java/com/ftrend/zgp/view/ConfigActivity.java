package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.ConfigAdapter;
import com.ftrend.zgp.api.ConfigContract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.Config;
import com.ftrend.zgp.presenter.ConfigPresenter;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.InputPanel;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.pop.ServerUrlInputCallback;
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
    private static Handler mHandler = null;


    @Override
    public void onNetWorkChange(boolean isOnline) {
        if (mTitleBar == null) {
            mTitleBar = findViewById(R.id.cfg_top_bar);
        }
        mTitleBar.setRightIcon(isOnline ? R.drawable.online : R.drawable.offline);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.config_activity;
    }

    @Override
    protected void initData() {
        mAdapter = new ConfigAdapter(null);
        mRecyclerView.setAdapter(mAdapter);
        mPresenter.loadCfgItem();
        mHandler = new Handler();
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
    public void show(final String msg) {
        MessageUtil.waitEnd();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MessageUtil.show(msg);
            }
        }, 200);
    }

    @Override
    public void showError(final String msg) {
        MessageUtil.waitEnd();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MessageUtil.error(msg);
            }
        }, 200);
    }

    @Override
    public void initCfgItem(List<Config> configList) {
        if (configList.isEmpty()) {
            mAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.rv_item_empty, (ViewGroup) mRecyclerView.getParent(), false));
        } else {
            mAdapter.setNewData(configList);
            addItemChildListener();
            addItemClickListener();
            addOnCheckedListener();
        }
    }

    @Override
    public void goIntroActivity() {
        Intent intent = new Intent(ConfigActivity.this, IntroActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_fade_out, R.anim.enter_fade_in);
    }


    @Override
    public void updateConfig(int position) {
        mAdapter.notifyItemChanged(position);
    }

    /**
     * 开关监听
     */
    private void addOnCheckedListener() {
        mAdapter.setListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (ClickUtil.onceClick()) {
                    return;
                }
                switch ((String) view.getTag()) {
                    case "结算成功自动打印交易小票":
                        mPresenter.print(isChecked);
                        break;
                    case "允许输入数量":
                        mPresenter.inputNum(isChecked);
                        break;
                    case "数量允许输入小数":
                        mPresenter.inputDecimal(isChecked);
                        break;
                    case "收钱吧":
                        mPresenter.payType(isChecked, 0);
                        break;
                    case "储值卡":
                        mPresenter.payType(isChecked, 1);
                        break;
                    case "现金":
                        mPresenter.payType(isChecked, 2);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 点击监听
     */
    private void addItemClickListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (ClickUtil.onceClick()) {
                    return;
                }
                mPresenter.config(position);
            }
        });
    }

    /**
     * 子控件监听
     */
    private void addItemChildListener() {
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                if (ClickUtil.onceClick()) {
                    return;
                }
                if (view.getId() == R.id.cfg_img_err) {
                    switch (position) {
                        case 6:
                            MessageUtil.show("收钱吧激活失败，暂不可用");
                            break;
                        default:
                            break;
                    }
                }
                if (view.getId() == R.id.cfg_ll) {
                    switch (position) {
                        case 10:
                            InputPanel.showServerDialog(ConfigActivity.this, ZgParams.getServerUrl(),
                                    new ServerUrlInputCallback() {
                                        @Override
                                        public void onOk(String value) {
                                            mPresenter.changeServerUrl(position, value);
                                            MessageUtil.waitCircleProgress("连接中");
                                        }

                                        @Override
                                        public void onCancel() {
                                        }

                                        @Override
                                        public String validate(String value) {
                                            if (value.contains(":") || value.contains("：")) {
                                                value = value.replace("：", ":");
                                                String portStr = value.substring(value.indexOf(":") + 1);
                                                int port = Integer.parseInt(portStr);
                                                if (!(port >= 0 && port <= 65535)) {
                                                    return "请输入正确的端口号";
                                                }
                                            }
                                            if (TextUtils.isEmpty(value)) {
                                                return "请输入服务地址";
                                            }
                                            return null;
                                        }
                                    });
                            break;
                        default:
                            break;
                    }
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
        mHandler = null;
        mPresenter.onDestory();
        super.onDestroy();
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
    public void onBackPressed() {
        if (MessageUtil.isWaiting()) {
            return;
        }
        super.onBackPressed();
    }
}
