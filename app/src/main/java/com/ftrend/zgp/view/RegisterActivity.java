package com.ftrend.zgp.view;

import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.zgp.R;
import com.ftrend.zgp.api.RegisterContract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.presenter.RegisterPresenter;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liziqiang@ftrend.cn
 */
public class RegisterActivity extends BaseActivity implements RegisterContract.RegisterView {
    @BindView(R.id.reg_edt_posCode)
    ClearEditText mPosCodeEdt;
    @BindView(R.id.reg_edt_regCode)
    ClearEditText mRegCodeEdt;
    @BindView(R.id.reg_edt_url)
    ClearEditText mURLEdt;
    private RegisterContract.RegisterPresenter mPresenter;

    @Override
    protected int getLayoutID() {
        return R.layout.register_activity;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = RegisterPresenter.createPresenter(this);
        }
        // 默认显示数字键盘
        mPosCodeEdt.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        mRegCodeEdt.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        mURLEdt.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        mURLEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                        && KeyEvent.ACTION_DOWN == event.getAction())) {
                    mPosCodeEdt.requestFocus();
                }
                return true;
            }
        });
        mPosCodeEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                        && KeyEvent.ACTION_DOWN == event.getAction())) {
                    mRegCodeEdt.requestFocus();
                }
                return true;
            }
        });
        mRegCodeEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                        && KeyEvent.ACTION_DOWN == event.getAction())) {
                    if (TextUtils.isEmpty(mPosCodeEdt.getText().toString()) || TextUtils.isEmpty(mRegCodeEdt.getText().toString()) || TextUtils.isEmpty(mURLEdt.getText().toString())) {
                        MessageUtil.show("请填写注册信息");
                        KeyboardUtils.hideSoftInput(RegisterActivity.this);
                    } else {
                        register();
                    }
                }
                return true;
            }
        });

    }


    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).autoDarkModeEnable(true).init();
    }

    @OnClick(R.id.reg_btn)
    public void register() {
        if (ClickUtil.onceClick()) {
            return;
        }
        KeyboardUtils.hideSoftInput(this);
        mPresenter.register(mURLEdt.getText().toString().trim(), mPosCodeEdt.getText().toString().trim(), mRegCodeEdt.getText().toString().trim());
    }


    @Override
    public void registerSuccess() {
        Intent intent = new Intent(RegisterActivity.this, InitActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 网络变化
     *
     * @param isOnline
     */
    @Override
    public void onNetWorkChange(boolean isOnline) {

    }

    @Override
    public void registerError(String error) {

    }

    @Override
    public void setPresenter(RegisterContract.RegisterPresenter presenter) {
        if (presenter == null) {
            mPresenter = presenter;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
    }
}
