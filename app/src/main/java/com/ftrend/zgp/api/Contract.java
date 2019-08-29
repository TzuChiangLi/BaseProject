package com.ftrend.zgp.api;

import android.content.Context;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.model.User;

import java.util.List;

/**
 * 接口类
 *
 * @author liziqiang@ftrend.cn
 */
public interface Contract {

    /**********************************Activity************************************/
    interface LoginPresenter {
        /**
         * 初始化可登录专柜数据
         */
        void initDepData(Context context);

        /**
         * 初始化可登录用户数据
         *
         * @param depCode 柜台编码
         */
        void initUserData(String depCode);

        /**
         * 验证用户信息
         */
        void checkUserInfo();
    }

    interface LoginView extends BaseView<Contract.LoginPresenter> {

        /**
         * 返回专柜信息
         *
         * @param depData 专柜信息
         */
        void setDepData(List<Dep> depData);

        /**
         * 返回可登录用户信息
         *
         * @param userData 可登录用户
         */
        void setUserData(List<User> userData);


    }


    interface HomePresenter {
        /**
         * 创建界面菜单的数据
         */
        void initMenuList();

        /**
         * 设置用户名、专柜号、当前日期
         */
        void setInfo();
    }

    interface HomeView extends BaseView<Contract.HomePresenter> {
        /**
         * 返回数据显示界面
         *
         * @param menuList 数据
         */
        void setMenuList(List<Menu> menuList);

        /**
         * 设置用户名、专柜号、当前日期
         *
         * @param info 字符串数组
         */
        void showInfo(String... info);
    }

    //region 商品选择界面接口
    interface ShopCartPresenter {
        /**
         * 加载商品信息
         *
         * @param context 上下文
         */
        void initProdList(Context context);

        /**
         * 筛选商品
         *
         * @param key 筛选关键字
         */
        void searchProdList(String key);

        /**
         * 添加到购物车
         *
         * @param depProduct 商品对象
         */
        void addToShopCart(DepProduct depProduct);

    }

    interface ShopCartView extends BaseView<Contract.ShopCartPresenter> {
        /**
         * 设置商品类别
         *
         * @param clsList 分类名称
         */
        void setClsList(List<DepCls> clsList);

        /**
         * 设置商品
         *
         * @param prodList 商品列表
         */
        void setProdList(List<DepProduct> prodList);

        /**
         * 返回过滤筛选后的商品列表
         *
         * @param prodList 商品列表
         */
        void updateProdList(List<DepProduct> prodList);

    }
    //endregion


}
