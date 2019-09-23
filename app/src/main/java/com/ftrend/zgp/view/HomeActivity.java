package com.ftrend.zgp.view;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.MenuAdapter;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.presenter.HomePresenter;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.ClickUtil;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.gyf.immersionbar.ImmersionBar;

import java.util.List;

import butterknife.BindView;

/**
 * 主界面V层----本层不处理任何数据，只负责调用、接收并显示
 *
 * @author liziqiang@ftrend.cn
 */
public class HomeActivity extends BaseActivity implements Contract.HomeView, MenuAdapter.OnMenuClickListener {
    @BindView(R.id.home_rv_menu)
    RecyclerView mRecyclerView;
    @BindView(R.id.home_tv_date)
    TextView mDateTv;
    @BindView(R.id.home_tv_user)
    TextView mUserTv;
    @BindView(R.id.home_tv_depname)
    TextView mDepTv;
    @BindView(R.id.home_img_online_status)
    ImageView mNetImg;
    private Contract.HomePresenter mPresenter;
    private MenuAdapter mMenuAdapter;


    @Override
    protected void initData() {
        mPresenter.initMenuList();
    }

    @Override
    protected void initView() {
        if (mPresenter == null) {
            mPresenter = HomePresenter.createPresenter(this);
        }
        //设置界面信息
        mPresenter.setInfo();
        //启动线程
        mPresenter.initServerThread();
        //初始化商米支付SDK
        mPresenter.initSunmiPaySdk();
    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        mNetImg.setImageResource(ZgParams.isIsOnline() ? R.drawable.online : R.drawable.offline);

    }


    @Override
    protected int getLayoutID() {
        return R.layout.home_activity;
    }

    @Override
    public void setPresenter(Contract.HomePresenter presenter) {
        if (presenter == null) {
            mPresenter = HomePresenter.createPresenter(this);
        }
        mPresenter = presenter;
    }


    @Override
    public void setMenuList(List<Menu> menuList) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMenuAdapter = new MenuAdapter(this, R.layout.home_rv_menu_item, menuList);
        mRecyclerView.setAdapter(mMenuAdapter);
        mMenuAdapter.setOnMenuClickListener(this);
    }

    /**
     * 设置主界面需要显示的几个信息
     *
     * @param info 数组信息
     */
    @Override
    public void showInfo(String... info) {
        mDateTv.setText(info[0]);
        mUserTv.setText(ZgParams.getCurrentUser().getUserName());
        mDepTv.setText(ZgParams.getCurrentDep().getDepName());
    }


    @Override
    public void onMenuClick(View view, int position) {
        if (ClickUtil.onceClick()) {
            return;
        }
        MessageUtil.show((String) view.getTag());
        switch ((String) view.getTag()) {
            case "收银":
                mPresenter.goShopCart();
                break;
            case "交班":
                mPresenter.goHandover();
                break;
            case "注销登录":
                mPresenter.logout();
                break;
            default:
                LogUtil.e("无此功能");
                break;
        }
    }


    @Override
    public void goShopChartActivity(String lsNo) {
        Intent intent = new Intent(HomeActivity.this, ShopCartActivity.class);
        startActivity(intent);
    }

    @Override
    public void goHandoverActivity() {
        Intent intent = new Intent(HomeActivity.this, HandoverActivity.class);
        startActivity(intent);
    }

    @Override
    public void hasNoTrade() {
        MessageUtil.showWarning("当前没有交易流水！");
    }

    @Override
    public void showOfflineTip() {
        MessageUtil.showWarning("单机模式不能交班");
    }

    @Override
    public void logout() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
        if (mNetImg == null) {
            mNetImg = findViewById(R.id.home_img_online_status);
        }
        mNetImg.setImageResource(isOnline ? R.drawable.online : R.drawable.offline);
    }
}
