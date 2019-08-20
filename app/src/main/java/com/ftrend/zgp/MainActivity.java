package com.ftrend.zgp;

import android.widget.TextView;

import com.ftrend.keyboard.KeyboardView;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.utils.http.BaseResponse;
import com.ftrend.zgp.utils.http.HttpCallBack;
import com.ftrend.zgp.utils.http.base_test.TestSubscribe;
import com.ftrend.zgp.utils.http.base_test.User;
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
        TestSubscribe.getInstance().getResponse(new HttpCallBack<User>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(User body, BaseResponse.ResHead head) {
                LogUtil.d("----onSuccess:" + body.getName());
            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onFinish() {

            }
        });
    }
}
