package com.skr.customviews;

import android.app.AlertDialog;
import android.content.Context;

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





}
