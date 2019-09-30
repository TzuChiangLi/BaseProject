package com.ftrend.zgp;

import com.ftrend.zgp.model.AppParams;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepPayInfo;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.SysParams;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.utils.db.ZgpDb;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;

import java.util.ArrayList;
import java.util.List;

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

    private static void importDep() {
        SQLite.delete(Dep.class).execute();
        List<Dep> depList = new ArrayList<>();
        depList.add(new Dep("2009", "太平鸟女装"));
        depList.add(new Dep("2018", "DULL"));
        depList.add(new Dep("2037", "绿时尚"));
        depList.add(new Dep("3002", "乐知味原料库"));

        FlowManager.getDatabase(ZgpDb.class).executeTransaction(
                FastStoreModelTransaction
                        .insertBuilder(FlowManager.getModelAdapter(Dep.class))
                        .addAll(depList)
                        .build());
    }

    private static void importUser() {
        SQLite.delete(User.class).execute();
        List<User> userList = new ArrayList<>();
        userList.add(new User("063", "蒋丽", "79>>H*H0Z", "011101111111011110", 70, 1000.00F, 0.00F));
        userList.add(new User("080", "授权卡号", "6A<>0/I;4", "111111111111111110", 80, 0.00F, 5000.00F));
        userList.add(new User("096", "退货账号", "7ANAL?;?)", "001101111011011110", 0, 0.00F, 0.00F));
        userList.add(new User("508", "周显云", "7-:>D,H;5", "111111111111111110", 0, 0.00F, 0.00F));
        FlowManager.getDatabase(ZgpDb.class).executeTransaction(
                FastStoreModelTransaction
                        .insertBuilder(FlowManager.getModelAdapter(User.class))
                        .addAll(userList)
                        .build());
    }

    private static void importDepCls() {
        SQLite.delete(DepCls.class).execute();
        List<DepCls> clsList = new ArrayList<>();
        clsList.add(new DepCls("2009", "2009", "太平鸟女装"));
        clsList.add(new DepCls("2018", "2018", "DULL"));
        clsList.add(new DepCls("2037", "2037", "绿时尚"));
        clsList.add(new DepCls("3002", "3002", "乐知味原料库"));

        FlowManager.getDatabase(ZgpDb.class).executeTransaction(
                FastStoreModelTransaction
                        .insertBuilder(FlowManager.getModelAdapter(DepCls.class))
                        .addAll(clsList)
                        .build());
    }

    private static void importDepProduct() {
        SQLite.delete(DepProduct.class).execute();
        List<DepProduct> prodList = new ArrayList<>();
        prodList.add(new DepProduct("2009", "2009", "", "太平鸟女装正价码", "2009", "   ", 0.00)
                .updateSaleInfo(1, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "200901", "", "太平鸟女装折扣码", "2009", "   ", 0.00)
                .updateSaleInfo(1, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "200905", "", "太平鸟女装双节活动折扣码", "2009", "   ", 0.00)
                .updateSaleInfo(1, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "200908", "", "太平鸟女装饰品", "2009", "   ", 0.00)
                .updateSaleInfo(1, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "9A0DD7301", "20090277", "太平鸟女装黑球鞋[PS]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1AA74507", "20090231", "太平鸟女装长大衣[D]", "2009", "05 ", 1899.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1AA74508", "20090212", "太平鸟女装廓形大衣[D]", "2009", "05 ", 1899.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1AA83501", "20090191", "太平鸟女装双面尼大衣[Q]", "2009", "05 ", 1999.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1AA84101", "20090234", "太平鸟女装斯文双面呢大衣[D]", "2009", "05 ", 2199.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1AA84121", "20090235", "太平鸟女装袖口系带大衣[D]", "2009", "05 ", 1799.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1AA84150", "20090236", "太平鸟女装双排扣双面呢大衣[D]", "2009", "05 ", 1999.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1AA84311", "20090264", "太平鸟女装长款斗篷式大衣[D]", "2009", "05 ", 3299.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1AC84309", "20090259", "太平鸟女装长款灯芯绒羽绒服[D]", "2009", "05 ", 2199.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1AC84322", "20090265", "太平鸟女装短款派克羽绒[D]", "2009", "05 ", 1799.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1BB83402", "20090169", "太平鸟女装灯芯绒外套[Q]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1BE83412", "20090195", "太平鸟女装领巾风衣[Q]", "2009", "05 ", 1399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1BE83502", "20090192", "太平鸟女装刺绣风衣[Q]", "2009", "05 ", 1299.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CA81111", "20090001", "太平鸟女装优雅衬衫[C]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CA81339", "20090002", "太平鸟女装条纹衬衫[C]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CA82129", "20090044", "太平鸟女装衬衫[X]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CA82339", "20090091", "太平鸟女装两穿系带衬衫[X]", "2009", "05 ", 529.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CA83140", "20090204", "太平鸟女装优雅撞色衬衫[Q]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CA83207", "20090155", "太平鸟女装优雅丝绵衬衫[Q]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CD74360", "20090230", "太平鸟女装上衣[D]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CD74403", "20090213", "太平鸟女装主题纹样上衣[D]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CD81120", "20090003", "太平鸟女装袖口绑带上衣[C]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CD82101", "20090045", "太平鸟女装上衣[X]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CD82161", "20090046", "太平鸟女装系带上衣[X]", "2009", "05 ", 529.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CD82186", "20090033", "太平鸟女装上衣[X]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CD82237", "20090063", "太平鸟女装荷叶边上衣[X]", "2009", "05 ", 529.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CD82317", "20090074", "太平鸟女装时尚上衣[X]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CD82346", "20090092", "太平鸟女装腰封上衣[X]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CD82426", "20090105", "太平鸟女装假两件上衣[X]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CD82438", "20090106", "太平鸟女装荷叶边装饰上衣[X]", "2009", "05 ", 469.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CD82697", "20090127", "太平鸟女装一字肩打揽上衣[X]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1CD83401", "20090186", "太平鸟女装优雅V领上衣[Q]", "2009", "05 ", 629.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1DA81307", "20090004", "太平鸟女装针织衫[C]", "2009", "05 ", 299.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1DA81430", "20090005", "太平鸟女装针织衫[C]", "2009", "05 ", 299.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1DA82113", "20090047", "太平鸟女装字母针织衫[X]", "2009", "05 ", 299.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1DA82252", "20090064", "太平鸟女装斯文针织衫[X]", "2009", "05 ", 369.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1DA82410", "20090107", "太平鸟女装袖口拼接针织衫[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1DA82568", "20090128", "太平鸟女装印花针织衫[X]", "2009", "05 ", 299.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1DA83333", "20090156", "太平鸟女装时尚字母针织衫[Q]", "2009", "05 ", 229.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1DA83A99", "20090198", "太平鸟女装工艺字母针织衫[Q]", "2009", "05 ", 369.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1DC74404", "20090214", "太平鸟女装时尚闪亮针织衫[D]", "2009", "05 ", 329.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1DC74503", "20090029", "太平鸟女装针织衫[C]", "2009", "05 ", 529.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1DC83310", "20090157", "太平鸟女装连帽长袖针织衫[Q]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1DC84113", "20090237", "太平鸟女装抽绳短款针织衫[D]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1DC84124", "20090238", "太平鸟女装字母长款针织衫[D]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1EB74338", "20090226", "太平鸟女装毛衫[D]", "2009", "05 ", 669.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1EB74448", "20090215", "太平鸟女装条纹高领毛衫[D]", "2009", "05 ", 459.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1EB83110", "20090205", "太平鸟女装时尚背心[Q]", "2009", "05 ", 359.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1EB83401", "20090185", "太平鸟女装条纹毛套衫[Q]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1EB83528", "20090193", "太平鸟女装连帽毛套衫[Q]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1EB84127", "20090239", "太平鸟女装马海毛套头衫[D]", "2009", "05 ", 999.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1EB84446", "20090260", "太平鸟女装袖口撞色毛衫[D]", "2009", "05 ", 799.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1EE81316", "20090006", "太平鸟女装时尚V领毛衫[C]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1EE82102", "20090048", "太平鸟女装套衫[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1EE82103", "20090034", "太平鸟女装套衫[X]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1EE84138", "20090240", "太平鸟女装时尚V领线衫[D]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1FA81113", "20090007", "太平鸟女装背带连衣裙[C]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1FA81446", "20090008", "太平鸟女装连衣裙[C]", "2009", "05 ", 899.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1FA82152", "20090049", "太平鸟女装连衣裙[X]", "2009", "05 ", 629.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1FA82186", "20090060", "太平鸟女装连衣裙[X]", "2009", "05 ", 899.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1FA82192", "20090035", "太平鸟女装连衣裙[X]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1FA82346", "20090140", "太平鸟女装斯文连衣裙[X]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1FA82354", "20090093", "太平鸟女装斯文连衣裙[X]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1FA82355", "20090075", "太平鸟女装优雅连衣裙[X]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1FA82385", "20090076", "太平鸟女装蕾丝连衣裙[X]", "2009", "05 ", 1199.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1FA82464", "20090108", "太平鸟女装背带连衣裙[X]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1FA82467", "20090109", "太平鸟女装印花连衣裙[X]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1FA82510", "20090129", "太平鸟女装荷叶边连衣裙[X]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1FA82516", "20090130", "太平鸟女装度假风纹样连衣裙[X]", "2009", "05 ", 799.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GB81309", "20090009", "太平鸟女装喇叭裤[C]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GB82340", "20090094", "太平鸟女装修身喇叭裤[X]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GB84107", "20090241", "太平鸟女装斯文喇叭裤[D]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GB84305", "20090266", "太平鸟女装撞色阔腿裤[D]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GC82101", "20090050", "太平鸟女装修身短裤[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GC83A04", "20090199", "太平鸟女装工装风短裤[Q]", "2009", "05 ", 569.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GC84103", "20090242", "太平鸟女装时尚PU短裤[D]", "2009", "05 ", 469.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GE74348", "20090216", "太平鸟女装呢料格纹短裙[D]", "2009", "05 ", 659.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GE81101", "20090010", "太平鸟女装时尚鱼尾裙[C]", "2009", "05 ", 469.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GE82101", "20090036", "太平鸟女装短裙[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GE82102", "20090037", "太平鸟女装半裙[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GE82120", "20090038", "太平鸟女装半裙[X]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GE82280", "20090065", "太平鸟女装斯文半裙[X]", "2009", "05 ", 369.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GE82321", "20090095", "太平鸟女装牛仔短裙[X]", "2009", "05 ", 329.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GE82423", "20090131", "太平鸟女装牛仔短裤[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GE83103", "20090206", "太平鸟女装前后两穿半裙[Q]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GE83401", "20090187", "太平鸟女装灯芯绒半裙[Q]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GE84107", "20090257", "太平鸟女装两穿系带短裙[D]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GF81404", "20090011", "太平鸟女装网纱长裙[C]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GF82101", "20090051", "太平鸟女装半裙[X]", "2009", "05 ", 469.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GF82102", "20090061", "太平鸟女装绣花半裙[X]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GF82162", "20090052", "太平鸟女装半裙[X]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GF82306", "20090096", "太平鸟女装荷叶边格纹半裙[X]", "2009", "05 ", 369.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GF82505", "20090132", "太平鸟女装背带印花半裙[X]", "2009", "05 ", 529.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1GF82563", "20090133", "太平鸟女装玫红裹裙[X]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1HA81105", "20090012", "太平鸟女装高腰牛仔裤[C]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1HA81106", "20090013", "太平鸟女装不规则脚口牛仔裤[C]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1HA83177", "20090207", "太平鸟女装破洞牛杂裤[Q]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1HA84116", "20090243", "太平鸟女装系带小脚牛仔裤[D]", "2009", "05 ", 569.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1HA84117", "20090244", "太平鸟女装字母印花牛仔裤[D]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1HB82432", "20090110", "太平鸟女装毛边牛仔短裤[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1HB82434", "20090111", "太平鸟女装主题刺绣牛仔短裤[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A1HB83284", "20090154", "太平鸟女装彩色牛仔短裤[Q]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2AA74228", "20090224", "太平鸟女装拼接大衣[D]", "2009", "05 ", 1699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2AA74418", "20090225", "太平鸟女装收腰大衣[D]", "2009", "05 ", 1799.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2AA84105", "20090245", "太平鸟女装设计感拼接大衣[D]", "2009", "05 ", 2199.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2AA84106", "20090246", "太平鸟女装时尚连帽大衣[D]", "2009", "05 ", 2199.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2AA84309", "20090267", "太平鸟女装连帽抽绳大衣[D]", "2009", "05 ", 2199.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2AC84332", "20090268", "太平鸟女装简约廓型羽绒服[D]", "2009", "05 ", 1599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2AC84416", "20090261", "太平鸟女装酷感户外长羽绒[D]", "2009", "05 ", 1899.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2AC84417", "20090258", "太平鸟女装时尚长款羽绒服[D]", "2009", "05 ", 1999.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2AC84419", "20090262", "太平鸟女装百搭收腰羽绒服[D]", "2009", "05 ", 2499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2BB83201", "20090150", "太平鸟女装廓形轻薄外套[Q]", "2009", "05 ", 799.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2BB83508", "20090197", "太平鸟女装丝绒短外套[Q]", "2009", "05 ", 799.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2BE81168", "20090014", "太平鸟女装时尚收腰风衣[C]", "2009", "05 ", 1099.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2BE83311", "20090158", "太平鸟女装廓形感风衣[Q]", "2009", "05 ", 1199.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2CA82128", "20090053", "太平鸟女装衬衫[X]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2CA82314", "20090097", "太平鸟女装收腰短衬衫[X]", "2009", "05 ", 529.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2CD81403", "20090015", "太平鸟女装上衣[C]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2CD82317", "20090077", "太平鸟女装露肩吊带上衣[X]", "2009", "05 ", 529.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2CD82363", "20090098", "太平鸟女装飘带上衣[X]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2CD82364", "20090078", "太平鸟女装时尚印花上衣[X]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2CD82488", "20090141", "太平鸟女装连帽短上衣[X]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2CD83388", "20090159", "太平鸟女装时髦感短上衣[Q]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2DA82107", "20090039", "太平鸟女装针织衫[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2DA82109", "20090040", "太平鸟女装彩虹针织衫[X]", "2009", "05 ", 299.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2DA82180", "20090054", "太平鸟女装针织衫[X]", "2009", "05 ", 259.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2DA82210", "20090066", "太平鸟女装拼接针织衫[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2DA82395", "20090079", "太平鸟女装酷感铆钉针织衫[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2DA82416", "20090112", "太平鸟女装露肩针织衫[X]", "2009", "05 ", 329.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2DA82596", "20090134", "太平鸟女装圆环装饰针织衫[X]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2DA82598", "20090135", "太平鸟女装袖口印花针织衫[X]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2DA82606", "20090136", "太平鸟女装链条肩带针织衫[X]", "2009", "05 ", 359.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2DA83221", "20090146", "太平鸟女装主题纹样针织衫[Q]", "2009", "05 ", 299.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2DC83335", "20090160", "太平鸟女装拼接连帽针织衫[Q]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2DC84311", "20090269", "太平鸟女装百搭连帽针织衫[D]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2EE74297", "20090217", "太平鸟女装条纹套衫[D]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2EE81315", "20090016", "太平鸟女装条纹线衫[C]", "2009", "05 ", 459.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2EE82166", "20090055", "太平鸟女装线衫[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2EE82312", "20090080", "太平鸟女装两穿式线衫[X]", "2009", "05 ", 459.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2EE83225", "20090148", "太平鸟女装时尚条纹线套衫[Q]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2EE83303", "20090161", "太平鸟女装拼接破洞线套衫[Q]", "2009", "05 ", 459.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA74203", "20090229", "太平鸟女装连衣裙[D]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA81138", "20090017", "太平鸟女装时尚H型连衣裙[C]", "2009", "05 ", 659.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA81334", "20090018", "太平鸟女装工装连衣裙[C]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA82125", "20090031", "太平鸟女装印花连衣裙[X]", "2009", "05 ", 799.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA82133", "20090032", "太平鸟女装带帽连衣裙[X]", "2009", "05 ", 899.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA82192", "20090041", "太平鸟女装牛仔背带裙[X]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA82207", "20090067", "太平鸟女装一字领连衣裙[X]", "2009", "05 ", 799.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA82229", "20090068", "太平鸟女装印花连衣裙[X]", "2009", "05 ", 669.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA82303", "20090081", "太平鸟女装印花连衣裙[X]", "2009", "05 ", 899.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA82312", "20090082", "太平鸟女装工装织带连衣裙[X]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA82322", "20090099", "太平鸟女装v领连衣裙[X]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA82388", "20090083", "太平鸟女装时尚长款连衣裙[X]", "2009", "05 ", 799.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA82409", "20090142", "太平鸟女装拼接连衣裙[X]", "2009", "05 ", 799.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA82440", "20090113", "太平鸟女装露肩连衣裙[X]", "2009", "05 ", 899.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA82560", "20090137", "太平鸟女装两穿式连衣裙[X]", "2009", "05 ", 1199.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA82634", "20090138", "太平鸟女装可拆卸吊带连衣裙[X]", "2009", "05 ", 899.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA83284", "20090147", "太平鸟女装主题纹样连衣裙[Q]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FA83487", "20090171", "太平鸟女装灯芯绒背带连衣裙[Q]", "2009", "05 ", 569.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2FB83A02", "20090200", "太平鸟女装背带短裤[Q]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GB74414", "20090232", "太平鸟女装阔腿裤[D]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GB81108", "20090019", "太平鸟女装腰头拼接收脚裤[C]", "2009", "05 ", 459.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GB82108", "20090062", "太平鸟女装长裤[X]", "2009", "05 ", 669.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GB82209", "20090069", "太平鸟女装九分微喇裤[X]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GB82404", "20090143", "太平鸟女装阔腿长腿[X]", "2009", "05 ", 459.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GB83406", "20090172", "太平鸟女装灯芯绒锥形裤[Q]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GB84306", "20090270", "太平鸟女装侧拼织带长裤[D]", "2009", "05 ", 629.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GB84408", "20090263", "太平鸟女装时尚简约七分裤[D]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GC81102", "20090020", "太平鸟女装侧拼织带PU短裙[C]", "2009", "05 ", 459.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GC82301", "20090084", "太平鸟女装设计感短裤[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GC82404", "20090114", "太平鸟女装运动感撞色短裤[X]", "2009", "05 ", 359.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GC82506", "20090118", "太平鸟女装简约设计感短裤[X]", "2009", "05 ", 359.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GE74314", "20090233", "太平鸟女装短裙[D]", "2009", "05 ", 459.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GE82105", "20090056", "太平鸟女装半裙[X]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GE82163", "20090042", "太平鸟女装牛仔半裙[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GE82164", "20090043", "太平鸟女装设计感连衣裙[X]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GE82216", "20090070", "太平鸟女装设计感半裙[X]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GE82323", "20090085", "太平鸟女装背带裙[X]", "2009", "05 ", 569.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GE82660", "20090119", "太平鸟女装设计感牛仔半裙[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GE83250", "20090151", "太平鸟女装基础牛仔半裙[Q]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GE83532", "20090194", "太平鸟女装百搭丝绒半裙[Q]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2GE83A52", "20090201", "太平鸟女装牛仔半裙[Q]", "2009", "05 ", 359.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2HA74409", "20090218", "太平鸟女装基础小脚牛仔裤[D]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2HA81422", "20090021", "太平鸟女装牛仔喇叭裤[C]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2HA82303", "20090100", "太平鸟女装锥形牛仔裤[X]", "2009", "05 ", 529.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2HA83208", "20090149", "太平鸟女装九分微喇牛仔裤[Q]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2HA83305", "20090162", "太平鸟女装高腰小脚牛仔裤[Q]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2HA83386", "20090163", "太平鸟女装时尚直筒牛仔裤[Q]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2HA84103", "20090247", "太平鸟女装高腰毛边牛仔裤[D]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2HA84112", "20090248", "太平鸟女装时尚微喇牛仔裤[D]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2HB82303", "20090101", "太平鸟女装基本牛仔短款[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2HB82405", "20090115", "太平鸟女装基本牛仔短裤[X]", "2009", "05 ", 329.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2HB82502", "20090120", "太平鸟女装设计感牛仔短裤[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A2HB82507", "20090121", "太平鸟女装简约牛仔短裤[X]", "2009", "05 ", 359.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3BB83454", "20090179", "太平鸟女装拼色牛仔外套[Q]", "2009", "05 ", 799.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3BE81303", "20090022", "太平鸟女装长款连帽风衣[C]", "2009", "05 ", 999.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3BE83408", "20090168", "太平鸟女装收腰连帽风衣[Q]", "2009", "05 ", 1299.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3CD82420", "20090117", "太平鸟女装连帽拼网眼上衣[X]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3CD83104", "20090208", "太平鸟女装拼色荷叶边卫衣[Q]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3CD83515", "20090182", "太平鸟女装丝绒短上衣[Q]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3DA82641", "20090122", "太平鸟女装露肩短袖t恤[X]", "2009", "05 ", 269.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3DA83A22", "20090202", "太平鸟女装印花针织衫[Q]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3DC81109", "20090023", "太平鸟女装拼色连帽卫衣[C]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3DC81123", "20090024", "太平鸟女装圆领长袖卫衣[C]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3DC81321", "20090025", "太平鸟女装V领荷叶边卫衣[C]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3DC83107", "20090209", "太平鸟女装拼色短卫衣[Q]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3DC83205", "20090164", "太平鸟女装灯笼袖卫衣[Q]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3DC83206", "20090153", "太平鸟女装嵌条短卫衣[Q]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3DC83460", "20090170", "太平鸟女装个性印花卫衣[Q]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3EB83415", "20090178", "太平鸟女装两面穿毛衫[Q]", "2009", "05 ", 529.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3EB84105", "20090249", "太平鸟女装挂脖V领毛衫[D]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3EE74507", "20090219", "太平鸟女装高领织带线衫[D]", "2009", "05 ", 529.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3EE83520", "20090189", "太平鸟女装满身提花毛衫[Q]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3EE83522", "20090181", "太平鸟女装烟囱领毛衫[Q]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3FA74209", "20090227", "太平鸟女装连衣裙[D]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3FA82101", "20090030", "太平鸟女装拼色连衣裙[X]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3FA82416", "20090116", "太平鸟女装拼蕾丝连衣裙[X]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3FA83223", "20090145", "太平鸟女装撞色连帽连衣裙[Q]", "2009", "05 ", 799.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3FB82111", "20090057", "太平鸟女装背带短裤[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3GB74209", "20090228", "太平鸟女装直筒裤[D]", "2009", "05 ", 529.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3GB81196", "20090026", "太平鸟女装拼色运动裤[C]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3GB83518", "20090183", "太平鸟女装丝绒拼接长裤[Q]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3GC82421", "20090103", "太平鸟女装侧织带短裤[X]", "2009", "05 ", 369.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3GC82625", "20090123", "太平鸟女装腰头刺绣短裤[X]", "2009", "05 ", 369.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3GC83466", "20090184", "太平鸟女装腰头系结短裤[Q]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3GC83566", "20090188", "太平鸟女装腰头系结短裤[Q]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3GE81398", "20090027", "太平鸟女装牛仔短裙[C]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3GE82102", "20090058", "太平鸟女装短裙[X]", "2009", "05 ", 329.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3GE82573", "20090124", "太平鸟女装扇形牛仔裙[X]", "2009", "05 ", 369.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3GE83415", "20090180", "太平鸟女装灯芯绒半身裙[Q]", "2009", "05 ", 469.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3HA84162", "20090250", "太平鸟女装高腰弹力紧身牛仔裤[D]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3HB82391", "20090102", "太平鸟女装拼网眼牛仔短裤[X]", "2009", "05 ", 329.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3HB82489", "20090144", "太平鸟女装侧织带牛仔短裤[X]", "2009", "05 ", 329.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A3HB82567", "20090125", "太平鸟女装腰带牛仔裤[X]", "2009", "05 ", 369.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4CD82304", "20090086", "太平鸟女装吊带条纹上衣[X]", "2009", "05 ", 329.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4DA82304", "20090087", "太平鸟女装长款针织衫[X]", "2009", "05 ", 299.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4DA82309", "20090088", "太平鸟女装时尚短款针织衫[X]", "2009", "05 ", 199.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4EA83427", "20090175", "太平鸟女装撞色领开衫[Q]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4EE74214", "20090220", "太平鸟女装撞色领线衫[D]", "2009", "05 ", 299.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4EE82121", "20090059", "太平鸟女装字母线衫[X]", "2009", "05 ", 329.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4EE82217", "20090071", "太平鸟女装撞色v领线衫[X]", "2009", "05 ", 369.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4EE82325", "20090089", "太平鸟女装吊带一字领线衫[X]", "2009", "05 ", 299.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4EE83223", "20090152", "太平鸟女装露肩中袖线衫[Q]", "2009", "05 ", 369.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4EE83309", "20090165", "太平鸟女装基础条纹线衫[Q]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4EE83412", "20090174", "太平鸟女装时尚打底线衫[Q]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4EE83414", "20090166", "太平鸟女装连帽线套衫[Q]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4EE83A21", "20090203", "太平鸟女装v领线衫[Q]", "2009", "05 ", 299.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4FA82302", "20090090", "太平鸟女装基本背带连衣裙[X]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4GB83499", "20090177", "太平鸟女装钻石绒阔腿裤[Q]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4GE83104", "20090210", "太平鸟女装牛仔短裙[Q]", "2009", "05 ", 329.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4GE83402", "20090176", "太平鸟女装时尚PU半裙[Q]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4GE83408", "20090173", "太平鸟女装灯芯绒包裙[Q]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4HA74255", "20090221", "太平鸟女装高腰绑带牛仔裤[D]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4HA81320", "20090028", "太平鸟女装牛仔裤[C]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4HA82205", "20090072", "太平鸟女装配送腰带牛仔裤[X]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4HA83105", "20090211", "太平鸟女装直筒九分牛仔裤[Q]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4HA83207", "20090196", "太平鸟女装高腰哈伦牛仔裤[Q]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4HA83311", "20090167", "太平鸟女装小高腰牛仔裤[Q]", "2009", "05 ", 369.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4HA84102", "20090251", "太平鸟女装提臀修身小脚裤[D]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4HB82201", "20090073", "太平鸟女装配送腰带牛仔裤[X]", "2009", "05 ", 329.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4HB82403", "20090104", "太平鸟女装假两件裙裤[X]", "2009", "05 ", 299.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A4HB82505", "20090126", "太平鸟女装修身牛仔短裤[X]", "2009", "05 ", 299.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A5DA82401", "20090139", "太平鸟女装印花针织衫[X]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A5FB83106", "20090190", "太平鸟女装连体背带裤[Q]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A6AC84305", "20090271", "太平鸟女装凤凰合作长款羽绒服[D]", "2009", "05 ", 1999.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A6AC84350", "20090272", "太平鸟女装凤凰合作印花羽绒服[D]", "2009", "05 ", 2299.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A6DC84190", "20090252", "太平鸟女装凤凰合作主题印花卫衣[D]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A6EE84153", "20090253", "太平鸟女装凤凰合作条纹圆领线套衫[D]", "2009", "05 ", 599.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A6EE84154", "20090254", "太平鸟女装凤凰合作满印圆领线套衫[D]", "2009", "05 ", 699.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A6GB84155", "20090255", "太平鸟女装凤凰合作印花运动裤[D]", "2009", "05 ", 669.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A6GC84153", "20090256", "太平鸟女装凤凰合作两面穿PU短裤[D]", "2009", "05 ", 429.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A9YA74302", "20090222", "太平鸟女装极简针织帽[D]", "2009", "05 ", 199.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A9YA74403", "20090223", "太平鸟女装潮流棒球帽[D]", "2009", "05 ", 199.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A9YA82101", "20090285", "太平鸟女装极简棒球帽[PS]", "2009", "05 ", 159.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A9YA82302", "20090286", "太平鸟女装个性渔夫帽[PS]", "2009", "05 ", 159.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A9YA82304", "20090273", "太平鸟女装炫酷空顶帽[PS]", "2009", "05 ", 159.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A9YB74302", "20090278", "太平鸟女装素色流苏围巾（牛绒）[PS]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A9YB74402", "20090279", "太平鸟女装字母烫印围巾（羊毛）[PS]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A9YB74505", "20090280", "太平鸟女装时尚拼色围巾（羊毛）[PS]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A9YB74506", "20090281", "太平鸟女装净色经典围巾（羊毛）[PS]", "2009", "05 ", 399.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A9YF74404", "20090282", "太平鸟女装毛肩带多用包[PS]", "2009", "05 ", 559.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A9YF74503", "20090283", "太平鸟女装毛绒单肩包[PS]", "2009", "05 ", 459.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A9YF82203", "20090287", "太平鸟女装极简时尚单肩包[PS]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A9YF83105", "20090275", "太平鸟女装时尚造型单肩包[PS]", "2009", "05 ", 559.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A9YF83306", "20090276", "太平鸟女装时尚简约单肩包[PS]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A9ZA82201", "20090284", "太平鸟女装时尚尖头女鞋[PS]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2009", "A9ZE82401", "20090274", "太平鸟女装百搭一字带女鞋[PS]", "2009", "05 ", 499.00)
                .updateSaleInfo(0, 0, 0, 0, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2018", "2018", "", "DULL正价码", "2018", "   ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 0.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2018", "201801", "", "DULL折扣码", "2018", "   ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 0.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2018", "201805", "", "DULL双节活动折扣码", "2018", "   ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 0.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "6667M", "20370039", "绿时尚2800D云舒棉16#均", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "6701N", "20370040", "绿时尚2800D棉舒裤16#均", "2037", "05 ", 89.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "6702O", "20370041", "绿时尚2000DBB舒裤16#均", "2037", "05 ", 79.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "6813P", "20370042", "绿时尚一体裤16#均", "2037", "05 ", 119.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X73319A", "20370027", "绿时尚薄男款套装碳灰XXL", "2037", "05 ", 169.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X73319Q", "20370017", "绿时尚薄男款套装浅灰L", "2037", "05 ", 169.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X73319R", "20370018", "绿时尚薄男款套装浅灰XL", "2037", "05 ", 169.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X73319S", "20370019", "绿时尚薄男款套装浅灰XXL", "2037", "05 ", 169.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X73319T", "20370020", "绿时尚薄男款套装黑XL", "2037", "05 ", 169.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X73319U", "20370021", "绿时尚薄男款套装黑XXL", "2037", "05 ", 169.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X73319V", "20370022", "绿时尚薄男款套装墨绿L", "2037", "05 ", 169.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X73319W", "20370023", "绿时尚薄男款套装墨绿XL", "2037", "05 ", 169.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X73319X", "20370024", "绿时尚薄男款套装墨绿XXL", "2037", "05 ", 169.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X73319Y", "20370025", "绿时尚薄男款套装碳灰L", "2037", "05 ", 169.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X73319Z", "20370026", "绿时尚薄男款套装碳灰XL", "2037", "05 ", 169.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74123A", "20370001", "绿时尚薄女单裤粉L", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74123B", "20370002", "绿时尚薄女单裤粉XL", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74123C", "20370003", "绿时尚薄女单裤黑L", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74123D", "20370004", "绿时尚薄女单裤黑XL", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74123E", "20370005", "绿时尚薄女单裤杏L", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74123F", "20370006", "绿时尚薄女单裤杏XL", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74123G", "20370007", "绿时尚薄女单裤碳灰L", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74123H", "20370008", "绿时尚薄女单裤碳灰XL", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74211I", "20370009", "绿时尚薄女单上衣粉L", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74211J", "20370010", "绿时尚薄女单上衣粉XL", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74211K", "20370011", "绿时尚薄女单上衣黑L", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74211L", "20370012", "绿时尚薄女单上衣黑XL", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74211M", "20370013", "绿时尚薄女单上衣杏L", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74211N", "20370014", "绿时尚薄女单上衣杏XL", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74211O", "20370015", "绿时尚薄女单上衣碳灰L", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74211P", "20370016", "绿时尚薄女单上衣碳灰XL", "2037", "05 ", 69.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74348Q", "20370043", "绿时尚薄女款套装大红L", "2037", "05 ", 139.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X74348R", "20370044", "绿时尚薄女款套装大红XL", "2037", "05 ", 139.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X83206S", "20370045", "绿时尚男士中领单衫黑L", "2037", "05 ", 129.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X84205G", "20370033", "绿时尚女中领单衫黑L", "2037", "05 ", 99.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X84205H", "20370034", "绿时尚女中领单衫黑XL", "2037", "05 ", 99.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X84205I", "20370035", "绿时尚女中领单衫黑XXL", "2037", "05 ", 99.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X84205J", "20370036", "绿时尚女中领单衫碳灰L", "2037", "05 ", 99.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X84205K", "20370037", "绿时尚女中领单衫碳灰XL", "2037", "05 ", 99.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X84205L", "20370038", "绿时尚女中领单衫碳灰XXL", "2037", "05 ", 99.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X84306B", "20370028", "绿时尚加绒女套黑XL", "2037", "05 ", 179.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X84306C", "20370029", "绿时尚加绒女套黑XXL", "2037", "05 ", 179.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X84306D", "20370030", "绿时尚加绒女套碳灰L", "2037", "05 ", 179.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X84306E", "20370031", "绿时尚加绒女套碳灰XL", "2037", "05 ", 179.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("2037", "X84306F", "20370032", "绿时尚加绒女套碳灰XXL", "2037", "05 ", 179.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 100.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300202", "33594", "专用红茶", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300205", "33138", "茉莉绿茶散茶", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300208", "2000003002085", "1109=安佳纯牛奶（白金奶）1L . 1*12", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300211", "2000003002115", "33383=众果果粒多茶酱-奇异果.1.3kg*12", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300213", "2000003002139", "3303=COTE果葡糖浆2. 5公斤.1*6", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300215", "2000003002153", "22629=馥美乐风味果泥-水蜜桃，1kg*6", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300216", "2000003002160", "3368=安德鲁颗粒果酱草莓1kg .1*8", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300217", "2000003002177", "33217=火龙果汁960ml,1*20", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300218", "2000003002184", "33214=百香果汁960ml . 1*20", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300219", "2000003002191", "22638=馥美乐风味果泥-芒果.1*6", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300220", "2000003002207", "3358=爱护牌咖啡植脂奶油1L..1*12", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300221", "2000003002214", "55273=圣农黄金鸡柳1kg . 1*10", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300222", "2000003002221", "22167=姐妹厨房脆香鸡5kg . 1*2", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300223", "2000003002238", "5586=麦肯金牌优+冷冻裹粉薯条（1000005255", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300224", "2000003002245", "55215=麦肯开心薯（1000001262) 4LB . 1*6", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300225", "2000003002252", "22347=亚洲渔港黄金蝴蝶虾（特惠装）.1000g", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300226", "2000003002269", "22176=姐妹厨房芒果慕斯2.5kg . 1*4", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300227", "2000003002276", "22166=姐妹厨房超值培根l.kg . 1*12", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300228", "2000003002283", "22144=海通披萨面团（170g*9个*8包）.1*8", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300229", "2000003002290", "55394=忆霖披萨酱1kg . 1*12", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300230", "2000003002306", "2203=安佳马苏里拉芝士碎.12kg", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300231", "2000003002313", "2290=宇翔蛋挞皮（中号）18g . 60个*5", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300232", "2000003002320", "11375=厚德葡式蛋挞液500g. 1*24", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300233", "2000003002337", "1179=奥世代可可脂纯白巧克力砖1kg . 1*10", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300234", "2000003002344", "1180=奥世代可可脂黑巧克力砖1kg . 1*10", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300235", "2000003002351", "11237=焙顺牌明胶片（吉利丁片）1kg . Ikg*", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300236", "2000003002368", "33523=阿榴哥速冻榴莲肉3Kg . 1*6", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300237", "2000003002375", "44921=妃莉65%黑巧克力2.5kg . 1*4", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300238", "2000003002382", "44534=妃莉（纽扣状）白巧克力33% 2.5kg .", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300239", "2000003002399", "1160=安佳奶油芝士 . 1 kg* 12", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300240", "2000003002405", "1159=安佳淡奶油.1L*12", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300241", "2000003002412", "1185=奥世精品高脂可可粉1kg. 1*8", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300242", "2000003002429", "11328=安佳黄油片1kg . 1*20", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300243", "2000003002436", "22163=姐妹厨房32cm德式香肠1kg . 1*12", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300244", "2000003002443", "22759=奶油披萨酱 1kg * 10 . 1*10", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300245", "2000003002450", "55322=泰森恐龙鸡块1kg . 1*10", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300246", "2000003002467", "55282=圣农美厨金牌烤翅0.92kg . 1*12", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300247", "2000003002474", "55385=憶霖蕃茄沙司10g300小包.1*2", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300248", "2000003002481", "11700=紫大成98蛋糕粉50LB . 1*1", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300249", "2000003002498", "22232=纽麦福全脂纯牛奶.1L* 12", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300250", "2000003002504", "55150=花旗起酥油（HSS42-05) 16kg . 1*1", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300251", "2000003002511", "11230=韩国幼砂糖30kg . 1*1", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300252", "2000003002528", "33398=众果果粒多茶酱-凤梨.1.3kg*12", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300253", "2000003002535", "黑珍珠0.7   1*20*900g", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300254", "2000003002542", "红小豆（三）3KG   1*6*3kg", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300255", "2000003002559", "吸管夹   1*10", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300256", "2000003002566", "钢柄压棒", "3002", "02 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300257", "2000003002573", "擀面杖30cm", "3002", "02 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300258", "2000003002580", "标准量匙   1*1000", "3002", "02 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300259", "2000003002597", "黑凉粉（颜） 1*30*500g", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300260", "2000003002603", "西米   1*30*900g", "3002", "03 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300261", "2000003002610", "餐3+1椰子奶茶   1*30*1kg", "3002", "01 ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300264", "2000003002641", "姐妹厨房经典盐酥鸡", "3002", "   ", 0.00)
                .updateSaleInfo(0, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300268", "2000003002689", "贝贝之星洋葱圈", "3002", "01 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300269", "2000003002696", "吉安香芋甜心", "3002", "04 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300270", "2000003002795", "QQ火龙果", "3002", "01 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300271", "1999990000147", "安德鲁颗粒果酱红枣生姜", "3002", "01 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300272", "1999990000130", "安德鲁颗粒果酱红丝绒混合", "3002", "01 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300273", "1999990000123", "辉山淳轩纯牛奶", "3002", "01 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300274", "1999990000116", "姐妹厨房吮指炸翅棍", "3002", "01 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300275", "1999990000109", "姐妹厨房吮指炸翅中", "3002", "01 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300276", "1999990000093", "安德鲁带籽西番莲颗粒果酱", "3002", "01 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300277", "1999990000086", "馥美乐经典沙司巧克力味1kg", "3002", "01 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300278", "1999990000079", "纳尔曼风味发酵乳", "3002", "01 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300279", "1999990000062", "贝贝之星美式红薯条", "3002", "01 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300280", "1999990000055", "巴黎塔脆骨盐酥鸡208g*36", "3002", "01 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300281", "1999990000048", "馥美乐经典沙司芝十味1kg", "3002", "01 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300282", "1999990000031", "雀巢100份小包装咖啡伴侣3g100小份", "3002", "01 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300283", "1999990000017", "雀巢100份醇品咖啡1.8g100小份", "3002", "01 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );
        prodList.add(new DepProduct("3002", "300284", "1999990000024", "安德鲁果酱密恋雪梨菊花颗粒果酱", "3002", "01 ", 0.00)
                .updateSaleInfo(1, 0, 0, 1, 0, 20.00, 0.00)
                .updateVipPrice(0.00, 0.00, 0.00, 0.00, 0.00, 0.00)
        );

        FlowManager.getDatabase(ZgpDb.class).executeTransaction(
                FastStoreModelTransaction
                        .insertBuilder(FlowManager.getModelAdapter(DepProduct.class))
                        .addAll(prodList)
                        .build());
    }

    private static void importDepPayInfo() {
        SQLite.delete(DepPayInfo.class).execute();
        List<DepPayInfo> payInfoList = new ArrayList<>();
        payInfoList.add(new DepPayInfo("2009", "0", "现金    ", "1"));
        payInfoList.add(new DepPayInfo("2009", "2", "微信支付", "3"));
        payInfoList.add(new DepPayInfo("2009", "3", "支付宝", "2"));
        payInfoList.add(new DepPayInfo("2009", "8", "储值卡  ", "4"));
        payInfoList.add(new DepPayInfo("2018", "0", "现金    ", "1"));
        payInfoList.add(new DepPayInfo("2018", "2", "微信支付", "3"));
        payInfoList.add(new DepPayInfo("2018", "3", "支付宝", "2"));
        payInfoList.add(new DepPayInfo("2018", "8", "储值卡  ", "4"));
        payInfoList.add(new DepPayInfo("2037", "0", "现金    ", "1"));
        payInfoList.add(new DepPayInfo("2037", "2", "微信支付", "3"));
        payInfoList.add(new DepPayInfo("2037", "3", "支付宝", "2"));
        payInfoList.add(new DepPayInfo("2037", "8", "储值卡  ", "4"));
        payInfoList.add(new DepPayInfo("3002", "0", "现金    ", "1"));
        payInfoList.add(new DepPayInfo("3002", "2", "微信支付", "3"));
        payInfoList.add(new DepPayInfo("3002", "3", "支付宝", "2"));
        payInfoList.add(new DepPayInfo("3002", "8", "储值卡  ", "4"));

        FlowManager.getDatabase(ZgpDb.class).executeTransaction(
                FastStoreModelTransaction
                        .insertBuilder(FlowManager.getModelAdapter(DepPayInfo.class))
                        .addAll(payInfoList)
                        .build());
    }

    private static void importSysParams() {
        SQLite.delete(SysParams.class).execute();
        List<SysParams> paramList = new ArrayList<>();
        paramList.add(new SysParams("AliPayAccount", "123456789"));
        paramList.add(new SysParams("NoClsDep", "2010,2018"));
        paramList.add(new SysParams("ProgramEdition", "百货版"));
        paramList.add(new SysParams("VipCardType", "1"));
        paramList.add(new SysParams("WxPayAccount", "123456789"));

        FlowManager.getDatabase(ZgpDb.class).executeTransaction(
                FastStoreModelTransaction
                        .insertBuilder(FlowManager.getModelAdapter(SysParams.class))
                        .addAll(paramList)
                        .build());
    }

    private static void importAppParams() {
        SQLite.delete(AppParams.class).execute();
        List<AppParams> paramList = new ArrayList<>();
        paramList.add(new AppParams("serverUrl", "192.168.1.20"));
        paramList.add(new AppParams("posCode", "201"));
        paramList.add(new AppParams("devSn", "EMULATOR29X0X11X0"));
        paramList.add(new AppParams("initFlag", "0"));
        paramList.add(new AppParams("lastUser", ""));
        paramList.add(new AppParams("lastDep", ""));
        paramList.add(new AppParams("printerConfig", "{}"));

        FlowManager.getDatabase(ZgpDb.class).executeTransaction(
                FastStoreModelTransaction
                        .insertBuilder(FlowManager.getModelAdapter(AppParams.class))
                        .addAll(paramList)
                        .build());
    }
}
