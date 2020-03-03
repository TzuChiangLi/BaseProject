package com.ftrend.zgp;

import android.os.Environment;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.ftrend.zgp.model.AppParams;
import com.ftrend.zgp.model.AppParams_Table;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepPayInfo;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.Product;
import com.ftrend.zgp.model.SysParams;
import com.ftrend.zgp.model.SysParams_Table;
import com.ftrend.zgp.model.User;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Locale;

/**
 * 测试数据导出工具类
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/29
 */
public class TestHelper {

    private static String filepath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/DATA/" + AppUtils.getAppPackageName() + "/code.txt";
    private static FileWriter writer = null;

    /**
     * 基础数据导出成代码，用于在测试环境下导入数据
     */
    public static void BaseData2Code() {
        if (!initOutput()) {
            return;
        }
        try {
            dep2Code();
            output("");
            user2Code();
            output("");
            depCls2Code();
            output("");
            depProd2Code();
            output("");
            prod2Code();
            output("");
            depPayInfo2Code();
            output("");
            sysParams2Code();
            output("");
            appParams2Code();
        } finally {
            closeOutput();
        }
    }

    /**
     * 打开输出文件
     *
     * @return
     */
    private static boolean initOutput() {
        try {
            File file = new File(filepath);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            writer = new FileWriter(file, true);
            return true;
        } catch (Exception e) {
            Log.e("TestHelper", "initOutput: EXCEPTION", e);
            return false;
        }
    }

    /**
     * 关闭输出文件
     */
    private static void closeOutput() {
        try {
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Log.e("TestHelper", "closeOutput: EXCEPTION", e);
        }
    }

    /**
     * 专柜信息
     */
    private static void dep2Code() {
        outputBegin("Dep");
        outputClear("Dep");

        output("List<Dep> depList = new ArrayList<>();");
        final String TPL = "depList.add(new Dep(\"%s\", \"%s\"));";
        List<Dep> depList = SQLite.select().from(Dep.class).queryList();
        for (Dep dep : depList) {
            output(TPL, dep.getDepCode(), dep.getDepName());
        }
        outputMulti("Dep", "depList");

        outputEnd();
    }

    /**
     * 用户信息
     */
    private static void user2Code() {
        outputBegin("User");
        outputClear("User");

        output("List<User> userList = new ArrayList<>();");
        final String TPL = "userList.add(new User(\"%s\", \"%s\", \"%s\", \"%s\", %d, %.2fF, %.2fF));";
        List<User> userList = SQLite.select().from(User.class).queryList();
        for (User user : userList) {
            output(TPL, user.getUserCode(), user.getUserName(), user.getUserPwd(),
                    user.getUserRights(), user.getMaxDscRate(), user.getMaxDscTotal(),
                    user.getMaxTHTotal());
        }
        outputMulti("User", "userList");

        outputEnd();
    }

    /**
     * 专柜商品类别
     */
    private static void depCls2Code() {
        outputBegin("DepCls");
        outputClear("DepCls");

        output("List<DepCls> clsList = new ArrayList<>();");
        final String TPL = "clsList.add(new DepCls(\"%s\", \"%s\", \"%s\"));";
        List<DepCls> depClsList = SQLite.select().from(DepCls.class).queryList();
        for (DepCls depCls : depClsList) {
            output(TPL, depCls.getDepCode(), depCls.getClsCode(), depCls.getClsName());
        }
        outputMulti("DepCls", "clsList");

        outputEnd();
    }


    /**
     * DepProduct商品信息
     */
    private static void depProd2Code() {
        outputBegin("DepProduct");
        outputClear("DepProduct");

        output("List<DepProduct> prodList = new ArrayList<>();");
        final String TPL = "prodList.add(new DepProduct(\"%s\", \"%s\")\n"
                + ");";
        List<DepProduct> productList = SQLite.select().from(DepProduct.class).queryList();
        for (DepProduct product : productList) {
            output(TPL,
                    product.getDepCode(), product.getProdCode());
        }
        outputMulti("DepProduct", "prodList");

        outputEnd();
    }

