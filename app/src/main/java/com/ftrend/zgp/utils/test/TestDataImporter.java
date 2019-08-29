package com.ftrend.zgp.utils.test;

import android.util.Log;

import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.User;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import static android.content.ContentValues.TAG;
import static com.raizlabs.android.dbflow.sql.language.Method.count;

/**
 * 测试数据导入工具
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/8/29
 */
public class TestDataImporter {

    /**
     * 导入全部测试数据
     */
    public static void importAll() {
        importDep();
        importUser();
        importDepCls();
        importDepProduct();
        importDepPayInfo();
        importSysParams();
        importAppParams();
    }

    /**
     * 导入专柜信息
     */
    private static void importDep() {
        SQLite.delete(Dep.class).execute();
        new Dep("2010", "CCDD").insert();
        new Dep("1020", "健足乐").insert();
        new Dep("2037", "绿时尚").insert();
        new Dep("2035", "公司自采").insert();
        Log.d(TAG, "=====================importDep记录数: " + SQLite.select(count()).from(Dep.class).count());
    }

    /**
     * 导入用户信息
     */
    private static void importUser() {
        SQLite.delete(User.class).execute();
        //String userCode, String userName, String userPwd, String userRights, int maxDscRate, float maxDscTotal, float maxTHTotal
        new User("080", "授权卡号", "123", "", 0, 0, 0).insert();
        new User("102", "宋昆林", "123", "", 0, 0, 0).insert();
        new User("061", "李雪勤", "123", "", 0, 0, 0).insert();
        new User("065", "高丽", "123", "", 0, 0, 0).insert();
        new User("096", "退货账号", "123", "", 0, 0, 0).insert();
        Log.d(TAG, "=====================importUser记录数: " + SQLite.select(count()).from(User.class).count());
    }

    /**
     * 导入专柜商品类别
     */
    private static void importDepCls() {
        SQLite.delete(DepCls.class).execute();
        //String depCode, String clsCode, String clsName
        new DepCls("2010", "2010", "CCDD").insert();
        new DepCls("1020", "1020", "健足乐").insert();
        new DepCls("2037", "2037", "绿时尚").insert();
        new DepCls("2035", "2035", "自采女装").insert();
        new DepCls("2035", "2051", "自采男装").insert();
        new DepCls("2035", "2052", "自采童装").insert();
        Log.d(TAG, "=====================importDepCls记录数: " + SQLite.select(count()).from(DepCls.class).count());
    }

    /**
     * 导入专柜商品
     */
    private static void importDepProduct() {
        SQLite.delete(DepProduct.class).execute();
        //String depCode, String prodCode, String barCode, String prodName, String clsCode, String spec, float price
        new DepProduct("2010", "2010", "", "CCDD正价码", "2010", "", 0).insert();
        new DepProduct("2010", "201001", "", "CCDD折扣码", "2010", "", 0).insert();
        new DepProduct("2010", "CESHI1", "20100001", "男装", "2010", "", 1000).insert();
        new DepProduct("2010", "CESHI2", "20100002", "女装", "2010", "", 1999).insert();
        new DepProduct("2010", "CESHI3", "20100003", "男鞋", "2010", "", 2999).insert();
        Log.d(TAG, "=====================importDepProduct记录数: " + SQLite.select(count()).from(DepProduct.class).count());
    }

    /**
     * 导入专柜支付方式
     */
    private static void importDepPayInfo() {
//        SQLite.delete(DepPayInfo.class).execute();
//        DepCode, PayTypeCode, PayTypeName, AppPayType
//        new DepPayInfo("2010", "0", "现金", "1").insert();
//        new DepPayInfo("2010", "2", "微信支付", "3").insert();
//        new DepPayInfo("2010", "3", "支付宝", "2").insert();
//        new DepPayInfo("2010", "8", "储值卡", "4").insert();
//        Log.d(TAG, "=====================importDepPayInfo记录数: " + SQLite.select(count()).from(DepPayInfo.class).count());
    }

    private static void importSysParams() {
//        SQLite.delete(SysParams.class).execute();
//        ParamName, ParamValue
//        new SysParams("NoClsDep", "2010,2018").insert();
//        new SysParams("VipCardType", "1").insert();
//        new SysParams("AliPayAccount", "123456789").insert();
//        new SysParams("WxPayAccount", "123456789").insert();
//        Log.d(TAG, "=====================importSysParams记录数: " + SQLite.select(count()).from(SysParams.class).count());
    }

    private static void importAppParams() {
//        SQLite.delete(AppParams.class).execute();
//        ParamName, ParamValue
//        new AppParams("printerConfig", "{}").insert();
//        new AppParams("cardConfig", "{}").insert();
//        new AppParams("lastDep", "1020").insert();
//        new AppParams("lastUser", "080").insert();
//        Log.d(TAG, "=====================importAppParams记录数: " + SQLite.select(count()).from(AppParams.class).count());
    }
}
