package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ftrend.zgp.R;
import com.ftrend.zgp.utils.event.Event;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.lxj.xpopup.core.CenterPopupView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择会员登录方式\购物车更多功能
 *
 * @author liziqiang@ftrend.cn
 */
public class VipMoreBtnDialog extends CenterPopupView implements View.OnClickListener {
    @BindView(R.id.img_close)
    ImageView mCloseImg;
    private Context mContext;
    private int type;


    public VipMoreBtnDialog(@NonNull Context context, int type) {
        super(context);
        mContext = context;
        this.type = type;
    }

    @Override
    protected int getImplLayoutId() {
        if (type == 0) {
            return R.layout.vip_way_select_dialog;
        } else {
            return R.layout.shop_list_more_dialog;
        }
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        if (type == 0) {
            LinearLayout[] layouts = {findViewById(R.id.vip_way_ll_mobile), findViewById(R.id.vip_way_ll_card)};
            for (int i = 0; i < layouts.length; i++) {
                layouts[i].setOnClickListener(this);
            }
        } else {
            Button[] btns = {findViewById(R.id.more_btn_cancel), findViewById(R.id.more_btn_hang_up),
                    findViewById(R.id.more_btn_vip_dsc), findViewById(R.id.more_btn_vip_whole_dsc)};
            for (int i = 0; i < btns.length; i++) {
                btns[i].setOnClickListener(this);
            }
        }
    }


    @OnClick(R.id.img_close)
    public void close() {
        dismiss();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vip_way_ll_card:
                MessageUtil.showVipCard(mContext);
                break;
            case R.id.vip_way_ll_scan:
                Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_ENTER_SCAN);
                break;
            case R.id.vip_way_ll_mobile:
                MessageUtil.showVipMobile(mContext, PriceMobileDialog.DIALOG_MOBILE);
                break;
            case R.id.more_btn_cancel:
                Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_DIALOG_CANCEL_TRADE);
                break;
            case R.id.more_btn_hang_up:
                Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_DIALOG_HANG_UP);
                break;
            case R.id.more_btn_vip_dsc:
                Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_DIALOG_VIP_DSC);
                break;
            case R.id.more_btn_vip_whole_dsc:
                Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_DIALOG_WHOLE_DSC);
                break;
            default:
                break;
        }
        dismiss();
    }
}
