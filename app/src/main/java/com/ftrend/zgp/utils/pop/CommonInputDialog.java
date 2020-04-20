package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.keyboard.KeyboardView;
import com.ftrend.log.LogUtil;
import com.ftrend.zgp.R;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.lxj.xpopup.core.BottomPopupView;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 通用输入界面
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/21
 */
public class CommonInputDialog extends BottomPopupView implements View.OnClickListener, KeyboardView.OnItemClickListener {
    @BindView(R.id.vip_way_edt)
    ClearEditText mEdt;
    @BindView(R.id.vip_dsc_tv_title)
    TextView mTitleTv;
    @BindView(R.id.vip_mobile_btn_submit)
    Button mSubmitBtn;
    @BindView(R.id.vip_ll_scan)
    LinearLayout mScanLayout;
    @BindDrawable(R.drawable.vip_query_btn_selector)
    Drawable vip_query_blue;
    @BindDrawable(R.drawable.pay_dialog_btn_selector)
    Drawable pay_red_selector;
    private KeyboardView mKeyView;
    private View mKeyViewStub;
    // 输入提示信息
    private String mTitle;
    // 按钮文本
    private String mBtnText;
    // 输入类型
    private ValueType mValueType;
    // 回调
    private MoneyInputCallback mMoneyCallback = null;
    private StringInputCallback mStringCallback = null;
    private ServerUrlInputCallback mServerCallback = null;
    //显示扫码
    private boolean mScan = false;
    private final String SCAN = "SCAN";
    // 输入长度限制
    private int mMaxLength;
    // 是否允许输入小数点
    private boolean mEnableDot = true;
    // 默认值
    private double mDefMoney = 0;
    private String mDefString = "";

    public CommonInputDialog(@NonNull Context context, String title, String action, double def,
                             MoneyInputCallback callback) {
        super(context);
        this.mTitle = title;
        this.mBtnText = action;
        this.mMoneyCallback = callback;
        this.mValueType = ValueType.MONEY;
        this.mMaxLength = 9;
        this.mDefMoney = def;
    }

    /**
     * @param context   上下文
     * @param title     标题
     * @param action    按钮文本
     * @param def       默认值
     * @param maxLength 最大长度
     * @param enableDot 允许小数点
     * @param callback  回调
     */
    public CommonInputDialog(@NonNull Context context, String title, String action, String def,
                             int maxLength, boolean enableDot, StringInputCallback callback) {
        super(context);
        this.mTitle = title;
        this.mBtnText = action;
        this.mStringCallback = callback;
        this.mValueType = ValueType.STRING;
        this.mMaxLength = maxLength;
        this.mEnableDot = enableDot;
        this.mDefString = def;
    }

    public CommonInputDialog(@NonNull Context context, String title, String action, String def,
                             int maxLength, boolean enableDot, boolean scan, StringInputCallback callback) {
        super(context);
        this.mTitle = title;
        this.mBtnText = action;
        this.mStringCallback = callback;
        this.mValueType = ValueType.STRING;
        this.mMaxLength = maxLength;
        this.mEnableDot = enableDot;
        this.mDefString = def;
        this.mScan = scan;
    }

    /**
     * 输入服务器
     *
     * @param context  上下文
     * @param title    标题
     * @param action   按钮文本
     * @param def      默认值
     * @param callback 回调
     */
    public CommonInputDialog(@NonNull Context context, String title, String action, String def, ServerUrlInputCallback callback) {
        super(context);
        this.mTitle = title;
        this.mBtnText = action;
        this.mValueType = ValueType.SERVER;
        this.mDefString = def;
        this.mServerCallback = callback;
    }

    /**
     * 输入称重数量，限制整数两位，小数三位
     *
     * @param context       上下文
     * @param title         标题
     * @param action        按钮文本
     * @param enableDot     输入小数点
     * @param moneyCallback 回调
     */
    public CommonInputDialog(@NonNull Context context, String title, String action,
                             boolean enableDot, MoneyInputCallback moneyCallback) {
        super(context);
        this.mTitle = title;
        this.mBtnText = action;
        this.mValueType = ValueType.QUANTITY;
        this.mMaxLength = 8;
        this.mEnableDot = enableDot;
        this.mMoneyCallback = moneyCallback;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.vip_dsc_mobile_dialog;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        if (mValueType != ValueType.SERVER) {
            mKeyViewStub = ((ViewStub) findViewById(mEnableDot?R.id.vip_way_key_lite_view_endot:R.id.vip_way_key_lite_view_disdot)).inflate();
            mKeyView = mKeyViewStub.findViewById(R.id.vip_way_keyboard);
            mKeyView.show();
            mKeyView.setOnKeyboardClickListener(this);
            KeyboardUtils.hideSoftInput(this);
        }
        if (mScan) {
            mScanLayout.setVisibility(VISIBLE);
            mScanLayout.setOnClickListener(this);
        }
        mTitleTv.setText(mTitle);
        mSubmitBtn.setText(mBtnText);
        mEdt.setInputType(InputType.TYPE_NULL);
        mEdt.setOnClickListener(this);
        mSubmitBtn.setBackground(pay_red_selector);//vip_query_blue
        switch (mValueType) {
            case STRING:
                mEdt.setText(mDefString);
                break;
            case MONEY:
                mEdt.setText(mDefMoney > 0 ? String.valueOf(mDefMoney) : "");
                break;
            case SERVER:
                mEdt.setText(mDefString);
                mEdt.selectAll();
            default:
                break;
        }
    }

