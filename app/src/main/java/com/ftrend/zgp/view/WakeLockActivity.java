package com.ftrend.zgp.view;


import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.zgp.R;
import com.ftrend.zgp.api.WakeLockContract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.presenter.WakeLockPresenter;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liziqiang@ftrend.cn
 */
public class WakeLockActivity extends BaseActivity implements WakeLockContract.WakeLockView {
    @BindView(R.id.wake_lock_tv_dep)
    TextView mDepNameTv;
    @BindView(R.id.wake_lock_tv_cashier)
    TextView mCashierTv;
    @BindView(R.id.wake_lock_edt_pwd)
    ClearEditText mEdt;
    private WakeLockContract.WakePresenter mPresenter;

    @Override
    public void onNetWorkChange(boolean isOnline) {

    }

    @Override
    protected int getLayoutID() {
        return R.layout.wake_lock_activity;
    }

    @Override
    protected void initData() {
        mDepNameTv.setText(ZgParams.getCurrentDep().getDepName());
        mCashierTv.setText(ZgParams.getCurrentUser().getUserName());
    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = WakeLockPresenter.createPresenter(this);
        }
        mEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                        && KeyEvent.ACTION_DOWN == event.getAction())) {
                    enter();
                }
                return true;
            }
        });
    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).transparentNavigationBar().autoDarkModeEnable(true).init();
    }

    @OnClick(R.id.wake_lock_btn_enter)
    public void enter() {
        mPresenter.enter(mEdt.getText() == null ? "" : mEdt.getText().toString());
    }

    @Override
    public void show(String msg) {
        MessageUtil.show(msg);
    }

    @Override
    public void success() {
        finish();
    }

    @Override
    public void setPresenter(WakeLockContract.WakePresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
    }
}
