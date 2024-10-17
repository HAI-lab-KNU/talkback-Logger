package com.google.android.libraries.accessibility.utils.log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class LogHelper  extends OrmLiteSqliteOpenHelper {

    // 데이터베이스 이름과 버전 설정
    private static final String DATABASE_NAME = "logentry.db";
    private static final int DATABASE_VERSION = 1;

    // DAO 객체 선언 (LogEntry에 대한 데이터베이스 접근을 제공)
    private Dao<LogEntry, LocalDateTime> logEntryDao = null;

    public LogHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 데이터베이스를 처음 생성할 때 호출
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            // 테이블 생성 (LogEntry 클래스에 매핑된 테이블)
            TableUtils.createTable(connectionSource, LogEntry.class);
        } catch (SQLException e) {
            Log.e(LogHelper.class.getName(), "Error creating database", e);
        }
    }

    // 데이터베이스 업그레이드 (버전 변경 시 호출)
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, LogEntry.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(LogHelper.class.getName(), "Error upgrading database", e);
        }
    }

    // LogEntry DAO 객체 반환
    public Dao<LogEntry, LocalDateTime> getLogEntryDao() throws SQLException {
        if (logEntryDao == null) {
            logEntryDao = getDao(LogEntry.class);
        }
        return logEntryDao;
    }

    // 데이터베이스 연결 해제
    @Override
    public void close() {
        super.close();
        logEntryDao = null;
    }
}