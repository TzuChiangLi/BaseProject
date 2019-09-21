package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.ftrend.zgp.R;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.CenterPopupView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择会员登录方式
 *
 * @author liziqiang@ftrend.cn
 */
public class VipWayDialog extends CenterPopupView {
    @BindView(R.id.vip_way_img_close)
    ImageView mCloseImg;
    private Context mContext;

    public VipWayDialog(@NonNull Context context) {
        super(context);
        mContext = context;
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
        dismiss();
        new XPopup.Builder(mContext)
                .dismissOnTouchOutside(false)
                .asCustom(new PriceDscDialog(mContext, PriceDscDialog.DIALOG_MOBILE))
                .show();
    }

    @OnClick(R.id.vip_way_ll_scan)
    public void scan() {
        MessageUtil.show("扫码");
    }

    @OnClick(R.id.vip_way_ll_card)
    public void card() {
        MessageUtil.show("会员卡");
        dismiss();
        new XPopup.Builder(mContext)
                .dismissOnTouchOutside(false)
                .asCustom(new VipCardDialog(mContext))
                .show();
    }
}
