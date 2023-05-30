package com.example.quickpic_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.quickpic_app.API.ApiService;
import com.example.quickpic_app.API.RetrofitClient;
import com.example.quickpic_app.Utils.SharedPreferencesUtil;
import com.example.quickpic_app.models.BarcodeDetectionRequest;
import com.example.quickpic_app.models.BarcodeDetectionResponse;
import com.example.quickpic_app.models.Product;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannerActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ImageView imageView;
    private Button snapButton, searchButton;
    private ImageCapture imageCapture;
    private ExecutorService executorService;
    private static final String TAG = "MyClass";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        previewView = findViewById(R.id.preview_view);
        imageView = findViewById(R.id.image_view);
        snapButton = findViewById(R.id.snap_button);
        searchButton = findViewById(R.id.search_photo);
        executorService = Executors.newSingleThreadExecutor();

        // Request camera permissions and start the camera
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        // Set up the click listener for the snap button
        snapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPhoto();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //    and allPermissionsGranted()
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

                imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePicture() {
        if (imageCapture == null) {
            return;
        }

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        Bitmap bitmap = imageProxyToBitmap(image);
                        imageView.setImageBitmap(bitmap);// set the bitmap to imageView
                        imageView.setVisibility(View.VISIBLE); // make the imageView visible
                        previewView.setVisibility(View.GONE); // hide the previewView
                        snapButton.setEnabled(false); // disable the snapButton
                        image.close();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e("ScannerActivity", "Image capture failed: " + exception.getMessage(), exception);
                    }
                });
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        // Rotate the bitmap based on the image's EXIF data.
        int rotationDegrees = image.getImageInfo().getRotationDegrees();
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void searchPhoto() {
        if (imageView.getDrawable() == null) {
            Toast.makeText(this, "Please capture an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert the image to a Base64 string
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        // Get the API service
        ApiService apiService = RetrofitClient.getInstance();

        // Create a request object and call the API
        BarcodeDetectionRequest request = new BarcodeDetectionRequest(imageBase64);
        String token = SharedPreferencesUtil.getToken(ScannerActivity.this);
        Call<BarcodeDetectionResponse> call = apiService.detectBarcode(token,request);

        call.enqueue(new Callback<BarcodeDetectionResponse>() {
            @Override
            public void onResponse(Call<BarcodeDetectionResponse> call, Response<BarcodeDetectionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BarcodeDetectionResponse barcodeResponse = response.body();
                    List<Product> products = barcodeResponse.getProducts();

                    if (products != null && !products.isEmpty()) {
                        // Do something with the results, such as displaying the product details
                        Product product = products.get(0);
                        Intent intent = new Intent(ScannerActivity.this, ItemDetailsActivity.class);
                        intent.putExtra("selected_item", product);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ScannerActivity.this, "No matching product found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ScannerActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<BarcodeDetectionResponse> call, Throwable t) {
                Toast.makeText(ScannerActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}