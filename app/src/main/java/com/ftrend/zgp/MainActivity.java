package com.ftrend.zgp;

import android.widget.TextView;

import com.ftrend.keyboard.KeyboardView;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.utils.LogUtil;
import com.ftrend.zgp.utils.http.HttpApi;
import com.ftrend.zgp.utils.http.HttpUtil;

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
        LogUtil.d("initView");
//        PermissionUtil.checkAndRequestPermission();
    }

    @Override
    protected void initTitleBar() {
        LogUtil.d("initTitleBar");
    }

    @OnClick(R.id.tv)
    public void show() {
        HttpUtil.get("hotkey");
//        mKeyboardView.show();
    }
}
