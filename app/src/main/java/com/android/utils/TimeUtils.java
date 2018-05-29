package com.android.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by SinghParamveer on 1/19/2018.
 * Utility class for handle time related functions such as comparison,conversion,formatting etc
 */

public final class TimeUtils {
    private static final long MILLIS_IN_MINUTE = 60 * 1000;
    private static final long MILLIS_IN_HOUR = 60 * MILLIS_IN_MINUTE;
    private static final long MILLIS_IN_DAY = 24 * MILLIS_IN_HOUR;

    /**
     * @param startDateString date from where comparison to be made
     * @param endDateString   date up to which comparison is required
     * @param dateFormat      the date format to be used for start and end dates
     * @return 0 if equal, 1 if start date is after end date, -1 if end date is after start date
     */
    public int compareDate(String startDateString, String endDateString, String dateFormat) {

        SimpleDateFormat inputDateFromat = new SimpleDateFormat(dateFormat, Locale.getDefault());

        Date startDate = new Date();
        Date endDate = new Date();
        try {
            startDate = inputDateFromat.parse(startDateString);
            endDate = inputDateFromat.parse(endDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return startDate.compareTo(endDate);
    }

    /**
     * @param sourceDate       the date which needs to be converted to specific format
     * @param targetDateFormat the format to which the current date is to be converted.
     *                         eg. dd/MM/yyyy hh:mm a for 01/12/2018 10:10 pm
     * @return return the string representation of formatted date
     */
    public String changeDateFormatFromDate(Date sourceDate, String targetDateFormat) {
        if (sourceDate == null || targetDateFormat == null || targetDateFormat.isEmpty()) {
            return "";
        }
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(targetDateFormat, Locale.getDefault());
        return outputDateFormat.format(sourceDate);
    }

    public String changeDateFormat(String dateString, String sourceDateFormat, String targetDateFormat) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }
        SimpleDateFormat inputDateFromat = new SimpleDateFormat(sourceDateFormat, Locale.getDefault());
        Date date = new Date();
        try {
            date = inputDateFromat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(targetDateFormat, Locale.getDefault());
        return outputDateFormat.format(date);
    }

    public String msToString(long ms) {
        return msToString(ms, false);
    }

    public String msToString(long ms, boolean shortString) {
        long remainder = ms;

        String daysString = "";
        if (remainder >= MILLIS_IN_DAY) {
            long days = remainder / MILLIS_IN_DAY;
            remainder = remainder % MILLIS_IN_DAY;
            if (days > 0) {
                daysString = days + (days > 1 ? " days " : " day ");
            }
        }
        String hoursString = "";
        if (remainder >= MILLIS_IN_HOUR) {
            long hours = (remainder / MILLIS_IN_HOUR);
            remainder = remainder % MILLIS_IN_HOUR;
            if (hours > 0)
                hoursString = hours + (hours > 1 ? " hours " : " hour ");
        }
        String minsString = "";
        if (remainder >= MILLIS_IN_MINUTE) {
            long mins = (remainder / MILLIS_IN_MINUTE);
            remainder = remainder % MILLIS_IN_MINUTE;
            if (mins > 0)
                minsString = mins + (mins > 1 ? (shortString ? "mins" : " minutes ")
                        : (shortString ? "min" : " minute "));
        }

        String secsString = "";
        if (remainder >= 1000) {
            //Comment to set custom value
//            long secs = (remainder / 1000);
//            if (secs > 0)
//                secsString = secs + (secs > 1 ? " seconds " : " second ");
            secsString = "1 minute";
        } else if (daysString.isEmpty() && hoursString.isEmpty() && minsString.isEmpty()) {
            secsString = "0 minute";
        }
        return daysString + hoursString + minsString + secsString;
    }


}
