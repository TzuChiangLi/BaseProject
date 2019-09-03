package com.ftrend.zgp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ftrend.zgp.R;
import com.ftrend.zgp.model.Menu;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */
public class MenuAdapter extends BaseQuickAdapter<Menu, BaseViewHolder> {
    private ChildAdapter mChildAdapter;
    private Context mContext;
    private OnMenuClickListener mClickListener;

    public MenuAdapter(Context mContext, int layoutResId, @Nullable List<Menu> data) {
        super(layoutResId, data);
        this.mContext = mContext;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Menu item) {
        helper.setText(R.id.main_rv_menu_title, item.getTypeName());
        mChildAdapter = new ChildAdapter(R.layout.menu_rv_func_item, item.getMenuList());
        RecyclerView childRecyclerView = helper.getView(R.id.main_rv_menu_rv);
        childRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
        childRecyclerView.setAdapter(mChildAdapter);
        mChildAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mClickListener.onMenuClick(view, position);
            }
        });
    }


    public class ChildAdapter extends BaseQuickAdapter<Menu.MenuList, BaseViewHolder> {

        public ChildAdapter(int layoutResId, @Nullable List<Menu.MenuList> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, Menu.MenuList item) {
            helper.setImageResource(R.id.menu_img_func, item.getMenuImg());
            helper.setText(R.id.menu_tv_func, item.getMenuName());
            helper.setTag(R.id.menu_ll_func, item.getMenuName());
        }
    }


    public interface OnMenuClickListener {
        /**
         * 菜单点击监听
         *
         * @param view     点击的view
         * @param position 点击的位置
         */
        void onMenuClick(View view, int position);
    }

    public void setOnMenuClickListener(MenuAdapter.OnMenuClickListener onMenuClickListener) {
        mClickListener = onMenuClickListener;
    }
}
