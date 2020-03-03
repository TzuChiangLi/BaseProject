package com.ftrend.zgp.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.ShopAdapter;
import com.ftrend.zgp.api.VipProdContract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.Product;
import com.ftrend.zgp.model.VipInfo;
import com.ftrend.zgp.presenter.VipProdPresenter;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.msg.InputPanel;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.pop.MoneyInputCallback;
import com.ftrend.zgp.utils.pop.ProdDialog;
import com.ftrend.zgp.utils.pop.StringInputCallback;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liziqiang@ftrend.cn
 */
public class VipProdActivity extends BaseActivity implements VipProdContract.VipProdView, OnTitleBarListener {
    @BindView(R.id.vip_prod_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.vip_tv_card)
    TextView mCardTv;
    @BindView(R.id.vip_tv_vip_code)
    TextView mVipCodeTv;
    @BindView(R.id.vip_tv_name)
    TextView mVipNameTv;
    @BindView(R.id.vip_tv_total)
    TextView mTotalTv;
    @BindView(R.id.vip_tv_score)
    TextView mScoreTv;
    @BindView(R.id.vip_prod_name)
    TextView mProdTv;
    @BindView(R.id.vip_total)
    TextView mSetTotalTv;
    @BindView(R.id.vip_img_read_card)
    ImageView mImg;
    @BindView(R.id.vip_rl_info)
    RelativeLayout mVipInfo;
    @BindView(R.id.vip_rl_read)
    CardView mReadCard;
    @BindView(R.id.vip_rl_show)
    CardView mVipCard;
    private VipProdContract.VipProdPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private ShopAdapter<Product> mAdapter;
    private int oldDepIndex = -1;
    private boolean showingImg = true;
    private AnimatorSet outSet = null, inSet = null;

    @Override
    public void onNetWorkChange(boolean isOnline) {
        if (mTitleBar == null) {
            mTitleBar = findViewById(R.id.vip_prod_top_bar);
        }
        mTitleBar.setRightIcon(isOnline ? R.drawable.online : R.drawable.offline);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.vip_prod_activity;
    }

