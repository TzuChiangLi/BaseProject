package com.ftrend.zgp.utils.pay;

import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.log.LogUtil;
import com.wosai.upay.bean.UpayOrder;
import com.wosai.upay.bean.UpayResult;
import com.wosai.upay.common.UpayCallBack;
import com.wosai.upay.common.UpayTask;

import java.util.Date;

/**
 * 收钱吧API接口功能类
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/11
 */
public class SqbPayHelper {
    /**
     * 收钱吧接入域名
     */
    public static final String apiDomain = "https://api.shouqianba.com";
    /**
     * 服务商ID
     */
    public static final String vendorId = "20160407103537";
    /**
     * 服务商Key
     */
    public static final String vendorKey = "a8bbaa0aeb3daf40f5924a3a9b694d00";
    /**
     * 服务商AppId
     */
    public static String appId = "";//ftrend.zgpos

    /**
     * 终端号
     */
    public static String terminalSn = "100035370009266786";
    /**
     * 终端密钥
     */
    public static String terminalKey = "316849581b75841b8b3f61f7cac0194f";
// "terminal_sn":"100035370009266786","terminal_key":"316849581b75841b8b3f61f7cac0194f",
// "merchant_sn":"1680000635494","merchant_name":"青岛方象",
// "store_sn":"1580000002217123","store_name":"方象测试"
    /**
     * 是否打开交易完成时,成功和失败的提示声音
     */
    public static boolean playSound = false;

    /**
     * 设备激活
     */
    public static void activate() {
        if (UpayTask.getInstance().isActivated()) {
            return;
        }
        String activateCode = "16060878";
        String deviceSn = "FT0001";
        String deviceName = "ZGPOS001";
        /**
         * activate 激活
         * @param code 激活码内容
         * @param vendor_id 服务商 ID
         * @param vendor_key 服务商 KEY
         * @param appId 服务商应用ID
         * @param deviceSn 设备号
         * @param deviceName 设备名称
         * @param payModel 支付模式
         * @param callBack 请求回调
         */
        UpayTask.getInstance().activate(activateCode, vendorId, vendorKey, UpayOrder.PayModel.NO_UI,
                new UpayCallBack() {
                    @Override
                    public void onExecuteResult(UpayResult result) {
                        LogUtil.e(result.toString());
                    }
                });
        //appId, deviceSn, deviceName,
    }

    /*
    https://api.shouqianba.com/upay/v2/pay
    {"terminal_sn":"100035370009271610","client_sn":"20112019101530100003","total_amount":"1",
     "dynamic_id":"288868636158142336","subject":"交易测试","operator":"508","description":"购物",
     "longitude":"4.9E-324","latitude":"4.9E-324","device_id":"1F0070AA33AB6AE195D74BC1899F3EA1",
     "reflect":"30100003"}
     Authorization:100035370009271610 100c09a03bce88e8df413808296a8606
     Content-Type:application/json

    UpayResult
    {terminal_sn='null', terminal_key='null', sn='7895238408655453', client_sn='client8673787',
     trade_no='242019101522001493110554510275', status='SUCCESS', order_status='PAID', payway='1',
     sub_payway='1', qr_code='null', payer_uid='2088002572693112', payer_login='red***@sina.com',
     total_amount='100', net_amount='100', subject='购物', finish_time='1571129271594',
     channel_finish_time='1571129271000', operator='508', description='零售商品', reflect='30100003',
     refund_request_no='null', result_code='PAY_SUCCESS', error_code='', error_message=''}
     */
    public static void pay(String scanCode) {
        Trade trade = TradeHelper.getTrade();
        trade.setTradeTime(new Date());
        //String clientSn = trade.getDepCode() + CommonUtil.dateToString(trade.getTradeTime()) + trade.getLsNo();
        String clientSn = "client" + (int) (Math.random() * 10000000);

        UpayOrder order = new UpayOrder();
        order.setClient_sn(clientSn);//商户订单号
        order.setTotal_amount("100");//交易总金额
        // order.setPayway("1");//支付方式--无需指定
        order.setDynamic_id(scanCode);//付款码内容
        order.setSubject("购物");//交易简介
        order.setOperator(trade.getCashier());//门店操作员
        order.setDescription("零售商品");//商品详情
        order.setReflect(trade.getLsNo());//反射参数
        order.setPayModel(UpayOrder.PayModel.NO_UI);//指定 SDK 启动模式为无界面模式

        UpayTask.getInstance().pay(order, new UpayCallBack() {
            @Override
            public void onExecuteResult(UpayResult result) {
                LogUtil.e(result.toString());
            }
        });
    }

