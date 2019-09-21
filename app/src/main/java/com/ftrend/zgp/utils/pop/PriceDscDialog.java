package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.keyboard.KeyboardView;
import com.ftrend.log.LogUtil;
import com.ftrend.zgp.R;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.event.Event;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.view.ShopCartActivity;
import com.ftrend.zgp.view.ShopListActivity;
import com.lxj.xpopup.core.BottomPopupView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 改价、输入手机号、单项优惠、整单优惠弹出窗口
 *
 * @author liziqiang@ftrend.cn
 */
public class PriceDscDialog extends BottomPopupView implements View.OnClickListener, KeyboardView.OnItemClickListener, View.OnFocusChangeListener {
    @BindView(R.id.vip_way_ll_info)
    LinearLayout mInfoLayout;
    @BindView(R.id.vip_way_edt)
    ClearEditText mEdt;
    @BindView(R.id.vip_way_tv_title)
    TextView mTitleTv;
    @BindView(R.id.vip_mobile_btn_submit)
    Button mSubmitBtn;
    @BindView(R.id.vip_way_img_close)
    ImageView mCloseImg;
    //会员弹窗：1-手机号
    public static final int DIALOG_MOBILE = 1;
    //购物车：  2-改价
    public static final int DIALOG_CHANGE_PRICE = 2;
    //优惠：    3-单项优惠
    public static final int DIALOG_SINGLE_RSC = 3;
    //优惠：    3-整单优惠
    public static final int DIALOG_WHOLE_RSC = 4;
    private int type;
    private int index = 0;
    private Context mContext;
    private KeyboardView mKeyView;
    private View mKeyViewStub, mRateDscView;
    private EditText mRateEdt, mDscEdt;
    private TextView mPriceTv, mTotalTv, mProdNameTv, mMaxRateTv, mMaxDscTv;
    private boolean isFirst = true;


    public PriceDscDialog(@NonNull Context context, int type) {
        super(context);
        this.type = type;
        mContext = context;
    }

    public PriceDscDialog(@NonNull Context context, int type, int index) {
        super(context);
        this.type = type;
        this.index = index;
        mContext = context;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.vip_dsc_mobile;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        KeyboardUtils.hideSoftInput(this);
        switch (type) {
            case DIALOG_MOBILE:
                //手机号
                mKeyViewStub = ((ViewStub) findViewById(R.id.vip_way_key_lite_view)).inflate();
                mKeyView = mKeyViewStub.findViewById(R.id.vip_way_keyboard);
                mKeyView.show();
                mEdt.setInputType(InputType.TYPE_NULL);
                mEdt.setOnClickListener(this);
                mKeyView.setOnKeyboardClickListener(this);
                break;
            case DIALOG_CHANGE_PRICE:
                //改价
                mKeyViewStub = ((ViewStub) findViewById(R.id.vip_way_key_lite_view)).inflate();
                mKeyView = mKeyViewStub.findViewById(R.id.vip_way_keyboard);
                mKeyView.show();
                mEdt.setInputType(InputType.TYPE_NULL);
                mTitleTv.setText("请输入修改后的商品价格：");
                mSubmitBtn.setText("修改");
                mEdt.setOnClickListener(this);
                mKeyView.setOnKeyboardClickListener(this);
                break;
            case DIALOG_SINGLE_RSC:
                //优惠
                initSingleDscView();
                initSingleDscData();
                break;
            case DIALOG_WHOLE_RSC:
                //优惠
                initWholeDscView();
                initWholeDscData();
                break;
            default:
                break;
        }
    }


    /**
     * 初始化整单优惠数据
     */
    private void initWholeDscData() {
        mPriceTv.setText(String.valueOf(TradeHelper.getWholePrice()));
    }

    /**
     * 初始化整单优惠面板界面
     */
    private void initWholeDscView() {
        //懒加载
        mKeyViewStub = ((ViewStub) findViewById(R.id.vip_way_key_func_view)).inflate();
        mKeyView = mKeyViewStub.findViewById(R.id.vip_way_keyboard);
        mRateDscView = ((ViewStub) findViewById(R.id.vip_dsc_rate_view)).inflate();
        //注册控件
        mDscEdt = mRateDscView.findViewById(R.id.vip_dsc_edt_dsc);
        mRateEdt = mRateDscView.findViewById(R.id.vip_dsc_edt_rate);
        mProdNameTv = mRateDscView.findViewById(R.id.vip_dsc_tv_prodname);
        mPriceTv = mRateDscView.findViewById(R.id.vip_dsc_tv_price);
        mTotalTv = mRateDscView.findViewById(R.id.vip_dsc_tv_total);
        //防止键盘弹出
        mRateEdt.setInputType(InputType.TYPE_NULL);
        mDscEdt.setInputType(InputType.TYPE_NULL);
        //展示自家小键盘
        mKeyView.show();
        //初始化其他界面信息
        mTitleTv.setText("请输入本笔交易优惠信息：");
        mEdt.setVisibility(GONE);
        mSubmitBtn.setVisibility(GONE);
        mProdNameTv.setVisibility(GONE);
        mKeyView.setOnKeyboardClickListener(this);
        mRateEdt.selectAll();

        mRateEdt.setOnFocusChangeListener(this);
    }


