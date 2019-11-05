package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.utils.HandoverHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.msg.MessageUtil;

import java.util.Locale;
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
                MessageUtil.waitError("交班失败！", null);
                return;
            }
            RestSubscribe.getInstance().posEnd(ZgParams.getPosCode(), new RestCallback(new RestResultHandler() {

                @Override
                public void onSuccess(Map<String, Object> body) {
                    HandoverHelper.finish();
                    MessageUtil.waitSuccesss("交班成功！", new MessageUtil.MessageBoxOkListener() {
                        @Override
                        public void onOk() {
                            mView.success();
                        }
                    });
                }

                @Override
                public void onFailed(String errorCode, String errorMsg) {
                    MessageUtil.waitError(String.format(Locale.CHINA, "%s - %s", errorCode, errorMsg), null);
                }
            }));
            // 上传APP配置参数，失败不影响交班结果
            HandoverHelper.uploadAppParams();
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
