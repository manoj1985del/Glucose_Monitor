package com.app.glucosemonitor.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        TextView txtSignUp = findViewById(R.id.txt_sign_up);

        progressBar = findViewById(R.id.progress_bar);

        Button btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(view -> validateInput());

        txtSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void validateInput() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.equals("")) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (!email.matches(emailPattern)) {
            Toast.makeText(this, "Enter a valid Email address", Toast.LENGTH_SHORT).show();
        } else if (password.equals("")) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            signInWithEmail(email, password);
        }
    }

    private void signInWithEmail(String email, String password) {
        CommonVariables.email = email;
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        //Sign in success, update UI with the signed-in user's information
                        LaunchActivity();
                    } else {
                        //If sign in fails, display a message to the user.
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, "Authentication failed. Reason: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void LaunchActivity() {
        CommonVariables.FirebaseUserId = auth.getUid();
        DocumentReference docRef = db.collection("users").document(CommonVariables.FirebaseUserId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    CommonVariables.loggedInUserDetails = document.toObject(User.class);
                    LaunchHomePage();
                }
            } else {
                Log.d("TAG", "get failed with ", task.getException());
            }
        });
    }

    private void LaunchHomePage() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

