package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.lxj.xpopup.core.BasePopupView;

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
    private String total = "", lsNo = "";

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
                        //支付宝
                        break;
                    case 1:
                        //微信支付
                        break;
                    case 2:
                        //储值卡
                        break;
                    case 3:
                        //现金
                        MessageUtil.info(String.format("确认使用现金收款%s元？", mTotalTv.getText().toString()));
                        MessageUtil.setMessageUtilClickListener(new MessageUtil.OnBtnClickListener() {
                            @Override
                            public void onLeftBtnClick(BasePopupView popView) {
                                if (mPresenter.paySuccess(TradeHelper.APP_PAY_TYPE_CASH)) {
                                    popView.dismiss();
                                    MessageUtil.showSuccess("交易已完成");

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(PayActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                        }
                                    }, 1500);
                                } else {
                                    MessageUtil.showError("交易失败，请稍后重试");
                                }
                            }

                            @Override
                            public void onRightBtnClick(BasePopupView popView) {
                                popView.dismiss();
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
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
    }
}
