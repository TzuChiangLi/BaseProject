package com.ftrend.zgp.utils.task;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ftrend.zgp.model.AppParams;
import com.ftrend.zgp.model.AppParams_Table;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepCls_Table;
import com.ftrend.zgp.model.DepPayInfo;
import com.ftrend.zgp.model.DepPayInfo_Table;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.DepProduct_Table;
import com.ftrend.zgp.model.SysParams;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.utils.db.ZgpDb;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.List;
import java.util.Map;

/**
 * DataDownloadHelper
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/4
 */
public class DataDownloadHelper {
    private static final String TAG = "DataDownloadHelper";

    //专柜商品类别
    private static final String DATA_TYPE_DEP_CLS = "CLS";
    //专柜商品信息
    private static final String DATA_TYPE_DEP_PRODUCT = "PRD";
    //专柜支付类型
    private static final String DATA_TYPE_DEP_PAYINFO = "PAY";
    //系统参数
    private static final String DATA_TYPE_POS_SYSPARAMS = "SPR";
    //可登录专柜信息
    private static final String DATA_TYPE_POS_DEP = "DEP";
    //可登录用户信息
    private static final String DATA_TYPE_POS_USER = "USR";

    private static String makeKeyDepCls(String depCode) {
        return depCode + "_" + DATA_TYPE_DEP_CLS;
    }

    private static String makeKeyDepProduct(String depCode) {
        return depCode + "_" + DATA_TYPE_DEP_PRODUCT;
    }

    private static String makeKeyDepPayInfo(String depCode) {
        return depCode + "_" + DATA_TYPE_DEP_PAYINFO;
    }

    private static String makeKeyPosSysParams(String posCode) {
        return posCode + "_" + DATA_TYPE_POS_SYSPARAMS;
    }

    private static String makeKeyPosDep(String posCode) {
        return posCode + "_" + DATA_TYPE_POS_DEP;
    }

    private static String makeKeyPosUser(String posCode) {
        return posCode + "_" + DATA_TYPE_POS_USER;
    }

    public static boolean isDepCls(String dataType) {
        return DATA_TYPE_DEP_CLS.equalsIgnoreCase(dataType);
    }

    public static boolean isDepProduct(String dataType) {
        return DATA_TYPE_DEP_PRODUCT.equalsIgnoreCase(dataType);
    }

    public static boolean isDepPayInfo(String dataType) {
        return DATA_TYPE_DEP_PAYINFO.equalsIgnoreCase(dataType);
    }

    public static boolean isPosSysParams(String dataType) {
        return DATA_TYPE_POS_SYSPARAMS.equalsIgnoreCase(dataType);
    }

    public static boolean isPosDep(String dataType) {
        return DATA_TYPE_POS_DEP.equalsIgnoreCase(dataType);
    }

    public static boolean isPosUser(String dataType) {
        return DATA_TYPE_POS_USER.equalsIgnoreCase(dataType);
    }

