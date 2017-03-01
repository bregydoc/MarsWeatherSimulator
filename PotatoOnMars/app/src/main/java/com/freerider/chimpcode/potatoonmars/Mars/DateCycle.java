package com.freerider.chimpcode.potatoonmars.Mars;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.freerider.chimpcode.potatoonmars.R;


import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by root on 2/18/17.
 */

public class DateCycle {


    private Date initDate;
    private Calendar calendar;

    public DateCycle() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT-5:00"));
        this.calendar = c;
        this.initDate = c.getTime();

    }

    public Calendar getCurrentCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT-5:00"));
    }

    public String getHourInString() {
        Calendar c = getCurrentCalendar();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public void putHourInTextView(TextView textView) {
        textView.setText(getHourInString());
    }

    public String getIfIsNightOrDay() {
        Calendar c = getCurrentCalendar();
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        if (currentHour > 5 && currentHour<18) {
            return "day";
        }else{
            return "night";
        }
    }

    public void linkWithImageView(ImageView image) {
        Drawable day = image.getContext().getDrawable(R.drawable.ic_park);
        Drawable night = image.getContext().getDrawable(R.drawable.ic_sky);

        if (getIfIsNightOrDay().equals("day")) {
            image.setImageDrawable(day);
        } else {
            image.setImageDrawable(night);
        }
    }



}
