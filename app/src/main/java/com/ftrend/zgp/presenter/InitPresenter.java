package com.ftrend.zgp.presenter;

import com.ftrend.zgp.App;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.CommonUtil;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.task.DataDownloadTask;
import com.ftrend.zgp.utils.task.LsDownloadTask;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * @author liziqiang@ftrend.cn
 */
public class InitPresenter implements Contract.InitPresenter {
    private Contract.InitView mView;
    private boolean flag = true;

    private InitPresenter(Contract.InitView mView) {
        this.mView = mView;
    }

    public static InitPresenter createPresenter(Contract.InitView mView) {
        return new InitPresenter(mView);
    }

    @Override
    public void startAnimator() {
        mView.startUpdate();
    }

    @Override
    public void startInitData(int step) {
        flag = true;
        if (step == 1) {
            DataDownloadTask.taskStart(true, new DataDownloadTask.ProgressHandler() {
                @Override
                public void handleProgress(int percent, boolean isFailed, String msg) {
                    if (flag) {
                        mView.updateProgress(1, percent);
                    }
                    System.out.println(String.format(Locale.getDefault(), "基础数据下载进度：%d%% %s", percent, msg));
                    if (isFailed) {
                        //失败退出
                        initFailed();
                    }
                }
            });
        } else if (step == 2) {
            LsDownloadTask.taskStart(new DataDownloadTask.ProgressHandler() {
                @Override
                public void handleProgress(int percent, boolean isFailed, String msg) {
                    if (flag) {
                        mView.updateProgress(2, percent);
                    }
                    System.out.println(String.format(Locale.getDefault(), "实时流水下载进度：%d%% %s", percent, msg));
                    if (isFailed) {
                        //失败退出
                        initFailed();
                    }
                }
            });
        } else if (step == 3) {
            RestSubscribe.getInstance().queryAppParams(ZgParams.getPosCode(),
                    new RestCallback(new RestResultHandler() {
                        @Override
                        public void onSuccess(Map<String, Object> body) {
                            for (String key : body.keySet()) {
                                ZgParams.saveAppParams(key, String.valueOf(body.get(key)));
                            }
                            ZgParams.loadParams();
                            mView.updateProgress(3, 100);
                        }

                        @Override
                        public void onFailed(String errorCode, String errorMsg) {
                            //失败退出
                            initFailed();
                        }
                    }));
        }
    }

    /**
     * 初始化失败
     */
    private void initFailed() {
        MessageUtil.error("初始化失败。请确认网络畅通后，重启应用继续初始化操作！",
                new MessageUtil.MessageBoxOkListener() {
                    @Override
                    public void onOk() {
                        CommonUtil.rebootApp(App.getContext());
                    }
                });
    }

    @Override
    public void stopInitData() {
        DataDownloadTask.taskCancel();
        LsDownloadTask.taskCancel();
        TradeHelper.rollbackInitTask();
        flag = false;
        mView.stopUpdate();
    }

    @Override
    public void finishInitData() {
        //设置初始化完成标志
        ZgParams.updateInitFlag();
        // 数据初始化完成，重新加载参数
        ZgParams.loadParams();

        List<Dep> depList = SQLite.select().distinct().from(Dep.class).queryList();
        StringBuilder depStr = new StringBuilder();
        for (Dep dep : depList) {
            depStr.append(dep.getDepCode()).append(" ").append(dep.getDepName()).append("\n");
        }
        List<User> userList = SQLite.select().distinct().from(User.class).queryList();
        StringBuilder userStr = new StringBuilder();
        for (User user : userList) {
            userStr.append(user.getUserCode()).append(" ").append(user.getUserName()).append("\n");
        }
        mView.finishUpdate(ZgParams.getPosCode() + "\n", depStr.toString(), userStr.toString());
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }
}
