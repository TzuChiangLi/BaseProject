package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.AppParams;
import com.ftrend.zgp.model.AppParams_Table;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.HttpCallBack;
import com.ftrend.zgp.utils.task.DataDownloadTask;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;
import java.util.Locale;


/**
 * @author liziqiang@ftrend.cn
 */
public class InitPresenter implements Contract.InitPresenter, HttpCallBack {
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
    public void startInitData() {
        isStart = true;
        new DataDownloadTask(true, new DataDownloadTask.ProgressHandler() {
            @Override
            public void handleProgress(int percent, boolean isFailed, String msg) {
//                if (isStart) {
                    mView.updateProgress(percent);
                    System.out.println(String.format(Locale.getDefault(), "%d%% %s", percent, msg));
//                }
            }
        }).start();

    }


    @Override
    public void stopInitData() {
        isStart = false;
        mView.stopUpdate();
    }

    @Override
    public void finishInitData() {
        String posCode = SQLite.select(AppParams_Table.paramValue).from(AppParams.class).where(AppParams_Table.paramName.eq("posCode")).querySingle().getParamValue();
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

        mView.finishUpdate(posCode, depStr.toString(), userStr.toString());
        mView.finishUpdate(null, null, null);
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onSuccess(Object body) {

    }

    @Override
    public void onFailed(String errorCode, String errorMsg) {

    }

    @Override
    public void onHttpError(int errorCode, String errorMsg) {

    }

    @Override
    public void onFinish() {

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
