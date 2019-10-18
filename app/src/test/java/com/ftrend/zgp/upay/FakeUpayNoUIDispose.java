// 收钱吧SDK反编译文件，仅用于理解业务逻辑和排查错误
// 源文件已做混淆处理，根据对代码的理解对部分变量和方法名称做了识别，不保证与源文件一致

package com.ftrend.zgp.upay;

import android.content.Context;
import android.os.AsyncTask;

import com.qihoo.linker.logcollector.LogCollector;
import com.wosai.upay.bean.HttpResult;
import com.wosai.upay.bean.UpayOrder;
import com.wosai.upay.bean.UpayOrder.ExecuteType;
import com.wosai.upay.bean.UpayResult;
import com.wosai.upay.common.UpayCallBack;
import com.wosai.upay.common.UpayTask;
import com.wosai.upay.util.DateUtil;
import com.wosai.upay.util.DeviceUtil;
import com.wosai.upay.util.LogUtil;
import com.wosai.upay.util.PreferencesUtil;
import com.wosai.upay.util.StringUtil;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.SocketTimeoutException;
import java.util.Timer;

import javax.net.ssl.SSLPeerUnverifiedException;
//以下2个包无法导入，通过定义内部类避免错误提示
//import org.apache.http.conn.ConnectionPoolTimeoutException;
//import org.apache.http.conn.HttpHostConnectException;

public class FakeUpayNoUIDispose {
    public static String terminal_sn;
    public static String terminal_key;
    public static String vendor_id;
    public static String vendor_key;
    private UpayOrder upayOrder;
    private UpayCallBack upayCallBack;
    private Context context;
    private PreferencesUtil preferencesUtil;
    private Timer timer;
    private int f = 0;
    private int g = 0;
    private int h = 0;
    private int i = 0;
    private int j = 0;
    private int k = 0;
    private Long l = 0L;

    public FakeUpayNoUIDispose(UpayOrder order, UpayCallBack callBack, Context context) {
        this.upayOrder = order;
        this.upayCallBack = callBack;
        this.context = context;
        this.preferencesUtil = PreferencesUtil.getInstance(context);
    }

    public void activate() {
        terminal_sn = null;
        if (!DeviceUtil.isNetworkAvailable(this.context)) {
            this.upayCallBack.onExecuteResult(this.makeErrorResult(com.wosai.upay.enumerate.a.NETWORK_DISCONNECT.code(), com.wosai.upay.enumerate.a.NETWORK_DISCONNECT.msg()));
        } else {
            vendor_id = this.upayOrder.getVendor_id();
            vendor_key = this.upayOrder.getVendor_key();
            (new TaskActivate(this.upayOrder.getCode(), vendor_id, vendor_key, UpayTask.DEVICE_ID, UpayTask.OS_INFO, "2.1.4", this.upayOrder.getApp_id(), this.upayOrder.getDevice_sn(), this.upayOrder.getDevice_name())).execute(new Integer[]{35});
        }
    }

    public void start() {
        if (!DeviceUtil.isNetworkAvailable(this.context)) {
            this.upayCallBack.onExecuteResult(this.makeErrorResult(com.wosai.upay.enumerate.a.NETWORK_DISCONNECT.code(), com.wosai.upay.enumerate.a.NETWORK_DISCONNECT.msg()));
        } else {
            this.updateTerminalKey();
        }
    }

    private void runTask() {
        this.upayOrder.setTerminal_sn(terminal_sn);
        this.upayOrder.setTerminal_key(terminal_key);
        switch (this.upayOrder.getExecuteType()) {
            case PAY:
                (new TaskPay()).execute(new Integer[]{35});
                break;
            case REFUND:
                (new TaskRefund()).execute(new Integer[]{35});
                break;
            case QUERY:
                (new TaskQuery()).execute(new Integer[]{20});
                break;
            case PRECREATE:
                (new TaskPreCreate()).execute(new Integer[]{35});
                break;
            case CANCEL:
                (new TaskCancel()).execute(new Integer[]{35});
                break;
            case REVOKE:
                (new TaskRevoke()).execute(new Integer[]{35});
        }//ACTIVATE,

    }

    public void updateTerminalKey() {
        terminal_sn = this.preferencesUtil.getDecodeString("terminal_sn");
        terminal_key = this.preferencesUtil.getDecodeString("terminal_key");
        String keyEffectiveDate = this.preferencesUtil.getString("key_effective_date");
        String today = DateUtil.formatSystemDate("yyyy-MM-dd");
        if (!StringUtil.isEmpty(terminal_sn) && !StringUtil.isEmpty(terminal_key)) {
            if (!today.equals(keyEffectiveDate)) {//每天自动签到
                this.f = 0;
                this.g = 0;
                (new TaskSignIn(terminal_sn, terminal_key, UpayTask.DEVICE_ID, UpayTask.OS_INFO, "2.1.4")).execute(new Integer[]{20});
            } else {
                this.runTask();
            }
        } else {
            this.upayCallBack.onExecuteResult(new UpayResult(com.wosai.upay.enumerate.a.NOT_ACTIVATE.code(), com.wosai.upay.enumerate.a.NOT_ACTIVATE.msg()));
        }

    }

