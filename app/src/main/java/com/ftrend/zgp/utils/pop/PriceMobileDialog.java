package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.keyboard.KeyboardView;
import com.ftrend.zgp.R;
import com.ftrend.zgp.model.VipInfo;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.event.Event;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.view.ShopCartActivity;
import com.ftrend.zgp.view.ShopListActivity;
import com.lxj.xpopup.core.BottomPopupView;

import java.util.Map;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 改价、输入手机号弹出窗口
 *
 * @author liziqiang@ftrend.cn
 */
public class PriceMobileDialog extends BottomPopupView implements View.OnClickListener, KeyboardView.OnItemClickListener {
    @BindView(R.id.vip_way_edt)
    ClearEditText mEdt;
    @BindView(R.id.vip_dsc_tv_title)
    TextView mTitleTv;
    @BindView(R.id.vip_mobile_btn_submit)
    Button mSubmitBtn;
    @BindDrawable(R.drawable.vip_query_btn_selector)
    Drawable vip_query_blue;
    @BindDrawable(R.drawable.pay_dialog_btn_selector)
    Drawable pay_red_selector;
    //会员弹窗：1-手机号
    public static final int DIALOG_MOBILE = 1;
    //购物车：  2-改价
    public static final int DIALOG_CHANGE_PRICE = 2;
    private int type;
    private int index = 0, changed = -1;
    private Context mContext;
    private KeyboardView mKeyView;
    private View mKeyViewStub;
    // 输入提示信息
    private String title = "请输入：";


    public PriceMobileDialog(@NonNull Context context, int type) {
        super(context);
        this.type = type;
        mContext = context;
    }

    public PriceMobileDialog(@NonNull Context context, String title, InputCallback callback) {
        super(context);
        this.title = title;
        this.callback = callback;
        mContext = context;
    }

