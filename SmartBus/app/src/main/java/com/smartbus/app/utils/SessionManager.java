package com.smartbus.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "smartbus_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_NOTIF_ENABLED = "notif_enabled";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_LANGUAGE = "app_language";

    private final SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
    }

    public void setUserName(String name) {
        sharedPreferences.edit().putString(KEY_USER_NAME, name).apply();
    }

    public void setUserEmail(String email) {
        sharedPreferences.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    public void setNotificationsEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_NOTIF_ENABLED, enabled).apply();
    }

    public boolean isNotificationsEnabled() {
        return sharedPreferences.getBoolean(KEY_NOTIF_ENABLED, true);
    }

    public void setDarkModeEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    public boolean isDarkModeEnabled() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "User");
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "user@email.com");
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setLanguage(String languageCode) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, languageCode).apply();
    }

    public String getLanguage() {
        return sharedPreferences.getString(KEY_LANGUAGE, "en");
    }

    public void logout() {
        sharedPreferences.edit().clear().apply();
    }
}

