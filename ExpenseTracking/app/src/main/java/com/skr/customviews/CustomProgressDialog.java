package com.skr.customviews;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by sreejithkr on 07/02/15.
 */
public class CustomProgressDialog extends ProgressDialog {


    public static CustomProgressDialog getInstance(Context context) {


        return new CustomProgressDialog(context);
    }
    public static CustomProgressDialog getInstanceWithMessage(Context context,String message) {
          CustomProgressDialog ourInstance = null;
        if(ourInstance == null){
            ourInstance= new CustomProgressDialog(context);
        }
        ourInstance.setMessage(message);
        return ourInstance;
    }
    private static Boolean cancelFlag = false;
    private CustomProgressDialog(Context context) {
        super(context);
        setCancelable(cancelFlag);

    }

    private CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        setCancelable(cancelFlag);

    }


}
