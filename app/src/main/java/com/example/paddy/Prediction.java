package com.example.paddy;

//Database
public class Prediction {
    private int id;
    private String image;
    private String prediction1;
    private String prediction2;
    private String prediction3;
    private String finalPrediction;
    private String dateTime;

    public Prediction(String image, String prediction1, String prediction2, String prediction3, String finalPrediction, String dateTime) {
        this.image = image;
        this.prediction1 = prediction1;
        this.prediction2 = prediction2;
        this.prediction3 = prediction3;
        this.finalPrediction = finalPrediction;
        this.dateTime = dateTime;
    }
    public Prediction(){

    }

    public String getImage() {
        return image;
    }

    public String getPrediction1() {
        return prediction1;
    }

    public String getPrediction2() {
        return prediction2;
    }

    public String getPrediction3() {
        return prediction3;
    }

    public String getFinalPrediction() {
        return finalPrediction;
    }

    public String getDateTime() {
        return dateTime;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setImageBase64(String imageBase64) {
        this.image = imageBase64;
    }
    public void setPredictedResult1(String predictedResult1) {
        this.prediction1 = predictedResult1;
    }
    public void setPredictedResult2(String predictedResult2) {
        this.prediction2 = predictedResult2;
    }
    public void setPredictedResult3(String predictedResult3) {
        this.prediction3 = predictedResult3;
    }
    public void setFinalPrediction(String finalPrediction) {
        this.finalPrediction = finalPrediction;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
