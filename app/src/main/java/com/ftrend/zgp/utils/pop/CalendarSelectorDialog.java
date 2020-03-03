package com.ftrend.zgp.utils.pop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.ftrend.zgp.R;
import com.lxj.xpopup.core.BottomPopupView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 日期选择对话框
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin
 * @since 2019/11/13
 */
public class CalendarSelectorDialog extends BottomPopupView implements OnRangeSelectedListener, OnDateSelectedListener {

    @BindView(R.id.calendarView_period)
    MaterialCalendarView mCalendarView;
    @BindView(R.id.textView_selection)
    TextView mSelectionLabel;
    @BindView(R.id.textView_current_date)
    TextView mCurrentDateLabel;

    private CalendarDay firstDay = null;
    private CalendarDay lastDay = null;
    private boolean isRangeSelector = false;
    private DateRangeInputCallback callback = null;

    /**
     * 日期选择
     *
     * @param context
     * @param initDate 默认选中日期，可为null
     * @return
     */
    public static CalendarSelectorDialog singleSelector(@NonNull Context context,
                                                        @Nullable Date initDate,
                                                        @NonNull DateRangeInputCallback callback) {
        CalendarSelectorDialog dialog = new CalendarSelectorDialog(context);
        dialog.isRangeSelector = false;
        if (initDate != null) {
            dialog.firstDay = CalendarDay.from(initDate.getTime());
        }
        dialog.callback = callback;
        return dialog;
    }

    /**
     * 日期范围选择
     *
     * @param context
     * @param from    默认选中开始日期，可为null
     * @param to      默认选中结束日期，可为null
     * @return
     */
    public static CalendarSelectorDialog multiSelector(@NonNull Context context,
                                                       @Nullable Date from, @Nullable Date to,
                                                       @NonNull DateRangeInputCallback callback) {
        CalendarSelectorDialog dialog = new CalendarSelectorDialog(context);
        dialog.isRangeSelector = true;
        if (from != null && to != null) {
            dialog.firstDay = CalendarDay.from(from.getTime());
            dialog.lastDay = CalendarDay.from(to.getTime());
        }
        dialog.callback = callback;
        return dialog;
    }

    private CalendarSelectorDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.calendar_selector_dialog;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);

        mCalendarView.setOnDateChangedListener(this);
        mCalendarView.setOnRangeSelectedListener(this);
        mCalendarView.setSelectionMode(isRangeSelector
                ? MaterialCalendarView.SELECTION_MODE_RANGE
                : MaterialCalendarView.SELECTION_MODE_SINGLE);
        if (!isRangeSelector && firstDay != null) {
            mCalendarView.setDateSelected(firstDay, true);
            mCalendarView.setCurrentDate(firstDay);
        } else if (isRangeSelector && firstDay != null && lastDay != null) {
            mCalendarView.selectRange(firstDay, lastDay);
            mCalendarView.setCurrentDate(firstDay);
        }
        mCurrentDateLabel.setText("当前日期：" + calendarDay2String(CalendarDay.today()));
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        if (selected) {
            updateSelection(date, date);
        } else {
            updateSelection(null, null);
        }
    }

    @Override
    public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
        updateSelection(dates.get(0), dates.get(dates.size() - 1));
    }

    /**
     * 更新当前选中的日期范围
     *
     * @param from
     * @param to
     */
    private void updateSelection(CalendarDay from, CalendarDay to) {
        firstDay = from;
        lastDay = to;
        if (firstDay == null || lastDay == null) {
            mSelectionLabel.setText("");
        } else {
            String selection = String.format(Locale.CHINA, "开始日期：%s    结束日期：%s",
                    calendarDay2String(firstDay), calendarDay2String(lastDay));
            mSelectionLabel.setText(selection);
        }
    }

    /**
     * 日期格式化
     *
     * @param date
     * @return
     */
    private String calendarDay2String(CalendarDay date) {
        return String.format(Locale.CHINA, "%04d-%02d-%02d", date.getYear(), date.getMonth() + 1, date.getDay());
    }

    @OnClick(R.id.textView_current_date)
    public void gotoToday() {
        mCalendarView.setCurrentDate(CalendarDay.today());
    }

    @OnClick(R.id.calendar_btn_ok)
    public void submit() {
        if (firstDay == null || lastDay == null) {
            return;
        }

        if (callback != null) {
            callback.onOk(firstDay.getDate(), lastDay.getDate());
        }
        dismiss();
    }

    @OnClick(R.id.calendar_btn_cancel)
    public void cancel() {
        if (callback != null) {
            callback.onCancel();
        }
        dismiss();
    }

    @OnClick(R.id.calendar_close)
    public void close() {
        cancel();
    }

}
