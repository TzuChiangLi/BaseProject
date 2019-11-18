package com.ftrend.zgp.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ftrend.zgp.R;
import com.ftrend.zgp.model.Trade;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public class TrdQryAdapter extends BaseQuickAdapter<Trade, BaseViewHolder> {

    public TrdQryAdapter(int layoutResId, @Nullable List<Trade> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Trade item) {
        helper.setText(R.id.trade_qry_rv_item_lsno, item.getLsNo());
        helper.setText(R.id.trade_qry_rv_item_total, String.format("%.2f", item.getTotal()));
        helper.setText(R.id.trade_qry_rv_item_trade_time, item.getTradeTime()==null?"":new SimpleDateFormat("yyyy/MM/dd HH:mm").format(item.getTradeTime()));    }
}