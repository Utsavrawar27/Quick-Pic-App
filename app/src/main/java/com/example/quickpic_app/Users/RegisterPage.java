package com.example.quickpic_app.Users;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quickpic_app.API.ApiService;
import com.example.quickpic_app.API.RetrofitClient;
import com.example.quickpic_app.R;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterPage extends AppCompatActivity {

    private ApiService apiService;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        apiService = RetrofitClient.getInstance();

        Button signupButton = findViewById(R.id.btn_register);
        final EditText firstnameEdit = findViewById(R.id.et_name);
        final EditText lastnameEdit = findViewById(R.id.last_name);
        final EditText emailEdit = findViewById(R.id.et_email);
        final EditText passwordEdit = findViewById(R.id.et_password);

        progressBar = findViewById(R.id.progressBar);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput(firstnameEdit, lastnameEdit, emailEdit, passwordEdit)) {
                    // Handle signup process
                    HashMap<String, String> map = new HashMap<>();

                    map.put("firstName", firstnameEdit.getText().toString());
                    map.put("lastName", lastnameEdit.getText().toString());
                    map.put("email", emailEdit.getText().toString());
                    map.put("password", passwordEdit.getText().toString());

                    progressBar.setVisibility(View.VISIBLE);

                    Call<Void> call = apiService.signUpUser(map);

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.code() == 200) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterPage.this,
                                        "Signed up successfully", Toast.LENGTH_LONG).show();
                                // Navigate to the login page
                                // Replace LoginPage.class with the actual class name of your login fragment or activity
                                Intent intent = new Intent(RegisterPage.this, LoginPage.class);
                                startActivity(intent);

                                // Clear the input fields
                                firstnameEdit.setText("");
                                lastnameEdit.setText("");
                                emailEdit.setText("");
                                passwordEdit.setText("");
                            } else if (response.code() == 400) {
                                Toast.makeText(RegisterPage.this,
                                        "Already registered", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterPage.this, t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private boolean validateInput(EditText firstnameEdit, EditText lastnameEdit, EditText emailEdit, EditText passwordEdit) {
        if (TextUtils.isEmpty(firstnameEdit.getText().toString())) {
            firstnameEdit.setError("First name is required");
            return false;
        } else if (TextUtils.isEmpty(lastnameEdit.getText().toString())) {
            lastnameEdit.setError("Last name is required");
            return false;
        } else if (TextUtils.isEmpty(emailEdit.getText().toString())) {
            emailEdit.setError("Email is required");
            return false;
        } else if (TextUtils.isEmpty(passwordEdit.getText().toString())) {
            passwordEdit.setError("Password is required");
            return false;
        }
        return true;
    }
}