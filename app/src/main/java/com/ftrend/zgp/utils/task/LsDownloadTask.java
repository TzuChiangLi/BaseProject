package com.ftrend.zgp.utils.task;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradePay;
import com.ftrend.zgp.model.TradePay_Table;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.TradeProd_Table;
import com.ftrend.zgp.model.TradeUploadQueue;
import com.ftrend.zgp.model.Trade_Table;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.db.ZgpDb;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 流水下载线程
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/6
 */
public class LsDownloadTask {
    private final String TAG = "LsDownloadTask";

    // 进度消息处理器
    private DataDownloadTask.ProgressHandler handler;

    // 是否已强制终止执行
    private volatile boolean interrupted = false;
    // 流水号列表
    private List<String> lsList = new ArrayList<>();
    // 当前步骤索引
    private int step = -1;
    // 当前步骤已重试次数
    private int retryCount = 0;
    // 最大重试次数
    private final int MAX_RETRY = 3;

    public LsDownloadTask(DataDownloadTask.ProgressHandler handler) {
        this.handler = handler;
    }

    /**
     * 开始执行数据下载任务
     */
    public void start() {
        step = -1;
        queryLsList();
    }

    /**
     * 终止数据下载
     */
    public void interrupt() {
        this.interrupted = true;
    }

    /**
     * 执行下一项更新
     */
    private void next() {
        postProgress();
        step++;
        retryCount = 0;
        exec();
    }

    /**
     * 重试当前更新，超过重试次数时，任务执行失败
     *
     * @param err
     */
    private void retry(String err) {
        retryCount++;
        if (retryCount > MAX_RETRY) {
            // TODO: 2019/9/4 优化：数据下载达到最大重试次数的提示消息
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
        if (step == -1) {
            queryLsList();
            return;
        }
        if (step >= lsList.size()) {
            //更新结束
            postFinished();
            return;
        }

        downloadLs(lsList.get(step));
    }

    /**
     * 推送下载进度消息
     */
    private void postProgress() {
        int percent = step * 100 / lsList.size();
        handler.handleProgress(percent, false, "");
    }

    /**
     * 推送下载完成消息
     */
    private void postFinished() {
        handler.handleProgress(100, false, "流水下载完成");
    }

    /**
     * 推送下载失败消息
     *
     * @param msg
     */
    private void postFailed(String msg) {
        int percent = step * 100 / lsList.size();
        handler.handleProgress(percent, true, msg);
    }

    /**
     * 查询待下载的流水号
     */
    private void queryLsList() {
        RestSubscribe.getInstance().queryPosLsList(ZgParams.getPosCode(), new RestCallback(new RestResultHandler() {
            @Override
            public void onSuccess(Map<String, Object> body) {
                lsList.clear();
                List<String> list = (List<String>) body.get("list");
                if (list != null && list.size() > 0) {
                    lsList.addAll(list);
                    next();
                } else {
                    postFinished();//没有可下载流水
                }
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                Log.w(TAG, "查询待下载的流水号发生错误: " + errorCode + " - " + errorMsg);
                retry("查询待下载的流水号发生错误");
            }
        }));
    }

    /**
     * 下载流水
     *
     * @param lsNo 流水号
     */
    private void downloadLs(final String lsNo) {
        RestSubscribe.getInstance().downloadPosLs(ZgParams.getPosCode(), lsNo, new RestCallback(new RestResultHandler() {
            @Override
            public void onSuccess(Map<String, Object> body) {
                if (!body.containsKey("trade") || !body.containsKey("prod") || !body.containsKey("pay")) {
                    next();//后台服务返回的数据无效，跳过
                    return;
                }

                Map<String, Object> trade = (Map<String, Object>) body.get("trade");
                List<Map<String, Object>> prod = (List<Map<String, Object>>) body.get("prod");
                Map<String, Object> pay = (Map<String, Object>) body.get("pay");
                saveLs(trade, prod, pay);
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                Log.w(TAG, "下载流水失败: " + errorCode + " - " + errorMsg);
                retry("下载流水失败，流水号：" + lsNo);
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
                next();//流水保存成功，继续下载下一条流水
            }
        }).error(new Transaction.Error() {
            @Override
            public void onError(@NonNull Transaction transaction, @NonNull Throwable error) {
                Log.d(TAG, "流水保存失败：" + error.getLocalizedMessage());
                retry("流水保存失败");//流水保存失败，重新下载
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
        //添加上传队列（状态为：已上传），避免重复上传
        saveQueue(trade);
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
        SQLite.delete(TradePay.class)
                .where(TradePay_Table.lsNo.eq(pay.getLsNo()))
                .execute();
        pay.insert();
    }

    /**
     * 添加上传队列，并设置为已上传
     *
     * @param trade
     */
    private void saveQueue(Trade trade) {
        TradeUploadQueue queue = new TradeUploadQueue();
        queue.setDepCode(trade.getDepCode());
        queue.setLsNo(trade.getLsNo());
        queue.setEnqueueTime(trade.getTradeTime());
        queue.setUploadTime(new Date());
        queue.insert();
    }

}
