package com.app.glucosemonitor.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.glucosemonitor.R;
import com.app.glucosemonitor.baseactivity.HomeActivity;
import com.app.glucosemonitor.model.User;
import com.app.glucosemonitor.utils.CommonVariables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextConfirmPassword, editTextUserName;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextUserName = findViewById(R.id.edit_text_user_name);
        editTextEmail = findViewById(R.id.edit_text_signup_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextConfirmPassword = findViewById(R.id.edit_text_confirm_password);
        TextView txtLogin = findViewById(R.id.txt_login);

        progressBar = findViewById(R.id.progress_bar);

        Button btnSignUp = findViewById(R.id.btn_signup);

        btnSignUp.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            registerUser();
        });

        txtLogin.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        boolean errorFound = false;

        String userName = editTextUserName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (userName.equals("")) {
            Toast.makeText(this, "Name cannnot be empty", Toast.LENGTH_SHORT).show();
            errorFound = true;
        }


        if (email.equals("")) {
            Toast.makeText(this, "Email address cannnot be empty", Toast.LENGTH_SHORT).show();
            errorFound = true;
        }

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (!email.matches(emailPattern)) {
            Toast.makeText(this, "Enter a valid Email address", Toast.LENGTH_SHORT).show();
            errorFound = true;
        }


        if (password.equals("")) {
            Toast.makeText(this, "Password cannnot be empty", Toast.LENGTH_SHORT).show();
            errorFound = true;
        }

        if (confirmPassword.equals("")) {
            Toast.makeText(this, "Confirm Password cannnot be empty", Toast.LENGTH_SHORT).show();
            errorFound = true;
        }

        if (!password.equals("") && !confirmPassword.equals("")) {
            if (!password.matches(confirmPassword)) {
                Toast.makeText(this, "Entered Passwords do not match", Toast.LENGTH_SHORT).show();
                errorFound = true;
            }
        }

        if (errorFound) {
            return;
        }

        CommonVariables.userName = userName;

        createUser(email, password);
    }

    private void createUser(String email, String password) {
        CommonVariables.email = email;
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        saveUserInDb();
                    } else {
                        // If sign in fails, display a message to the user.
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this, "Registration failed" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserInDb() {
        User user = new User();

        String userId = auth.getUid();
        user.user_id = userId;
        user.name = CommonVariables.userName;
        user.email = CommonVariables.email;

        CommonVariables.loggedInUserDetails = user;
        db.collection("users").document(userId).set(user);
        LaunchLandingPage();
    }

    private void LaunchLandingPage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}