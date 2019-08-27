package com.ftrend.zgp.utils.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库创建、升级工具
 */
public class DBHelper extends SQLiteOpenHelper {
    /**
     * 数据库名称
     */
    private static final String DATABASE_NAME = "TEST";
    /**
     * 数据库版本号
     */
    private static final int DATABASE_VERSION = 0;
    /**
     * 专柜商品类别
     */
    private static final String CREATE_DEPCLS = "create table DepCls(" +
            "ID Integer primary key autoincrement not null," +
            "DepCode Text(12) not null," +
            "ClsCode Text(12) not null," +
            "ClsName Text(40) not null);";
    /**
     * 专柜商品信息
     */
    private static final String CREATE_DEPPRODUCT = "create table DepProduct(" +
            "ID Integer primary key autoincrement not null," +
            "ProdCode Text(18) not null,BarCode Text(18)," +
            "ProdName Text(40) not null,DepCode Text(12) not null," +
            "ClsCode Text(12) not null,CargoNo Text(15)," +
            "Spec Text(40) not null,Unit Text(40)," +
            "Price Decimal(12,4) default(0) not null,Brand Text(40)," +
            "PriceFlag Integer default(0),IsLargess Integer default(0),ForSaleRet Integer," +
            "ForDsc Integer ,ForLargess Integer default(0),ScoreSet Decimal(12,4)," +
            "VipPrice1 Decimal(12,4),VipPrice2 Decimal(12,4),VipPrice3 Decimal(12,4)," +
            "VipRate1 Decimal(12,4),VipRate2 Decimal(12,4),VipRate3 Decimal(12,4)," +
            "MinimumPrice Decimal(12,4));";
    /**
     * 专柜支付类型
     */
    private static final String CREATE_DEPPAYINFO = "create table DepPayInfo(" +
            "ID Integer primary key autoincrement not null," +
            "DepCode Text(12) not null,PayTypeCode Text(1) not null," +
            "PayTypeName Text(40) not null,AppPayType Text(1) not null);";
    /**
     * 交易流水表
     */
    private static final String CREATE_TRADE = "create table Trade(ID Integer primary key autoincrement not null," +
            "DepCode Text(12) not null,LsNo Text(8) not null,TradeTime Datetime(19)," +
            "TradeFlag Text(1) not null,Cashier Text(6) not null," +
            "DscTotal Decimal(12,4),Total Decimal(12,4),CustType Text(1)," +
            "VipCode Text(20),CardCode Text(20),VipTotal Decimal(12,4) default(0)," +
            "Status Text(1) default(0) not null);";
    /**
     * 交易商品表
     */
    private static final String CREATE_TRADEPROD = "create table TradeProd(" +
            "ID Integer primary key autoincrement not null," +
            "LsNo Text(8) not null,SortNo Integer(4) not null," +
            "ProdCode Text(18) not null,BarCode Text(18) not null," +
            "ProdName Text(40) default('1') not null,DepCode Text(12)," +
            "Price Decimal(8) default(0),Amount Decimal(8) default(0) not null," +
            "ManuDsc Decimal(8) default(0),TranDsc Decimal(8)," +
            "Total Decimal(8),VipTotal Decimal(8),SaleInfo Text(50)," +
            "DelFlag Text(0) default(0));";
    /**
     * 交易支付表
     */
    private static final String CREATE_TRADEPAY = "create table TradePay(" +
            "ID Integer primary key autoincrement not null," +
            "LsNo Text(8) not null,PayTypeCode Text(1) not null," +
            "Amount Decimal(12,4) not null,Change Decimal(12,4) default(0)," +
            "PayCode Text(20),PayTime Datetime(19) not null);";
    /**
     * 交班记录
     */
    private static final String CREATE_HANDOVER = "create table Handover(" +
            "ID Integer primary key autoincrement not null," +
            "HandoverNo Text(8) not null,HandoverTime Datetime(19) not null," +
            "DepCode Text(12) not null,Cashier Text(6) not null," +
            "LsNoMin Text(8) not null,LsNoMax Text(8) not null," +
            "SaleCount Decimal(12,4) default(0) not null," +
            "SaleTotal Decimal(12,4) default(0) not null," +
            "RtnCount Decimal(12,4) default(0) not null," +
            "DelCount Decimal(12,4) default(0) not null," +
            "DelTotal Decimal(12,4) default(0) not null," +
            "CancelCount Decimal(12,4) default(0) not null," +
            "CancelTotal Decimal(12,4) default(0) not null," +
            "HangupCount Decimal(12,4) default(0) not null," +
            "HangupTotal Decimal(12,4) default(0) not null," +
            "Status Text(1) default(0) not null);";
    /**
     * 交班记录(支付方式统计)
     */
    private static final String CREATE_HANDOVERPAY = "create table HandoverPay(" +
            "ID Integer primary key autoincrement not null," +
            "HandoverNo Text(8) not null,TradeFlag Text(1) not null," +
            "PayType Text(1) not null,SaleCount Decimal(12,4) default(0) not null," +
            "SaleTotal Decimal(12,4) default(0) not null," +
            "RtnCount Decimal(12,4) default(0) not null," +
            "RtnTotal Decimal(12,4) default(0) not null);";
    /**
     * 打印记录
     */
    private static final String CREATE_PRINTLOG = "create table PrintLog(" +
            "ID Integer primary key autoincrement not null," +
            "PrintType Text(1) not null,DocName Text(40) not null," +
            "PrintCount Integer not null,PrintTime Datetime not null," +
            "UserCode Text(6) not null);";
    /**
     * 系统参数
     */
    private static final String CREATE_SYSPARAMS = "create table SysParams(" +
            "ID Integer primary key autoincrement not null," +
            "ParamName Text(20) not null,ParamValue Text(100) not null);";
    /**
     * 可登录专柜信息
     */
    private static final String CREATE_DEP = "create table Dep(" +
            "ID Integer primary key autoincrement not null," +
            "DepCode Text(12) not null,DepName Text(40) not null);";
    /**
     * 可登录用户信息
     */
    private static final String CREATE_USER = "create table User(" +
            "ID Integer primary key autoincrement not null,UserCode Text(6) not null," +
            "UserName Text(10) not null,UserPwd Text(12) not null," +
            "UserRights Text(500) not null,MaxDscRate Integer(4) default(0) not null," +
            "MaxDscTotal Decimal(12,4) default(0) not null," +
            "MaxTHTotal Decimal(12,4) default(0) not null);";
    /**
     * 用户操作日志
     */
    private static final String CREATE_USERLOG = "create table UserLog(" +
            "ID Integer primary key autoincrement not null," +
            "Module Text(10),Function Text(10),OccurTime Datetime not null," +
            "Content Text(1000) not null,UserCode Text(6),DepCode Text(12));";
    /**
     * 交易流水上传队列
     */
    private static final String CREATE_TRADEUPLOADQUEUE = "create table TradeUploadQueue(" +
            "ID Integer primary key autoincrement not null," +
            "DepCode Text(12) not null,LsNo varchao(8) not null," +
            "EnquequeTime Datetime(19) not null,UploadTime Datetime(19) not null);";
    /**
     * App配置参数
     */
    private static final String CREATE_APPPARAMS = "create table AppParams("
            + "ID Integer primary key autoincrement not null,"
            + "ParamName Text(20) not null,"
            + "ParamValue Text(100) not null)";

