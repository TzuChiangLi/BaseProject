package com.ftrend.zgp.utils.scan;

import android.content.Intent;


/**
 * 扫码
 *
 * @author liziqiang@ftrend.cn
 */
public class ScanUtil {
    private static ScanUtil INSTANCE;

    /**
     * 创建单例
     *
     * @return ScanUtil的单例
     */
    public static ScanUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScanUtil();
        }
        return INSTANCE;
    }


    private void openScannerConfig() {
        Intent intent = new Intent("com.summi.scan");
        intent.setPackage("com.sunmi.sunmiqrcodescanner");
    }
}
