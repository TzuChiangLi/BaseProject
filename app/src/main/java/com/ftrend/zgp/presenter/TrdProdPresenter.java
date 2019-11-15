package com.ftrend.zgp.presenter;

import android.text.TextUtils;

import com.ftrend.log.LogUtil;
import com.ftrend.zgp.api.TrdProdContract;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.VipInfo;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @author liziqiang@ftrend.cn
 */

public class TrdProdPresenter implements TrdProdContract.TrdProdPresenter {
    private TrdProdContract.TrdProdView mView;

    private TrdProdPresenter(TrdProdContract.TrdProdView mView) {
        this.mView = mView;
    }

    public static TrdProdPresenter createPresenter(TrdProdContract.TrdProdView mView) {
        return new TrdProdPresenter(mView);
    }

    @Override
    public void initTradeInfo() {
        String appPayType = TradeHelper.getPay().getAppPayType();
        String depCode = TradeHelper.getTrade().getDepCode();
        Trade trade = TradeHelper.getTrade();

        if (!TextUtils.isEmpty(trade.getVipCode())) {
            //未结、未挂起的单据有会员优惠的信息，但是vip是null
            if (ZgParams.isIsOnline()) {
                //查询会员信息
                RestSubscribe.getInstance().queryVipInfo(trade.getVipCode(), new RestCallback(regHandler));
            } else {
                //
                VipInfo vipInfo = TradeHelper.vip();
                vipInfo.setVipCode(trade.getVipCode());
                vipInfo.setVipGrade(trade.getVipGrade());
                vipInfo.setCardCode(trade.getCardCode());
                //保存会员信息
                TradeHelper.saveVip();
                mView.showVipInfo(vipInfo);
            }
        }
        mView.showTradeProd(TradeHelper.getProdList());
        mView.showPayInfo(TradeHelper.convertAppPayType(appPayType, depCode), TradeHelper.payTypeImgRes(appPayType));
        mView.showTradeInfo(new SimpleDateFormat("yyyy年MM月dd日HH:mm").format(trade.getTradeTime()),
                trade.getLsNo().length() > 8 ? trade.getLsNo() : String.format("%s%s", new SimpleDateFormat("yyyyMMdd").format(trade.getTradeTime()), trade.getLsNo()),
                TradeHelper.getCashierByUserCode(trade.getCashier()),
                String.format("%.2f", trade.getTotal()));
    }

    private RestResultHandler regHandler = new RestResultHandler() {
        @Override
        public void onSuccess(Map<String, Object> body) {
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
            //保存会员信息
            TradeHelper.saveVip();
            //刷新界面
            mView.showVipInfo(vipInfo);
        }

        @Override
        public void onFailed(String errorCode, String errorMsg) {
            LogUtil.d("----vipCode err:"+errorCode+errorMsg);
        }
    };
}