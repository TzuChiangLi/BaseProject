package com.ftrend.zgp.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.DeviceUtils;
import com.ftrend.zgp.model.AppParams;
import com.ftrend.zgp.model.AppParams_Table;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepCls_Table;
import com.ftrend.zgp.model.SysParams;
import com.ftrend.zgp.model.SysParams_Table;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.pay.SqbConfig;
import com.ftrend.zgp.utils.printer.PrintConfig;
import com.ftrend.zgp.utils.sunmi.SunmiCardConfig;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;
import java.util.Locale;

import static com.raizlabs.android.dbflow.sql.language.Method.count;

/**
 * 全局参数
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/8/29
 */
public class ZgParams {

    private static String TAG = "ZgParams";

    //是否为商米设备
    private static boolean isSunmi = true;
    //是否联机模式
    private static volatile boolean isOnline = false;
    //联机状态消息
    public static final String MSG_ONLINE = "online mode";
    //单机状态消息
    public static final String MSG_OFFLINE = "offline mode";

    //系统参数：后台系统版本
    private static String programEdition = "";
    //系统参数：收钱吧参数
    private static SqbConfig sqbConfig = new SqbConfig();
    //系统参数：是否选择专柜(0-false,1-true)
    private static String useDep = "1";
    //系统参数：店铺名称
    private static String shopName = "";
    //系统参数：是否结算时打印小票
    private static boolean printBill = false;
    //系统参数：额外打印份数
    private static int printBillBak = 0;
    //系统参数：打印储值卡存根
    private static boolean prnCounterFoil = false;

    //本地参数：服务器地址
    private static String serverUrl = "";
    //本地参数：机器编号
    private static String posCode = "";
    //本地参数：设备识别码
    private static String devSn = "";
    //本地参数：初始化标识（0-未完成，1-已完成）
    private static String initFlag = "0";
    //本地参数：打印机设置
    private static PrintConfig printerConfig = new PrintConfig();
    //本地参数：读卡器设置
    private static SunmiCardConfig cardConfig = SunmiCardConfig.def();
    //本地参数：上次登录专柜
    private static String lastDep = "";
    //本地参数：上次登录用户
    private static String lastUser = "";
    //本地参数：上次生成的交易流水号
    private static String lastLsNo = "";
    //本地参数：收钱吧、储值卡、现金 0-关  1-开
    private static String[] payType = new String[]{"1", "1", "1"};
    //本地参数：会员刷卡商品
    private static String vipProd = "";
    //本地参数：手动输入数量   0-关  1-开
    private static String inputNum = "";
    //本地参数：输入小数 只有允许手动输入数量才能开启此选项
    private static String inputDecimal = "";

    //业务参数：本次登录的专柜
    private static Dep currentDep = new Dep();
    //业务参数：本次登录的用户
    private static User currentUser = new User();
    //业务参数：本机IP
    private static String currentIp = "";

