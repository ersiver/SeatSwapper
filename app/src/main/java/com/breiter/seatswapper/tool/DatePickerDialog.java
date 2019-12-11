package com.breiter.seatswapper.tool;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import com.breiter.seatswapper.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerDialog {

        private EditText dateEditText;
        private Calendar calendar;
        private android.app.DatePickerDialog datePickerDialog;
        private Context context;
        private Date pickedDate;

        int day, month, year;

        public DatePickerDialog(Context context, EditText dateEditText) {
            this.context = context;
            this.dateEditText = dateEditText;

        }

        public void pickDate() {
            dateEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    calendar = Calendar.getInstance();

                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    month = calendar.get(Calendar.MONTH);
                    year = calendar.get(Calendar.YEAR);

                    datePickerDialog = new android.app.DatePickerDialog(context, R.style.DialogStyle  ,new android.app.DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(android.widget.DatePicker datePicker, int mYear, int monthOfYear, int dayOfMonth) {
                            calendar.set(mYear, monthOfYear, dayOfMonth);

                            day = calendar.get(Calendar.DAY_OF_MONTH);
                            month = calendar.get(Calendar.MONTH);
                            year = calendar.get(Calendar.YEAR);

                            updateLabel();
                        }
                    }, day, month, year);


                    //following line to restrict to future date selection only
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    datePickerDialog.show();

                }
            });
        }

        private void updateLabel() {

            pickedDate = calendar.getTime();

            dateEditText.setText(fromDateToString(pickedDate));

        }


        public Date getDate(){
            return pickedDate;
        }




        public static String fromDateToString(Date date){
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(date);


        }


}