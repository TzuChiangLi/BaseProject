package com.ftrend.zgp.utils.printer;

import android.content.Context;

import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;

/**
 * 打印机
 *
 * @author liziqiang@ftrend.cn
 */
public class PrinterHelper {

    /**
     * 初始化打印机
     */
    public static void initPrinter(Context context, InnerPrinterCallback innerPrinterCallback) {
        try {
            if (innerPrinterCallback != null) {
                InnerPrinterManager.getInstance().bindService(context, innerPrinterCallback);
            }
        } catch (InnerPrinterException e) {
            e.printStackTrace();
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


}
