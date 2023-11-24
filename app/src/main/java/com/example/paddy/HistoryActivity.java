package com.example.paddy;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        historyListView = findViewById(R.id.historyListView); // Use your actual ListView ID
        dbHelper = new DatabaseHelper(this);
        List<Prediction> predictions = dbHelper.getAllPredictions();
        PredictionAdapter adapter = new PredictionAdapter(this, R.layout.list_item_prediction, predictions);

        // Set the adapter to the ListView
        historyListView.setAdapter(adapter);
    }
}