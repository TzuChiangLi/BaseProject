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
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.db.ZgpDb;
import com.ftrend.zgp.utils.http.RestBodyMap;
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
import java.util.List;

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

    // 线程是否正在运行
    private volatile boolean running = false;
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

    // 线程唯一实例，避免重复运行
    private static LsDownloadTask task = null;

    /**
     * 启动线程
     *
     * @param handler
     * @return
     */
    public static boolean taskStart(DataDownloadTask.ProgressHandler handler) {
        if (task != null && task.running) {
            return false;
        }
        task = new LsDownloadTask(handler);
        task.start();
        return true;
    }

    /**
     * 停止线程
     */
    public static void taskCancel() {
        if (task != null) {
            task.interrupt();
        }
    }

    private LsDownloadTask(DataDownloadTask.ProgressHandler handler) {
        this.handler = handler;
    }

    /**
     * 开始执行数据下载任务
     */
    private void start() {
        running = true;
        step = -1;
        queryLsList();
    }

    /**
     * 终止数据下载
     */
    private void interrupt() {
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
        running = false;
        handler.handleProgress(100, false, "流水下载完成");
    }

    /**
     * 推送下载失败消息
     *
     * @param msg
     */
    private void postFailed(String msg) {
        running = false;
        int percent = lsList.size() > 0 ? step * 100 / lsList.size() : 0;
        handler.handleProgress(percent, true, msg);
    }

    /**
     * 查询待下载的流水号
     */
    private void queryLsList() {
        RestSubscribe.getInstance().queryPosLsList(ZgParams.getPosCode(), new RestCallback(new RestResultHandler() {
            @Override
            public void onSuccess(RestBodyMap body) {
                lsList.clear();
                List<String> list = body.getStringList("list");
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
            public void onSuccess(RestBodyMap body) {
                if (!body.containsKey("trade") || !body.containsKey("prod") || !body.containsKey("pay")) {
                    next();//后台服务返回的数据无效，跳过
                    return;
                }

                RestBodyMap trade = body.getMap("trade");
                List<RestBodyMap> prod = body.getMapList("prod");
                RestBodyMap pay = body.getMap("pay");
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
    private void saveLs(final RestBodyMap trade, final List<RestBodyMap> prod, final RestBodyMap pay) {
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
    private long saveTrade(Gson gson, RestBodyMap values) {
        Trade trade = gson.fromJson(gson.toJson(values), Trade.class);
        SQLite.delete(Trade.class)
                .where(Trade_Table.lsNo.eq(trade.getLsNo()))
                .execute();
        trade.setStatus(TradeHelper.TRADE_STATUS_PAID);
        trade.setCreateTime(trade.getTradeTime());
        trade.setCreateIp(ZgParams.getCurrentIp());
        return trade.insert();
        //添加上传队列（状态为：已上传），避免重复上传
    }

    /**
     * 保存商品列表
     *
     * @param values
     */
    private boolean saveProd(Gson gson, List<RestBodyMap> values) {
        if (values.size() == 0) {
            return false;
        }
        List<TradeProd> prodList = new ArrayList<>();
        for (RestBodyMap map : values) {
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
        int result = 0;
        for (TradeProd prod : prodList) {
            result += prod.insert();
        }
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存支付信息
     *
     * @param values
     */
    private long savePay(Gson gson, RestBodyMap values) {
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
        return pay.insert();
    }


}
