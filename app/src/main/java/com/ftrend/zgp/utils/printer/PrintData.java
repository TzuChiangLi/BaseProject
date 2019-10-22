package com.ftrend.zgp.utils.printer;

import com.ftrend.zgp.model.TradeProd;

import java.util.ArrayList;
import java.util.List;

/**
 * 打印数据
 *
 * @author CrazyBody
 */
public class PrintData {
    /**
     * 打印数据，文本字符串
     */
    private String printData;

    /**
     * 是否加粗
     */
    private boolean isBold;

    /**
     * 字体大小
     */
    private int fontSize;

    /**
     * 对齐方式,0==居左,1==居中,2==居右
     */
    private int align;

    /**
     * 是否使用原始样式
     */
    private boolean initStyle = false;

    /**
     * 是否为商品列表
     */
    private boolean isTypeList = false;

    /**
     * 商品列表
     */
    private List<TradeProd> prodList = new ArrayList<>();

    public String getPrintData() {
        return printData;
    }

    public void setPrintData(String printData) {
        this.printData = printData + "\n";
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    public boolean isBold() {
        return isBold;
    }

    public int getFontSize() {
        return fontSize;
    }

    public int getAlign() {
        return align;
    }

    public boolean isInitStyle() {
        return initStyle;
    }

    public void setInitStyle(boolean initStyle) {
        this.initStyle = initStyle;
    }

    public List<TradeProd> getProdList() {
        return prodList;
    }

    public void setProdList(List<TradeProd> prodList) {
        this.prodList = prodList;
    }

    public boolean isTypeList() {
        return isTypeList;
    }

    public void setTypeList(boolean typeList) {
        isTypeList = typeList;
    }
}