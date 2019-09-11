package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.Handover;
import com.ftrend.zgp.utils.HandoverHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;

import java.util.Map;

/**
 * @author liziqiang@ftrend.cn
 */

public class HandoverPresenter implements Contract.HandoverPresenter {
    private Contract.HandoverView mView;
    private double saleTotal, rtnTotal, tradeTotal;
    private double moneyTotal, aliPayTotal, wechatTotal, cardTotal, payTotal;
    private long saleCount, rtnCount, tradeCount, moneyCount, aliPayCount, wechatCount, cardCount, payCount;

    private HandoverPresenter(Contract.HandoverView mView) {
        this.mView = mView;
    }

    public static HandoverPresenter createPresenter(Contract.HandoverView mView) {
        return new HandoverPresenter(mView);
    }

    private RestResultHandler regHandler = new RestResultHandler() {
        @Override
        public void onSuccess(Map<String, Object> body) {
            mView.showSuccess();
            //TODO 2019年9月10日18:27 写入数据库
            Handover handover = new Handover();
            handover.setRtnCount(rtnCount);
            handover.setRtnTotal(rtnTotal);
            handover.setSaleTotal(saleTotal);
            handover.setSaleCount(saleCount);
            handover.setCashier(ZgParams.getCurrentUser().getUserCode());
            handover.setDepCode(ZgParams.getCurrentDep().getDepCode());
            handover.setStatus(HandoverHelper.HANDOVER_STATUS_FINISH);
            //TODO 2019年9月10日19:56 获取最大lsNo和最小lsNo
            HandoverHelper.saveHandover(handover);

        }

        @Override
        public void onFailed(String errorCode, String errorMsg) {
            mView.showError();
        }
    };


    @Override
    public void initView() {
        saleTotal = HandoverHelper.tradeTotal("T");
        saleCount = HandoverHelper.tradeCount("T");
        rtnTotal = HandoverHelper.tradeTotal("R");
        rtnCount = HandoverHelper.tradeCount("R");
        tradeTotal = HandoverHelper.tradeTotal("ALL");
        tradeCount = HandoverHelper.tradeCount("ALL");

        moneyTotal = HandoverHelper.payTotal("1");
        moneyCount = HandoverHelper.payCount("1");
        aliPayTotal = HandoverHelper.payTotal("2");
        aliPayCount = HandoverHelper.payCount("2");
        wechatTotal = HandoverHelper.payTotal("3");
        wechatCount = HandoverHelper.payCount("3");
        cardTotal = HandoverHelper.payTotal("4");
        cardCount = HandoverHelper.payCount("4");
        payTotal = HandoverHelper.payTotal("ALL");
        payCount = HandoverHelper.payCount("ALL");


        //显示收款员信息
        mView.showUserInfo(ZgParams.getCurrentUser().getUserCode(), ZgParams.getCurrentUser().getUserName());
        //收银、退货信息
        mView.showCashInfo(saleTotal, saleCount);
        mView.showTHInfo(rtnTotal, rtnCount);
        //交易合计
        mView.showTradeInfo(tradeTotal, tradeCount);
        //现金
        mView.showMoneyInfo(moneyTotal, moneyCount);
        //支付宝
        mView.showAliPayInfo(aliPayTotal, aliPayCount);
        //微信支付
        mView.showWeChatInfo(wechatTotal, wechatCount);
        //储值卡
        mView.showCardInfo(cardTotal, cardCount);
        //支付方式合计
        mView.showPayInfo(payTotal, payCount);
    }

    @Override
    public void doHandover() {
        RestSubscribe.getInstance().posEnd(ZgParams.getPosCode(), new RestCallback(regHandler));
    }
}
