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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 123;
    private Button btnLogin;
    private EditText txtLoginEmail, txtLoginPassword;
    private TextView txtSignUp, txtGoogleSignIn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
//            updateUI(currentUser);
    }

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
        txtGoogleSignIn = findViewById(R.id.loginWithGoogle);

        txtSignUp = findViewById(R.id.signUpTextViewId);

        btnLogin.setOnClickListener(this);
        txtSignUp.setOnClickListener(this);
        txtGoogleSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        createRequest();
        if (view.getId() == R.id.loginId) {

            loginUser();
        } else if (view.getId() == R.id.signUpTextViewId) {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.loginWithGoogle) {
            signIn();
        }
    }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1076073061064-gvejgfjdo9rishel9a87eni132qq27bc.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Toast.makeText(getApplicationContext(), "sigin in clicked", Toast.LENGTH_SHORT).show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
//                            updateUI(null);
                        }
                    }
                });
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
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
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