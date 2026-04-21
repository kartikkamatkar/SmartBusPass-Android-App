package com.smartbus.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.smartbus.app.models.BusPass;
import com.smartbus.app.utils.ValidationHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Senior Developer Implementation: DBHelper
 * Comprehensive database layer with validation, error handling, and data integrity
 */
@SuppressWarnings("unused")
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "smartbus.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_USERS = "users";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";

    public static final String TABLE_PASSES = "passes";
    public static final String COL_PASS_ID = "id";
    public static final String COL_PASS_NAME = "name";
    public static final String COL_PASS_ROUTE = "route";
    public static final String COL_PASS_VALIDITY = "validity";
    public static final String COL_PASS_CREATED_AT = "created_at";

    public static final String TABLE_HISTORY = "history";
    public static final String COL_HISTORY_ID = "id";
    public static final String COL_HISTORY_PASS_ID = "pass_id";
    public static final String COL_HISTORY_TRAVEL_DATE = "travel_date";
    public static final String COL_HISTORY_ROUTE = "route";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_NAME + " TEXT NOT NULL, "
                    + COL_EMAIL + " TEXT UNIQUE NOT NULL, "
                    + COL_PASSWORD + " TEXT NOT NULL)";

            String createPassesTable = "CREATE TABLE " + TABLE_PASSES + " ("
                    + COL_PASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_PASS_NAME + " TEXT NOT NULL, "
                    + COL_PASS_ROUTE + " TEXT NOT NULL, "
                    + COL_PASS_VALIDITY + " TEXT NOT NULL, "
                    + COL_PASS_CREATED_AT + " TEXT NOT NULL)";

            String createHistoryTable = "CREATE TABLE " + TABLE_HISTORY + " ("
                    + COL_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_HISTORY_PASS_ID + " INTEGER NOT NULL, "
                    + COL_HISTORY_TRAVEL_DATE + " TEXT NOT NULL, "
                    + COL_HISTORY_ROUTE + " TEXT NOT NULL)";

            db.execSQL(createUsersTable);
            db.execSQL(createPassesTable);
            db.execSQL(createHistoryTable);
            Log.i(TAG, "Database tables created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating database tables: " + e.getMessage(), e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            // For a dev environment, we simple clear and recreate to ensure schema consistency.
            if (oldVersion < 4) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASSES);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
                onCreate(db);
                Log.i(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database: " + e.getMessage(), e);
        }
    }

    /**
     * Insert user with comprehensive validation
     */
    public boolean insertUser(String name, String email, String password) {
        // Validate input
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Log.w(TAG, "Invalid user data - empty fields");
            return false;
        }

        if (!ValidationHelper.isValidName(name)) {
            Log.w(TAG, "Invalid name format: " + name);
            return false;
        }

        if (!ValidationHelper.isValidEmail(email)) {
            Log.w(TAG, "Invalid email format: " + email);
            return false;
        }

        if (!ValidationHelper.isValidPassword(password)) {
            Log.w(TAG, "Password too weak");
            return false;
        }

        try {
            if (isEmailExists(email)) {
                Log.w(TAG, "Email already exists: " + email);
                return false;
            }

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_NAME, name.trim());
            values.put(COL_EMAIL, email.trim().toLowerCase());
            values.put(COL_PASSWORD, password);

            long result = db.insert(TABLE_USERS, null, values);
            boolean success = result != -1;
            if (success) {
                Log.i(TAG, "User inserted successfully");
            }
            return success;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting user: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check user credentials with safety
     */
    public boolean checkUser(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Log.w(TAG, "Empty credentials provided");
            return false;
        }

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_USERS + " WHERE "
                    + COL_EMAIL + " = ? AND " + COL_PASSWORD + " = ?";

            Cursor cursor = db.rawQuery(query, new String[]{email.trim().toLowerCase(), password});
            boolean isValid = cursor.getCount() > 0;
            cursor.close();
            return isValid;
        } catch (Exception e) {
            Log.e(TAG, "Error checking user: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get user name safely
     */
    public String getUserName(String email) {
        if (TextUtils.isEmpty(email)) {
            return "User";
        }

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT " + COL_NAME + " FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{email.trim().toLowerCase()});
            String name = "User";
            if (cursor.moveToFirst()) {
                name = cursor.getString(0);
            }
            cursor.close();
            return name != null ? name : "User";
        } catch (Exception e) {
            Log.e(TAG, "Error getting user name: " + e.getMessage(), e);
            return "User";
        }
    }

    /**
     * Check if email exists safely
     */
    public boolean isEmailExists(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{email.trim().toLowerCase()});
            boolean exists = cursor.getCount() > 0;
            cursor.close();
            return exists;
        } catch (Exception e) {
            Log.e(TAG, "Error checking email existence: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Insert pass with comprehensive validation
     */
    public long insertPass(String name, String route, String validity, String createdAt) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(route) || TextUtils.isEmpty(validity) || TextUtils.isEmpty(createdAt)) {
            Log.w(TAG, "Invalid pass data - empty fields");
            return -1;
        }

        // Normalize user/input data to avoid false failures during pass activation.
        String normalizedName = name.trim();
        String normalizedRoute = route.trim();

        if (!ValidationHelper.isValidPassName(normalizedName)) {
            Log.w(TAG, "Pass name had unsupported chars, normalizing: " + normalizedName);
            normalizedName = normalizedName.replaceAll("[^\\p{L}0-9\\s\\-().&]", "").trim();
            if (TextUtils.isEmpty(normalizedName)) {
                normalizedName = "Smart Pass";
            }
        }

        if (!ValidationHelper.isValidRoute(normalizedRoute)) {
            Log.w(TAG, "Route format not standard, storing as entered: " + normalizedRoute);
            // Allow if length is at least 2 (very permissive)
            if (normalizedRoute.length() < 2) {
                return -1;
            }
        }

        if (!ValidationHelper.isValidDateFormat(validity)) {
            Log.w(TAG, "Invalid validity date format: " + validity);
            return -1;
        }

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_PASS_NAME, normalizedName);
            values.put(COL_PASS_ROUTE, normalizedRoute);
            values.put(COL_PASS_VALIDITY, validity.trim());
            values.put(COL_PASS_CREATED_AT, createdAt.trim());

            long result = db.insert(TABLE_PASSES, null, values);
            if (result != -1) {
                Log.i(TAG, "Pass inserted successfully with ID: " + result);
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting pass: " + e.getMessage(), e);
            return -1;
        }
    }

    /**
     * Get all passes safely
     */
    public List<BusPass> getAllPasses() {
        List<BusPass> passList = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PASSES + " ORDER BY " + COL_PASS_ID + " DESC", null);
            
            if (cursor.moveToFirst()) {
                do {
                    try {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PASS_ID));
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASS_NAME));
                        String route = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASS_ROUTE));
                        String validity = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASS_VALIDITY));
                        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASS_CREATED_AT));

                        passList.add(new BusPass(id, name != null ? name : "", route != null ? route : "", 
                                validity != null ? validity : "", createdAt != null ? createdAt : ""));
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing pass row: " + e.getMessage(), e);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all passes: " + e.getMessage(), e);
        }
        return passList;
    }

    /**
     * Get pass by ID safely
     */
    public BusPass getPassById(int passId) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PASSES + " WHERE " + COL_PASS_ID + " = ?",
                    new String[]{String.valueOf(passId)});

            BusPass pass = null;
            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASS_NAME));
                String route = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASS_ROUTE));
                String validity = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASS_VALIDITY));
                String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASS_CREATED_AT));
                pass = new BusPass(passId, name != null ? name : "", route != null ? route : "", 
                        validity != null ? validity : "", createdAt != null ? createdAt : "");
            }
            cursor.close();
            return pass;
        } catch (Exception e) {
            Log.e(TAG, "Error getting pass by ID: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get latest pass safely
     */
    public BusPass getLatestPass() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PASSES + " ORDER BY " + COL_PASS_ID + " DESC LIMIT 1", null);
            BusPass pass = null;
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PASS_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASS_NAME));
                String route = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASS_ROUTE));
                String validity = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASS_VALIDITY));
                String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASS_CREATED_AT));
                pass = new BusPass(id, name != null ? name : "", route != null ? route : "", 
                        validity != null ? validity : "", createdAt != null ? createdAt : "");
            }
            cursor.close();
            return pass;
        } catch (Exception e) {
            Log.e(TAG, "Error getting latest pass: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Delete pass safely
     */
    public boolean deletePass(int passId) {
        try {
            if (passId <= 0) {
                Log.w(TAG, "Invalid pass ID for deletion: " + passId);
                return false;
            }

            SQLiteDatabase db = this.getWritableDatabase();
            int deletedRows = db.delete(TABLE_PASSES, COL_PASS_ID + " = ?", new String[]{String.valueOf(passId)});
            
            if (deletedRows > 0) {
                // Also delete associated history
                db.delete(TABLE_HISTORY, COL_HISTORY_PASS_ID + " = ?", new String[]{String.valueOf(passId)});
                Log.i(TAG, "Pass deleted successfully");
            }
            return deletedRows > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting pass: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if pass is expired
     */
    public boolean checkExpiry(String validityDate) {
        if (TextUtils.isEmpty(validityDate)) {
            return true;
        }

        String[] formats = {"dd/MM/yyyy", "yyyy-MM-dd"};
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        Date todayStart = now.getTime();

        for (String f : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(f, Locale.US);
                Date validity = sdf.parse(validityDate);
                if (validity != null) {
                    return todayStart.after(validity);
                }
            } catch (ParseException e) {
                Log.d(TAG, "Date parsing failed for format " + f + ": " + e.getMessage());
            }
        }
        return true;
    }

    /**
     * Insert travel history safely
     */
    public long insertTravelHistory(int passId, String travelDate, String route) {
        if (passId <= 0 || TextUtils.isEmpty(travelDate) || TextUtils.isEmpty(route)) {
            Log.w(TAG, "Invalid history data");
            return -1;
        }

        if (!ValidationHelper.isValidDateFormat(travelDate)) {
            Log.w(TAG, "Invalid travel date format: " + travelDate);
            return -1;
        }

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_HISTORY_PASS_ID, passId);
            values.put(COL_HISTORY_TRAVEL_DATE, travelDate.trim());
            values.put(COL_HISTORY_ROUTE, route.trim());
            
            long result = db.insert(TABLE_HISTORY, null, values);
            if (result != -1) {
                Log.i(TAG, "Travel history inserted successfully");
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting travel history: " + e.getMessage(), e);
            return -1;
        }
    }
}
