package com.google.android.libraries.accessibility.utils.log;

import android.database.SQLException;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.google.android.accessibility.utils.Performance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "log_entries")
public class LogEntry {
    @DatabaseField(id = true)
    private String dateTime;
    @DatabaseField
//  private AccessibilityEvent accessibilityEvent;
    private String accessibilityEvent;
//  private String eventType;
//  private String packageName;
//  private int accessibilityEventAction;
//  private String ContentDescription;
    @DatabaseField
//  private Performance.EventId eventId;
    private String eventId;
    @DatabaseField
    private int gesture;
    @DatabaseField
    private String  action;
    @DatabaseField
    private int scrollDirection;
    @DatabaseField
    private int isScreenOn;
    @DatabaseField
    private boolean isWindowChange;
    @DatabaseField
//  private AccessibilityNodeInfo currentNodeInfo;
    private String currentNodeInfo;
    @DatabaseField
//  private List<AccessibilityNodeInfo> viewNodeInfo;
    private String viewNodeInfo;
    @DatabaseField
    private String ttsEngine;
    @DatabaseField
    private float ttsSpeechRate;
    @DatabaseField
    private float ttsPitch;

    public LogEntry() {
        dateTime=LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        scrollDirection = -1;
        eventId= null;
        isScreenOn = -1;
        accessibilityEvent = null;
        isWindowChange =false;
        currentNodeInfo = null;
        viewNodeInfo = null;
        gesture= -1;
        action= null;
        ttsEngine = null;
        ttsSpeechRate = -1.0F;
        ttsPitch = -1.0F;
    }

    public void setScrollDirection(int scrollDirection) {this.scrollDirection = scrollDirection;}

    public void setEventId(Performance.EventId eventId) {
        this.eventId = eventId.toString();
    }

    public void setIsScreenOn(int isScreenOn) {
        this.isScreenOn = isScreenOn;
    }

    public void setIsWindowChange(boolean isWindowChange) {
        this.isWindowChange = isWindowChange;
    }

    public void setCurrentNodeInfo(AccessibilityNodeInfo currentNodeInfo) {this.currentNodeInfo = currentNodeInfo.toString();}

    public void setViewNodeInfo(List<AccessibilityNodeInfo> viewNodeInfo) {this.viewNodeInfo = viewNodeInfo.toString();}

    public void setGesture(int gesture) {
        this.gesture = gesture;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setTTSPitch(float ttsPitch) {
        this.ttsPitch = ttsPitch;
    }

    public void setTTSSpeechRate(float ttsSpeechRate) {
        this.ttsSpeechRate = ttsSpeechRate;
    }

    public void setTTSEngine(String ttsEngine) {
        this.ttsEngine = ttsEngine;
    }

    public void setAccessibilityEvent(AccessibilityEvent accessibilityEvent) {this.accessibilityEvent = accessibilityEvent.toString();}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LogEntry{");
        sb.append("dateTime=").append(dateTime).append(", ");

        if (scrollDirection != -1) {
            sb.append("scrollDirection=").append(scrollDirection).append(", ");
        }
        if (eventId != null) {
            sb.append("eventId=").append(eventId).append(", ");
        }
        if (isScreenOn != -1) {
            sb.append("isScreenOn=").append(isScreenOn).append(", ");
        }
        if (accessibilityEvent != null) {
            sb.append("accessibilityEvent=").append(accessibilityEvent).append(", ");
        }
        if (isWindowChange) {
            sb.append("isWindowChange=").append(isWindowChange).append(", ");
        }
        if (currentNodeInfo != null) {
            sb.append("currentNodeInfo=").append(currentNodeInfo).append(", ");
        }
        if (viewNodeInfo != null) {
            sb.append("viewNodeInfo=").append(viewNodeInfo).append(", ");
        }
        if (gesture != -1) {
            sb.append("gesture=").append(gesture).append(", ");
        }
        if (action != null) {
            sb.append("action='").append(action).append("', ");
        }
        if (ttsEngine != null) {
            sb.append("ttsEngine='").append(ttsEngine).append("', ");
        }
        if (ttsSpeechRate != -1.0F) {
            sb.append("ttsSpeechRate=").append(ttsSpeechRate).append(", ");
        }
        if (ttsPitch != -1.0F) {
            sb.append("ttsPitch=").append(ttsPitch).append(", ");
        }

        // 마지막에 붙은 ", " 제거
        if (sb.length() > 9) { // "LogEntry{" 길이는 9
            sb.setLength(sb.length() - 2); // 마지막 ", "를 제거
        }

        sb.append("}");
        return sb.toString();
    }

}
