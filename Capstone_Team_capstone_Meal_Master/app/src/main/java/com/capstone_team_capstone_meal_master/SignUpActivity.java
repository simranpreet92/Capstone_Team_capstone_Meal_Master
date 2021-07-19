package com.capstone_team_capstone_meal_master;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    EditText etName, etEmail, etPassword, etConfirmPassword;
   // ProgressBar progressBar;
    //LinearLayout llContent;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
       // progressBar = findViewById(R.id.pBar);
        //llContent = findViewById(R.id.llContent);
        //llContent.setVisibility(View.VISIBLE);
    }


    public void signUp() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Please fill all details", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Snackbar.make(findViewById(android.R.id.content), "Password should be greater than 6 characters", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Snackbar.make(findViewById(android.R.id.content), "Passwords should be same", Snackbar.LENGTH_SHORT).show();
            return;
        }

          //  llContent.setVisibility(View.GONE);
           // progressBar.setVisibility(View.VISIBLE);
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    FirebaseUser user = task.getResult().getUser();
                    if (user == null) {
                        return;
                    }
                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
                    user.updateProfile(userProfileChangeRequest);
                    FirebaseFirestore instance = FirebaseFirestore.getInstance();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("points", 10);
                    instance.collection("discount").document(user.getUid()).set(map);
                    startActivity(new Intent(this, HomeActivity.class));
                    finishAffinity();
                } else {
                  //  llContent.setVisibility(View.VISIBLE);
                    //progressBar.setVisibility(View.GONE);
                    String msg = "An unknown error occurred";
                    if (task.getException() != null) {
                        msg = task.getException().getMessage();
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
            });


        }


    public void saveUser(View view) {

        signUp();
    }
}