package com.ftrend.zgp;

import android.view.View;
import android.widget.TextView;

import com.ftrend.keyboard.KeyboardView;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.utils.dialog.DialogBuilder;
import com.ftrend.zgp.utils.dialog.DialogUtil;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.permission.PermissionUtil;

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
        DialogBuilder builder = new DialogBuilder(this, 2);
        builder.setRightBtn("确定");
        builder.setLeftBtn("返回");
        builder.setTitle("提示");
        builder.setContent("你懂了吗");
        builder.setOnClickListener(new DialogBuilder.onBtnClickListener() {
            @Override
            public void onLeftBtnClick(View v) {
                LogUtil.d("----你点了左边");
            }

            @Override
            public void onRightBtnClick(View v) {
                LogUtil.d("----你点了右边");
            }
        });
        DialogUtil.showErrorDialog(builder);
        //region http请求举例
//        ExampleSubscribe.getInstance().getHotKey(new ExampleCallBack<KeyWord>() {
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void onSuccess(List<KeyWord.DataBean> body, String msg) {
//                LogUtil.d("----onSuccess" + body.get(0).getName());
//            }
//
//            @Override
//            public void onFailed() {
//
//            }
//
//            @Override
//            public void onError() {
//
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        });
        //endregion
//        mKeyboardView.show();
    }
}