    private void logUpload() {
        //com.wosai.upay.common.b没有public构造方法，这里无关紧要
//        LogCollector.upload(false, new com.wosai.upay.common.b(this));
    }

    private void signIn(UpayResult var1) {
        if (this.f == 0) {
            LogUtil.saveLog(var1);
            (new TaskSignIn(terminal_sn, terminal_key, UpayTask.DEVICE_ID, UpayTask.OS_INFO, "2.1.4")).execute(new Integer[]{20});
            ++this.f;
        } else if (this.f == 1 && this.upayCallBack != null) {
            this.upayCallBack.onExecuteResult(var1);
            LogUtil.saveLog(var1);
        }

    }

    private UpayResult makeErrorResult(Exception var1) {
        UpayResult var2;
        if (var1 instanceof HttpHostConnectException) {
            var2 = new UpayResult(com.wosai.upay.enumerate.a.NETWORK_DISCONNECT.code(), com.wosai.upay.enumerate.a.NETWORK_DISCONNECT.msg());
        } else if (var1 instanceof ConnectionPoolTimeoutException) {
            var2 = new UpayResult(com.wosai.upay.enumerate.a.REQUEST_ERROR.code(), com.wosai.upay.enumerate.a.REQUEST_ERROR.msg());
        } else if (var1 instanceof ConnectTimeoutException) {
            var2 = new UpayResult(com.wosai.upay.enumerate.a.CONNECT_TIMEOUT.code(), com.wosai.upay.enumerate.a.CONNECT_TIMEOUT.msg());
        } else if (var1 instanceof SocketTimeoutException) {
            var2 = new UpayResult(com.wosai.upay.enumerate.a.REQUEST_TIMEOUT.code(), com.wosai.upay.enumerate.a.REQUEST_TIMEOUT.msg());
        } else if (var1 instanceof SSLPeerUnverifiedException) {
            var2 = new UpayResult(com.wosai.upay.enumerate.a.SSL_CHECK_FAIL.code(), com.wosai.upay.enumerate.a.SSL_CHECK_FAIL.msg());
        } else if (var1 instanceof com.wosai.upay.common.a) {
            var2 = this.makeErrorResult(400, com.wosai.upay.enumerate.a.INVALID_PARAMETER.code(), var1.getMessage());
        } else {
            LogUtil.saveLog(var1);
            var2 = new UpayResult(com.wosai.upay.enumerate.a.CLIENT_ERROR.code(), com.wosai.upay.enumerate.a.CLIENT_ERROR.msg());
        }

        var1.printStackTrace();
        return var2;
    }

