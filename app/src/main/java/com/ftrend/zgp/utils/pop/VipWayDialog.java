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
        dismiss();
        MessageUtil.showVipMobile(mContext, PriceMobileDialog.DIALOG_MOBILE);
    }

    @OnClick(R.id.vip_way_ll_scan)
    public void scan() {

    }

    @OnClick(R.id.vip_way_ll_card)
    public void card() {
        dismiss();
        MessageUtil.showVipCard(mContext);
    }
}
