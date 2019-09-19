package com.ftrend.zgp.utils;

import android.util.Log;

import com.blankj.utilcode.util.GsonUtils;
import com.ftrend.zgp.model.AppParams;
import com.ftrend.zgp.model.AppParams_Table;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.SysParams;
import com.ftrend.zgp.model.User;
import com.google.gson.JsonObject;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

/**
 * 全局参数
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/8/29
 */
public class ZgParams {

    //是否联机模式
    private static volatile boolean isOnline = false;
    //联机状态消息
    public static final String MSG_ONLINE = "online mode";
    //单机状态消息
    public static final String MSG_OFFLINE = "offline mode";

    //系统参数：是否使用商品类别
    private static String noClsDep = "";
    //系统参数：会员卡类型，1-磁卡，2-IC卡
    private static String vipCardType = "1";
    //系统参数：支付宝收款账号
    private static String aliPayAccount = "";
    //系统参数：微信支付收款账号
    private static String wxPayAccount = "";
    //系统参数：优惠版本 null或者“超市版”时为超市版，否则为百货版
    private static String programEdition = "";


    //本地参数：服务器地址
    private static String serverUrl = "";
    //本地参数：机器编号
    private static String posCode = "";
    //本地参数：设备识别码
    private static String devSn = "";
    //本地参数：初始化标识（0-未完成，1-已完成）
    private static String initFlag = "0";
    //本地参数：打印机设置
    private static Map<String, Object> printerConfig = new HashMap<>();
    //本地参数：读卡器设置
    private static Map<String, Object> cardConfig = new HashMap<>();
    //本地参数：上次登录专柜
    private static String lastDep = "";
    //本地参数：上次登录用户
    private static String lastUser = "";

    //业务参数：本次登录的专柜
    private static Dep currentDep = new Dep();
    //业务参数：本次登录的用户
    private static User currentUser = new User();
    //业务参数：本机IP
    private static String currentIp = "";

    /**
     * 读取参数，包括系统参数和APP本地参数
     *
     * @return
     */
    public static boolean loadParams() {
        //系统参数
        List<SysParams> sysParamsList = SQLite.select().from(SysParams.class).queryList();
        for (SysParams param : sysParamsList) {
            if ("noClsDep".equalsIgnoreCase(param.getParamName())) {
                noClsDep = "," + param.getParamValue() + ",";
            } else if ("vipCardType".equalsIgnoreCase(param.getParamName())) {
                vipCardType = param.getParamValue();
            } else if ("aliPayAccount".equalsIgnoreCase(param.getParamName())) {
                aliPayAccount = param.getParamValue();
            } else if ("wxPayAccount".equalsIgnoreCase(param.getParamName())) {
                wxPayAccount = param.getParamValue();
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
                parseJson(printerConfig, param.getParamValue());
            } else if ("cardConfig".equalsIgnoreCase(param.getParamName())) {
                parseJson(cardConfig, param.getParamValue());
            } else if ("lastDep".equalsIgnoreCase(param.getParamName())) {
                lastDep = param.getParamValue();
            } else if ("lastUser".equalsIgnoreCase(param.getParamName())) {
                lastUser = param.getParamValue();
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
        return noClsDep.contains("," + depCode + ",");
    }

    /**
     * 解析JSON格式参数
     *
     * @param map   输出解析结果到此Map对象
     * @param value json字符串
     */
    private static void parseJson(Map<String, Object> map, String value) {
        map.clear();
        try {
            JsonObject json = GsonUtils.fromJson(value, JsonObject.class);
            for (String key : json.keySet()) {
                map.put(key, json.getAsJsonObject(key));
            }
        } catch (Exception e) {
            Log.e(TAG, "parseJson: 解析JSON格式参数发生异常", e);
        }
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
    }

    /**
     * 保存本地参数信息到数据库
     *
     * @param paramName  列名
     * @param paramValue 列值
     */
    public static void saveAppParams(String paramName, String paramValue) {
        AppParams appParams = SQLite.select().from(AppParams.class).where(AppParams_Table.paramName.eq(paramName)).querySingle();
        appParams.setParamValue(paramValue);
        appParams.update();
    }


    public static void checkProgramEdition(){

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

    public static String getVipCardType() {
        return vipCardType;
    }

    public static String getAliPayAccount() {
        return aliPayAccount;
    }

    public static String getWxPayAccount() {
        return wxPayAccount;
    }

    public static Map<String, Object> getPrinterConfig() {
        return printerConfig;
    }

    public static Map<String, Object> getCardConfig() {
        return cardConfig;
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
}
