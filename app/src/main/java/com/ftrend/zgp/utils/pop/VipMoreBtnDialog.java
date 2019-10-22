package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ftrend.zgp.R;
import com.ftrend.zgp.model.VipInfo;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.event.Event;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.msg.InputPanel;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.lxj.xpopup.core.CenterPopupView;

import java.util.Map;

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
        if (ClickUtil.onceClick()) {
            return;
        }
        dismiss();
    }


    @Override
    public void onClick(View v) {
        if (ClickUtil.onceClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.vip_way_ll_card:
                InputPanel.showVipCard(mContext);
                break;
            case R.id.vip_way_ll_scan:
                Event.sendEvent(Event.TARGET_SHOP_LIST, Event.TYPE_ENTER_SCAN);
                break;
            case R.id.vip_way_ll_mobile:
                InputPanel.showVipMobile(mContext, new StringInputCallback() {
                    @Override
                    public void onOk(String value) {
                        queryVipInfo(value); // TODO: 2019/10/21 移到购物车界面去处理
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public String validate(String value) {
                        return null;
                    }
                });
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

    private void queryVipInfo(String value) {
        if (ZgParams.isIsOnline()) {
            //在线查询会员信息
            RestSubscribe.getInstance().queryVipInfo(value, new RestCallback(regHandler));
        } else {
            MessageUtil.showWarning("当前为单机模式，无法查询会员信息");
            //保存vipCode
            TradeHelper.saveVipCodeOffline(value);
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
            } else {
                MessageUtil.show("服务返回异常错误");
            }
        }

        @Override
        public void onFailed(String errorCode, String errorMsg) {
            MessageUtil.show(errorCode + errorMsg);
        }
    };
}
