package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.ShopAdapter;
import com.ftrend.zgp.api.RtnTradeContract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.presenter.RtnTradePresenter;
import com.ftrend.zgp.utils.RtnHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.InputPanel;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.pop.MoneyInputCallback;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 退货----包含按单退货和不按单退货
 *
 * @author liziqiang@ftrend.cn
 */
public class RtnTradeActivity extends BaseActivity implements OnTitleBarListener, RtnTradeContract.RtnTradeView {
    @BindView(R.id.rtn_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.rtn_top_bar_title)
    TextView mTitleTv;
    @BindView(R.id.rtn_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.rtn_edt_trade)
    ClearEditText mEdt;
    @BindView(R.id.rtn_btn_chg_mode)
    TextView mChgBtn;
    @BindView(R.id.rtn_btn_search)
    TextView mSearchBtn;
    @BindView(R.id.rtn_img_pay_type)
    ImageView mPayTypeImg;
    @BindView(R.id.rtn_tv_pay_type)
    TextView mPayTypeTv;
    @BindView(R.id.rtn_rl_bottom_trade)
    RelativeLayout mTradeLayout;
    @BindView(R.id.rtn_ll_mid)
    LinearLayout mVipLayout;
    @BindView(R.id.rtn_tv_trade_time)
    TextView mTradeTimeTv;
    @BindView(R.id.rtn_tv_trade_lsno)
    TextView mLsNoTv;
    @BindView(R.id.rtn_tv_cahier)
    TextView mCashierTv;
    @BindView(R.id.rtn_tv_trade_total)
    TextView mTradeTotalTv;
    @BindView(R.id.rtn_tv_rtn_total)
    TextView mRtnTotalTv;
    @BindString(R.string.rtn_title_trade)
    String tradeTitle;
    @BindString(R.string.rtn_title_prod)
    String prodTitle;
    @BindString(R.string.rtn_trade_tv_search)
    String tradeSearch;
    @BindString(R.string.rtn_prod_tv_search)
    String prodSearch;
    private int oldPosition = -1;
    private RtnTradeContract.RtnTradePresenter mPresenter;
    private ShopAdapter<TradeProd> mProdAdapter;
    //退货模式：true----按单退货  false----不按单退货
    private boolean currentMode = true;
    private static int START_SCAN = 003;

    @Override
    public void onNetWorkChange(boolean isOnline) {
        if (mTitleBar == null) {
            mTitleBar = findViewById(R.id.rtn_top_bar);
        }
        mTitleBar.setRightIcon(isOnline ? R.drawable.online : R.drawable.offline);
    }

    @Override
    public void setCurrentModule() {
        LogUtil.setCurrentModule("按单退货");
    }

