package com.ftrend.zgp.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ftrend.zgp.model.HandoverRecord;

import java.util.List;

/**
 * 交班记录界面暂时使用两个适配器的模式方便后续迭代
 *
 * @author liziqiang@ftrend.cn
 */
public class HandoverAdapter extends BaseQuickAdapter<HandoverRecord, BaseViewHolder> {

    public HandoverAdapter(int layoutResId, @Nullable List<HandoverRecord> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, HandoverRecord item) {






    }


}
