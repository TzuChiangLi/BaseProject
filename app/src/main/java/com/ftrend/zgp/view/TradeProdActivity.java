package com.ftrend.zgp.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liziqiang@ftrend.cn
 */
public class TradeProdActivity extends BaseActivity implements TrdProdContract.TrdProdView, OnTitleBarListener {
    @BindView(R.id.trd_qry_prod_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.trd_qry_prod_top_bar_title)
    TextView mTitleTv;
    @BindView(R.id.trd_qry_prod_img_pay_type)
    ImageView mPayTypeImg;
    @BindView(R.id.trd_qry_prod_tv_pay_type)
    TextView mPayTypeTv;
    @BindView(R.id.trd_qry_prod_tv_old_lsno)
    TextView mOldLsNoTv;
    @BindView(R.id.trd_qry_prod_title_old_lsno)
    TextView mOldLsNoTitle;
    @BindView(R.id.trd_qry_prod_tv_trade_time)
    TextView mTradeTimeTv;
    @BindView(R.id.trd_qry_prod_tv_trade_lsno)
    TextView mLsNoTv;
    @BindView(R.id.trd_qry_prod_tv_cahier)
    TextView mCashierTv;
    @BindView(R.id.trd_qry_prod_tv_trade_total)
    TextView mTotalTv;
    @BindView(R.id.trd_qry_prod_tv_vip_name)
    TextView mVipNameTv;
    @BindView(R.id.trd_qry_prod_tv_vip_grade)
    TextView mCardGradeTv;
    @BindView(R.id.trd_qry_prod_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.trd_qry_prod_rl_bottom)
    RelativeLayout mBottomLayout;
    @BindView(R.id.trd_qry_prod_ll_more)
    LinearLayout mMoreLayout;
    @BindView(R.id.trd_qry_prod_rl_vip)
    RelativeLayout mVipLayout;
    @BindString(R.string.trade_prod_sale)
    String saleTitle;
    @BindString(R.string.trade_prod_rtn)
    String rtnTitle;
    private TrdProdContract.TrdProdPresenter mPresenter;
    private ShopAdapter<TradeProd> mAdapter;

    @Override
    public void onNetWorkChange(boolean isOnline) {
        if (mTitleBar == null) {
            mTitleBar = findViewById(R.id.trd_qry_prod_top_bar);
        }
        mTitleBar.setRightIcon(isOnline ? R.drawable.online : R.drawable.offline);
    }

    @Override
    public void setCurrentModule() {
        LogUtil.setCurrentModule("流水查询--详情");
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
    public void showMoreInfo(String oldLsNo) {
        mMoreLayout.setVisibility(View.VISIBLE);
        mOldLsNoTv.setVisibility(View.VISIBLE);
        mOldLsNoTitle.setVisibility(View.VISIBLE);
        mOldLsNoTv.setText(oldLsNo);
    }

    @Override
    public void showTradeFlag(boolean isSale) {
        mTitleTv.setText(isSale ? saleTitle : rtnTitle);
    }

    @Override
    public void showTradeProd(List<TradeProd> prodList) {
        mAdapter.setNewData(prodList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showVipInfo(VipInfo vip) {
        mVipLayout.setVisibility(View.VISIBLE);
        mVipNameTv.setText(vip.getVipName());
        mCardGradeTv.setText(String.format("%s/%s", vip.getCardCode(), vip.getVipGrade()));
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
    public void setTradeFlag(boolean isSale) {
        mAdapter = new ShopAdapter<>(R.layout.shop_list_rv_product_item, null, isSale ? 9 : 6);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void printResult() {
        if (MessageUtil.isWaiting()) {
            MessageUtil.waitEnd();
        }
    }

    @OnClick(R.id.trd_btn_print_again)
    public void print() {
        //打印小票
        MessageUtil.waitCircleProgress("请稍后");
        mPresenter.print();
    }

    @OnClick(R.id.trd_btn_back)
    public void back() {
        finish();
    }

    @Override
    public void setPresenter(TrdProdContract.TrdProdPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }
}
