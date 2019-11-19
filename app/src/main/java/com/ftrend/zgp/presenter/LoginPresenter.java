package com.ftrend.zgp.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.ftrend.zgp.api.LoginContract;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.Dep_Table;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.model.User_Table;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.EncryptUtill;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * 登录功能P层
 *
 * @author liziqiang@ftrend.cn
 */
public class LoginPresenter implements LoginContract.LoginPresenter {
    private LoginContract.LoginView mView;


    private LoginPresenter(LoginContract.LoginView mView) {
        this.mView = mView;
    }

    public static LoginPresenter createPresenter(LoginContract.LoginView mView) {
        return new LoginPresenter(mView);
    }


    @Override
    public void initDepData(Context context) {
        List<Dep> depList = SQLite.select().from(Dep.class).queryList();
        String depCode = ZgParams.getLastDep();
        if (!TextUtils.isEmpty(depCode)) {
            for (int i = 0; i < depList.size(); i++) {
                if (depCode.equals(depList.get(i).getDepCode())) {
                    Dep dep = depList.get(i);
                    depList.remove(i);
                    depList.add(0, dep);
                }
            }
        }
        mView.setDepData(depList);
    }

    @Override
    public void initUserData() {
        List<User> userList = SQLite.select().from(User.class).queryList();
        String userCode = ZgParams.getLastUser();
        if (!TextUtils.isEmpty(userCode)) {
            for (int i = 0; i < userList.size(); i++) {
                if (userCode.equals(userList.get(i).getUserCode())) {
                    User user = userList.get(i);
                    userList.remove(i);
                    userList.add(0, user);
                }
            }
        }
        mView.setUserData(userList);
    }


    @Override
    public void checkUserInfo(String userCode, String userPwd, String depCode) {
        if (!TextUtils.isEmpty(userCode)) {
            User user = SQLite.select().from(User.class).where(User_Table.userCode.eq(userCode)).querySingle();
            Dep dep = SQLite.select().from(Dep.class).where(Dep_Table.depCode.eq(depCode)).querySingle();
            if (user != null) {
                if (userPwd.equals(EncryptUtill.pwdDecrypt(user.getUserPwd()))) {
                    mView.loginSuccess(user, dep);
                    //保存静态变量
                    ZgParams.saveCurrentInfo(user, dep);
                } else {
                    mView.loginFailed("用户名或密码错误\n请重试！");
                }
            }
        }
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }


}
