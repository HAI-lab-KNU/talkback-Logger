package com.google.android.libraries.accessibility.utils.log;

import android.util.Log;

import androidx.annotation.NonNull;

import com.j256.ormlite.field.DatabaseField;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoggerUtil {
    private static final String TAG = "LoggerUtil";
    public static class LogEntry{

        @DatabaseField(generatedId= true,index = true)
        long id;

        @DatabaseField
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

        @NonNull
        @Override
        public String toString() {
            return "timestamp=" + timestamp +
                    "| level=" + level +
                    "| domain=" + domain +
                    "| msg='" + msg;
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

    public static final int DOMAIN_TALKBACK_PREFERENCE  = 4;

    public static final int DOMAIN_SPEECH_CONTROLLER=5;

    private static ExecutorService executor;

    public static final int EVENT_IS_SCREEN_ON =0;

    public static final int EVENT_CHILDREN_NODES =1;

    public static final int EVENT_CURRENT_NODE =2;

    public static final int EVENT_EVENT_ID =3;

    public static final int EVENT_PREFERENCE =4;

    public static final int EVENT_TTS_INFORMATION_DEFAULT =5;

    public static final int EVENT_TTS_SPEECH_RATE =6;

    public static final int EVENT_SCROLL_DIRECTION =7;

    public LoggerUtil() {
        if(executor == null)
            executor = Executors.newSingleThreadExecutor();
    }

    public static void i(long timestamp, int domain, String format, @Nullable Object... args) {
        if(executor!=null)
            if(!executor.isShutdown()) {
                executor.submit(() -> {
                    if (format != null) {
                        String message = (args == null || args.length == 0) ? format : String.format(format, args);
                        String replaced = message.replace("\n", "\\n");
                        if (replaced.isEmpty()) return;
                        Log.i(TAG + "-" + domain, LogHelper.SavetoLocalDB(new LogEntry(timestamp, LEVEL_INFO, domain, replaced)));
                    }
                });
            }
        else{
            if (format != null) {
                String message = (args == null || args.length == 0) ? format : String.format(format, args);
                String replaced = message.replace("\n", "\\n");
                if (replaced.isEmpty()) return;
                try {
                    Log.i(TAG + "-" + domain, LogHelper.SavetoLocalDB(new LogEntry(timestamp, LEVEL_INFO, domain, replaced)));
                } catch (Exception e) {
                    Log.i(TAG+" - Error","executor is terminated && OrmLiteSqliteOpenHelper is released");
                }
            }
        }
    }
    public static void e(long timestamp, int domain, String format, @Nullable Object... args) {
        if(executor!=null)
            if(!executor.isShutdown()) {
                executor.submit(() -> {
                    if (format != null) {
                        String message = (args == null || args.length == 0) ? format : String.format(format, args);
                        String replaced = message.replace("\n", "\\n");
                        if (replaced.isEmpty()) return;
                        Log.e(TAG + "-" + domain, LogHelper.SavetoLocalDB(new LogEntry(timestamp, LEVEL_ERROR, domain, replaced)));
                    }
                });
            }
            else{
                if (format != null) {
                    String message = (args == null || args.length == 0) ? format : String.format(format, args);
                    String replaced = message.replace("\n", "\\n");
                    if (replaced.isEmpty()) return;
                    try {
                        Log.e(TAG + "-" + domain, LogHelper.SavetoLocalDB(new LogEntry(timestamp, LEVEL_ERROR, domain, replaced)));
                    } catch (Exception e) {
                        Log.e(TAG+" - Error","executor is terminated && OrmLiteSqliteOpenHelper is released");
                    }
                }
            }
    }
    // ExecutorService를 안전하게 종료하는 메서드
    public static void shutdownExecutor() {
        // 새로운 작업을 받지 않도록 종료
        executor.shutdown();

        // 별도의 스레드에서 작업이 종료될 때까지 비동기적으로 대기
        new Thread(() -> {
            try {
                // 남아 있는 작업이 모두 완료될 때까지 대기
                while (!executor.isTerminated()) {
                    Thread.sleep(3000);  // 3초 간격으로 상태 확인
                }
                // 모든 작업이 완료되면 shutdownNow 호출
                executor.shutdownNow();
                LogHelper.releaseHelper();
            } catch (InterruptedException e) {
                executor.shutdownNow();  // 예외 발생 시 즉시 종료
                Thread.currentThread().interrupt();  // 현재 스레드에 인터럽트 설정
            }
        }).start();  // 새로운 스레드 시작
    }
}
