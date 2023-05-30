package com.example.quickpic_app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Product product;

    // Constructor
    public ProductResponse(boolean success, String message, Product product) {
        this.success = success;
        this.message = message;
        this.product = product;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Product getProduct() {
        return product;
    }

    // Setters
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
