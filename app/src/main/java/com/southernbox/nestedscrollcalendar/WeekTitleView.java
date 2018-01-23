package com.southernbox.nestedscrollcalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import static java.util.Calendar.DATE;

/**
 * Created by nanquan on 2018/1/23.
 */

public class WeekTitleView extends ViewGroup {

    protected static final int DEFAULT_DAYS_IN_WEEK = 7;

    private Context mContext;

    private int textResourceId;

    private Calendar calendar;

    public WeekTitleView(Context context) {
        this(context, null);
    }

    public WeekTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        this.mContext = context;

        TypedArray a = mContext.getTheme()
                .obtainStyledAttributes(attrs, com.prolificinteractive.materialcalendarview.R.styleable.MaterialCalendarView, 0, 0);
        textResourceId = a.getResourceId(
                com.prolificinteractive.materialcalendarview.R.styleable.MaterialCalendarView_mcv_weekDayTextAppearance,
                com.prolificinteractive.materialcalendarview.R.style.TextAppearance_MaterialCalendarWidget_WeekDay);

        calendar = Calendar.getInstance();
        addView();
    }

    private void addView() {
        for (int i = 1; i <= DEFAULT_DAYS_IN_WEEK; i++) {
            TextView weekTextView = new TextView(getContext());
            calendar.set(Calendar.DAY_OF_WEEK, i);
            weekTextView.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));

            weekTextView.setTextAppearance(mContext, textResourceId);
            weekTextView.setGravity(Gravity.CENTER);

            addView(weekTextView);
            calendar.add(DATE, 1);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        //We expect to be somewhere inside a MaterialCalendarView, which should measure EXACTLY
        if (specHeightMode == MeasureSpec.UNSPECIFIED || specWidthMode == MeasureSpec.UNSPECIFIED) {
            throw new IllegalStateException("CalendarPagerView should never be left to decide it's size");
        }

        //The spec width should be a correct multiple
        final int measureTileWidth = specWidthSize / DEFAULT_DAYS_IN_WEEK;
        final int measureTileHeight = specHeightSize;

        //Just use the spec sizes
        setMeasuredDimension(specWidthSize, specHeightSize);

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    measureTileWidth,
                    MeasureSpec.EXACTLY
            );

            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    measureTileHeight,
                    MeasureSpec.EXACTLY
            );

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();

        final int parentLeft = 0;

        int childTop = 0;
        int childLeft = parentLeft;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            child.layout(childLeft, childTop, childLeft + width, childTop + height);

            childLeft += width;

            //We should warp every so many children
            if (i % DEFAULT_DAYS_IN_WEEK == (DEFAULT_DAYS_IN_WEEK - 1)) {
                childLeft = parentLeft;
                childTop += height;
            }

        }
    }
}
