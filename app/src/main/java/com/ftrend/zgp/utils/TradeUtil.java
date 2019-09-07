package com.ftrend.zgp.utils;

import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.TradeProd_Table;
import com.ftrend.zgp.model.Trade_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import static com.raizlabs.android.dbflow.sql.language.Method.count;

/**
 * 交易工具类
 *
 * @author liziqiang@ftrend.cn
 */
public class TradeUtil {
    /**
     * 当前流水单号
     */
    private static String lsNo = "";
    /**
     * 当前流水单，不保存Status
     */
    private static Trade trade = new Trade();


    /**
     * 创建流水单号的时候调用此方法，将Trade保存到静态常量中
     *
     * @param lsNo 流水单号
     */
    public static void setLsNo(String lsNo) {
        TradeUtil.lsNo = lsNo;
        initTrade();
    }

    public static String getLsNo() {
        return lsNo;
    }

    /**
     * 加载当前单据
     */
    private static void initTrade() {
        trade = SQLite.select().from(Trade.class).where(Trade_Table.lsNo.eq(lsNo)).querySingle();
    }




    public static Trade getTrade() {
        return trade;
    }

}
