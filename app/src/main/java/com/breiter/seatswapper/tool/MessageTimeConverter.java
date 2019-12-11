package com.breiter.seatswapper.tool;

import com.breiter.seatswapper.model.Message;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MessageTimeConverter {

    public static String getTime(Long time) {

        Timestamp timestamp = new Timestamp(time);

        Date date = new Date(timestamp.getTime());

        String pattern;

        if (date.before(periodInDays(366)))
            pattern = "dd/MM/yyyy, HH:mm";

        else if (date.before(periodInDays(7)))
            pattern = "d MMM, HH:mm";

        else if (date.before(periodInDays(1)))
            pattern = "EEE HH:mm";

        else
            pattern = "HH:mm";


        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        return sdf.format(date);


    }


    public static String getRequestTime(Message message) {

        Long time = message.getTimeRequest();

        return getTime(time);

    }


    public static String getResponseTime(Message message) {

        Long time = message.getTimeResponse();

        if (time == 0)
            return "awaiting";
        else
            return getTime(time);

    }


    private static Date periodInDays(int daysAmount) {

        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DATE, -daysAmount);

        return cal.getTime();
    }


}

