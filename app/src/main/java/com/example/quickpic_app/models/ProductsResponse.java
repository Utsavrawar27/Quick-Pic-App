package com.example.quickpic_app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<Product> products;

    public ProductsResponse(boolean success, List<Product> products) {
        this.success = success;
        this.products = products;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
