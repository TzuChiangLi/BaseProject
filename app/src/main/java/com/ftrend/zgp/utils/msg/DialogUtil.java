package com.ftrend.zgp.utils.msg;

import android.content.Context;

import com.ftrend.zgp.utils.log.LogUtil;
import com.lxj.xpopup.XPopup;

/**
 * 模态窗口调用工具
 * 用法：先构建DialogBuilder然后把builder作为参数传入进来
 *
 * @author liziqiang@ftrend.cn
 */
public class DialogUtil {

    public static void showTipDialog(Context context, DialogBuilder builder) {
        builder.setDialogType(0);
        new XPopup.Builder(context)
                .asCustom(builder)
                .show();
    }


    public static void showWarningDialog(Context context, DialogBuilder builder) {
        builder.setDialogType(1);
        new XPopup.Builder(context)
                .asCustom(builder)
                .show();
    }

    public static void showErrorDialog(Context context, DialogBuilder builder) {
        builder.setDialogType(2);
        new XPopup.Builder(context)
                .asCustom(builder)
                .show();
    }

    public static void showAskDialog(Context context, DialogBuilder builder) {
        builder.setDialogType(3);
        new XPopup.Builder(context)
                .asCustom(builder)
                .show();
    }


}
