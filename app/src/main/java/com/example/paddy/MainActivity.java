package com.example.paddy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.paddy.ml.MyModel1611;
import com.example.paddy.ml.MyModel2611;
import com.example.paddy.ml.MyModel3611;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button selectBtn, predictBtn, historyBtn, measuresBtn;
    private TextView result, dateTimeView, result1, result2, result3;
    private ImageView img, logoImg;
    private Bitmap bitmap;
    private Map<String, String> leaderModels;
    private List<String> predictions;
    private List<Double> confidences;
    private final String url="https://api.openweathermap.org/data/2.5/forecast";
    private final String appId="51441a4263c66c074264c00aeba6e444";
    DecimalFormat df=new DecimalFormat("#.##");

    //Database
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check for runtime permission and request it if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        selectBtn = findViewById(R.id.selectBtn);
        predictBtn = findViewById(R.id.predictBtn);
        historyBtn = findViewById(R.id.historyBtn);
        measuresBtn = findViewById(R.id.measuresBtn);
        result=findViewById(R.id.result);
        result1=findViewById(R.id.model1Result);
        result2=findViewById(R.id.model2Result);
        result3=findViewById(R.id.model3Result);
        img = findViewById(R.id.imageView);
        logoImg=findViewById(R.id.logoImg);

        confidences=new ArrayList<>();
        predictions=new ArrayList<>();
        leaderModels = new HashMap<>();
        leaderModels.put("bacterial_leaf_blight", "3");
        leaderModels.put("bacterial_leaf_streak", "1");
        leaderModels.put("bacterial_panicle_blight", "2");
        leaderModels.put("blast", "3");
        leaderModels.put("brown_spot", "3");
        leaderModels.put("dead_heart", "3");
        leaderModels.put("downy_mildew", "3");
        leaderModels.put("hispa", "3");
        leaderModels.put("normal", "3");
        leaderModels.put("tungro", "3");
        leaderModels.put("accuracy", "3");
        leaderModels.put("macro avg", "3");
        leaderModels.put("weighted avg", "3");

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

