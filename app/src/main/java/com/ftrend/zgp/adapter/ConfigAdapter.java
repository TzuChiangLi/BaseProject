package com.ftrend.zgp.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ftrend.zgp.R;
import com.ftrend.zgp.model.Config;
import com.suke.widget.SwitchButton;

import java.util.List;

import butterknife.BindDrawable;
import butterknife.ButterKnife;

/**
 * @author liziqiang@ftrend.cn
 */

public class ConfigAdapter extends BaseMultiItemQuickAdapter<Config, BaseViewHolder> {
    @BindDrawable(R.drawable.cfg_modify)
    Drawable modify;
    @BindDrawable(R.drawable.cfg_more)
    Drawable more;
    private SwitchButton.OnCheckedChangeListener listener;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ConfigAdapter(List<Config> data) {
        super(data);
        addItemType(Config.TYPE_TITLE, R.layout.config_rv_item_type);
        addItemType(Config.NORMAL_SWB, R.layout.config_rv_item_switch);
        addItemType(Config.NORMAL_MOD, R.layout.config_rv_item_normal);
        addItemType(Config.NORMAL_MULTI, R.layout.config_rv_item_normal);
        addItemType(Config.NORMAL_TEXT, R.layout.config_rv_item_normal);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, Config item) {
        ButterKnife.bind(this, helper.itemView);
        switch (helper.getItemViewType()) {
            case Config.TYPE_TITLE:
                //分组标题
                helper.setText(R.id.cfg_tv_type, item.getText());
                break;
            case Config.NORMAL_SWB:
                //开关按钮
                helper.setTag(R.id.cfg_btn_switch, item.getText());
                helper.setText(R.id.cfg_tv, item.getText());
                helper.setChecked(R.id.cfg_btn_switch, item.isOn());
                helper.setEnabled(R.id.cfg_btn_switch, item.isLock());
                helper.setGone(R.id.cfg_img_err, item.isErr());
                if (item.isErr()) {
                    helper.addOnClickListener(R.id.cfg_img_err);
                }
                ((SwitchButton) helper.getView(R.id.cfg_btn_switch)).setOnCheckedChangeListener(listener);
                break;
            case Config.NORMAL_MOD:
                //修改带输入框的选项
                helper.setImageDrawable(R.id.cfg_img, modify);
                helper.setText(R.id.cfg_tv_text, item.getText());
                helper.setText(R.id.cfg_tv, TextUtils.isEmpty(item.getData()) ? "" : item.getData());
                helper.addOnClickListener(R.id.cfg_ll);
                break;
            case Config.NORMAL_MULTI:
                helper.setImageDrawable(R.id.cfg_img, more);
                helper.setText(R.id.cfg_tv_text, item.getText());
                break;
            case Config.NORMAL_TEXT:
                helper.setGone(R.id.cfg_tv_lite, true);
                helper.setGone(R.id.cfg_tv, false);
                helper.setGone(R.id.cfg_img, false);
                helper.setText(R.id.cfg_tv_lite, item.getData());
                helper.setText(R.id.cfg_tv_text, item.getText());
            default:
                break;
        }
    }


    public void setListener(SwitchButton.OnCheckedChangeListener listener) {
        this.listener = listener;
    }

}
