package com.ftrend.zgp.view;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.ftrend.cleareditview.ClearEditText;
import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.LoginAdapter;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.presenter.LoginPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录功能实现
 *
 * @author liziqiang@ftrend.cn
 */
public class LoginActivity extends BaseActivity implements Contract.LoginView {
    @BindView(R.id.login_sp_dep)
    Spinner mDepSp;
    @BindView(R.id.login_sp_user)
    Spinner mUserSp;
    @BindView(R.id.login_edt_password)
    ClearEditText mPwdEdt;
    @BindView(R.id.login_btn)
    Button mLoginBtn;
    private Contract.LoginPresenter mPresenter;
    private LoginAdapter<Dep> mDepAdapter;
    private LoginAdapter<User> mUserAdapter;
    private String depCode, userCode;


    @Override
    protected int getLayoutID() {
        return R.layout.login_activity;
    }

    @Override
    protected void initData() {
        mPresenter.initDepData(this);


    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = LoginPresenter.createPresenter(this);
        }
    }

    @Override
    protected void initTitleBar() {
    }


    @OnClick(R.id.login_btn)
    public void doLogin() {
//        MessageUtil.show("暂未向下一界面传递数据");
//        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//        startActivity(intent);
        mPresenter.checkUserInfo();

    }

    @Override
    public void setDepData(final List<Dep> depData) {
        mDepAdapter = new LoginAdapter<>(this, depData, 0);
        mDepSp.setAdapter(mDepAdapter);
        mDepSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                depCode = mDepAdapter.getItemCode(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                depCode = null;
            }
        });
    }

    @Override
    public void setUserData(final List<User> userData) {
        mUserAdapter = new LoginAdapter<>(this, userData, 1);
        mUserSp.setAdapter(mUserAdapter);
        mUserSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userCode = userData.get(position).getUserCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                userCode = null;
            }
        });
    }

    @Override
    public void setPresenter(Contract.LoginPresenter presenter) {
        if (presenter != null) {
            mPresenter = presenter;
        }
    }

}
