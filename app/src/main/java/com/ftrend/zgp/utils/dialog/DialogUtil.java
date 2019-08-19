package com.ftrend.zgp.utils.dialog;

import com.lxj.xpopup.XPopup;

/**
 * 模态窗口调用工具
 * 用法：先构建DialogBuilder然后把builder作为参数传入进来
 *
 * @author LZQ
 */
public class DialogUtil {

    public static void showTipDialog(DialogBuilder builder) {
        builder.setDialogType(0);
        new XPopup.Builder(builder.getBuilderContext())
                .asCustom(builder)
                .show();
    }


    public static void showWarningDialog(DialogBuilder builder) {
        builder.setDialogType(1);
        new XPopup.Builder(builder.getBuilderContext())
                .asCustom(builder)
                .show();
    }

    public static void showErrorDialog(DialogBuilder builder) {
        builder.setDialogType(2);
        new XPopup.Builder(builder.getBuilderContext())
                .asCustom(builder)
                .show();
    }

    public static void showAskDialog(DialogBuilder builder) {
        builder.setDialogType(3);
        new XPopup.Builder(builder.getBuilderContext())
                .asCustom(builder)
                .show();
    }


}
