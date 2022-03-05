package ch.uzh.ifi.hase.soprafs22.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Time {
    private static final SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String getCurrentTime() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return f1.format(timestamp);
    }
}
