package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.zgp.R;
import com.ftrend.zgp.api.PwdChangeContract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.presenter.PwdChangePresenter;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

public class PwdChangeActivity extends BaseActivity implements PwdChangeContract.PwdChangeView, OnTitleBarListener {
    @BindView(R.id.pwd_chg_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.pwd_chg_edt_old)
    ClearEditText mOldEdt;
    @BindView(R.id.pwd_chg_edt_new)
    ClearEditText mNewEdt;
    @BindView(R.id.pwd_chg_edt_new_again)
    ClearEditText mConfirmEdt;
    private PwdChangeContract.PwdChangePresenter mPresenter;


    @Override
    public void onNetWorkChange(boolean isOnline) {
        if (mTitleBar == null) {
            mTitleBar = findViewById(R.id.pwd_chg_top_bar);
        }
        mTitleBar.setRightIcon(isOnline ? R.drawable.online : R.drawable.offline);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.pwd_change_activity;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = PwdChangePresenter.createPresenter(this);
        }
        mOldEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                        && KeyEvent.ACTION_DOWN == event.getAction())) {
                    mNewEdt.requestFocus();
                }
                return true;
            }
        });
        mNewEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                        && KeyEvent.ACTION_DOWN == event.getAction())) {
                    mConfirmEdt.requestFocus();
                }
                return true;
            }
        });
        mConfirmEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                        && KeyEvent.ACTION_DOWN == event.getAction())) {
                    if (TextUtils.isEmpty(mOldEdt.getText().toString()) || TextUtils.isEmpty(mNewEdt.getText().toString()) ||
                            TextUtils.isEmpty(mConfirmEdt.getText().toString())) {
                        MessageUtil.show("请确认输入信息是否正确");
                        KeyboardUtils.hideSoftInput(PwdChangeActivity.this);
                    } else {
                        modify();
                    }
                }
                return true;
            }
        });
    }

    private void modify() {
        mPresenter.modify(mOldEdt.getText().toString().trim(), mNewEdt.getText().toString().trim(),
                mConfirmEdt.getText().toString().trim());
    }

    @Override
    protected void initTitleBar() {
        mTitleBar.setOnTitleBarListener(this);
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        mTitleBar.setRightIcon(ZgParams.isIsOnline() ? R.drawable.online : R.drawable.offline);
    }

    @Override
    public void onLeftClick(View v) {
        finish();
    }

    @Override
    public void onTitleClick(View v) {

    }

    @Override
    public void onRightClick(View v) {

    }

    @OnClick(R.id.pwd_chg_btn_submit)
    public void submit() {
        modify();
    }

    @Override
    public void show(String msg) {
        MessageUtil.show(msg);
    }

    @Override
    public void showSuccess(String msg) {
        MessageUtil.info(msg, new MessageUtil.MessageBoxOkListener() {
            @Override
            public void onOk() {
                Intent intent = new Intent(PwdChangeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void showError(String err) {
        MessageUtil.showError(err);
    }

    @Override
    public void setPresenter(PwdChangeContract.PwdChangePresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }
}
