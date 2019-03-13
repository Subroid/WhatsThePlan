package platinum.whatstheplan.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import platinum.whatstheplan.R;

public class DatesDecoration implements DayViewDecorator {

    private static final String TAG = "BookingDateDotTag";
    private CalendarDay calendarDay;
    private Context context;
    private int decorationType;
    private boolean dotDecoration;
    private boolean circleDecoration;
    public static final int DOT_DECORATION = 11;
    public static final int CIRCLE_DECORATION = 22;
    private Drawable drawable;

    public DatesDecoration() {
    }

    public DatesDecoration(CalendarDay calendarDay, Activity activity, int decorationType) {
        drawable = ContextCompat.getDrawable(activity, R.drawable.blue_dot_128);
        this.calendarDay = calendarDay;
        context = activity;
        this.decorationType = decorationType;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(calendarDay);
    }

    @Override
    public void decorate(DayViewFacade view) {
        switch (decorationType) {
            case DOT_DECORATION :
                view.addSpan(new DotSpan(8, context.getColor(R.color.TealGreen)));
                break;
            case CIRCLE_DECORATION :
                view.setSelectionDrawable(drawable);
                break;
        }

    }
}
