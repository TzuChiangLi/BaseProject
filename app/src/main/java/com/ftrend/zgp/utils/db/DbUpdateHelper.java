package com.ftrend.zgp.utils.db;

import com.blankj.utilcode.util.GsonUtils;
import com.ftrend.zgp.model.AppParams;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.Dep_Table;
import com.ftrend.zgp.model.Product;
import com.ftrend.zgp.model.Product_Table;
import com.ftrend.zgp.model.SysParams;
import com.ftrend.zgp.model.SysParams_Table;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.log.LogUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

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
        //new AppParams("regCode", "").insert(db);
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
        //db.beginTransaction();
        for (TradeProd prod : prodList) {
            Product product = SQLite.select().from(Product.class)
                    .where(Product_Table.prodCode.eq(prod.getProdCode())).querySingle(db);
            if (product != null) {
                prod.setProdForDsc(product.getForDsc());
                prod.setProdPriceFlag(product.getPriceFlag());
                prod.setProdIsLargess(product.getIsLargess());
                prod.setProdMinPrice(product.getMinimumPrice());
                prod.update(db);
            }
        }
        //db.setTransactionSuccessful();
        //db.endTransaction();
    }

    public static void update_10(DatabaseWrapper db) {
        //转移数据,因为新版本中DepProduct的数据库关联关系取消，所以采用Cursor方式
        FlowCursor cursor = SQLite.select().distinct().from(DepProduct.class).query(db);
        try {
            //region 转移数据
            if (cursor != null && cursor.getCount() != 0) {
                if (cursor.moveToFirst()) {
                    do {
                        //将DepProduct表中不重复的商品信息添加到Product表中
                        Product prod = new Product();
                        prod.setPrice(cursor.getDoubleOrDefault("price"));
                        prod.setDepCode(cursor.getStringOrDefault("depCode"));
                        prod.setBarCode(cursor.getStringOrDefault("barCode"));
                        prod.setProdCode(cursor.getStringOrDefault("prodCode"));
                        prod.setProdName(cursor.getStringOrDefault("prodName"));
                        prod.setBrand(cursor.getStringOrDefault("brand"));
                        prod.setCargoNo(cursor.getStringOrDefault("cargoNo"));
                        prod.setClsCode(cursor.getStringOrDefault("clsCode"));
                        prod.setForDsc(cursor.getIntOrDefault("forDsc"));
                        prod.setForLargess(cursor.getIntOrDefault("forLargess"));
                        prod.setForSaleRet(cursor.getIntOrDefault("forSaleRet"));
                        prod.setIsLargess(cursor.getIntOrDefault("isLargess"));
                        prod.setMinimumPrice(cursor.getDoubleOrDefault("minimumPrice"));
                        prod.setPriceFlag(cursor.getIntOrDefault("priceFlag"));
                        prod.setProdStatus(cursor.getStringOrDefault("prodStatus"));
                        prod.setScoreSet(cursor.getDoubleOrDefault("scoreSet"));
                        prod.setSeason(cursor.getStringOrDefault("season"));
                        prod.setSpec(cursor.getStringOrDefault("spec"));
                        prod.setUnit(cursor.getStringOrDefault("unit"));
                        prod.setVipPrice1(cursor.getDoubleOrDefault("vipPrice1"));
                        prod.setVipPrice2(cursor.getDoubleOrDefault("vipPrice2"));
                        prod.setVipPrice3(cursor.getDoubleOrDefault("vipPrice3"));
                        prod.setVipRate1(cursor.getDoubleOrDefault("vipRate1"));
                        prod.setVipRate2(cursor.getDoubleOrDefault("vipRate2"));
                        prod.setVipRate3(cursor.getDoubleOrDefault("vipRate3"));
                        prod.insert(db);
                    } while (cursor.moveToNext());
                }
            }
            //endregion
            //DepCls表中插入0专柜数据
            List<DepCls> clsList = SQLite.select().from(DepCls.class).queryList(db);
            for (DepCls cls : clsList) {
                DepCls depCls = new DepCls();
                depCls.setClsCode(cls.getClsCode());
                depCls.setClsName(cls.getClsName());
                depCls.setDepCode("0");
                depCls.save(db);
            }
            //DepProduct表中插入0专柜数据
            List<DepProduct> prodList = SQLite.select().distinct().from(DepProduct.class).queryList(db);
            for (DepProduct prod : prodList) {
                //生成0专柜数据
                DepProduct depProd = new DepProduct();
                depProd.setProdCode(prod.getProdCode());
                depProd.setDepCode("0");
                depProd.setProdDepCode("");
                depProd.setProdName("");
                depProd.setClsCode("");
                depProd.save(db);
            }
            //检查0专柜是否存在，不存在则插入
            Dep dep = SQLite.select().from(Dep.class).where(Dep_Table.depCode.eq("0")).querySingle(db);
            SysParams params = SQLite.select().from(SysParams.class).where(SysParams_Table.paramName.eq("ShopName"))
                    .querySingle(db);
            if (dep == null && params != null) {
                dep = new Dep();
                dep.setDepName(params.getParamValue());
                dep.setDepCode("0");
                dep.save(db);
            }
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        } finally {
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
    }

    public static void update_11(DatabaseWrapper db) {
        //升级到此数据库版本后默认为关闭
        new AppParams("inputNum", "0").insert(db);
        new AppParams("inputDecimal", "0").insert(db);
    }
}
//region SQL语句删除DepProduct表,暂不执行
//            db.execSQL("DROP TABLE DepProduct");
//            db.execSQL("create table DepProduct("
//                    + "ID integer primary key autoincrement,"
//                    + "prodCode varchar,depCode varchar");
//            List<Product> mList = SQLite.select().from(Product.class).queryList(db);
//            DepProduct depProduct;
//            for (Product prod : mList) {
//                depProduct = new DepProduct();
//                depProduct.setProdCode(prod.getProdCode());
//                depProduct.setDepCode(prod.getDepCode());
//                depProduct.insert(db);
//            }
//endregion