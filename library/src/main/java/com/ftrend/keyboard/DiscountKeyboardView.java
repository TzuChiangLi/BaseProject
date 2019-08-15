package com.ftrend.keyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

/**
 * @author LZQ
 * @content 折扣小键盘view 用法：在布局文件或者代码中添加KeyboardView。注册捆绑后添加监听，调用show和dismiss即可。
 */
public class DiscountKeyboardView extends KeyboardView {


    public DiscountKeyboardView(Context context) {
        super(context);
    }

    public DiscountKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiscountKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 添加折扣区域
     */
    @Override
    public void addDiscountView() {
        super.addDiscountView();
        RelativeLayout mRelativeLayout = new RelativeLayout(mContext);
        RadioGroup mRadioGroup = new RadioGroup(mContext);
        RadioButton mDiscountRBtn = new RadioButton(mContext);
        RadioButton mMoneyRBtn = new RadioButton(mContext);
        mDiscountRBtn.setText("折扣优惠");
        mMoneyRBtn.setText("现金优惠");
        mRadioGroup.addView(mDiscountRBtn);
        mRadioGroup.addView(mMoneyRBtn);
        mRelativeLayout.addView(mRadioGroup);

    }
}
