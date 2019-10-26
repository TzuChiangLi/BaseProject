package com.ftrend.zgp.utils.task;

import android.util.Log;

import com.ftrend.zgp.model.SqbPayOrder;
import com.ftrend.zgp.model.SqbPayOrder_Table;
import com.ftrend.zgp.model.SqbPayResult;
import com.ftrend.zgp.model.SqbPayResult_Table;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradePay;
import com.ftrend.zgp.model.TradePay_Table;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.TradeProd_Table;
import com.ftrend.zgp.model.TradeUploadQueue;
import com.ftrend.zgp.model.TradeUploadQueue_Table;
import com.ftrend.zgp.model.Trade_Table;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 流水上传线程
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/6
 */
public class LsUploadThread extends Thread {
    private final String TAG = "LsUploadThread";

    //是否正在上传数据，防止重复调用
    private boolean isUploading = false;

    public void run() {
        super.run();

        String posCode = ZgParams.getPosCode();
        while (!isInterrupted()) {
            List<TradeUploadQueue> list = SQLite.select().from(TradeUploadQueue.class)
                    .where(TradeUploadQueue_Table.uploadTime.isNull())
                    .queryList();
            if (list.size() == 0 || !ZgParams.isIsOnline()) {
                //没有需要上传的数据或者单机模式，等待一段时间
                Log.e(TAG, "没有需要上传的数据或者单机模式，10秒钟后继续");
                try {
                    Thread.sleep(1000 * 10);
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }

            for (final TradeUploadQueue queue : list) {
                if (isInterrupted()) {
                    break;//线程终止，停止上传
                }

                String lsNo = queue.getLsNo();
                Trade trade = SQLite.select().from(Trade.class)
                        .where(Trade_Table.lsNo.eq(lsNo))
                        .querySingle();
                List<TradeProd> prodList = SQLite.select().from(TradeProd.class)
                        .where(TradeProd_Table.lsNo.eq(lsNo))
                        .and(TradeProd_Table.delFlag.eq("0"))//行清商品不上传
                        .queryList();
                TradePay pay = SQLite.select().from(TradePay.class)
                        .where(TradePay_Table.lsNo.eq(lsNo))
                        .querySingle();
                // 如果流水号无效（流水信息不存在），直接从队列删除
                if (trade == null || prodList.size() == 0 || pay == null) {
                    Log.e(TAG, "流水号无效（流水信息不存在），直接从队列删除：" + lsNo);
                    queue.delete();
                    continue;
                }

                isUploading = true;
                RestSubscribe.getInstance().uploadTrade(posCode, trade, prodList, pay,
                        new RestCallback(new RestResultHandler() {
                            @Override
                            public void onSuccess(Map<String, Object> body) {
                                //上传收钱吧交易记录
                                uploadSqb(queue);
                            }

                            @Override
                            public void onFailed(String errorCode, String errorMsg) {
                                Log.e(TAG, "流水上传失败: " + errorCode + " - " + errorMsg);
                                isUploading = false;//上传失败不做处理，下次再上传
                            }
                        }));

                while (isUploading) {
                    //等待上传完成
                    yield();
                    if (isInterrupted()) {
                        break;//线程终止，停止等待
                        //此时退出可能导致流水重复上传（未标记已上传），后台服务有对应的处理机制
                    }
                }
            }
        }
    }

    /**
     * 上传对应的收钱吧交易记录
     *
     * @param queue
     */
    private void uploadSqb(final TradeUploadQueue queue) {
        List<SqbPayOrder> orderList = SQLite.select().from(SqbPayOrder.class)
                .where(SqbPayOrder_Table.lsNo.eq(queue.getLsNo()))
                .queryList();
        if (orderList.size() == 0) {
            //没有收钱吧交易记录，流水上传完毕
            setUploaded(queue);
        }

        final int total = orderList.size();
        final int[] uploadCount = {0};
        for (SqbPayOrder order : orderList) {
            if (!isUploading || isInterrupted()) {
                break;
            }
            SqbPayResult result = SQLite.select().from(SqbPayResult.class)
                    .where(SqbPayResult_Table.requestNo.eq(order.getRequestNo()))
                    .querySingle();
            if (result == null) {
                //没有对应的交易结果，该请求可能没有发送出去，无需上传
                uploadCount[0]++;
                if (uploadCount[0] >= total) {
                    //流水上传完毕
                    setUploaded(queue);
                    break;
                }
                continue;
            }
            RestSubscribe.getInstance().uploadSqb(order, result,
                    new RestCallback(new RestResultHandler() {
                        @Override
                        public void onSuccess(Map<String, Object> body) {
                            uploadCount[0]++;
                            if (uploadCount[0] >= total) {
                                //流水上传完毕
                                setUploaded(queue);
                            }
                        }

                        @Override
                        public void onFailed(String errorCode, String errorMsg) {
                            Log.e(TAG, "收钱吧交易记录上传失败: " + errorCode + " - " + errorMsg);
                            isUploading = false;//上传失败不做处理，下次再上传
                        }
                    }));
        }
    }

    /**
     * 设置当前流水上传完毕
     *
     * @param queue
     */
    private void setUploaded(final TradeUploadQueue queue) {
        //记录上传时间
        queue.setUploadTime(new Date());
        queue.update();
        isUploading = false;
    }

}
