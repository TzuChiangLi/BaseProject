package com.ftrend.zgp.utils.msg;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.ActivityUtils;
import com.ftrend.toast.XToast;
import com.ftrend.zgp.R;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.utils.pop.PayChargeDialog;
import com.ftrend.zgp.utils.pop.PriceDscDialog;
import com.ftrend.zgp.utils.pop.PriceMobileDialog;
import com.ftrend.zgp.utils.pop.VipCardDialog;
import com.ftrend.zgp.utils.pop.VipMoreBtnDialog;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import static com.ftrend.zgp.utils.pop.PriceDscDialog.DIALOG_SINGLE_RSC;
import static com.ftrend.zgp.utils.pop.PriceDscDialog.DIALOG_WHOLE_RSC;
import static com.ftrend.zgp.utils.pop.PriceMobileDialog.DIALOG_CHANGE_PRICE;
import static com.ftrend.zgp.utils.pop.PriceMobileDialog.DIALOG_MOBILE;

/**
 * @author liziqiang@ftrend.cn
 */
public class MessageUtil {

//-------------------------------------业务弹窗-----------------------------------------//

    /**
     * 会员登录方式选择
     *
     * @param context 上下文
     */
    public static void showVipWayDialog(Context context) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new VipMoreBtnDialog(context, 0))
                .show();
    }


    /**
     * 更多功能按钮
     *
     * @param context 上下文
     */
    public static void showMoreFuncDialog(Context context) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new VipMoreBtnDialog(context, 1))
                .show();
    }


    /**
     * 现金找零
     */
    public static void showChargeDialog(Context context, double total) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new PayChargeDialog(context, total))
                .show();
    }


    /**
     * 会员卡登录
     */
    public static void showVipCard(Context context) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new VipCardDialog(context))
                .show();
    }

    /**
     * 改价弹窗
     *
     * @param index 索引
     */
    public static void showPriceChange(Context context, int index) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new PriceMobileDialog(context, DIALOG_CHANGE_PRICE, index))
                .show();
    }

    /**
     * 会员输入弹窗
     *
     * @param index 索引
     */
    public static void showVipMobile(Context context, int index) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new PriceMobileDialog(context, DIALOG_MOBILE, index))
                .show();
    }

    /**
     * 单项优惠
     *
     * @param context 上下文
     * @param index   索引
     */
    public static void showSingleDscChange(Context context, int index) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new PriceDscDialog(context, DIALOG_SINGLE_RSC, index))
                .show();
    }

    /**
     * 整单优惠
     *
     * @param context 上下文
     */
    public static void showWholeDscChange(Context context) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new PriceDscDialog(context, DIALOG_WHOLE_RSC))
                .show();
    }


