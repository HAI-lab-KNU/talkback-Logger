//package com.google.android.libraries.accessibility.utils.log;
//
//import android.util.Log;
//
//import com.j256.ormlite.field.DatabaseField;
//
//import org.checkerframework.checker.nullness.qual.Nullable;
//
//import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
//import java.util.Date;
//
//public class Logger {
//
//    public static class LogEntry{
//
//        @DatabaseField(generatedId= true)
//        long id;
//
//        @DatabaseField(index = true)
//        long timestamp;
//
//        @DatabaseField
//        int level;
//
//        @DatabaseField
//        int domain;
//
//        @DatabaseField
//        String msg;
//
//        LogEntry() {
//        }
//
//        public LogEntry(long timestamp, int level, int domain, String msg) {
//            this.timestamp = timestamp;
//            this.level = level;
//            this.domain = domain;
//            this.msg = msg;
//        }
//
//
//
//    }
//
//    private static final int LEVEL_I = 1;
//
//    public static final int DOMAIN_PERFORMANCE  = 1;
//
//    public static void i(LocalDateTime timestamp, int domain, String format, @Nullable Object... args) {
//        long timestampInMili = timestamp.
//        if(args == null)
//            LogHelper.SavetoLocalDB(new LogEntry(timestamp., LEVEL_I, domain, format));
//        else
//            LogHelper.SavetoLocalDB(new LogEntry(timestamp, LEVEL_I, domain, String.format(format, args));
//    }
//}
