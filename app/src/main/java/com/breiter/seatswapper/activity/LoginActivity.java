package com.breiter.seatswapper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.breiter.seatswapper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText emailEditText;
    private EditText passwordEditText;
    private FrameLayout hidePasswordFrameLayout;
    private FrameLayout showPasswordFrameLayout;
    private boolean isHidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        //If user is logged redirect to AccountActivity
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null)
                    redirectUser(); //1

            }
        };

        bindViews(); //2
        setClickListeners(); //3

    }

    //1. Redirect logged user to the Account
    public void redirectUser() {

        Intent intent = new Intent(LoginActivity.this, AccountActivity.class);
        startActivity(intent);

    }

    //2.
    private void bindViews() {

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passAtLoginEditText);
        hidePasswordFrameLayout = findViewById(R.id.hidePasswordFrameLayout);
        showPasswordFrameLayout = findViewById(R.id.showPasswordFrameLayout);
        isHidden = true;
    }

    //3.
    private void setClickListeners() {

        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById(R.id.signupTextView).setOnClickListener(this);
        showPasswordFrameLayout.setOnClickListener(this);
        hidePasswordFrameLayout.setOnClickListener(this);
        findViewById(R.id.mainLayout).setOnClickListener(this);
        findViewById(R.id.logoImageView).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.loginButton)
            login(); //4

        else if (view.getId() == R.id.signupTextView)
            signUp(); //5

        else if (view.getId() == R.id.showPasswordFrameLayout || view.getId() == R.id.hidePasswordFrameLayout)
            showOrHidePassword(); //6

        else if (view.getId() == R.id.logoImageView || view.getId() == R.id.mainLayout)
            dismissKeyboard(); //7

    }

    //4. Login and redirect
    public void login() {

        String emailInput = emailEditText.getText().toString().trim();
        String passwordInput = passwordEditText.getText().toString().trim();

        if (credentialsValid(emailInput, passwordInput)) { //3a.
            mAuth.signInWithEmailAndPassword(emailInput, passwordInput)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful())
                                redirectUser();
                            else
                                Toast.makeText(LoginActivity.this,
                                        "Authentication failed", Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    //4a. Check, if email and/or password inputs are not empty
    private boolean credentialsValid(String emailInput, String passwordInput) {

        if (TextUtils.isEmpty(emailInput) || TextUtils.isEmpty(passwordInput)) {
            Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;

        } else
            return true;

    }

    //5. Click the sign-up option
    public void signUp() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);

    }

    //6. Click the show/hide-icon to reveal or hide the password
    public void showOrHidePassword() {

        if (isHidden) {
            //Show password
            passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordEditText.setSelection(passwordEditText.length());
            showPasswordFrameLayout.setVisibility(View.INVISIBLE);
            hidePasswordFrameLayout.setVisibility(View.VISIBLE);
            isHidden = false;

        } else {
            //Hide password
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordEditText.setSelection(passwordEditText.length());
            hidePasswordFrameLayout.setVisibility(View.INVISIBLE);
            showPasswordFrameLayout.setVisibility(View.VISIBLE);
            isHidden = true;
        }
    }

    //7. Dismiss keyboard once layout or logo are tapped
    public void dismissKeyboard() {

        InputMethodManager inputMethodManager = (InputMethodManager) LoginActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (LoginActivity.this.getCurrentFocus() != null && inputMethodManager != null)
            inputMethodManager.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), 0);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }


}

