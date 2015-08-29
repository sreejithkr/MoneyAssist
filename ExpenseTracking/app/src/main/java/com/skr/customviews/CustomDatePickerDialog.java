package com.skr.customviews;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.skr.AppController;
import com.skr.expensetrack.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sreejithkr on 27/08/15.
 */
public   class CustomDatePickerDialog extends DatePickerDialog {
    TextView cutomeAlertTitle;

    public CustomDatePickerDialog(Context context,
                                  OnDateSetListener callBack,
                                  int year,
                                  int monthOfYear,
                                  int dayOfMonth,Activity activity) {
        super(context, 0, callBack, year, monthOfYear, dayOfMonth);
        View view= activity.getLayoutInflater().inflate(R.layout.title_text_view, null);
        cutomeAlertTitle= (TextView)view.findViewById(R.id.cutomeAlertTitle);
        this.setCustomTitle(view);
        DateFormat format = new SimpleDateFormat(AppController.MonthSpaceDateSpaceYearFormat, Locale.ENGLISH);
        cutomeAlertTitle.setText(format.format(new Date()));

    }
    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view,year,month,day);
        String dateString = day+"-"+(month+1)+"-"+year;
        setTitle(dateString);
    }
    public void setTitle(String dateString) {

        // Do something with the date chosen by the user


        //"MMMM d, yyyy"
        Date date = null;
        DateFormat format = new SimpleDateFormat(AppController.DateDashMonthDashYearFormat, Locale.ENGLISH);
        try {

            date = format.parse(dateString);

        }catch(Exception e){

        }
        format = new SimpleDateFormat(AppController.MonthSpaceDateSpaceYearFormat, Locale.ENGLISH);
        cutomeAlertTitle.setText(format.format(date));

    }


}