    /**
     * 读取参数，包括系统参数和APP本地参数
     *
     * @return 是否成功
     */
    public static boolean loadParams() {
        //系统参数
        List<SysParams> sysParamsList = SQLite.select().from(SysParams.class).queryList();
        for (SysParams param : sysParamsList) {
            if ("CardConfig".equalsIgnoreCase(param.getParamName())) {
                cardConfig = SunmiCardConfig.fromJson(param.getParamValue());
            } else if ("SqbConfig".equalsIgnoreCase(param.getParamName())) {
                sqbConfig = SqbConfig.fromJson(param.getParamValue());
            } else if ("ProgramEdition".equalsIgnoreCase(param.getParamName())) {
                programEdition = param.getParamValue();
            } else if ("UseDep".equalsIgnoreCase(param.getParamName())) {
                //默认为true使用专柜
                useDep = param.getParamValue();
            } else if ("ShopName".equalsIgnoreCase(param.getParamName())) {
                shopName = param.getParamValue();
            } else if ("ICRWPwd".equalsIgnoreCase(param.getParamName())) {
                String pwd = SunmiCardConfig.formatM1Pwd(param.getParamValue().trim());
                cardConfig.setM1Key(pwd);
                cardConfig.setM1WKey(pwd);
            } else if ("PrnCounterFoil".equalsIgnoreCase(param.getParamName())) {
                prnCounterFoil = "True".equalsIgnoreCase(param.getParamValue());
            } else if ("PRINTBILLBAK".equalsIgnoreCase(param.getParamName())) {
                printBillBak = Integer.parseInt(param.getParamValue());
            } else if ("PRINTBILL".equalsIgnoreCase(param.getParamName())) {
                printBill = "True".equalsIgnoreCase(param.getParamValue());
            }
        }

        //本地参数
        List<AppParams> appParamsList = SQLite.select().from(AppParams.class)
                .where(AppParams_Table.paramName.notLike("UDP_%"))
                .queryList();
        for (AppParams param : appParamsList) {
            if ("serverUrl".equalsIgnoreCase(param.getParamName())) {
                serverUrl = param.getParamValue();
            } else if ("posCode".equalsIgnoreCase(param.getParamName())) {
                posCode = param.getParamValue();
            } else if ("devSn".equalsIgnoreCase(param.getParamName())) {
                devSn = param.getParamValue();
            } else if ("initFlag".equalsIgnoreCase(param.getParamName())) {
                initFlag = param.getParamValue();
            } else if ("printerConfig".equalsIgnoreCase(param.getParamName())) {
                printerConfig = PrintConfig.fromJson(param.getParamValue());
            } else if ("lastDep".equalsIgnoreCase(param.getParamName())) {
                lastDep = param.getParamValue();
            } else if ("lastUser".equalsIgnoreCase(param.getParamName())) {
                lastUser = param.getParamValue();
            } else if ("lastLsNo".equalsIgnoreCase(param.getParamName())) {
                lastLsNo = param.getParamValue();
            } else if ("PRINTBILL".equalsIgnoreCase(param.getParamName())) {
                printBill = "True".equalsIgnoreCase(param.getParamValue());
            } else if ("payType".equalsIgnoreCase(param.getParamName())) {
                for (int i = 0; i < payType.length; i++) {
                    payType[i] = String.valueOf(param.getParamValue().charAt(i));
                }
            } else if (String.format(Locale.CHINA, "vipProd_%s", ZgParams.getCurrentDep().getDepCode()).equalsIgnoreCase(param.getParamName())) {
                vipProd = param.getParamValue();
            } else if ("inputNum".equalsIgnoreCase(param.getParamName())) {
                inputNum = param.getParamValue();
            } else if ("inputDecimal".equalsIgnoreCase(param.getParamName())) {
                inputDecimal = param.getParamValue();
            }
        }
        //业务参数初始化
        currentIp = devSn.length() > 15 ? devSn.substring(0, 15) : devSn;
        isSunmi = "SUNMI".equalsIgnoreCase(DeviceUtils.getManufacturer());
        return true;
    }

    /**
     * 判断指定专柜是否使用商品类别
     *
     * @param depCode 专柜编码
     * @return
     */
    public static boolean isShowCls(String depCode) {
        long count = SQLite.select(count()).from(DepCls.class)
                .where(DepCls_Table.depCode.eq(depCode))
                .count();
        if (count <= 1) {
            return false;
        }
        return true;
    }

    /**
     * 当前后台是否百货版
     *
     * @return
     */
    public static boolean isBhEdition() {
        //参数值为空或者“超市版”时，为超市版；否则为“百货版”。
        return !TextUtils.isEmpty(programEdition) && !"超市版".equals(programEdition);
    }

    /**
     * @return 当前后台是否为睿尚版
     */
    public static boolean isRSEdition() {
        return !TextUtils.isEmpty(programEdition) && "睿尚版".equals(programEdition);
    }


    /**
     * 将本次登录信息保存
     *
     * @param user 当前用户
     * @param dep  当前柜台
     */
    public static void saveCurrentInfo(User user, Dep dep) {
        currentDep = dep;
        currentUser = user;
        saveAppParams("lastUser", user.getUserCode());
        saveAppParams("lastDep", dep.getDepCode());
    }

    /**
     * 清除登录信息（用于注销登录）
     */
    public static void clearCurrentInfo() {
        currentDep = new Dep();
        currentUser = new User();
        vipProd = "";
        TradeHelper.clearVip();
    }

