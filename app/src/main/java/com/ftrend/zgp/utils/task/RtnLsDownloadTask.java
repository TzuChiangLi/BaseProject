package com.ftrend.zgp.utils.task;

import android.util.Log;

import com.ftrend.zgp.model.DepPayInfo;
import com.ftrend.zgp.model.DepPayInfo_Table;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradePay;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.OperateCallback;
import com.ftrend.zgp.utils.RtnHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.log.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 退货下载线程
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/11/09
 */
public class RtnLsDownloadTask {
    private final String TAG = "RtnLsDownloadTask";

    // 线程是否正在运行
    private volatile boolean running = false;
    //错误
    private final String errCode = "";
    // 退货单号
    private static String rtnLsNo;
    // 线程唯一实例，避免重复运行
    private static RtnLsDownloadTask task = null;

    private static OperateCallback taskCallback;

    /**
     * 启动线程
     *
     * @param lsNo
     * @return
     */
    public static boolean taskStart(String lsNo, OperateCallback callback) {
        if (task != null && task.running) {
            return false;
        }
        rtnLsNo = lsNo;
        taskCallback = callback;
        LogUtil.d("----online rtn lsNo search:" + rtnLsNo);
        task = new RtnLsDownloadTask();
        task.start();
        return true;
    }

    /**
     * 开始执行数据下载任务
     */
    private void start() {
        running = true;
        downloadLs();
    }

    /**
     * 推送下载失败消息
     *
     * @param msg
     */
    private void postFailed(String msg) {
        running = false;
        taskCallback.onError(errCode, msg);
    }

    private void postSuccess() {
        running = false;
        taskCallback.onSuccess(null);
    }

    /**
     * 下载退货流水
     */
    private void downloadLs() {
        RestSubscribe.getInstance().queryRefundLs(rtnLsNo, new RestCallback(new RestResultHandler() {
            @Override
            public void onSuccess(Map<String, Object> body) {
                if (body != null) {
                    LogUtil.d("----trade:" + body.get("trade"));
                }
                if (!body.containsKey("trade") || !body.containsKey("prod") || !body.containsKey("pay")) {
                    return;
                }
                Map<String, Object> trade = (Map<String, Object>) body.get("trade");
                List<Map<String, Object>> prod = (List<Map<String, Object>>) body.get("prod");
                Map<String, Object> pay = (Map<String, Object>) body.get("pay");
                //不是当前专柜的销售流水不允许退货
                if (!ZgParams.getCurrentDep().getDepCode().equals(trade.get("depCode"))) {
                    postFailed("指定流水不存在");
                    return;
                }

                saveLs(trade, prod, pay);
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                Log.d(TAG, "下载流水失败: " + errorCode + " - " + errorMsg);
                postFailed(errorMsg);
            }
        }));
    }

    /**
     * 保存流水信息，启用事务保存
     *
     * @param trade 交易流水信息
     * @param prod  商品列表
     * @param pay   支付信息
     */
    private void saveLs(final Map<String, Object> trade, final List<Map<String, Object>> prod, final Map<String, Object> pay) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        saveTrade(gson, trade);
        saveProd(gson, prod);
        savePay(gson, pay);
        postSuccess();
    }

    /**
     * 保存交易流水
     *
     * @param values
     */
    private void saveTrade(Gson gson, Map<String, Object> values) {
        Trade trade = gson.fromJson(gson.toJson(values), Trade.class);
        //初始化
        LogUtil.d("----rtnFlag:" + trade.getRtnFlag());
        RtnHelper.setTrade(trade);
    }

    /**
     * 保存商品列表
     *
     * @param values
     */
    private void saveProd(Gson gson, List<Map<String, Object>> values) {
        if (values.size() == 0) {
            return;
        }
        List<TradeProd> prodList = new ArrayList<>();
        for (Map<String, Object> map : values) {
            TradeProd prod = gson.fromJson(gson.toJson(map), TradeProd.class);
            //已退货数量
            prod.setLastRtnAmount(Double.parseDouble(map.get("rtnAmount").toString()));
            //已退货金额
            prod.setLastRtnTotal(Double.parseDouble(map.get("rtnTotal").toString()));
            //初始化退货单价
            prod.setRtnPrice(prod.getTotal() / prod.getAmount());
            prodList.add(prod);
        }
        //初始化
        RtnHelper.setProdList(prodList);
    }

    /**
     * 保存支付信息
     *
     * @param values
     */
    private void savePay(Gson gson, Map<String, Object> values) {
        TradePay pay = gson.fromJson(gson.toJson(values), TradePay.class);
        // 查找对应的appPayType（后台不保存此字段）
        DepPayInfo payInfo = SQLite.select().from(DepPayInfo.class)
                .where(DepPayInfo_Table.payTypeCode.eq(pay.getPayTypeCode()))
                .querySingle();
        pay.setAppPayType(payInfo == null ? "" : payInfo.getAppPayType());
        //初始化
        RtnHelper.setPay(pay);
    }

}