    @OnClick(R.id.vip_mobile_btn_submit)
    public void submit() {
        switch (mValueType) {
            case MONEY:
            case QUANTITY:
                if (mMoneyCallback != null && !TextUtils.isEmpty(mEdt.getText().toString())) {
                    double d = Double.parseDouble(mEdt.getText().toString());
                    String msg = mMoneyCallback.validate(d);
                    if (TextUtils.isEmpty(msg)) {
                        mMoneyCallback.onOk(d);
                        mMoneyCallback = null;
                        dismiss();
                    } else {
                        MessageUtil.show(msg);
                    }
                }
                break;
            case STRING:
                if (mStringCallback != null) {
                    String s = mEdt.getText().toString();
                    String msg = mStringCallback.validate(s);
                    if (TextUtils.isEmpty(msg)) {
                        mStringCallback.onOk(s);
                        mStringCallback = null;
                        dismiss();
                    } else {
                        MessageUtil.show(msg);
                    }
                }
                break;
            case SERVER:
                if (mServerCallback != null) {
                    String s = mEdt.getText().toString();
                    String msg = mServerCallback.validate(s);
                    if (TextUtils.isEmpty(msg)) {
                        mServerCallback.onOk(s);
                        mServerCallback = null;
                        dismiss();
                    } else {
                        MessageUtil.show(msg);
                    }
                }
                break;
            default:
                dismiss();
                break;
        }
    }

    @OnClick(R.id.vip_way_img_close)
    public void close() {
        KeyboardUtils.hideSoftInput(this);
        if (mMoneyCallback != null) {
            mMoneyCallback.onCancel();
        }
        if (mStringCallback != null) {
            mStringCallback.onCancel();
        }
        if (mServerCallback != null) {
            mServerCallback.onCancel();
        }
        dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vip_ll_scan:
                dismiss();
                mStringCallback.validate(SCAN);
                break;
            default:
                break;
        }
    }

    /**
     * 输入数据类型定义
     */
    private enum ValueType {
        /**
         * 金额
         */
        MONEY,
        /**
         * 字符串
         */
        STRING,
        /**
         * 输入服务地址
         */
        SERVER,
        /**
         * 商品数量 例：10.123 kg，主要用于称重
         */
        QUANTITY
    }

    //-------------------------------------键盘响应--------------------------------------------//
    @Override
    public void onKeyClick(View v, int key) {
        appendText(String.valueOf(key));
    }

    @Override
    public void onDeleteClick() {
        if (mEdt.getText() == null) {
            return;
        }
        String text = mEdt.getText().toString();
        mEdt.setText(TextUtils.isEmpty(text) ? "" : text.substring(0, text.length() - 1));
    }

    @Override
    public void onPointClick() {
        if (!mEnableDot) {
            return;
        }
        switch (mValueType) {
            case STRING:
                appendText(".");
                break;
            case MONEY:
                if (mEdt.getText() == null) {
                    appendText("0.");
                } else if (!mEdt.getText().toString().contains(".")) {
                    appendText(".");
                }
                break;
            case QUANTITY:
                if (mEdt.getText() == null) {
                    appendText("0.");
                } else if (!mEdt.getText().toString().contains(".")) {
                    appendText(".");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 往输入框中追加文字
     *
     * @param s
     */
    private void appendText(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        if (!mEnableDot && s.contains(".")) {
            return;
        }
        if (mEdt.getText() == null) {
            mEdt.setText(s);
            return;
        }
        String text = mEdt.getText().toString();
        if (text.length() >= mMaxLength) {
            return;
        }
        if (mValueType == ValueType.MONEY) {
            if ("0".equals(text)) {
                text = "";
            }
            if (text.contains(".")) {
                int position = text.indexOf(".");
                // 小数位数不超过2位
                if (text.length() - position >= 3) {
                    return;
                }
            } else if (text.length() >= mMaxLength - 3 && !".".equals(s)) {
                return;
            }
        }
        if (mValueType==ValueType.QUANTITY){
            if ("0".equals(text)) {
                text = "";
            }
            if (text.contains(".")) {
                int position = text.indexOf(".");
                // 小数位数不超过3位
                if (text.length() - position >= 4) {
                    return;
                }
            } else if (text.length() >= mMaxLength - 4 && !".".equals(s)) {
                //整数位最长4位
                return;
            }
        }
        mEdt.setText(text + s);
    }

    @Override
    public void onHideClick(View v) {
        dismiss();
    }

    @Override
    public void onNextClick() {
    }

    @Override
    public void onClearClick() {
        mEdt.setText("");
    }

    @Override
    public void onCancelClick() {
        dismiss();
    }

    @Override
    public void onEnterClick() {
    }

}
