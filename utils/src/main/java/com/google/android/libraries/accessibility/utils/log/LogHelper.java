package com.google.android.libraries.accessibility.utils.log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LogHelper  extends OrmLiteSqliteOpenHelper {

    // 데이터베이스 이름과 버전 설정
    private static final String DATABASE_NAME = "SmartPhone Usage.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "TalkBackLogger LogHelper";

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();  // 싱글 스레드 풀 생성

    // DAO 객체 선언 (LogEntry에 대한 데이터베이스 접근을 제공)
    private Dao<LoggerUtil.LogEntry, Long> logEntryDao = null;

    private static Context instance;


    public LogHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        instance = context;

    }

    // 데이터베이스를 처음 생성할 때 호출
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            // 테이블 생성 (LogEntry 클래스에 매핑된 테이블)
            TableUtils.createTable(connectionSource, LoggerUtil.LogEntry.class);
        } catch (SQLException e) {
            Log.e(LogHelper.class.getName(), "Error creating database", e);
        }
    }

    // 데이터베이스 업그레이드 (버전 변경 시 호출)
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, LoggerUtil.LogEntry.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(LogHelper.class.getName(), "Error upgrading database", e);
        }
    }

    // LogEntry DAO 객체 반환
    public Dao<LoggerUtil.LogEntry, Long> getLogEntryDao() throws SQLException {
        if (logEntryDao == null) {
            logEntryDao = getDao(LoggerUtil.LogEntry.class);
        }
        return logEntryDao;
    }

    // 데이터베이스 연결 해제
    @Override
    public void close() {
        super.close();
        logEntryDao = null;
    }
    private static boolean flag = false;
    public static String SavetoLocalDB(LoggerUtil.LogEntry logEntry) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                // 데이터베이스에 로그를 저장하는 작업
                LogHelper logHelper = OpenHelperManager.getHelper(instance, LogHelper.class);
                try {
                    Dao<LoggerUtil.LogEntry, Long> logEntryDao = logHelper.getLogEntryDao();
                    logEntryDao.create(logEntry);  // 로그 항목을 데이터베이스에 저장
                    flag=true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    flag=false;
                } finally {
                    OpenHelperManager.releaseHelper();  // 데이터베이스 연결 해제
                }
            }
        });
        return flag? "Success save to Local DB: "+logEntry.msg:"Fail save to Local DB: " + logEntry.msg;
    }
    // ExecutorService를 안전하게 종료하는 메서드
    public static void shutdownExecutor() {
        executor.shutdown();  // 더 이상 새로운 작업을 받지 않음
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {  // 작업이 종료될 때까지 최대 60초 대기
                executor.shutdownNow();  // 대기 시간이 초과되면 즉시 종료
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();  // 예외 발생 시 즉시 종료
        }
    }
}