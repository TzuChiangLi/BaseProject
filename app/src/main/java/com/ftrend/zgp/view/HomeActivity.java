package com.ftrend.zgp.view;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.ftrend.zgp.utils.permission.PermissionUtil;
import com.ftrend.zgp.utils.task.LsUploadThread;
import com.ftrend.zgp.utils.task.ServerWatcherThread;
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
    private Contract.HomePresenter mPresenter;
    private MenuAdapter mMenuAdapter;


    @Override
    protected void initData() {
        mPresenter.initMenuList();
    }

    @Override
    protected void initView() {
        PermissionUtil.checkAndRequestPermission();
        if (mPresenter == null) {
            mPresenter = HomePresenter.createPresenter(this);
        }
        mPresenter.setInfo();

        //启动后台服务心跳检测线程
        ServerWatcherThread watcherThread = new ServerWatcherThread();
        watcherThread.start();
        //启动数据上传线程
        LsUploadThread lsUploadThread = new LsUploadThread();
        lsUploadThread.start();
    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        LogUtil.d("initTitleBar");
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
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestory();
    }
}
