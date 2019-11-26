package com.ftrend.zgp.utils.msg;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

import com.blankj.utilcode.util.ActivityUtils;
import com.ftrend.zgp.utils.event.Event;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 等待消息提示框
 * 如果当前线程不是主线程，通过消息方式通知主线程来执行对应的方法
 * <p>
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin
 * @since 2019/11/26
 */
class WaitUtil {
    private static WaitUtil instance = null;

    public static WaitUtil getInstance() {
        if (instance == null) {
            instance = new WaitUtil();
        }
        return instance;
    }

    /**
     * 当前显示的等待提示框对象
     */
    private BasePopupView waitDialog = null;

    WaitUtil() {
        EventBus.getDefault().register(this);
    }

    boolean isWaiting() {
        return waitDialog != null && waitDialog.isShow();
    }

    /**
     * 显示等待提示框
     *
     * @param message
     * @param listener 取消按钮监听回调，如果不希望点击取消按钮立即关闭对话框，onCancel请返回false
     */
    void waitBegin(String message, final MessageUtil.MessageBoxCancelListener listener) {
        if (waitDialog != null) {
            waitEnd();
        }
        if (!isMainThread()) {
            postMessage(MSG_BEGIN, message, listener);
            return;
        }
        Context context = ActivityUtils.getTopActivity();
        DialogBuilder builder = new DialogBuilder(context, 1);
        builder.setContent(message);
        builder.setLeftBtn("取消");
        builder.setDialogType(DialogBuilder.DialogType.wait);
        builder.setOnClickListener(new DialogBuilder.OnBtnClickListener() {
            @Override
            public void onLeftBtnClick(BasePopupView v) {
                if (listener != null) {
                    if (listener.onCancel()) {
                        waitEnd();
                    }
                } else {
                    waitEnd();
                }
            }

            @Override
            public void onRightBtnClick(BasePopupView v) {

            }
        });
        waitDialog = new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(builder)
                .show();
    }

    /**
     * 更新等待提示框消息内容，可用于更新执行进度等
     *
     * @param msg
     */
    void waitUpdate(String msg) {
        if (waitDialog == null) {
            return;
        }
        if (!isMainThread()) {
            postMessage(MSG_UPDATE, msg);
            return;
        }
        if (waitDialog instanceof DialogBuilder) {
            ((DialogBuilder) waitDialog).updateMsg(msg);
        }
    }

    void waitUpdate(String msg, final MessageUtil.MessageBoxOkListener listener) {
        if (waitDialog == null) {
            return;
        }
        if (!isMainThread()) {
            postMessage(MSG_UPDATE, msg, listener);
            return;
        }
        if (waitDialog instanceof DialogBuilder) {
            ((DialogBuilder) waitDialog).updateMsg(msg);
            ((DialogBuilder) waitDialog).updateListener(listener);
        }
    }

    void waitUpdate(String msg, final MessageUtil.MessageBoxCancelListener listener) {
        if (waitDialog == null) {
            return;
        }
        if (!isMainThread()) {
            postMessage(MSG_UPDATE, msg, listener);
            return;
        }
        if (waitDialog instanceof DialogBuilder) {
            ((DialogBuilder) waitDialog).updateMsg(msg);
            ((DialogBuilder) waitDialog).updateListener(listener);
        }
    }

    /**
     * @param msg 内容文本
     */
    void waitError(String msg, final MessageUtil.MessageBoxOkListener listener) {
        if (waitDialog == null) {
            return;
        }
        if (!isMainThread()) {
            postMessage(MSG_ERROR, msg, listener);
            return;
        }
        if (waitDialog instanceof DialogBuilder) {
            ((DialogBuilder) waitDialog).updateError(msg);
            ((DialogBuilder) waitDialog).updateListener(listener);
        }
    }

    void waitError(String errCode, String errMsg, final MessageUtil.MessageBoxOkListener listener) {
        waitError(MessageUtil.formatErrorMsg(errCode, errMsg), listener);
    }

    void waitSuccesss(String msg, final MessageUtil.MessageBoxOkListener listener) {
        if (waitDialog == null) {
            return;
        }
        if (!isMainThread()) {
            postMessage(MSG_SUCCESS, msg, listener);
            return;
        }
        if (waitDialog instanceof DialogBuilder) {
            ((DialogBuilder) waitDialog).updateSucccess(msg);
            ((DialogBuilder) waitDialog).updateListener(listener);
        }
    }

    /**
     * 关闭等待提示框
     */
    void waitEnd() {
        if (waitDialog == null) {
            return;
        }
        if (!isMainThread()) {
            postMessage(MSG_END);
            return;
        }
        waitDialog.dismiss();
        waitDialog = null;
    }

    /**
     * 加载中圆圈对话框
     *
     * @param message 文本
     */
    void waitCircleProgress(String message) {
        if (!isMainThread()) {
            postMessage(MSG_PROGRESS, message);
            return;
        }
        Context context = ActivityUtils.getTopActivity();
        DialogBuilder builder = new DialogBuilder(context, 0);
        builder.setContent(TextUtils.isEmpty(message) ? "加载中" : message);
        builder.setDialogType(DialogBuilder.DialogType.wait_circle);
        waitDialog = new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .hasShadowBg(false)
                .asCustom(builder)
                .show();
    }

    private final int MSG_BEGIN = 0;
    private final int MSG_UPDATE = 1;
    private final int MSG_SUCCESS = 2;
    private final int MSG_ERROR = 3;
    private final int MSG_END = 4;
    private final int MSG_PROGRESS = 5;

    private String waitMsg = "";
    private MessageUtil.MessageBoxCancelListener cancelListener = null;
    private MessageUtil.MessageBoxOkListener okListener = null;

    private boolean isMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }

    private void postMessage(int msgId, String waitMsg,
                             MessageUtil.MessageBoxCancelListener cancelListener,
                             MessageUtil.MessageBoxOkListener okListener) {
        this.waitMsg = waitMsg;
        this.cancelListener = cancelListener;
        this.okListener = okListener;
        Event.sendEvent(Event.TARGET_WAIT_DIALOG, msgId);
    }

    private void postMessage(int msgId) {
        postMessage(msgId, "", null, null);
    }

    private void postMessage(int msgId, String waitMsg) {
        postMessage(msgId, waitMsg, null, null);
    }

    private void postMessage(int msgId, String waitMsg, MessageUtil.MessageBoxCancelListener listener) {
        postMessage(msgId, waitMsg, listener, null);
    }

    private void postMessage(int msgId, String waitMsg, MessageUtil.MessageBoxOkListener listener) {
        postMessage(msgId, waitMsg, null, listener);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onMessage(Event event) {
        if (event.getTarget() != Event.TARGET_WAIT_DIALOG) {
            return;
        }
        switch (event.getType()) {
            case MSG_BEGIN:
                waitBegin(waitMsg, cancelListener);
                break;
            case MSG_UPDATE:
                if (cancelListener != null) {
                    waitUpdate(waitMsg, cancelListener);
                } else if (okListener != null) {
                    waitUpdate(waitMsg, okListener);
                } else {
                    waitUpdate(waitMsg);
                }
                break;
            case MSG_SUCCESS:
                waitSuccesss(waitMsg, okListener);
                break;
            case MSG_ERROR:
                waitError(waitMsg, okListener);
                break;
            case MSG_END:
                waitEnd();
            case MSG_PROGRESS:
                waitCircleProgress(waitMsg);
                break;
            default:
                break;
        }
    }

}
