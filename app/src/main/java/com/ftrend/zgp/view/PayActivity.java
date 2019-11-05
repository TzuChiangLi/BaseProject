package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.ShopAdapter;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.presenter.PayPresenter;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.msg.InputPanel;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.pay.PayType;
import com.ftrend.zgp.utils.pay.SqbPayHelper;
import com.ftrend.zgp.utils.pop.MoneyInputCallback;
import com.ftrend.zgp.utils.pop.StringInputCallback;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;

/**
 * 结算选择付款方式
 *
 * @author liziqiang@ftrend.cn
 */
public class PayActivity extends BaseActivity implements Contract.PayView, OnTitleBarListener {
    @BindView(R.id.pay_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.pay_tv_total)
    TextView mTotalTv;
    @BindView(R.id.pay_rv_pay_way)
    RecyclerView mRecyclerView;
    private Contract.PayPresenter mPresenter;
    private static int START_SCAN = 002;
    private String lsNo;

    @Override
    protected int getLayoutID() {
        return R.layout.pay_activity;
    }

    @Override
    protected void initData() {
        mPresenter.initPayWay();
    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = PayPresenter.createPresenter(this);
        }
        lsNo = TradeHelper.getTrade().getLsNo();
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

    /**
     * 网络变化
     *
     * @param isOnline
     */
    @Override
    public void onNetWorkChange(boolean isOnline) {
        if (mTitleBar == null) {
            mTitleBar = findViewById(R.id.pay_top_bar);
        }
        mTitleBar.setRightIcon(isOnline ? R.drawable.online : R.drawable.offline);
    }

    @Override
    public void setPresenter(Contract.PayPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    @Override
    public void showPayway(final List<Menu.MenuList> payWay) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ShopAdapter<Menu.MenuList> mPayWayAdapter = new ShopAdapter<>(R.layout.pay_way_rv_item, payWay, 3);
        mRecyclerView.setAdapter(mPayWayAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mPayWayAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (ClickUtil.onceClick()) {
                    return;
                }
                switch (position) {
                    case 0:
                        //收钱吧
                        if (!SqbPayHelper.isActivated()) {
                            MessageUtil.showError("当前设备未激活收钱吧服务，无法使用");
                            return;
                        }
                        try {
                            Intent intent = new Intent("com.summi.scan");
                            intent.setPackage("com.sunmi.sunmiqrcodescanner");
                            startActivityForResult(intent, START_SCAN);
                        } catch (Exception e) {
                            String msg = "本设备不支持刷卡，请输入顾客支付码：";
                            InputPanel.showInput(PayActivity.this, msg, new StringInputCallback() {
                                @Override
                                public void onOk(String value) {
                                    mPresenter.payByShouQian(value);
                                }

                                @Override
                                public void onCancel() {
                                    MessageUtil.show("已取消支付");
                                }

                                @Override
                                public String validate(String value) {
                                    return null;
                                }
                            });
                        }
                        break;
                    case 1:
                        //储值卡
                        break;
                    case 2:
                        //现金
                        final double total = TradeHelper.getTradeTotal();
                        InputPanel.showChargeDialog(PayActivity.this, total,
                                new MoneyInputCallback() {
                                    @Override
                                    public void onOk(double value) {
                                        if (mPresenter.paySuccess(PayType.PAYTYPE_CASH, value)) {
                                            MessageUtil.info("交易已完成", new MessageUtil.MessageBoxOkListener() {
                                                @Override
                                                public void onOk() {
                                                    returnHomeActivity();
                                                }
                                            });
                                        } else {
                                            MessageUtil.showError("交易失败，请稍后重试");
                                        }
                                    }

                                    @Override
                                    public void onCancel() {
                                        if (!TradeHelper.checkPayStatus(lsNo)) {
                                            MessageUtil.show("已取消支付");
                                        }
                                    }

                                    @Override
                                    public String validate(double value) {
                                        return (value >= total) ? "" : "支付金额不足！";
                                    }
                                });
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void showTradeInfo(double total) {
        mTotalTv.setText(String.format("%.2f", total));
    }

    @Override
    public void waitPayResult() {
        MessageUtil.waitBegin("正在完成付款，请稍后...", new MessageUtil.MessageBoxCancelListener() {
            @Override
            public boolean onCancel() {
                MessageUtil.waitEnd();
                return true;
            }
        });
    }

    public void returnHomeActivity() {
        Intent intent = new Intent(PayActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void paySuccess() {
        MessageUtil.waitSuccesss("交易已完成", new MessageUtil.MessageBoxOkListener() {
            @Override
            public void onOk() {
                returnHomeActivity();
            }
        });
    }

    @Override
    public void payFail(String msg) {
        MessageUtil.waitError(msg, null);
    }

    @Override
    public void showError(String msg) {
        MessageUtil.showError(msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_SCAN && data != null) {
            Bundle bundle = data.getExtras();
            ArrayList result = (ArrayList) bundle.getSerializable("data");
            Iterator it = result.iterator();
            while (it.hasNext()) {
                HashMap hashMap = (HashMap) it.next();
                //此处传入扫码结果
                mPresenter.payByShouQian(String.valueOf(hashMap.get("VALUE")));
                Log.i("----sunmi", String.valueOf(hashMap.get("TYPE")));//这个是扫码的类型
                Log.i("----sunmi", String.valueOf(hashMap.get("VALUE")));//这个是扫码的结果
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
        EventBus.getDefault().unregister(this);
    }
}