//-------------------------------------模态弹窗-----------------------------------------//

    //region 模态弹窗
    /**
     * 模态弹窗需要获取上下文
     */
    private static Context mContext;
    private static OnBtnClickListener mListener = null;
    private static DialogBuilder builder;

    public static void setMessageUtilClickListener(OnBtnClickListener mListener) {
        MessageUtil.mListener = mListener;
    }

    @Deprecated
    public interface OnBtnClickListener {
        /**
         * 左按钮响应
         *
         * @param popView 弹窗控件
         */
        void onLeftBtnClick(BasePopupView popView);

        /**
         * 右按钮响应
         *
         * @param popView 弹窗控件
         */
        void onRightBtnClick(BasePopupView popView);

    }

    public interface MessageBoxOkListener {
        void onOk();
    }

    public interface MessageBoxCancelListener {
        void onCancel();
    }

    public interface MessageBoxYesNoListener {
        void onYes();

        void onNo();
    }

    /**
     * 默认的OK按钮点击事件处理
     */
    private static MessageBoxOkListener defaultOkListener = new MessageBoxOkListener() {
        @Override
        public void onOk() {
        }
    };

    /**
     * 显示只有一个按钮的消息对话框
     *
     * @param message    消息内容
     * @param btnText    按钮文字
     * @param dialogType 对话框类型：0-info, 1-waring, 2-error
     * @param listener   按钮点击事件监听
     */
    private static void oneBtnDialog(String message, String btnText,
                                     DialogBuilder.DialogType dialogType,
                                     final MessageBoxOkListener listener) {
        mContext = BaseActivity.mContext;
        builder = new DialogBuilder(mContext, 1);
        builder.setContent(message);
        builder.setLeftBtn(btnText);
        builder.setDialogType(dialogType);
        builder.setOnClickListener(new DialogBuilder.OnBtnClickListener() {
            @Override
            public void onLeftBtnClick(BasePopupView v) {
                listener.onOk();
                v.dismiss();
            }

            @Override
            public void onRightBtnClick(BasePopupView v) {

            }
        });
        new XPopup.Builder(mContext)
                .dismissOnTouchOutside(false)
                .asCustom(builder)
                .show();
    }

    private static void oneBtnDialog(String message, String btnText,
                                     DialogBuilder.DialogType dialogType) {
        oneBtnDialog(message, btnText, dialogType, defaultOkListener);
    }

    private static void oneBtnDialog(String message, DialogBuilder.DialogType dialogType,
                                     MessageBoxOkListener listener) {
        oneBtnDialog(message, "确定", dialogType, listener);
    }

    private static void oneBtnDialog(String message, DialogBuilder.DialogType dialogType) {
        oneBtnDialog(message, dialogType, defaultOkListener);
    }

    /**
     * 提示弹窗
     *
     * @param message 提示文本
     */
    public static void info(String message) {
        oneBtnDialog(message, DialogBuilder.DialogType.info);
    }

    public static void info(String message, MessageBoxOkListener listener) {
        oneBtnDialog(message, DialogBuilder.DialogType.info, listener);
    }

    /**
     * 警告弹窗
     *
     * @param message 警告文本
     */
    public static void warning(String message) {
        oneBtnDialog(message, DialogBuilder.DialogType.warning);
    }

    public static void warning(String message, MessageBoxOkListener listener) {
        oneBtnDialog(message, DialogBuilder.DialogType.warning, listener);
    }

    /**
     * 错误弹窗
     *
     * @param message 错误文本
     */
    public static void error(String message) {
        oneBtnDialog(message, DialogBuilder.DialogType.error);
    }

    public static void error(String message, MessageBoxOkListener listener) {
        oneBtnDialog(message, DialogBuilder.DialogType.error, listener);
    }

    /**
     * 询问弹窗
     * 默认两个按钮
     *
     * @param message 询问文本
     */
    public static void question(String message, String btnTextYes, String btnTextNo,
                                final MessageBoxYesNoListener listener) {
        mContext = BaseActivity.mContext;
        builder = new DialogBuilder(mContext, 2);
        builder.setContent(message);
        builder.setLeftBtn(btnTextYes);
        builder.setRightBtn(btnTextNo);
        builder.setDialogType(DialogBuilder.DialogType.question);
        builder.setOnClickListener(new DialogBuilder.OnBtnClickListener() {
            @Override
            public void onLeftBtnClick(BasePopupView v) {
                listener.onYes();
                v.dismiss();
            }

            @Override
            public void onRightBtnClick(BasePopupView v) {
                listener.onNo();
                v.dismiss();
            }
        });
        new XPopup.Builder(mContext)
                .dismissOnTouchOutside(false)
                .asCustom(builder)
                .show();
    }

    public static void question(String message, final MessageBoxYesNoListener listener) {
        question(message, "是", "否", listener);
    }

    private static BasePopupView waitDialog = null;

    public static void waitBegin(String message, final MessageBoxCancelListener listener) {
        if (waitDialog != null) {
            waitEnd();
        }
        mContext = BaseActivity.mContext;
        builder = new DialogBuilder(mContext, 1);
        builder.setContent(message);
        builder.setLeftBtn("取消");
        builder.setDialogType(DialogBuilder.DialogType.wait);
        builder.setOnClickListener(new DialogBuilder.OnBtnClickListener() {
            @Override
            public void onLeftBtnClick(BasePopupView v) {
                if (listener != null) {
                    listener.onCancel();
                }
                waitEnd();
            }

            @Override
            public void onRightBtnClick(BasePopupView v) {

            }
        });
        waitDialog = new XPopup.Builder(mContext)
                .dismissOnTouchOutside(false)
                .asCustom(builder)
                .show();
    }

    public static void waitEnd() {
        if (waitDialog == null) {
            return;
        }
        waitDialog.dismiss();
        waitDialog = null;
    }

    //endregion


//-------------------------------------吐司工具-----------------------------------------//

    //region 吐司

    /**
     * 初始化
     */
    public static void init(Application app) {
        setAPP(app);
    }


    /**
     * 纯文字吐司
     *
     * @param msg 吐司文本
     */
    public static void show(String msg) {
        new XToast(ActivityUtils.getTopActivity())
                .setView(R.layout.toast_normal_hint)
                .setDuration(Duration)
                .setText(android.R.id.message, msg)
                .show();
    }


    /**
     * 成功
     */
    public static void showSuccess() {
        showSuccess("成功");
    }

    /**
     * 成功自定义文字
     *
     * @param text
     */
    public static void showSuccess(String text) {
        new XToast(ActivityUtils.getTopActivity())
                .setView(R.layout.toast_state_hint)
                .setDuration(Duration)
                .setImageDrawable(android.R.id.icon, R.drawable.dialog_state_success)
                .setText(android.R.id.message, text)
                .show();
    }

    /**
     * 错误
     */
    public static void showError() {
        showError("出现错误");
    }

    /**
     * 错误自定义文字
     *
     * @param text
     */
    public static void showError(String text) {
        new XToast(ActivityUtils.getTopActivity())
                .setView(R.layout.toast_state_hint)
                .setDuration(Duration)
                .setImageDrawable(android.R.id.icon, R.drawable.dialog_state_error)
                .setText(android.R.id.message, text)
                .show();
    }

    /**
     * 警告
     *
     * @param text
     */
    public static void showWarning(String text) {
        new XToast(ActivityUtils.getTopActivity())
                .setView(R.layout.toast_state_hint)
                .setDuration(Duration)
                .setImageDrawable(android.R.id.icon, R.drawable.dialog_state_warning)
                .setText(android.R.id.message, text)
                .show();
    }


    public static void setAPP(Application APP) {
        MessageUtil.APP = APP;
    }

    /**
     * 吐司工具初始化所需app
     */
    private static Application APP;
    /**
     * 设置吐司停留时长
     */
    private static int Duration = 1500;

    public static int getDuration() {
        return Duration;
    }

    public static void setDuration(int duration) {
        Duration = duration;
    }
    //endregion
}