    @Override
    protected int getLayoutID() {
        return R.layout.rtn_trade_activity;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = RtnTradePresenter.createPresenter(this);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProdAdapter = new ShopAdapter<>(R.layout.rtn_list_rv_product_item, null, 5);
        ((SimpleItemAnimator) (Objects.requireNonNull(mRecyclerView.getItemAnimator()))).setSupportsChangeAnimations(false);
        mRecyclerView.setAdapter(mProdAdapter);
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
    public void setPresenter(RtnTradeContract.RtnTradePresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
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
            }
        }
    }

    private void scanResult(String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
    }

    @OnClick(R.id.rtn_btn_chg_mode)
    public void changeRtnMode() {
        KeyboardUtils.hideSoftInput(this);
        Intent intent = new Intent(RtnTradeActivity.this, RtnProdActivity.class);
        startActivity(intent);
        finish();
//        if (mRecyclerView.getAdapter() != null && mRecyclerView.getAdapter().getItemCount() != 0) {
//            MessageUtil.question("切换退货方式将失去当前数据，是否切换？", new MessageUtil.MessageBoxYesNoListener() {
//                @Override
//                public void onYes() {
//                    currentMode = !currentMode;
//                    mPresenter.changeRtnMode(currentMode);
//                }
//
//                @Override
//                public void onNo() {
//
//                }
//            });
//        } else {
//            currentMode = !currentMode;
//            mPresenter.changeRtnMode(currentMode);
//        }
    }

    @OnClick(R.id.rtn_btn_search)
    public void search() {
        if (ClickUtil.onceClick()) {
            return;
        }
        mProdAdapter.setNewData(null);
        mPresenter.getTradeByLsNo(mEdt.getText().toString());
        KeyboardUtils.hideSoftInput(this);
    }

    @OnClick(R.id.rtn_btn_enter)
    public void enter() {
        if (ClickUtil.onceClick()) {
            return;
        }
        double rtnTotal = Double.parseDouble(mRtnTotalTv.getText().toString().trim());
        if (rtnTotal == 0) {
            showError("未选择退货商品");
            return;
        }
        showRtnInfo(rtnTotal, RtnHelper.isPayCash() ? "" : mPayTypeTv.getText().toString().trim());
    }

    @OnClick(R.id.rtn_btn_cancel)
    public void cancel() {
        if (ClickUtil.onceClick()) {
            return;
        }
        finish();
    }


    @Override
    public void returnHomeActivity() {
        cancel();
    }

    @Override
    public void showInputPanel(final int position) {
        InputPanel.showPriceChange(RtnTradeActivity.this, new MoneyInputCallback() {
            @Override
            public void onOk(double value) {
                mPresenter.changePrice(position, value);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public String validate(double value) {
                return null;
            }
        });
    }

    @Override
    public void showRtnInfo(double rtnTotal, String payTypeName) {
        String msg;
        if (TextUtils.isEmpty(payTypeName)) {
            msg = String.format(Locale.CHINA, "现金退款：￥%.2f", rtnTotal);
        } else {
            msg = String.format(Locale.CHINA, "退款金额￥%.2f，将自动返还至[%s]付款原账户", rtnTotal, payTypeName);
        }
        MessageUtil.question(msg, "确认", "返回",
                new MessageUtil.MessageBoxYesNoListener() {
                    @Override
                    public void onYes() {
                        //先退货，再写入数据库
                        mPresenter.rtnTrade();
                    }

                    @Override
                    public void onNo() {

                    }
                });
    }

    @Override
    public void updateTradeProd(int index) {
        mProdAdapter.notifyItemChanged(index);
        mPresenter.updateTradeInfo();
    }

    @Override
    public void existTrade(final List<TradeProd> data) {
        if (data.isEmpty() || data == null) {
            mProdAdapter.setNewData(null);
            mProdAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.rv_item_empty, (ViewGroup) mRecyclerView.getParent(), false));
            return;
        }
        //防止点击item的子view出现画面闪烁的问题
        mProdAdapter.setNewData(data);
        mProdAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                if (ClickUtil.onceClick()) {
                    return;
                }
                switch (view.getId()) {
                    case R.id.rtn_list_rv_img_add:
                        //商品数量+1
                        mPresenter.changeAmount(position, 1);
                        break;
                    case R.id.rtn_list_rv_img_minus:
                        //改变数量-1
                        mPresenter.changeAmount(position, -1);
                        break;
                    case R.id.rtn_list_rv_btn_change_price:
                        mPresenter.showInputPanel(position);
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
                mRecyclerView.smoothScrollToPosition(position);
            }
        });
    }

    @Override
    public void showTradeTotal(double tradeTotal) {
        mTradeLayout.setVisibility(View.VISIBLE);
        mTradeTotalTv.setText(String.format("%.2f", tradeTotal));
    }

    @Override
    public void showRtnTotal(double rtnTotal) {
        mTradeLayout.setVisibility(View.VISIBLE);
        mRtnTotalTv.setText(String.format("%.2f", rtnTotal));
    }

    @Override
    public void showPayTypeName(String payTypeName, int img) {
        mTradeLayout.setVisibility(View.VISIBLE);
        mPayTypeTv.setText(payTypeName);
        mPayTypeImg.setImageResource(img);
    }

    /**
     * @param info 交易时间，流水号，收款员
     */
    @Override
    public void showTradeInfo(String... info) {
        mTradeTimeTv.setText(info[0]);
        mLsNoTv.setText(info[1]);
        mCashierTv.setText(info[2]);
    }

    @Override
    public void showError(String msg) {
        MessageUtil.showError(msg);
    }

    @Override
    public void showSuccess(String msg) {
        MessageUtil.showSuccess(msg);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
    }
}
