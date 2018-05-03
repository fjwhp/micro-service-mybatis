package aljoin.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 
 * 日期工具类
 *
 * @author：zhongjy
 * 
 * @date：2017年5月25日 下午2:15:32
 */
public class DateUtil {
    /**
     * 年、月、星期、日、时、分、秒、毫秒
     */
    public static final String UNIT_YEAR = "year";
    public static final String UNIT_MONTH = "month";
    public static final String UNIT_WEEK = "week";
    public static final String UNIT_DAY = "day";
    public static final String UNIT_HOUR = "hour";
    public static final String UNIT_MINUTE = "minute";
    public static final String UNIT_SECOND = "second";
    public static final String UNIT_MILLI = "milli";

    /**
     * 
     * 时间转字符串,如果传入参数为空则返回当前时间
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月26日 下午1:10:30
     */
    public static String datetime2str(Date date) {
        if (date == null) {
            return new DateTime().toString("yyyy-MM-dd HH:mm:ss");
        } else {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        }
    }

    /**
     * 
     * 日期转字符串,如果传入参数为空则返回当前日期
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月26日 下午1:10:18
     */
    public static String date2str(Date date) {
        if (date == null) {
            return new DateTime().toString("yyyy-MM-dd");
        } else {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
        }
    }

    /**
     * 
     * 字符串转日期,如果参数为空则返回当前日期
     *
     * @return：Date
     *
     * @author：zhongjy
     *
     * @date：2017年5月26日 下午1:10:07
     */
    public static Date str2date(String str) throws ParseException {
        if (str == null) {
            return new Date();
        } else {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str);
        }
    }

    /**
     * 
     * 字符串转时间,如果参数为空则返回当前时间
     *
     * @return：Date
     *
     * @author：zhongjy
     *
     * @date：2017年5月26日 下午1:09:58
     */
    public static Date str2datetime(String str) throws ParseException {
        if (str == null) {
            return new Date();
        } else {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
        }
    }

    /**
     * 
     * 字符串转日期,如果参数为空则返回当前日期(匹配日期或者日期时间)
     *
     * @return：Date
     *
     * @author：zhongjy
     *
     * @date：2017年5月26日 下午1:10:07
     */
    public static Date str2dateOrTime(String str) throws ParseException {
        if (str == null) {
            return new Date();
        } else {
            if (str.length() == 10) {
                return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str);
            } else {
                return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
            }
        }
    }

    /**
     * 
     * 判断tag是否在beg到end这个时间段中(不含临界时间).
     *
     * @return：boolean
     *
     * @author：zhongjy
     *
     * @date：2017年5月26日 下午1:21:26
     */
    public static boolean isInPeriod(Date beg, Date end, Date tag) {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        // 转DateTime
        DateTime beg1 = DateTime.parse(datetime2str(beg), format);
        DateTime end1 = DateTime.parse(datetime2str(end), format);
        DateTime tag1 = DateTime.parse(datetime2str(tag), format);

        Interval inteval = new Interval(beg1, end1);
        return inteval.contains(tag1);
    }

    /**
     * 
     * 判断tag是否在beg到end这个时间段中(不含临界时间).
     *
     * @return：boolean
     *
     * @author：zhongjy
     *
     * @date：2017年5月26日 下午1:21:26
     */
    public static boolean isInPeriod(String beg, String end, String tag) {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        // 转DateTime
        DateTime beg1 = DateTime.parse(beg, format);
        DateTime end1 = DateTime.parse(end, format);
        DateTime tag1 = DateTime.parse(tag, format);

        Interval inteval = new Interval(beg1, end1);
        return inteval.contains(tag1);
    }

    /**
     * 
     * 获取两个时间之间相差的时间间隔
     *
     * @return：long
     *
     * @author：zhongjy
     *
     * @date：2017年5月26日 下午1:42:12
     */
    public static long getPeriod(Date beg, Date end, String unit) {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        // 转DateTime
        DateTime beg1 = DateTime.parse(datetime2str(beg), format);
        DateTime end1 = DateTime.parse(datetime2str(end), format);

        Duration duration = new Duration(beg1, end1);
        if (DateUtil.UNIT_DAY.equals(unit)) {
            return duration.getStandardDays();
        } else if (DateUtil.UNIT_HOUR.equals(unit)) {
            return duration.getStandardHours();
        } else if (DateUtil.UNIT_MINUTE.equals(unit)) {
            return duration.getStandardMinutes();
        } else if (DateUtil.UNIT_SECOND.equals(unit)) {
            return duration.getStandardSeconds();
        } else {
            return duration.getMillis();
        }

    }

    /**
     * 
     * 获取两个时间之间相差的时间间隔
     *
     * @return：long
     *
     * @author：zhongjy
     *
     * @date：2017年5月26日 下午1:42:03
     */
    public static long getPeriod(String beg, String end, String unit) {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        // 转DateTime
        DateTime beg1 = DateTime.parse(beg, format);
        DateTime end1 = DateTime.parse(end, format);

        Duration duration = new Duration(beg1, end1);
        if (DateUtil.UNIT_DAY.equals(unit)) {
            return duration.getStandardDays();
        } else if (DateUtil.UNIT_HOUR.equals(unit)) {
            return duration.getStandardHours();
        } else if (DateUtil.UNIT_MINUTE.equals(unit)) {
            return duration.getStandardMinutes();
        } else if (DateUtil.UNIT_SECOND.equals(unit)) {
            return duration.getStandardSeconds();
        } else {
            return duration.getMillis();
        }
    }

    /**
     * 
     * 获取n年、月、周...前的日期
     *
     * @return：Date
     *
     * @author：zhongjy
     *
     * @date：2017年5月26日 下午2:57:21
     */
    public static Date getBefore(Date datetime, String unit, int n) throws ParseException {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        // 转DateTime
        DateTime dt = DateTime.parse(datetime2str(datetime), format);
        // 根据时间单位获取之前的某个时间
        if (DateUtil.UNIT_YEAR.equals(unit)) {
            return str2datetime(dt.minusYears(n).toString("yyyy-MM-dd HH:mm:ss"));
        } else if (DateUtil.UNIT_MONTH.equals(unit)) {
            return str2datetime(dt.minusMonths(n).toString("yyyy-MM-dd HH:mm:ss"));
        } else if (DateUtil.UNIT_WEEK.equals(unit)) {
            return str2datetime(dt.minusWeeks(n).toString("yyyy-MM-dd HH:mm:ss"));
        } else if (DateUtil.UNIT_DAY.equals(unit)) {
            return str2datetime(dt.minusDays(n).toString("yyyy-MM-dd HH:mm:ss"));
        } else if (DateUtil.UNIT_HOUR.equals(unit)) {
            return str2datetime(dt.minusHours(n).toString("yyyy-MM-dd HH:mm:ss"));
        } else if (DateUtil.UNIT_MINUTE.equals(unit)) {
            return str2datetime(dt.minusMinutes(n).toString("yyyy-MM-dd HH:mm:ss"));
        } else if (DateUtil.UNIT_SECOND.equals(unit)) {
            return str2datetime(dt.minusSeconds(n).toString("yyyy-MM-dd HH:mm:ss"));
        } else {
            return str2datetime(dt.minusMillis(n).toString("yyyy-MM-dd HH:mm:ss"));
        }
    }

    /**
     * 
     * 获取n年、月、周...后的日期
     *
     * @return：Date
     *
     * @author：zhongjy
     *
     * @date：2017年5月26日 下午2:58:01
     */
    public static Date getAfter(Date datetime, String unit, int n) throws ParseException {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        // 转DateTime
        DateTime dt = DateTime.parse(datetime2str(datetime), format);

        // 根据时间单位获取之后的某个时间
        if (DateUtil.UNIT_YEAR.equals(unit)) {
            return str2datetime(dt.plusYears(n).toString("yyyy-MM-dd HH:mm:ss"));
        } else if (DateUtil.UNIT_MONTH.equals(unit)) {
            return str2datetime(dt.plusMonths(n).toString("yyyy-MM-dd HH:mm:ss"));
        } else if (DateUtil.UNIT_WEEK.equals(unit)) {
            return str2datetime(dt.plusWeeks(n).toString("yyyy-MM-dd HH:mm:ss"));
        } else if (DateUtil.UNIT_DAY.equals(unit)) {
            return str2datetime(dt.plusDays(n).toString("yyyy-MM-dd HH:mm:ss"));
        } else if (DateUtil.UNIT_HOUR.equals(unit)) {
            return str2datetime(dt.plusHours(n).toString("yyyy-MM-dd HH:mm:ss"));
        } else if (DateUtil.UNIT_MINUTE.equals(unit)) {
            return str2datetime(dt.plusMinutes(n).toString("yyyy-MM-dd HH:mm:ss"));
        } else if (DateUtil.UNIT_SECOND.equals(unit)) {
            return str2datetime(dt.plusSeconds(n).toString("yyyy-MM-dd HH:mm:ss"));
        } else {
            return str2datetime(dt.plusMillis(n).toString("yyyy-MM-dd HH:mm:ss"));
        }
    }

    /**
     * 
     * 根据日期获取星期(如果参数为空则返回当天星期几).
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月26日 下午2:46:04
     */
    public static String getWeek(Date date) {
        int w = 0;
        if (date == null) {
            w = new DateTime().getDayOfWeek();
        } else {
            DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            // 转DateTime
            DateTime dt = DateTime.parse(datetime2str(date), format);
            w = dt.getDayOfWeek();
        }
        String week = "";
        if (w == 1) {
            week = "星期一";
        } else if (w == 2) {
            week = "星期二";
        } else if (w == 3) {
            week = "星期三";
        } else if (w == 4) {
            week = "星期四";
        } else if (w == 5) {
            week = "星期五";
        } else if (w == 6) {
            week = "星期六";
        } else {
            week = "星期日";
        }
        return week;
    }

    /**
     * 
     * 获取传入的时间是一年中的第几个星期（如果传入的时间为null则返回当前时间是第几个星期）.
     *
     * @return：int
     *
     * @author：zhongjy
     *
     * @date：2017年5月26日 下午2:54:45
     */
    public static int getWeekNumber(Date date) {
        if (date == null) {
            return new DateTime().getWeekOfWeekyear();
        } else {
            DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            // 转DateTime
            DateTime dt = DateTime.parse(datetime2str(date), format);
            return dt.getWeekOfWeekyear();
        }
    }

    /**
     * 
     * 时间转字符串,如果传入参数为空则返回当前时间(返回数字字符串)
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月26日 下午1:10:30
     */
    public static String datetime2numstr(Date date) {
        if (date == null) {
            return new DateTime().toString("yyyyMMddHHmmss");
        } else {
            return new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(date);
        }
    }

    /**
     *
     * 计算两个时间间隔分钟数(返回数字)
     *
     * @return：Long
     *
     * @author：wangj
     *
     * @date：2017年2月26日
     */
    public static Long get2DateMinutes(Date date) {
        DateTime dt = new DateTime();
        DateTime begin = new DateTime(date);
        Duration duration = new Duration(begin, dt);
        // 两个时间之间 所差 天，小时 ，分，秒
        // duration.getStandardDays();
        // duration.getStandardHours();
        // duration.getStandardMinutes();
        // duration.getStandardSeconds();
        return duration.getStandardMinutes();
    }

    /**
     *
     * 获得本月天数(返回数字)
     *
     *
     * @return：int
     *
     * @author：wangj
     *
     * @date：2017年2月26日
     */
    public static int getDaysOfMonth() {
        LocalDate localDate = new LocalDate();
        DateTime dateTime = new DateTime(localDate.getYear(), localDate.getMonthOfYear(), 14, 12, 0, 0, 000);
        return dateTime.dayOfMonth().getMaximumValue();
    }

    /**
     *
     * 获得本月第一天的日期(返回字符串)
     *
     * parttern : 日期字符串返回格式(如：yyyy-MM-dd)
     *
     * @return：Stirng
     *
     * @author：wangj
     *
     * @date：2017年2月26日
     */
    public static String getFirstDateOfMonth(String parttern) {
        return DateTime.now().dayOfMonth().withMinimumValue().toString(parttern);
    }

    /**
     *
     * 获得本月最后一天的日期(返回字符串)
     *
     * parttern : 日期字符串返回格式(如：yyyy-MM-dd)
     *
     * @return：Stirng
     *
     * @author：wangj
     *
     * @date：2017年2月26日
     */
    public static String getLastDateOfMonth(String parttern) {
        return DateTime.now().dayOfMonth().withMaximumValue().toString(parttern);
    }

    /**
     *
     * 获得本周第一天的日期(返回字符串)
     *
     * parttern : 日期字符串返回格式(如：yyyy-MM-dd)
     *
     * @return：Stirng
     *
     * @author：wangj
     *
     * @date：2017年2月26日
     */
    public static String getFirstDateOfWeek(String parttern) {
        return DateTime.now().dayOfWeek().withMinimumValue().toString(parttern);
    }

    /**
     *
     * 获得本周最后一天的日期(返回字符串)
     *
     * parttern : 日期字符串返回格式(如：yyyy-MM-dd)
     *
     * @return：Stirng
     *
     * @author：wangj
     *
     * @date：2017年2月26日
     */
    public static String getLastDateOfWeek(String parttern) {
        return DateTime.now().dayOfWeek().withMaximumValue().toString(parttern);
    }

    /**
     *
     * 获得上周第一天的日期(返回字符串)
     *
     * parttern : 日期字符串返回格式(如：yyyy-MM-dd)
     *
     * @return：Stirng
     *
     * @author：wangj
     *
     * @date：2017年2月26日
     */
    public static String getFirstDateOfLastWeek(String parttern) {
        return DateTime.now().dayOfWeek().withMinimumValue().minusDays(7).toString(parttern);
    }

    /**
     *
     * 获得上周最后一天的日期(返回字符串)
     *
     * parttern : 日期字符串返回格式(如：yyyy-MM-dd)
     *
     * @return：Stirng
     *
     * @author：wangj
     *
     * @date：2017年2月26日
     */
    public static String getLastDateOfLastWeek(String parttern) {
        return DateTime.now().dayOfWeek().withMaximumValue().minusDays(7).toString(parttern);
    }

    /**
     *
     * 获得下周第一天的日期(返回字符串)
     *
     * parttern : 日期字符串返回格式(如：yyyy-MM-dd)
     *
     * @return：Stirng
     *
     * @author：wangj
     *
     * @date：2017年2月26日
     */
    public static String getFirstDateOfNextWeek(String parttern) {
        return DateTime.now().dayOfWeek().withMinimumValue().plusDays(7).toString(parttern);
    }

    /**
     *
     * 获得上周最后一天的日期(返回字符串)
     *
     * parttern : 日期字符串返回格式(如：yyyy-MM-dd)
     *
     * @return：Stirng
     *
     * @author：wangj
     *
     * @date：2017年2月26日
     */
    public static String getLastDateOfNextWeek(String parttern) {
        return DateTime.now().dayOfWeek().withMaximumValue().plusDays(7).toString(parttern);
    }

    /**
     *
     * 获得指定月份第一天的日期(返回字符串)
     *
     * month : 月份的格式（如：2017-10） parttern : 日期字符串返回格式(如：yyyy-MM-dd)
     *
     * @return：Stirng
     *
     * @author：wangj
     *
     * @date：2017年10月09日
     */
    public static String getFirstDateOfMonth(String month, String parttern) {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM");
        DateTime dateTime = DateTime.parse(month, format);
        return dateTime.dayOfMonth().withMinimumValue().toString(parttern);
    }

    /**
     *
     * 获得指定月份最后一天的日期(返回字符串)
     *
     * month : 月份的格式（如：2017-10） parttern : 日期字符串返回格式(如：yyyy-MM-dd)
     *
     * @return：Stirng
     *
     * @author：wangj
     *
     * @date：2017年10月09日
     */
    public static String getLastDateOfMonth(String month, String parttern) {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM");
        DateTime dateTime = DateTime.parse(month, format);
        return dateTime.dayOfMonth().withMaximumValue().toString(parttern);
    }

    /**
     *
     * 根据月份获得对应天数(返回数字)
     *
     * month : 月份的格式（如：2017-10）
     *
     * @return：int
     *
     * @author：wangj
     *
     * @date：2017年10月09日
     */
    public static int getDaysOfMonth(String month) {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM");
        DateTime date = DateTime.parse(month, format);
        LocalDate localDate = new LocalDate(date);

        DateTime dateTime = new DateTime(localDate.getYear(), localDate.getMonthOfYear(), 14, 12, 0, 0, 000);
        return dateTime.dayOfMonth().getMaximumValue();
    }

    /**
     *
     * 获得两个月份相差月份数(返回数字)
     *
     * monthBeg : 开始月份的格式（如：2017-10）
     *
     * monthEnd: 结束月份的格式（如：2017-10）
     *
     * @return：int
     *
     * @author：wangj
     *
     * @date：2017年10月09日
     */
    public static int getMonthNumOf2Month(String monthBeg, String monthEnd) {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM");
        DateTime dateTime = DateTime.parse(monthBeg, format);

        DateTime dateTime2 = DateTime.parse(monthEnd, format);
        Months months = Months.monthsBetween(dateTime, dateTime2);

        return months.getMonths();
    }

    /**
     *
     * 获得当前月份(字符串)
     *
     * date : 日期
     *
     * @return：String
     *
     * @author：wangj
     *
     * @date：2017年10月11日
     */
    public static String getMonthNameOfMonth(Date date) {
        DateTime dateTime = new DateTime(date);
        int month = dateTime.getMonthOfYear();
        String monthName = "";
        switch (month) {
            case 1:
                monthName = "一";
                break;
            case 2:
                monthName = "二";
                break;
            case 3:
                monthName = "三";
                break;
            case 4:
                monthName = "四";
                break;
            case 5:
                monthName = "五";
                break;
            case 6:
                monthName = "六";
                break;
            case 7:
                monthName = "七";
                break;
            case 8:
                monthName = "八";
                break;
            case 9:
                monthName = "九";
                break;
            case 10:
                monthName = "十";
                break;
            case 11:
                monthName = "十一";
                break;
            case 12:
                monthName = "十二";
                break;
            default:
                monthName = "";
                break;
        }
        return monthName;
    }

    /**
     *
     * 根据月份获得对应天数(返回数字)
     *
     * month : 月份的格式（如：2017-10）
     *
     * @return：int
     *
     * @author：wangj
     *
     * @date：2017年10月09日
     */
    @SuppressWarnings("unused")
    public static int getDaysOfMonth(Date d) {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM");
        DateTime date = new DateTime(d);
        return date.getDayOfMonth();
    }

    /**
     * 
     * 获取当前日期时间 hh-12小时制 HH-24小时制，如果date为null则返回当前时间
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年10月26日 上午10:06:25
     */
    public static String date2str(int formatModel, Date date) {
        SimpleDateFormat format = null;
        if (1 == formatModel) {
            format = new SimpleDateFormat("yyyy年MM月dd日");
        } else if (2 == formatModel) {
            format = new SimpleDateFormat("yyyy年MM月dd日 hh时mm分ss秒");
        } else if (3 == formatModel) {
            format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        } else if (4 == formatModel) {
            format = new SimpleDateFormat("yyyy年MM月dd日 hh时mm分ss秒SSS毫秒");
        } else if (5 == formatModel) {
            format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS毫秒");
        } else if (6 == formatModel) {
            format = new SimpleDateFormat("yyyy-MM-dd");
        } else if (7 == formatModel) {
            format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        } else if (8 == formatModel) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else if (9 == formatModel) {
            format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
        } else if (10 == formatModel) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        } else if (11 == formatModel) {
            format = new SimpleDateFormat("yyyy/MM/dd");
        } else if (12 == formatModel) {
            format = new SimpleDateFormat("yyyy/MM/dd hh/mm/ss");
        } else if (13 == formatModel) {
            format = new SimpleDateFormat("yyyy/MM/dd HH/mm/ss");
        } else if (14 == formatModel) {
            format = new SimpleDateFormat("yyyy/MM/dd hh/mm/ss/SSS");
        } else if (15 == formatModel) {
            format = new SimpleDateFormat("yyyy/MM/dd HH/mm/ss/SSS");
        } else if (16 == formatModel) {
            format = new SimpleDateFormat("yyyyMMdd");
        } else if (17 == formatModel) {
            format = new SimpleDateFormat("yyyyMMddhhmmss");
        } else if (18 == formatModel) {
            format = new SimpleDateFormat("yyyyMMddHHmmss");
        } else if (19 == formatModel) {
            format = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        } else if (20 == formatModel) {
            format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        } else if (21 == formatModel) {
            format = new SimpleDateFormat("yyyy.MM.dd");
        } else if (22 == formatModel) {
            format = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");
        } else {
            return null;
        }
        if (date == null) {
            date = new Date();
        }
        return format.format(date);
    }

    /**
     * 
     * 获取某个时间所在的月的所有日期列表
     *
     * @return：List
     *
     * @author：sln
     *
     * @date：2017年10月30日
     */
    public static List<Date> getAllTheDateOftheMonth(Date date) {
        List<Date> list = new ArrayList<Date>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DATE, 1);
        int month = cal.get(Calendar.MONTH);
        while (cal.get(Calendar.MONTH) == month) {
            list.add(cal.getTime());
            cal.add(Calendar.DATE, 1);
        }
        return list;
    }

    /**
     * 
     * 获取某个时间所在的月的所有日期（）
     *
     * @return：List
     *
     * @author：sln
     *
     * @date：2017年10月30日
     */
    public static List<String> getAllTheDateOftheMonthStr(Date date) {
        List<String> list = new ArrayList<String>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DATE, 1);
        int month = cal.get(Calendar.MONTH);
        String dateStr = "";
        String alldateStr = "";
        while (cal.get(Calendar.MONTH) == month) {
            String week = DateUtil.getWeek(cal.getTime());
            dateStr = new DateTime(cal.getTime()).toString("yyyy-MM-dd");
            alldateStr = dateStr + " " + week;
            list.add(alldateStr);
            cal.add(Calendar.DATE, 1);
        }
        return list;
    }

    /**
     * 
     * 字符串转日期,如果参数为空则返回当前日期
     *
     * @return：Date
     *
     * @author：sln
     *
     * @date：2017年5月26日 下午1:10:07
     */
    public static Date str2date(String str, String format) throws ParseException {
        if (str == null) {
            return new Date();
        } else {
            return new java.text.SimpleDateFormat(format).parse(str);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(str2date("2017-10-10"));
    }
}
