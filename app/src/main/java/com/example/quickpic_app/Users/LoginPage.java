package com.example.quickpic_app.Users;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quickpic_app.API.ApiService;
import com.example.quickpic_app.API.RetrofitClient;
import com.example.quickpic_app.HomePage;
import com.example.quickpic_app.R;
import com.example.quickpic_app.Utils.SharedPreferencesUtil;
import com.example.quickpic_app.models.LoginResult;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginPage extends AppCompatActivity {

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        apiService = RetrofitClient.getInstance();

        Button loginButton = findViewById(R.id.btn_login);
        EditText emailEdit = findViewById(R.id.et_email);
        EditText passwordEdit = findViewById(R.id.et_password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Handle signin process
                if (validateInput(emailEdit, passwordEdit)) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("email", emailEdit.getText().toString());
                    map.put("password", passwordEdit.getText().toString());

                    Call<LoginResult> call = apiService.signInUser(map);

                    call.enqueue(new Callback<LoginResult>() {
                        @Override
                        public void onResponse(@NonNull Call<LoginResult> call, @NonNull Response<LoginResult> response) {
                            if (response.code() == 200) {
                                LoginResult result = response.body();
                                assert result != null;
                                // Save the token
                                String token = response.headers().get("x-access-token");
                                SharedPreferencesUtil.saveToken(LoginPage.this, token);

                                // Navigate to the home page
                                Intent intent = new Intent(LoginPage.this, HomePage.class);
                                startActivity(intent);

                                // Clear the input fields
                                emailEdit.setText("");
                                passwordEdit.setText("");
                            } else if (response.code() == 404 || response.code() == 403) {
                                Toast.makeText(LoginPage.this, "Wrong Credentials", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<LoginResult> call, @NonNull Throwable t) {
                            Toast.makeText(LoginPage.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        Button registerButton = findViewById(R.id.btn_reg);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, RegisterPage.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateInput(EditText emailEdit, EditText passwordEdit) {
        if (TextUtils.isEmpty(emailEdit.getText().toString())) {
            emailEdit.setError("Email is required");
            return false;
        } else if (TextUtils.isEmpty(passwordEdit.getText().toString())) {
            passwordEdit.setError("Password is required");
            return false;
        }
        return true;
    }
}