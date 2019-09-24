package com.ftrend.zgp.utils.pop;

import android.content.Context;
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
public class PriceMobileDialog extends BottomPopupView implements View.OnClickListener, KeyboardView.OnItemClickListener {
    @BindView(R.id.vip_way_edt)
    ClearEditText mEdt;
    @BindView(R.id.vip_way_tv_title)
    TextView mTitleTv;
    @BindView(R.id.vip_mobile_btn_submit)
    Button mSubmitBtn;
    //会员弹窗：1-手机号
    public static final int DIALOG_MOBILE = 1;
    //购物车：  2-改价
    public static final int DIALOG_CHANGE_PRICE = 2;
    private int type;
    private int index = 0;
    private Context mContext;
    private KeyboardView mKeyView;
    private View mKeyViewStub;


    public PriceMobileDialog(@NonNull Context context, int type) {
        super(context);
        this.type = type;
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
            default:
                break;
        }
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

    @Override
    protected void onDismiss() {
        super.onDismiss();
        if (mContext instanceof ShopCartActivity) {
            //需要撤销添加的最后一条
            if (TextUtils.isEmpty(mEdt.getText().toString())) {
                Event.sendEvent(Event.TARGET_SHOP_CART, Event.TYPE_CANCEL_PRICE_CHANGE);
            }
        }
    }


    //-------------------------------------键盘响应--------------------------------------------//
    @Override
    public void onKeyClick(View v, int key) {
        switch (type) {
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