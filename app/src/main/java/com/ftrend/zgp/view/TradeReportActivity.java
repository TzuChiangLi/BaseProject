package com.ftrend.zgp.view;

import android.util.TypedValue;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

import com.ftrend.zgp.R;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.msg.InputPanel;
import com.ftrend.zgp.utils.pop.DateRangeInputCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class TradeReportActivity extends BaseActivity {
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

    /**
     * 起始日期
     */
    private Date begin;
    /**
     * 结束日期
     */
    private Date end;

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

    }

    @OnClick(R.id.textView_date_range)
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
        String msg = String.format(Locale.CHINA, "日期：%s ~ %s", beginDate, endDate);
        mDateRange.setText(msg);
        RestSubscribe.getInstance().queryTradeReport(ZgParams.getCurrentDep().getDepCode(),
                beginDate, endDate,
                new RestCallback(new RestResultHandler() {
                    @Override
                    public void onSuccess(Map<String, Object> body) {
                        updateReport((List<Map<String, Object>>) body.get("list"));
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
    private void updateReport(List<Map<String, Object>> dataList) {
        Integer sumCount = 0;
        Double sumTotal = 0.0;
        List<ReportData> payList = new ArrayList<>();
        for (Map<String, Object> data : dataList) {
            ReportData reportData = new ReportData(data);
            if (reportData.itemName.equals("T")) {
                mSaleCount.setText(reportData.tradeCount.toString());
                mSaleTotal.setText(reportData.tradeTotal.toString());
                sumCount += reportData.tradeCount;
                sumTotal += reportData.tradeTotal;
            } else if (reportData.itemName.equals("R")) {
                mRtnCount.setText(reportData.tradeCount.toString());
                mRtnTotal.setText(reportData.tradeTotal.toString());
                sumCount += reportData.tradeCount;
                sumTotal += reportData.tradeTotal;
            } else {
                payList.add(reportData);
            }
        }
        mSumCount.setText(sumCount.toString());
        mSumTotal.setText(sumTotal.toString());
        // 显示支付方式列表
        addPayRows(payList);
    }

    /**
     * 向表格添加支付方式统计数据
     *
     * @param payList
     */
    private void addPayRows(List<ReportData> payList) {
        for (ReportData reportData : payList) {
            addGridCell(reportData.itemName);
            addGridCell(reportData.tradeTotal.toString());
            addGridCell(reportData.tradeCount.toString());
        }
    }

    /**
     * 向表格中添加一个单元格
     *
     * @param text 单元格内容
     */
    private void addGridCell(String text) {
        TextView textView = new TextView(this);
        /*单元格样式
        android:layout_gravity="fill"
        android:gravity="center"
        android:textSize="@dimen/sp_14"
         */
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setText(text);
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

    /**
     * 报表数据
     */
    class ReportData {
        private String itemName;
        private Integer tradeCount;
        private Double tradeTotal;

        ReportData(Map<String, Object> map) {
            if (map.containsKey("itemName")) {
                itemName = map.get("itemName").toString();
            }
            if (map.containsKey("tradeCount")) {
                tradeCount = Math.round(Float.parseFloat(map.get("tradeCount").toString()));
            }
            if (map.containsKey("tradeTotal")) {
                tradeTotal = Double.parseDouble(map.get("tradeTotal").toString());
            }
        }
    }

}
