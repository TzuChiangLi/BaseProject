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
import com.ftrend.zgp.utils.log.LogUtil;
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

    //region 变量定义
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

    private KeyboardView mKeyView;
    private View mKeyViewStub, mRateDscView;

    //优惠计算参数
    private DscData dscData = null;
    //优惠计算回调
    private DscInputCallback callback = null;
    //输入模式
    private DscInputMode inputMode;

    //输入控件
    private TextView mEdtLabel;
    private EditText mEdtBox;
    private TextView mEdtUnit;

    //商品信息
    private TextView mProdNameTv, mUnitTv, mAmountTv;
    //优惠上限
    private TextView mMaxRateTv, mMaxDscTv;
    //列标题
    private TextView mPriceHeader, mTotalHeader, mRateHeader;
    //原价
    private TextView mPriceOri, mTotalOri, mRateOri;
    //原有优惠
    private TextView mPriceBefore, mTotalBefore, mRateBefore;
    //当前输入的优惠
    private TextView mPriceAfter, mTotalAfter, mRateAfter;

    //endregion

    /**
     * 单项优惠输入框
     *
     * @param context
     * @return
     */
    public static PriceDscDialog singleDscInput(@NonNull Context context, @NonNull DscData data,
                                                @NonNull DscInputCallback callback) {
        PriceDscDialog dlg = new PriceDscDialog(context, callback);
        dlg.dscData = data;
        dlg.inputMode = DscInputMode.singleByTotal;
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
        PriceDscDialog dlg = new PriceDscDialog(context, callback);
        dlg.dscData = data;
        dlg.inputMode = DscInputMode.wholeByTotal;
        return dlg;
    }

    public PriceDscDialog(@NonNull Context context, @NonNull DscInputCallback callback) {
        super(context);
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
        updateDscData();
    }

    /**
     * 初始化界面
     */
    private void initDscView() {
        //懒加载
        mKeyViewStub = ((ViewStub) findViewById(R.id.vip_way_key_func_view)).inflate();
        mKeyView = mKeyViewStub.findViewById(R.id.vip_way_keyboard);
        mRateDscView = ((ViewStub) findViewById(R.id.vip_dsc_rate_view)).inflate();

        //显示控件
        mProdNameTv = mRateDscView.findViewById(R.id.vip_dsc_tv_prodname);
        mUnitTv = mRateDscView.findViewById(R.id.vip_dsc_tv_unit);
        mAmountTv = mRateDscView.findViewById(R.id.vip_dsc_tv_amount);
        mMaxDscTv = mRateDscView.findViewById(R.id.vip_dsc_tv_dsc_range);
        mMaxRateTv = mRateDscView.findViewById(R.id.vip_dsc_tv_rate_range);

        mPriceHeader = mRateDscView.findViewById(R.id.vip_dsc_grid_price_header);
        mTotalHeader = mRateDscView.findViewById(R.id.vip_dsc_grid_total_header);
        mRateHeader = mRateDscView.findViewById(R.id.vip_dsc_grid_rate_header);

        mPriceOri = mRateDscView.findViewById(R.id.vip_dsc_grid_price_ori);
        mTotalOri = mRateDscView.findViewById(R.id.vip_dsc_grid_total_ori);
        mRateOri = mRateDscView.findViewById(R.id.vip_dsc_grid_rate_ori);

        mPriceBefore = mRateDscView.findViewById(R.id.vip_dsc_grid_price_before);
        mTotalBefore = mRateDscView.findViewById(R.id.vip_dsc_grid_total_before);
        mRateBefore = mRateDscView.findViewById(R.id.vip_dsc_grid_rate_before);

        mPriceAfter = mRateDscView.findViewById(R.id.vip_dsc_grid_price_after);
        mTotalAfter = mRateDscView.findViewById(R.id.vip_dsc_grid_total_after);
        mRateAfter = mRateDscView.findViewById(R.id.vip_dsc_grid_rate_after);

        //输入控件
        mEdtLabel = mRateDscView.findViewById(R.id.vip_dsc_edt_dsc_title);
        mEdtBox = mRateDscView.findViewById(R.id.vip_dsc_edt_dsc);
        mEdtUnit = mRateDscView.findViewById(R.id.vip_dsc_tv_dsc_unit);

        //防止键盘弹出
        mEdtBox.setInputType(InputType.TYPE_NULL);
        //展示自家小键盘
        mKeyView.show();
        //初始化其他界面信息
        mEdt.setVisibility(GONE);
        mSubmitBtn.setVisibility(GONE);
        mKeyView.setOnKeyboardClickListener(this);

        if (isSingleMode()) {
            mTitleTv.setText("单项优惠");
        } else if (isWholeMode()) {
            mTitleTv.setText("整单优惠");
            mProdNameTv.setVisibility(GONE);
            mUnitTv.setVisibility(GONE);
            mAmountTv.setVisibility(GONE);
        }
        mEdtBox.addTextChangedListener(inputWatcher);
        switchInputMode(inputMode);
    }

    /**
     * 更新折扣优惠数据面板
     */
    private void updateDscData() {
        mProdNameTv.setText(dscData.getProdName());
        mUnitTv.setText(dscData.getUnit());
        mAmountTv.setText(String.valueOf(Math.round(dscData.getAmount())));

        mPriceHeader.setText(isSingleMode() ? "单价" : "优惠金额");
        mTotalHeader.setText(isSingleMode() ? "小计" : "应收金额");
        mRateHeader.setText(isSingleMode() ? "折扣" : "折扣比例");

        if (isSingleMode()) {
            mPriceOri.setText(CommonUtil.moneyToString(dscData.getPrice()));
            mTotalOri.setText(CommonUtil.moneyToString(dscData.getTotal()));
            mRateOri.setText("0%");

            mPriceBefore.setText(DscData.formatPrice(dscData.getPriceBefore(), dscData.getPrice()));
            mPriceAfter.setText(DscData.formatPrice(dscData.getPriceAfter(), dscData.getPrice()));
        } else {
            mPriceOri.setText("0");
            mTotalOri.setText(CommonUtil.moneyToString(dscData.getTotal()));
            mRateOri.setText("0%");

            mPriceBefore.setText(DscData.formatDsc(dscData.getDscMoneyBefore(), dscData.getDscOtherBefore()));
            mPriceAfter.setText(DscData.formatDsc(dscData.getDscMoneyAfter(), dscData.getDscOtherAfter()));
        }

        mTotalBefore.setText(DscData.formatPrice(dscData.getTotalBefore(), dscData.getTotal()));
        mRateBefore.setText(DscData.formatRate(dscData.getDscRateBefore()));
        mTotalAfter.setText(DscData.formatPrice(dscData.getTotalAfter(), dscData.getTotal()));
        mRateAfter.setText(DscData.formatRate(dscData.getDscRateAfter()));

        if (dscData.getDscRateAfter() > dscData.getDscRateMax()
                || dscData.getDscMoneyAfter() > dscData.getDscMoneyMax()) {
            errorTextColor(mPriceAfter);
            errorTextColor(mRateAfter);
            errorTextColor(mTotalAfter);
            errorTextColor(mEdtBox);
        } else {
            restoreTextColor(mPriceAfter);
            restoreTextColor(mRateAfter);
            restoreTextColor(mTotalAfter);
            restoreTextColor(mEdtBox);
        }
    }

    /**
     * 切换优惠输入模式
     *
     * @param mode
     */
    private void switchInputMode(DscInputMode mode) {
        inputMode = mode;
        //清除之前的计算结果
        dscData.reset();
        //更新已输入金额或比例
        dscData.reset();
        switch (inputMode) {
            case singleByRate:
            case wholeByRate:
                mEdtLabel.setText("折扣比例：");
                //显示已优惠比例
                mEdtBox.setText(String.valueOf(dscData.getDscRateAfter()));
                break;
            case singleByPrice:
                mEdtLabel.setText("单价优惠：");
                //显示单价已优惠金额
                mEdtBox.setText(CommonUtil.moneyToString(dscData.getDscMoneyAfterByPrice()));
                break;
            case singleByTotal:
                mEdtLabel.setText("总价优惠：");
                //显示总价已优惠金额
                mEdtBox.setText(CommonUtil.moneyToString(dscData.getDscMoneyAfter()));
                break;
            case wholeByTotal:
                mEdtLabel.setText("优惠金额：");
                //显示已优惠金额
                mEdtBox.setText(CommonUtil.moneyToString(dscData.getDscMoneyAfter()));
                break;
            default:
                break;
        }
        mEdtUnit.setText(isRateMode() ? "%" : "元");
        //更新优惠范围
        updateDscLimit();
    }

    private void updateDscLimit() {
        //单价优惠时，显示单价的优惠范围；其他显示总价优惠范围
        if (inputMode == DscInputMode.singleByPrice) {
            mMaxRateTv.setText(String.format(Locale.CHINA, "(%d%%)", dscData.getDscRateMax()));
            mMaxDscTv.setText(String.format(Locale.CHINA, "%s元", CommonUtil.moneyToString(dscData.getDscMoneyMaxByPrice())));
        } else {
            mMaxRateTv.setText(String.format(Locale.CHINA, "(%d%%)", dscData.getDscRateMax()));
            mMaxDscTv.setText(String.format(Locale.CHINA, "%s元", CommonUtil.moneyToString(dscData.getDscMoneyMax())));
        }
    }

    private void errorTextColor(EditText editText) {
        editText.setTextColor(Color.RED);
    }

    private void restoreTextColor(EditText editText) {
        editText.setTextColor(Color.BLACK);
    }

    private void errorTextColor(TextView textView) {
        textView.setTextColor(Color.RED);
    }

    private void restoreTextColor(TextView textView) {
        textView.setTextColor(Color.BLACK);
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

    //region 输入实时监听

    private TextWatcher inputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!calcDsc(s.toString())) {
                mEdtBox.setText("0");
            } else {
                updateDscData();
            }
        }

        private boolean calcDsc(String val) {
            try {
                double value = Double.valueOf(val);
                restoreTextColor(mEdtBox);
                if (inputMode == DscInputMode.singleByPrice) {
                    callback.onDscByTotal(value * dscData.getAmount());
//                    dscData.setDscMoney(callback.onDscByTotal(value * dscData.getAmount()));
                } else if (isRateMode()) {
                    callback.onDscByRate(value);
//                    dscData.setDscMoney(callback.onDscByRate(value));
                } else {
                    callback.onDscByTotal(value);
//                    dscData.setDscMoney(callback.onDscByTotal(value));
                }
//                dscData.setDscRate((int) Math.round(dscData.getDscMoney() * 100 / dscData.getOriTotal()));
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    };

    //endregion

    //region 键盘响应
    @Override
    public void onKeyClick(View v, int key) {
        String val = mEdtBox.getText().toString();
        if (isRateMode()) {
            if (TextUtils.isEmpty(val) || val.equals("0")) {
                val = "";
            }
            if (val.length() >= 3) {
                //折扣比例最长3位
                return;
            }
        } else {
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
        }
        mEdtBox.setText(val + key);
    }

    @Override
    public void onDeleteClick() {
        String val = mEdtBox.getText().toString();
        if (!TextUtils.isEmpty(val)) {
            val = val.substring(0, val.length() - 1);
        }
        if (TextUtils.isEmpty(val)) {
            val = "0";
        }
        mEdtBox.setText(val);
    }

    @Override
    public void onPointClick() {
        //折扣比例不允许输入小数点
        if (!isRateMode()) {
            if (!mEdtBox.getText().toString().contains(".")) {
                mEdtBox.getText().append('.');
            }
        }
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
        switch (inputMode) {
            case singleByTotal:
                switchInputMode(DscInputMode.singleByPrice);
                break;
            case singleByPrice:
                switchInputMode(DscInputMode.singleByRate);
                break;
            case singleByRate:
                switchInputMode(DscInputMode.singleByTotal);
                break;
            case wholeByTotal:
                switchInputMode(DscInputMode.wholeByRate);
                break;
            case wholeByRate:
                switchInputMode(DscInputMode.wholeByTotal);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClearClick() {
        mEdtBox.setText("0");
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
        if (!dscData.isValid()) {
            MessageUtil.showError("超出最大可优惠范围");
            return;
        }
        if (callback.onOk(dscData.getDscRateAfter(), dscData.getDscMoneyAfter())) {
            dismiss();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
    }

    //endregion

    /**
     * 当前是否单项优惠输入模式
     *
     * @return
     */
    private boolean isSingleMode() {
        return inputMode == DscInputMode.singleByPrice
                || inputMode == DscInputMode.singleByRate
                || inputMode == DscInputMode.singleByTotal;
    }

    /**
     * 当前是否整单优惠输入模式
     *
     * @return
     */
    private boolean isWholeMode() {
        return inputMode == DscInputMode.wholeByRate
                || inputMode == DscInputMode.wholeByTotal;
    }

    /**
     * 当前时候折扣比例输入模式
     *
     * @return
     */
    private boolean isRateMode() {
        return inputMode == DscInputMode.singleByRate
                || inputMode == DscInputMode.wholeByRate;
    }

    /**
     * 输入模式定义
     */
    enum DscInputMode {
        singleByPrice,
        singleByTotal,
        singleByRate,
        wholeByTotal,
        wholeByRate
    }
}
