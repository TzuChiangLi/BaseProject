package com.ftrend.zgp.view;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.HandoverAdapter;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.HandoverRecord;
import com.ftrend.zgp.presenter.HandoverPresenter;
import com.ftrend.zgp.utils.common.ClickUtil;
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
public class HandoverActivity extends BaseActivity implements Contract.HandoverView, OnTitleBarListener {
    @BindView(R.id.handover_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.handover_btn_handover)
    Button mHandoverBtn;
    @BindView(R.id.handover_top_bar)
    TitleBar mTitleBar;
    private HandoverAdapter mAdapter = null;
    private Contract.HandoverPresenter mPresenter;

    @Override
    protected int getLayoutID() {
        return R.layout.handover_activity;
    }

    @Override
    protected void initData() {
        mPresenter.initView();
    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = HandoverPresenter.createPresenter(this);
        }
        mTitleBar.setOnTitleBarListener(this);
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
        mPresenter.doHandover();
    }

    @OnClick(R.id.handover_btn_cancel)
    public void cancel() {
        if (ClickUtil.onceClick()) {
            return;
        }
        finish();
    }


    @Override
    public void showUserInfo(String userCode, String userName) {
//        mUserCodeTv.setText(userCode);
//        mUserNameTV.setText(userName);
    }

    @Override
    public void showCashInfo(double cashTotal, long cashCount) {
//        mCashCountTv.setText(String.valueOf(cashCount));
//        mCashTotalTv.setText(String.valueOf(cashTotal));
    }

    @Override
    public void showTHInfo(double thTotal, long thCount) {
//        mTHCountTv.setText(String.valueOf(thCount));
//        mTHTotalTv.setText(String.valueOf(thTotal));
    }

    @Override
    public void showTradeInfo(double tradeTotal, long tradeCount) {
//        mTradeTotalTv.setText(String.valueOf(tradeTotal));
//        mTradeCountTv.setText(String.valueOf(tradeCount));
    }

    @Override
    public void showMoneyInfo(double moneyTotal, long moneyCount) {
//        mMoneyCountTv.setText(String.valueOf(moneyCount));
//        mMoneyTotalTv.setText(String.valueOf(moneyTotal));
    }

    @Override
    public void showAliPayInfo(double aliTotal, long aliCount) {
//        mAliPayCountTv.setText(String.valueOf(aliCount));
//        mAliPayTotalTv.setText(String.valueOf(aliTotal));
    }

    @Override
    public void showWeChatInfo(double wechatTotal, long wechatCount) {
//        mWeChatCountTv.setText(String.valueOf(wechatCount));
//        mWeChatTotalTv.setText(String.valueOf(wechatTotal));
    }

    @Override
    public void showCardInfo(double cardTotal, long cardCount) {
//        mCardCountTv.setText(String.valueOf(cardCount));
//        mCardTotalTv.setText(String.valueOf(cardTotal));
    }

    @Override
    public void showPayInfo(double payTotal, long payCount) {
//        mPayWayCountTv.setText(String.valueOf(payCount));
//        mPayWayTotalTv.setText(String.valueOf(payTotal));
    }

    @Override
    public void showHandoverRecord(List<HandoverRecord> recordList) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new HandoverAdapter(R.layout.handover_rv_item, recordList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showSuccess() {
        MessageUtil.showSuccess("交班成功！");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1500);
    }

    @Override
    public void showError() {
        MessageUtil.showError("交班失败！");
    }

    @Override
    public void showOfflineTip() {
        MessageUtil.showError("当前状态为单机模式，无法交班！");
    }

    @Override
    public void setPresenter(Contract.HandoverPresenter presenter) {
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


}
//@BindView(R.id.handover_tv_usercode)
//    TextView mUserCodeTv;
//    @BindView(R.id.handover_tv_username)
//    TextView mUserNameTV;
//    @BindView(R.id.handover_trade_total)
//    TextView mTradeTotalTv;
//    @BindView(R.id.handover_trade_count)
//    TextView mTradeCountTv;
//    @BindView(R.id.handover_th_count)
//    TextView mTHCountTv;
//    @BindView(R.id.handover_th_total)
//    TextView mTHTotalTv;
//    @BindView(R.id.handover_cash_count)
//    TextView mCashCountTv;
//    @BindView(R.id.handover_cash_total)
//    TextView mCashTotalTv;
//    @BindView(R.id.handover_money_total)
//    TextView mMoneyTotalTv;
//    @BindView(R.id.handover_money_count)
//    TextView mMoneyCountTv;
//    @BindView(R.id.handover_alipay_total)
//    TextView mAliPayTotalTv;
//    @BindView(R.id.handover_alipay_count)
//    TextView mAliPayCountTv;
//    @BindView(R.id.handover_wechat_total)
//    TextView mWeChatTotalTv;
//    @BindView(R.id.handover_wechat_count)
//    TextView mWeChatCountTv;
//    @BindView(R.id.handover_card_total)
//    TextView mCardTotalTv;
//    @BindView(R.id.handover_card_count)
//    TextView mCardCountTv;
//    @BindView(R.id.handover_pay_count)
//    TextView mPayWayCountTv;
//    @BindView(R.id.handover_pay_total)
//    TextView mPayWayTotalTv;
