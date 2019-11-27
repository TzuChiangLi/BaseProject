package com.ftrend.zgp.utils.task;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.ftrend.zgp.model.AppParams;
import com.ftrend.zgp.model.AppParams_Table;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepCls_Table;
import com.ftrend.zgp.model.DepPayInfo;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.DepProduct_Table;
import com.ftrend.zgp.model.SysParams;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.utils.db.ZgpDb;
import com.ftrend.zgp.utils.http.RestBodyMap;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.List;

/**
 * 数据下载工具类
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

    /**
     * 数据下载服务请求回调，将下载的数据保存到数据中
     *
     * @param dataType 数据类型
     * @param code     机器号或专柜编码
     * @param handler  回调实现对象，用于发送处理结果
     * @return
     */
    public static RestCallback makeCallback(final String dataType, final String code, final DownloadResultHandler handler) {
        return new RestCallback(new RestResultHandler() {
            @Override
            public void onSuccess(RestBodyMap body) {
                final List<RestBodyMap> dataList = body.getMapList("list");
                final String dataSign = body.getString("sign");
                if (dataList == null || TextUtils.isEmpty(dataSign)) {
                    handler.onError("后台服务返回结果异常");
                    return;
                }
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
    private static void savePosDep(final List<RestBodyMap> depList) {
        //清空数据表
        SQLite.delete(Dep.class).execute();
        //写入数据
        for (RestBodyMap map : depList) {
            Dep dep = new Dep();
            dep.setDepCode(map.getString("depCode"));
            dep.setDepName(map.getString("depName"));
            dep.insert();
        }
    }

    /**
     * 更新可登录用户信息
     *
     * @param userList
     */
    private static void savePosUser(final List<RestBodyMap> userList) {
        //清空数据表
        SQLite.delete(User.class).execute();
        //写入数据
        for (RestBodyMap map : userList) {
            User user = new User();
            user.setUserCode(map.getString("userCode"));
            user.setUserName(map.getString("userName"));
            user.setUserPwd(map.getString("userPwd"));
            user.setUserRights(map.getString("userRights"));
            user.setMaxDscRate(map.getDouble("maxDscRate"));
            user.setMaxDscTotal(map.getDouble("maxDscTotal"));
            user.setMaxTHTotal(map.getDouble("maxThTotal"));
            user.insert();
        }
    }

    /**
     * 更新系统参数信息
     *
     * @param paramsList
     */
    private static void savePosSysParams(final List<RestBodyMap> paramsList) {
        //清空数据表
        SQLite.delete(SysParams.class).execute();
        //写入数据
        for (RestBodyMap map : paramsList) {
            SysParams param = new SysParams();
            param.setParamName(map.getString("paramName"));
            param.setParamValue(map.getString("paramValue"));
            param.insert();
        }
    }

    /**
     * 更新专柜商品类别信息
     *
     * @param depCode
     * @param clsList
     */
    private static void saveDepCls(final String depCode, final List<RestBodyMap> clsList) {
        //清空数据表
        SQLite.delete(DepCls.class).where(DepCls_Table.depCode.eq(depCode)).execute();
        //写入数据
        for (RestBodyMap map : clsList) {
            DepCls cls = new DepCls();
            cls.setDepCode(depCode);
            cls.setClsCode(map.getString("clsCode"));
            cls.setClsName(map.getString("clsName"));
            cls.insert();
        }
    }

    /**
     * 更新专柜商品信息
     *
     * @param depCode
     * @param productList
     */
    private static void saveDepProduct(final String depCode, final List<RestBodyMap> productList) {
        //清空数据表
        SQLite.delete(DepProduct.class).where(DepProduct_Table.depCode.eq(depCode)).execute();
        //写入数据
        for (RestBodyMap map : productList) {
            DepProduct product = new DepProduct();
            product.setProdCode(map.getString("prodCode"));
            product.setBarCode(map.getString("barCode"));
            product.setProdName(map.getString("prodName"));
            product.setDepCode(depCode);//专柜编号
            product.setProdDepCode(map.getString("depCode"));//商品所属部门编号
            product.setClsCode(map.getString("clsCode"));
            product.setCargoNo(map.getString("cargoNo"));
            product.setSpec(map.getString("spec"));
            product.setUnit(map.getString("unit"));
            product.setPrice(map.getDouble("price"));
            product.setBrand(map.getString("brand"));
            product.setPriceFlag(map.getInt("priceFlag"));
            product.setIsLargess(map.getInt("isLargess"));
            product.setForSaleRet(map.getInt("forSaleRet"));
            product.setForDsc(map.getInt("forDsc"));
            product.setForLargess(map.getInt("forLargess"));
            product.setScoreSet(map.getDouble("scoreSet"));
            product.setVipPrice1(map.getDouble("vipPrice1"));
            product.setVipPrice2(map.getDouble("vipPrice2"));
            product.setVipPrice3(map.getDouble("vipPrice3"));
            product.setVipRate1(map.getDouble("vipRate1"));
            product.setVipRate2(map.getDouble("vipRate2"));
            product.setVipRate3(map.getDouble("vipRate3"));
            product.setMinimumPrice(map.getDouble("minimumPrice"));
            product.setProdStatus(map.getString("prodStatus"));
            product.setSeason(map.getString("season"));
            product.insert();
        }
    }

    /**
     * 更新专柜商品类别信息
     *
     * @param depCode
     * @param payInfoList
     */
    private static void saveDepPayInfo(final String depCode, final List<RestBodyMap> payInfoList) {
        //清空数据表
        SQLite.delete(DepPayInfo.class)/*.where(DepPayInfo_Table.depCode.eq(depCode))*/.execute();//支付方式不再区分专柜
        //写入数据
        for (RestBodyMap map : payInfoList) {
            DepPayInfo payInfo = new DepPayInfo();
            payInfo.setDepCode(depCode);
            payInfo.setPayTypeCode(map.getString("payTypeCode"));
            payInfo.setPayTypeName(map.getString("payTypeName"));
            payInfo.setAppPayType(map.getString("appPayType"));
            payInfo.setIsScore(map.getString("isScore"));
            payInfo.insert();
        }
    }

    /**
     * 数据更新结果处理器
     */
    public interface DownloadResultHandler {
        /**
         * 成功
         */
        void onSuccess();

        /**
         * 失败
         *
         * @param msg 错误消息
         */
        void onError(String msg);
    }
}
