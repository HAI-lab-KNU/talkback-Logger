package com.google.android.libraries.accessibility.utils.log;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.google.android.accessibility.utils.SharedPreferencesUtils;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogHelper  extends OrmLiteSqliteOpenHelper {

    // 데이터베이스 이름과 버전 설정
    private static final String DATABASE_NAME = "SmartPhone Usage.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "TalkBackLogger LogHelper";


    // DAO 객체 선언 (LogEntry에 대한 데이터베이스 접근을 제공)
    private Dao<LoggerUtil.LogEntry, Long> logEntryDao = null;
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static Context instance;

    public LogHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        instance = context.getApplicationContext();
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

    public static void releaseHelper(){
        OpenHelperManager.releaseHelper();
        executorService.shutdown();
    }
    //noti : Log.d("Log to server", "");
    public static void scheduleDBUpload() {
        executorService.scheduleWithFixedDelay(() -> {
            try {
                Log.d("Log to server", "executorService run");
                String dbPath = instance.getDatabasePath(DATABASE_NAME).getAbsolutePath();
                File dbFile = new File(dbPath);
                if (dbFile.exists()) {
                    Log.d("Log to server", "dbFile exists");
                    File zipFile = new File(instance.getFilesDir(), "SmartPhoneUsage.zip");
                    SharedPreferences prefs = SharedPreferencesUtils.getSharedPreferences(instance);
                    String pid = prefs.getString("pref_experimenter_number", "default_pid");
                    Log.d("Log to server", "pid : "+ pid);
                    if(!pid.equals("default_pid")&&!pid.equals("컨텍스트 없음")&&!pid.equals("권한 없음")&&!pid.equals("전화번호 없음")) {
                        zipDBFile(dbFile, zipFile);
                        uploadDBFile(zipFile, pid);
                    }
                }
            } catch (IOException e) {
                Log.e("Log to server", "Error in scheduleDBUpload: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.HOURS); // Initial delay: 0, Delay between task completions: 1 hour
    }

    private static void zipDBFile(File dbFile, File zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new java.io.FileOutputStream(zipFile))) {
            try (java.io.FileInputStream fis = new java.io.FileInputStream(dbFile)) {
                ZipEntry zipEntry = new ZipEntry(dbFile.getName());
                zos.putNextEntry(zipEntry);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                Log.d("Log to server", "success to compress");
            }
        }
    }

    private static void uploadDBFile(File zipFile,String pid) {
        LogHelper logHelper = OpenHelperManager.getHelper(instance, LogHelper.class);
        long si = 0;
        long ei = 0;
        long count = 0;

        try {
            Dao<LoggerUtil.LogEntry, Long> logEntryDao = logHelper.getLogEntryDao();
            si = logEntryDao.queryRawValue("SELECT MIN(id) FROM LogEntry");
            ei = logEntryDao.queryRawValue("SELECT MAX(id) FROM LogEntry");
            count = logEntryDao.countOf();
        } catch (SQLException e) {
            Log.e("Log to server", "Error fetching data for upload URL: " + e.getMessage());
        } finally {
            OpenHelperManager.releaseHelper();
        }

        String url = String.format(ServerURL.serverURL+"/api/upload?pid=%s&si=%d&ei=%d&count=%d", pid, si, ei, count);
        Log.d("Log to server", "URL : "+ url);
        OkHttpClient client = new OkHttpClient.Builder().build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", zipFile.getName(),
                        RequestBody.create(zipFile, MediaType.parse("application/zip")))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        final long finalSi = si;
        final long finalEi = ei;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Log to server", "Failed to upload file: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null && response.body().string().contains("Success")) {
                    Log.d("Log to server", "File uploaded successfully");
                    if (zipFile.delete()) {
                        Log.d("Log to server", "Zip file deleted successfully");
                    } else {
                        Log.e("Log to server", "Failed to delete zip file");
                    }

                    LogHelper logHelper = OpenHelperManager.getHelper(instance, LogHelper.class);
                    try {
                        Dao<LoggerUtil.LogEntry, Long> logEntryDao = logHelper.getLogEntryDao();
                        logEntryDao.executeRaw("DELETE FROM LogEntry WHERE id BETWEEN ? AND ?", String.valueOf(finalSi), String.valueOf(finalEi));
                        Log.d("Log to server", "Original log entries deleted successfully");
                    } catch (SQLException e) {
                        Log.e("Log to server", "Failed to delete original log entries: " + e.getMessage());
                    } finally {
                        OpenHelperManager.releaseHelper();
                    }
                } else {
                    if (zipFile.delete()) {
                        Log.d("Log to server", "Zip file deleted successfully");
                    } else {
                        Log.e("Log to server", "Failed to delete zip file");
                    }
                    Log.e("Log to server", "Failed to upload file: " + response.message());
                }
            }
        });
    }
}
