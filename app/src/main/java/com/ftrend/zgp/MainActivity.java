package com.ftrend.zgp;

import android.widget.TextView;

import com.ftrend.keyboard.KeyboardView;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.example.KeyWord;
import com.ftrend.zgp.example.ExampleCallBack;
import com.ftrend.zgp.example.ExampleSubscribe;
import com.ftrend.zgp.utils.LogUtil;
import com.ftrend.zgp.utils.PermissionUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author LZQ
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
        //region http请求举例
        ExampleSubscribe.getInstance().getHotKey(new ExampleCallBack<KeyWord>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(List<KeyWord.DataBean> body, String msg) {
                LogUtil.d("----onSuccess" + body.get(0).getName());
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
        //endregion
        mKeyboardView.show();
    }
}
