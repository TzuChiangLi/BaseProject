package com.ftrend.zgp.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ftrend.zgp.R;
import com.ftrend.zgp.model.HandoverRecord;

import java.util.List;

/**
 * 交班功能的适配器
 *
 * @author liziqiang@ftrend.cn
 */
public class HandoverAdapter extends BaseQuickAdapter<HandoverRecord, BaseViewHolder> {


    public HandoverAdapter(int layoutResId, @Nullable List<HandoverRecord> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, HandoverRecord item) {
        helper.setText(R.id.handover_tv_usercode, String.valueOf(item.getCashier()));
        helper.setText(R.id.handover_tv_username, String.valueOf(item.getCashierName()));

        helper.setText(R.id.handover_cash_total, String.format("%.2f", item.getSaleTotal()));
        helper.setText(R.id.handover_cash_count, String.valueOf(item.getSaleCount()));
        helper.setText(R.id.handover_th_total, String.format("%.2f", item.getRtnTotal()));
        helper.setText(R.id.handover_th_count, String.valueOf(item.getRtnCount()));
        helper.setText(R.id.handover_trade_total, String.format("%.2f", item.getTotal()));
        helper.setText(R.id.handover_trade_count, String.valueOf(item.getCount()));

        helper.setText(R.id.handover_money_total, String.format("%.2f", item.getMoneyTotal()));
        helper.setText(R.id.handover_money_count, String.valueOf(item.getMoneyCount()));
        helper.setText(R.id.handover_sqb_total, String.format("%.2f", item.getSqbTotal()));
        helper.setText(R.id.handover_sqb_count, String.valueOf(item.getSqbCount()));
        helper.setText(R.id.handover_card_total, String.format("%.2f", item.getCardTotal()));
        helper.setText(R.id.handover_card_count, String.valueOf(item.getCardCount()));
        helper.setText(R.id.handover_pay_total, String.format("%.2f", item.getPayTotal()));
        helper.setText(R.id.handover_pay_count, String.valueOf(item.getPayCount()));

    }
}
