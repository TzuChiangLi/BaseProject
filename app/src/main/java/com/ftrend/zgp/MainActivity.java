package com.ftrend.zgp;

import android.widget.TextView;

import com.ftrend.keyboard.KeyboardView;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.utils.http.BaseResponse;
import com.ftrend.zgp.utils.http.HttpCallBack;
import com.ftrend.zgp.utils.http.base_test.Login;
import com.ftrend.zgp.utils.http.base_test.LoginSubsrcibe;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.permission.PermissionUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liziqiang@ftrend.cn
 */
public class MainActivity extends BaseActivity {
    @BindView(R.id.tv)
    TextView mTv;
    @BindView(R.id.keyboard_view)
    KeyboardView mKeyboardView;


    @Override
    protected int getLayoutID(int i) {
        return R.layout.main_activity;
    }

    @Override
    protected void initData() {
        LogUtil.d("initData");
    }

    @Override
    protected void initView() {
        PermissionUtil.checkAndRequestPermission();

    }

    @Override
    protected void initTitleBar() {
        LogUtil.d("initTitleBar");
    }


    @OnClick(R.id.tv)
    public void show() {

        LoginSubsrcibe.getInstance().login("TzuChiangLi", "85654886", new HttpCallBack<Login>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Login body, BaseResponse.ResHead head) {
                LogUtil.d("----onSuccess" + body.getErrorMsg());
            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onError(String errorMsg) {

            }

            @Override
            public void onFinish() {

            }
        });
    }
}
