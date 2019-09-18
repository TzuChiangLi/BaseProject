package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.KeyboardUtils;
import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.zgp.R;
import com.lxj.xpopup.core.BottomPopupView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author liziqiang@ftrend.cn
 */
public class MobileCardDialog extends BottomPopupView {
    @BindView(R.id.vip_way_edt_mobile)
    ClearEditText mMobileEdt;
    //会员弹窗：0-手机号
    public static final int DIALOG_CARD = 0;
    //会员弹窗：1-会员卡
    public static final int DIALOG_MOBILE = 1;
    private int type = 0;

    public MobileCardDialog(@NonNull Context context, int type) {
        super(context);
        this.type = type;
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
            default:
                break;
        }


    }


}
