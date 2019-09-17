package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.ftrend.zgp.R;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.lxj.xpopup.core.CenterPopupView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择会员登录方式
 *
 * @author liziqiang@ftrend.cn
 */
public class VipDialog extends CenterPopupView {
    @BindView(R.id.vip_way_img_close)
    ImageView mCloseImg;

    public VipDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.vip_way_select;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
    }

    @OnClick(R.id.vip_way_img_close)
    public void close() {
        dismiss();
    }

    @OnClick(R.id.vip_way_ll_mobile)
    public void mobile() {
        MessageUtil.show("手机号");
    }

    @OnClick(R.id.vip_way_ll_scan)
    public void scan() {
        MessageUtil.show("扫码");
    }

    @OnClick(R.id.vip_way_ll_card)
    public void card() {
        MessageUtil.show("会员卡");
    }
}
