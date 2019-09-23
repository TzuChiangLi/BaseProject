package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.task.DataDownloadTask;
import com.ftrend.zgp.utils.task.LsDownloadTask;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;
import java.util.Locale;


/**
 * @author liziqiang@ftrend.cn
 */
public class InitPresenter implements Contract.InitPresenter {
    private Contract.InitView mView;
    private boolean isStart = false;

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
        isStart = true;
        if (step == 1) {
            new DataDownloadTask(true, new DataDownloadTask.ProgressHandler() {
                @Override
                public void handleProgress(int percent, boolean isFailed, String msg) {
//                if (isStart) {
                    mView.updateProgress(1, percent);
                    System.out.println(String.format(Locale.getDefault(), "基础数据下载进度：%d%% %s", percent, msg));
//                }
                }
            }).start();
        } else if (step == 2) {
            new LsDownloadTask(new DataDownloadTask.ProgressHandler() {
                @Override
                public void handleProgress(int percent, boolean isFailed, String msg) {
//                if (isStart) {
                    mView.updateProgress(2, percent);
                    System.out.println(String.format(Locale.getDefault(), "实时流水下载进度：%d%% %s", percent, msg));
//                }
                }
            }).start();
        }
    }


    @Override
    public void stopInitData() {
        isStart = false;
        mView.stopUpdate();
    }

    @Override
    public void finishInitData() {
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


//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (isStart)
//                        mView.updateProgress(0);
//                }
//            }, 200);
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (isStart)
//                        mView.updateProgress(20);
//                }
//            }, 1000);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (isStart)
//                        mView.updateProgress(50);
//                }
//            }, 2000);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (isStart)
//                        mView.updateProgress(90);
//                }
//            }, 3000);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (isStart)
//                    mView.updateProgress(100);
//            }
//        }, 5000);
}
