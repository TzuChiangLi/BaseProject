package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.AppParams;
import com.ftrend.zgp.model.AppParams_Table;
import com.ftrend.zgp.utils.HandoverHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;
import java.util.Map;

/**
 * @author liziqiang@ftrend.cn
 */

public class HandoverPresenter implements Contract.HandoverPresenter {
    private Contract.HandoverView mView;

    private HandoverPresenter(Contract.HandoverView mView) {
        this.mView = mView;
    }

    public static HandoverPresenter createPresenter(Contract.HandoverView mView) {
        return new HandoverPresenter(mView);
    }

    @Override
    public void initView() {
        HandoverHelper.initHandover();
        HandoverHelper.handoverSum();
        mView.showHandoverRecord(HandoverHelper.getRecordList());
    }

    @Override
    public void doHandover() {
        if (ZgParams.isIsOnline()) {
            MessageUtil.waitBegin("交班处理中，请稍候...", new MessageUtil.MessageBoxCancelListener() {
                @Override
                public boolean onCancel() {
                    return false;//交班操作暂时无法取消
                }
            });
            if (!HandoverHelper.save()) {
                mView.showError();
                return;
            }
            RestSubscribe.getInstance().posEnd(ZgParams.getPosCode(), new RestCallback(new RestResultHandler() {

                @Override
                public void onSuccess(Map<String, Object> body) {
                    HandoverHelper.finish();
                    MessageUtil.waitEnd();
                    mView.showSuccess();
                }

                @Override
                public void onFailed(String errorCode, String errorMsg) {
                    mView.showError();
                }
            }));
            // 上传APP配置参数，失败不影响交班结果（只上传必要的参数）
            List<AppParams> appParamsList = SQLite.select().from(AppParams.class)
                    .where(AppParams_Table.paramName.in("printerConfig", "lastDep", "lastUser"))
                    .queryList();
            RestSubscribe.getInstance().uploadAppParams(ZgParams.getPosCode(), appParamsList,
                    new RestCallback(new RestResultHandler() {
                        @Override
                        public void onSuccess(Map<String, Object> body) {
                            // 无需处理上传结果
                        }

                        @Override
                        public void onFailed(String errorCode, String errorMsg) {
                            // 无需处理上传结果
                        }
                    }));
        } else {
            mView.showOfflineTip();
        }
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }
}
