package com.ftrend.zgp.utils;

import android.text.TextUtils;

import com.ftrend.zgp.model.AppParams;
import com.ftrend.zgp.model.AppParams_Table;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.SysParams;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.utils.pay.SqbConfig;
import com.ftrend.zgp.utils.printer.PrintConfig;
import com.ftrend.zgp.utils.sunmi.SunmiCardConfig;
import com.ftrend.zgp.utils.sunmi.VipCardParams;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * 全局参数
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/8/29
 */
public class ZgParams {

    private static String TAG = "ZgParams";

    //是否联机模式
    private static volatile boolean isOnline = false;
    //联机状态消息
    public static final String MSG_ONLINE = "online mode";
    //单机状态消息
    public static final String MSG_OFFLINE = "offline mode";

    //系统参数：是否使用商品类别
    private static String noClsDep = "";
    //系统参数：后台系统版本
    private static String programEdition = "";
    //系统参数：收钱吧参数
    private static SqbConfig sqbConfig = new SqbConfig();
    //系统参数：会员卡参数
    private static VipCardParams vipCardParams = new VipCardParams();

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
            if ("noClsDep".equalsIgnoreCase(param.getParamName())) {
                noClsDep = "," + param.getParamValue() + ",";
            } else if ("CardConfig".equalsIgnoreCase(param.getParamName())) {
                cardConfig = SunmiCardConfig.fromJson(param.getParamValue());
            } else if ("SqbConfig".equalsIgnoreCase(param.getParamName())) {
                sqbConfig = SqbConfig.fromJson(param.getParamValue());
            } else if ("CardOpt".equalsIgnoreCase(param.getParamName())) {
                vipCardParams = VipCardParams.fromJson(param.getParamValue());
            } else if ("ProgramEdition".equalsIgnoreCase(param.getParamName())) {
                programEdition = param.getParamValue();
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
            }
        }

        //业务参数初始化
        currentIp = devSn.length() > 15 ? devSn.substring(0, 15) : devSn;

        return true;
    }

    /**
     * 判断指定专柜是否使用商品类别
     *
     * @param depCode 专柜编码
     * @return
     */
    public static boolean isShowCls(String depCode) {
        return !noClsDep.contains("," + depCode + ",");
    }

    /**
     * 当前后台是否百货版
     *
     * @return
     */
    public static boolean isBhEdition() {
        //参数值为空或者“超市版”时，为超市版；否则为“百货版”。
        return !TextUtils.isEmpty(programEdition) && !programEdition.equals("超市版");
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
     * 设置初始化完成标志
     */
    public static void updateInitFlag() {
        saveAppParams("initFlag", "1");
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

    public static VipCardParams getVipCardParams() {
        return vipCardParams;
    }

    public static String getLastLsNo() {
        return lastLsNo;
    }

    public static void setLastLsNo(String lastLsNo) {
        ZgParams.lastLsNo = lastLsNo;
    }
}
