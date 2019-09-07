package com.ftrend.zgp.view;

import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.zgp.R;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.presenter.RegisterPresenter;
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
        mPosCodeEdt.setText("101");
        mURLEdt.setText("192.168.1.153:8091");
        mRegCodeEdt.setText("1234");
    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).autoDarkModeEnable(true).init();
    }

    @OnClick(R.id.reg_btn)
    public void register() {
        mPresenter.register(mURLEdt.getText().toString().trim(), mPosCodeEdt.getText().toString().trim(), mRegCodeEdt.getText().toString().trim());
    }


    @Override
    public void registerSuccess() {

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
