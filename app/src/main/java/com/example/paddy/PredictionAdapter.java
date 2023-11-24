package com.example.paddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PredictionAdapter extends ArrayAdapter<Prediction> {

    private int resourceLayout;

    public PredictionAdapter(Context context, int resource, List<Prediction> predictions) {
        super(context, resource, predictions);
        this.resourceLayout = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(resourceLayout, null);
        }

        Prediction prediction = getItem(position);

        if (prediction != null) {
            TextView textViewPrediction1 = v.findViewById(R.id.textViewPrediction1);
            TextView textViewPrediction2 = v.findViewById(R.id.textViewPrediction2);
            TextView textViewPrediction3 = v.findViewById(R.id.textViewPrediction3);
            TextView textViewFinalPrediction = v.findViewById(R.id.textViewFinalPrediction);
            TextView textViewDateTime = v.findViewById(R.id.textViewDateTime);

            // Set values to TextViews
            textViewPrediction1.setText("Prediction 1: " + prediction.getPrediction1());
            textViewPrediction2.setText("Prediction 2: " + prediction.getPrediction2());
            textViewPrediction3.setText("Prediction 3: " + prediction.getPrediction3());
            textViewFinalPrediction.setText("Final Prediction: " + prediction.getFinalPrediction());
            textViewDateTime.setText("Date and Time: " + prediction.getDateTime());
        }

        return v;
    }
}