package com.ftrend.zgp.view;

import android.content.Intent;
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
import com.ftrend.zgp.utils.msg.MessageUtil;
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
    TextView mPriceTotalTv;
    @BindView(R.id.pay_rv_pay_way)
    RecyclerView mRecyclerView;
    private Contract.PayPresenter mPresenter;
    private String total = "", lsNo = "";
    private ShopAdapter<Menu.MenuList> mPayWayAdapter;

    @Override
    protected int getLayoutID() {
        return R.layout.pay_activity;
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        lsNo = intent.getStringExtra("lsNo");
        total = intent.getStringExtra("total");
        mPriceTotalTv.setText(total);
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

    @Override
    public void setPresenter(Contract.PayPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    @Override
    public void showPayway(List<Menu.MenuList> payWay) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPayWayAdapter = new ShopAdapter<>(R.layout.pay_way_rv_item, payWay, 3);
        mRecyclerView.setAdapter(mPayWayAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mPayWayAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
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
                        MessageUtil.warning("确认已使用现金完成交易？");
                        MessageUtil.setMessageUtilClickListener(new MessageUtil.OnBtnClickListener() {
                            @Override
                            public void onLeftBtnClick(BasePopupView view) {
                                MessageUtil.showSuccess("交易已完成");
                                mPresenter.paySuccess(lsNo, Float.parseFloat(total), 3);
                                view.dismiss();
                            }

                            @Override
                            public void onRightBtnClick(BasePopupView view) {

                            }
                        });
                        break;
                }
            }
        });
    }
}
