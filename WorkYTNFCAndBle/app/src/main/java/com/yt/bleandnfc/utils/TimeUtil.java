package com.yt.bleandnfc.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具
 */
public class TimeUtil {
    public static final String FORMAT_A = "yyyy-MM-dd";
    public static final String FORMAT_B = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_C = "MM-dd";
    public static final String FORMAT_D = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_E = "MM月dd日";
    public static final String FORMAT_F = "yyyy年MM月dd日";

    /**
     * 返回年月日 时分
     * @return
     */
    public static String getTodayTime(){
        long time = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_B);
        return formatter.format(time);
    }

    /**
     * 返回年月日数据
     * @return
     */
    public static String getTodayTimeYMD(){
        long time = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_A);
        return formatter.format(time);
    }

    /**
     * 将字符串按照时间格式转换为Date
     *
     * @param strDate
     * @param format
     * @return
     */
    public static Date strToDate(String strDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将字符串按照时间格式转换为指定时间格式的字符串
     *
     * @param date
     * @param startFormat
     * @param endFormat
     * @return
     */
    public static String stringDateToString(String date, String startFormat, String endFormat) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(startFormat);
            ParsePosition pos = new ParsePosition(0);
            Date strtodate = formatter.parse(date, pos);

            SimpleDateFormat formatter1 = new SimpleDateFormat(endFormat);
            String str = formatter1.format(strtodate);
            return str;
        }catch (Exception e){
            if(date==null){
                return "";
            }else {
                return date;
            }
        }
    }

    /**
     * 获取目标月的天数
     */
    @SuppressLint("WrongConstant")
    public static String getDestinationMonth(int year, int month){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONDAY,month);

        calendar.add(Calendar.DAY_OF_MONTH,-1);
        SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_A);

        calendar.set(Calendar.DAY_OF_MONTH,1);
        Date firstDate = calendar.getTime();
        String firstStr = formatter.format(firstDate);

        calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastData = calendar.getTime();
        String lastStr = formatter.format(lastData);

        return firstStr + "---" + lastStr;
    }

    /**
     * 将毫秒数按照时间格式转换为字符串
     *
     * @param milliseconds
     * @param format
     * @return
     */
    public static String dateToStr(long milliseconds, String format) {
        if(milliseconds==0){
            return "";
        }
        Date date = new Date(milliseconds);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String str = formatter.format(date);
        return str;
    }

    public static String updateTime(long time){
        String data = "";
        long l = System.currentTimeMillis() - time;
        if (l > 0 && l < (1000 * 60L)) {
            data = (l / (1000L)) + "秒钟前更新";
        } else if (l > (1000 * 60L) && l < (1000 * 60 * 60L)) {
            data = (l / (1000 * 60L)) + "分钟前更新";
        } else if (l >= (1000 * 60 * 60L) && l < (1000 * 60 * 60 * 24L)) {
            data = (l / (1000 * 60 * 60L)) + "小时前更新";
        } else if (l >= (1000 * 60 * 60 * 24L) && l < (1000 * 60 * 60 * 24 * 30L)) {
            data = (l / (1000 * 60 * 60 * 24L)) + "天前更新";
        } else if (l >= (1000 * 60 * 60 * 24 * 30L) && l < (1000L * 60L * 60L * 24L * 30L * 3L)) {
            data = (l / (1000 * 60 * 60 * 24 * 30L)) + "个月前更新";
        } else {
            data = time + "";
        }
        return data;
    }

    public static String updateTimeSmart(long time){
        String data = "";
        long l = System.currentTimeMillis() - time;
        if (l > 0 && l < (1000 * 60L)) {
            data = "刚刚更新";
        } else if (l > (1000 * 60L) && l < (1000 * 60 * 60L)) {
            data = (l / (1000 * 60L)) + "分钟前更新";
        } else if (l >= (1000 * 60 * 60L) && l < (1000 * 60 * 60 * 24L)) {
            data = (l / (1000 * 60 * 60L)) + "小时前更新";
        } else if (l >= (1000 * 60 * 60 * 24L) && l < (1000 * 60 * 60 * 24 * 30L)) {
            data = (l / (1000 * 60 * 60 * 24L)) + "天前更新";
        } else if (l >= (1000 * 60 * 60 * 24 * 30L) && l < (1000L * 60L * 60L * 24L * 30L * 3L)) {
            data = (l / (1000 * 60 * 60 * 24 * 30L)) + "个月前更新";
        } else {
            data = time + "";
        }
        return data;
    }

    /**
     * 马小秘时间格式
     * @param timeStr
     * @return
     */
    public static String updateTimeHorseMi(String timeStr){
        Date date = strToDate(timeStr,"yyyy-MM-dd HH:mm:ss");
        if(date==null){
            return "";
        }
        long time=date.getTime();
        String data = "";
        long currentTime = System.currentTimeMillis() - time;
        if (currentTime < (1000 * 60L)) {
            data = "刚刚";
        } else if (currentTime > (1000 * 60L) && currentTime < (1000 * 60 * 60L)) {
            data = (currentTime / (1000 * 60L)) + "分钟前";
        } else {
            if(isStrTimeToday(timeStr,"yyyy-MM-dd HH:mm:ss")){
                data = "今天 "+dateToStr(time,"HH:mm");
            }else if(isYesterday(timeStr,"yyyy-MM-dd HH:mm:ss")){
                data = "昨天 "+dateToStr(time,"HH:mm");
            }else {
                data = dateToStr(time,"MM-dd HH:mm");
            }
        }
        return data;
    }
    /**
     * 将现在时间按照时间格式转换为字符串
     * @param format
     * @return
     */
    public static String dateToStr(String format) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String str = formatter.format(date);
        return str;
    }
    /**
     * 将现在时间按照时间格式转换为字符串
     * @param time
     * @param format
     * @return
     */
    public static boolean isStrTimeToday(String time,String format) {
        String times=stringDateToString(time,format,"yyyy-MM-dd");
        String todayTime=dateToStr("yyyy-MM-dd");
        if(todayTime.equals(times)){
            return true;
        }
        return false;
    }
    /**
     * 判断是否为昨天(效率比较高)
     * @param day 传入的 时间
     * @param format 传入的 时间格式
     * @return true今天 false不是
     */
    public static boolean isYesterday(String day,String format){
        try {
            Calendar pre = Calendar.getInstance();
            Date predate = new Date(System.currentTimeMillis());
            pre.setTime(predate);

            Calendar cal = Calendar.getInstance();
            Date date = strToDate(day,format);
            cal.setTime(date);

            if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
                int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);

                if (diffDay == -1) {
                    return true;
                }
            }
            return false;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 判断是否为明天
     * @param day
     * @return
     */
    public static boolean isTomorrowday(String day,String format) {
        try {
            Calendar pre = Calendar.getInstance();
            Date predate = new Date(System.currentTimeMillis());
            pre.setTime(predate);

            Calendar cal = Calendar.getInstance();
            Date date = strToDate(day,format);
            cal.setTime(date);

            if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
                int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);
                if (diffDay == 1) {
                    return true;
                }
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }
    /**
     * 判断是否为前天(效率比较高)
     * @param day 传入的 时间
     * @param format 传入的 时间格式
     * @return true今天 false不是
     */
    public static boolean isBeforeYesterday(String day,String format){
        try {
            Calendar pre = Calendar.getInstance();
            Date predate = new Date(System.currentTimeMillis());
            pre.setTime(predate);

            Calendar cal = Calendar.getInstance();
            Date date = strToDate(day,format);
            cal.setTime(date);

            if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
                int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);

                if (diffDay == -2) {
                    return true;
                }
            }
            return false;
        }catch (Exception e){
            return false;
        }
    }
    /**
     * 获取两个日期的间隔天数
     * @param day1 开始 时间
     * @param day2 结束的 时间
     * @param format 传入的 时间格式
     * @return 间隔天数 如果是负数表示day2日期小于day1日期
     */
    public static int getBetweenIntervalDays(String day1,String day2,String format){
        try {
            Calendar cal1 = Calendar.getInstance();
            Date date1 = new Date();
            if(!TextUtils.isEmpty(day1)){
                date1 = strToDate(day1,format);
            }
            cal1.setTime(date1);

            Calendar cal2 = Calendar.getInstance();
            Date date2 = strToDate(day2,format);
            cal2.setTime(date2);

            if (cal1.get(Calendar.YEAR) == (cal2.get(Calendar.YEAR))) {
                int diffDay = cal2.get(Calendar.DAY_OF_YEAR) - cal1.get(Calendar.DAY_OF_YEAR);
                return diffDay;
            }else {
                int yearDays=cal1.getActualMaximum(Calendar.DAY_OF_YEAR);
                int yearDay1= cal1.get(Calendar.DAY_OF_YEAR);
                int yearDay2= cal2.get(Calendar.DAY_OF_YEAR);
                int diffDay = yearDays - yearDay1+yearDay2;
                return diffDay;
            }
        }catch (Exception e){
            return 0;
        }
    }
    /**
     * 根据时间格式format获取现在日期
     * @param format 时间格式
     * @return
     */
    public static Date getNowDateShort(String format) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(dateString, pos);
        return strtodate;
    }

    public static int getWeek(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(week_index<0){
            week_index = 0;
        }
        return week_index;
    }

    /**.
     * 获取星期
     * @param dateStr 字符串类型的时间
     * @param format 时间格式
     * @return 0到6 0表示星期天 6表示星期六
     */
    public static int getWeek(String dateStr,String format){
        Date date=strToDate(dateStr,format);
        return getWeek(date);
    }

    /**
     * 将秒数转为时分秒
     * @param second 秒数
     * @return
     */
    public static String change(long second) {
        long h = 0;
        long d = 0;
        long s = 0;
        long temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }
        String th = h < 10 ? "0" + h : h + "";
        String td = d < 10 ? "0" + d : d + "";
        String ts = s < 10 ? "0" + s : s + "";
        if (h == 0) {
            return td + ":" + ts + "";
        } else {
            return th + ":" + td + ":" + ts + "";
        }
    }

    /**
     * 时间戳转字符串
     *
     * @param timestamp     时间戳
     * @param isPreciseTime 是否包含时分
     * @return 格式化的日期字符串
     */
    public static String long2Str(long timestamp, boolean isPreciseTime) {
        return long2Str(timestamp, getFormatPattern(isPreciseTime));
    }
    /**
     * 时间戳转字符串
     *
     * @param timestamp     时间戳
     * @param isPreciseTime 是否包含时分
     * @return 格式化的日期字符串
     */
    public static String long2StrEnd(long timestamp, boolean isPreciseTime) {
        return long2Str(timestamp, FORMAT_F);
    }

    private static String long2Str(long timestamp, String pattern) {
        return new SimpleDateFormat(pattern, Locale.CHINA).format(new Date(timestamp));
    }

    public static int getYear(){
        Calendar cd = Calendar.getInstance();
        return  cd.get(Calendar.YEAR);
    }

    /**
     * 字符串转时间戳
     *
     * @param dateStr       日期字符串
     * @param isPreciseTime 是否包含时分
     * @return 时间戳
     */
    public static long str2Long(String dateStr, boolean isPreciseTime) {
        return str2Long(dateStr, getFormatPattern(isPreciseTime));
    }

    private static long str2Long(String dateStr, String pattern) {
        try {
            return new SimpleDateFormat(pattern, Locale.CHINA).parse(dateStr).getTime();
        } catch (Throwable ignored) {
        }
        return 0;
    }

    private static String getFormatPattern(boolean showSpecificTime) {
        if (showSpecificTime) {
            return FORMAT_B;
        } else {
            return FORMAT_A;
        }
    }
    // 获取最近12个月的月份
    public static int getLast12Months(int i) {
        Calendar c = Calendar.getInstance();
        int month=c.get(Calendar.MONTH)+i;
        return month;
    }
    // 获取最近12个月的时间
    public static String getLast12MonthsDate(int i,String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, i);
        Date m = c.getTime();
        return sdf.format(m);
    }
    /**
     * 获取那一天的日期
     * @param index 0表示今天 小于0表示今天前 大于0表示今天后
     * @param dateFormat
     * @return
     */
    public static String getLast12MonthsDateDay(int index,String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH,index);
        Date m = c.getTime();
        return sdf.format(m);
    }
    // 获取最近月的天数
    public static int getLastMonthsDay(int i) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, i);
        int day=c.getActualMaximum(Calendar.DATE);
        return day;
    }

    /**
     * 获取这个那一天是多少号
     * @param index 0表示今天 小于0表示今天前 大于0表示今天后
     * @return
     */
    public static int getTodayDay(int index) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH,0);
        return  c.get(Calendar.DAY_OF_MONTH);
    }

    //两个时间戳是否是同一天 时间戳是long型的（11或者13）
    public static boolean isSameData(String currentTime,String lastTime) {
        try {
            Calendar nowCal = Calendar.getInstance();
            Calendar dataCal = Calendar.getInstance();
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            Long nowLong = new Long(currentTime);
            Long dataLong = new Long(lastTime);
            String data1 = df1.format(nowLong);
            String data2 = df2.format(dataLong);
            Date now = df1.parse(data1);
            Date date = df2.parse(data2);
            nowCal.setTime(now);
            dataCal.setTime(date);
            return isSameDay(nowCal, dataCal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if(cal1 != null && cal2 != null) {
            return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                    && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        } else {
            return false;
        }
    }

    /**
     * 界面显示集结点的时间
     * @return
     */
    public static String getShowGatherTime(String gatherTime) {
        if(TextUtils.isEmpty(gatherTime)){
            return "01-01 10:00";
        }
        if (gatherTime.length() > 12) {
            return TimeUtil.stringDateToString(gatherTime, "yyyy-MM-dd HH:mm", "MM-dd HH:mm");
        } else {
            return gatherTime;
        }
    }

    /**
     * 界面显示签到时间 已签到时间显示
     * @return
     */
    public static String getSignInTime(long gatherTime) {
        if(gatherTime <= 0){
            return "10:00";
        }
        return TimeUtil.long2Str(gatherTime, "HH:mm");
    }

    /***
     * 获取当前日期距离过期时间的日期差值
     * @param endTime
     * @return
     */
    public static String dateDiff(long startTime,long endTime) {
        String strTime = null;
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        long day = 0;
        Date startTimeDate = new Date(startTime);//获取当前时间
        String startTimestr = sd.format(startTimeDate);
        Date endTimeDate = new Date(endTime);//获取当前时间
        String endTimestr = sd.format(endTimeDate);
        try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(endTimestr).getTime() - sd.parse(startTimestr).getTime();
            day = diff / nd;// 计算差多少天
            long hour = diff % nd / nh;// 计算差多少小时
            long min = diff % nd % nh / nm;// 计算差多少分钟
            long sec = diff % nd % nh % nm / ns;// 计算差多少秒
            // 输出结果
            if (day >= 1) {
                strTime = day + "天" + hour + "时";
            } else {
                if (hour >= 1) {
                    strTime = /*day + "天" +*/ hour + "时" + min + "分";
                } else {
                    if (sec >= 1) {
                        strTime = /*day + "天"*/ + hour + "时" + min + "分" /*+ sec + "秒"*/;
                    } else {
                        strTime = "1分";
                    }
                }
            }
            return strTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 界面显示签到时间 迟到多少分钟
     * @return
     */
    public static String getSignInLateTime(long time) {
        int timeSize = 0;
        if(time <= (1000 * 60)){
            timeSize = 1;
        } else {
            timeSize = (int) (time / (1000 * 60));
        }
        return timeSize + "分";
    }
}
