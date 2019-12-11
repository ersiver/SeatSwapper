package com.breiter.seatswapper.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.breiter.seatswapper.R;
import com.breiter.seatswapper.adapter.PasswordRulesAdapter;
import com.breiter.seatswapper.tool.SignupInputChecker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    private SignupInputChecker signupInputsManager;

    private Button signupButton;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView invalidNameTextView;
    private TextView invalidEmailTextView;
    private TextView invalidPasswordTextView;
    private FrameLayout hidePasswordFrameLayout;
    private FrameLayout showPasswordFrameLayout;
    private boolean isHidden;

    private ExpandableListView expandableTextView;
    private PasswordRulesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        bindViews(); //1

        setClickListeners(); //2

        checkUserInput(); //3.

    }



    //1.
    private void bindViews() {

        signupButton = findViewById(R.id.signupButton);
        signupButton.setEnabled(false);

        usernameEditText = findViewById(R.id.usernameEditText);
        invalidNameTextView = findViewById(R.id.invalidNameTextView);
        emailEditText = findViewById(R.id.emailEditText);
        invalidEmailTextView = findViewById(R.id.invalidEmailTextView);
        passwordEditText = findViewById(R.id.passwordEditText);
        invalidPasswordTextView = findViewById(R.id.invalidPasswordTextView);
        hidePasswordFrameLayout = findViewById(R.id.hidePasswordFrameLayout);
        showPasswordFrameLayout = findViewById(R.id.showPasswordFrameLayout);
        isHidden = true;

        expandableTextView = findViewById(R.id.expandableListView);
        adapter = new PasswordRulesAdapter(SignupActivity.this);
        expandableTextView.setAdapter(adapter);

    }



    //2.
    private void setClickListeners() {

        signupButton.setOnClickListener(this);
        showPasswordFrameLayout.setOnClickListener(this);
        hidePasswordFrameLayout.setOnClickListener(this);
        findViewById(R.id.mainLayout).setOnClickListener(this);
        findViewById(R.id.logoImageView).setOnClickListener(this);
        findViewById(R.id.goBackImageView).setOnClickListener(this);

    }



    //3. Check  user input & display appropriate response in case the input is invalid
    private void checkUserInput() {

        signupInputsManager = new SignupInputChecker(signupButton);

        signupInputsManager.validateUserInput(usernameEditText, invalidNameTextView,"Please enter a valid name");

        signupInputsManager.validateUserInput(emailEditText, invalidEmailTextView, "Please enter a valid e-mail" );

        signupInputsManager.validateUserInput(passwordEditText, invalidPasswordTextView, "Password weak" );

    }



    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.signupButton)
            signUp(); //4


        else if (view.getId() == R.id.showPasswordFrameLayout || view.getId() == R.id.hidePasswordFrameLayout)
            showOrHidePassword(); //5


        else if (view.getId() == R.id.logoImageView || view.getId() == R.id.mainLayout)
            dismissKeyboard(); //6


        else if (view.getId() == R.id.goBackImageView)
            finish(); // go back to previous activity

    }



    //4. Create a new user on the Firebase & redirect
    public void signUp() {

        //Credentials valid, user will be registered in database
        mAuth.createUserWithEmailAndPassword(signupInputsManager.getEmailInput(), signupInputsManager.getPasswordInput())
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            assert user != null;

                            String userId = user.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            Map<String, String> hashMap = new HashMap<>();

                            hashMap.put("userId", userId);

                            hashMap.put("username", signupInputsManager.getUsernameInput());

                            hashMap.put("imageURL", "default");

                            hashMap.put("email", signupInputsManager.getEmailInput());

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        redirectUser(); //3a
                                    }
                                }
                            });


                        } else {

                                Toast.makeText(SignupActivity.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();

                        }
                    }

                });
    }



    //4a.
    private void redirectUser() {

        Intent intent = new Intent(SignupActivity.this, AccountActivity.class);
        startActivity(intent);

    }



    //5. Click the show/hide-icon to reveal or hide the password
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



    //6. Dismiss keyboard once layout or logo are tapped
    public void dismissKeyboard() {

        InputMethodManager inputMethodManager = (InputMethodManager) SignupActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (SignupActivity.this.getCurrentFocus() != null)

            inputMethodManager.hideSoftInputFromWindow(SignupActivity.this.getCurrentFocus().getWindowToken(), 0);

    }
}