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
import com.ftrend.zgp.utils.DscHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.event.Event;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.view.ShopCartActivity;
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
    private boolean isFirst = true, inputFlag = true;


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
        return R.layout.vip_dsc_mobile_dialog;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        KeyboardUtils.hideSoftInput(this);
        switch (type) {
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
        DscHelper.beginWholeDsc();
        mMaxRateTv.setText(TradeHelper.getMaxWholeRate() == 0 ? "(无优惠)"
                : "(0-" + TradeHelper.getMaxWholeRate() + "%)");
        mMaxDscTv.setText(TradeHelper.getMaxWholeDsc() == 0 ? "(无优惠)"
                : "(0-" + TradeHelper.priceFormat(TradeHelper.getMaxWholeDsc()) + "元)");
        mPriceTv.setText(String.valueOf(TradeHelper.getWholeForDscPrice()));
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
        mMaxDscTv = mRateDscView.findViewById(R.id.vip_dsc_tv_dsc_range);
        mMaxRateTv = mRateDscView.findViewById(R.id.vip_dsc_tv_rate_range);
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
        TradeProd tradeProd = TradeHelper.getProdList().get(index);
        mPriceTv.setText(String.valueOf(tradeProd.getPrice()));
        mTotalTv.setText(String.valueOf(tradeProd.getTotal() / tradeProd.getAmount()));
        mProdNameTv.setText(tradeProd.getProdName());
        mMaxRateTv.setText(TradeHelper.getMaxSingleRate(index) == 0 ? "(无优惠)"
                : "(0-" + TradeHelper.getMaxSingleRate(index) + "%)");
        mMaxDscTv.setText(TradeHelper.getMaxSingleDsc(index) == 0 ? "(无优惠)"
                : "(0-" + TradeHelper.priceFormat(TradeHelper.getMaxSingleDsc(index)) + "元)");
        mDscEdt.setText(String.valueOf(tradeProd.getManuDsc() + tradeProd.getVipDsc() + tradeProd.getTranDsc()));
        mRateEdt.setText(String.valueOf(TradeHelper.getSingleRate(index, Double.parseDouble(mDscEdt.getText().toString()))));
        mRateEdt.selectAll();
    }


    @OnClick(R.id.vip_way_img_close)
    public void close() {
        if (mContext instanceof ShopCartActivity) {
            //需要撤销添加的最后一条
            Event.sendEvent(Event.TARGET_SHOP_CART, Event.TYPE_CANCEL_PRICE_CHANGE);
        }
        KeyboardUtils.hideSoftInput(this);
        DscHelper.cancelWholeDsc();
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

    @Override
    protected void onDismiss() {
        super.onDismiss();
        if (mContext instanceof ShopCartActivity) {
            //需要撤销添加的最后一条
            if (TextUtils.isEmpty(mEdt.getText().toString())) {
                Event.sendEvent(Event.TARGET_SHOP_CART, Event.TYPE_CANCEL_PRICE_CHANGE);
            }
            DscHelper.cancelWholeDsc();
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
                    MessageUtil.show("格式不正确");
                    errorTextColor(mRateEdt);
                    return;
                }
                if (!TradeHelper.checkRateFormat(s.toString())) {
                    MessageUtil.show("格式不正确");
                    return;
                }
                switch (type) {
                    case DIALOG_SINGLE_RSC:
                        if (Long.valueOf(s.toString()) > TradeHelper.getMaxSingleRate(index)) {
                            //超过限制
                            errorTextColor(mRateEdt);
                        } else {
                            restoreTextColor(mRateEdt);
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
                        }
                        break;
                    case DIALOG_WHOLE_RSC:
                        if (TradeHelper.getWholeForDscPrice() == 0) {
                            MessageUtil.show("当前无法优惠");
                            return;
                        }
                        if (Long.valueOf(s.toString()) > TradeHelper.getMaxWholeRate()) {
                            //超过限制
                            errorTextColor(mRateEdt);
                        } else {
                            restoreTextColor(mRateEdt);
                            DscHelper.wholeDscByRate(Integer.valueOf(s.toString()));
                            mTotalTv.setText(String.format("%.2f", TradeHelper.getWholeTotal(Integer.valueOf(s.toString()))));
                            mDscEdt.setText(String.format("%.2f", TradeHelper.getWholeDsc(Integer.valueOf(s.toString()))));
                        }
                        break;
                    default:
                        break;
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
                switch (type) {
                    case DIALOG_SINGLE_RSC:
                        if (Double.parseDouble(s.toString()) > TradeHelper.getMaxSingleDsc(index)) {
                            //超过限制
                            errorTextColor(mDscEdt);
                        } else {
                            restoreTextColor(mDscEdt);
                            //设置折扣率
                            mRateEdt.setText(String.valueOf(TradeHelper.getSingleRate(index, Double.parseDouble(s.toString()))));
                            //修改优惠后的价格
                            mTotalTv.setText(String.format("%.2f", TradeHelper.getSingleTotal(index, Double.parseDouble(mDscEdt.getText().toString()))));
                            if (Long.valueOf(mRateEdt.getText().toString()) > TradeHelper.getMaxSingleRate(index)) {
                                errorTextColor(mRateEdt);
                            } else {
                                restoreTextColor(mRateEdt);
                            }
                        }
                        break;
                    case DIALOG_WHOLE_RSC:
                        if (TradeHelper.getWholeForDscPrice() == 0) {
                            MessageUtil.show("当前无法优惠");
                            return;
                        }
                        if (Double.parseDouble(s.toString()) > TradeHelper.getMaxWholeDsc()) {
                            //超过限制
                            errorTextColor(mDscEdt);
                        } else {
                            restoreTextColor(mDscEdt);
                            DscHelper.wholeDscByTotal(Double.parseDouble(s.toString()));
                            mRateEdt.setText(String.valueOf(TradeHelper.getWholeRate(Double.parseDouble(s.toString()))));
                            mTotalTv.setText(String.format("%.2f", TradeHelper.getWholeTotal(Double.parseDouble(s.toString()))));
                        }
                        break;
                    default:
                        break;
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
                    if (mRateEdt.getText().toString().length() >= 3) {
                        MessageUtil.show("超出限制");
                        errorTextColor(mRateEdt);
                        return;
                    }
                    mRateEdt.setText(isFirst ? String.valueOf(key) : mRateEdt.getText().append(String.valueOf(key)));
                    isFirst = false;
                } else {
                    if (mDscEdt.getText().toString().contains(".")) {
                        int position = mDscEdt.getText().toString().indexOf(".");
                        if (mDscEdt.getText().toString().substring(0, position).length() > 6) {
                            MessageUtil.show("超出限制");
                            errorTextColor(mDscEdt);
                            return;
                        }
                        if (mDscEdt.getText().toString().substring(position, mDscEdt.getText().toString().length() - 1).length() >= 2) {
                            MessageUtil.show("超出限制");
                            errorTextColor(mDscEdt);
                            return;
                        }
                    } else {
                        if (mDscEdt.getText().toString().length() > 6) {
                            MessageUtil.show("超出限制");
                            errorTextColor(mDscEdt);
                            return;
                        }
                    }
                    mDscEdt.setText(mDscEdt.getText().append(String.valueOf(key)));
                }

                break;
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
                    if (!mDscEdt.getText().toString().contains(".")) {
                        mDscEdt.getText().append('.');
                    }
                }
                break;
            default:
                if (!mEdt.getText().toString().contains(".")) {
                    mEdt.getText().append('.');
                }
                break;
        }
    }

    @Override
    public void onHideClick(View v) {
        DscHelper.cancelWholeDsc();
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
                if (Integer.parseInt(mRateEdt.getText().toString()) <= TradeHelper.getMaxSingleRate(index) &&
                        Double.parseDouble(mDscEdt.getText().toString()) <= TradeHelper.getMaxSingleDsc(index)) {
                    //两个都为true的时候，才能保存成功
                    //初始化整单优惠列表
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
                //原价不为0，判断是否为空
                if (Integer.parseInt(mRateEdt.getText().toString()) <= TradeHelper.getMaxWholeRate() &&
                        Double.parseDouble(mDscEdt.getText().toString()) <= TradeHelper.getMaxWholeDsc()) {
                    if (TradeHelper.getWholeForDscPrice() == 0) {
                        MessageUtil.show("本笔交易已无可优惠商品");
                    } else {
                        //两个都为true的时候，才能保存成功
                        Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_COMMIT_WHOLE_DSC);
                        dismiss();
                    }
                } else {
                    MessageUtil.showError("失败");
                }
                break;
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

//                        MessageUtil.info("本单商品已享受其他优惠，如确定将取消先前优惠，是否继续？");
//                        MessageUtil.setMessageUtilClickListener(new MessageUtil.OnBtnClickListener() {
//                            @Override
//                            public void onLeftBtnClick(BasePopupView popView) {
//                                //两个都为true的时候，才能保存成功
//                                if (DscHelper.commitWholeDsc(Double.parseDouble(mDscEdt.getText().toString()))) {
//                                    Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_REFRESH_WHOLE_PRICE);
//                                    popView.dismiss();
//                                    dismiss();
//                                } else {
//                                    MessageUtil.showError("操作失败，请重试");
//                                }
//                            }
//
//                            @Override
//                            public void onRightBtnClick(BasePopupView popView) {
//                                popView.dismiss();
//                            }
//                        });