package com.skr.expensetrack;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

import com.skr.customviews.CustomDatePickerDialog;

import java.util.Calendar;

/**
 * Created by sreejithkr on 28/08/15.
 */


public  class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener,DatePickerDialog.OnCancelListener,DialogInterface.OnDismissListener  {

    public interface DatePickerFragmentListener {
        public void onCancel();
        public void onDateSet(DatePicker view, int year, int month, int day);
    }


    DatePickerFragmentListener datePickerFragmentListener;
    public void setDatePickerFragmentListener(DatePickerFragmentListener datePickerFragmentListener) {
        this.datePickerFragmentListener = datePickerFragmentListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        CustomDatePickerDialog datePickerDialog = new CustomDatePickerDialog(getActivity(), this, year, month, day,getActivity());

        // Create a new instance of DatePickerDialog and return it
        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {


        if(datePickerFragmentListener != null){
            datePickerFragmentListener.onDateSet(view, year, month, day);
        }

    }


    public void onCancel(DialogInterface dialog){



        if(datePickerFragmentListener != null){
            datePickerFragmentListener.onCancel();
        }

    }
    public void onDismiss(DialogInterface dialog){

        if(datePickerFragmentListener != null){
            datePickerFragmentListener.onCancel();
        }

    }

}