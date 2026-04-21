package com.smartbus.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.smartbus.app.R;
import com.smartbus.app.database.DBHelper;
import com.smartbus.app.utils.SessionManager;
import com.smartbus.app.utils.ValidationHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private DBHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvGoRegister = findViewById(R.id.tv_go_register);

        btnLogin.setOnClickListener(v -> loginUser());
        tvGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.auth_fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationHelper.isValidEmail(email)) {
            Toast.makeText(this, getString(R.string.auth_invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isValidUser = dbHelper.checkUser(email, password);
        if (isValidUser) {
            String name = dbHelper.getUserName(email);
            sessionManager.setLoggedIn(true);
            sessionManager.setUserName(name);
            sessionManager.setUserEmail(email);
            Toast.makeText(this, getString(R.string.auth_login_success), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, getString(R.string.auth_invalid_credentials), Toast.LENGTH_SHORT).show();
        }
    }
}
