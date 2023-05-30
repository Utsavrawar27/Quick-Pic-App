package com.example.quickpic_app.models;

public class BarcodeDetectionRequest {
    private String imageBase64;

    public BarcodeDetectionRequest(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
