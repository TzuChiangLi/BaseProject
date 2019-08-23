package com.ftrend.zgp.utils.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    //数据库名称
    private static final String DATABASE_NAME = "TEST";
    //数据库版本号
    private static final int DATABASE_VERSION = 0;
    //暂时的测试表
    //专柜商品类别
    public static final String CREATE_DEPCLS = "create table DepCls(" +
            "ID Integer primary key autoincrement not null," +
            "DepCode varchar(12) not null," +
            "ClsCode varchar(12) not null," +
            "ClsName varchar(40) not null);";
    //专柜商品信息
    public static final String CREATE_DEPPRODUCT = "create table DepProduct(" +
            "ID Integer primary key autoincrement not null," +
            "ProdCode varchar(18) not null,BarCode varchar(18)," +
            "ProdName varchar(40) not null,DepCode varchar(12) not null," +
            "ClsCode varchar(12) not null,CargoNo varchar(15)," +
            "Spec varchar(40) not null,Unit varchar(40)," +
            "Price Decimal(12,4) default(0) not null,Brand varchar(40)," +
            "PriceFlag Integer default(0),IsLargess Integer default(0),ForSaleRet Integer," +
            "ForDsc Integer ,ForLargess Integer default(0),ScoreSet Decimal(12,4)," +
            "VipPrice1 Decimal(12,4),VipPrice2 Decimal(12,4),VipPrice3 Decimal(12,4)," +
            "VipRate1 Decimal(12,4),VipRate2 Decimal(12,4),VipRate3 Decimal(12,4)," +
            "MinimumPrice Decimal(12,4));";
    //专柜支付类型
    public static final String CREATE_DEPPAYINFO = "create table DepPayInfo(" +
            "ID Integer primary key autoincrement not null," +
            "DepCode varchar(12) not null,PayTypeCode varchar(1) not null," +
            "PayTypeName varchar(40) not null,AppPayType varchar(1) not null);";
    //交易流水表
    public static final String CREATE_TRADE = "create table Trade(ID Integer primary key autoincrement not null," +
            "DepCode varchar(12) not null,LsNo varchar(8) not null,TradeTime datetime(19)," +
            "TradeFlag varchar(1) not null,Cashier varchar(6) not null," +
            "DscTotal decimal(12,4),Total decimal(12,4),CustType varchar(1)," +
            "VipCode varchar(20),CardCode varchar(20),VipTotal decimal(12,4) default(0)," +
            "Status varchar(1) default(0) not null);";
    //交易商品表
    public static final String CREATE_TRADEPROD = "create table TradeProd(" +
            "ID Integer primary key autoincrement not null," +
            "LsNo varchar(8) not null,SortNo Integer(4) not null," +
            "ProdCode varchar(18) not null,BarCode varchar(18) not null," +
            "ProdName varchar(40) default('1') not null,DepCode varchar(12)," +
            "Price decimal(8) default(0),Amount decimal(8) default(0) not null," +
            "ManuDsc decimal(8) default(0),TranDsc decimal(8)," +
            "Total decimal(8),VipTotal decimal(8),SaleInfo varchar(50)," +
            "DelFlag varchar(0) default(0));";
    //交易支付表
    public static final String CREATE_TRADEPAY = "create table TradePay(" +
            "ID Integer primary key autoincrement not null," +
            "LsNo varchar(8) not null,PayType varchar(1) not null," +
            "Amount decimal(12,4) not null,Change decimal(12,4) default(0)," +
            "PayCode varchar(20),PayTime datetime(19) not null);";
    //交班记录
    public static final String CREATE_HANDOVER = "create table Handover(" +
            "ID Integer primary key autoincrement not null," +
            "HandoverNo varchar(8) not null,HandoverTime datetime(19) not null," +
            "DepCode varchar(12) not null,Cashier varchar(6) not null," +
            "LsNoMin varchar(8) not null,LsNoMax varchar(8) not null," +
            "SaleCount decimal(12,4) default(0) not null," +
            "SaleTotal decimal(12,4) default(0) not null," +
            "RtnCount decimal(12,4) default(0) not null," +
            "DelCount decimal(12,4) default(0) not null," +
            "DelTotal decimal(12,4) default(0) not null," +
            "CancelCount decimal(12,4) default(0) not null," +
            "CancelTotal decimal(12,4) default(0) not null," +
            "HangupCount decimal(12,4) default(0) not null," +
            "HangupTotal decimal(12,4) default(0) not null," +
            "Status varchar(1) default(0) not null);";
    //交班记录(支付方式统计)
    public static final String CREATE_HANDOVERPAY = "create table HandoverPay(" +
            "ID Integer primary key autoincrement not null," +
            "HandoverNo varchar(8) not null,TradeFlag varchar(1) not null," +
            "PayType varchar(1) not null,SaleCount decimal(12,4) default(0) not null," +
            "SaleTotal decimal(12,4) default(0) not null," +
            "RtnCount decimal(12,4) default(0) not null," +
            "RtnTotal decimal(12,4) default(0) not null);";
    //打印记录
    public static final String CREATE_PRINTLOG = "create table PrintLog(" +
            "ID Integer primary key autoincrement not null," +
            "PrintType varchar(1) not null,DocName varchar(40) not null," +
            "PrintCount Integer not null,PrintTime datetime not null," +
            "UserCode varchar(6) not null);";
    //系统参数
    public static final String CREATE_SYSPARAMS = "create table SysParams(" +
            "ID Integer primary key autoincrement not null," +
            "ParamName varchar(20) not null,ParamValue varchar(100) not null);";
    //可登录专柜信息
    public static final String CREATE_DEP = "create table Dep(" +
            "ID Integer primary key autoincrement not null," +
            "DepCode varchar(12) not null,DepName varchar(40) not null);";
    //可登录用户信息
    public static final String CREATE_USER = "create table User(" +
            "ID Integer primary key autoincrement not null,UserCode varchar(6) not null," +
            "UserName varchar(10) not null,UserPwd varchar(12) not null," +
            "UserRights varchar(500) not null,MaxDscRate Integer(4) default(0) not null," +
            "MaxDscTotal decimal(12,4) default(0) not null," +
            "MaxTHTotal decimal(12,4) default(0) not null);";
    //用户操作日志
    public static final String CREATE_USERLOG = "create table UserLog(" +
            "ID Integer primary key autoincrement not null," +
            "Module varchar(10),Function varchar(10),OccurTime datetime not null," +
            "Content varchar(1000) not null,UserCode varchar(6),DepCode varchar(12));";
    //交易流水上传队列
    public static final String CREATE_TRADEUPLOADQUEUE = "create table TradeUploadQueue(" +
            "ID Integer primary key autoincrement not null," +
            "DepCode varchar(12) not null,LsNo varchao(8) not null," +
            "EnquequeTime datetime(19) not null,UploadTime datetime(19) not null);";


    public static final String CREATE_APPPARAMS = "create table AppParams("
            + "ID integer primary key autoincrement not null,"
            + "ParamName varchar(20) not null,"
            + "ParamValue varchar(100) not null)";

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
