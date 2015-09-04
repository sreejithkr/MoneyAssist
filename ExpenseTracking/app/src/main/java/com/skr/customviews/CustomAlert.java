package com.skr.customviews;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.skr.expensetrack.R;

/**
 * Created by sreejithkr on 07/02/15.
 */
public class CustomAlert extends AlertDialog {
    private static Boolean cancelFlag = false;

    public CustomAlert(final Context context) {
        super(context);
        setCancelable(cancelFlag);


    }

    public CustomAlert(final Context context, final int theme) {
        super(context, theme);
        setCancelable(cancelFlag);

    }

    public CustomAlert(final Context context, final boolean cancelable, final OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setCancelable(cancelFlag);

    }

    public  static class CustomBuilder extends Builder {
       // TextView cutomeAlertTitle;
        Context context;
        public CustomBuilder(final Context context,LayoutInflater inflater,String title){
            super(context);

            this.context = context;
            // LayoutInflater inflater = getLayoutInflater();
            View view=inflater.inflate(R.layout.title_text_view, null);
            TextView cutomeAlertTitle= (TextView)view.findViewById(R.id.cutomeAlertTitle);
            cutomeAlertTitle.setText(title);

            setCancelable(cancelFlag);
            this.setCustomTitle(view);






        }

    }



}
