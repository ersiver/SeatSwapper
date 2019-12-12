package com.breiter.seatswapper.tool;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.breiter.seatswapper.R;
import com.breiter.seatswapper.activity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutManager {

    private Context context;
    private Dialog dialog;
    private TextView titleTextView;
    private TextView messageTextView;
    private Button cancelButton;
    private Button yesButton;

    public LogoutManager(Context context) {
        this.context = context;
    }


    public void logout() {

        String title = "Logout confirmation";
        String message = "Are you sure you want to logout?";

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_box_logout_signout);

        titleTextView = dialog.findViewById(R.id.questionTextView);
        titleTextView.setText(title);

        messageTextView = dialog.findViewById(R.id.warningTextView);
        messageTextView.setText(message);

        cancelButton = dialog.findViewById(R.id.noButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        yesButton = dialog.findViewById(R.id.okButton);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(context, LoginActivity.class).
                        setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);

            }
        });

        dialog.show();
    }




}






