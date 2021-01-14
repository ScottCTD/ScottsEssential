package xyz.scottc.scessential.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String toString(long time, String pattern) {
        return new SimpleDateFormat(pattern).format(new Date(time));
    }

    public static long getTime(String date, String pattern) throws ParseException {
        return new SimpleDateFormat(pattern).parse(date).getTime();
    }

}
