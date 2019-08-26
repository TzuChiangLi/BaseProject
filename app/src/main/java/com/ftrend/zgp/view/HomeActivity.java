package com.ftrend.zgp.view;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ftrend.zgp.R;
import com.ftrend.zgp.adapter.MenuAdapter;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.presenter.HomePresenter;
import com.ftrend.zgp.utils.db.DBHelper;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.permission.PermissionUtil;

import java.util.List;

import butterknife.BindView;

/**
 * 主界面V层----本界面不处理任何数据，只负责调用、接收并显示
 *
 * @author liziqiang@ftrend.cn
 */
public class HomeActivity extends BaseActivity implements Contract.HomeView, MenuAdapter.onMenuClickListener {
    @BindView(R.id.home_rv_menu)
    RecyclerView mRecyclerView;
    private Contract.HomePresenter mPresenter;
    private MenuAdapter mMenuAdapter;


    @Override
    protected void initData() {
        DBHelper dbHelper = new DBHelper(this, "TEST.db", null, 1);
        dbHelper.getWritableDatabase();
        mPresenter.initMenuList();
    }

    @Override
    protected void initView() {
        PermissionUtil.checkAndRequestPermission();
        if (mPresenter == null) {
            mPresenter = HomePresenter.createPresenter(this);
        }
    }

    @Override
    protected void initTitleBar() {
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


    @Override
    public void onMenuClick(View view, int position) {
        MessageUtil.show((String) view.getTag());
        Intent intent = new Intent(HomeActivity.this, ShopCartActivity.class);
        switch ((String) view.getTag()) {
            case "收银":
                startActivity(intent);
                break;
        }
    }
}