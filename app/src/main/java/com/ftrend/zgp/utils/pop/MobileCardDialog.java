package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.zgp.R;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.msg.MessageUtil;
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
    private int type = 0;
    private int index = 0;

    public MobileCardDialog(@NonNull Context context, int type) {
        super(context);
        this.type = type;
    }

    public MobileCardDialog(@NonNull Context context, int type, int index) {
        super(context);
        this.type = type;
        this.index = index;
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
                if (TradeHelper.priceChange(index, Double.parseDouble(mMobileEdt.getText().toString()))) {
                    dismiss();
                    MessageUtil.showSuccess("改价成功");
                    KeyboardUtils.hideSoftInput(this);
                } else {
                    MessageUtil.showError("改价失败");
                }
                break;
            default:
                break;
        }
    }


    @OnClick(R.id.vip_way_img_close)
    public void close() {
        dismiss();
    }

}
