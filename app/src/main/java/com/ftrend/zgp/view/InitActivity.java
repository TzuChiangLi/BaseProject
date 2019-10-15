package com.ftrend.zgp.view;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.ftrend.progressview.ProgressView;
import com.ftrend.zgp.R;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.presenter.InitPresenter;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.log.LogUtil;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;

/**
 * 首次初始化
 *
 * @author liziqiang@ftrend.cn
 */
public class InitActivity extends BaseActivity implements Contract.InitView {
    @BindView(R.id.init_progress_view)
    ProgressView mLoadView;
    @BindView(R.id.init_tv_warning)
    TextView mWarnningTv;
    @BindView(R.id.init_tv_title)
    TextView mTitleTv;
    @BindView(R.id.init_scl_info)
    ScrollView mInfoLayout;
    @BindView(R.id.init_tv_result_title1)
    TextView mFinishTv;
    @BindView(R.id.init_result_user)
    TextView mUserTv;
    @BindView(R.id.init_result_dep)
    TextView mDepTv;
    @BindView(R.id.init_result_posCode)
    TextView mPosCodeTv;
    private Contract.InitPresenter mPresenter;

    @Override
    protected int getLayoutID() {
        return R.layout.init_activity;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        LogUtil.d("----getScreenDensityDpi:" + ScreenUtils.getScreenDensityDpi());
        LogUtil.d("----getScreenDensity:" + ScreenUtils.getScreenDensity());
        LogUtil.d("----getScreenWidth:" + ScreenUtils.getScreenWidth());
        LogUtil.d("----getScreenHeight:" + ScreenUtils.getScreenHeight());
        if (mPresenter == null) {
            mPresenter = InitPresenter.createPresenter(this);
        }
        //防抖动点击
        mLoadView.setOnClickListener(new ClickUtils.OnDebouncingClickListener() {
            @Override
            public void onDebouncingClick(View v) {
                if (ClickUtil.onceClick()) {
                    return;
                }
                //点击开始获取数据，动效开始
                LogUtil.d(String.valueOf(((ProgressView) v).FLAG));
                if (((ProgressView) v).FLAG == -1) {
                    mPresenter.startAnimator();
                    mPresenter.startInitData(1);
                    return;
                }
                if (((ProgressView) v).FLAG == 0) {
                    mPresenter.stopInitData();
                    return;
                }
                if (((ProgressView) v).FLAG > 0) {
                    Intent intent = new Intent(InitActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        });
    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).autoDarkModeEnable(true).init();
    }

    @Override
    public void startUpdate() {
        mLoadView.start();
    }

    @Override
    public void updateProgress(int step, int progress) {
        // 确保进度值在0～100之间
        int current = progress < 0 ? 0 : progress > 100 ? 100 : progress;
        mLoadView.setProgress((current + (step - 1) * 100) / 2);
        if (current == 100) {
            if (step == 1) {
                mPresenter.startInitData(2);
            } else {
                mPresenter.finishInitData();
            }
        }
    }

    @Override
    public void stopUpdate() {
        mLoadView.restore(false);
    }

    @Override
    public void finishUpdate(final String posCode, final String dep, final String user) {
        mLoadView.restore(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mInfoLayout.setVisibility(View.VISIBLE);
                    mFinishTv.setVisibility(View.VISIBLE);
                    mTitleTv.setAlpha(0);
                    mWarnningTv.setAlpha(0);
                    mPosCodeTv.setText(posCode);
                    mDepTv.setText(dep);
                    mUserTv.setText(user);
                } catch (Exception e) {
                    LogUtil.e(e.getMessage());
                }
            }
        }, 1200);

    }

    @Override
    public void setPresenter(Contract.InitPresenter presenter) {
        if (presenter == null) {
            mPresenter = presenter;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
    }

    /**
     * 网络变化
     *
     * @param isOnline
     */
    @Override
    public void onNetWorkChange(boolean isOnline) {
    }
}
