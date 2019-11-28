package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.ShopAdapter;
import com.ftrend.zgp.api.PayContract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.presenter.PayPresenter;
import com.ftrend.zgp.utils.RtnHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.event.Event;
import com.ftrend.zgp.utils.log.LogUtil;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * 结算选择付款方式
 *
 * @author liziqiang@ftrend.cn
 */
public class PayActivity extends BaseActivity implements PayContract.View, OnTitleBarListener {
    @BindView(R.id.pay_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.pay_tv_total)
    TextView mTotalTv;
    @BindView(R.id.pay_tv_total_title)
    TextView mTypeTv;
    @BindView(R.id.pay_tv_title)
    TextView mTitleTv;
    @BindView(R.id.pay_rv_pay_way)
    RecyclerView mRecyclerView;
    private PayContract.Presenter mPresenter;
    private static int START_SCAN = 002;
    private String lsNo;
    private boolean isSale = true;

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
        EventBus.getDefault().register(this);
        if (mPresenter == null) {
            mPresenter = PayPresenter.createPresenter(this);
        }
        //接收交易类型
        isSale = getIntent().getBooleanExtra("isSale", true);
        //设置交易类型
        mPresenter.setTradeType(isSale);
        //销售、退货分开处理
        lsNo = isSale ? TradeHelper.getTrade().getLsNo() : RtnHelper.getRtnTrade().getLsNo();
        mTypeTv.setText(isSale ? "应收款：" : "退款金额：");
        mTitleTv.setText(isSale ? "收银-结算" : "退货-结算");
    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        mTitleBar.setRightIcon(ZgParams.isIsOnline() ? R.drawable.online : R.drawable.offline);
        mTitleBar.setOnTitleBarListener(this);
    }

    @Override
    public void onLeftClick(android.view.View v) {
        finish();
    }

    @Override
    public void onTitleClick(android.view.View v) {
    }

    @Override
    public void onRightClick(android.view.View v) {
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
    public void setPresenter(PayContract.Presenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    @Override
    public void cardPayWait(final String msg) {
        if (MessageUtil.isWaiting()) {
            MessageUtil.waitUpdate(msg);
        } else {
            MessageUtil.waitBegin(msg, new MessageUtil.MessageBoxCancelListener() {
                @Override
                public boolean onCancel() {
                    return mPresenter.cardPayCancel();
                }
            });
        }
    }

    @Override
    public void cardPaySuccess(String msg) {
        MessageUtil.waitSuccesss(msg, new MessageUtil.MessageBoxOkListener() {
            @Override
            public void onOk() {
                returnHomeActivity();
            }
        });
    }

    @Override
    public void cardPayFail(String msg) {
        MessageUtil.waitError(msg, null);
    }

    @Override
    public void cardPayFail(String code, String msg) {
        MessageUtil.waitError(code, msg, null);
    }

    @Override
    public void cardPayTimeout(String msg) {
        MessageUtil.waitEnd();
        MessageUtil.question(msg, "重试", "取消", new MessageUtil.MessageBoxYesNoListener() {
            @Override
            public void onYes() {
                mPresenter.cardPayRetry();
            }

            @Override
            public void onNo() {

            }
        });
    }

    @Override
    public void cardPayPassword() {
        MessageUtil.waitEnd();
        InputPanel.showInput(this, "请输入支付密码：", new StringInputCallback() {
            @Override
            public void onOk(String value) {
                mPresenter.cardPayPass(value);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public String validate(String value) {
                return null;
            }
        });
    }

    @Override
    public void showPayway(final List<Menu.MenuList> payWay) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ShopAdapter<Menu.MenuList> mPayWayAdapter = new ShopAdapter<>(R.layout.pay_way_rv_item, payWay, 3);
        mRecyclerView.setAdapter(mPayWayAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mPayWayAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, android.view.View view, int position) {
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
                            String msg = "本设备不支持扫码，请输入顾客支付码：";
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
                    case 1://储值卡
                        //显示刷卡等待提示
                        mPresenter.cardPay();
                        break;
                    case 2:
                        //现金
                        final double total = isSale ? TradeHelper.getTradeTotal() : RtnHelper.getRtnTotal();
                        if (isSale) {
                            InputPanel.showChargeDialog(PayActivity.this, total,
                                    new MoneyInputCallback() {
                                        @Override
                                        public void onOk(double value) {
                                            if (mPresenter.paySuccess(PayType.PAYTYPE_CASH, value, "")) {
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
                        } else {
                            String msg;
                            msg = String.format(Locale.CHINA, "现金退款：￥%.2f", total);
                            MessageUtil.question(msg, "确认", "返回",
                                    new MessageUtil.MessageBoxYesNoListener() {
                                        @Override
                                        public void onYes() {
                                            if (mPresenter.paySuccess(PayType.PAYTYPE_CASH, total, "")) {
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
                                        public void onNo() {

                                        }
                                    });
                        }
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


    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onMessage(Event event) {
        if (event.getTarget() != Event.TARGET_PAY) {
            return;
        }
        switch (event.getType()) {
            case PayContract.MSG_CARD_CODE_INPUT:
                cardInput();
                break;
            default:
                break;
        }
    }

    /**
     * 手工输入储值卡卡号
     */
    private void cardInput() {
        InputPanel.showInput(this, "请输入储值卡卡号（仅支持磁卡）：", new StringInputCallback() {
            @Override
            public void onOk(String value) {
                mPresenter.cardPay(value);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public String validate(String value) {
                return TextUtils.isEmpty(value) ? "请输入卡号" : "";
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
        EventBus.getDefault().unregister(this);
    }
}
