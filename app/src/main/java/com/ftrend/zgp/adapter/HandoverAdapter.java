package com.ftrend.zgp.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ftrend.zgp.model.Handover;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public class HandoverAdapter extends BaseQuickAdapter<Handover, BaseViewHolder> {
    public HandoverAdapter(int layoutResId, @Nullable List<Handover> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Handover item) {

    }
}
