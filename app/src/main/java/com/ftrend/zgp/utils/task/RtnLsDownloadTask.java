package com.ftrend.zgp.utils.task;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ftrend.zgp.model.DepPayInfo;
import com.ftrend.zgp.model.DepPayInfo_Table;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradePay;
import com.ftrend.zgp.model.TradePay_Table;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.TradeProd_Table;
import com.ftrend.zgp.model.Trade_Table;
import com.ftrend.zgp.utils.OperateCallback;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.db.ZgpDb;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.log.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 流水下载线程
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/6
 */
public class RtnLsDownloadTask {
    private final String TAG = "RtnLsDownloadTask";

    // 进度消息处理器
    private DataDownloadTask.ProgressHandler handler;

    // 线程是否正在运行
    private volatile boolean running = false;
    // 是否已强制终止执行
    private volatile boolean interrupted = false;
    // 当前步骤已重试次数
    private int retryCount = 0;
    // 最大重试次数
    private final int MAX_RETRY = 3;
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
        task.start(callback);
        return true;
    }

    /**
     * 停止线程
     */
    public static void cancel() {
        if (task != null) {
            task.interrupt();
        }
    }


    /**
     * 开始执行数据下载任务
     */
    private void start(OperateCallback callback) {
        running = true;
        downloadLs(callback);
    }

    /**
     * 终止数据下载
     */
    private void interrupt() {
        this.interrupted = true;
    }


    /**
     * 重试当前更新，超过重试次数时，任务执行失败
     *
     * @param err
     */
    private void retry(String err) {
        retryCount++;
        if (retryCount > MAX_RETRY) {
            postFailed(err + "达到最大重试次数");
        } else {
            exec();
        }
    }

    /**
     * 执行当前更新任务
     */
    private void exec() {
        if (interrupted) {
            //线程中断，停止执行
            return;
        }
        start(taskCallback);
    }


    /**
     * 推送下载失败消息
     *
     * @param msg
     */
    private void postFailed(String msg) {
        running = false;
    }


    /**
     * 下载退货流水
     *
     * @param callback
     */
    private void downloadLs(final OperateCallback callback) {
        RestSubscribe.getInstance().queryRefundLs(rtnLsNo, new RestCallback(new RestResultHandler() {
            @Override
            public void onSuccess(Map<String, Object> body) {
                if (!body.containsKey("trade") || !body.containsKey("prod") || !body.containsKey("pay")) {
                    return;
                }
                Map<String, Object> trade = (Map<String, Object>) body.get("trade");
                List<Map<String, Object>> prod = (List<Map<String, Object>>) body.get("prod");
                Map<String, Object> pay = (Map<String, Object>) body.get("pay");

                saveLs(trade, prod, pay);
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                Log.d(TAG, "下载流水失败: " + errorCode + " - " + errorMsg);
                retry("----下载流水失败，流水号：" + rtnLsNo);
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
        Transaction transaction = FlowManager.getDatabase(ZgpDb.class).beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                saveTrade(gson, trade);
                saveProd(gson, prod);
                savePay(gson, pay);
            }
        }).success(new Transaction.Success() {
            @Override
            public void onSuccess(@NonNull Transaction transaction) {
                if (taskCallback == null) {
                    return;
                }
                taskCallback.onSuccess(null);
            }
        }).error(new Transaction.Error() {
            @Override
            public void onError(@NonNull Transaction transaction, @NonNull Throwable error) {
                Log.d(TAG, "流水保存失败：" + error.getLocalizedMessage());
                //流水保存失败，重新下载
                retry("流水保存失败");
            }
        }).build();
        transaction.execute();
    }

    /**
     * 保存交易流水
     *
     * @param values
     */
    private void saveTrade(Gson gson, Map<String, Object> values) {
        Trade trade = gson.fromJson(gson.toJson(values), Trade.class);
        SQLite.delete(Trade.class)
                .where(Trade_Table.lsNo.eq(trade.getLsNo()))
                .execute();
        trade.setStatus(TradeHelper.TRADE_STATUS_PAID);
        trade.setCreateTime(trade.getTradeTime());
        trade.setCreateIp(ZgParams.getCurrentIp());
        trade.insert();
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
            prod.setDelFlag("0");
            //手工优惠统一写入单项优惠，防止计算total出错
            if (map.containsKey("manuDsc")) {
                prod.setSingleDsc(Double.parseDouble(map.get("manuDsc").toString()));
                prod.setWholeDsc(0);
            }
            prodList.add(prod);
        }

        SQLite.delete(TradeProd.class)
                .where(TradeProd_Table.lsNo.eq(prodList.get(0).getLsNo()))
                .execute();
        for (TradeProd prod : prodList) {
            prod.insert();
        }
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
        // 先删除后添加
        SQLite.delete(TradePay.class)
                .where(TradePay_Table.lsNo.eq(pay.getLsNo()))
                .execute();
        pay.insert();
    }

}
