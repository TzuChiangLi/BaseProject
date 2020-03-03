package com.ftrend.zgp.presenter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

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
    private AnimatorSet alphaSet = null;
    private AnimatorSet translationSet = null;

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
    public void start(View... views) {
        /*logo渐渐显示后开始下一步的动画*/
        if (alphaSet == null || translationSet == null) {
            Float Height = 25f;
            alphaSet = new AnimatorSet();
            translationSet = new AnimatorSet();

            ObjectAnimator alpha_title = ObjectAnimator.ofFloat(views[0], "alpha", 0, 1);
            ObjectAnimator alpha_dep = ObjectAnimator.ofFloat(views[1], "alpha", 0, 1);
            ObjectAnimator alpha_cashier = ObjectAnimator.ofFloat(views[2], "alpha", 0, 1);
            ObjectAnimator alpha_pwd = ObjectAnimator.ofFloat(views[3], "alpha", 0, 1);
            ObjectAnimator alpha_btn = ObjectAnimator.ofFloat(views[4], "alpha", 0, 1);

            ObjectAnimator translationY_title = ObjectAnimator.ofFloat(views[0], "translationY", 0, -Height, -Height - 10, 0);
            ObjectAnimator translationY_dep = ObjectAnimator.ofFloat(views[1], "translationY", 0, -Height, -Height - 10, 0);
            ObjectAnimator translationY_cashier = ObjectAnimator.ofFloat(views[2], "translationY", 0, -Height, -Height - 10, 0);
            ObjectAnimator translationY_pwd = ObjectAnimator.ofFloat(views[3], "translationY", 0, -Height, -Height - 10, 0);
            ObjectAnimator translationY_btn = ObjectAnimator.ofFloat(views[4], "translationY", 0, -Height, -Height - 10, 0);

            alphaSet.playTogether(alpha_title, alpha_dep, alpha_cashier,
                    alpha_pwd, alpha_btn);
            alphaSet.setDuration(1200);
            translationY_title.setDuration(600);
            translationY_dep.setDuration(600);
            translationY_cashier.setDuration(600);
            translationY_pwd.setDuration(600);
            translationY_btn.setDuration(600);

            translationY_dep.setStartDelay(100);
            translationY_cashier.setStartDelay(100);
            translationY_pwd.setStartDelay(200);
            translationY_btn.setStartDelay(200);
            translationSet.playTogether(translationY_title, translationY_dep, translationY_cashier, translationY_pwd,
                    translationY_btn);
        }
        mView.start(alphaSet, translationSet);
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
        if (alphaSet != null) {
            //取消动画
            alphaSet.cancel();
            alphaSet = null;
        }
        if (translationSet != null) {
            translationSet.cancel();
            translationSet = null;
        }
    }
}
