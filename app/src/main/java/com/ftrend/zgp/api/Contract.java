package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.Menu;

import java.util.List;

/**
 * 接口类
 *
 * @author liziqiang@ftrend.cn
 */
public interface Contract {

    /**********************************Activity************************************/

    interface HomePresenter {
        /**
         * 创建界面菜单的数据
         */
        void initMenuList();
    }

    interface HomeView extends BaseView<Contract.HomePresenter> {
        /**
         * 返回数据显示界面
         *
         * @param menuList 数据
         */
        void setMenuList(List<Menu> menuList);
    }

    interface ShopCartPresenter {
        void initProdList();

        void searchProdList(String key);
    }

    interface ShopCartView extends BaseView<Contract.ShopCartPresenter> {
        void setClsList(List<DepCls> clsList);
        void setProdList(List<DepProduct> prodList);

        void updateProdList();

    }
}
