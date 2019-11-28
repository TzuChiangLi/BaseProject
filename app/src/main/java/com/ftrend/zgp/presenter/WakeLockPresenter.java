package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.WakeLockContract;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.model.User_Table;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.EncryptUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * @author liziqiang@ftrend.cn
 */
public class WakeLockPresenter implements WakeLockContract.WakePresenter {
    private WakeLockContract.WakeLockView mView;

    private WakeLockPresenter(WakeLockContract.WakeLockView mView) {
        this.mView = mView;
    }

    public static WakeLockPresenter createPresenter(WakeLockContract.WakeLockView mView) {
        return new WakeLockPresenter(mView);
    }

    @Override
    public void enter(String pwd) {
        User user = SQLite.select().from(User.class)
                .where(User_Table.userCode.eq(ZgParams.getCurrentUser().getUserCode()))
                .querySingle();
        if (user != null) {
            if (pwd.equals(EncryptUtil.pwdDecrypt(user.getUserPwd()))) {
                mView.success();
            } else {
                mView.show("密码不正确");
            }
        } else {
            mView.show("本地数据不存在，请尝试重新安装本程序");
        }
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }
}
