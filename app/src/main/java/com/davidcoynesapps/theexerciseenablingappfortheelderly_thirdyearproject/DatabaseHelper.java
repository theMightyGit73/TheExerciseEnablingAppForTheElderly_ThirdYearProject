package com.davidcoynesapps.theexerciseenablingappfortheelderly_thirdyearproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "users.db";
    private static final String TABLE_NAME = "users";

    // column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_BIRTHDATE = "birthdate";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_HEIGHT = "height";
    private static final String COLUMN_WEIGHT_UNIT = "weight_unit";
    private static final String COLUMN_HEIGHT_UNIT = "height_unit";
    private static final String COLUMN_CALORIES_BURNT = "calories_burnt";
    private static final String COLUMN_DISTANCE = "distance";
    private static final String COLUMN_STEPS = "steps";
    private static final String COLUMN_EXERCISE = "exercise";
    private static final String COLUMN_EXERCISE_DURATION = "exercise_duration";

    private static final String TAG = "DatabaseHelper";

    private SQLiteDatabase db;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_BIRTHDATE + " TEXT, " +
                COLUMN_GENDER + " TEXT, " +
                COLUMN_WEIGHT + " REAL, " +
                COLUMN_WEIGHT_UNIT + " TEXT, " +
                COLUMN_HEIGHT + " REAL, " +
                COLUMN_HEIGHT_UNIT + " TEXT, " +
                COLUMN_CALORIES_BURNT + " REAL, " +
                COLUMN_DISTANCE + " REAL, " +
                COLUMN_STEPS + " INTEGER, " +
                COLUMN_EXERCISE + " TEXT, " +
                COLUMN_EXERCISE_DURATION + " INTEGER)";
        db.execSQL(createTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addUser(User user) {
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_BIRTHDATE, user.getBirthdate());
        values.put(COLUMN_GENDER, user.getGender());
        values.put(COLUMN_WEIGHT, user.getWeight());
        values.put(COLUMN_WEIGHT_UNIT, user.getWeightUnits());
        values.put(COLUMN_HEIGHT, user.getHeight());
        values.put(COLUMN_HEIGHT_UNIT, user.getHeightUnits());
        values.put(COLUMN_CALORIES_BURNT, user.getCaloriesBurnt());
        values.put(COLUMN_DISTANCE, user.getDistance());
        values.put(COLUMN_STEPS, user.getSteps());
        values.put(COLUMN_EXERCISE, user.getExercise());
        values.put(COLUMN_EXERCISE_DURATION, user.getExerciseDuration());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public boolean checkUserExists(String username, String password) {
        db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + "=? AND " + COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        boolean userExists = cursor.getCount() > 0;

        cursor.close();
        db.close();
        return userExists;
    }

    public User getLoggedInUser() throws IllegalStateException, SQLiteException {
        User user = null;

        // Check if a valid Context was provided to the DatabaseHelper constructor
        if (context == null) {
            throw new IllegalStateException("Cannot retrieve logged-in user without a valid Context");
        }

        // Get the ID of the logged-in user from the app's state
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int loggedInUserId = prefs.getInt("loggedInUserId", -1);

        // If no user is currently logged in, return null
        if (loggedInUserId < 0) {
            return null;
        }

        // Query the database to retrieve the user with the specified ID
    SQLiteDatabase db = null;
    Cursor cursor = null;
        try {
        db = this.getReadableDatabase();
        cursor = db.query(TABLE_NAME, null, COLUMN_ID + "=?", new String[]{String.valueOf(loggedInUserId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            int columnIndex;
            columnIndex = cursor.getColumnIndex(COLUMN_ID);
            if (columnIndex >= 0) {
                user.setId(cursor.getInt(columnIndex));
            } else {
                Log.e(TAG, "Column not found: " + COLUMN_ID);
            }
            columnIndex = cursor.getColumnIndex(COLUMN_NAME);
            if (columnIndex >= 0) {
                user.setName(cursor.getString(columnIndex));
            } else {
                Log.e(TAG, "Column not found: " + COLUMN_NAME);
            }
            columnIndex = cursor.getColumnIndex(COLUMN_EMAIL);
            if (columnIndex >= 0) {
                user.setEmail(cursor.getString(columnIndex));
            } else {
                Log.e(TAG, "Column not found: " + COLUMN_EMAIL);
            }
            columnIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
            if (columnIndex >= 0) {
                user.setPassword(cursor.getString(columnIndex));
            } else {
                Log.e(TAG, "Column not found: " + COLUMN_PASSWORD);
            }
            columnIndex = cursor.getColumnIndex(COLUMN_BIRTHDATE);
            if (columnIndex >= 0) {
                user.setBirthdate(cursor.getString(columnIndex));
            } else {
                Log.e(TAG, "Column not found: " + COLUMN_BIRTHDATE);
            }
            columnIndex = cursor.getColumnIndex(COLUMN_GENDER);
            if (columnIndex >= 0) {
                user.setGender(cursor.getString(columnIndex));
            } else {
                Log.e(TAG, "Column not found: " + COLUMN_GENDER);
            }
            columnIndex = cursor.getColumnIndex(COLUMN_WEIGHT);
            if (columnIndex >= 0) {
                user.setWeight(cursor.getFloat(columnIndex));
            } else {
                Log.e(TAG, "Column not found: " + COLUMN_WEIGHT);
            }
            columnIndex = cursor.getColumnIndex(COLUMN_WEIGHT_UNIT);
            if (columnIndex >= 0) {
                user.setWeightUnits(cursor.getString(columnIndex));
            } else {
                Log.e(TAG, "Column not found: " + COLUMN_WEIGHT_UNIT);
            }
            columnIndex = cursor.getColumnIndex(COLUMN_HEIGHT);
            if (columnIndex >= 0) {
                user.setHeight(cursor.getFloat(columnIndex));
            } else {
                Log.e(TAG, "Column not found: " + COLUMN_HEIGHT);
            }
            columnIndex = cursor.getColumnIndex(COLUMN_HEIGHT_UNIT);
            if (columnIndex >= 0) {
                user.setHeightUnits(cursor.getString(columnIndex));
            } else {
                Log.e(TAG, "Column not found: " + COLUMN_HEIGHT_UNIT);
            }
            columnIndex = cursor.getColumnIndex(COLUMN_CALORIES_BURNT);
            if (columnIndex >= 0) {
                user.setCaloriesBurnt(cursor.getFloat(columnIndex));
            } else {
                Log.e(TAG, "Column not found: " + COLUMN_CALORIES_BURNT);
            }
            columnIndex = cursor.getColumnIndex(COLUMN_DISTANCE);
            if (columnIndex >= 0) {
                user.setDistance(cursor.getFloat(columnIndex));
            } else {
                Log.e(TAG, "Column not found: " + COLUMN_DISTANCE);
            }
            columnIndex = cursor.getColumnIndex(COLUMN_STEPS);
            if (columnIndex >= 0) {
                user.setSteps(cursor.getInt(columnIndex));
            } else {
                Log.e(TAG, "Column not found: " + COLUMN_STEPS);
            }
            columnIndex = cursor.getColumnIndex(COLUMN_EXERCISE);
            if (columnIndex >= 0) {
                user.setExercise(cursor.getString(columnIndex));
            } else {
                Log.e(TAG, "Column not found: " + COLUMN_EXERCISE);
            }
            columnIndex = cursor.getColumnIndex(COLUMN_EXERCISE_DURATION);
            if (columnIndex >= 0) {
                user.setExerciseDuration(cursor.getInt(columnIndex));
            } else {
                Log.e(TAG, "Column not found: " + COLUMN_EXERCISE_DURATION);
            }
        }
    } catch (SQLiteException e) {
        Log.e(TAG, "Error retrieving logged-in user from database", e);
        throw e;
    } finally {
        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
    }
        return user;
}



    public void updateUser(User user) {
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_BIRTHDATE, user.getBirthdate());
        values.put(COLUMN_GENDER, user.getGender());
        values.put(COLUMN_WEIGHT, user.getWeight());
        values.put(COLUMN_WEIGHT_UNIT, user.getWeightUnits());
        values.put(COLUMN_HEIGHT, user.getHeight());
        values.put(COLUMN_HEIGHT_UNIT, user.getHeightUnits());
        values.put(COLUMN_CALORIES_BURNT, user.getCaloriesBurnt());
        values.put(COLUMN_DISTANCE, user.getDistance());
        values.put(COLUMN_STEPS, user.getSteps());
        values.put(COLUMN_EXERCISE, user.getExercise());
        values.put(COLUMN_EXERCISE_DURATION, user.getExerciseDuration());

        db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(user.getId())});
        db.close();
    }

}
