package com.ftrend.zgp.view;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.ShopAdapter;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.UserLog;
import com.ftrend.zgp.presenter.ShopCartPresenter;
import com.ftrend.zgp.utils.db.DatabaseManger;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 收银-商品选择
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopCartActivity extends BaseActivity implements Contract.ShopCartView {
    @BindView(R.id.shop_cart_top_ll_edt_search)
    ClearEditText mSearchEdt;
    @BindView(R.id.shop_cart_rv_classes)
    RecyclerView mClassRecyclerView;
    @BindView(R.id.shop_cart_rv_product)
    RecyclerView mProdRecyclerView;
    @BindView(R.id.shop_cart_bottom_tip)
    TextView mTipTv;
    @BindView(R.id.shop_cart_bottom_btn_car)
    Button mListBtn;
    @BindView(R.id.shop_cart_bottom_tv_payment)
    Button mPayBtn;
    private Contract.ShopCartPresenter mPresenter;
    private ShopAdapter<DepProduct> mProdAdapter;
    private ShopAdapter<DepCls> mClsAdapter;

    @Override
    protected int getLayoutID() {
        return R.layout.shop_cart_activity;
    }

    @Override
    protected void initData() {
        mPresenter.initProdList();
        mTipTv.setVisibility(View.VISIBLE);
        mTipTv.bringToFront();
    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = ShopCartPresenter.createPresenter(this);
        }
        mSearchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void initTitleBar() {

    }

    @Override
    public void setPresenter(Contract.ShopCartPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }


    @Override
    public void setClsList(List<DepCls> clsList) {
        mClassRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mClsAdapter = new ShopAdapter<>(R.layout.shop_cart_rv_classes_item, clsList, 0);
        mClassRecyclerView.setAdapter(mClsAdapter);
        mClassRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void setProdList(List<DepProduct> prodList) {
        mProdRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProdAdapter = new ShopAdapter<>(R.layout.shop_cart_rv_product_item, prodList, 1);
        mProdRecyclerView.setAdapter(mProdAdapter);
        mProdRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void updateProdList() {
    }

    @OnClick(R.id.shop_cart_bottom_btn_car)
    public void goShopListActivity() {
        DatabaseManger.getInstance(this).logUserHandle(new UserLog("ShopCart", "进入购物车", "查看购物车", "userCode", "depCode"));
        Intent intent = new Intent(ShopCartActivity.this, ShopListActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.shop_cart_bottom_tv_payment)
    public void goPayActivity(){
        DatabaseManger.getInstance(this).logUserHandle(new UserLog("ShopCart", "结算", "结算", "userCode", "depCode"));
        Intent intent = new Intent(ShopCartActivity.this, PayActivity.class);
        startActivity(intent);
    }
}