    public static RestCallback makeCallback(final String dataType, final String code, final DownloadResultHandler handler) {
        return new RestCallback(new RestResultHandler() {
            @Override
            public void onSuccess(Map<String, Object> body) {
                if (!body.containsKey("list") || !body.containsKey("sign")) {
                    handler.onError("后台服务返回结果异常");
                    return;
                }
                final List<Map<String, Object>> dataList = (List<Map<String, Object>>) body.get("list");
                final String dataSign = body.get("sign").toString();
                //保存数据
                Transaction transaction = FlowManager.getDatabase(ZgpDb.class).beginTransactionAsync(new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        //全表更新
                        String dataKey;
                        if (isDepCls(dataType)) {
                            dataKey = makeKeyDepCls(code);
                            saveDepCls(code, dataList);
                        } else if (isDepPayInfo(dataType)) {
                            dataKey = makeKeyDepPayInfo(code);
                            saveDepPayInfo(code, dataList);
                        } else if (isDepProduct(dataType)) {
                            dataKey = makeKeyDepProduct(code);
                            saveDepProduct(code, dataList);
                        } else if (isPosDep(dataType)) {
                            dataKey = makeKeyPosDep(code);
                            savePosDep(dataList);
                        } else if (isPosSysParams(dataType)) {
                            dataKey = makeKeyPosSysParams(code);
                            savePosSysParams(dataList);
                        } else if (isPosUser(dataType)) {
                            dataKey = makeKeyPosUser(code);
                            savePosUser(dataList);
                        } else {
                            dataKey = code;
                        }
                        //更新数据更新标志
                        AppParams params = SQLite.select().from(AppParams.class)
                                .where(AppParams_Table.paramName.eq(dataKey))
                                .querySingle();
                        if (params == null) {
                            params = new AppParams();
                            params.setParamName(dataKey);
                        }
                        params.setParamValue(dataSign);
                        params.save();
                    }
                }).success(new Transaction.Success() {
                    @Override
                    public void onSuccess(@NonNull Transaction transaction) {
                        handler.onSuccess();
                    }
                }).error(new Transaction.Error() {
                    @Override
                    public void onError(@NonNull Transaction transaction, @NonNull Throwable error) {
                        Log.d(TAG, "onError: " + dataList);
                        handler.onError(String.format("更新数据(%s_%s)失败", code, dataType));
                    }
                }).build();
                transaction.execute();
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                handler.onError("下载数据发生异常");
            }
        });
    }

    /**
     * 更新可登录专柜信息
     *
     * @param depList
     */
    private static void savePosDep(final List<Map<String, Object>> depList) {
        //清空数据表
        SQLite.delete(Dep.class).execute();
        //写入数据
        for (Map<String, Object> map : depList) {
            Dep dep = new Dep();
            dep.setDepCode(String.valueOf(map.get("depCode")));
            dep.setDepName(String.valueOf(map.get("depName")));
            dep.insert();
        }
    }

    /**
     * 更新可登录用户信息
     *
     * @param userList
     */
    private static void savePosUser(final List<Map<String, Object>> userList) {
        //清空数据表
        SQLite.delete(User.class).execute();
        //写入数据
        for (Map<String, Object> map : userList) {
            User user = new User();
            user.setUserCode(String.valueOf(map.get("userCode")));
            user.setUserName(String.valueOf(map.get("userName")));
            user.setUserPwd(String.valueOf(map.get("userPwd")));
            user.setUserRights(String.valueOf(map.get("userRights")));
            user.setMaxDscRate(Float.valueOf(String.valueOf(map.get("maxDscRate"))).intValue());
            user.setMaxDscTotal(Float.valueOf(String.valueOf(map.get("maxDscTotal"))));
            user.setMaxTHTotal(Float.valueOf(String.valueOf(map.get("maxThTotal"))));
            user.insert();
        }
    }

    /**
     * 更新系统参数信息
     *
     * @param paramsList
     */
    private static void savePosSysParams(final List<Map<String, Object>> paramsList) {
        //清空数据表
        SQLite.delete(SysParams.class).execute();
        //写入数据
        for (Map<String, Object> map : paramsList) {
            SysParams param = new SysParams();
            param.setParamName(String.valueOf(map.get("paramName")));
            param.setParamValue(String.valueOf(map.get("paramValue")));
            param.insert();
        }
    }

    /**
     * 更新专柜商品类别信息
     *
     * @param depCode
     * @param clsList
     */
    private static void saveDepCls(final String depCode, final List<Map<String, Object>> clsList) {
        //清空数据表
        SQLite.delete(DepCls.class).where(DepCls_Table.depCode.eq(depCode)).execute();
        //写入数据
        for (Map<String, Object> map : clsList) {
            DepCls cls = new DepCls();
            cls.setDepCode(depCode);
            cls.setClsCode(String.valueOf(map.get("clsCode")));
            cls.setClsName(String.valueOf(map.get("clsName")));
            cls.insert();
        }
    }

    /**
     * 更新专柜商品信息
     *
     * @param depCode
     * @param productList
     */
    private static void saveDepProduct(final String depCode, final List<Map<String, Object>> productList) {
        //清空数据表
        SQLite.delete(DepProduct.class).where(DepProduct_Table.depCode.eq(depCode)).execute();
        //写入数据
        for (Map<String, Object> map : productList) {
            DepProduct product = new DepProduct();
            product.setProdCode(String.valueOf(map.get("prodCode")));
            product.setBarCode(String.valueOf(map.get("barCode")));
            product.setProdName(String.valueOf(map.get("prodName")));
            product.setDepCode(String.valueOf(map.get("depCode")));
            product.setClsCode(String.valueOf(map.get("clsCode")));
            product.setCargoNo(String.valueOf(map.get("cargoNo")));
            product.setSpec(String.valueOf(map.get("spec")));
            product.setUnit(String.valueOf(map.get("unit")));
            product.setPrice(Float.valueOf(String.valueOf(map.get("price"))));
            product.setBrand(String.valueOf(map.get("brand")));
            product.setPriceFlag(Integer.valueOf(String.valueOf(map.get("priceFlag"))));
//            product.set(String.valueOf(map.get("total")));
            product.setIsLargess(Integer.valueOf(String.valueOf(map.get("isLargess"))));
            product.setForSaleRet(Integer.valueOf(String.valueOf(map.get("forSaleRet"))));
            product.setForDsc(Integer.valueOf(String.valueOf(map.get("forDsc"))));
            product.setForLargess(Integer.valueOf(String.valueOf(map.get("forLargess"))));
            product.setScoreSet(Float.valueOf(String.valueOf(map.get("scoreSet"))));
            product.setVipPrice1(Float.valueOf(String.valueOf(map.get("vipPrice1"))));
            product.setVipPrice2(Float.valueOf(String.valueOf(map.get("vipPrice2"))));
            product.setVipPrice3(Float.valueOf(String.valueOf(map.get("vipPrice3"))));
            product.setVipRate1(Float.valueOf(String.valueOf(map.get("vipRate1"))));
            product.setVipRate2(Float.valueOf(String.valueOf(map.get("vipRate2"))));
            product.setVipRate3(Float.valueOf(String.valueOf(map.get("vipRate3"))));
            product.setMinimumPrice(Float.valueOf(String.valueOf(map.get("minimumPrice"))));
            product.insert();
        }
    }

    /**
     * 更新专柜商品类别信息
     *
     * @param depCode
     * @param payInfoList
     */
    private static void saveDepPayInfo(final String depCode, final List<Map<String, Object>> payInfoList) {
        //清空数据表
        SQLite.delete(DepPayInfo.class).where(DepPayInfo_Table.depCode.eq(depCode)).execute();
        //写入数据
        for (Map<String, Object> map : payInfoList) {
            DepPayInfo payInfo = new DepPayInfo();
            payInfo.setDepCode(depCode);
            payInfo.setPayTypeCode(String.valueOf(map.get("payTypeCode")));
            payInfo.setPayTypeName(String.valueOf(map.get("payTypeName")));
            payInfo.setAppPayType(String.valueOf(map.get("appPayType")));
            payInfo.insert();
        }
    }

    /**
     * 数据更新结果处理器
     */
    public interface DownloadResultHandler {
        void onSuccess();

        void onError(String msg);
    }
}
