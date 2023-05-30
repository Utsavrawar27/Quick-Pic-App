package com.example.quickpic_app.models;

import java.util.List;

public class BarcodeDetectionResponse {
    private boolean success;
    private List<Product> products;

    public BarcodeDetectionResponse(boolean success, List<Product> products) {
        this.success = success;
        this.products = products;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
