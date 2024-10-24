package com.google.android.accessibility.talkback.preference;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

public class PrefrenceLogger {

    /** SharedPreferences에서 해당 키의 값을 가져오는 메서드 */
    private static Object getPreferenceValue(SharedPreferences sharedPreferences, String key) {
        Map<String, ?> allPrefs = sharedPreferences.getAll();
        return allPrefs.containsKey(key) ? allPrefs.get(key) : "Not set";
    }
    public static String Log(SharedPreferences sharedPreferences, String key){
        //noti: 로깅: 변경된 키와 새로운 값을 출력
        Object newValue = getPreferenceValue(sharedPreferences, key);
        return "Preference changed: Key = " + key + ", New Value = " + newValue;
    }
    public static String Log(String key,Object value){
        //noti: 로깅: 변경된 키와 새로운 값을 출력
        return "Preference changed: Key = " + key + ", New Value = " + value;
    }
}
