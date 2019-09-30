package com.ftrend.zgp.utils.db;

import com.blankj.utilcode.util.GsonUtils;
import com.ftrend.zgp.model.AppParams;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.DepProduct_Table;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.ZgParams;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.List;

/**
 * 数据库更新类
 *
 * @author liziqiang@ftrend.cn
 */
public class DbUpdateHelper {

    /**
     * 数据库初始化：预置数据
     *
     * @param db
     */
    public static void initDb(DatabaseWrapper db) {
        // 初始化本地参数
        new AppParams("serverUrl", ZgParams.getServerUrl()).insert(db);
        new AppParams("posCode", ZgParams.getPosCode()).insert(db);
//        new AppParams("regCode", "").insert(db);
        new AppParams("devSn", ZgParams.getDevSn()).insert(db);
        new AppParams("initFlag", ZgParams.getInitFlag()).insert(db);
        new AppParams("lastUser", ZgParams.getLastUser()).insert(db);
        new AppParams("lastDep", ZgParams.getLastDep()).insert(db);
        new AppParams("printerConfig", GsonUtils.toJson(ZgParams.getPrinterConfig())).insert(db);
        new AppParams("cardConfig", GsonUtils.toJson(ZgParams.getCardConfig())).insert(db);
    }

    public static void update_2(DatabaseWrapper db) {
        //新增字段赋值
        List<TradeProd> prodList = SQLite.select().from(TradeProd.class).queryList(db);
//        db.beginTransaction();
        for (TradeProd prod : prodList) {
            DepProduct product = SQLite.select().from(DepProduct.class)
                    .where(DepProduct_Table.prodCode.eq(prod.getProdCode())).querySingle(db);
            if (product != null) {
                prod.setProdForDsc(product.getForDsc());
                prod.setProdPriceFlag(product.getPriceFlag());
                prod.setProdIsLargess(product.getIsLargess());
                prod.setProdMinPrice(product.getMinimumPrice());
                prod.update(db);
            }
        }
//        db.setTransactionSuccessful();
//        db.endTransaction();
    }

}
