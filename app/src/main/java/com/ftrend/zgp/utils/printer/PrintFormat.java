package com.ftrend.zgp.utils.printer;

import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 打印数据格式化
 *
 * @author liziqiang@ftrend.cn
 */

public class PrintFormat {
    /**
     * 根据打印纸宽度定义字符总数
     */
    public static int iSplitCount = 32;//默认是58mm=32的,也有80mm=48
    /**
     * 前半部分空格数量
     */
    private static int startSpace = iSplitCount == 32 ? 19 : 24;
    /**
     * 居左
     */
    public static final int ALIGN_LEFT = 0;
    /**
     * 居中
     */
    public static final int ALIGN_CENTER = 1;
    /**
     * 居右
     */
    public static final int ALIGN_RIGHT = 2;
    /**
     * 加粗指令
     */
    public static final byte[] BOLD_ON = {0x1B, 0x45, 0x1};
    /**
     * 取消加粗
     */
    public static final byte[] BOLD_OFF = {0x1B, 0x45, 0x0};
    /**
     * 中文区域
     */
    private static String ChinaRegEx = "[\u4e00-\u9fa5]";

    /**
     * @return 生成打印数据
     */
    public static List<PrintData> printFormat() {
        List<PrintData> printDataList = new ArrayList<>();
        PrintData printData;
        //分割线
        printDataList.add(newLine());
        //柜台名
        printData = new PrintData();
        printData.setBold(true);
        printData.setFontSize(36);
        printData.setAlign(ALIGN_LEFT);
        printData.setPrintData(ZgParams.getCurrentDep().getDepName());
        printDataList.add(printData);
        //流水号
        printData = new PrintData();
        printData.setInitStyle(true);
        printData.setAlign(ALIGN_LEFT);
        printData.setPrintData(String.format("%s%s", "流水号：",
                TradeHelper.getTrade().getLsNo()));
        printDataList.add(printData);
        //交易时间
        printData = new PrintData();
        printData.setInitStyle(true);
        printData.setAlign(ALIGN_LEFT);
        printData.setPrintData(mergeString("交易时间:", new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss").format(TradeHelper.getTrade().getTradeTime()), 32));
        printDataList.add(printData);
        //分割线
        printDataList.add(newLine());
        //商品明细标题
        //商品        数量    小计
        printData = new PrintData();
        printData.setInitStyle(true);
        printData.setPrintData(mergeTitle("商品", "数量", "小计", 32));
//        printData.setPrintData("商品--------------数量------小计");
        printDataList.add(printData);
        //分割线
        printDataList.add(newLine());
        //商品明细列表
        //商品名      数量     小计
        //健力宝      ×10   100.00
        printData = new PrintData();
        printData.setInitStyle(true);
        printData.setTypeList(true);
        printData.setProdList(TradeHelper.getProdList());
        printDataList.add(printData);
        //分割线
        printDataList.add(newLine());
        //支付方式
        printData = new PrintData();
        printData.setAlign(ALIGN_LEFT);
        printData.setPrintData(mergeString("支付方式：", TradeHelper.convertAppPayType(TradeHelper.getPay().getAppPayType()), 32));
        printDataList.add(printData);
        //商品原价
        printData = new PrintData();
        printData.setAlign(ALIGN_LEFT);
        printData.setPrintData(mergeString("原价总计：", String.format("￥%.2f", TradeHelper.getTradePrice()), 32));
        printDataList.add(printData);
        //优惠总计
        printData = new PrintData();
        printData.setAlign(ALIGN_LEFT);
        printData.setPrintData(mergeString("优惠总计：", String.format("￥-%.2f", TradeHelper.getTrade().getDscTotal()), 32));
        printDataList.add(printData);
        //合计金额
        printData = new PrintData();
        printData.setAlign(ALIGN_LEFT);
        printData.setPrintData(mergeString("合计金额：", String.format("￥%.2f", TradeHelper.getTrade().getTotal()), 32));
        printDataList.add(printData);
        //分割线
        printDataList.add(newLine());
        return printDataList;
    }

    /**
     * @return 分割线
     */
    private static PrintData newLine() {
        //分割线
        PrintData line = new PrintData();
        String lineStr = "";
        for (int i = 0; i < iSplitCount; i++) {
            lineStr += "-";
        }
        line.setAlign(ALIGN_CENTER);
        line.setBold(false);
        line.setInitStyle(true);
        line.setPrintData(lineStr);
        return line;
    }

    /**
     * 标题格式化
     *
     * @param start  左标题
     * @param mid    中间标题
     * @param end    右标题
     * @param length 长度
     * @return 文本
     */
    private static String mergeTitle(String start, String mid, String end, int length) {
        String resultStr = "";
        int startLength = 0;
        int midLength = 0;
        int endLength = 0;
        //TODO 24这个值不准确，需要拿到80mm机器实际测试
        startSpace = iSplitCount == 32 ? 19 : 24;
        //int chineseCharNum = 0;
        // 计算开始的字符的总长度和中文字符串的数量
        for (int i = 0; i < start.length(); i++) {
            String temp = start.substring(i, i + 1);
            //Log.i("","---->Start:"+isChineseChar(temp)+":"+temp);
            if (isChineseChar(temp)) {
                //chineseCharNum += 1;
                startLength += 2;
            } else {
                startLength += 1;
            }
        }

        for (int i = 0; i < mid.length(); i++) {
            String temp = mid.substring(i, i + 1);
            //Log.i("","---->Start:"+isChineseChar(temp)+":"+temp);
            if (isChineseChar(temp)) {
                //chineseCharNum += 1;
                midLength += 2;
            } else {
                midLength += 1;
            }
        }

        // 计算结束的字符串的长度和总长度
        for (int i = 0; i < end.length(); i++) {
            String temp = end.substring(i, i + 1);
            //Log.i("","---->End:"+isChineseChar(temp));
            if (isChineseChar(temp)) {
                //chineseCharNum += 1;
                endLength += 2;
            } else {
                endLength += 1;
            }
        }
        String space = "";
        for (int i = 0; i < startSpace - startLength; i++) {
            space += " ";
        }
        resultStr = start + space + mid;
        space = "";
        for (int i = 0; i < length - (startSpace + midLength + endLength); i++) {
            space += " ";
        }
        resultStr = resultStr + space + end;
        return resultStr;
    }

    /**
     * 拼接三个字符串
     * 58mm标题初始化样式下的标准
     * 商品--------------数量------小计
     *
     * @param start  首字符串
     * @param mid    中字符串
     * @param end    尾字符串
     * @param length 字符串总长度(58mm下初始化为32)
     * @return
     */
    public static String[] mergeString(String start, String mid, String end, int length) {
        String space = "";
        String firstLine = "";
        String secondLine = "";
        //直接换行，简单粗暴
        firstLine = start + "\n";
        //        计算字符数换行
//        if (startLength <= length) {
//            for (int i = 0; i < length - startLength - 1; i++) {
//                space += " ";
//            }
//            firstLine = start + space;
//        } else {
//            firstLine = start+"\n";
//        }
        for (int i = 0; i < startSpace; i++) {
            space += " ";
        }
        secondLine = space + mid;
        while (length - secondLine.length() - end.length() > 0) {
            secondLine += " ";
        }
        secondLine = secondLine + end;
        return new String[]{firstLine, secondLine};
    }


    /**
     * 拼接两个字符串
     *
     * @param start  开始的头字符串
     * @param end    结尾的尾字符串
     * @param length 字符串的总长度
     * @return 拼接之后的字符串
     */
    public static String mergeString(String start, String end, int length) {
        String rtnStr = "";
        int startLength = 0;
        int endLength = 0;
        //int chineseCharNum = 0;
        // 计算开始的字符的总长度和中文字符串的数量
        for (int i = 0; i < start.length(); i++) {
            String temp = start.substring(i, i + 1);
            //Log.i("","---->Start:"+isChineseChar(temp)+":"+temp);
            if (isChineseChar(temp)) {
                //chineseCharNum += 1;
                startLength += 2;
            } else {
                startLength += 1;
            }
        }

        // 计算结束的字符串的长度和总长度
        for (int i = 0; i < end.length(); i++) {
            String temp = end.substring(i, i + 1);
            //Log.i("","---->End:"+isChineseChar(temp));
            if (isChineseChar(temp)) {
                //chineseCharNum += 1;
                endLength += 2;
            } else {
                endLength += 1;
            }
        }

        String space = "";
        if ((endLength + startLength) <= length) {
            int loopNum = length - endLength - startLength;
            for (int i = 0; i < loopNum; i++) {
                space = space + " ";
            }
            rtnStr = start + space + end;
        } else {
            String temp = start + end;
//			rtnStr = temp.substring(0,length - 1 - chineseCharNum);
            for (int i = temp.length(); i > 0; i--) {
                String sub = temp.substring(0, i);
                int newLeng = 0;
                for (int j = 0; j < sub.length(); j++) {
                    String in = sub.substring(j, j + 1);
                    //Log.i("","---->Start:"+isChineseChar(temp)+":"+temp);
                    if (isChineseChar(in)) {
                        newLeng += 2;
                    } else {
                        newLeng += 1;
                    }
                }

                if (newLeng <= length) {
                    int loop = length - newLeng;
                    for (int j = 0; j < loop; j++) {
                        sub += " ";
                    }
                    rtnStr = sub;
                    break;
                }
            }
        }

        return rtnStr;
    }


    public static boolean isChineseChar(String s) {
        boolean rtn = false;
        Pattern p = Pattern.compile(ChinaRegEx);
        Matcher m = p.matcher(s);

        if (!m.find()) {
            if (s.getBytes().length == s.length()) {
                rtn = false;
            } else {
                rtn = true;
            }
        } else {
            rtn = true;
        }
        return rtn;
    }

    public static int getiSplitCount() {
        return iSplitCount;
    }

    public static void setiSplitCount(int iSplitCount) {
        PrintFormat.iSplitCount = iSplitCount;
    }
}
