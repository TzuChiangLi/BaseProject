package com.ftrend.zgp.utils.printer;

import android.content.Context;
import android.os.RemoteException;

import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.log.LogUtil;
import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.SunmiPrinterService;

import java.util.List;

/**
 * 打印机
 *
 * @author liziqiang@ftrend.cn
 */
public class PrinterHelper {
    /**
     * 打印结果
     */
    private static boolean result = true;
    /**
     * 打印服务
     */
    private static SunmiPrinterService service;

    /**
     * 初始化打印机
     */
    public static void initPrinter(Context context, final PrintInitCallBack callBack) {
        try {
            if (callBack != null) {
                InnerPrinterManager.getInstance().bindService(context, new InnerPrinterCallback() {
                    @Override
                    protected void onConnected(SunmiPrinterService service) {
                        try {
                            switch (service.updatePrinterState()) {
                                case 1:
                                    //打印机工作正常
                                    PrinterHelper.service = service;
                                    callBack.onSuccess(service);
                                    break;
                                default:
                                    //TODO 还有其他错误代码
                                    callBack.onFailed();
                                    break;
                            }
                        } catch (Exception e) {
                            LogUtil.e("----printError:" + e.getMessage());
                        }
                    }

                    @Override
                    protected void onDisconnected() {
                        callBack.onFailed();
                    }
                });
            }
        } catch (InnerPrinterException e) {
            e.printStackTrace();
            LogUtil.e("printer:" + e.getMessage());
        }
    }

    /**
     * 解绑打印机服务
     */
    public static void unbindPrinter(Context context, InnerPrinterCallback innerPrinterCallback) {
        try {
            if (innerPrinterCallback != null) {
                InnerPrinterManager.getInstance().unBindService(context, innerPrinterCallback);
            }
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }


    /**
     * 打印
     *
     * @param printDataList 数据
     * @return 是否打印成功
     */
    public static boolean print(List<PrintData> printDataList) {
        if (!ZgParams.getPrinterConfig().isPrintTrade()) {
            return true;
        }
        try {
            for (PrintData p : printDataList) {
                //处理样式
                if (p.isInitStyle()) {
                    //初始化打印机样式设置
                    service.printerInit(null);
                } else {
                    //粗细
                    service.sendRAWData(p.isBold() ? PrintFormat.BOLD_ON : PrintFormat.BOLD_OFF, null);
                    //字体大小
                    service.setFontSize(p.getFontSize(), null);
                    //对齐方式
                    service.setAlignment(p.getAlign(), null);
                }
                //处理数据类型
                if (p.isTypeList()) {
                    //商品列表的处理
                    String[] result;
                    for (TradeProd prod : p.getProdList()) {
                        result = PrintFormat.mergeString(prod.getProdName(), String.format("%.0f", prod.getAmount()), String.format("%.2f", prod.getTotal()),
                                32);
                        service.printText(result[0], null);
                        service.printText(result[1], null);
                    }
                    service.lineWrap(1, null);
                } else {
                    //打印单行文本
                    service.printText(p.getPrintData(), null);
                }
            }
            service.lineWrap(4, null);
            result = true;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }


    /**
     * 打印机回调
     */
    public interface PrintInitCallBack {
        /**
         * 成功回调
         *
         * @param service 商米打印服务
         * @throws RemoteException 错误
         */
        void onSuccess(SunmiPrinterService service) throws RemoteException;

        /**
         * TODO 2019年10月18日17:07:27，考虑加错误代码
         */
        void onFailed();
    }
}
