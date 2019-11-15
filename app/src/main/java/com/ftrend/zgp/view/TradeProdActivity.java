package com.ftrend.zgp.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.ShopAdapter;
import com.ftrend.zgp.api.TrdProdContract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.VipInfo;
import com.ftrend.zgp.presenter.TrdProdPresenter;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import java.util.List;

import butterknife.BindView;

/**
 * @author liziqiang@ftrend.cn
 */
public class TradeProdActivity extends BaseActivity implements TrdProdContract.TrdProdView, OnTitleBarListener {
    @BindView(R.id.trd_qry_prod_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.trd_qry_prod_img_pay_type)
    ImageView mPayTypeImg;
    @BindView(R.id.trd_qry_prod_tv_pay_type)
    TextView mPayTypeTv;
    @BindView(R.id.trd_qry_prod_tv_trade_time)
    TextView mTradeTimeTv;
    @BindView(R.id.trd_qry_prod_tv_trade_lsno)
    TextView mLsNoTv;
    @BindView(R.id.trd_qry_prod_tv_cahier)
    TextView mCashierTv;
    @BindView(R.id.trd_qry_prod_tv_trade_total)
    TextView mTotalTv;
    @BindView(R.id.trd_qry_prod_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.trd_qry_prod_rl_bottom)
    RelativeLayout mBottomLayout;
    private TrdProdContract.TrdProdPresenter mPresenter;
    private ShopAdapter<TradeProd> mAdapter;

    @Override
    public void onNetWorkChange(boolean isOnline) {

    }

    @Override
    protected int getLayoutID() {
        return R.layout.trade_query_prod_activity;
    }

    @Override
    protected void initData() {
        mPresenter.initTradeInfo();
    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = TrdProdPresenter.createPresenter(this);
        }
    }

    @Override
    protected void initTitleBar() {
        mTitleBar.setOnTitleBarListener(this);
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        mTitleBar.setRightIcon(ZgParams.isIsOnline() ? R.drawable.online : R.drawable.offline);
    }

    @Override
    public void onLeftClick(View v) {
        if (ClickUtil.onceClick()) {
            return;
        }
        finish();
    }

    @Override
    public void onTitleClick(View v) {

    }

    @Override
    public void onRightClick(View v) {

    }

    @Override
    public void showTradeProd(List<TradeProd> prodList) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ShopAdapter<>(R.layout.shop_list_rv_product_item, prodList, 6);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showVipInfo(VipInfo vip) {

    }

    @Override
    public void showTradeInfo(String... info) {
        mTradeTimeTv.setText(info[0]);
        mLsNoTv.setText(info[1]);
        mCashierTv.setText(info[2]);
        mTotalTv.setText(info[3]);
    }


    @Override
    public void showPayInfo(String payTypeName, int img) {
        if (mBottomLayout.getVisibility() == View.GONE) {
            mBottomLayout.setVisibility(View.VISIBLE);
        }
        mPayTypeTv.setText(payTypeName);
        mPayTypeImg.setImageResource(img);
    }


    @Override
    public void setPresenter(TrdProdContract.TrdProdPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }
}
