package com.ftrend.zgp.utils;


/**
 * @author liziqiang@ftrend.cn
 */

public class FormatHelper {
    /**
     * 价格格式化
     *
     * @param before 格式化前的价格
     * @return 保留小数点后两位
     */
    public static double priceFormat(double before) {
        return Double.parseDouble(String.format("%.2f", before));
    }

    /**
     * 金额正则表达式
     *
     * @param price 价格
     * @return 是或否
     */
    public static boolean checkPriceFormat(Object price) {
//        String match = "　^[0-9]+(.[0-9]{2})?$";
        String match = "(?!^0*(\\.0{1,2})?$)^\\d{1,13}(\\.\\d{1,2})?$";

        return String.valueOf(price).matches(match);
    }

    /**
     * 非负整数正则表达式
     *
     * @param rate 折扣率
     * @return 是或否
     */
    public static boolean checkRateFormat(Object rate) {
        String match = "^[1-9]\\d*|0$";
        return String.valueOf(rate).matches(match);
    }
    /**
     * 手机号正则表达式
     *
     * @param phone
     * @return 是或否
     */
    public static boolean checkPhoneNoFormat(Object phone) {
        String match = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
        return String.valueOf(phone).matches(match);
    }
}