    /*
    https://api.shouqianba.com/upay/v2/refund
    {"terminal_sn":"100035370009271610","client_sn":"client8673787","refund_request_no":"7894259244086017",
     "operator":"508","refund_amount":"100","longitude":"4.9E-324","latitude":"4.9E-324",
     "device_id":"1F0070AA33AB6AE195D74BC1899F3EA1","reflect":"7894259244086017"}

     UpayResult
     {terminal_sn='null', terminal_key='null', sn='7895238408655453', client_sn='client8673787',
      trade_no='242019101522001493110554510275', status='SUCCESS', order_status='REFUNDED',
      payway='1', sub_payway='1', qr_code='null', payer_uid='2088002572693112',
      payer_login='red***@sina.com', total_amount='100', net_amount='0', subject='购物',
      finish_time='1571129661145', channel_finish_time='1571129660000', operator='508',
      description='零售商品', reflect='7894259244086017', refund_request_no='7894259244086017',
      result_code='REFUND_SUCCESS', error_code='', error_message=''}
     */
    public static void refund(String clientSn) {
//        Trade trade = TradeHelper.getTrade();
        //String clientSn = trade.getDepCode() + CommonUtil.dateToString(trade.getTradeTime()) + trade.getLsNo();
        String requestNo = "7894259244086017";

        UpayOrder order = new UpayOrder();
        //无UI(sn和client_sn不能同时为空)
        //order.setSn("7894259244086017");//收钱吧订单号
        order.setClient_sn(clientSn);//商户订单号
        //商户退款所需序列号，用于唯一标识某次退款请求，以防止意外的重复退款。
        // 正常情况下，对同一笔订单进行多次退款请求时该字段不能重复；
        // 而当通信质量不佳，终端不确认退款请求是否成功，自动或手动发起的退款请求重试，则务必要保持序列号不变
        order.setRefund_request_no(requestNo);//退款序列号
        order.setOperator(ZgParams.getCurrentUser().getUserCode());//操作员
        order.setRefund_amount("100");//退款金额
        order.setReflect(requestNo);//反射参数
        order.setRefundModel(UpayOrder.RefundModel.CLIENT_SN);//指定退款模式为商户订单号退款
        order.setPayModel(UpayOrder.PayModel.NO_UI);//指定 SDK 启动模式为无界面模式

        UpayTask.getInstance().refund(order, new UpayCallBack() {
            @Override
            public void onExecuteResult(UpayResult result) {
                LogUtil.e(result.toString());
            }
        });
    }

    /*
UpayResult类说明
属性	属性名称	描述
sn	收钱吧订单号	收钱吧系统内部唯一订单号
client_sn	商户订单号	商户系统订单号
trade_no	支付服务商订单号	支付通道交易凭证号
status	流水状态	本次操作产生的流水的状态
order_status	订单状态	当前订单状态
payway	支付方式	一级支付方式(支付宝、微信、百付宝、京东)
sub_payway	二级支付方式	二级支付方式(条码支付、二维码支付)
payer_uid	付款人ID	支付平台(微信,支付宝)上的付款人 ID
payer_login	付款人账号	支付平台上(微信,支付宝)的付款人账号
total_amount	交易总额	本次交易总金额
net_amount	实收金额	如果没有退款,这个字段等于 total_amount。否则等于 total_amount 减去退款金额
subject	交易概述	本次交易概述
finish_time	付款动作在收钱吧的完成时间	时间戳
channel_finish_time	付款动作在支付服务商的完成时间	时间戳
operator	操作员	门店操作员
description	商品详情	对商品或本次交易的描述
reflect	反射参数	透传参数
qr_code	二维码内容	预下单成功后生成的二维码
result_code	业务执行响应码
error_code	业务执行结果返回码
error_message	业务执行错误信息
     */
}