    /**
     * 折扣单项优惠初始化界面
     */
    private void initSingleDscView() {
        //懒加载
        mKeyViewStub = ((ViewStub) findViewById(R.id.vip_way_key_func_view)).inflate();
        mKeyView = mKeyViewStub.findViewById(R.id.vip_way_keyboard);
        mRateDscView = ((ViewStub) findViewById(R.id.vip_dsc_rate_view)).inflate();
        //注册控件
        mDscEdt = mRateDscView.findViewById(R.id.vip_dsc_edt_dsc);
        mRateEdt = mRateDscView.findViewById(R.id.vip_dsc_edt_rate);
        mProdNameTv = mRateDscView.findViewById(R.id.vip_dsc_tv_prodname);
        mPriceTv = mRateDscView.findViewById(R.id.vip_dsc_tv_price);
        mTotalTv = mRateDscView.findViewById(R.id.vip_dsc_tv_total);
        mMaxDscTv = mRateDscView.findViewById(R.id.vip_dsc_tv_dsc_range);
        mMaxRateTv = mRateDscView.findViewById(R.id.vip_dsc_tv_rate_range);
        //防止键盘弹出
        mRateEdt.setInputType(InputType.TYPE_NULL);
        mDscEdt.setInputType(InputType.TYPE_NULL);
        //展示自家小键盘
        mKeyView.show();
        //初始化其他界面信息
        mTitleTv.setText("商品单项优惠：");
        mEdt.setVisibility(GONE);
        mSubmitBtn.setVisibility(GONE);
        mKeyView.setOnKeyboardClickListener(this);
        mRateEdt.selectAll();

        mRateEdt.setOnFocusChangeListener(this);
    }

    /**
     * 折扣优惠初始化数据面板
     */
    private void initSingleDscData() {
        //获取该条商品的信息
        TradeProd tradeProd = TradeHelper.getTradeProdList().get(index);
        mPriceTv.setText(String.valueOf(tradeProd.getPrice()));
        mTotalTv.setText(String.valueOf(tradeProd.getTotal() / tradeProd.getAmount()));
        mProdNameTv.setText(tradeProd.getProdName());
        mMaxRateTv.setText(TradeHelper.getMaxDscRate() == 0 ? "此商品无优惠活动"
                : "(0%-" + TradeHelper.getMaxDscRate() + "%)");
        mMaxDscTv.setText(TradeHelper.getMaxSingleDsc(index) == 0 ? "此商品无优惠活动"
                : "(0-" + TradeHelper.getMaxSingleDsc(index) + "元)");
        mDscEdt.setText(String.valueOf(tradeProd.getManuDsc() + tradeProd.getVipDsc() + tradeProd.getTranDsc()));
        mRateEdt.setText(String.valueOf(TradeHelper.getSingleRate(index, Double.parseDouble(mDscEdt.getText().toString()))));
        mRateEdt.selectAll();
    }


