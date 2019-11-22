package com.ftrend.zgp.presenter;

import android.text.TextUtils;

import com.ftrend.log.LogUtil;
import com.ftrend.zgp.api.TrdProdContract;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.VipInfo;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.RestBodyMap;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.printer.PrintFormat;
import com.ftrend.zgp.utils.printer.PrinterHelper;
import com.ftrend.zgp.view.TradeProdActivity;
import com.sunmi.peripheral.printer.SunmiPrinterService;

import java.text.SimpleDateFormat;

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
        //如果交易流水中包含会员信息，那么根据设备是否在线，获取相关的会员信息进行显示
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

    @Override
    public void print() {
        PrinterHelper.initPrinter(TradeProdActivity.mContext, new PrinterHelper.PrintInitCallBack() {
            @Override
            public void onSuccess(SunmiPrinterService service) {
                getPrintData(service);
                mView.printResult();
            }

            @Override
            public void onFailed() {
                MessageUtil.showError("打印机出现故障，请检查");
            }
        });
    }

    public void getPrintData(SunmiPrinterService service) {
        if (service == null) {
            return;
        }
        //生成数据，执行打印命令
        PrinterHelper.print(PrintFormat.printSale());
    }

    private RestResultHandler regHandler = new RestResultHandler() {
        @Override
        public void onSuccess(RestBodyMap body) {
            VipInfo vipInfo = TradeHelper.vip();
            vipInfo.setVipName(body.getString("vipName"));
            vipInfo.setVipCode(body.getString("vipCode"));
            vipInfo.setVipDscRate(body.getDouble("vipDscRate"));
            vipInfo.setVipGrade(body.getString("vipGrade"));
            vipInfo.setVipPriceType(body.getDouble("vipPriceType"));
            vipInfo.setRateRule(body.getDouble("rateRule"));
            vipInfo.setForceDsc(body.getString("forceDsc"));
            vipInfo.setCardCode(body.getString("cardCode"));
            vipInfo.setDscProdIsDsc(body.getString("dscProdIsDsc"));
            //保存会员信息
            TradeHelper.saveVip();
            //刷新界面
            mView.showVipInfo(vipInfo);
        }

        @Override
        public void onFailed(String errorCode, String errorMsg) {
            LogUtil.d("----vipCode err:" + errorCode + errorMsg);
        }
    };
}