    public DBHelper(Context context) {
        this(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_APPPARAMS);
        db.execSQL(CREATE_DEPCLS);
        db.execSQL(CREATE_DEPPRODUCT);
        db.execSQL(CREATE_DEPPAYINFO);
        db.execSQL(CREATE_TRADE);
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_USERLOG);
        db.execSQL(CREATE_TRADEUPLOADQUEUE);
        db.execSQL(CREATE_TRADEPROD);
        db.execSQL(CREATE_TRADEPAY);
        db.execSQL(CREATE_DEP);
        db.execSQL(CREATE_SYSPARAMS);
        db.execSQL(CREATE_PRINTLOG);
        db.execSQL(CREATE_HANDOVERPAY);
        db.execSQL(CREATE_HANDOVER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 1) {
            DataBaseUpdate.update_0_1(db);
        }
        if (oldVersion < 2) {
            DataBaseUpdate.update_1_2(db);
        }
        if (oldVersion < 3) {
            DataBaseUpdate.update_2_3(db);
        }
        if (oldVersion < 4) {
            DataBaseUpdate.update_3_4(db);
        }
        if (oldVersion < 5) {
            DataBaseUpdate.update_4_5(db);
        }
        if (oldVersion < 6) {
            DataBaseUpdate.update_5_6(db);
        }
    }
}