    @OnClick(R.id.vip_mobile_btn_submit)
    public void submit() {
        switch (type) {
            case DIALOG_MOBILE:
                break;
            case DIALOG_CHANGE_PRICE:
                //TODO 价格格式验证
                if (TextUtils.isEmpty(mEdt.getText().toString())) {
                    return;
                }
                if (mContext instanceof ShopListActivity) {
                    if (TradeHelper.priceChangeInShopList(index, Double.parseDouble(mEdt.getText().toString()))) {
                        Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_REFRESH, Double.parseDouble(mEdt.getText().toString()));
                        MessageUtil.showSuccess("改价成功");
                        KeyboardUtils.hideSoftInput(this);
                        dismiss();
                    } else {
                        MessageUtil.showError("改价失败");
                    }
                    return;
                }
                if (mContext instanceof ShopCartActivity) {
                    if (TradeHelper.priceChangeInShopCart(Double.parseDouble(mEdt.getText().toString()))) {
                        Event.sendEvent(Event.TARGET_SHOP_CART, Event.TYPE_REFRESH);
                        KeyboardUtils.hideSoftInput(this);
                        MessageUtil.showSuccess("改价成功");
                        dismiss();
                    } else {
                        MessageUtil.showError("改价失败");
                    }
                    return;
                }
                break;
            default:
                break;
        }
    }


    @OnClick(R.id.vip_way_img_close)
    public void close() {
        if (mContext instanceof ShopCartActivity) {
            //需要撤销添加的最后一条
            Event.sendEvent(Event.TARGET_SHOP_CART, Event.TYPE_CANCEL_PRICE_CHANGE);
        }
        KeyboardUtils.hideSoftInput(this);
        dismiss();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vip_way_edt:
                if (mKeyView.isShow()) {
                    mKeyView.show();
                }
                break;
            default:
                break;
        }
    }


    //-------------------------------------输入实时监听--------------------------------------------//
    private TextWatcher rateWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count != 0) {
                if (s.toString().contains(".")) {
                    errorTextColor(mRateEdt);
                    return;
                }
                if (Long.valueOf(s.toString()) > TradeHelper.getMaxDscRate()) {
                    //超过限制
                    errorTextColor(mRateEdt);
                } else {
                    restoreTextColor(mRateEdt);
                    switch (type) {
                        case DIALOG_SINGLE_RSC:
                            //TODO 2019年9月20日17:16:17 需要加入正则校验是否正确
                            //设置优惠金额
                            mDscEdt.setText(String.format("%.2f", TradeHelper.getSingleDsc(index, Integer.valueOf(s.toString()))));
                            //修改优惠后的价格
                            mTotalTv.setText(String.format("%.2f", TradeHelper.getSingleTotal(index, Double.parseDouble(mDscEdt.getText().toString()))));
                            //如果优惠金额大于最大金额
                            if (Double.parseDouble(mDscEdt.getText().toString()) > TradeHelper.getMaxSingleDsc(index)) {
                                errorTextColor(mDscEdt);
                            } else {
                                restoreTextColor(mDscEdt);
                            }
                            break;
                        case DIALOG_WHOLE_RSC:
                            mTotalTv.setText(String.format("%.2f", TradeHelper.getWholeTotal(Integer.valueOf(s.toString()))));
                            mDscEdt.setText(String.format("%.2f", TradeHelper.getWholeDsc(Integer.valueOf(s.toString()))));
                            break;
                        default:
                            break;
                    }
                }
            } else {
                mDscEdt.setText("");
                restoreTextColor(mRateEdt);
                restoreTextColor(mDscEdt);
                mTotalTv.setText(mPriceTv.getText().toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextWatcher dscWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count != 0) {
                if (Double.parseDouble(s.toString()) > TradeHelper.getMaxDscTotal()) {
                    //超过限制
                    errorTextColor(mDscEdt);
                } else {
                    restoreTextColor(mDscEdt);
                    switch (type) {
                        case DIALOG_SINGLE_RSC:
                            //TODO 2019年9月20日17:16:17 需要加入正则校验是否正确
                            //设置折扣率
                            mRateEdt.setText(String.valueOf(TradeHelper.getSingleRate(index, Double.parseDouble(s.toString()))));
                            //修改优惠后的价格
                            mTotalTv.setText(String.format("%.2f", TradeHelper.getSingleTotal(index, Double.parseDouble(mDscEdt.getText().toString()))));
                            if (Integer.parseInt(mRateEdt.getText().toString()) > TradeHelper.getMaxDscRate()) {
                                errorTextColor(mRateEdt);
                            } else {
                                restoreTextColor(mRateEdt);
                            }
                            break;
                        case DIALOG_WHOLE_RSC:
                            mRateEdt.setText(String.valueOf(TradeHelper.getWholeRate(Double.parseDouble(s.toString()))));
                            mTotalTv.setText(String.format("%.2f", TradeHelper.getWholeTotal(Double.parseDouble(s.toString()))));
                            break;
                        default:
                            break;
                    }
                }
            } else {
                mRateEdt.setText("");
                restoreTextColor(mRateEdt);
                restoreTextColor(mDscEdt);
                mTotalTv.setText(mPriceTv.getText().toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //-------------------------------------键盘响应--------------------------------------------//
    @Override
    public void onKeyClick(View v, int key) {
        switch (type) {
            case DIALOG_SINGLE_RSC:
            case DIALOG_WHOLE_RSC:
                if (mRateEdt.hasFocus()) {
                    mRateEdt.setText(isFirst ? String.valueOf(key) : mRateEdt.getText().append(String.valueOf(key)));
                    isFirst = false;
                } else {
                    mDscEdt.setText(mDscEdt.getText().append(String.valueOf(key)));
                }

                break;
            case DIALOG_MOBILE:
            case DIALOG_CHANGE_PRICE:
            default:
                mEdt.setText(mEdt.getText().append(String.valueOf(key)));
                break;
        }

    }

    @Override
    public void onDeleteClick() {
        switch (type) {
            case DIALOG_SINGLE_RSC:
            case DIALOG_WHOLE_RSC:
                if (mRateEdt.hasFocus()) {
                    mRateEdt.setText(TextUtils.isEmpty(mRateEdt.getText().toString()) ? "" :
                            mRateEdt.getText().toString().trim().substring(0, mRateEdt.getText().toString().trim().length() - 1));
                } else {
                    mDscEdt.setText(TextUtils.isEmpty(mDscEdt.getText().toString()) ? "" :
                            mDscEdt.getText().toString().trim().substring(0, mDscEdt.getText().toString().trim().length() - 1));
                }

                break;
            case DIALOG_MOBILE:
            case DIALOG_CHANGE_PRICE:
            default:
                mEdt.setText(TextUtils.isEmpty(mEdt.getText().toString()) ? "" :
                        mEdt.getText().toString().trim().substring(0, mEdt.getText().toString().trim().length() - 1));
                break;
        }

    }

    @Override
    public void onPointClick() {
        switch (type) {
            case DIALOG_SINGLE_RSC:
            case DIALOG_WHOLE_RSC:
                if (mDscEdt.hasFocus()) {
                    mDscEdt.getText().append('.');
                }
                break;
            case DIALOG_MOBILE:
            case DIALOG_CHANGE_PRICE:
            default:
                mEdt.getText().append('.');
                break;
        }
    }

    @Override
    public void onHideClick(View v) {
        dismiss();
    }

    @Override
    public void onNextClick() {
        switch (type) {
            case DIALOG_SINGLE_RSC:
            case DIALOG_WHOLE_RSC:
                if (mRateEdt.hasFocus()) {
                    mDscEdt.requestFocus();
                    mDscEdt.selectAll();
                } else {
                    mRateEdt.requestFocus();
                    mRateEdt.selectAll();
                }
                break;
            case DIALOG_MOBILE:
            case DIALOG_CHANGE_PRICE:
            default:
                break;
        }
    }

    @Override
    public void onClearClick() {
        switch (type) {
            case DIALOG_SINGLE_RSC:
            case DIALOG_WHOLE_RSC:
                if (mRateEdt.hasFocus()) {
                    mRateEdt.setText("");
                } else {
                    mDscEdt.setText("");
                }
                restoreTextColor(mRateEdt);
                restoreTextColor(mDscEdt);
                break;
            case DIALOG_MOBILE:
            case DIALOG_CHANGE_PRICE:
            default:
                mEdt.setText("");
                break;
        }
    }

    @Override
    public void onCancelClick() {
        dismiss();
    }

    @Override
    public void onEnterClick() {
        switch (type) {
            case DIALOG_SINGLE_RSC:
                if (TextUtils.isEmpty(mRateEdt.getText().toString()) && TextUtils.isEmpty(mDscEdt.getText().toString())) {
                    dismiss();
                    return;
                }
                if (Integer.parseInt(mRateEdt.getText().toString()) <= TradeHelper.getMaxDscRate() &&
                        Double.parseDouble(mDscEdt.getText().toString()) <= TradeHelper.getMaxSingleDsc(index)) {
                    //两个都为true的时候，才能保存成功
                    if (TradeHelper.saveSingleDsc(index, Double.parseDouble(mDscEdt.getText().toString()))) {
                        Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_REFRESH);
                        dismiss();
                    }
                } else {
                    MessageUtil.showError("失败");
                }
                break;
            case DIALOG_WHOLE_RSC:
                if (TextUtils.isEmpty(mRateEdt.getText().toString()) && TextUtils.isEmpty(mDscEdt.getText().toString())) {
                    dismiss();
                    return;
                }
                if (Integer.parseInt(mRateEdt.getText().toString()) <= TradeHelper.getMaxDscRate() &&
                        Double.parseDouble(mDscEdt.getText().toString()) <= TradeHelper.getMaxSingleDsc(index)) {
                    //两个都为true的时候，才能保存成功


                } else {
                    MessageUtil.showError("失败");
                }
                break;
            case DIALOG_MOBILE:
            case DIALOG_CHANGE_PRICE:
            default:
                break;
        }


    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        LogUtil.d(String.valueOf(hasFocus));
        if (hasFocus) {
            mDscEdt.removeTextChangedListener(dscWatcher);
            mRateEdt.addTextChangedListener(rateWatcher);
        } else {
            mRateEdt.removeTextChangedListener(rateWatcher);
            mDscEdt.addTextChangedListener(dscWatcher);
        }
    }


    private void errorTextColor(EditText edt) {
        edt.setTextColor(Color.RED);
    }

    private void restoreTextColor(EditText edt) {
        edt.setTextColor(Color.BLACK);
    }
}