//        measuresBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(getApplicationContext(), MeasuresActivity.class);
//                startActivity(intent);
//            }
//        });

        predictBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MyModel1611 model1 = MyModel1611.newInstance(MainActivity.this);
                    MyModel2611 model2 = MyModel2611.newInstance(MainActivity.this);
                    MyModel3611 model3=MyModel3611.newInstance(MainActivity.this);

                    predictions.clear();
                    confidences.clear();
                    // Resize the input image to match the expected input shape
                    bitmap = Bitmap.createScaledBitmap(bitmap, 112, 112, true);

                    // Prepare the input buffer directly from the model's input shape
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 112, 112, 3}, DataType.FLOAT32);

                    // Convert the input image (bitmap) to a float array and load it into the input buffer
                    float[] inputData = bitmapToFloatArray(bitmap);
                    inputFeature0.loadArray(inputData);

                    // Run model inference and get the result

                    MyModel1611.Outputs outputs1=model1.process(inputFeature0);
                    TensorBuffer outputFeature1=outputs1.getOutputFeature0AsTensorBuffer();

                    MyModel2611.Outputs outputs2=model2.process(inputFeature0);
                    TensorBuffer outputFeature2=outputs2.getOutputFeature0AsTensorBuffer();

                    MyModel3611.Outputs outputs3=model3.process(inputFeature0);
                    TensorBuffer outputFeature3=outputs3.getOutputFeature0AsTensorBuffer();

                    // Convert the output to a float array
                    float[] outputArray1=outputFeature1.getFloatArray();
                    float[] outputArray2=outputFeature2.getFloatArray();
                    float[] outputArray3=outputFeature3.getFloatArray();

                    // Get the predicted class index
                    int predictedClassIndex1=getMax(outputArray1);
                    int predictedClassIndex2=getMax(outputArray2);
                    int predictedClassIndex3=getMax(outputArray3);

                    //confidence score
                    float confidence1=outputArray1[predictedClassIndex1];
                    float confidence2=outputArray2[predictedClassIndex2];
                    float confidence3=outputArray3[predictedClassIndex3];
                    confidences.add((double) confidence1);
                    confidences.add((double) confidence2);
                    confidences.add((double) confidence3);
                    // Define class labels
                    String[] classLabels = {
                            "bacterial_leaf_blight", "bacterial_leaf_streak", "bacterial_panicle_blight",
                            "blast", "brown_spot", "dead_heart", "downy_mildew", "hispa", "normal", "tungro"
                    };
                    String predictedResult1=classLabels[predictedClassIndex1];
                    String predictedResult2=classLabels[predictedClassIndex2];
                    String predictedResult3=classLabels[predictedClassIndex3];

                    // Check if the predictedClassIndex is valid
                    if(predictedClassIndex1>=0 && predictedClassIndex1<classLabels.length){
                        predictedResult1=classLabels[predictedClassIndex1];
                        predictions.add(predictedResult1);
                        result1.setText("Model1 Prediction: "+predictedResult1+" : "+ confidence1);
                    }else{
                        result1.setText("Invalid prediction result");
                    }
                    if(predictedClassIndex2>=0 && predictedClassIndex2<classLabels.length){
                        predictedResult2=classLabels[predictedClassIndex2];
                        predictions.add(predictedResult2);
                        result2.setText("Model2 Prediction: "+predictedResult2+" : "+ confidence2);
                    }else{
                        result2.setText("Invalid prediction result");
                    }
                    if(predictedClassIndex3>=0 && predictedClassIndex3<classLabels.length){
                        predictedResult3=classLabels[predictedClassIndex3];
                        predictions.add(predictedResult3);
                        result3.setText("Model3 Prediction: "+predictedResult3+" : "+ confidence3);
                    }else{
                        result3.setText("Invalid prediction result");
                    }

                    // Releases model resources
                    model1.close();
                    model2.close();
                    model3.close();
                    String finalPrediction = leaderClassConfidenceEnsemble(predictions, confidences, leaderModels);
                    String dateTime = getCurrentDateTime();
                    result.setText("Final Prediction: " + finalPrediction);
                    savePredictionToDatabase(bitmap, predictedResult1, predictedResult2, predictedResult3, finalPrediction, dateTime);
                } catch (IOException e) {
                    // Handle exceptions related to model loading
                    e.printStackTrace();
                    // Display an error message to the user
                    result.setText("Error loading model: " + e.getMessage());
                } catch (Exception e) {
                    // Handle other exceptions that may occur during inference
                    e.printStackTrace();
                    // Display an error message to the user
                    result.setText("An error occurred: " + e.getMessage());
                }
            }
        });
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(intent);
            }
        });
        //Database code
        dbHelper=new DatabaseHelper(this);
    }

    //code to save prediction to database
    // Add the following method to save the prediction in the database
    private void savePredictionToDatabase(Bitmap imageBitmap, String predictedResult1, String predictedResult2, String predictedResult3, String finalPrediction, String dateTime) {
        // Convert the bitmap to a byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // Convert the byte array to a Base64 encoded string (you may use another method if you prefer)
        String imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        // Create a Prediction object
        Prediction prediction = new Prediction(imageBase64, predictedResult1, predictedResult2, predictedResult3, finalPrediction, dateTime);

        // Add the prediction to the database
        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
        dbHelper.addPrediction(prediction);

        // Optionally, display a message to the user
        Toast.makeText(MainActivity.this, "Prediction saved to database", Toast.LENGTH_SHORT).show();
    }

    //function to get date and time
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    //code for game theory
    private String leaderClassConfidenceEnsemble(List<String> predictions, List<Double> confidences, Map<String, String> leaderModels) {
        if (predictions.size() == 1) {
            return predictions.get(0);
        } else if (predictions.size() == 2) {
            for (int i = 0; i < predictions.size(); i++) {
                Log.d("Prediction", "Predicted_class: " + predictions.get(i) + " | Leader model of predicted class: " + leaderModels.get(predictions.get(i)) + "::" + predictions.get(Integer.parseInt(leaderModels.get(predictions.get(i))) - 1));
            }
            int commClass = findMostCommonClass(predictions);
            return predictions.get(Integer.parseInt(leaderModels.get(String.valueOf(commClass))) - 1);
        } else if (predictions.size() == 3) {
            int counter = 0;
            int idx = -1;
            for (int i = 0; i < predictions.size(); i++) {
                Log.d("Prediction", "Predicted_class: " + predictions.get(i) + " | Leader model of predicted class: " + leaderModels.get(predictions.get(i)) + "::" + predictions.get(Integer.parseInt(leaderModels.get(predictions.get(i))) - 1));
                if (predictions.get(i).equals(predictions.get(Integer.parseInt(leaderModels.get(predictions.get(i))) - 1))) {
                    counter++;
                    idx = i;
                }
            }
            if (counter == 1) {
                return predictions.get(idx);
            } else {
                int outIdx = findIndexOfMax(confidences);
                return predictions.get(outIdx);
            }
        }
        return "Unknown";
    }

    private int findMostCommonClass(List<String> predictions) {
        Map<String, Integer> classCount = new HashMap<>();
        for (String prediction : predictions) {
            classCount.put(prediction, classCount.getOrDefault(prediction, 0) + 1);
        }

        int mostCommonClass = -1;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : classCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostCommonClass = Integer.parseInt(entry.getKey());
                maxCount = entry.getValue();
            }
        }
        return mostCommonClass;
    }

    private int findIndexOfMax(List<Double> values) {
        int maxIndex = 0;
        double maxValue = Double.MIN_VALUE;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) > maxValue) {
                maxIndex = i;
                maxValue = values.get(i);
            }
        }
        return maxIndex;
    }
    // Helper method to convert a Bitmap to a float array
    private float[] bitmapToFloatArray(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int channelSize = 3; // Assuming it's an RGB image

        float[] floatValues = new float[width * height * channelSize];

        int[] intValues = new int[width * height];
        bitmap.getPixels(intValues, 0, width, 0, 0, width, height);

        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            floatValues[i * 3 + 0] = ((val >> 16) & 0xFF) / 255.0f;
            floatValues[i * 3 + 1] = ((val >> 8) & 0xFF) / 255.0f;
            floatValues[i * 3 + 2] = (val & 0xFF) / 255.0f;
        }
        return floatValues;
    }

    // Helper method to get the index of the maximum value in an array
    private int getMax(float[] arr) {
        int max = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > arr[max]) max = i;
        }
        return max;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // compare the resultCode with the SELECT_PICTURE constant
            if (requestCode == 10) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    try {
                        // Convert the Uri to a Bitmap
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                        // Update the preview image in the layout
                        img.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Handle the exception (e.g., display an error message)
                        Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}

