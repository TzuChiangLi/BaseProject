package com.ftrend.zgp.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.ShopAdapter;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.presenter.RtnProdPresenter;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 退货
 *
 * @author liziqiang@ftrend.cn
 */
public class RtnProdActivity extends BaseActivity implements OnTitleBarListener, Contract.RtnProdView {
    @BindView(R.id.rtn_prod_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.rtn_prod_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.rtn_prod_edt_trade)
    ClearEditText mEdt;
    @BindView(R.id.rtn_prod_btn_search)
    Button mSearchBtn;
    private int oldPosition = -1;
    private Contract.RtnProdPresenter mPresenter;
    private ShopAdapter<TradeProd> mProdAdapter;

    @Override
    public void onNetWorkChange(boolean isOnline) {
        if (mTitleBar == null) {
            mTitleBar = findViewById(R.id.rtn_prod_top_bar);
        }
        mTitleBar.setRightIcon(isOnline ? R.drawable.online : R.drawable.offline);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.rtn_prod_activity;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = RtnProdPresenter.createPresenter(this);
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

    @Override
    public void setPresenter(Contract.RtnProdPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    @OnClick(R.id.rtn_prod_btn_search)
    public void search() {
        mPresenter.getTradeByLsNo(mEdt.getText().toString());
    }

    @Override

    public void existTrade(final List<TradeProd> data) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //防止点击item的子view出现画面闪烁的问题
        ((SimpleItemAnimator) (Objects.requireNonNull(mRecyclerView.getItemAnimator()))).setSupportsChangeAnimations(false);
        mProdAdapter = new ShopAdapter<>(R.layout.shop_list_rv_product_item, data, 2);
        mRecyclerView.setAdapter(mProdAdapter);
        mProdAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (ClickUtil.onceClick()) {
                    return;
                }
                switch (view.getId()) {
                    case R.id.shop_list_rv_img_add:
                        //商品数量+1
                        mPresenter.changeAmount(position, 1);
                        break;
                    case R.id.shop_list_rv_img_minus:
                        //改变数量-1
                        mPresenter.changeAmount(position, -1);
                        break;
                    case R.id.shop_list_rv_btn_change_price:
                        //先检查商品是否允许改价
                        mPresenter.getProdPriceFlag(data.get(position).getProdCode(), data.get(position).getBarCode(), position);
                        break;
                    case R.id.shop_list_rv_btn_discount:
                        //单品优惠
                        mPresenter.checkProdForDsc(position);
                        break;
                    case R.id.shop_list_rv_btn_del:
                        //检查行清权限
                        mPresenter.checkDelProdRight(position);
                        break;
                    default:
                        break;
                }
            }
        });
        mProdAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (oldPosition != -1 && oldPosition < adapter.getItemCount()) {
                    mProdAdapter.getData().get(oldPosition).setSelect(false);
                    mProdAdapter.notifyItemChanged(oldPosition);
                }
                oldPosition = position;
                mProdAdapter.getData().get(position).setSelect(true);
                mProdAdapter.notifyItemChanged(position);
            }
        });
    }

    @Override
    public void showError(String msg) {
        MessageUtil.showError(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
    }
}
