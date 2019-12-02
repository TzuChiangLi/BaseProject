package com.ftrend.zgp.view;

import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.ftrend.zgp.R;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.RestBodyMap;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.msg.InputPanel;
import com.ftrend.zgp.utils.msg.MessageUtil;
import com.ftrend.zgp.utils.pop.DateRangeInputCallback;
import com.ftrend.zgp.utils.printer.PrintFormat;
import com.ftrend.zgp.utils.printer.PrinterHelper;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.sunmi.peripheral.printer.SunmiPrinterService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.OnClick;

public class TradeReportActivity extends BaseActivity implements OnTitleBarListener {
    @BindView(R.id.trade_report_top_bar)
    TitleBar mTitleBar;
    @BindView(R.id.textView_dep)
    TextView mDep;
    @BindView(R.id.textView_date_range)
    TextView mDateRange;
    @BindView(R.id.cell_sale_total)
    TextView mSaleTotal;
    @BindView(R.id.cell_sale_count)
    TextView mSaleCount;
    @BindView(R.id.cell_rtn_total)
    TextView mRtnTotal;
    @BindView(R.id.cell_rtn_count)
    TextView mRtnCount;
    @BindView(R.id.cell_sum_total)
    TextView mSumTotal;
    @BindView(R.id.cell_sum_count)
    TextView mSumCount;
    @BindView(R.id.grid_report)
    GridLayout mGridReport;
    @BindColor(R.color.text_black_first)
    int textBlack;
    @BindDrawable(R.drawable.view_white_bottom_left_line)
    Drawable bgLeft;
    @BindDrawable(R.drawable.view_white_bottom_mid_line)
    Drawable bgMid;
    @BindDrawable(R.drawable.view_white_bottom_right_line)
    Drawable bgRight;

    /**
     * 起始日期
     */
    private Date begin;
    /**
     * 结束日期
     */
    private Date end;

    private List<RestBodyMap> dataList = null;
    private List<ReportData> payList = null;

    @Override
    public void onNetWorkChange(boolean isOnline) {

    }

    @Override
    protected int getLayoutID() {
        return R.layout.trade_report_activity;
    }

    @Override
    protected void initData() {
        Date today = new Date();
        query(today, today);
    }

    @Override
    protected void initView() {
        mDep.setText("专柜：" + ZgParams.getCurrentDep().getDepName());
    }

