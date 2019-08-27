package com.ftrend.zgp.model;

import java.util.List;

/**
 * 主界面菜单实体类
 *
 * @author liziqiang@ftrend.cn
 */
public class Menu {
    /**
     * 分类名称
     */
    private String typeName;
    private List<MenuList> menuList;

    public Menu(String typeName, List<MenuList> menuList) {
        this.typeName = typeName;
        this.menuList = menuList;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<MenuList> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<MenuList> menuList) {
        this.menuList = menuList;
    }

    /**
     * 具体的功能按钮类
     * 包括图标、文本
     */
    public static class MenuList {
        /**
         * 图标资源
         */
        private int menuImg;
        /**
         * 功能文本
         */
        private String menuName;

        public MenuList(int menuImg, String menuName) {
            this.menuImg = menuImg;
            this.menuName = menuName;
        }

        public int getMenuImg() {
            return menuImg;
        }

        public void setMenuImg(int menuImg) {
            this.menuImg = menuImg;
        }

        public String getMenuName() {
            return menuName;
        }

        public void setMenuName(String menuName) {
            this.menuName = menuName;
        }
    }
}