    /**
     * 保存本地参数信息到数据库
     *
     * @param paramName  列名
     * @param paramValue 列值
     */
    public static boolean saveAppParams(String paramName, String paramValue) {
        AppParams appParams = SQLite.select().from(AppParams.class).where(AppParams_Table.paramName.eq(paramName)).querySingle();
        if (appParams == null) {
            appParams = new AppParams();
            appParams.setParamName(paramName);
        }
        appParams.setParamValue(paramValue);
        return appParams.save();
    }


    /**
     * 保存系统参数信息到数据库(系统参数一般不允许更改)
     *
     * @param paramName  列名
     * @param paramValue 列值
     */
    public static boolean saveSysParams(String paramName, String paramValue) {
        SysParams sysParams = SQLite.select().from(SysParams.class).where(SysParams_Table.paramName.eq(paramName)).querySingle();
        if (sysParams == null) {
            sysParams = new SysParams();
            sysParams.setParamName(paramName);
        }
        sysParams.setParamValue(paramValue);
        return sysParams.update();
    }

    /**
     * 设置初始化完成标志
     */
    public static void updateInitFlag() {
        saveAppParams("initFlag", "1");
    }

    /**
     * 启用或禁用收钱吧支付
     *
     * @param enabled
     */
    public static void enableSqb(boolean enabled) {
        payType[0] = enabled ? "1" : "0";
        StringBuilder result = new StringBuilder();
        for (String s : payType) {
            result.append(s);
        }
        ZgParams.saveAppParams("payType", result.toString());
    }

    public static boolean isPrintBill() {
        return printBill;
    }

    public static void setPrintBill(boolean printBill) {
        ZgParams.printBill = printBill;
    }

    public static boolean isPrnCounterFoil() {
        return prnCounterFoil;
    }

    public static void setPrnCounterFoil(boolean prnCounterFoil) {
        ZgParams.prnCounterFoil = prnCounterFoil;
    }

    public static int getPrintBillBak() {
        return printBillBak + 1;
    }

    public static void setPrintBillBak(int printTimes) {
        ZgParams.printBillBak = printBillBak;
    }

    /**
     * 1-选择,0-不选择
     *
     * @return 是否选择专柜
     */
    public static boolean getUseDep() {
        return "1".equals(useDep);
    }

    public static void setUseDep(String useDep) {
        ZgParams.useDep = useDep;
    }

    public static boolean isIsOnline() {
        return isOnline;
    }

    public static void setIsOnline(boolean isOnline) {
        ZgParams.isOnline = isOnline;
    }

    public static String getServerUrl() {
        return serverUrl;
    }

    public static void setServerUrl(String serverUrl) {
        ZgParams.serverUrl = serverUrl;
    }

    public static String getPosCode() {
        return posCode;
    }

    public static String getDevSn() {
        return devSn;
    }

    public static String getInitFlag() {
        return initFlag;
    }

    public static PrintConfig getPrinterConfig() {
        return printerConfig;
    }

    public static SunmiCardConfig getCardConfig() {
        return cardConfig;
    }

    public static SqbConfig getSqbConfig() {
        return sqbConfig;
    }

    public static String getLastDep() {
        return lastDep;
    }

    public static String getLastUser() {
        return lastUser;
    }

    public static Dep getCurrentDep() {
        return currentDep;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static String getCurrentIp() {
        return currentIp;
    }

    public static String getLastLsNo() {
        return lastLsNo;
    }

    public static void setLastLsNo(String lastLsNo) {
        ZgParams.lastLsNo = lastLsNo;
    }

    public static boolean isSunmi() {
        return isSunmi;
    }

    public static String getShopName() {
        return shopName;
    }

    public static String[] getPayType() {
        return payType;
    }

    public static void setPayType(String[] payType) {
        ZgParams.payType = payType;
    }

    public static String getVipProd() {
        return vipProd;
    }

    public static void setVipProd(String vipProd) {
        ZgParams.vipProd = vipProd;
    }

    public static String getInputNum() {
        return inputNum;
    }

    public static void setInputNum(String inputNum) {
        ZgParams.inputNum = inputNum;
    }

    public static String getInputDecimal() {
        return inputDecimal;
    }

    public static void setInputDecimal(String inputDecimal) {
        ZgParams.inputDecimal = inputDecimal;
    }
}
