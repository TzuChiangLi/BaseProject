package com.ftrend.zgp.utils;

import com.blankj.utilcode.util.StringUtils;
import com.ftrend.zgp.model.User;

/**
 * 用户操作权限判断功能类
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/27
 */
public class UserRightsHelper {
    // 1	P1001   	前台收款
    public static final int SALE = 1;
    // 2	P1002   	报表查询
    public static final int REPORT = 2;
    // 3	P1003   	优惠折扣
    public static final int DSC = 3;
    // 4	P1004   	赠送商品
    // 5	P1005   	销售结账
    public static final int PAY = 5;
    // 6	P1006   	销售退货
    public static final int REFUND = 6;
    // 7	P1007   	交易取消
    public static final int CANCEL_TRADE = 7;
    // 8	P1008   	单项取消
    public static final int CANCEL_PROD = 8;
    // 9	P1009   	商品查询
    // 10	P1010   	返利转储值
    // 11	P1011   	前台系统维护
    // 12	P1012	低于限卖价销售
    // 13	P1013	前台挂账
    // 14	P1014	非交易开钱箱
    // 15	P1015	历史报表查询
    public static final int HISTORY_REPORT = 15;
    // 16	P1016	退货时支付方式不受限
    public static final int REFUND_PAYTYPE = 16;
    // 17	P1017	存款发券
    // 18	P1018	卡券取款
    // 19	P1019	重打小票

    /**
     * 指定操作权限判断
     *
     * @param rightsNo
     * @return
     */
    public static boolean hasRights(int rightsNo) {
        User user = ZgParams.getCurrentUser();
        if (user == null) {
            return false;//用户未登录
        }
        String userRights = user.getUserRights();
        if (StringUtils.isEmpty(userRights) || userRights.length() < rightsNo) {
            return false;//用户权限数据异常
        }
        return userRights.substring(rightsNo - 1, rightsNo).equals("1");
    }

    /**
     * @param user 用户
     * @return 是否更新
     */
    public static boolean changeUserPwd(User user) {
        return user.save();
    }
}
