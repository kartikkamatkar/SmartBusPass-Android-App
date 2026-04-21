package com.smartbus.app.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

/**
 * Senior Developer Implementation: ErrorHandler
 * Centralized error handling and user feedback mechanism
 */
public class ErrorHandler {

    private static final String TAG = "ErrorHandler";

    /**
     * Show simple toast error message
     */
    public static void showError(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            Log.e(TAG, message);
        }
    }

    /**
     * Show error with longer duration
     */
    public static void showLongError(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            Log.e(TAG, message);
        }
    }

    /**
     * Show alert dialog with error details
     */
    public static void showErrorDialog(Activity activity, String title, String message) {
        if (activity != null && !activity.isFinishing()) {
            new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
            Log.e(TAG, title + ": " + message);
        }
    }

    /**
     * Show error dialog with custom action
     */
    public static void showErrorDialog(Activity activity, String title, String message,
                                      String positiveText, Runnable positiveAction) {
        if (activity != null && !activity.isFinishing()) {
            new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positiveText, (dialog, which) -> {
                        dialog.dismiss();
                        if (positiveAction != null) {
                            positiveAction.run();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
            Log.e(TAG, title + ": " + message);
        }
    }

    /**
     * Log exception with context
     */
    public static void logException(String tag, String message, Exception e) {
        Log.e(tag, message, e);
    }

    /**
     * Handle database errors with user-friendly message
     */
    public static String handleDatabaseError(Exception e) {
        if (e == null) {
            return "Database operation failed";
        }

        String message = e.getMessage();
        if (message == null) {
            message = e.getClass().getSimpleName();
        }

        if (message.contains("UNIQUE constraint")) {
            return "This entry already exists";
        } else if (message.contains("NOT NULL")) {
            return "Please fill all required fields";
        } else if (message.contains("no such table")) {
            return "Database initialization error";
        } else if (message.contains("disk")) {
            return "Storage error. Please check device storage";
        } else {
            return "Database error: " + message;
        }
    }

    /**
     * Handle network errors
     */
    public static String handleNetworkError(Exception e) {
        if (e == null) {
            return "Network error occurred";
        }

        String message = e.getMessage();
        if (message == null) {
            return "Unable to connect to server";
        }

        if (message.contains("Network") || message.contains("connection")) {
            return "Check your internet connection";
        } else if (message.contains("timeout")) {
            return "Request timeout. Please try again";
        } else if (message.contains("404")) {
            return "Resource not found";
        } else if (message.contains("500")) {
            return "Server error. Please try again later";
        } else {
            return "Network error: " + message;
        }
    }

    /**
     * Safe cast with error handling
     */
    public static <T> T safeCast(Object obj, Class<T> type) {
        try {
            if (type.isInstance(obj)) {
                return type.cast(obj);
            }
        } catch (ClassCastException e) {
            Log.e(TAG, "Cast error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Safe null check with fallback
     */
    public static <T> T safeGet(T value, T fallback) {
        return value != null ? value : fallback;
    }

    /**
     * Debug log
     */
    public static void debugLog(String tag, String message) {
        Log.d(tag, message);
    }

    /**
     * Info log
     */
    public static void info(String tag, String message) {
        Log.i(tag, message);
    }

    /**
     * Warning log
     */
    public static void warning(String tag, String message) {
        Log.w(tag, message);
    }
}

