package com.google.android.libraries.accessibility.utils.log;

import android.database.SQLException;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.google.android.accessibility.utils.Performance;

import java.time.LocalDateTime;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "log_entries")
public class LogEntry {
    @DatabaseField(id = true)
    private LocalDateTime               dateTime;
    @DatabaseField
    private int                         scrollDirection;
    @DatabaseField
    private Performance.EventId         eventId;
    @DatabaseField
    private int                         isScreenOn;
    @DatabaseField
    private AccessibilityEvent          accessibilityEvent;
    @DatabaseField
    private boolean                     isWindowChange;
    @DatabaseField
    private AccessibilityNodeInfo       currentNodeInfo;
    @DatabaseField
    private List<AccessibilityNodeInfo> viewNodeInfo;
    @DatabaseField
    private int                         gesture;
    @DatabaseField
    private int                         action;
    @DatabaseField
    private String                      ttsEngine;
    @DatabaseField
    private int                         ttsSpeechRate;
    @DatabaseField
    private int                         ttsPitch;

    public LogEntry() {
        dateTime=LocalDateTime.now();
        scrollDirection = -1;
        eventId= null;
        isScreenOn = -1;
        accessibilityEvent = null;
        isWindowChange =false;
        currentNodeInfo = null;
        viewNodeInfo = null;
        gesture= -1;
        action= -1;
        ttsEngine = null;
        ttsSpeechRate = -1;
        ttsPitch = -1;
    }

    public void setScrollDirection(int scrollDirection) {
        this.scrollDirection = scrollDirection;
    }

    public void setEventId(Performance.EventId eventId) {
        this.eventId = eventId;
    }

    public void setIsScreenOn(int isScreenOn) {
        this.isScreenOn = isScreenOn;
    }

    public void setIsWindowChange(boolean isWindowChange) {
        this.isWindowChange = isWindowChange;
    }

    public void setCurrentNodeInfo(AccessibilityNodeInfo currentNodeInfo) {
        this.currentNodeInfo = currentNodeInfo;
    }

    public void setViewNodeInfo(List<AccessibilityNodeInfo> viewNodeInfo) {
        this.viewNodeInfo = viewNodeInfo;
    }

    public void setGesture(int gesture) {
        this.gesture = gesture;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public void setTTSPitch(int ttsPitch) {
        this.ttsPitch = ttsPitch;
    }

    public void setTTSSpeechRate(int ttsSpeechRate) {
        this.ttsSpeechRate = ttsSpeechRate;
    }

    public void setTTSEngine(String ttsEngine) {
        this.ttsEngine = ttsEngine;
    }

    public void setAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        this.accessibilityEvent = accessibilityEvent;
    }

    public void SaveToLocalDB(LogHelper logHelper){
        try {
            // DAO 객체 가져오기
            Dao<LogEntry, LocalDateTime> logEntryDao = logHelper.getLogEntryDao();
            // 데이터베이스에 저장
            logEntryDao.create(this);
        } catch (SQLException | java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public void SaveToDBServer(){

    }
}
