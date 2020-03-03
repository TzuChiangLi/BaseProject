package com.ftrend.zgp.view;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.ftrend.zgp.R;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;

/**
 * @author liziqiang@ftrend.cn
 */
public class IntroActivity extends BaseActivity implements OnTitleBarListener {
    @BindView(R.id.intro_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.intro_app_version)
    TextView mVersionTv;
    @BindView(R.id.intro_tv_home)
    TextView mHomeTv;
    @BindColor(R.color.common_tv_link)
    int mLinkColor;

    @Override
    public void onNetWorkChange(boolean isOnline) {

    }

    @Override
    protected int getLayoutID() {
        return R.layout.intro_activity;
    }

    @Override
    protected void initData() {
        mVersionTv.setText(String.format("Version  %s", AppUtils.getAppVersionName()));
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        mTitleBar.setOnTitleBarListener(this);
    }

    @Override
    public void onLeftClick(View v) {
        if (ClickUtil.onceClick()) {
            return;
        }
        finish();
        overridePendingTransition(R.anim.exit_fade_out, R.anim.exit_fade_in);
    }

    @Override
    public void onTitleClick(View v) {

    }

    @Override
    public void onRightClick(View v) {
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.exit_fade_out, R.anim.exit_fade_in);
    }
}
