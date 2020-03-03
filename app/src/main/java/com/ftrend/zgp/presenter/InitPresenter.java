package com.ftrend.zgp.presenter;

import com.blankj.utilcode.util.ActivityUtils;
import com.ftrend.zgp.App;
import com.ftrend.zgp.api.InitContract;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.CommonUtil;
import com.ftrend.zgp.utils.http.RestBodyMap;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.task.DataDownloadTask;
import com.ftrend.zgp.utils.task.LsDownloadTask;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;
import java.util.Locale;


/**
 * @author liziqiang@ftrend.cn
 */
public class InitPresenter implements InitContract.InitPresenter {
    private static final String TAG = "InitPresenter";
    private InitContract.InitView mView;
    private boolean flag = true;

    private InitPresenter(InitContract.InitView mView) {
        this.mView = mView;
    }

    public static InitPresenter createPresenter(InitContract.InitView mView) {
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
                        LogUtil.d("----1");
                    }
                }
            });
        } else if (step == 2) {
            LsDownloadTask.taskStart(new DataDownloadTask.ProgressHandler() {
                @Override
                public void handleProgress(int percent, boolean isFailed, String msg) {
                    if (flag) {
                        if (mView != null) {
                            mView.updateProgress(2, percent);
                        }
                    }
                    System.out.println(String.format(Locale.getDefault(), "实时流水下载进度：%d%% %s", percent, msg));
                    if (isFailed) {
                        //失败退出
                        initFailed();
                        LogUtil.d("----2");
                    }
                }
            });
        } else if (step == 3) {
            RestSubscribe.getInstance().queryAppParams(ZgParams.getPosCode(),
                    new RestCallback(new RestResultHandler() {
                        @Override
                        public void onSuccess(RestBodyMap body) {
                            for (String key : body.keySet()) {
                                ZgParams.saveAppParams(key, body.getString(key));
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
    public void finishInitData() {
        List<Dep> depList = SQLite.select().distinct().from(Dep.class).queryList();
        List<User> userList = SQLite.select().distinct().from(User.class).queryList();
        if (depList.size() == 0 || userList.size() == 0) {
            MessageUtil.error("请在后台配置可登录专柜和用户！", new MessageUtil.MessageBoxOkListener() {
                @Override
                public void onOk() {
                    ActivityUtils.finishAllActivities();
                    CommonUtil.rebootApp(App.getContext());
                }
            });
            return;
        }
        //设置初始化完成标志
        ZgParams.updateInitFlag();
        // 数据初始化完成，重新加载参数
        ZgParams.loadParams();
        //加载用户数据
        StringBuilder userStr = new StringBuilder();
        for (User user : userList) {
            userStr.append(user.getUserCode()).append(" ").append(user.getUserName()).append("\n");
        }

        if (ZgParams.getUseDep()) {
            StringBuilder depStr = new StringBuilder();
            String depCode;
            for (Dep dep : depList) {
                depCode = dep.getDepCode();
                depCode = depCode.contains("-") ? depCode.substring(0, depCode.indexOf("-")) : depCode;
                depStr.append(depCode).append(" ").append(dep.getDepName()).append("\n");
            }
            mView.finishUpdate(ZgParams.getPosCode() + "\n", depStr.toString(), userStr.toString());
        } else {
            mView.finishUpdate(ZgParams.getPosCode() + "\n", "", userStr.toString());
        }
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
        try {
            DataDownloadTask.taskCancel();
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
    }
}
