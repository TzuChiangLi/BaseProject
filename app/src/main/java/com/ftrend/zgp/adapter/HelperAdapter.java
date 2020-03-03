package com.ftrend.zgp.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ftrend.zgp.R;
import com.ftrend.zgp.model.Helper;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public class HelperAdapter extends BaseQuickAdapter<Helper, BaseViewHolder> {
    public HelperAdapter(int layoutResId, @Nullable List<Helper> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Helper item) {
        helper.setText(R.id.helper_rv_question,item.getQuestion());
        helper.setText(R.id.helper_rv_answer,item.getAnswer());
    }
}
