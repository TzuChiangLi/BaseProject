package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.utils.http.BaseResponse;
import com.ftrend.zgp.utils.http.HttpCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面P层----所有业务逻辑在此
 *
 * @author liziqiang@ftrend.cn
 */
public class HomePresenter implements Contract.HomePresenter, HttpCallBack {
    private Contract.HomeView mView;


    public HomePresenter(Contract.HomeView mView) {
        this.mView = mView;
    }

    public static HomePresenter createPresenter(Contract.HomeView mView) {
        return new HomePresenter(mView);
    }


    @Override
    public void initMenuList() {
        List<Menu> menuList = new ArrayList<>();
        List<Menu.MenuList> childList = new ArrayList<>();
        String[] menuName = {"收银", "取单", "退货", "交班", "交班报表", "交易统计", "流水查询", "数据同步", "操作指南", "参数设置"
                , "修改密码", "注销登录"};
        for (int i = 0; i < 4; i++) {
            childList.add(new Menu.MenuList(0, menuName[i]));
        }
        menuList.add(new Menu("交易", childList));
        childList=new ArrayList<>();
        for (int i = 4; i < 7; i++) {
            childList.add(new Menu.MenuList(0, menuName[i]));
        }
        menuList.add(new Menu("报表查询", childList));
        childList=new ArrayList<>();
        for (int i = 7; i < menuName.length; i++) {
            childList.add(new Menu.MenuList(0, menuName[i]));
        }
        menuList.add(new Menu("系统功能", childList));
        mView.setMenuList(menuList);

    }


    @Override
    public void onStart() {

    }

    @Override
    public void onSuccess(Object body, BaseResponse.ResHead head) {

    }

    @Override
    public void onFailed() {

    }

    @Override
    public void onError(String errorMsg) {

    }

    @Override
    public void onFinish() {

    }


}
