package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.Handover;
import com.ftrend.zgp.model.HandoverRecord;
import com.ftrend.zgp.utils.HandoverHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ftrend.zgp.utils.HandoverHelper.HANDOVER_PAY_ALIPAY;
import static com.ftrend.zgp.utils.HandoverHelper.HANDOVER_PAY_CARD;
import static com.ftrend.zgp.utils.HandoverHelper.HANDOVER_PAY_MONEY;
import static com.ftrend.zgp.utils.HandoverHelper.HANDOVER_PAY_WECHAT;

/**
 * @author liziqiang@ftrend.cn
 */

public class HandoverPresenter implements Contract.HandoverPresenter {
    private Contract.HandoverView mView;
    private List<HandoverRecord> mRecordList = new ArrayList<>();


    private HandoverPresenter(Contract.HandoverView mView) {
        this.mView = mView;
    }

    public static HandoverPresenter createPresenter(Contract.HandoverView mView) {
        return new HandoverPresenter(mView);
    }

    private RestResultHandler regHandler = new RestResultHandler() {
        @Override
        public void onSuccess(Map<String, Object> body) {
            if (mRecordList.size() == 0) {
            } else {
                //将本页面已获取到的数据保存到对象中
                for (int i = 0; i < mRecordList.size(); i++) {
                    Handover handover = new Handover();
                    handover.setRtnCount(mRecordList.get(i).getRtnCount());
                    handover.setRtnTotal(mRecordList.get(i).getRtnTotal());
                    handover.setSaleTotal(mRecordList.get(i).getSaleTotal());
                    handover.setSaleCount(mRecordList.get(i).getSaleCount());
                    handover.setCashier(mRecordList.get(i).getCashier());
                    handover.setDepCode(mRecordList.get(i).getDepCode());
                    handover.setStatus(HandoverHelper.HANDOVER_STATUS_FINISH);
                    HandoverHelper.saveHandover(handover);
                }
                mView.showSuccess();
            }
            TradeHelper.clearAllTradeData();
        }

        @Override
        public void onFailed(String errorCode, String errorMsg) {
            mView.showError();
        }
    };


    @Override
    public void initView() {
        double saleTotal, rtnTotal, getTotalByTradeFlag;
        double moneyTotal, aliPayTotal, wechatTotal, cardTotal, getTotalByPayType;
        long saleCount, rtnCount, getCountByTradeFlag, moneyCount, aliPayCount, wechatCount, cardCount, getCountByPayType;
        //先获取流水表里的用户名
        List<String> userCodeList = HandoverHelper.getUserCode();
        //根据用户名查询该用户下的流水
        if (userCodeList.size() == 0 || userCodeList == null) {
            return;
        } else {

            List<String> lsNoList = new ArrayList<>();
            //根据用户名能够获取到交易收银、退货的金额、次数，总的金额、次数
            for (int i = 0; i < userCodeList.size(); i++) {
                //region 用户名、用户账号、收银、退货、合计
                HandoverRecord handoverRecord = new HandoverRecord();
                handoverRecord.setCashier(userCodeList.get(i));
                handoverRecord.setCashierName(HandoverHelper.convertUserCodeToUserName(userCodeList.get(i)));
                saleTotal = HandoverHelper.getTotalByTradeFlag("T", userCodeList.get(i));
                saleCount = HandoverHelper.getCountByTradeFlag("T", userCodeList.get(i));
                rtnTotal = HandoverHelper.getTotalByTradeFlag("R", userCodeList.get(i));
                rtnCount = HandoverHelper.getCountByTradeFlag("R", userCodeList.get(i));
                getTotalByTradeFlag = HandoverHelper.getTotalByTradeFlag("ALL", userCodeList.get(i));
                getCountByTradeFlag = HandoverHelper.getCountByTradeFlag("ALL", userCodeList.get(i));

                handoverRecord.setSaleTotal(saleTotal);
                handoverRecord.setSaleCount(saleCount);
                handoverRecord.setRtnTotal(rtnTotal);
                handoverRecord.setRtnCount(rtnCount);
                handoverRecord.setTotal(getTotalByTradeFlag);
                handoverRecord.setCount(getCountByTradeFlag);
                //endregion
                //region 处理支付方式
                //取出流水单号集合
                lsNoList = HandoverHelper.getLsNoByUserCode(userCodeList.get(i));
                //再根据流水单号集合取数据(有用户名则必有流水单号，暂不判空)
                //现金
                moneyTotal = HandoverHelper.getTotalByPayType(HANDOVER_PAY_MONEY, lsNoList);
                moneyCount = HandoverHelper.getCountByPayType(HANDOVER_PAY_MONEY, lsNoList);
                //支付宝
                aliPayTotal = HandoverHelper.getTotalByPayType(HANDOVER_PAY_ALIPAY, lsNoList);
                aliPayCount = HandoverHelper.getCountByPayType(HANDOVER_PAY_ALIPAY, lsNoList);
                //微信支付
                wechatTotal = HandoverHelper.getTotalByPayType(HANDOVER_PAY_WECHAT, lsNoList);
                wechatCount = HandoverHelper.getCountByPayType(HANDOVER_PAY_WECHAT, lsNoList);
                //储值卡
                cardTotal = HandoverHelper.getTotalByPayType(HANDOVER_PAY_CARD, lsNoList);
                cardCount = HandoverHelper.getCountByPayType(HANDOVER_PAY_CARD, lsNoList);

                getTotalByPayType = HandoverHelper.getTotalByPayType(HandoverHelper.TRADE_ALL, lsNoList);
                getCountByPayType = HandoverHelper.getCountByPayType(HandoverHelper.TRADE_ALL, lsNoList);

                handoverRecord.setMoneyTotal(moneyTotal);
                handoverRecord.setMoneyCount(moneyCount);
                handoverRecord.setAliPayTotal(aliPayTotal);
                handoverRecord.setAliPayCount(aliPayCount);
                handoverRecord.setCardTotal(cardTotal);
                handoverRecord.setCardCount(cardCount);
                handoverRecord.setWechatTotal(wechatTotal);
                handoverRecord.setWechatCount(wechatCount);

                handoverRecord.setPayTotal(getTotalByPayType);
                handoverRecord.setPayCount(getCountByPayType);
                //endregion

                handoverRecord.setDepCode(ZgParams.getCurrentDep().getDepCode());

                mRecordList.add(handoverRecord);
            }
            mView.showHandoverRecord(mRecordList);
        }
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


//        saleTotal = HandoverHelper.getTotalByTradeFlag("T");
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