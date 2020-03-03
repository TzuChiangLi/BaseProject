package com.ftrend.zgp;

import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.utils.UserRightsHelper;
import com.ftrend.zgp.utils.ZgParams;

import org.junit.Test;

/**
 * UserRightsTest
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/27
 */
public class UserRightsTest {
    @Test
    public void checkRights() {
        User user = new User();
        Dep dep = new Dep();
        ZgParams.saveCurrentInfo(user, dep);

        String rights = "011101111111011110";
        user.setUserRights(rights);
        System.out.println(user.getUserRights());
        System.out.println(rights.charAt(UserRightsHelper.SALE - 1));
        System.out.println(UserRightsHelper.hasRights(UserRightsHelper.SALE));
        System.out.println(rights.charAt(UserRightsHelper.REPORT - 1));
        System.out.println(UserRightsHelper.hasRights(UserRightsHelper.REPORT));

        rights = "111111111111111110";
        user.setUserRights(rights);
        System.out.println(user.getUserRights());
        System.out.println(rights.charAt(UserRightsHelper.SALE - 1));
        System.out.println(UserRightsHelper.hasRights(UserRightsHelper.SALE));
        System.out.println(rights.charAt(UserRightsHelper.REPORT - 1));
        System.out.println(UserRightsHelper.hasRights(UserRightsHelper.REPORT));

    }
}
