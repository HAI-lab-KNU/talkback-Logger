package com.google.android.libraries.accessibility.utils.log;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;

import org.checkerframework.checker.nullness.qual.Nullable;

public class LoggerUtil {
    private static final String TAG = "TalkBackLogger Logger";
    public static class LogEntry{

        @DatabaseField(generatedId= true)
        long id;

        @DatabaseField(index = true)
        long timestamp;

        @DatabaseField
        int level;

        @DatabaseField
        int domain;

        @DatabaseField
        String msg;

        LogEntry() {
        }

        public LogEntry(long timestamp, int level, int domain, String msg) {
            this.timestamp = timestamp;
            this.level = level;
            this.domain = domain;
            this.msg = msg;
        }

    }

    public static final int LEVEL_VERBOSE = 0;

    public static final int LEVEL_DEBUG = 1;

    public static final int LEVEL_INFO = 2;

    public static final int LEVEL_WARN = 3;

    public static final int LEVEL_ERROR = 4;

    public static final int DOMAIN_PERFORMANCE  = 0;

    public static final int DOMAIN_SCROLL_EVENT_INTERPRETER = 1;

    public static final int DOMAIN_RINGER_MODE_AND_SCREEN_MONITOR = 2;

    public static final int DOMAIN_TALKBACK_SERVICE  = 3;

    public static final int DOMAIN_TALKBACK_PREFERENCE_FRAGMENT  = 4;



    public static void i(long timestamp, int domain, String format, @Nullable Object... args) {
        if(args == null)
            Log.i(TAG,LogHelper.SavetoLocalDB(new LogEntry(timestamp, LEVEL_INFO, domain, format)));
        else
            Log.i(TAG,LogHelper.SavetoLocalDB(new LogEntry(timestamp, LEVEL_INFO, domain, String.format(format, args))));
    }
    public static void e(long timestamp, int domain, String format, @Nullable Object... args) {
        if(args == null)
            Log.e(TAG,LogHelper.SavetoLocalDB(new LogEntry(timestamp, LEVEL_ERROR, domain, format)));
        else
            Log.e(TAG,LogHelper.SavetoLocalDB(new LogEntry(timestamp, LEVEL_ERROR, domain, String.format(format, args))));
    }
}