    @Override
    protected void initData() {
        mPresenter.initVipProd();
    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = VipProdPresenter.createPresenter(this);
        }
    }

    @Override
    protected void onResume() {
        inSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, ResourceUtils.getAnimIdByName("card_flip_in"));
        outSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, ResourceUtils.getAnimIdByName("card_flip_out"));
        super.onResume();
    }

    @Override
    protected void initTitleBar() {
        mTitleBar.setOnTitleBarListener(this);
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        mTitleBar.setRightIcon(ZgParams.isIsOnline() ? R.drawable.online : R.drawable.offline);
    }

    @OnClick(R.id.vip_ll_prod)
    public void setProd() {
        mPresenter.showProdDialog();
    }

    @OnClick(R.id.vip_btn_pay)
    public void pay() {
        mPresenter.pay(Double.parseDouble(mSetTotalTv.getText().toString()));
    }

    @OnClick(R.id.vip_read_card)
    public void readCard() {
        mPresenter.readCard(false);
    }

    @OnClick(R.id.vip_ll_total)
    public void modify() {
        InputPanel.showVipProdDialog(this, Double.parseDouble(mSetTotalTv.getText().toString()),
                new MoneyInputCallback() {
                    @Override
                    public void onOk(double value) {
                        mSetTotalTv.setText(String.format("%.2f", value));
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


    public void returnHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void cardPaySuccess(String msg) {
        MessageUtil.waitSuccesss(msg, new MessageUtil.MessageBoxOkListener() {
            @Override
            public void onOk() {
                if (!showingImg) {
                    clearVipInfo();
                    flipToImg();
                }
            }
        });
    }

    @Override
    public void cardPayWait(String msg) {
        if (MessageUtil.isWaiting()) {
            MessageUtil.waitUpdate(msg);
        } else {
            MessageUtil.waitBegin(msg, new MessageUtil.MessageBoxCancelListener() {
                @Override
                public boolean onCancel() {
                    return mPresenter.cardPayCancel();
                }
            });
        }
    }

    @Override
    public void cardPayFail(String msg) {
        MessageUtil.waitError(msg, null);
    }

    @Override
    public void cardPayFail(String code, String msg) {
        MessageUtil.waitError(code, msg, null);
    }

    @Override
    public void cardPayTimeout(String msg) {
        MessageUtil.waitEnd();
        MessageUtil.question(msg, "重试", "取消", new MessageUtil.MessageBoxYesNoListener() {
            @Override
            public void onYes() {
                mPresenter.cardPayRetry();
            }

            @Override
            public void onNo() {
            }
        });
    }

    @Override
    public void cardPayPassword() {
        MessageUtil.waitEnd();
        InputPanel.showInput(this, "请输入支付密码：", new StringInputCallback() {
            @Override
            public void onOk(String value) {
                mPresenter.cardPayPass(value);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public String validate(String value) {
                return null;
            }
        });
    }


    @Override
    public void showProdDialog(final List<Product> mProdList) {
        //不按单退货添加退货商品
        MessageUtil.vipProd(new ProdDialog.onDialogCallBack() {
            @Override
            public void onLoad(RecyclerView recyclerView, ShopAdapter<Product> adapter) {
                mAdapter = adapter;
                mRecyclerView = recyclerView;
                mRecyclerView.setLayoutManager(new LinearLayoutManager(VipProdActivity.this));
                mAdapter.setNewData(mProdList);
                mRecyclerView.addItemDecoration(new DividerItemDecoration(VipProdActivity.this, DividerItemDecoration.VERTICAL));
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onItemClick(RecyclerView view, ShopAdapter<Product> adapter, int position) {
                if (ClickUtil.onceClick()) {
                    return;
                }
                if (oldDepIndex != -1 && oldDepIndex < mAdapter.getItemCount()) {
                    mProdList.get(oldDepIndex).setSelect(false);
                    mAdapter.notifyItemChanged(oldDepIndex);
                }
                oldDepIndex = position;
                mAdapter.getData().get(position).setSelect(true);
                mAdapter.notifyItemChanged(position);
                mPresenter.setVipProd(mAdapter.getData().get(position));
            }

            @Override
            public void onScan() {
                try {
                    scan();
                } catch (Exception e) {
                    MessageUtil.showError("本设备不支持扫码");
                }
            }

            @Override
            public void onClose() {
            }

            @Override
            public void onSearch(String key, ShopAdapter<Product> mAdapter) {
                //需要过滤商品
                List<Product> products = mPresenter.searchDepProdList(key, mProdList);
                if (products != null && !products.isEmpty()) {
                    mAdapter.setNewData(products);
                } else {
                    mAdapter.setNewData(null);
                }
            }
        });
    }

    @Override
    public void setVipProd(String prod) {
        mProdTv.setText(prod);
    }

    @Override
    public void setVipInfo(VipInfo vip) {
        MessageUtil.waitEnd();
        if (showingImg) {
            flipToCard();
        } else {
            flip();
        }

        String regex = "(.{4})";
        String cardCode = vip.getCardCode().replaceAll(regex, "$1\t\t");

        mCardTv.setText(cardCode);
        mVipCodeTv.setText(vip.getVipCode());
        mVipNameTv.setText(vip.getVipName());
        mTotalTv.setText(String.format("%.2f", vip.getBalance()));
        mScoreTv.setText(vip.getVipScore());
    }

    private void clearVipInfo() {
        mCardTv.setText("");
        mVipCodeTv.setText("");
        mVipNameTv.setText("");
        mTotalTv.setText("");
        mScoreTv.setText("0.00");
    }

    @Override
    public void show(String msg) {
        MessageUtil.show(msg);
    }

    @Override
    public void showError(String msg) {
        MessageUtil.showError(msg);
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
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
    }

    @Override
    public void setPresenter(VipProdContract.VipProdPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }


    private void flipToImg() {
        if (!outSet.isRunning() && !inSet.isRunning()) {
            showingImg = true;
            inSet.setTarget(mReadCard);
            mVipCard.setCardElevation(0);
            inSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            outSet.setTarget(mVipCard);
            outSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    inSet.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            outSet.start();
        }
    }

    /**
     * 已是Vip卡片状态
     */
    private void flip() {
        if (!outSet.isRunning() && !inSet.isRunning()) {
            showingImg = false;
            clearVipInfo();
            mVipCard.setCardElevation(0);
            inSet.setTarget(mVipCard);
            inSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mVipCard.setCardElevation(ConvertUtils.dp2px(4));
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            outSet.setTarget(mVipCard);
            outSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    inSet.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            outSet.start();
        }
    }

    /**
     * 第一次从占位图翻转到Vip卡片状态
     */
    private void flipToCard() {
        if (showingImg && !outSet.isRunning() && !inSet.isRunning()) {
            showingImg = false;

            mReadCard.setCardElevation(0);
            mVipCard.setCardElevation(0);

            outSet.setTarget(mReadCard);
            outSet.start();

            inSet.setTarget(mVipCard);
            inSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mVipCard.setCardElevation(ConvertUtils.dp2px(4));
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            inSet.start();
        }
    }
}
