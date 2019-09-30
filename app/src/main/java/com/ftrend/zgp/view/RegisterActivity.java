package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Handler;

import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.zgp.R;
import com.ftrend.zgp.api.Contract;
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
public class RegisterActivity extends BaseActivity implements Contract.RegisterView {
    @BindView(R.id.reg_edt_posCode)
    ClearEditText mPosCodeEdt;
    @BindView(R.id.reg_edt_regCode)
    ClearEditText mRegCodeEdt;
    @BindView(R.id.reg_edt_url)
    ClearEditText mURLEdt;
    private Contract.RegisterPresenter mPresenter;

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
        mPresenter.register(mURLEdt.getText().toString().trim(), mPosCodeEdt.getText().toString().trim(), mRegCodeEdt.getText().toString().trim());
    }


    @Override
    public void registerSuccess() {
        MessageUtil.showSuccess("设备注册成功！");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(RegisterActivity.this, InitActivity.class);
                startActivity(intent);
            }
        }, 1500);
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
    public void setPresenter(Contract.RegisterPresenter presenter) {
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
