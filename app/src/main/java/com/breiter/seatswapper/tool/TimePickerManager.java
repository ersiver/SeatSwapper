package com.breiter.seatswapper.tool;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.breiter.seatswapper.R;

import java.util.Calendar;

public class TimePickerManager {

    private EditText timeEditText;
    private Calendar calendar;
    private TimePickerDialog timePickerDialog;
    private Context context;
    private String pickedTime;


    int hour, minute;

    public TimePickerManager(Context context, EditText dateEditText) {
        this.context = context;
        this.timeEditText = dateEditText;

    }

    public void pickTime() {
        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calendar = Calendar.getInstance();

                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);



                timePickerDialog = new TimePickerDialog(context, R.style.DialogStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        hour = selectedHour;
                        minute = selectedMinute;


                        pickedTime = String.format("%02d:%02d", selectedHour, selectedMinute);

                        updateLabel();
                    }
                }, hour, minute, true);

                timePickerDialog.show();
            }
        });
    }


    private void updateLabel() {
        timeEditText.setText(pickedTime);
    }

    public String getTime() {
        return pickedTime;
    }
}


