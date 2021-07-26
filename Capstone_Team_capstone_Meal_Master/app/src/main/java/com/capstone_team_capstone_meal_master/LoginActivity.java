package com.capstone_team_capstone_meal_master;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.payments.ProofOfPayment;

import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    LinearLayout llContent;
    ProgressBar pBar;
    ImageView signInButton, loginButton;
    CallbackManager callbackManager = CallbackManager.Factory.create();
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuthWithCustomProvider(credential);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(LoginActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }
    });

    public void createPointsIfNotPresent(String uid) {
        Log.d("MYMSG", "in points");
        FirebaseFirestore instance = FirebaseFirestore.getInstance();
        instance.collection("discount").document(uid).get().addOnCompleteListener(task -> {
            toggleLoading(false);
            if (task.isSuccessful() && task.getResult() != null) {
                if (!task.getResult().exists()) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("points", 10);
                    instance.collection("discount").document(uid).set(map);
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                }
            } else {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }
        });

    }

    private void firebaseAuthWithCustomProvider(AuthCredential credential) {
        toggleLoading(true);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult().getUser() != null) {
                        createPointsIfNotPresent(task.getResult().getUser().getUid());
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    } else {
                        String msg = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                        toggleLoading(false);
                        Toast.makeText(LoginActivity.this, msg,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(this);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        llContent = findViewById(R.id.llContent);
        pBar = findViewById(R.id.pBar);
        llContent = findViewById(R.id.llContent);
        loginButton = findViewById(R.id.fb_button);
        signInButton = findViewById(R.id.sign_in_button);
        toggleLoading(false);
        signInButton.setOnClickListener((l) -> {
            toggleLoading(true);
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("176348668580-4v7t5atc41bjlveoo9llijatou42u18v.apps.googleusercontent.com")
                    .requestEmail()
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
            Intent signInIntent = googleSignInClient.getSignInIntent();
            launcher.launch(signInIntent);
            toggleLoading(false);
        });

        loginButton.setOnClickListener((l) -> {
            LoginManager.getInstance().logIn(LoginActivity.this, Arrays.asList("email", "public_profile"));
        });
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        toggleLoading(true);
                        firebaseAuthWithCustomProvider(FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken()));
                    }

                    @Override
                    public void onCancel() {
                        // App code

                    }

                    @Override
                    public void onError(FacebookException exception) {

                    }
                });

    }

    public void toggleLoading(boolean isLoading) {
        if (isLoading) {
            llContent.setVisibility(View.GONE);
            pBar.setVisibility(View.VISIBLE);
        } else {
            llContent.setVisibility(View.VISIBLE);
            pBar.setVisibility(View.GONE);
        }

    }

    public void continueAsGuest(View view) {
        toggleLoading(true);
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                String msg = "An unknown error occurred.";
                if (task.getException() != null) {
                    msg = task.getException().getMessage();
                }
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                toggleLoading(false);
            }
        });
    }

    public void login(View view) {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Please fill all fields", Snackbar.LENGTH_SHORT).show();
            return;
        }

        toggleLoading(true);
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            } else {
                toggleLoading(false);
                String exception = "An unknown error occurred.";
                if (task.getException() != null) {
                    exception = task.getException().getMessage();
                }
                Toast.makeText(this, exception, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void openRegisterActivity(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}