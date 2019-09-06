package com.ftrend.zgp.utils.task;

import android.util.Log;

import com.ftrend.zgp.model.SysParams;
import com.ftrend.zgp.model.SysParams_Table;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据下载线程
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/4
 */
public class DataDownloadTask {
    private final String TAG = "DataDownloadTask";

    // 进度消息处理器
    private ProgressHandler handler;
    // 是否强制更新
    private boolean isForce;

    // 是否已强制终止执行
    private volatile boolean interrupted = false;
    // 数据更新标志列表，强制更新时不做比对
    private List<UpdateInfo> updateInfoList = new ArrayList<>();
    // 当前步骤索引
    private int step = -1;
    // 当前步骤已重试次数
    private int retryCount = 0;
    // 最大重试次数
    private final int MAX_RETRY = 3;

    public DataDownloadTask(boolean isForce, ProgressHandler handler) {
        this.handler = handler;
        this.isForce = isForce;
    }

    /**
     * 开始执行数据下载任务
     */
    public void start() {
        step = -1;
        checkUpdateSign();
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
        if (step >= updateInfoList.size()) {
            //更新结束
            postFinished();
            return;
        }
        UpdateInfo info = updateInfoList.get(step);
        if (!isForce && !info.needUpdate()) {
            //无需更新，跳过
            next();
            return;
        }

        if (info.isPosDep()) {
            //2 可登录专柜
            RestSubscribe.getInstance().updatePosDep(info.getCode(), makeCallback(info));
        } else if (info.isPosUser()) {
            //3 可登录用户
            RestSubscribe.getInstance().updatePosUser(info.getCode(), makeCallback(info));
        } else if (info.isPosSysParams()) {
            //4 系统参数
            RestSubscribe.getInstance().updatePosSysParams(info.getCode(), makeCallback(info));
        } else if (info.isDepCls()) {
            //5 专柜商品类别
            RestSubscribe.getInstance().updateDepCls(info.getCode(), makeCallback(info));
        } else if (info.isDepProduct()) {
            //6 专柜商品
            RestSubscribe.getInstance().updateDepProduct(info.getCode(), makeCallback(info));
        } else if (info.isDepPayInfo()) {
            //7 专柜支付方式
            RestSubscribe.getInstance().updateDepPayInfo(info.getCode(), makeCallback(info));
        } else {
            //不支持的数据类型，跳过
            next();
        }
    }

    /**
     * 推送更新进度消息
     */
    private void postProgress() {
        int percent = step * 100 / updateInfoList.size();
        handler.handleProgress(percent, false, "");
    }

    /**
     * 推送更新完成消息（全部更新成功）
     */
    private void postFinished() {
        handler.handleProgress(100, false, "数据更新完成");
    }

    /**
     * 推送更新失败消息
     *
     * @param msg
     */
    private void postFailed(String msg) {
        int percent = step * 100 / updateInfoList.size();
        handler.handleProgress(percent, true, msg);
    }

    /**
     * 1 检查数据更新标志
     */
    private void checkUpdateSign() {
        RestSubscribe.getInstance().checkPosUpdate(ZgParams.getPosCode(), new RestCallback(new RestResultHandler() {
            @Override
            public void onSuccess(Map<String, Object> body) {
                updateInfoList.clear();
                List<Map<String, Object>> list = (List<Map<String, Object>>) body.get("list");
                for (Map<String, Object> map : list) {
                    String key = String.valueOf(map.get("paramName"));
                    String sign = String.valueOf(map.get("paramValue"));
                    updateInfoList.add(new UpdateInfo(key, sign));
                }
                next();
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                Log.w(TAG, "检查数据更新标志发生错误: " + errorCode + " - " + errorMsg);
                retry("检查数据更新标志发生错误");
            }
        }));
    }

    //数据更新结果处理器
    private DataDownloadHelper.DownloadResultHandler downloadResultHandler =
            new DataDownloadHelper.DownloadResultHandler() {
                @Override
                public void onSuccess() {
                    next();
                }

                @Override
                public void onError(String msg) {
                    retry(msg);
                }
            };

    /**
     * 创建后台服务请求回调
     *
     * @param info
     * @return
     */
    private RestCallback makeCallback(final UpdateInfo info) {
        return DataDownloadHelper.makeCallback(info.dataType, info.code, downloadResultHandler);
    }

    /**
     * 进度消息处理器
     */
    public interface ProgressHandler {
        void handleProgress(int percent, boolean isFailed, String msg);
    }

    /**
     * 待更新数据信息
     */
    private class UpdateInfo {
        //数据标志
        private String key;
        //机器号或专柜号，通过数据类型确定
        private String code;
        //数据类型代码
        private String dataType;
        //数据更新标志
        private String dataSign;

        /**
         * @param key      数据标志，如：101_DEP
         * @param dataSign 数据更新标志
         */
        UpdateInfo(String key, String dataSign) {
            this.key = key;
            String[] values = key.split("_");
            if (values.length == 2) {
                this.code = values[0];
                this.dataType = values[1];
            }
            this.dataSign = dataSign;
        }

        public String toString() {
            return key + " = " + dataSign;
        }

        String getKey() {
            return key;
        }

        String getCode() {
            return code;
        }

        String getDataSign() {
            return dataSign;
        }

        boolean isDepCls() {
            return DataDownloadHelper.isDepCls(this.dataType);
        }

        boolean isDepProduct() {
            return DataDownloadHelper.isDepProduct(this.dataType);
        }

        boolean isDepPayInfo() {
            return DataDownloadHelper.isDepPayInfo(this.dataType);
        }

        boolean isPosSysParams() {
            return DataDownloadHelper.isPosSysParams(this.dataType);
        }

        boolean isPosDep() {
            return DataDownloadHelper.isPosDep(this.dataType);
        }

        boolean isPosUser() {
            return DataDownloadHelper.isPosUser(this.dataType);
        }

        /**
         * 判断当前数据是否需要更新
         *
         * @return
         */
        boolean needUpdate() {
            SysParams params = SQLite.select().from(SysParams.class)
                    .where(SysParams_Table.paramName.eq(key))
                    .querySingle();
            return params == null || !dataSign.equals(params.getParamValue());
        }
    }


}
