package com.google.android.libraries.accessibility.utils.log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogHelper  extends OrmLiteSqliteOpenHelper {

    // 데이터베이스 이름과 버전 설정
    private static final String DATABASE_NAME = "SmartPhone Usage.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "TalkBackLogger LogHelper";


    // DAO 객체 선언 (LogEntry에 대한 데이터베이스 접근을 제공)
    private Dao<LoggerUtil.LogEntry, Long> logEntryDao = null;
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static Context instance;

    private static long lastBackupTimestamp = System.currentTimeMillis();
    // 1시간(3600000 밀리초)마다 백업
    private static final long BACKUP_INTERVAL = 3600000;
    private static Handler backupHandler;
    private static Runnable backupTask;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초", Locale.getDefault());

    public LogHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        instance = context;
        if(backupHandler == null) backupHandler= new Handler(Looper.getMainLooper());
        if(backupTask==null)

            backupTask = new Runnable() {
                @Override
                public void run() {
                    backupDatabase();  // 백업 작업 실행
                    backupHandler.postDelayed(this, BACKUP_INTERVAL);  // 1시간 후 다시 실행
                }
        };


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

    public static String SavetoLocalDB(LoggerUtil.LogEntry logEntry) {
        // 데이터베이스에 로그를 저장하는 작업
        LogHelper logHelper = OpenHelperManager.getHelper(instance, LogHelper.class);
        try {
            Dao<LoggerUtil.LogEntry, Long> logEntryDao = logHelper.getLogEntryDao();
            logEntryDao.create(logEntry);  // 로그 항목을 데이터베이스에 저장
        } catch (SQLException e) {
            e.printStackTrace();
            return "Fail save to Local DB (" + e + "): " + logEntry.msg;
        }
        return "Success save to Local DB: " + logEntry.msg;
    }

    public static void backupDatabase() {
        LogHelper logHelper = OpenHelperManager.getHelper(instance, LogHelper.class);
        try {
            Dao<LoggerUtil.LogEntry, Long> logEntryDao = logHelper.getLogEntryDao();

            // 마지막 백업 시점 이후에 저장된 로그만 가져옴
            List<LoggerUtil.LogEntry> newLogs = logEntryDao.queryBuilder()
                    .where().ge("timestamp", lastBackupTimestamp)  // 마지막 백업 이후의 로그만
                    .query();

            // 새 백업 파일에 새로운 로그 저장 (백업 파일명: log_backup_<현재 시간>.db)
            String backupFileName = "log_backup_" + dateFormat.format(new Date(System.currentTimeMillis())) + ".db";
            BackupToFile(newLogs, backupFileName,logEntryDao);

            // 마지막 백업 시점을 업데이트
            lastBackupTimestamp = System.currentTimeMillis();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void BackupToFile(List<LoggerUtil.LogEntry> logs, String backupFileName, Dao<LoggerUtil.LogEntry, Long> logEntryDao) {
        // 백업 파일에 로그를 저장하는 로직 구현
        // 백업 파일을 생성하고, logs 리스트를 파일에 저장
        //todo : LogEntry 형식으로 저장.
        executorService.submit(()->{
            try {
                for (LoggerUtil.LogEntry log : logs) {
                    logEntryDao.create(log);
                }
                Log.i(TAG, "Backup completed: " + backupFileName);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to create backup file: " + e.getMessage());
            }
        });
    }
    // 백업 작업을 시작하는 메서드
    public static void startBackupTask() {
        backupHandler.post(backupTask);  // 즉시 첫 백업 실행
    }

    // 백업 작업을 중지하는 메서드
    public static void stopBackupTask() {
        backupHandler.removeCallbacks(backupTask);  // 백업 작업 취소
    }
    public static void releaseHelper(){
        OpenHelperManager.releaseHelper();
        executorService.shutdown();
    }
}