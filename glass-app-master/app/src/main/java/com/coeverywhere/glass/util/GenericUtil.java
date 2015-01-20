/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ryaneldridge on 5/1/14.
 */
public class GenericUtil {

    public static String calculateDateFromOffset(int offset) {
        int timeZoneOffset = offset * 1000;
        TimeZone tz = TimeZone.getTimeZone("GMT");
        Calendar calendar = Calendar.getInstance(tz);

        SimpleDateFormat formatter = localTime.get();
        formatter.setTimeZone(tz);
        long time = calendar.getTimeInMillis() + timeZoneOffset;
        return formatter.format(new Date(time));
    }

    static ThreadLocal<SimpleDateFormat> localTime = new ThreadLocal<SimpleDateFormat>() {
        @Override
        public SimpleDateFormat get() {
            return new SimpleDateFormat("h:mm a");
        }
    };

}
