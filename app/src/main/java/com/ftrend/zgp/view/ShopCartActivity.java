package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.ShopAdapter;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.presenter.ShopCartPresenter;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.event.Event;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindColor;
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
    @BindView(R.id.shop_cart_bottom_tv_toal_price)
    TextView mTotalTv;
    @BindView(R.id.shop_cart_top_ll_btn_scan)
    ImageButton mScanBtn;
    @BindView(R.id.shop_cart_bottom_tv_hang_up)
    Button mHangUpBtn;
    @BindColor(R.color.common_rv_item)
    int rv_item_selected;
    @BindColor(R.color.common_white)
    int rv_item_normal;
    private Contract.ShopCartPresenter mPresenter;
    private ShopAdapter<DepProduct> mProdAdapter;
    private ShopAdapter<DepCls> mClsAdapter;
    private int oldProdIndex = -1, oldClsIndex = -1;
    private static int START_SCAN = 001;

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
        EventBus.getDefault().register(this);
        mPresenter.initOrderInfo();
        mSearchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.searchProdList(mClsAdapter.getData().get(oldClsIndex == -1 ? 0 : oldClsIndex).getClsCode(), s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        mTitleBar.setRightIcon(ZgParams.isIsOnline() ? R.drawable.online : R.drawable.offline);
        mTitleBar.setOnTitleBarListener(this);
    }

    @Override
    public void setClsList(final List<DepCls> clsList) {
        mClassRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mClsAdapter = new ShopAdapter<>(R.layout.shop_cart_rv_classes_item, clsList, 0);
        mClassRecyclerView.setAdapter(mClsAdapter);
        mClsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (oldClsIndex == -1 && oldClsIndex <= clsList.size()) {
                    mClsAdapter.getData().get(0).setSelect(false);
                    mClsAdapter.notifyItemChanged(0);
                }
                if (oldClsIndex != -1 && oldClsIndex <= clsList.size()) {
                    mClsAdapter.getData().get(oldClsIndex).setSelect(false);
                    mClsAdapter.notifyItemChanged(oldClsIndex);
                }
                oldClsIndex = position;
                mClsAdapter.getData().get(position).setSelect(true);
                mClsAdapter.notifyItemChanged(position);
                LogUtil.d("----edt:"+mSearchEdt.getText().toString());
                mPresenter.searchProdList(clsList.get(position).getClsCode(),mSearchEdt.getText().toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_SCAN && data != null) {
            Bundle bundle = data.getExtras();
            ArrayList result = (ArrayList) bundle.getSerializable("data");
            Iterator it = result.iterator();

            while (it.hasNext()) {
                HashMap hashMap = (HashMap) it.next();
                //此处传入扫码结果
                scanResult(String.valueOf(hashMap.get("VALUE")));
                Log.i("----sunmi", String.valueOf(hashMap.get("TYPE")));//这个是扫码的类型
                Log.i("----sunmi", String.valueOf(hashMap.get("VALUE")));//这个是扫码的结果
            }
        }
    }

    private void scanResult(String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        mPresenter.searchProdByScan(value, mProdAdapter.getData());
    }

    @Override
    public void setProdList(final List<DepProduct> prodList) {
        mProdRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProdAdapter = new ShopAdapter<>(R.layout.shop_cart_rv_product_item, prodList, 1);
        mProdRecyclerView.setAdapter(mProdAdapter);
        mProdRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mProdAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (oldProdIndex != -1 && oldProdIndex < adapter.getItemCount()) {
                    mProdAdapter.getData().get(oldProdIndex).setSelect(false);
                    mProdAdapter.notifyItemChanged(oldProdIndex);
                }
                oldProdIndex = position;
                mProdAdapter.getData().get(position).setSelect(true);
                mProdAdapter.notifyItemChanged(position);
                //添加到购物车中
                mPresenter.addToShopCart((DepProduct) adapter.getItem(position));
                //如果商品价格是0，那么就弹出窗口
                // TODO: 2019/9/29 价格为0也有可能是赠品，此处判断逻辑待优化
                if (prodList.get(position).getPrice() == 0) {
                    MessageUtil.showPriceChange(ShopCartActivity.this, position);
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
            mProdAdapter.setNewData(null);
            mProdAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.rv_item_empty, (ViewGroup) mProdRecyclerView.getParent(), false));
        }
    }

    @Override
    public void updateTradeProd(double count, double price) {
        mTipTv.setText(String.valueOf(count).replace(".0", ""));
        mTotalTv.setText(String.format("%.2f", price));
    }

    @Override
    public void returnHomeActivity(String status) {
        //HomeActivity的启动模式设置为栈内复用
        //如果Activity栈内有HomeActivity存在，把他之上的所有栈全部移除，并将他置顶
        MessageUtil.showSuccess(status);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ShopCartActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }, 1500);
    }

    @Override
    public void setScanProdPosition(int index) {
        mProdRecyclerView.smoothScrollToPosition(index);
        if (oldProdIndex != -1 && oldProdIndex < mProdAdapter.getItemCount()) {
            mProdAdapter.getData().get(oldProdIndex).setSelect(false);
            mProdAdapter.notifyItemChanged(oldProdIndex);
        }
        oldProdIndex = index;
        mProdAdapter.getData().get(index).setSelect(true);
        mProdAdapter.notifyItemChanged(index);
    }

    @Override
    public void noScanProdPosition() {
        MessageUtil.showError("商品库中无此商品");
    }

    @Override
    public void cancelAddProduct(int index) {
        mProdAdapter.notifyItemChanged(index);
    }

    @Override
    public void updateOrderInfo() {
        mProdAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.shop_cart_bottom_btn_car)
    public void goShopListActivity() {
        if (!"0".equals(mTipTv.getText().toString())) {
            Intent intent = new Intent(ShopCartActivity.this, ShopListActivity.class);
            startActivity(intent);
        } else {
            MessageUtil.showWarning("购物车为空");
        }
    }

    @OnClick(R.id.shop_cart_bottom_tv_payment)
    public void goPayActivity() {
        if (!"0".equals(mTipTv.getText().toString())) {
            Intent intent = new Intent(ShopCartActivity.this, PayActivity.class);
            startActivity(intent);
        } else {
            MessageUtil.showWarning("购物车为空");
        }
    }

    @OnClick(R.id.shop_cart_bottom_tv_hang_up)
    public void hangUp() {
        mPresenter.setTradeStatus(TradeHelper.TRADE_STATUS_HANGUP);
    }

    @OnClick(R.id.shop_cart_top_ll_btn_scan)
    public void goScanActivity() {
        try {
            Intent intent = new Intent("com.summi.scan");
            intent.setPackage("com.sunmi.sunmiqrcodescanner");
            startActivityForResult(intent, START_SCAN);
        } catch (Exception e) {
            MessageUtil.showError("本设备不兼容");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(Event event) {
        if (event.getTarget() == Event.TARGET_SHOP_CART) {
            if (event.getType() == Event.TYPE_REFRESH) {
                mPresenter.updateTradeInfo();
            }
            if (event.getType() == Event.TYPE_CANCEL_PRICE_CHANGE) {
                mPresenter.cancelPriceChange((int) event.getData());
            }
        }
    }

    /**
     * 网络变化
     *
     * @param isOnline
     */
    @Override
    public void onNetWorkChange(boolean isOnline) {
        if (mTitleBar == null) {
            mTitleBar = findViewById(R.id.shop_cart_top_bar);
        }
        mTitleBar.setRightIcon(isOnline ? R.drawable.online : R.drawable.offline);
    }

    @Override
    public void setPresenter(Contract.ShopCartPresenter presenter) {
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

    @Override
    protected void onRestart() {
        super.onRestart();
        mPresenter.updateOrderInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
        EventBus.getDefault().unregister(this);
    }
}
