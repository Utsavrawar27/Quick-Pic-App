package com.example.quickpic_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickpic_app.API.ApiService;
import com.example.quickpic_app.API.RetrofitClient;
import com.example.quickpic_app.Utils.SharedPreferencesUtil;
import com.example.quickpic_app.models.Product;
import com.example.quickpic_app.models.SummaryResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemDetailsActivity extends AppCompatActivity {

    private ApiService apiService;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        apiService = RetrofitClient.getInstance();


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selected_item")) {
            product = intent.getParcelableExtra("selected_item");

            // Get references to your views in activity_item_details.xml
            TextView nameTextView = findViewById(R.id.name_text_view);
            TextView namePriceView = findViewById(R.id.price_text_view);
            TextView nameQuantityView = findViewById(R.id.quantity_text_view);
            TextView nameBarcodeView = findViewById(R.id.barcode_text_view);
            TextView nameTagsView = findViewById(R.id.tags_text_view);
            ImageView itemImageView = findViewById(R.id.item_image_view);
            // Add similar lines for Price, Quantity, BarCode, Tags TextViews and ImageView

            // Set the values of your Item object to the views
            nameTextView.setText(product.getName());
            namePriceView.setText(product.getPrice().toString());
            nameQuantityView.setText(String.valueOf(product.getQuantity()));
            nameBarcodeView.setText(product.getBarcode());
//            nameTagsView.setText(selectedItem.getTags());
            nameTagsView.setText(TextUtils.join(", ", product.getTags()));
            itemImageView.setImageBitmap(product.getImage());
        } else {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        Button descriptionButton = findViewById(R.id.btn_discription);
        descriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSummary(product.getId());
            }
        });
    }

    private void requestSummary(String productId) {
        String token = SharedPreferencesUtil.getToken(ItemDetailsActivity.this);
        Map<String, String> productIdMap = new HashMap<>();
        productIdMap.put("id", productId);
        Call<SummaryResponse> call = apiService.getSummary(token, productIdMap);
        call.enqueue(new Callback<SummaryResponse>() {
            @Override
            public void onResponse(Call<SummaryResponse> call, Response<SummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SummaryResponse summaryResponse = response.body();
                    if (summaryResponse.isSuccess()) {
                        String summary = summaryResponse.getData();
                        openSummaryActivity(summary);
                    } else {
                        // Handle error
                        Log.e("Summary Products", "Error: " + response.message());
                        Toast.makeText(ItemDetailsActivity.this, "Failed to Summarise", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SummaryResponse> call, Throwable t) {
                // Handle error
                Log.e("Summary Products", "Error: " + t);
                Toast.makeText(ItemDetailsActivity.this, "Failed to Summarise", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openSummaryActivity(String summary) {
        Intent intent = new Intent(ItemDetailsActivity.this, SummaryActivity.class);
        intent.putExtra("summary", summary);
        startActivity(intent);
    }
}