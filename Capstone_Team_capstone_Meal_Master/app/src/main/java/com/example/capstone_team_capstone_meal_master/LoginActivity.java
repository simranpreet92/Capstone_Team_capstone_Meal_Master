package com.capstone_team_capstone_meal_master;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    EditText etEmail, etPassword;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
    }

    public void continueAsGuest(View view) {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    public void login(View view) {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Please fill all fields", Snackbar.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            } else {
                String exception = "";
                if (task.getException() != null) {
                    exception = task.getException().getMessage();
                }
                Snackbar.make(findViewById(android.R.id.content), exception, Snackbar.LENGTH_SHORT).show();
            }
        });

    }
}