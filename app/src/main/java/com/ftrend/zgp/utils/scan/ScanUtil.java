package com.ftrend.zgp.utils.scan;

import com.sunmi.scan.Config;
import com.sunmi.scan.ImageScanner;
import com.sunmi.scan.Symbol;

/**
 * 扫码
 *
 * @author liziqiang@ftrend.cn
 */
public class ScanUtil {
    //声明扫描器
    private static ImageScanner scanner;

    private static ScanUtil INSTANCE;

    public ScanUtil() {
        initScannerConfig();
    }


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


    /**
     * 初始化SDK
     */
    private void initScannerConfig() {
        //行扫描间隔
        scanner.setConfig(0, Config.X_DENSITY, 2);
        //列扫描间隔
        scanner.setConfig(0, Config.Y_DENSITY, 2);
        scanner.setConfig(0, Config.ENABLE_MULTILESYMS, 0);
        //是否开启同一幅图一次解多个条码,0表示只解一个，1为多个
        //是否解反色的条码
        scanner.setConfig(0, Config.ENABLE_INVERSE, 0);
        //允许识读QR码，默认1:允许
        scanner.setConfig(Symbol.QRCODE, Config.ENABLE, 1);
        //允许识读PDF417码，默认0：禁止
        scanner.setConfig(Symbol.PDF417, Config.ENABLE, 1);
        //允许识读DataMatrix码，默认0：禁止
        scanner.setConfig(Symbol.DataMatrix, Config.ENABLE, 1);
        //允许识读AZTEC码，默认0：禁止
        scanner.setConfig(Symbol.AZTEC, Config.ENABLE, 1);
    }
}