    public PriceMobileDialog(@NonNull Context context, int type, int index) {
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
            case DIALOG_MOBILE:
                //手机号
                mKeyViewStub = ((ViewStub) findViewById(R.id.vip_way_key_lite_view)).inflate();
                mKeyView = mKeyViewStub.findViewById(R.id.vip_way_keyboard);
                mKeyView.show();
                mSubmitBtn.setText("查询");
                mEdt.setInputType(InputType.TYPE_NULL);
                mEdt.setOnClickListener(this);
                mEdt.setText("13637366688");
                mKeyView.setOnKeyboardClickListener(this);
                mSubmitBtn.setBackground(vip_query_blue);
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
                mSubmitBtn.setBackground(vip_query_blue);
                mKeyView.setOnKeyboardClickListener(this);
                mSubmitBtn.setBackground(pay_red_selector);
                break;
            default:
                mKeyViewStub = ((ViewStub) findViewById(R.id.vip_way_key_lite_view)).inflate();
                mKeyView = mKeyViewStub.findViewById(R.id.vip_way_keyboard);
                mKeyView.show();
                mTitleTv.setText(title);
                mSubmitBtn.setText("确定");
                mEdt.setInputType(InputType.TYPE_NULL);
                mEdt.setOnClickListener(this);
                mKeyView.setOnKeyboardClickListener(this);
                mSubmitBtn.setBackground(pay_red_selector);
                break;
        }
    }


    @OnClick(R.id.vip_mobile_btn_submit)
    public void submit() {
        switch (type) {
            case DIALOG_MOBILE:
                queryVipInfo();
                break;
            case DIALOG_CHANGE_PRICE:
                if (TextUtils.isEmpty(mEdt.getText().toString())) {
                    changed = -1;
                    return;
                }
                if (!TradeHelper.checkPriceFormat(mEdt.getText().toString())) {
                    changed = -1;
                    MessageUtil.show("格式不正确");
                    return;
                }
                if (mContext instanceof ShopListActivity) {
                    if (TradeHelper.priceChangeInShopList(index, Double.parseDouble(mEdt.getText().toString()))) {
                        Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_REFRESH, Double.parseDouble(mEdt.getText().toString()));
                        MessageUtil.showSuccess("改价成功");
                        changed = 0;
                        KeyboardUtils.hideSoftInput(this);
                        dismiss();
                    } else {
                        MessageUtil.showError("改价失败");
                        changed = -1;
                    }
                    return;
                }
                if (mContext instanceof ShopCartActivity) {
                    if (TradeHelper.priceChangeInShopCart(Double.parseDouble(mEdt.getText().toString()))) {
                        Event.sendEvent(Event.TARGET_SHOP_CART, Event.TYPE_REFRESH);
                        KeyboardUtils.hideSoftInput(this);
                        changed = 0;
                        MessageUtil.showSuccess("改价成功");
                        dismiss();
                    } else {
                        MessageUtil.showError("改价失败");
                        changed = -1;
                    }
                    return;
                }
                break;
            default:
                if (callback != null) {
                    callback.onOk(mEdt.getText().toString());
                    callback = null;
                }
                dismiss();
                break;
        }
    }

    private InputCallback callback = null;

    public interface InputCallback {
        void onOk(final String value);

        void onCancel();
    }

    private void queryVipInfo() {
        if (ZgParams.isIsOnline()) {
            //在线查询会员信息
            RestSubscribe.getInstance().queryVipInfo(mEdt.getText().toString(), new RestCallback(regHandler));
        } else {
            MessageUtil.showWarning("当前为单机模式，无法查询会员信息");
            //保存vipCode
            TradeHelper.saveVipCodeOffline(mEdt.getText().toString());
            dismiss();
        }

    }

    private RestResultHandler regHandler = new RestResultHandler() {
        @Override
        public void onSuccess(Map<String, Object> body) {
            if (body != null) {
                VipInfo vipInfo = TradeHelper.vip();
                vipInfo.setVipName(body.get("vipName").toString());
                vipInfo.setVipCode(body.get("vipCode").toString());
                vipInfo.setVipDscRate(Double.parseDouble(body.get("vipDscRate").toString()));
                vipInfo.setVipGrade(body.get("vipGrade").toString());
                vipInfo.setVipPriceType(Double.parseDouble(body.get("vipPriceType").toString()));
                vipInfo.setRateRule(Double.parseDouble(body.get("rateRule").toString()));
                vipInfo.setForceDsc(body.get("forceDsc").toString());
                vipInfo.setCardCode(body.get("cardCode").toString());
                vipInfo.setDscProdIsDsc(body.get("dscProdIsDsc").toString());
                //保存会员信息到流水
                TradeHelper.saveVip();
                //刷新会员优惠
                TradeHelper.saveVipDsc();
                Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_REFRESH_VIP_INFO, vipInfo);
                dismiss();
            } else {
                MessageUtil.show("服务返回异常错误");
            }
        }

        @Override
        public void onFailed(String errorCode, String errorMsg) {
            MessageUtil.show(errorCode + errorMsg);
        }
    };


    @OnClick(R.id.vip_way_img_close)
    public void close() {
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

    @Override
    protected void onDismiss() {
        super.onDismiss();
        if (mContext instanceof ShopCartActivity) {
            //需要撤销添加的最后一条
            if (changed == -1) {
                Event.sendEvent(Event.TARGET_SHOP_CART, Event.TYPE_CANCEL_PRICE_CHANGE, index);
            }
        }
        if (callback != null) {
            callback.onCancel();
        }
    }


    //-------------------------------------键盘响应--------------------------------------------//
    @Override
    public void onKeyClick(View v, int key) {
        switch (type) {
            case DIALOG_MOBILE:
                if (mEdt.getText().toString().length() >= 11) {
                    return;
                }
            case DIALOG_CHANGE_PRICE:
                if (type == DIALOG_CHANGE_PRICE) {
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
                }
            default:
                mEdt.setText(mEdt.getText().append(String.valueOf(key)));
                break;
        }

    }

    @Override
    public void onDeleteClick() {
        switch (type) {
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
            case DIALOG_MOBILE:
                break;
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
    }

    @Override
    public void onClearClick() {
        switch (type) {
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


    }

}