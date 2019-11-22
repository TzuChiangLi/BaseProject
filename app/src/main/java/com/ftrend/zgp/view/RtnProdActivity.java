package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.ShopAdapter;
import com.ftrend.zgp.api.RtnProdContract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.presenter.RtnProdPresenter;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.InputPanel;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.pop.MoneyInputCallback;
import com.ftrend.zgp.utils.pop.RtnProdDialog;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 退货----包含按单退货和不按单退货
 *
 * @author liziqiang@ftrend.cn
 */
public class RtnProdActivity extends BaseActivity implements OnTitleBarListener, RtnProdContract.RtnProdView {
    @BindView(R.id.rtn_prod_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.rtn_prod_top_bar_title)
    TextView mTitleTv;
    @BindView(R.id.rtn_prod_tv_total)
    TextView mTotalTv;
    @BindView(R.id.rtn_prod_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.rtn_prod_rl_bottom_prod)
    RelativeLayout mProdLayout;
    private int oldPosition = -1;
    private RtnProdContract.RtnProdPresenter mPresenter;
    private ShopAdapter<TradeProd> mProdAdapter;
    private static int START_SCAN = 003;

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
        mPresenter.updateTradeInfo();
        if (mProdAdapter == null) {
            //刷新不按单退货的界面
            mProdAdapter = new ShopAdapter<>(R.layout.shop_list_rv_product_item, null, 6);
            ((SimpleItemAnimator) (Objects.requireNonNull(mRecyclerView.getItemAnimator())))
                    .setSupportsChangeAnimations(false);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mProdAdapter);
        mProdAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.rv_item_empty,
                (ViewGroup) mRecyclerView.getParent(), false));
    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = RtnProdPresenter.createPresenter(this);
        }
    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white)
                .autoDarkModeEnable(true).init();
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
    public void setPresenter(RtnProdContract.RtnProdPresenter presenter) {
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

    @OnClick(R.id.rtn_prod_btn_prod_rtn)
    public void pay() {
        if (ClickUtil.onceClick()) {
            return;
        }
        if (!mProdAdapter.getData().isEmpty()) {
            Intent intent = new Intent(RtnProdActivity.this, PayActivity.class);
            intent.putExtra("isSale", false);
            startActivity(intent);
        } else {
            MessageUtil.showError("当前无退货商品");
        }

    }

    @OnClick(R.id.rtn_prod_btn_prod_add)
    public void add() {
        if (ClickUtil.onceClick()) {
            return;
        }
        mPresenter.showRtnProdDialog();
    }

    @OnClick(R.id.rtn_prod_btn_back)
    public void back() {
        if (ClickUtil.onceClick()) {
            return;
        }
        finish();
    }

    @Override
    public void showRtnProdDialog(final List<DepProduct> mProdList) {
        LogUtil.d("----depList.size:" + mProdList.size());
        //不按单退货添加退货商品
        MessageUtil.rtnProd(new RtnProdDialog.onDialogCallBack() {
            @Override
            public void onStart(Button btn) {
                btn.setText(mPresenter.getRtnProdAmount());
            }

            @Override
            public void onLoad(RecyclerView recyclerView, final ShopAdapter<DepProduct> mAdapter, final Button btn) {
                recyclerView.setLayoutManager(new LinearLayoutManager(RtnProdActivity.this));
                mAdapter.setNewData(mProdList);
                recyclerView.addItemDecoration(new DividerItemDecoration(RtnProdActivity.this, DividerItemDecoration.VERTICAL));
                recyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        //添加到购物车中
                        if (mPresenter.addRtnProd(mProdList.get(position))) {
                            mAdapter.notifyItemChanged(position);
                            btn.setText(mPresenter.getRtnProdAmount());
                        }
                    }
                });
            }

            @Override
            public void onScan() {
                try {
                    Intent intent = new Intent("com.summi.scan");
                    intent.setPackage("com.sunmi.sunmiqrcodescanner");
                    startActivityForResult(intent, START_SCAN);
                } catch (Exception e) {
                    MessageUtil.showError("本设备不支持扫码");
                }
            }

            @Override
            public void onClose() {
                //需要刷新界面，已经自带关闭弹窗，这里只需要加入除关闭之外的操作
                if (mProdAdapter != null) {
                    mPresenter.updateRtnProdList();
                }
            }

            @Override
            public void onSearch(String key, ShopAdapter<DepProduct> mAdapter) {
                //需要过滤商品
                List<DepProduct> depProducts = mPresenter.searchDepProdList(key, mProdList);
                if (depProducts != null || !depProducts.isEmpty()) {
                    mAdapter.setNewData(depProducts);
                } else {
                    mAdapter.setNewData(null);
                }
            }
        });
    }

    @Override
    public void initProdList(List<TradeProd> prodList) {
        mProdAdapter.setNewData(prodList);
        mProdAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                if (ClickUtil.onceClick()) {
                    return;
                }
                switch (view.getId()) {
                    case R.id.shop_list_rv_img_add:
                        mPresenter.changeAmount(position, 1);
                        break;
                    case R.id.shop_list_rv_img_minus:
                        mPresenter.changeAmount(position, -1);
                        break;
                    case R.id.shop_list_rv_btn_change_price:
                        showInputPanel(position);
                        break;
                    case R.id.shop_list_rv_btn_del:
                        MessageUtil.question("确定删除此商品？", new MessageUtil.MessageBoxYesNoListener() {
                            @Override
                            public void onYes() {
                                mPresenter.delRtnProd(position);
                            }

                            @Override
                            public void onNo() {
                            }
                        });
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
    public void delTradeProd(int index) {
        mProdAdapter.notifyItemRemoved(index);
    }

    @Override
    public void searchRtnProdList(List<TradeProd> prodList) {
        if (mProdAdapter != null) {
            mProdAdapter.setNewData(prodList);
            mProdAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showInputPanel(final int position) {
        InputPanel.showPriceChange(RtnProdActivity.this, new MoneyInputCallback() {
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
    }

    @Override
    public void showRtnTotal(double rtnTotal) {
        mTotalTv.setText(String.format(Locale.CHINA, "%.2f", rtnTotal).replace("-", ""));
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
