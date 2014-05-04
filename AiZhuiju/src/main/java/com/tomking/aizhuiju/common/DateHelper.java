package com.tomking.aizhuiju.common;

import com.tomking.aizhuiju.R;
import com.tomking.aizhuiju.test.MyLog;
import com.tvrage.models.ShowStatus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper  {

    public static final int[] DAYS = {
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
            Calendar.SUNDAY
    };

    public static final String[] DAY_NAME = {
            "星期天",
            "星期一",
            "星期二",
            "星期三",
            "星期四",
            "星期五",
            "星期六",
    };

    public static final String THIS_WEEK = "本周";
    public static final String LAST_WEEK = "上周";
    public static final String LONG_BEFORE = "更早前";

    public static java.sql.Date toSqlDate(Calendar c) {

        return new java.sql.Date(c.getTimeInMillis());

    }



    public static Calendar getFirstDayOfWeek() {

        Calendar monday = Calendar.getInstance();

        return getADayOfWeek(monday, Calendar.MONDAY);

    }



    public static Calendar getFirstDayOfWeek(Calendar day) {

        Calendar monday = (Calendar) day.clone();

        return getADayOfWeek(monday, Calendar.MONDAY);

    }



    public static Calendar getLastDayOfWeek() {

        Calendar sunday = Calendar.getInstance();

        return getADayOfWeek(sunday, Calendar.SUNDAY);

    }



    public static Calendar getLastDayOfWeek(Calendar day) {

        Calendar sunday = (Calendar) day.clone();

        return getADayOfWeek(sunday, Calendar.SUNDAY);

    }

    public static Calendar getNextMonday() {
        Calendar thisMonday = getFirstDayOfWeek();
        thisMonday.add(Calendar.DATE, 7);
        return thisMonday;
    }

    public static Calendar getThisMonday() {
        return getFirstDayOfWeek();
    }

    public static Calendar getLastMonday() {
        Calendar thisMonday = getThisMonday();
        thisMonday.add(Calendar.DATE, -7);
        return thisMonday;
    }

    private static Calendar getADayOfWeek(Calendar day, int dayOfWeek) {
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);
        int week = day.get(Calendar.DAY_OF_WEEK);

        if (week == dayOfWeek)

            return day;

        int diffDay = dayOfWeek - week;

        if (week == Calendar.SUNDAY) {

            diffDay -= 7;

        } else if (dayOfWeek == Calendar.SUNDAY) {

            diffDay += 7;

        }

        day.add(Calendar.DATE, diffDay);

        return day;

    }

    public static Calendar getCurrentTime() {
        return Calendar.getInstance();
    }

    public static long getCurrentMilliseconds() {
        return getCurrentTime().getTimeInMillis();
    }

    public static boolean isWithinThisWeek(Date day) {
        Date thisMonday = DateHelper.getThisMonday().getTime();
        Date nextMonday = DateHelper.getNextMonday().getTime();
        MyLog.d(thisMonday + " ");

        return day.compareTo(thisMonday) >= 0 && day.compareTo(nextMonday) <= 0;
    }

    public static boolean isWithinLastWeek(Date day) {
        Date thisMonday = DateHelper.getThisMonday().getTime();
        Date lastMonday = DateHelper.getLastMonday().getTime();

        return day.compareTo(lastMonday) >= 0 && day.compareTo(thisMonday) <= 0;
    }

    public static boolean isLongBefore(Date day) {
        Date lastMonday = DateHelper.getLastMonday().getTime();
        return day.compareTo(lastMonday) <= 0;
    }


    public static Date parseEpisodeDate(String pubdate) {
        SimpleDateFormat sdf = new SimpleDateFormat(
                ShowStatus.DATE_FORMAT, Locale.US);
        try{

            return sdf.parse(pubdate);
        }catch(Exception e) {
            MyLog.d("exception " + e.toString() + " " + ShowStatus.DATE_FORMAT);
            return null;
        }
    }

    public static boolean isLargerThanThisWeek(Date day) {
        Date nextMonday = DateHelper.getNextMonday().getTime();

        return day.compareTo(nextMonday) >= 0;
    }

    public static boolean isLargerThanToday(Date day) {
        Date today = DateHelper.getCurrentTime().getTime();

        return day.compareTo(today) >= 0;
    }

    public static int getDaysFromNow(Date returnDate) {

        double todayMilliseconds = Calendar.getInstance().getTimeInMillis();
        double returnDayMilliseconds = returnDate.getTime();

        return (int)Math.ceil((returnDayMilliseconds - todayMilliseconds) / (24 * 60 * 60 * 1000));
    }

}

