package com.example.paddy;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database name and version
    private static final String DATABASE_NAME = "PredictionHistory.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PREDICTIONS = "predictions";

    // Table name
    private static final String TABLE_PREDICTION_HISTORY = "prediction_history";

    // Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_PREDICTION_1 = "prediction_1";
    private static final String COLUMN_PREDICTION_2 = "prediction_2";
    private static final String COLUMN_PREDICTION_3 = "prediction_3";
    private static final String COLUMN_FINAL_PREDICTION = "final_prediction";
    private static final String COLUMN_DATE_TIME = "date_time";

    // Create table statement
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_PREDICTION_HISTORY + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_IMAGE + " TEXT,"
                    + COLUMN_PREDICTION_1 + " TEXT,"
                    + COLUMN_PREDICTION_2 + " TEXT,"
                    + COLUMN_PREDICTION_3 + " TEXT,"
                    + COLUMN_FINAL_PREDICTION + " TEXT,"
                    + COLUMN_DATE_TIME + " TEXT"
                    + ")";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    // Upgrade database (if needed)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREDICTION_HISTORY);

        // Create tables again
        onCreate(db);
    }

    // Add a prediction to the database
    public void addPrediction(Prediction prediction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_IMAGE, prediction.getImage());
        values.put(COLUMN_PREDICTION_1, prediction.getPrediction1());
        values.put(COLUMN_PREDICTION_2, prediction.getPrediction2());
        values.put(COLUMN_PREDICTION_3, prediction.getPrediction3());
        values.put(COLUMN_FINAL_PREDICTION, prediction.getFinalPrediction());
        values.put(COLUMN_DATE_TIME, prediction.getDateTime());

        // Insert row
        db.insert(TABLE_PREDICTION_HISTORY, null, values);
        db.close();
    }
    public List<Prediction> getAllPredictions() {
        List<Prediction> predictionList = new ArrayList<>();

        // Select all predictions query
        String selectQuery = "SELECT * FROM " + TABLE_PREDICTION_HISTORY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                Prediction prediction = new Prediction();
                prediction.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                prediction.setImageBase64(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
                prediction.setPredictedResult1(cursor.getString(cursor.getColumnIndex(COLUMN_PREDICTION_1)));
                prediction.setPredictedResult2(cursor.getString(cursor.getColumnIndex(COLUMN_PREDICTION_2)));
                prediction.setPredictedResult3(cursor.getString(cursor.getColumnIndex(COLUMN_PREDICTION_3)));
                prediction.setFinalPrediction(cursor.getString(cursor.getColumnIndex(COLUMN_FINAL_PREDICTION)));
                prediction.setDateTime(cursor.getString(cursor.getColumnIndex(COLUMN_DATE_TIME)));

                predictionList.add(prediction);
            } while (cursor.moveToNext());
        }

        // Close the cursor and return the prediction list
        cursor.close();
        return predictionList;
    }
}
