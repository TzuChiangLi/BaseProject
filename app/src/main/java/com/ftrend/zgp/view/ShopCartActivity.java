package com.ftrend.zgp.view;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
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
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 收银-商品选择
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopCartActivity extends BaseActivity implements Contract.ShopCartView, OnTitleBarListener {
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
    @BindView(R.id.shop_cart_top_bar)
    TitleBar mTitleBar;
    private Contract.ShopCartPresenter mPresenter;
    private ShopAdapter<DepProduct> mProdAdapter;
    private ShopAdapter<DepCls> mClsAdapter;
    private int oldPosition = -1;
    private List<DepProduct> mProdList = new ArrayList<>();

    @Override
    protected int getLayoutID() {
        return R.layout.shop_cart_activity;
    }

    @Override
    protected void initData() {
        mPresenter.initProdList(this);
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
                mPresenter.searchProdList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void initTitleBar() {
        mTitleBar.setOnTitleBarListener(this);
    }

    @Override
    public void setPresenter(Contract.ShopCartPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }


    @Override
    public void setClsList(final List<DepCls> clsList) {
        mClassRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mClsAdapter = new ShopAdapter<>(R.layout.shop_cart_rv_classes_item, clsList, 0);
        mClassRecyclerView.setAdapter(mClsAdapter);
        mClassRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mClsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mPresenter.searchProdList(clsList.get(position).getClsCode());
            }
        });
    }

    @Override
    public void setProdList(List<DepProduct> prodList) {
        mProdRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProdAdapter = new ShopAdapter<>(R.layout.shop_cart_rv_product_item, prodList, 1);
        mProdRecyclerView.setAdapter(mProdAdapter);
        mProdRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mProdAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.shop_rv_product_btn_add) {
                    //添加到购物车中
                    mPresenter.addToShopCart((DepProduct) adapter.getItem(position));
                    MessageUtil.show(String.valueOf(position));
                }
            }
        });
    }

    @Override
    public void updateProdList(List<DepProduct> prodList) {
        //过滤筛选
        if (prodList.size() != 0) {
            mProdAdapter.setNewData(prodList);
            mProdAdapter.notifyDataSetChanged();
        } else {
            //如果没有数据，就显示为空
            mProdAdapter.setEmptyView(LinearLayout.inflate(this, R.layout.rv_item_empty, null));
        }

    }

    @OnClick(R.id.shop_cart_bottom_btn_car)
    public void goShopListActivity() {
        UserLog userLog = new UserLog();
        userLog.setModule("ShopCart");
        userLog.setUserCode("userCode");
        userLog.setContent("查看购物车");
        userLog.setFunction("进入购物车");
        userLog.setDepCode("DepCode");
        DatabaseManger.getInstance(this).logUserHandle(userLog);
        Intent intent = new Intent(ShopCartActivity.this, ShopListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.shop_cart_bottom_tv_payment)
    public void goPayActivity() {
        UserLog userLog = new UserLog();
        userLog.setModule("ShopCart");
        userLog.setUserCode("userCode");
        userLog.setContent("结算");
        userLog.setFunction("结算");
        userLog.setDepCode("DepCode");
        DatabaseManger.getInstance(this).logUserHandle(userLog);
        Intent intent = new Intent(ShopCartActivity.this, PayActivity.class);
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
}
