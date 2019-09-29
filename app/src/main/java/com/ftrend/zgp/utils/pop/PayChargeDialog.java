package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
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
public class PayChargeDialog extends BottomPopupView implements View.OnClickListener, KeyboardView.OnItemClickListener {
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
    private View mKeyViewStub;
    private EditText mRateEdt, mDscEdt;
    private boolean isFirst = true;


    public PayChargeDialog(@NonNull Context context, int type) {
        super(context);
        this.type = type;
        mContext = context;
    }

    public PayChargeDialog(@NonNull Context context, int type, int index) {
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
                    mDscEdt.getText().append('.');
                }
                break;
            default:
                mEdt.getText().append('.');
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
                        if (DscHelper.commitWholeDsc(Double.parseDouble(mDscEdt.getText().toString()))) {
                            Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_REFRESH_WHOLE_PRICE);
                            dismiss();
                        }
                    }
                } else {
                    MessageUtil.showError("失败");
                }
                break;
            default:
                break;
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