package com.ftrend.zgp.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ftrend.zgp.R;
import com.ftrend.zgp.model.Config;
import com.suke.widget.SwitchButton;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public class ConfigAdapter extends BaseQuickAdapter<Config, BaseViewHolder> {
    private SwitchButton.OnCheckedChangeListener listener;

    public ConfigAdapter(int layoutResId, @Nullable List<Config> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Config item) {
        if (item.isType()) {
            helper.setText(R.id.cfg_tv_type, item.getCfgName());
            helper.setGone(R.id.cfg_tv_type, true);
            helper.setGone(R.id.cfg_rl, false);
        } else {
            helper.setText(R.id.cfg_tv_name, item.getCfgName());
            helper.setGone(R.id.cfg_tv_type, false);
            helper.setGone(R.id.cfg_rl, true);
            helper.setTag(R.id.cfg_btn_switch,item.getCfgName());
            helper.setChecked(R.id.cfg_btn_switch,item.isOn());
            ((SwitchButton) helper.getView(R.id.cfg_btn_switch)).setOnCheckedChangeListener(listener);
        }
    }

    public void setListener(SwitchButton.OnCheckedChangeListener listener) {
        this.listener = listener;
    }

}
