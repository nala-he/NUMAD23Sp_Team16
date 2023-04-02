package edu.northeastern.numad23sp_team16.Project;

import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

public class DayDecorator extends AppCompatActivity implements DayViewDecorator {

    // Make color of dot pastel pink
    private String color = "#F7D1D1";
    private final CalendarDay date;

    public DayDecorator(CalendarDay date) {
        this.date = date;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // Only decorate date if matches
        return day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // Add dot underneath day
        view.addSpan(new DotSpan(10, Color.parseColor(color)));
    }
}
