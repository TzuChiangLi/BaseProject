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
import com.ftrend.zgp.R;
import com.ftrend.zgp.utils.common.CommonUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.lxj.xpopup.core.BottomPopupView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 改价、输入手机号、单项优惠、整单优惠弹出窗口
 *
 * @author liziqiang@ftrend.cn
 */
public class PriceDscDialog extends BottomPopupView
        implements View.OnClickListener, KeyboardView.OnItemClickListener, View.OnFocusChangeListener {
    @BindView(R.id.vip_way_ll_info)
    LinearLayout mInfoLayout;
    @BindView(R.id.vip_way_edt)
    ClearEditText mEdt;
    @BindView(R.id.vip_dsc_tv_title)
    TextView mTitleTv;
    @BindView(R.id.vip_mobile_btn_submit)
    Button mSubmitBtn;
    @BindView(R.id.vip_way_img_close)
    ImageView mCloseImg;
    //优惠类别：    3-单项优惠
    private static final int DIALOG_SINGLE_RSC = 3;
    //优惠类别：    4-整单优惠
    private static final int DIALOG_WHOLE_RSC = 4;
    //优惠类别
    private int dscType;
    //数据更新：全部更新
    private final int UPDATE_DATA_ALL = 0;
    //数据更新：除折扣比例输入框以外全部更新
    private final int UPDATE_DATA_EXCEPT_RATE = 1;
    //数据更新：除优惠金额输入框以外全部更新
    private final int UPDATE_DATA_EXCEPT_MONEY = 2;
    private KeyboardView mKeyView;
    private View mKeyViewStub, mRateDscView;
    private EditText mRateEdt, mDscEdt;
    private TextView mPriceTv, mDscTv, mTotalTv, mProdNameTv, mAmountTv, mMaxRateTv, mMaxDscTv;
    private TextView mPriceTitle, mDscTitle, mTotalTitle, mRateMemo, mDscMemo;
    private LinearLayout mLlRate, mLlDsc;
    //优惠计算参数
    private DscData dscData = null;
    //优惠计算回调
    private DscInputCallback callback = null;

    /**
     * 单项优惠输入框
     *
     * @param context
     * @return
     */
    public static PriceDscDialog singleDscInput(@NonNull Context context, @NonNull DscData data,
                                                @NonNull DscInputCallback callback) {
        PriceDscDialog dlg = new PriceDscDialog(context, DIALOG_SINGLE_RSC, callback);
        dlg.dscData = data;
        return dlg;
    }

    /**
     * 整单优惠输入框
     *
     * @param context
     * @return
     */
    public static PriceDscDialog wholeDscInput(@NonNull Context context, @NonNull DscData data,
                                               @NonNull DscInputCallback callback) {
        PriceDscDialog dlg = new PriceDscDialog(context, DIALOG_WHOLE_RSC, callback);
        dlg.dscData = data;
        return dlg;
    }

    public PriceDscDialog(@NonNull Context context, int dscType, @NonNull DscInputCallback callback) {
        super(context);
        this.dscType = dscType;
        this.callback = callback;
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
        initDscView();
        updateDscData(UPDATE_DATA_ALL);
        mRateEdt.setText(String.valueOf(dscData.getDscRate()));
//        mRateEdt.selectAll();
    }

    /**
     * 初始化界面
     */
    private void initDscView() {
        //懒加载
        mKeyViewStub = ((ViewStub) findViewById(R.id.vip_way_key_func_view)).inflate();
        mKeyView = mKeyViewStub.findViewById(R.id.vip_way_keyboard);
        mRateDscView = ((ViewStub) findViewById(R.id.vip_dsc_rate_view)).inflate();
        //注册控件
        mDscEdt = mRateDscView.findViewById(R.id.vip_dsc_edt_dsc);
        mRateEdt = mRateDscView.findViewById(R.id.vip_dsc_edt_rate);
        mDscMemo = mRateDscView.findViewById(R.id.vip_dsc_tv_dsc_memo);
        mRateMemo = mRateDscView.findViewById(R.id.vip_dsc_tv_rate_memo);
        mProdNameTv = mRateDscView.findViewById(R.id.vip_dsc_tv_prodname);
        mAmountTv = mRateDscView.findViewById(R.id.vip_dsc_tv_amount);
        mPriceTv = mRateDscView.findViewById(R.id.vip_dsc_tv_price);
        mDscTv = mRateDscView.findViewById(R.id.vip_dsc_tv_dsc);
        mTotalTv = mRateDscView.findViewById(R.id.vip_dsc_tv_total);
        mPriceTitle = mRateDscView.findViewById(R.id.vip_dsc_tv_price_title);
        mDscTitle = mRateDscView.findViewById(R.id.vip_dsc_tv_dsc_title);
        mTotalTitle = mRateDscView.findViewById(R.id.vip_dsc_tv_total_title);
        mMaxDscTv = mRateDscView.findViewById(R.id.vip_dsc_tv_dsc_range);
        mMaxRateTv = mRateDscView.findViewById(R.id.vip_dsc_tv_rate_range);
        //
        mLlRate = mRateDscView.findViewById(R.id.vip_dsc_ll_rate);
        mLlDsc = mRateDscView.findViewById(R.id.vip_dsc_ll_dsc);
        //防止键盘弹出
        mRateEdt.setInputType(InputType.TYPE_NULL);
        mDscEdt.setInputType(InputType.TYPE_NULL);
        //展示自家小键盘
        mKeyView.show();
        //初始化其他界面信息
        mEdt.setVisibility(GONE);
        mSubmitBtn.setVisibility(GONE);
        mKeyView.setOnKeyboardClickListener(this);
//        mRateEdt.selectAll();
        mRateEdt.setOnFocusChangeListener(this);
        if (dscType == DIALOG_WHOLE_RSC) {
            mTitleTv.setText("整单优惠");
            mProdNameTv.setVisibility(GONE);
            mAmountTv.setVisibility(GONE);
            mPriceTitle.setText("总价：");
            mDscTitle.setText("已优惠：");
            mTotalTitle.setText("实收：");
        } else if (dscType == DIALOG_SINGLE_RSC) {
            mTitleTv.setText("单项优惠");
            mPriceTitle.setText("原价：");
            mDscTitle.setText("优惠价：");
            mTotalTitle.setText("小计：");
        }
    }

    /**
     * 更新折扣优惠数据面板
     */
    private void updateDscData(int type) {
//        mMaxRateTv.setText(String.format(Locale.CHINA, "(0-%d%%)", dscData.getDscRateMax()));
//        mMaxDscTv.setText(String.format(Locale.CHINA, "(0-%s元)", CommonUtil.moneyToString(dscData.getDscMoneyMax())));
        mMaxRateTv.setText(String.format(Locale.CHINA, "(%d%%)", dscData.getDscRateMax()));
        mMaxDscTv.setText(String.format(Locale.CHINA, "%s元", CommonUtil.moneyToString(dscData.getDscMoneyMax())));
        if (dscType == DIALOG_SINGLE_RSC) {
            mPriceTv.setText(CommonUtil.moneyToString(dscData.getPrice()));//原价
            double dscPrice = (dscData.getTotal() - dscData.getDscMoney()) / dscData.getAmount();
            mDscTv.setText(String.format(Locale.CHINA, "%s(-%s)",
                    CommonUtil.moneyToString(dscPrice),
                    CommonUtil.moneyToString(dscData.getPrice() - dscPrice)));//优惠价
            mTotalTv.setText(CommonUtil.moneyToString(dscData.getTotal() - dscData.getDscMoney()));//小计
            mDscMemo.setText(String.format(Locale.CHINA, "(-%d%%)", dscData.getDscRate()));
            mRateMemo.setText(String.format(Locale.CHINA, "(-%s元)", CommonUtil.moneyToString(dscData.getDscMoney())));
        } else if (dscType == DIALOG_WHOLE_RSC) {
            mPriceTv.setText(CommonUtil.moneyToString(dscData.getTotal()));//原价
            mDscTv.setText(CommonUtil.moneyToString(dscData.getDscMoney()));//优惠金额
            mTotalTv.setText(CommonUtil.moneyToString(dscData.getTotal() - dscData.getDscMoney()));//实收
        }

        mProdNameTv.setText(dscData.getProdName());
        mAmountTv.setText(String.valueOf(Math.round(dscData.getAmount())));
        if (type != UPDATE_DATA_EXCEPT_RATE) {
            mRateEdt.setText(String.valueOf(dscData.getDscRate()));
        }
        if (type != UPDATE_DATA_EXCEPT_MONEY) {
            mDscEdt.setText(CommonUtil.moneyToString(dscData.getDscMoney()));
        }
        if (dscData.getDscRate() > dscData.getDscRateMax()) {
            errorTextColor(mRateEdt);
        } else {
            restoreTextColor(mRateEdt);
        }
        if (dscData.getDscMoney() > dscData.getDscMoneyMax()) {
            errorTextColor(mDscEdt);
        } else {
            restoreTextColor(mDscEdt);
        }
    }

    @OnClick(R.id.vip_way_img_close)
    public void close() {
        if (callback != null) {
            callback.onCancel();
        }
        dismiss();
    }


    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
    }

    //-------------------------------------输入实时监听--------------------------------------------//
    private TextWatcher rateWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (callback != null) {
                try {
                    int rate = Integer.valueOf(s.toString());
                    if (rate > dscData.getDscRateMax()) {
                        //超过限制
                        errorTextColor(mRateEdt);
                    } else {
                        restoreTextColor(mRateEdt);
                        dscData.setDscRate(rate);
                        dscData.setDscMoney(callback.onDscByRate(rate));
                        updateDscData(UPDATE_DATA_EXCEPT_RATE);
                    }
                } catch (Exception e) {
                    mRateEdt.setText("0");
                }
            }
        }
    };
    private TextWatcher dscWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (callback != null) {
                try {
                    double money = Double.valueOf(s.toString());
                    if (money > dscData.getDscMoneyMax()) {
                        //超过限制
                        errorTextColor(mDscEdt);
                    } else {
                        restoreTextColor(mDscEdt);
                        dscData.setDscMoney(callback.onDscByTotal(money));
                        dscData.setDscRate((int) Math.round(dscData.getDscMoney() * 100 / dscData.getTotal()));
                        updateDscData(UPDATE_DATA_EXCEPT_MONEY);
                    }
                } catch (Exception e) {
                    mDscEdt.setText("0");
                }
            }
        }
    };

    //-------------------------------------键盘响应--------------------------------------------//
    @Override
    public void onKeyClick(View v, int key) {
        if (mRateEdt.hasFocus()) {
            String val = mRateEdt.getText().toString();
            if (TextUtils.isEmpty(val) || val.equals("0")) {
                val = "";
            }
            if (val.length() >= 3) {
                //折扣比例最长3位
                return;
            }
            mRateEdt.setText(val + key);
        } else {
            String val = mDscEdt.getText().toString();
            if (TextUtils.isEmpty(val) || val.equals("0") || val.equals("0.00")) {
                val = "";
            }
            if (val.contains(".")) {
                int position = val.indexOf(".");
                if (val.substring(position, val.length() - 1).length() >= 2) {
                    //优惠金额小数点后最长2位
                    return;
                }
            } else {
                if (val.length() >= 6) {
                    //优惠金额小数点前最长6位
                    return;
                }
            }
            mDscEdt.setText(val + key);
        }
    }

    @Override
    public void onDeleteClick() {
        if (mRateEdt.hasFocus()) {
            String val = mRateEdt.getText().toString();
            if (!TextUtils.isEmpty(val)) {
                val = val.substring(0, val.length() - 1);
            }
            if (TextUtils.isEmpty(val)) {
                val = "0";
            }
            mRateEdt.setText(val);
        } else {
            String val = mDscEdt.getText().toString();
            if (!TextUtils.isEmpty(val)) {
                val = val.substring(0, val.length() - 1);
            }
            if (TextUtils.isEmpty(val)) {
                val = "0";
            }
            mDscEdt.setText(val);
        }
    }

    @Override
    public void onPointClick() {
        if (mDscEdt.hasFocus()) {
            if (!mDscEdt.getText().toString().contains(".")) {
                mDscEdt.getText().append('.');
            }
        }
        //折扣比例不允许输入小数点
    }

    @Override
    public void onHideClick(View v) {
        if (callback != null) {
            callback.onCancel();
        }
        dismiss();
    }

    @Override
    public void onNextClick() {
        if (mRateEdt.hasFocus()) {
            mLlRate.setVisibility(INVISIBLE);
            mLlDsc.setVisibility(VISIBLE);
            mDscEdt.requestFocus();
//                    mDscEdt.selectAll();
        } else {
            mLlRate.setVisibility(VISIBLE);
            mLlDsc.setVisibility(INVISIBLE);
            mRateEdt.requestFocus();
//                    mRateEdt.selectAll();
        }
    }

    @Override
    public void onClearClick() {
        if (mRateEdt.hasFocus()) {
            mRateEdt.setText("0");
        } else {
            mDscEdt.setText("0");
        }
    }

    @Override
    public void onCancelClick() {
        if (callback != null) {
            callback.onCancel();
        }
        dismiss();
    }

    @Override
    public void onEnterClick() {
        int rate = Integer.parseInt(mRateEdt.getText().toString());
        double money = Double.parseDouble(mDscEdt.getText().toString());
        if (callback != null) {
            if (rate > dscData.getDscRateMax() || money > dscData.getDscMoneyMax()) {
                MessageUtil.showError("超出最大可优惠范围");
                return;
            }
            dscData.setDscMoney(money);
            dscData.setDscRate(rate);
            if (callback.onOk(rate, money)) {
                dismiss();
            }
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
//        int colorActive = getResources().getColor(R.color.white_transparent);
//        int colorInactive = getResources().getColor(R.color.common_gray_bg);
        if (hasFocus) {
//            mRateEdt.selectAll();
            mDscEdt.removeTextChangedListener(dscWatcher);
            mRateEdt.addTextChangedListener(rateWatcher);
//            mLlRate.setBackgroundColor(colorActive);
//            mLlDsc.setBackgroundColor(colorInactive);
        } else {
//            mDscEdt.selectAll();
            mRateEdt.removeTextChangedListener(rateWatcher);
            mDscEdt.addTextChangedListener(dscWatcher);
//            mLlRate.setBackgroundColor(colorInactive);
//            mLlDsc.setBackgroundColor(colorActive);
        }
    }

    private void errorTextColor(EditText edt) {
        edt.setTextColor(Color.RED);
    }

    private void restoreTextColor(EditText edt) {
        edt.setTextColor(Color.BLACK);
    }

}
