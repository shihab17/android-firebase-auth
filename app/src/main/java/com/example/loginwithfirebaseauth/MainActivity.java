package com.example.loginwithfirebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin;
    private EditText txtLoginEmail, txtLoginPassword;
    private TextView txtSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Login");

        mAuth = FirebaseAuth.getInstance();

        btnLogin = findViewById(R.id.loginId);
        progressBar = findViewById(R.id.loginProgress);
        txtLoginEmail = findViewById(R.id.loginEmail);
        txtLoginPassword = findViewById(R.id.loginPassword);

        txtSignUp = findViewById(R.id.signUpTextViewId);

        btnLogin.setOnClickListener(this);
        txtSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.loginId) {
            loginUser();
        } else if (view.getId() == R.id.signUpTextViewId) {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        }
    }

    private void loginUser() {
        String email = txtLoginEmail.getText().toString();
        String password = txtLoginPassword.getText().toString();

        if (email.isEmpty()) {
            txtLoginEmail.setError("Please Enter an email address");
            txtLoginEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtLoginEmail.setError("Please Enter a valid email ");
            txtLoginEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            txtLoginPassword.setError("Please Enter an email address");
            txtLoginPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    finish();
                    Toast.makeText(getApplicationContext(), "Successfully login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } else {
                    Log.w("TAG", "signInWithEmailAndPassword:failure", task.getException());
                    Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}