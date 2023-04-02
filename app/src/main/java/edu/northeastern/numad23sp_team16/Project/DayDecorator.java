package edu.northeastern.numad23sp_team16.Project;

import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import edu.northeastern.numad23sp_team16.R;

public class DayDecorator extends AppCompatActivity implements DayViewDecorator {

    private int color = R.color.pastel_pink;
    private final CalendarDay dates;

    public DayDecorator(CalendarDay dates) {
        this.dates = dates;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates==day;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5, color));
    }
}
