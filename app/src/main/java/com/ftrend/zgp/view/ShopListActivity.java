package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
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
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.pop.VipDialog;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.lxj.xpopup.XPopup;

import java.util.List;
import java.util.Objects;

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
    TextView mTotalTv;
    @BindView(R.id.shop_list_tv_count)
    TextView mCountTv;
    @BindView(R.id.shop_list_btn_cancel)
    Button mCancelBtn;
    @BindView(R.id.shop_list_btn_vip)
    Button mVipBtn;
    @BindColor(R.color.common_rv_item)
    int rv_item_selected;
    @BindColor(R.color.common_white)
    int rv_item_normal;
    private ShopAdapter<TradeProd> mProdAdapter;
    private Contract.ShopListPresenter mPresenter;
    private String lsNo = "", total = "";
    private int oldPosition = -1;


    /**
     * 网络变化
     *
     * @param isOnline
     */
    @Override
    public void onNetWorkChange(boolean isOnline) {
        if (mTitleBar == null) {
            mTitleBar = findViewById(R.id.shop_list_top_bar);
        }
        mTitleBar.setRightIcon(isOnline ? R.drawable.online : R.drawable.offline);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.shop_list_activity;
    }

    @Override
    protected void initData() {
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
        mTitleBar.setRightIcon(ZgParams.isIsOnline() ? R.drawable.online : R.drawable.offline);
        mTitleBar.setOnTitleBarListener(this);
    }


    @OnClick(R.id.shop_list_btn_vip)
    public void selectVipLoginWay() {
        new XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .asCustom(new VipDialog(this))
                .show();
    }

    @OnClick(R.id.shop_list_btn_pay)
    public void doPay() {
        if (mProdAdapter.getData().size() > 0) {
            Intent intent = new Intent(ShopListActivity.this, PayActivity.class);
            intent.putExtra("lsNo", lsNo);
            intent.putExtra("total", total);
            startActivity(intent);
        } else {
            MessageUtil.showWarning("购物车为空");
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
    public void setPresenter(Contract.ShopListPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    @Override
    public void showTradeProd(final List<TradeProd> prodList) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //防止点击item的子view出现画面闪烁的问题
        ((SimpleItemAnimator) (Objects.requireNonNull(mRecyclerView.getItemAnimator()))).setSupportsChangeAnimations(false);
        mProdAdapter = new ShopAdapter<>(R.layout.shop_list_rv_product_item, prodList, 2);
        mRecyclerView.setAdapter(mProdAdapter);
        mProdAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                double amount = mProdAdapter.getData().get(position).getAmount();
                switch (view.getId()) {
                    case R.id.shop_list_rv_img_add:
                        //商品数量+1
                        mProdAdapter.getData().get(position).setAmount(amount + 1);
                        mProdAdapter.notifyItemChanged(position);
                        mPresenter.changeAmount(position, 1);
                        break;
                    case R.id.shop_list_rv_img_minus:
                        //商品数量-1，当商品数量为1时，不再减少
                        mProdAdapter.getData().get(position).setAmount((amount - 1 == 0) ? 1 : amount - 1);
                        mProdAdapter.notifyItemChanged(position);
                        mPresenter.changeAmount(position, -1);
                        break;
                    case R.id.shop_list_rv_btn_change_price:
                        //改价
                        break;
                    case R.id.shop_list_rv_btn_discount:
                        //单品优惠
                        break;
                    case R.id.shop_list_rv_btn_del:
                        //检查行清权限
                        if (TradeHelper.getUserRight(TradeHelper.USER_RIGHT_DEL)) {
                            TradeHelper.delProduct(position);
                            prodList.remove(position);
                            mProdAdapter.notifyItemRemoved(position);
                            mPresenter.updateTradeInfo();
                        } else {
                            //提示用户无此权限
                            MessageUtil.show("当前用户无此权限");
                        }
                        break;
                    default:
                        break;
                }

            }
        });
        mProdAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (oldPosition != -1) {
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
    public void updateTotal(double total) {
        mTotalTv.setText(String.valueOf(total));
    }

    @Override
    public void updateCount(double count) {
        mCountTv.setText(String.valueOf(count));
    }

    @Override
    public void returnHomeActivity() {
        //HomeActivity的启动模式设置为栈内复用
        //如果Activity栈内有HomeActivity存在，把他之上的所有栈全部移除，并将他置顶
        MessageUtil.showSuccess("已挂单");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ShopListActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }, 1500);
    }

    @OnClick(R.id.shop_list_btn_cancel)
    public void cancelTrade() {
        mPresenter.setTradeStatus(TradeHelper.TRADE_STATUS_CANCELLED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
    }
}
