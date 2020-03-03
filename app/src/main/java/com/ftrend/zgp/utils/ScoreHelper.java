package com.ftrend.zgp.utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradePay;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.common.CommonUtil;
import com.ftrend.zgp.utils.http.RestBodyMap;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分辅助类
 *
 * @author liziqiang@ftrend.cn
 */
public class ScoreHelper {
    private static Trade trade = null;
    private static OperateCallback callback = null;
    private static String[] requestSign = {""};
    private static Map<String, Object> tradeMap = null;
    private static List<Map<String, Object>> prodMapList = null;
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RETRY:
                    retryVipScore();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 发送请求计算会员积分
     *
     * @param trade    交易
     * @param prodList 交易商品列表
     * @param pay      支付信息
     * @param callback 回调
     */
    public static void calcVipTotal(final Trade trade, List<TradeProd> prodList, TradePay pay,
                                    final OperateCallback callback) {
        setTrade(trade);
        setCallback(callback);
        //准备数据
        tradeMap = new HashMap<>();
        tradeMap.put("lsNo", trade.getLsNo());
        tradeMap.put("tradeFlag", trade.getTradeFlag());
        tradeMap.put("cardCode", trade.getCardCode());
        tradeMap.put("payType", pay.getPayTypeCode());
        prodMapList = new ArrayList<>();
        for (TradeProd prod : prodList) {
            Map<String, Object> prodMap = new HashMap<>();
            prodMap.put("sortNo", prod.getSortNo());
            prodMap.put("prodCode", prod.getProdCode());
            prodMap.put("amount", prod.getAmount());
            prodMap.put("total", prod.getTotal());
            prodMapList.add(prodMap);
        }
        //发送查询请求
        qryVipTotal(tradeMap, prodMapList);
    }

    /**
     * 发送查询请求
     *
     * @param tradeMap
     * @param prodMapList
     */
    private static void qryVipTotal(Map<String, Object> tradeMap, List<Map<String, Object>> prodMapList) {
        // 查询积分金额
        RestSubscribe.getInstance().calcVipTotal(tradeMap, prodMapList,
                new RestCallback(vipTotalHandler));
    }

    /**
     * 重试
     */
    private static void retryVipScore() {
        if (!TextUtils.isEmpty(requestSign[0])) {
            // 查询实时积分计算结果
            RestSubscribe.getInstance().vipScore(requestSign[0], new RestCallback(calcResultHandler));
        }
    }

    /**
     * 积分计算结果回调
     */
    static final RestResultHandler calcResultHandler = new RestResultHandler() {
        @Override
        public void onSuccess(RestBodyMap body) {
            // 返回实时积分计算结果
                /* body内容：
                "totalScore": 3969.0, // 剩余积分
                "currScore": 248.0    // 本次消费产生积分
                 */
            callback.onSuccess(body);
            //最终返回失败，清理掉信息
            finish();
        }

        @Override
        public void onFailed(String errorCode, String errorMsg) {
            if (errorCode.endsWith("B0")) {
                //B0-处理中,继续查询实时积分计算结果
                postMessage();
            } else {
                callback.onError(errorCode, errorMsg);
                //最终返回失败，清理掉信息
                finish();
            }
        }
    };

    /**
     * 积分计算请求回调
     */
    static final RestResultHandler calcRequestHandler = new RestResultHandler() {
        @Override
        public void onSuccess(RestBodyMap body) {
            requestSign[0] = body.getString("dataSign");
            // 查询实时积分计算结果
            RestSubscribe.getInstance().vipScore(requestSign[0], new RestCallback(calcResultHandler));
        }

        @Override
        public void onFailed(String errorCode, String errorMsg) {
            callback.onError(errorCode, errorMsg);
        }
    };

    /**
     * 积分金额计算回调
     */
    static final RestResultHandler vipTotalHandler = new RestResultHandler() {
        @Override
        public void onSuccess(RestBodyMap body) {
            double vipTotal = body.getDouble("vipTotal");
            // 发送实时积分计算请求
            RestSubscribe.getInstance().vipScoreRequest(
                    ZgParams.getPosCode(),
                    trade.getLsNo(),
                    CommonUtil.dateToYyyyMmDd(new Date()),
                    ZgParams.getCurrentUser().getUserCode(),
                    trade.getVipCode(),
                    trade.getCardCode(),
                    vipTotal,
                    trade.getTotal(),
                    new RestCallback(calcRequestHandler)
            );
        }

        @Override
        public void onFailed(String errorCode, String errorMsg) {
            callback.onError(errorCode, errorMsg);
        }
    };

    /**
     * 清理信息
     */
    private static void finish() {
        trade = null;
        callback = null;
        tradeMap = null;
        prodMapList = null;
        requestSign = new String[]{""};
    }

    /**
     * 消息触发重试
     */
    private static final int RETRY = 0;

    /**
     * 消息发送
     */
    public static void postMessage() {
        Message msg = new Message();
        msg.what = RETRY;
        mHandler.sendMessage(msg);
    }

    public static void setTrade(Trade trade) {
        ScoreHelper.trade = trade;
    }

    public static void setCallback(OperateCallback callback) {
        ScoreHelper.callback = callback;
    }
}
