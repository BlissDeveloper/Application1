package com.example.application1;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public long getCurrentTimestamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        return tsLong;
    }

    public String getCurrentDate() {
        Date date = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(date);
        return formattedDate;
    }

    public String getCurrentTime() {
        Date date = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss aa");
        String formattedDate = df.format(date);

        return formattedDate;
    }
}