    @Override
    protected void initTitleBar() {
        mTitleBar.setOnTitleBarListener(this);
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.common_white).autoDarkModeEnable(true).init();
        mTitleBar.setRightIcon(ZgParams.isIsOnline() ? R.drawable.online : R.drawable.offline);
    }

    @OnClick(R.id.rl_date_range)
    public void selectDateRange() {
        InputPanel.showMultiCalendarSelector(this, begin, end, new DateRangeInputCallback() {
            @Override
            public void onOk(Date begin, Date end) {
                //刷新数据
                query(begin, end);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    /**
     * 查询报表
     *
     * @param begin 起始日期
     * @param end   结束日期
     */
    private void query(Date begin, Date end) {
        this.begin = begin;
        this.end = end;
        clearReport();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String beginDate = sdf.format(begin);
        String endDate = sdf.format(end);
        String msg = String.format(Locale.CHINA, "选择日期：%s ~ %s", beginDate, endDate);
        mDateRange.setText(msg);
        RestSubscribe.getInstance().queryTradeReport(ZgParams.getCurrentDep().getDepCode(),
                beginDate, endDate,
                new RestCallback(new RestResultHandler() {
                    @Override
                    public void onSuccess(RestBodyMap body) {
                        updateReport(body.getMapList("list"));
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMsg) {

                    }
                }));
    }

    /**
     * 清空报表数据
     */
    private void clearReport() {
        mSaleCount.setText("0");
        mSaleTotal.setText("0");
        mRtnCount.setText("0");
        mRtnTotal.setText("0");
        mSumCount.setText("0");
        mSumTotal.setText("0");
        clearPayRows();
    }

    /**
     * 刷新报表数据
     *
     * @param dataList
     */
    private void updateReport(List<RestBodyMap> dataList) {
        if (dataList == null) {
            return;
        }
        this.dataList = dataList;
        Integer sumCount = 0;
        Double sumTotal = 0.0;
        payList = new ArrayList<>();
        for (RestBodyMap data : dataList) {
            ReportData reportData = new ReportData(data);
            if (reportData.itemName.equals("T")) {
                mSaleCount.setText(reportData.tradeCount.toString());
                mSaleTotal.setText(String.format("%.2f", reportData.tradeTotal));
                sumCount += reportData.tradeCount;
                sumTotal += reportData.tradeTotal;
            } else if (reportData.itemName.equals("R")) {
                mRtnCount.setText(reportData.tradeCount.toString());
                mRtnTotal.setText(String.format("%.2f", reportData.tradeTotal));
                sumCount += reportData.tradeCount;
                sumTotal += reportData.tradeTotal;
            } else {
                payList.add(reportData);
            }
        }
        mSumCount.setText(sumCount.toString());
        mSumTotal.setText(String.format("%.2f", sumTotal));
        // 显示支付方式列表
        addPayRows(payList);
    }

    /**
     * 向表格添加支付方式统计数据
     *
     * @param payList
     */
    private void addPayRows(List<ReportData> payList) {
        int i = 0;
        for (ReportData reportData : payList) {
            if (i != payList.size() - 1) {
                addLeftCell(reportData.itemName);
                addMidCell(String.format("%.2f", reportData.tradeTotal));
                addRightCell(String.format("%d", reportData.tradeCount));
            } else {
                addLeftCell(reportData.itemName);
                addMidCell(String.format("%.2f", reportData.tradeTotal));
                addRightCell(String.format("%d", reportData.tradeCount));
            }
            i++;
        }
    }


    private void addLeftCell(String text) {
         /*单元格样式
            android:layout_gravity="fill"
            android:gravity="center"
            android:textSize="@dimen/sp_14"
             */
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setBackground(bgLeft);
        //添加
        addGridCell(textView);
    }

    private void addMidCell(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setBackground(bgMid);
        //添加
        addGridCell(textView);
    }

    private void addRightCell(String text) {
        TextView textView = new TextView(this);
        textView.setBackground(bgRight);
        textView.setText(text);
        //添加
        addGridCell(textView);
    }


    /**
     * 向表格中添加一个单元格
     */
    private void addGridCell(TextView textView) {
        int padding = ConvertUtils.dp2px(8);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(textBlack);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setPadding(padding, padding, padding, padding);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.setGravity(Gravity.FILL);
        mGridReport.addView(textView, layoutParams);
    }

    /**
     * 清除表格内的支付方式统计数据
     */
    private void clearPayRows() {
        //保留前面4行3列
        mGridReport.removeViewsInLayout(12, mGridReport.getChildCount() - 12);
    }

    @Override
    public void onLeftClick(View v) {
        finish();
    }

    @Override
    public void onTitleClick(View v) {
    }

    @Override
    public void onRightClick(View v) {
    }

    @OnClick(R.id.trade_report_btn_print_again)
    public void print() {
        PrinterHelper.initPrinter(TradeProdActivity.mContext, new PrinterHelper.PrintInitCallBack() {
            @Override
            public void onSuccess(SunmiPrinterService service) {
                getPrintData(service);
            }

            @Override
            public void onFailed() {
                MessageUtil.showError("打印机出现故障，请检查");
            }
        });
    }


    public void getPrintData(SunmiPrinterService service) {
        if (service == null) {
            return;
        }
        //生成数据，执行打印命令
        if (!dataList.isEmpty()) {
            PrinterHelper.print(PrintFormat.printTradeReport(begin, end, dataList, payList));
        } else {
            RestBodyMap bodyMap = new RestBodyMap();
            bodyMap.put("itemName", "T");
            bodyMap.put("tradeCount", 0);
            bodyMap.put("tradeTotal", 0.00);
            dataList.add(bodyMap);
            bodyMap = new RestBodyMap();
            bodyMap.put("itemName", "R");
            bodyMap.put("tradeCount", 0);
            bodyMap.put("tradeTotal", 0.00);
            dataList.add(bodyMap);
            PrinterHelper.print(PrintFormat.printTradeReport(begin, end, dataList, null));
        }
    }

    @OnClick(R.id.trade_report_btn_back)
    public void back() {
        finish();
    }

    /**
     * 报表数据
     */
    public static class ReportData {
        public String itemName;
        public Integer tradeCount;
        public Double tradeTotal;
        public String itemCode;

        public ReportData(RestBodyMap map) {
            if (map.containsKey("itemName")) {
                itemName = map.getString("itemName");
            }
            if (map.containsKey("tradeCount")) {
                tradeCount = (int) Math.round(map.getDouble("tradeCount"));
            }
            if (map.containsKey("tradeTotal")) {
                tradeTotal = map.getDouble("tradeTotal");
            }
            if (map.containsKey("itemCode")) {
                tradeTotal = map.getDouble("itemCode");
            }
        }
    }

}
