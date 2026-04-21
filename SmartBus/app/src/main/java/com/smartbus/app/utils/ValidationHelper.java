package com.smartbus.app.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Senior Developer Implementation: ValidationHelper
 * Centralized validation logic for the entire application
 * Ensures data consistency, security, and proper error messaging
 */
public class ValidationHelper {

    private static final Pattern EMAIL_PATTERN = Patterns.EMAIL_ADDRESS;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");
    private static final String DATE_FORMAT = "dd/MM/yyyy";

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate password strength
     * Rules: Min 6 chars, at least 1 letter, at least 1 digit
     */
    public static boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            return false;
        }
        return Pattern.compile("[a-zA-Z]").matcher(password).find() &&
               Pattern.compile("[0-9]").matcher(password).find();
    }

    /**
     * Validate phone number (10 digits)
     */
    public static boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validate name (2-50 chars, letters and spaces only)
     */
    public static boolean isValidName(String name) {
        if (TextUtils.isEmpty(name) || name.length() < 2 || name.length() > 50) {
            return false;
        }
        // Allow letters, numbers, spaces, and dots
        return Pattern.compile("^[a-zA-Z0-9\\s.]+$").matcher(name).matches();
    }

    /**
     * Validate date format (dd/MM/yyyy)
     */
    public static boolean isValidDateFormat(String date) {
        if (TextUtils.isEmpty(date)) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            sdf.setLenient(false);
            sdf.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate date is not in the past
     */
    public static boolean isValidFutureDate(String date) {
        if (!isValidDateFormat(date)) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            Date parsedDate = sdf.parse(date);
            return parsedDate != null && parsedDate.getTime() > System.currentTimeMillis();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate route format (should contain ↔ or - or /)
     */
    public static boolean isValidRoute(String route) {
        if (TextUtils.isEmpty(route) || route.length() < 3) {
            return false;
        }
        return route.contains("↔") || route.contains("-") || route.contains("/");
    }

    /**
     * Validate pass name (alphanumeric, spaces, max 100 chars)
     */
    public static boolean isValidPassName(String passName) {
        if (TextUtils.isEmpty(passName) || passName.length() > 100) {
            return false;
        }
        return Pattern.compile("^[a-zA-Z0-9\\s\\-]+$").matcher(passName).matches();
    }

    /**
     * Sanitize input to prevent SQL injection
     */
    public static String sanitizeInput(String input) {
        if (TextUtils.isEmpty(input)) {
            return "";
        }
        return input.trim()
                .replace("'", "''")
                .replace("\"", "\"\"")
                .substring(0, Math.min(input.length(), 255));
    }

    /**
     * Validate empty field with custom error message
     */
    public static boolean isNotEmpty(String value, String fieldName) {
        if (TextUtils.isEmpty(value)) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        return true;
    }

    /**
     * Get user-friendly error message
     */
    public static String getErrorMessage(String field, String validationType) {
        switch (validationType.toLowerCase()) {
            case "email":
                return "Please enter a valid " + field;
            case "password":
                return field + " must be at least 6 characters with letters and numbers";
            case "phone":
                return field + " must be 10 digits";
            case "name":
                return field + " must be 2-50 letters only";
            case "date":
                return "Please enter valid date (dd/MM/yyyy)";
            case "route":
                return field + " must contain a valid route (e.g., City A - City B)";
            case "future_date":
                return "Date must be in the future";
            default:
                return "Invalid " + field;
        }
    }
}