    private UpayResult parseException(Exception e) {
        UpayResult upayResult = null;
        if (e instanceof HttpHostConnectException) {
            upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.NETWORK_DISCONNECT.code(), com.wosai.upay.enumerate.a.NETWORK_DISCONNECT.msg());
        } else if (e instanceof ConnectionPoolTimeoutException) {
            upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.REQUEST_ERROR.code(), com.wosai.upay.enumerate.a.REQUEST_ERROR.msg());
        } else if (e instanceof ConnectTimeoutException) {
            upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.CONNECT_TIMEOUT.code(), com.wosai.upay.enumerate.a.CONNECT_TIMEOUT.msg());
        } else if (e instanceof SocketTimeoutException) {
            switch (this.upayOrder.getExecuteType()) {
                case PAY:
                    if (this.h == 0) {
                        this.a(5, 20);
                        ++this.h;
                    } else if (this.h == 1) {
                        upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.PAY_FAIL.code(), com.wosai.upay.enumerate.a.PAY_FAIL.msg());
                    } else if (this.h == 2) {
                        upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.PAY_FAIL.code(), com.wosai.upay.enumerate.a.PAY_FAIL.msg());
                        this.h = 0;
                    }
                    break;
                case REFUND:
                    if (this.i == 0) {
                        (new TaskQuery2()).execute(new Integer[]{20});
                        ++this.i;
                    } else if (this.i == 1) {
                        upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.REFUND_FAIL.code(), com.wosai.upay.enumerate.a.REFUND_FAIL.msg());
                        this.i = 0;
                    }
                case QUERY:
                default:
                    break;
                case PRECREATE:
                    upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.PRECREATE_TIMEOUT.code(), com.wosai.upay.enumerate.a.PRECREATE_TIMEOUT.msg());
                    break;
                case CANCEL:
                    upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.CANCEL_FAIL.code(), com.wosai.upay.enumerate.a.CANCEL_FAIL.msg());
                    break;
                case REVOKE:
                    if (this.j == 0) {
                        (new TaskQuery2()).execute(new Integer[]{20});
                        ++this.j;
                    } else if (this.j == 1) {
                        upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.REVOKE_FAIL.code(), com.wosai.upay.enumerate.a.REVOKE_FAIL.msg());
                        this.j = 0;
                    }
            }
        } else if (e instanceof SSLPeerUnverifiedException) {
            upayResult = new UpayResult(com.wosai.upay.enumerate.a.SSL_CHECK_FAIL.code(), com.wosai.upay.enumerate.a.SSL_CHECK_FAIL.msg());
        } else if (e instanceof com.wosai.upay.common.a) {
            upayResult = this.makeErrorResult(400, com.wosai.upay.enumerate.a.INVALID_PARAMETER.code(), e.getMessage());
        } else {
            LogUtil.saveLog(e);
            upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.CLIENT_ERROR.code(), com.wosai.upay.enumerate.a.CLIENT_ERROR.msg());
        }

        e.printStackTrace();
        return upayResult;
    }

    private UpayResult makeErrorResult(UpayResult upayResult, Exception e) {
        if (e instanceof HttpHostConnectException) {
            upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.NETWORK_DISCONNECT.code(), com.wosai.upay.enumerate.a.NETWORK_DISCONNECT.msg());
        } else if (e instanceof ConnectionPoolTimeoutException) {
            upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.REQUEST_ERROR.code(), com.wosai.upay.enumerate.a.REQUEST_ERROR.msg());
        } else if (e instanceof ConnectTimeoutException) {
            upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.CONNECT_TIMEOUT.code(), com.wosai.upay.enumerate.a.CONNECT_TIMEOUT.msg());
        } else if (e instanceof SocketTimeoutException) {
            switch (this.upayOrder.getExecuteType()) {
                case PAY:
                case REFUND:
                case CANCEL:
                case REVOKE:
                default:
                    break;
                case QUERY:
                    upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.QUERY_TIMEOUT.code(), com.wosai.upay.enumerate.a.QUERY_TIMEOUT.msg());
                    break;
                case PRECREATE:
                    if (StringUtil.isEmpty(upayResult.getError_code())) {
                        upayResult.setError_code(com.wosai.upay.enumerate.a.QUERY_TIMEOUT.code());
                        upayResult.setError_message(com.wosai.upay.enumerate.a.QUERY_TIMEOUT.msg());
                    }
            }
        } else if (e instanceof SSLPeerUnverifiedException) {
            upayResult = new UpayResult(com.wosai.upay.enumerate.a.SSL_CHECK_FAIL.code(), com.wosai.upay.enumerate.a.SSL_CHECK_FAIL.msg());
        } else if (e instanceof com.wosai.upay.common.a) {
            upayResult = this.makeErrorResult(400, com.wosai.upay.enumerate.a.INVALID_PARAMETER.code(), e.getMessage());
        } else {
            LogUtil.saveLog(e);
            upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.CLIENT_ERROR.code(), com.wosai.upay.enumerate.a.CLIENT_ERROR.msg());
        }

        e.printStackTrace();
        return upayResult;
    }

    private void dealWithPayResult(HttpResult httpResult) {
        UpayResult upayResult;
        if (httpResult.getResult_code() == 200) {
            if (StringUtil.isNotEmpty(httpResult.getBiz_response())) {
                upayResult = UpayResult.parse(httpResult.getBiz_response());
                if (StringUtil.isNotEmpty(upayResult.getOrder_status())) {
                    String orderStatus = upayResult.getOrder_status();
                    byte var4 = -1;
                    switch (orderStatus.hashCode()) {
                        case -1079448719:
                            if (orderStatus.equals("PAY_ERROR")) {//UpayResult.ORDER_PAY_ERROR
                                var4 = 3;
                            }
                            break;
                        case 2448076:
                            if (orderStatus.equals("PAID")) {//UpayResult.ORDER_PAID
                                var4 = 1;
                            }
                            break;
                        case 1339099760:
                            if (orderStatus.equals("PAY_CANCELED")) {//UpayResult.ORDER_PAY_CANCELED
                                var4 = 2;
                            }
                            break;
                        case 1746537160:
                            if (orderStatus.equals("CREATED")) {//UpayResult.ORDER_CREATED
                                var4 = 0;
                            }
                    }

                    switch (var4) {
                        case 0://CREATED
                            this.upayOrder.setSn(upayResult.getSn());
                            (new TaskQuery(upayResult)).execute(new Integer[]{20});
                            break;
                        case 1://PAID
                        case 2://PAY_CANCELED
                            this.upayCallBack.onExecuteResult(upayResult);
                            LogUtil.saveLog(upayResult);
                            break;
                        case 3://PAY_ERROR
                            if (StringUtil.isEmpty(upayResult.getError_code())) {
                                upayResult.setError_code(com.wosai.upay.enumerate.a.PAY_FAIL.code());
                            }

                            if (StringUtil.isEmpty(upayResult.getError_message())) {
                                upayResult.setError_message(com.wosai.upay.enumerate.a.PAY_FAIL.msg());
                            }

                            this.upayCallBack.onExecuteResult(upayResult);
                            LogUtil.saveLog(upayResult);
                            break;
                        default:
                            this.upayOrder.setSn(upayResult.getSn());
                            (new TaskQuery(upayResult)).execute(new Integer[]{20});
                    }
                } else {//if (StringUtil.isNotEmpty(upayResult.getOrder_status())) {
                    this.upayCallBack.onExecuteResult(upayResult);
                    LogUtil.saveLog(upayResult);
                }
            } else {//if (StringUtil.isNotEmpty(httpResult.getBiz_response())) {
                upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.SERVER_ERROR.code(), com.wosai.upay.enumerate.a.SERVER_ERROR.msg());
                this.upayCallBack.onExecuteResult(upayResult);
                LogUtil.saveLog(upayResult);
            }
        } else {//if (httpResult.getResult_code() == 200) {
            upayResult = this.makeErrorResult(httpResult.getResult_code(), httpResult.getError_code(), httpResult.getError_message());
            this.upayCallBack.onExecuteResult(upayResult);
            LogUtil.saveLog(upayResult);
        }

    }

    private void dealWithRefundResult(HttpResult httpResult) {
        UpayResult upayResult;
        if (httpResult.getResult_code() == 200) {
            if (StringUtil.isNotEmpty(httpResult.getBiz_response())) {
                upayResult = UpayResult.parse(httpResult.getBiz_response());
                if (StringUtil.isNotEmpty(upayResult.getOrder_status())) {
                    String var3 = upayResult.getOrder_status();
                    byte var4 = -1;
                    switch (var3.hashCode()) {
                        case -114583967:
                            if (var3.equals("REFUND_ERROR")) {
                                var4 = 2;
                            }
                            break;
                        case 74702359:
                            if (var3.equals("REFUNDED")) {
                                var4 = 0;
                            }
                            break;
                        case 2041853749:
                            if (var3.equals("PARTIAL_REFUNDED")) {
                                var4 = 1;
                            }
                    }

                    switch (var4) {
                        case 0://REFUNDED
                        case 1://PARTIAL_REFUNDED
                            this.upayCallBack.onExecuteResult(upayResult);
                            LogUtil.saveLog(upayResult);
                            break;
                        case 2://REFUND_ERROR
                        default:
                            this.a(upayResult, 20, 20);
                    }
                } else {//if (StringUtil.isNotEmpty(upayResult.getOrder_status())) {
                    this.upayCallBack.onExecuteResult(upayResult);
                    LogUtil.saveLog(upayResult);
                }
            } else {//if (StringUtil.isNotEmpty(httpResult.getBiz_response())) {
                upayResult = this.makeErrorResult(com.wosai.upay.enumerate.a.SERVER_ERROR.code(), com.wosai.upay.enumerate.a.SERVER_ERROR.msg());
                this.upayCallBack.onExecuteResult(upayResult);
                LogUtil.saveLog(upayResult);
            }
        } else {//if (httpResult.getResult_code() == 200) {
            upayResult = this.makeErrorResult(httpResult.getResult_code(), httpResult.getError_code(), httpResult.getError_message());
            this.upayCallBack.onExecuteResult(upayResult);
            LogUtil.saveLog(upayResult);
        }

    }

    private void c(HttpResult var1) {
        UpayResult var2;
        if (var1.getResult_code() == 200) {
            if (StringUtil.isNotEmpty(var1.getBiz_response())) {
                var2 = UpayResult.parse(var1.getBiz_response());
                if (StringUtil.isNotEmpty(var2.getOrder_status())) {
                    String var3 = var2.getOrder_status();
                    byte var4 = -1;
                    switch (var3.hashCode()) {
                        case -1079448719:
                            if (var3.equals("PAY_ERROR")) {
                                var4 = 3;
                            }
                            break;
                        case 2448076:
                            if (var3.equals("PAID")) {
                                var4 = 1;
                            }
                            break;
                        case 1339099760:
                            if (var3.equals("PAY_CANCELED")) {
                                var4 = 2;
                            }
                            break;
                        case 1746537160:
                            if (var3.equals("CREATED")) {
                                var4 = 0;
                            }
                    }

                    switch (var4) {
                        case 0:
                            this.upayCallBack.onExecuteResult(var2);
                            this.upayOrder.setSn(var2.getSn());
                            this.l = System.currentTimeMillis();
                            this.a(var2, 5, 20);
                            break;
                        case 1:
                        case 2:
                        case 3:
                            this.upayCallBack.onExecuteResult(var2);
                            LogUtil.saveLog(var2);
                            break;
                        default:
                            this.upayOrder.setSn(var2.getSn());
                            (new TaskQuery(var2)).execute(new Integer[]{20});
                    }
                } else {
                    this.upayCallBack.onExecuteResult(var2);
                    LogUtil.saveLog(var2);
                }
            } else {
                var2 = this.makeErrorResult(com.wosai.upay.enumerate.a.SERVER_ERROR.code(), com.wosai.upay.enumerate.a.SERVER_ERROR.msg());
                this.upayCallBack.onExecuteResult(var2);
                LogUtil.saveLog(var2);
            }
        } else {
            var2 = this.makeErrorResult(var1.getResult_code(), var1.getError_code(), var1.getError_message());
            this.upayCallBack.onExecuteResult(var2);
            LogUtil.saveLog(var2);
        }

    }

    private void d(HttpResult var1) {
        UpayResult var2;
        if (var1.getResult_code() == 200) {
            if (StringUtil.isNotEmpty(var1.getBiz_response())) {
                var2 = UpayResult.parse(var1.getBiz_response());
                String var3 = var2.getResult_code();
                byte var4 = -1;
                switch (var3.hashCode()) {
                    case -2002684492:
                        if (var3.equals("CANCEL_ABORT_ERROR")) {
                            var4 = 3;
                        }
                        break;
                    case -822631249:
                        if (var3.equals("CANCEL_ABORT_SUCCESS")) {
                            var4 = 1;
                        }
                        break;
                    case 544766750:
                        if (var3.equals("CANCEL_SUCCESS")) {
                            var4 = 0;
                        }
                        break;
                    case 1743985635:
                        if (var3.equals("CANCEL_ERROR")) {
                            var4 = 2;
                        }
                }

                switch (var4) {
                    case 0:
                    case 1:
                        this.upayCallBack.onExecuteResult(var2);
                        LogUtil.saveLog(var2);
                        break;
                    case 2:
                    case 3:
                    default:
                        if (StringUtil.isEmpty(var2.getError_code())) {
                            var2.setError_code(com.wosai.upay.enumerate.a.CANCEL_FAIL.code());
                        }

                        if (StringUtil.isEmpty(var2.getError_message())) {
                            var2.setError_message(com.wosai.upay.enumerate.a.CANCEL_FAIL.msg());
                        }

                        this.upayCallBack.onExecuteResult(var2);
                        LogUtil.saveLog(var2);
                }
            } else {
                var2 = this.makeErrorResult(com.wosai.upay.enumerate.a.SERVER_ERROR.code(), com.wosai.upay.enumerate.a.SERVER_ERROR.msg());
                this.upayCallBack.onExecuteResult(var2);
                LogUtil.saveLog(var2);
            }
        } else {
            var2 = this.makeErrorResult(var1.getResult_code(), var1.getError_code(), var1.getError_message());
            this.upayCallBack.onExecuteResult(var2);
            LogUtil.saveLog(var2);
        }

    }

    private void e(HttpResult var1) {
        UpayResult var2;
        if (var1.getResult_code() == 200) {
            if (StringUtil.isNotEmpty(var1.getBiz_response())) {
                var2 = UpayResult.parse(var1.getBiz_response());
                if (StringUtil.isNotEmpty(var2.getOrder_status())) {
                    String var3 = var2.getOrder_status();
                    byte var4 = -1;
                    switch (var3.hashCode()) {
                        case -1079448719:
                            if (var3.equals("PAY_ERROR")) {
                                var4 = 3;
                            }
                            break;
                        case -114583967:
                            if (var3.equals("REFUND_ERROR")) {
                                var4 = 6;
                            }
                            break;
                        case 2448076:
                            if (var3.equals("PAID")) {
                                var4 = 1;
                            }
                            break;
                        case 74702359:
                            if (var3.equals("REFUNDED")) {
                                var4 = 4;
                            }
                            break;
                        case 659453081:
                            if (var3.equals("CANCELED")) {
                                var4 = 7;
                            }
                            break;
                        case 1339099760:
                            if (var3.equals("PAY_CANCELED")) {
                                var4 = 2;
                            }
                            break;
                        case 1743985635:
                            if (var3.equals("CANCEL_ERROR")) {
                                var4 = 8;
                            }
                            break;
                        case 1746537160:
                            if (var3.equals("CREATED")) {
                                var4 = 0;
                            }
                            break;
                        case 2041853749:
                            if (var3.equals("PARTIAL_REFUNDED")) {
                                var4 = 5;
                            }
                    }

                    switch (var4) {
                        case 0:
                            switch (this.upayOrder.getExecuteType()) {
                                case PAY:
                                    if (this.h == 1) {
                                        this.a(30, 20);
                                        ++this.h;
                                        return;
                                    } else {
                                        if (this.h == 2) {
                                            this.upayOrder.setExecuteType(ExecuteType.CANCEL);
                                            this.upayOrder.setSn(var2.getSn());
                                            (new TaskCancel()).execute(new Integer[]{35});
                                            this.h = 0;
                                        } else {
                                            if (StringUtil.isEmpty(var2.getError_code())) {
                                                var2.setError_code(com.wosai.upay.enumerate.a.PAY_FAIL.code());
                                                var2.setError_message(com.wosai.upay.enumerate.a.PAY_FAIL.msg());
                                            }

                                            this.upayCallBack.onExecuteResult(var2);
                                            LogUtil.saveLog(var2);
                                        }

                                        return;
                                    }
                                case REFUND:
                                case CANCEL:
                                case REVOKE:
                                default:
                                    return;
                                case QUERY:
                                    this.upayCallBack.onExecuteResult(var2);
                                    LogUtil.saveLog(var2);
                                    return;
                                case PRECREATE:
                                    if (System.currentTimeMillis() - this.l < 95000L) {
                                        this.a(var2, 5, 20);
                                    } else {
                                        this.upayCallBack.onExecuteResult(var2);
                                        LogUtil.saveLog(var2);
                                    }

                                    return;
                            }
                        case 1:
                            if (this.upayOrder.getExecuteType() == ExecuteType.PRECREATE || this.upayOrder.getExecuteType() == ExecuteType.PAY) {
                                var2.setResult_code("PAY_SUCCESS");
                            }
                        case 2:
                        case 3:
                        case 4:
                            this.upayCallBack.onExecuteResult(var2);
                            LogUtil.saveLog(var2);
                            break;
                        case 5:
                            String var5 = var2.getRefund_request_no();
                            if (this.upayOrder.getExecuteType().equals(ExecuteType.REFUND) && (StringUtil.isEmpty(var5) || !var5.equals(this.upayOrder.getRefund_request_no()))) {
                                var2 = this.makeErrorResult(com.wosai.upay.enumerate.a.NETWORK_ERROR.code(), com.wosai.upay.enumerate.a.NETWORK_ERROR.msg());
                                var2.setResult_code("REFUND_ERROR");
                            }

                            this.upayCallBack.onExecuteResult(var2);
                            LogUtil.saveLog(var2);
                            break;
                        case 6:
                            if (StringUtil.isEmpty(var2.getError_code())) {
                                var2.setError_code(com.wosai.upay.enumerate.a.REFUND_FAIL.code());
                            }

                            if (StringUtil.isEmpty(var2.getError_message())) {
                                var2.setError_message(com.wosai.upay.enumerate.a.REFUND_FAIL.msg());
                            }

                            this.upayCallBack.onExecuteResult(var2);
                            LogUtil.saveLog(var2);
                            break;
                        case 7:
                            this.upayCallBack.onExecuteResult(var2);
                            LogUtil.saveLog(var2);
                            break;
                        case 8:
                            if (StringUtil.isEmpty(var2.getError_code())) {
                                var2.setError_code(com.wosai.upay.enumerate.a.CANCEL_FAIL.code());
                            }

                            if (StringUtil.isEmpty(var2.getError_message())) {
                                var2.setError_message(com.wosai.upay.enumerate.a.CANCEL_FAIL.msg());
                            }

                            this.upayCallBack.onExecuteResult(var2);
                            LogUtil.saveLog(var2);
                    }
                } else {
                    this.upayCallBack.onExecuteResult(var2);
                    LogUtil.saveLog(var2);
                }
            } else {
                var2 = this.makeErrorResult(com.wosai.upay.enumerate.a.SERVER_ERROR.code(), com.wosai.upay.enumerate.a.SERVER_ERROR.msg());
                this.upayCallBack.onExecuteResult(var2);
                LogUtil.saveLog(var2);
            }
        } else {
            var2 = this.makeErrorResult(var1.getResult_code(), var1.getError_code(), var1.getError_message());
            this.upayCallBack.onExecuteResult(var2);
            LogUtil.saveLog(var2);
        }

    }

    private UpayResult makeErrorResult(int var1, String var2, String var3) {
        UpayResult var4 = new UpayResult();
        var4.setResult_code(var1 + "");
        var4.setError_code(var2);
        var4.setError_message(var3);
        var4.setClient_sn(this.upayOrder.getClient_sn());
        return var4;
    }

    private UpayResult makeErrorResult(String var1, String var2) {
        UpayResult var3 = new UpayResult();
        var3.setError_code(var1);
        var3.setError_message(var2);
        var3.setClient_sn(this.upayOrder.getClient_sn());
        var3.setSn(this.upayOrder.getSn());
        return var3;
    }

    private void a(int var1, int var2) {
        if (this.timer == null) {
            this.timer = new Timer();
            //com.wosai.upay.common.c继承自TimeTask，没有public构造方法，这里先注释掉
            //this.timer.schedule(new com.wosai.upay.common.c(this, var2), (long)(var1 * 1000));
        }

    }

    private void a(UpayResult var1, int var2, int var3) {
        if (this.timer == null) {
            this.timer = new Timer();
            //com.wosai.upay.common.d继承自TimeTask，没有public构造方法，这里先注释掉
            //this.timer.schedule(new com.wosai.upay.common.d(this, var1, var3), (long)(var2 * 1000));
        }

    }

    class TaskQuery extends AsyncTask<Integer, Void, HttpResult> {
        UpayResult a = null;

        public TaskQuery() {
        }

        public TaskQuery(UpayResult var2) {
            this.a = var2;
        }

        @Override
        protected HttpResult doInBackground(Integer... var1) {
            HttpResult var2 = null;

            try {
                com.wosai.upay.http.c.timeout = var1[0];
                var2 = com.wosai.upay.http.d.query(FakeUpayNoUIDispose.this.upayOrder);
            } catch (Exception var4) {
                this.a = FakeUpayNoUIDispose.this.makeErrorResult(this.a, var4);
            }

            return var2;
        }

        protected void onPostExecute(HttpResult var1) {
            if (var1 != null) {
                FakeUpayNoUIDispose.this.e(var1);
            } else if (this.a != null) {
                FakeUpayNoUIDispose.this.upayCallBack.onExecuteResult(this.a);
                LogUtil.saveLog(this.a);
            }

        }
    }

    class TaskQuery2 extends AsyncTask<Integer, Void, HttpResult> {
        UpayResult a = null;

        TaskQuery2() {
        }

        protected HttpResult doInBackground(Integer... var1) {
            HttpResult var2 = null;

            try {
                com.wosai.upay.http.c.timeout = var1[0];
                var2 = com.wosai.upay.http.d.query(FakeUpayNoUIDispose.this.upayOrder);
            } catch (Exception var4) {
                this.a = FakeUpayNoUIDispose.this.parseException(var4);
            }

            return var2;
        }

        protected void onPostExecute(HttpResult var1) {
            if (var1 != null) {
                FakeUpayNoUIDispose.this.e(var1);
            } else if (this.a != null) {
                FakeUpayNoUIDispose.this.upayCallBack.onExecuteResult(this.a);
                LogUtil.saveLog(this.a);
            }

        }
    }

    class TaskRevoke extends AsyncTask<Integer, Void, HttpResult> {
        UpayResult a = null;

        TaskRevoke() {
        }

        protected HttpResult doInBackground(Integer... var1) {
            HttpResult var2 = null;

            try {
                com.wosai.upay.http.c.timeout = var1[0];
                var2 = com.wosai.upay.http.d.revoke(FakeUpayNoUIDispose.this.upayOrder);
            } catch (Exception var4) {
                this.a = FakeUpayNoUIDispose.this.parseException(var4);
            }

            return var2;
        }

        protected void onPostExecute(HttpResult var1) {
            if (var1 != null) {
                FakeUpayNoUIDispose.this.d(var1);
            } else if (this.a != null) {
                FakeUpayNoUIDispose.this.upayCallBack.onExecuteResult(this.a);
                LogUtil.saveLog(this.a);
            }

        }
    }

    class TaskCancel extends AsyncTask<Integer, Void, HttpResult> {
        UpayResult a = null;

        TaskCancel() {
        }

        protected HttpResult doInBackground(Integer... var1) {
            HttpResult var2 = null;

            try {
                com.wosai.upay.http.c.timeout = var1[0];
                var2 = com.wosai.upay.http.d.cancel(FakeUpayNoUIDispose.this.upayOrder);
            } catch (Exception var4) {
                this.a = FakeUpayNoUIDispose.this.parseException(var4);
            }

            return var2;
        }

        protected void onPostExecute(HttpResult var1) {
            if (var1 != null) {
                FakeUpayNoUIDispose.this.d(var1);
            } else if (this.a != null) {
                FakeUpayNoUIDispose.this.upayCallBack.onExecuteResult(this.a);
                LogUtil.saveLog(this.a);
            }

        }
    }

    class TaskPreCreate extends AsyncTask<Integer, Void, HttpResult> {
        UpayResult a = null;

        TaskPreCreate() {
        }

        protected HttpResult doInBackground(Integer... var1) {
            HttpResult var2 = null;

            try {
                com.wosai.upay.http.c.timeout = var1[0];
                var2 = com.wosai.upay.http.d.preCreate(FakeUpayNoUIDispose.this.upayOrder);
            } catch (Exception var4) {
                this.a = FakeUpayNoUIDispose.this.parseException(var4);
            }

            return var2;
        }

        protected void onPostExecute(HttpResult var1) {
            if (var1 != null) {
                FakeUpayNoUIDispose.this.c(var1);
            } else if (this.a != null) {
                FakeUpayNoUIDispose.this.upayCallBack.onExecuteResult(this.a);
                LogUtil.saveLog(this.a);
            }

        }
    }

    class TaskRefund extends AsyncTask<Integer, Void, HttpResult> {
        UpayResult upayResult = null;

        TaskRefund() {
        }

        protected HttpResult doInBackground(Integer... var1) {
            HttpResult httpResult = null;

            try {
                com.wosai.upay.http.c.timeout = var1[0];
                httpResult = com.wosai.upay.http.d.refund(FakeUpayNoUIDispose.this.upayOrder);
            } catch (Exception e) {
                this.upayResult = FakeUpayNoUIDispose.this.parseException(e);
            }

            return httpResult;
        }

        protected void onPostExecute(HttpResult httpResult) {
            if (httpResult != null) {
                FakeUpayNoUIDispose.this.dealWithRefundResult(httpResult);
            } else if (this.upayResult != null) {
                FakeUpayNoUIDispose.this.upayCallBack.onExecuteResult(this.upayResult);
                LogUtil.saveLog(this.upayResult);
            }

        }
    }

    class TaskPay extends AsyncTask<Integer, Void, HttpResult> {
        UpayResult upayResult = null;

        TaskPay() {
        }

        protected HttpResult doInBackground(Integer... var1) {
            HttpResult httpResult = null;

            try {
                com.wosai.upay.http.c.timeout = var1[0];
                httpResult = com.wosai.upay.http.d.pay(FakeUpayNoUIDispose.this.upayOrder);
            } catch (Exception e) {
                this.upayResult = FakeUpayNoUIDispose.this.parseException(e);
            }

            return httpResult;
        }

        protected void onPostExecute(HttpResult httpResult) {
            if (httpResult != null) {
                FakeUpayNoUIDispose.this.dealWithPayResult(httpResult);
            } else if (this.upayResult != null) {
                FakeUpayNoUIDispose.this.upayCallBack.onExecuteResult(this.upayResult);
                LogUtil.saveLog(this.upayResult);
            }

        }
    }

    public class TaskSignIn extends AsyncTask<Integer, Void, HttpResult> {
        String a;
        String b;
        String c;
        String d;
        String e;
        UpayResult f = null;

        public TaskSignIn(String var2, String var3, String var4, String var5, String var6) {
            this.a = var2;
            this.b = var3;
            this.c = var4;
            this.d = var5;
            this.e = var6;
        }

        protected HttpResult doInBackground(Integer... var1) {
            HttpResult var2 = null;

            try {
                com.wosai.upay.http.c.timeout = var1[0];
                var2 = com.wosai.upay.http.d.signIn(this.a, this.b, this.c, this.d, this.e);
            } catch (Exception var4) {
                this.f = FakeUpayNoUIDispose.this.makeErrorResult(var4);
            }

            return var2;
        }

        protected void onPostExecute(HttpResult var1) {
            if (var1 != null) {
                if (var1.getResult_code() == 200) {
                    if (StringUtil.isNotEmpty(var1.getBiz_response())) {
                        this.f = UpayResult.parseActivate(var1.getBiz_response());
                        FakeUpayNoUIDispose.this.preferencesUtil.putEncodeString("terminal_key", this.f.getTerminal_key());
                        FakeUpayNoUIDispose.this.preferencesUtil.putString("key_effective_date", DateUtil.formatSystemDate("yyyy-MM-dd"));
                        this.b = this.f.getTerminal_key();
                        LogUtil.saveLog(this.f);
                        LogCollector.setTerminalInfo(this.f.getTerminal_sn(), this.f.getTerminal_key());
                        FakeUpayNoUIDispose.this.logUpload();
                    } else {
                        this.f = FakeUpayNoUIDispose.this.makeErrorResult(com.wosai.upay.enumerate.a.SERVER_ERROR.code(), com.wosai.upay.enumerate.a.SERVER_ERROR.msg());
                        FakeUpayNoUIDispose.this.upayCallBack.onExecuteResult(this.f);
                        LogUtil.saveLog(this.f);
                    }
                } else {
                    this.f = FakeUpayNoUIDispose.this.makeErrorResult(var1.getResult_code(), var1.getError_code(), var1.getError_message());
                    FakeUpayNoUIDispose.this.signIn(this.f);
                }
            } else {
                if (this.f == null) {
                    this.f = new UpayResult(com.wosai.upay.enumerate.a.CLIENT_ERROR.code(), com.wosai.upay.enumerate.a.CLIENT_ERROR.msg());
                }

                FakeUpayNoUIDispose.this.signIn(this.f);
            }

        }
    }

    public class TaskActivate extends AsyncTask<Integer, Void, HttpResult> {
        String a;
        String b;
        String c;
        String d;
        String e;
        String f;
        String g;
        String h;
        String i;
        UpayResult j = null;

        public TaskActivate(String var2, String var3, String var4, String var5, String var6, String var7, String var8, String var9, String var10) {
            this.a = var2;
            this.b = var3;
            this.c = var4;
            this.d = var5;
            this.e = var6;
            this.f = var7;
            this.g = var8;
            this.h = var9;
            this.i = var10;
        }

        protected HttpResult doInBackground(Integer... var1) {
            HttpResult var2 = null;

            try {
                com.wosai.upay.http.c.timeout = var1[0];
                var2 = com.wosai.upay.http.d.activate(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i);
            } catch (Exception var4) {
                this.j = FakeUpayNoUIDispose.this.makeErrorResult(var4);
            }

            return var2;
        }

        protected void onPostExecute(HttpResult var1) {
            if (var1 != null) {
                if (var1.getResult_code() == 200) {
                    if (StringUtil.isNotEmpty(var1.getBiz_response())) {
                        this.j = UpayResult.parseActivate(var1.getBiz_response());
                        FakeUpayNoUIDispose.this.preferencesUtil.putEncodeString("terminal_sn", this.j.getTerminal_sn());
                        FakeUpayNoUIDispose.this.preferencesUtil.putEncodeString("terminal_key", this.j.getTerminal_key());
                        FakeUpayNoUIDispose.this.preferencesUtil.putString("key_effective_date", DateUtil.formatSystemDate("yyyy-MM-dd"));
                        this.j = new UpayResult(com.wosai.upay.enumerate.a.ACTIVATE_SUCCESS.code());
                        FakeUpayNoUIDispose.this.upayCallBack.onExecuteResult(this.j);
                    } else {
                        this.j = FakeUpayNoUIDispose.this.makeErrorResult(com.wosai.upay.enumerate.a.SERVER_ERROR.code(), com.wosai.upay.enumerate.a.SERVER_ERROR.msg());
                        FakeUpayNoUIDispose.this.upayCallBack.onExecuteResult(this.j);
                    }
                } else {
                    this.j = new UpayResult(var1.getResult_code() + "", var1.getError_code(), var1.getError_message());
                    FakeUpayNoUIDispose.this.upayCallBack.onExecuteResult(this.j);
                }
            } else if (this.j != null) {
                FakeUpayNoUIDispose.this.upayCallBack.onExecuteResult(this.j);
            } else {
                this.j = new UpayResult(com.wosai.upay.enumerate.a.CLIENT_ERROR.code(), com.wosai.upay.enumerate.a.CLIENT_ERROR.msg());
                FakeUpayNoUIDispose.this.upayCallBack.onExecuteResult(this.j);
            }

            LogUtil.saveLog(this.j);
        }
    }

    private class ConnectionPoolTimeoutException extends Exception {

    }

    private class HttpHostConnectException extends Exception {

    }
}
