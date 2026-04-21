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
import com.smartbus.app.utils.ValidationHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DBHelper(this);

        etName = findViewById(R.id.et_register_name);
        etEmail = findViewById(R.id.et_register_email);
        etPassword = findViewById(R.id.et_register_password);
        etConfirmPassword = findViewById(R.id.et_register_confirm_password);
        Button btnRegister = findViewById(R.id.btn_register);
        TextView tvGoLogin = findViewById(R.id.tv_go_login);

        btnRegister.setOnClickListener(v -> registerUser());
        tvGoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, getString(R.string.auth_fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationHelper.isValidName(name)) {
            Toast.makeText(this, getString(R.string.auth_invalid_name), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationHelper.isValidEmail(email)) {
            Toast.makeText(this, getString(R.string.auth_invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationHelper.isValidPassword(password)) {
            Toast.makeText(this, getString(R.string.auth_invalid_password), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, getString(R.string.auth_password_mismatch), Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.isEmailExists(email)) {
            Toast.makeText(this, getString(R.string.auth_email_exists), Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = dbHelper.insertUser(name, email, password);
        if (isInserted) {
            Toast.makeText(this, getString(R.string.auth_register_success), Toast.LENGTH_SHORT).show();
            
            // Auto-login after registration
            com.smartbus.app.utils.SessionManager sessionManager = new com.smartbus.app.utils.SessionManager(this);
            sessionManager.setLoggedIn(true);
            sessionManager.setUserName(name);
            sessionManager.setUserEmail(email);

            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, getString(R.string.auth_register_failed), Toast.LENGTH_SHORT).show();
        }
    }
}

