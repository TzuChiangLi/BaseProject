package com.ftrend.zgp.model;


import com.ftrend.zgp.utils.db.ZgpDb;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 商品信息
 *
 * @author liziqiang@ftrend.cn
 */
@Table(database = ZgpDb.class)
public class DepProduct extends BaseModel {
    @PrimaryKey(autoincrement = true)
    @NotNull
    private int id;
    @Column
    @NotNull
    private String depCode;
    @Column
    @NotNull
    private String prodCode;
    //以下数据库字段废弃,非空需要注意
    @Column
    private String prodName;
    @Column
    private String clsCode;
    @Column
    private String prodDepCode;

    public DepProduct() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProdCode() {
        return prodCode;
    }

    public void setProdCode(String prodCode) {
        this.prodCode = prodCode;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getClsCode() {
        return clsCode;
    }

    public void setClsCode(String clsCode) {
        this.clsCode = clsCode;
    }

    public String getProdDepCode() {
        return prodDepCode;
    }

    public void setProdDepCode(String prodDepCode) {
        this.prodDepCode = prodDepCode;
    }

    @Deprecated
    public DepProduct updateSaleInfo(int priceFlag, int isLargess, int forSaleRet, int forDsc,
                                     int forLargess, double scoreSet, double minimumPrice) {
        return this;
    }

    @Deprecated
    public DepProduct updateVipPrice(double vipPrice1, double vipPrice2, double vipPrice3,
                                     double vipRate1, double vipRate2, double vipRate3) {
        return this;
    }

}