    /**
     * Product商品信息
     */
    private static void prod2Code() {
        outputBegin("Product");
        outputClear("Product");

        output("List<Product> prodList = new ArrayList<>();");
        final String TPL = "prodList.add(new Product(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", %.2f)\n"
                + ".updateSaleInfo(%d, %d, %d, %d, %d, %.2f, %.2f)\n"
                + ".updateVipPrice(%.2f, %.2f, %.2f, %.2f, %.2f, %.2f)\n"
                + ");";
        List<Product> productList = SQLite.select().from(Product.class).queryList();
        for (Product product : productList) {
            output(TPL,
                    product.getProdCode(), product.getBarCode(),
                    product.getProdName(), product.getClsCode(), product.getUnit(),
                    product.getPrice(),
                    product.getPriceFlag(), product.getIsLargess(), product.getForSaleRet(),
                    product.getForDsc(), product.getForLargess(), product.getScoreSet(),
                    product.getMinimumPrice(),
                    product.getVipPrice1(), product.getVipPrice2(), product.getVipPrice3(),
                    product.getVipRate1(), product.getVipRate2(), product.getVipRate3());
        }
        outputMulti("Product", "prodList");

        outputEnd();
    }

    /**
     * 专柜支付方式信息
     */
    private static void depPayInfo2Code() {
        outputBegin("DepPayInfo");
        outputClear("DepPayInfo");

        output("List<DepPayInfo> payInfoList = new ArrayList<>();");
        final String TPL = "payInfoList.add(new DepPayInfo(\"%s\", \"%s\", \"%s\", \"%s\"));";
        List<DepPayInfo> payInfoList = SQLite.select().from(DepPayInfo.class).queryList();
        for (DepPayInfo payInfo : payInfoList) {
            output(TPL, payInfo.getDepCode(), payInfo.getPayTypeCode(), payInfo.getPayTypeName(),
                    payInfo.getAppPayType());
        }
        outputMulti("DepPayInfo", "payInfoList");

        outputEnd();
    }

    /**
     * 系统全局参数
     */
    private static void sysParams2Code() {
        outputBegin("SysParams");
        outputClear("SysParams");

        output("List<SysParams> paramList = new ArrayList<>();");
        final String TPL = "paramList.add(new SysParams(\"%s\", \"%s\"));";
        List<SysParams> paramsList = SQLite.select().from(SysParams.class)
                .where(SysParams_Table.paramName.notEq("CardConfig"))
                .queryList();
        for (SysParams params : paramsList) {
            output(TPL, params.getParamName(), params.getParamValue());
        }
        outputMulti("SysParams", "paramList");

        outputEnd();
    }

    /**
     * APP本地参数
     */
    private static void appParams2Code() {
        outputBegin("AppParams");
        outputClear("AppParams");

        output("List<AppParams> paramList = new ArrayList<>();");
        final String TPL = "paramList.add(new AppParams(\"%s\", \"%s\"));";
        //2019/9/29 这里的查询条件不起作用，可能和转义字符有关
        //select * from AppParams where paramName not like '%/_%' escape '/';
        List<AppParams> paramsList = SQLite.select().from(AppParams.class)
                .where(AppParams_Table.paramName.notLike("%/_%"))
                .queryList();
        for (AppParams params : paramsList) {
            output(TPL, params.getParamName(), params.getParamValue());
        }
        outputMulti("AppParams", "paramList");

        outputEnd();
    }

    /**
     * 输出一行，自动换行
     *
     * @param msg
     */
    private static void output(String msg) {
        try {
            writer.write(msg + "\n");
            writer.flush();
        } catch (Exception e) {
            Log.e("TestHelper", "output: EXCEPTION", e);
        }
    }

    /**
     * 带参数输出一行
     *
     * @param tpl
     * @param args
     */
    private static void output(String tpl, Object... args) {
        output(String.format(Locale.CHINA, tpl, args));
    }

    /**
     * 输出：清除已有数据
     *
     * @param clsName 要清除的类名
     */
    private static void outputClear(String clsName) {
        final String TPL = "SQLite.delete(%s.class).execute();";
        output(TPL, clsName);
    }

    /**
     * 输出批量插入语句
     *
     * @param clsName  数据类名
     * @param listName 数据列表变量名
     */
    private static void outputMulti(String clsName, String listName) {
        final String TPL = "\n" +
                "FlowManager.getDatabase(ZgpDb.class).executeTransaction(\n" +
                "        FastStoreModelTransaction\n" +
                "                .insertBuilder(FlowManager.getModelAdapter(%s.class))\n" +
                "                .addAll(%s)\n" +
                "                .build());";
        output(TPL, clsName, listName);
    }

    private static void outputBegin(String clsName) {
        final String TPL = "private static void import%s() {";
        output(TPL, clsName);
    }

    private static void outputEnd() {
        output("}");
    }
}
