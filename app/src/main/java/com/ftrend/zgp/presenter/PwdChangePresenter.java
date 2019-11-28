package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.PwdChangeContract;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.model.User_Table;
import com.ftrend.zgp.utils.RtnHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.UserRightsHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.EncryptUtil;
import com.ftrend.zgp.utils.http.RestBodyMap;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.log.LogUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * @author liziqiang@ftrend.cn
 */

public class PwdChangePresenter implements PwdChangeContract.PwdChangePresenter {
    private PwdChangeContract.PwdChangeView mView;

    private PwdChangePresenter(PwdChangeContract.PwdChangeView mView) {
        this.mView = mView;
    }

    public static PwdChangePresenter createPresenter(PwdChangeContract.PwdChangeView mView) {
        return new PwdChangePresenter(mView);
    }


    @Override
    public void modify(String old, String newPwd, String confirm) {
        //先检测旧密码是否正确
        final User user = SQLite.select().from(User.class).where(User_Table.userCode.eq(ZgParams.getCurrentUser().getUserCode()))
                .querySingle();
        if (user == null) {
            LogUtil.d("----该用户不存在，请尝试更新数据");
            mView.show("该用户不存在，请尝试更新数据");
            return;
        }

        if (old.equals(EncryptUtil.pwdDecrypt(user.getUserPwd()))) {
            if (newPwd.equals(confirm)) {
                //两次输入的密码一致则调用接口成功
                LogUtil.d("----调用接口");
                RestSubscribe.getInstance().userChangePwd(ZgParams.getCurrentUser().getUserCode(),
                        old, newPwd, new RestCallback(new RestResultHandler() {
                            @Override
                            public void onSuccess(RestBodyMap body) {
                                user.setUserPwd(body.get("pwd").toString());
                                if (UserRightsHelper.changeUserPwd(user)) {
                                    //注销操作
                                    ZgParams.clearCurrentInfo();
                                    //清除数据
                                    TradeHelper.clear();
                                    RtnHelper.clearAllData();
                                    mView.showSuccess("修改成功\n请重新登录");
                                } else {
                                    mView.showError("数据写入失败");
                                }
                            }

                            @Override
                            public void onFailed(String errorCode, String errorMsg) {
                                LogUtil.e("----err:" + errorMsg);
                                mView.showError(String.format("%s(%s)", errorMsg, errorCode));
                            }
                        }));

            } else {
                mView.show("两次输入的新密码不一致，请重新输入");
            }
        } else {
            mView.show("用户信息验证失败");
        }
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }
}
