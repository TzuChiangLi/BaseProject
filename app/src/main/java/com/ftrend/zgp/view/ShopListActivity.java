package com.ftrend.zgp.view;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.ShopAdapter;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.presenter.ShopListPresenter;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 购物车
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopListActivity extends BaseActivity implements Contract.ShopListView, OnTitleBarListener {
    @BindView(R.id.shop_list_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.shop_list_btn_pay)
    Button mPayBtn;
    @BindView(R.id.shop_list_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.shop_list_tv_total)
    TextView mPriceTotalTv;
    @BindView(R.id.shop_list_btn_cancel)
    Button mCancelBtn;
    @BindColor(R.color.common_rv_item)
    int rv_item_selected;
    @BindColor(R.color.common_white)
    int rv_item_normal;
    private ShopAdapter<TradeProd> mProdAdapter;
    private Contract.ShopListPresenter mPresenter;
    private String lsNo = "", total = "";
    private int oldPosition = -1;

    @Override
    protected int getLayoutID() {
        return R.layout.shop_list_activity;
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        total = intent.getStringExtra("total");
        mPriceTotalTv.setText(total);
        mPresenter.initShopList(TradeHelper.getTrade().getLsNo());
    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = ShopListPresenter.createPresenter(this);
        }

    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        mTitleBar.setOnTitleBarListener(this);
    }


    @OnClick(R.id.shop_list_btn_pay)
    public void doPay() {
        Intent intent = new Intent(ShopListActivity.this, PayActivity.class);
        intent.putExtra("lsNo", lsNo);
        intent.putExtra("total", total);
        startActivity(intent);
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
    public void setPresenter(Contract.ShopListPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    @Override
    public void showTradeProd(List<TradeProd> prodList) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProdAdapter = new ShopAdapter<>(R.layout.shop_list_rv_product_item, prodList, 2);
        mRecyclerView.setAdapter(mProdAdapter);
        mProdAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (oldPosition != -1) {
                    adapter.getViewByPosition(mRecyclerView, oldPosition, R.id.shop_list_rv_product_rl).setBackgroundColor(rv_item_normal);
                }
                oldPosition = position;
                view.setBackgroundColor(rv_item_selected);
                MessageUtil.show(String.valueOf(position));
            }
        });
    }

    @Override
    public void returnHomeActivity() {
        //HomeActivity的启动模式设置为栈内复用
        //如果Activity栈内有HomeActivity存在，把他之上的所有栈全部移除，并将他置顶
        Intent intent = new Intent(ShopListActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.shop_list_btn_cancel)
    public void cancelTrade() {
        mPresenter.setTradeStatus(lsNo, 3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
    }
}
