package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.Handover;
import com.ftrend.zgp.model.HandoverRecord;
import com.ftrend.zgp.utils.HandoverHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liziqiang@ftrend.cn
 */

public class HandoverPresenter implements Contract.HandoverPresenter {
    private Contract.HandoverView mView;
    private List<HandoverRecord> mRecordList = new ArrayList<>();
    private double saleTotal, rtnTotal, getTotalByTradeFlag;
    private double moneyTotal, aliPayTotal, wechatTotal, cardTotal, getTotalByPayType;
    private long saleCount, rtnCount, getCountByTradeFlag, moneyCount, aliPayCount, wechatCount, cardCount, getCountByPayType;

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
            //将本页面已获取到的数据保存到对象中
            Handover handover = new Handover();
            handover.setRtnCount(rtnCount);
            handover.setRtnTotal(rtnTotal);
            handover.setSaleTotal(saleTotal);
            handover.setSaleCount(saleCount);
            handover.setCashier(ZgParams.getCurrentUser().getUserCode());
            handover.setDepCode(ZgParams.getCurrentDep().getDepCode());
            handover.setStatus(HandoverHelper.HANDOVER_STATUS_FINISH);
            HandoverHelper.saveHandover(handover);
        }

        @Override
        public void onFailed(String errorCode, String errorMsg) {
            mView.showError();
        }
    };


    @Override
    public void initView() {
        //TODO 2019年9月18日19:34:02 收银-退货-合计，在Trade表内即可计算完毕
        //TODO 剩下支付方式，需要根据用户名筛选出流水单号，再用交易支付表的数据计算
        //先获取流水表里的用户名
        List<String> userCodeList = HandoverHelper.getUserCode();
        //根据用户名查询该用户下的流水
        if (userCodeList.size() == 0 || userCodeList == null) {
            //TODO 2019年9月18日19:09:39 插入空view
        } else {
            List<String> lsNoList = new ArrayList<>();
            for (int i = 0; i < userCodeList.size(); i++) {
                //取出流水单号集合
                lsNoList = HandoverHelper.getLsNoByUserCode(userCodeList.get(i));
                //再根据流水单号集合取数据(有用户名则必有流水单号，暂不判空)
                for (int j = 0; j < lsNoList.size(); j++) {


                }
            }
        }


        saleTotal = HandoverHelper.getTotalByTradeFlag("T");
//        saleCount = HandoverHelper.getCountByTradeFlag("T");
//        rtnTotal = HandoverHelper.getTotalByTradeFlag("R");
//        rtnCount = HandoverHelper.getCountByTradeFlag("R");
//        getTotalByTradeFlag = HandoverHelper.getTotalByTradeFlag("ALL");
//        getCountByTradeFlag = HandoverHelper.getCountByTradeFlag("ALL");
//        //现金
//        moneyTotal = HandoverHelper.getTotalByPayType("1");
//        moneyCount = HandoverHelper.getCountByPayType("1");
//        //支付宝
//        aliPayTotal = HandoverHelper.getTotalByPayType("2");
//        aliPayCount = HandoverHelper.getCountByPayType("2");
//        //微信支付
//        wechatTotal = HandoverHelper.getTotalByPayType("3");
//        wechatCount = HandoverHelper.getCountByPayType("3");
//        //储值卡
//        cardTotal = HandoverHelper.getTotalByPayType("4");
//        cardCount = HandoverHelper.getCountByPayType("4");
//
//
//        getTotalByPayType = HandoverHelper.getTotalByPayType(HandoverHelper.TRADE_ALL);
//        getCountByPayType = HandoverHelper.getCountByPayType(HandoverHelper.TRADE_ALL);
//
//
//        //显示收款员信息
//        mView.showUserInfo(ZgParams.getCurrentUser().getUserCode(), ZgParams.getCurrentUser().getUserName());
//        //收银、退货信息
//        mView.showCashInfo(saleTotal, saleCount);
//        mView.showTHInfo(rtnTotal, rtnCount);
//        //交易合计
//        mView.showTradeInfo(getTotalByTradeFlag, getCountByTradeFlag);
//        //现金
//        mView.showMoneyInfo(moneyTotal, moneyCount);
//        //支付宝
//        mView.showAliPayInfo(aliPayTotal, aliPayCount);
//        //微信支付
//        mView.showWeChatInfo(wechatTotal, wechatCount);
//        //储值卡
//        mView.showCardInfo(cardTotal, cardCount);
//        //支付方式合计
//        mView.showPayInfo(getTotalByPayType, getCountByPayType);
    }

    @Override
    public void doHandover() {
        if (ZgParams.isIsOnline()) {
            RestSubscribe.getInstance().posEnd(ZgParams.getPosCode(), new RestCallback(regHandler));
        } else {
            mView.showOfflineTip();
        }
    }
}
