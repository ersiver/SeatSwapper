package com.breiter.seatswapper.tool;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.breiter.seatswapper.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupInputChecker {


    private String usernameInput;
    private String emailInput;
    private String passwordInput;
    private boolean isUsernameValid;
    private boolean isEmailValid;
    private boolean isPasswordValid;
    private Button signupButton;

    public SignupInputChecker(Button signupButton){
        this.signupButton = signupButton;
    }

    //Build warning message in case of invalid user inputs once signing up
    public void validateUserInput(final EditText inputEditText, final TextView responseTextView, final String messageWhenInvalid) {

        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {

                String input = inputEditText.getText().toString().trim();
                boolean isValid;

                if (inputEditText.getId() == R.id.usernameEditText) {
                    usernameInput = input;
                    isValid = isUsernameValid();
                    isUsernameValid = isValid;
                }

                else if (inputEditText.getId() == R.id.emailEditText) {
                    emailInput = input;
                    isValid = isEmailValid();
                    isEmailValid = isValid;
                }

                else {
                    passwordInput = input;
                    isValid = isPasswordValid();
                    isPasswordValid = isValid;
                }

                if (!isValid) {
                    responseTextView.setTextColor(Color.RED);
                    responseTextView.setText(messageWhenInvalid);

                } else
                    responseTextView.setText("");

                updateButton();

            }
        });
    }



    //Validate username input
    private boolean isUsernameValid() {
        return usernameInput.length() >= 1 && usernameInput.length() <= 30;

    }



    //Validate email input
    private boolean isEmailValid() {

        if (emailInput.length() >= 1) {
            Pattern emailAddress = Patterns.EMAIL_ADDRESS;
            Matcher isEmail = emailAddress.matcher(emailInput);
            return isEmail.find();
        } else
            return false;
    }



    //Validate password input
    private boolean isPasswordValid() {

        if (passwordInput.length() >= 8) {

            Pattern letter = Pattern.compile("[a-zA-Z]");
            Pattern digit = Pattern.compile("[0-9]");
            Pattern special = Pattern.compile("\\p{Punct}"); //Special character : !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~

            Matcher hasLetter = letter.matcher(passwordInput);
            Matcher hasDigit = digit.matcher(passwordInput);
            Matcher hasSpecial = special.matcher(passwordInput);

            return hasLetter.find() && hasDigit.find() && hasSpecial.find();

        } else
            return false;
    }



    //Enable sign-up button, when all credenitals meet requirements
    private void updateButton(){

        if (isUsernameValid && isEmailValid && isPasswordValid)
            signupButton.setEnabled(true);

        else
            signupButton.setEnabled(false);
    }



    //Getters
    public String getUsernameInput() {
        return usernameInput;
    }

    public String getEmailInput() {
        return emailInput;
    }

    public String getPasswordInput() {
        return passwordInput;
    }


}


