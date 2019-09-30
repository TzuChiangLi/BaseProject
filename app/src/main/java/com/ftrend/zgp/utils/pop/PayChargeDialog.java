package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.keyboard.KeyboardView;
import com.ftrend.zgp.R;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.event.Event;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.lxj.xpopup.core.BottomPopupView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 改价、输入手机号、单项优惠、整单优惠弹出窗口
 *
 * @author liziqiang@ftrend.cn
 */
public class PayChargeDialog extends BottomPopupView implements KeyboardView.OnItemClickListener {
    @BindView(R.id.pay_charge_edt)
    ClearEditText mEdt;
    @BindView(R.id.pay_charge_img_close)
    ImageView mCloseImg;
    @BindView(R.id.pay_charge_tv_charge)
    TextView mChargeTv;
    @BindView(R.id.pay_charge_btn_submit)
    Button mPayBtn;
    private View mKeyViewStub;
    private KeyboardView mKeyView;


    public PayChargeDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pay_charge_dialog;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        KeyboardUtils.hideSoftInput(this);
        mEdt.setInputType(InputType.TYPE_NULL);
        mKeyViewStub = ((ViewStub) findViewById(R.id.pay_charge_key_lite_view)).inflate();
        mKeyView = mKeyViewStub.findViewById(R.id.vip_way_keyboard);
        mKeyView.show();
        mKeyView.setOnKeyboardClickListener(this);
        mEdt.addTextChangedListener(edtWatcher);
    }


    TextWatcher edtWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s.toString())) {
                mChargeTv.setText(String.format("%.2f", Double.parseDouble(s.toString()) - TradeHelper.getTrade().getTotal()));
            } else {
                mChargeTv.setText("");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @OnClick(R.id.pay_charge_btn_submit)
    public void pay() {
        if (ClickUtil.onceClick()) {
            return;
        }
        if (TextUtils.isEmpty(mEdt.getText())) {
            MessageUtil.show("请输入收入现金金额");
        } else if (mEdt.getText().toString().contains("-")) {
            MessageUtil.show("收入现金金额不足");
        } else {
            Event.sendEvent(Event.TARGET_PAY_WAY, Event.TYPE_PAY_CASH);
        }

    }

    @OnClick(R.id.pay_charge_img_close)
    public void close() {
        dismiss();
    }

    @Override
    public void onKeyClick(View v, int key) {
        if (mEdt.getText().toString().contains(".")) {
            int position = mEdt.getText().toString().indexOf(".");
            if (mEdt.getText().toString().substring(0, position).length() > 6) {
                MessageUtil.show("超出限制");
                return;
            }
            if (mEdt.getText().toString().substring(position, mEdt.getText().toString().length() - 1).length() >= 2) {
                MessageUtil.show("超出限制");
                return;
            }
        } else {
            if (mEdt.getText().toString().length() >= 6) {
                return;
            }
        }
        mEdt.setText(mEdt.getText().append(String.valueOf(key)));
    }

    @Override
    public void onDeleteClick() {
        mEdt.setText(TextUtils.isEmpty(mEdt.getText().toString()) ? "" :
                mEdt.getText().toString().trim().substring(0, mEdt.getText().toString().trim().length() - 1));
    }

    @Override
    public void onPointClick() {
        if (mEdt.getText().toString().contains(".") || TextUtils.isEmpty(mEdt.getText().toString())) {
            return;
        }
        mEdt.getText().append('.');
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

    }

    @Override
    public void onCancelClick() {

    }

    @Override
    public void onEnterClick() {

    }
}