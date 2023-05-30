package com.example.quickpic_app;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickpic_app.API.ApiService;
import com.example.quickpic_app.Utils.SharedPreferencesUtil;
import com.example.quickpic_app.models.Product;
import com.example.quickpic_app.models.ProductResponse;
import com.example.quickpic_app.models.ProductsResponse;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.quickpic_app.API.RetrofitClient;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private List<Product> items;
    private ItemAdapter itemAdapter;
    private static final int PICK_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    private static final int TAKE_PHOTO_REQUEST_CODE = 1001;
    private Context currentContext;
    private Bitmap imageBitmap;
    private String imageBase64;
    private Uri imageUri;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiService = RetrofitClient.getInstance();

        items = new ArrayList<>();
        itemAdapter = new ItemAdapter(items);


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemAdapter);

        FloatingActionButton fabAdd = findViewById(R.id.fab_main_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openItemEntryForm();
            }
        });
        fetchProducts();

        // Set the toolbar as the action bar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete) {
            List<Product> selectedItems = itemAdapter.getSelectedItems();
            for (Product product : selectedItems) {
                deleteProduct(product);
            }
            itemAdapter.clearSelection();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openItemEntryForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.product_item, null);
        builder.setView(dialogView);

        final TextInputEditText nameEditText = dialogView.findViewById(R.id.name_product_value);
        final TextInputEditText priceEditText = dialogView.findViewById(R.id.price_product_value);
        final TextInputEditText quantityEditText = dialogView.findViewById(R.id.quantity_product_value);
        final TextInputEditText barcodeEditText = dialogView.findViewById(R.id.baarcode_product_value);
        final TextInputEditText tagsEditText = dialogView.findViewById(R.id.tags_product_value);
        // Add similar lines for Price, Quantity, BarCode, and Tags EditTexts
        final MaterialButton imageButton = dialogView.findViewById(R.id.image_button);
        final MaterialButton saveButton = dialogView.findViewById(R.id.save_button);

        final AlertDialog dialog = builder.create();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose an option");
                String[] options = {"Pick from gallery", "Take a photo"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // Pick from gallery
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST_CODE);
                        } else if (which == 1) {
                            // Take a photo
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST_CODE);
//                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                            }
                        }
                    });
                builder.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                // Get the values from the other EditTexts
                String priceString = priceEditText.getText().toString();
                BigDecimal price = new BigDecimal(priceString);
                String quantityString = quantityEditText.getText().toString();
                int quantity = Integer.parseInt(quantityString);
                String barcode = barcodeEditText.getText().toString();
                String tags = tagsEditText.getText().toString();
                List<String> tagList = Arrays.asList(tags.split(","));

                // Hide instruction text
                TextView instructionText = findViewById(R.id.empty_list_text);
                instructionText.setVisibility(View.GONE);

                if (imageBitmap != null) {
                    Product newItem = new Product(null, name, price, quantity, barcode, imageBitmap, null, tagList);

                    // Send POST request to API
                    String token = SharedPreferencesUtil.getToken(MainActivity.this);
                    Call<ProductResponse> call = apiService.addProduct(token, newItem);
                    call.enqueue(new Callback<ProductResponse>() {
                        @Override
                        public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                            if (response.isSuccessful()) {
                                fetchProducts();
                                dialog.dismiss();
                            } else {
                                Log.e("AddProduct", "Error: " + response.message());
                                Toast.makeText(MainActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ProductResponse> call, Throwable t) {
                            Log.e("AddProduct", "Error: ", t);
                            Toast.makeText(MainActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Show a message to inform the user that an image must be picked
                    Toast.makeText(MainActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                imageBase64 = bitmapToBase64(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == TAKE_PHOTO_REQUEST_CODE && resultCode == RESULT_OK && data != null){
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                imageBase64 = bitmapToBase64(imageBitmap);
            }
        }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void fetchProducts() {

        // Hide instruction text
        TextView instructionText = findViewById(R.id.empty_list_text);
        instructionText.setVisibility(View.GONE);

        String token = SharedPreferencesUtil.getToken(MainActivity.this);
        Call<ProductsResponse> call = apiService.getProducts(token);
        call.enqueue(new Callback<ProductsResponse>() {
            @Override
            public void onResponse(Call<ProductsResponse> call, Response<ProductsResponse> response) {
                if (response.isSuccessful()) {
                    items.clear();
                    items.addAll(response.body().getProducts());
                    itemAdapter.notifyDataSetChanged();
                } else {
                    Log.e("FetchProducts", "Error: " + response.message());
                    Toast.makeText(MainActivity.this, "Failed to fetch products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductsResponse> call, Throwable t) {
                Log.e("FetchProducts", "Error: ", t);
                Toast.makeText(MainActivity.this, "Failed to fetch products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteProduct(Product product) {
        String token = SharedPreferencesUtil.getToken(MainActivity.this);
        Call<Void> call = apiService.deleteProduct(token, product.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    itemAdapter.removeItem(product);
                    Toast.makeText(MainActivity.this, "Product deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to delete product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to delete product", Toast.LENGTH_SHORT).show();
            }
        });
    }

}