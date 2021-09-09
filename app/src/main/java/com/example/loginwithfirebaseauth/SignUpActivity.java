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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnSignup;
    private EditText txtSignupEmail, txtSignupPassword;
    private TextView txtLogin;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.setTitle("Sign Up ");

        mAuth = FirebaseAuth.getInstance();

        btnSignup = findViewById(R.id.signUpId);

        progressBar = findViewById(R.id.signUpProgressbar);

        txtSignupEmail = findViewById(R.id.signUpEmail);
        txtSignupPassword = findViewById(R.id.signUpPassword);

        txtLogin = findViewById(R.id.loginTextViewId);

        btnSignup.setOnClickListener(this);
        txtLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signUpId) {
            newUser();

        } else if (view.getId() == R.id.loginTextViewId) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    public void newUser() {
        String email = txtSignupEmail.getText().toString().trim();
        String password = txtSignupPassword.getText().toString().trim();

        if (email.isEmpty()) {
            txtSignupEmail.setError("Please Enter an email address");
            txtSignupEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtSignupEmail.setError("Please Enter a valid email ");
            txtSignupEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            txtSignupPassword.setError("Please Enter password");
            txtSignupPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            txtSignupPassword.setError("minimum length of password should be 6");
            txtSignupPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success");
                    finish();
                    Toast.makeText(getApplicationContext(), "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(), "User is already register", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }

//                    updateUI(null);
                }
            }
        });
    }
}