package com.google.android.libraries.accessibility.utils.log;

import java.util.Date;

public class LogEntry {
    //todo : Add Constants, Constructor
    private static int                  scrollDirection;
    private static EventId              eventId;
    private static boolean              isScreenOn;
    private static Accessibility        accessibility;
    private static boolean              isWindowChange;
    private static NodeInfo             currentNodeInfo;
    private static List<ViewNodeInfo>   viewNodeInfo;
    private static int                  gesture;
    private static int                  action;
    private static Fragment             ttsSettingStatus;

    public static void setScrollDirection(int scrollDirection) {
        LogEntry.scrollDirection = scrollDirection;
    }

    public static void setEventId(EventId eventId) {
        LogEntry.eventId = eventId;
    }

    public static void setIsScreenOn(boolean isScreenOn) {
        LogEntry.isScreenOn = isScreenOn;
    }

    public static void setAccessibility(Accessibility accessibility) {
        LogEntry.accessibility = accessibility;
    }

    public static void setIsWindowChange(boolean isWindowChange) {
        LogEntry.isWindowChange = isWindowChange;
    }

    public static void setCurrentNodeInfo(NodeInfo currentNodeInfo) {
        LogEntry.currentNodeInfo = currentNodeInfo;
    }

    public static void setViewNodeInfo(List<ViewNodeInfo> viewNodeInfo) {
        LogEntry.viewNodeInfo = viewNodeInfo;
    }

    public static void setGesture(int gesture) {
        LogEntry.gesture = gesture;
    }

    public static void setAction(int action) {
        LogEntry.action = action;
    }

    public static void setTtsSettingStatus(Fragment ttsSettingStatus) {
        LogEntry.ttsSettingStatus = ttsSettingStatus;
    }

    public LogEntry() {
        //todo
    }

    public static SaveToDB(){

    }
}
