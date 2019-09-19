package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.ftrend.cleareditview.ClearEditText;
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
 * @author liziqiang@ftrend.cn
 */
public class MobileCardDialog extends BottomPopupView {
    @BindView(R.id.vip_way_edt_mobile)
    ClearEditText mMobileEdt;
    @BindView(R.id.vip_way_img_card)
    ImageView mCardImg;
    @BindView(R.id.vip_way_tv_title)
    TextView mTitleTv;
    @BindView(R.id.vip_mobile_btn_submit)
    Button mSubmitBtn;
    @BindView(R.id.vip_way_img_close)
    ImageView mCloseImg;

    //会员弹窗：0-手机号
    public static final int DIALOG_CARD = 0;
    //会员弹窗：1-会员卡
    public static final int DIALOG_MOBILE = 1;
    //购物车：  2-改价
    public static final int DIALOG_CHANGE_PRICE = 2;
    private int type;
    private int index = 0;
    private Context mContext;

    public MobileCardDialog(@NonNull Context context, int type) {
        super(context);
        this.type = type;
        mContext = context;
    }

    public MobileCardDialog(@NonNull Context context, int type, int index) {
        super(context);
        this.type = type;
        this.index = index;
        mContext = context;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.vip_mobile_card;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        switch (type) {
            case DIALOG_CARD:
                mCardImg.setVisibility(VISIBLE);
                mMobileEdt.setVisibility(GONE);
                mTitleTv.setVisibility(GONE);
                break;
            case DIALOG_MOBILE:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMobileEdt.requestFocus();
                        KeyboardUtils.showSoftInput(mMobileEdt);
                    }
                }, 300);
                break;
            case DIALOG_CHANGE_PRICE:
                mTitleTv.setText("请输入修改后的商品价格：");
                mSubmitBtn.setText("修改");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMobileEdt.requestFocus();
                        KeyboardUtils.showSoftInput(mMobileEdt);
                    }
                }, 300);
                break;
            default:
                break;
        }
    }


    @OnClick(R.id.vip_mobile_btn_submit)
    public void submit() {
        switch (type) {
            case DIALOG_CARD:
                break;
            case DIALOG_MOBILE:
                break;
            case DIALOG_CHANGE_PRICE:
                //TODO 价格格式验证
                if (TextUtils.isEmpty(mMobileEdt.getText().toString())) {
                    return;
                }
                if (mContext instanceof ShopListActivity) {
                    if (TradeHelper.priceChangeInShopList(index, Double.parseDouble(mMobileEdt.getText().toString()))) {
                        Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_REFRESH, Double.parseDouble(mMobileEdt.getText().toString()));
                        MessageUtil.showSuccess("改价成功");
                        KeyboardUtils.hideSoftInput(this);
                        dismiss();
                    } else {
                        MessageUtil.showError("改价失败");
                    }
                    return;
                }
                if (mContext instanceof ShopCartActivity) {
                    if (TradeHelper.priceChangeInShopCart(Double.parseDouble(mMobileEdt.getText().toString()))) {
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

}
