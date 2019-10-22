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
import com.ftrend.zgp.utils.DscHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.event.Event;
import com.ftrend.zgp.utils.msg.InputPanel;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    @BindView(R.id.shop_list_btn_add)
    Button mAddBtn;
    @BindView(R.id.shop_list_tv_vip_name)
    TextView mVipNameTv;
    @BindView(R.id.shop_list_tv_card_grade)
    TextView mCardGradeTv;
    @BindView(R.id.shop_list_tv_not_vip)
    TextView mNotVipTv;
    @BindColor(R.color.common_rv_item)
    int rv_item_selected;
    @BindColor(R.color.common_white)
    int rv_item_normal;
    private ShopAdapter<TradeProd> mProdAdapter;
    private Contract.ShopListPresenter mPresenter;
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
        mPresenter.initShopList();
        mPresenter.showVipInfo();
    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = ShopListPresenter.createPresenter(this);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        mTitleBar.setRightIcon(ZgParams.isIsOnline() ? R.drawable.online : R.drawable.offline);
        mTitleBar.setOnTitleBarListener(this);
    }


    @OnClick(R.id.shop_list_btn_pay)
    public void doPay() {
        if (ClickUtil.onceClick()) {
            return;
        }
        mPresenter.refreshTrade();
        if (mProdAdapter.getData().size() > 0) {
            Intent intent = new Intent(ShopListActivity.this, PayActivity.class);
            startActivity(intent);
        } else {
            MessageUtil.showWarning("购物车为空");
        }
    }


    @OnClick(R.id.shop_list_btn_add)
    public void add() {
        if (ClickUtil.onceClick()) {
            return;
        }
        finish();
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

    @OnClick(R.id.shop_list_btn_more)
    public void more() {
        if (ClickUtil.onceClick()) {
            return;
        }
        InputPanel.showMoreFuncDialog(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(Event event) {
        if (event.getTarget() == Event.TARGET_SHOP_LIST) {
            switch (event.getType()) {
                case Event.TYPE_REFRESH:
                    if (event.getData() != null) {
                        mProdAdapter.getData().get(oldPosition).setPrice((Double) event.getData());
                    }
                    mProdAdapter.notifyItemChanged(oldPosition);
                    mPresenter.updateTradeInfo();
                    break;
                case Event.TYPE_REFRESH_WHOLE_PRICE:
                    mProdAdapter.notifyDataSetChanged();
                    mPresenter.updateTradeInfo();
                    break;
                case Event.TYPE_REFRESH_VIP_INFO:
                    showVipInfoOnline();
                    mProdAdapter.notifyDataSetChanged();
                    mPresenter.updateTradeInfo();
                    break;
                case Event.TYPE_ENTER_SCAN:
                    Intent intent = new Intent("com.summi.scan");
                    intent.setPackage("com.sunmi.sunmiqrcodescanner");
                    startActivityForResult(intent, 001);
                    break;
                case Event.TYPE_COMMIT_WHOLE_DSC:
                    if (DscHelper.commitWholeDsc()) {
                        Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_REFRESH_WHOLE_PRICE);
                    } else {
                        MessageUtil.showError("失败");
                    }
                    break;
                case Event.TYPE_TOAST:
                    MessageUtil.show((String) event.getData());
                    break;
                case Event.TYPE_DIALOG_CANCEL_TRADE:
                    mPresenter.checkCancelTradeRight();
                    break;
                case Event.TYPE_DIALOG_VIP_DSC:
                    InputPanel.showVipWayDialog(this);
                    break;
                case Event.TYPE_DIALOG_HANG_UP:
                    MessageUtil.question("是否挂起当前交易？", new MessageUtil.MessageBoxYesNoListener() {
                        @Override
                        public void onYes() {
                            mPresenter.setTradeStatus(TradeHelper.TRADE_STATUS_HANGUP);
                        }

                        @Override
                        public void onNo() {
                            MessageUtil.show("已放弃当前操作");
                        }
                    });
                    break;
                case Event.TYPE_DIALOG_WHOLE_DSC:
                    InputPanel.showWholeDscChange(this);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 展示会员信息
     */
    @Override
    public void showVipInfoOnline() {
        mNotVipTv.setVisibility(View.GONE);
        mVipNameTv.setText(TradeHelper.vip.getVipName());
        mCardGradeTv.setText(String.format("%s/%s", TradeHelper.vip.getCardCode(), TradeHelper.vip.getVipGrade()));
    }

    @Override
    public void showVipInfoOffline() {
        mNotVipTv.setVisibility(View.GONE);
        mVipNameTv.setVisibility(View.GONE);
        mCardGradeTv.setVisibility(View.GONE);
    }

    @Override
    public void showError(String error) {
        MessageUtil.showError(error);
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
                        mPresenter.getProdPriceFlag(prodList.get(position).getProdCode(), prodList.get(position).getBarCode(), position);
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
                if (ClickUtil.onceClick()) {
                    return;
                }
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
    public void updateTotal(double total) {
        mTotalTv.setText(String.format("%.2f", total));
    }

    @Override
    public void updateCount(double count) {
        mCountTv.setText(String.valueOf(count).replace(".0", ""));
    }

    @Override
    public void delTradeProd(int index) {
        mProdAdapter.notifyItemRemoved(index);
        mPresenter.updateTradeInfo();
    }

    @Override
    public void updateTradeProd(int index) {
        mProdAdapter.notifyItemChanged(index);
        mPresenter.updateTradeInfo();
    }

    @Override
    public void returnHomeActivity(final String status) {
        Intent intent = new Intent(ShopListActivity.this, HomeActivity.class);
        startActivity(intent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MessageUtil.showSuccess(status);
            }
        }, 200);
    }

    @Override
    public void showPriceChangeDialog(int index) {
        //弹出改价窗口
        InputPanel.showPriceChange(ShopListActivity.this, index);
    }

    @Override
    public void showNoRightDscDialog(String msg) {
        MessageUtil.showError(msg);
    }

    @Override
    public void hasDelProdRight(final int index) {
        MessageUtil.question("确定删除此商品？", new MessageUtil.MessageBoxYesNoListener() {
            @Override
            public void onYes() {
                mPresenter.delTradeProd(index);
            }

            @Override
            public void onNo() {
            }
        });
    }

    @Override
    public void showSingleDscDialog(int index) {
        //弹出改价窗口
        InputPanel.showSingleDscChange(ShopListActivity.this, index);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
        EventBus.getDefault().unregister(this);
    }
}
