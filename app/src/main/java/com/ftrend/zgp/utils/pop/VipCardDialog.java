package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ftrend.zgp.R;
import com.lxj.xpopup.core.BottomPopupView;

/**
 * 刷卡获取会员信息
 *
 * @author liziqiang@ftrend.cn
 */

public class VipCardDialog extends BottomPopupView {


    public VipCardDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.vip_login_card_dialog;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
    }
}
