package com.ftrend.zgp.utils.printer;

import com.blankj.utilcode.util.GsonUtils;

/**
 * PrintConfig
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/25
 */
public class PrintConfig {
    private boolean printTrade = false;

    public PrintConfig() {
    }

    public static PrintConfig fromJson(String json) {
        try {
            return GsonUtils.fromJson(json, PrintConfig.class);
        } catch (Exception e) {
            return new PrintConfig();
        }
    }

    public static String toJson(PrintConfig printConfig) {
        try {
            return GsonUtils.toJson(printConfig);
        } catch (Exception e) {
            return String.format("{}");
        }
    }

    public boolean isPrintTrade() {
        return printTrade;
    }

    public void setPrintTrade(boolean printTrade) {
        this.printTrade = printTrade;
    }
}
