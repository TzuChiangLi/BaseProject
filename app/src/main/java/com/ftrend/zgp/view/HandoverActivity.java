package com.ftrend.zgp.view;

import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import com.ftrend.zgp.R;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.presenter.HandoverPresenter;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liziqiang@ftrend.cn
 */
public class HandoverActivity extends BaseActivity implements Contract.HandoverView {
    @BindView(R.id.handover_tv_usercode)
    TextView mUserCodeTv;
    @BindView(R.id.handover_tv_username)
    TextView mUserNameTV;
    @BindView(R.id.handover_trade_total)
    TextView mTradeTotalTv;
    @BindView(R.id.handover_trade_count)
    TextView mTradeCountTv;
    @BindView(R.id.handover_th_count)
    TextView mTHCountTv;
    @BindView(R.id.handover_th_total)
    TextView mTHTotalTv;
    @BindView(R.id.handover_cash_count)
    TextView mCashCountTv;
    @BindView(R.id.handover_cash_total)
    TextView mCashTotalTv;
    @BindView(R.id.handover_money_total)
    TextView mMoneyTotalTv;
    @BindView(R.id.handover_money_count)
    TextView mMoneyCountTv;
    @BindView(R.id.handover_alipay_total)
    TextView mAliPayTotalTv;
    @BindView(R.id.handover_alipay_count)
    TextView mAliPayCountTv;
    @BindView(R.id.handover_wechat_total)
    TextView mWeChatTotalTv;
    @BindView(R.id.handover_wechat_count)
    TextView mWeChatCountTv;
    @BindView(R.id.handover_card_total)
    TextView mCardTotalTv;
    @BindView(R.id.handover_card_count)
    TextView mCardCountTv;
    @BindView(R.id.handover_pay_count)
    TextView mPayWayCountTv;
    @BindView(R.id.handover_pay_total)
    TextView mPayWayTotalTv;
    @BindView(R.id.handover_btn_handover)
    Button mHandoverBtn;


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


    @Override
    public void showUserInfo(String userCode, String userName) {
        mUserCodeTv.setText(userCode);
        mUserNameTV.setText(userName);
    }

    @Override
    public void showCashInfo(double cashTotal, long cashCount) {
        mCashCountTv.setText(String.valueOf(cashCount));
        mCashTotalTv.setText(String.valueOf(cashTotal));
    }

    @Override
    public void showTHInfo(double thTotal, long thCount) {
        mTHCountTv.setText(String.valueOf(thCount));
        mTHTotalTv.setText(String.valueOf(thTotal));
    }

    @Override
    public void showTradeInfo(double tradeTotal, long tradeCount) {
        mTradeTotalTv.setText(String.valueOf(tradeTotal));
        mTradeCountTv.setText(String.valueOf(tradeCount));
    }

    @Override
    public void showMoneyInfo(double moneyTotal, long moneyCount) {
        mMoneyCountTv.setText(String.valueOf(moneyCount));
        mMoneyTotalTv.setText(String.valueOf(moneyTotal));
    }

    @Override
    public void showAliPayInfo(double aliTotal, long aliCount) {
        mAliPayCountTv.setText(String.valueOf(aliCount));
        mAliPayTotalTv.setText(String.valueOf(aliTotal));
    }

    @Override
    public void showWeChatInfo(double wechatTotal, long wechatCount) {
        mWeChatCountTv.setText(String.valueOf(wechatCount));
        mWeChatTotalTv.setText(String.valueOf(wechatTotal));
    }

    @Override
    public void showCardInfo(double cardTotal, long cardCount) {
        mCardCountTv.setText(String.valueOf(cardCount));
        mCardTotalTv.setText(String.valueOf(cardTotal));
    }

    @Override
    public void showPayInfo(double payTotal, long payCount) {
        mPayWayCountTv.setText(String.valueOf(payCount));
        mPayWayTotalTv.setText(String.valueOf(payTotal));
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
    public void setPresenter(Contract.HandoverPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }
}
