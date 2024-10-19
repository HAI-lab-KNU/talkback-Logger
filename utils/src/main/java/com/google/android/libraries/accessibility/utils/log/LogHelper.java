package com.google.android.libraries.accessibility.utils.log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class LogHelper  extends OrmLiteSqliteOpenHelper {

    // 데이터베이스 이름과 버전 설정
    private static final String DATABASE_NAME = "SmartPhone Usage.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "TalkBackLogger LogHelper";

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
    public static String SavetoLocalDB(LoggerUtil.LogEntry logEntry) {
        // OpenHelperManager를 통해 LogHelper 인스턴스를 얻음
        LogHelper logHelper = OpenHelperManager.getHelper(instance, LogHelper.class);

        try {
            // LogEntry를 데이터베이스에 저장
            Dao<LoggerUtil.LogEntry, Long> logEntryDao = logHelper.getLogEntryDao();
            logEntryDao.create(logEntry);
        } catch (SQLException e) {
            e.printStackTrace();
            return "Fail save to DB";
            // 예외 처리 로직 추가 가능
        } finally {
            // 리소스 해제
            OpenHelperManager.releaseHelper();

        }
        return logEntry.timestamp+" : "+logEntry.msg;
    }
}