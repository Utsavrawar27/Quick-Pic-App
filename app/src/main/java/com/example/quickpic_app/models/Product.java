package com.example.quickpic_app.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.util.Base64;

public class Product implements Parcelable {
    private String _id;
    private String name;
    private BigDecimal price;
    private int quantity;
    private String barcode;
    private Bitmap image; // This is for displaying the image in the app
    private List<String> images; // Add this field for the list of image strings
    private List<String> tags;

    public Product(String _id, String name, BigDecimal price, int quantity, String barcode, Bitmap image, List<String> images, List<String> tags) {
        this._id = _id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.barcode = barcode;
        // // Convert Bitmap to base64-encoded string
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        this.tags = tags;

        // Initialize the images list and add the base64 image
        this.images = new ArrayList<>();
        this.images.add(imageBase64);
    }

    public Bitmap getImage() {
        // Convert the first base64-encoded string in the images list back to Bitmap
        if (!images.isEmpty()) {
            byte[] decodedBytes = Base64.decode(images.get(0), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }
        return null;
    }

    public String getId(){return _id;}

    public void setId(String _id){this._id = _id;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    protected Product(Parcel in) {
        _id = in.readString();
        name = in.readString();
        price = (BigDecimal) in.readSerializable();
        quantity = in.readInt();
        barcode = in.readString();
        images = in.createStringArrayList();
        tags = in.createStringArrayList();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeSerializable(price);
        dest.writeInt(quantity);
        dest.writeString(barcode);
        dest.writeStringList(images);
        dest.writeStringList(tags);
    }
}

//public class Product {
//    private String name;
//    private double price;
//    private int quantity;
//    private String barcode;
//    private String images; // You can change this to a different data type if needed
//    private String tags;
//
//    public Product(String name, double price, int quantity, String barcode, String images, String tags) {
//        this.name = name;
//        this.price = price;
//        this.quantity = quantity;
//        this.barcode = barcode;
//        this.images = images;
//        this.tags = tags;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public double getPrice() {
//        return price;
//    }
//
//    public void setPrice(double price) {
//        this.price = price;
//    }
//
//    public int getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(int quantity) {
//        this.quantity = quantity;
//    }
//
//    public String getBarcode() {
//        return barcode;
//    }
//
//    public void setBarcode(String barcode) {
//        this.barcode = barcode;
//    }
//
//    public String getImages() {
//        return images;
//    }
//
//    public void setImages(String images) {
//        this.images = images;
//    }
//
//    public String getTags() {
//        return tags;
//    }
//
//    public void setTags(String tags) {
//        this.tags = tags;
//    }
//}
