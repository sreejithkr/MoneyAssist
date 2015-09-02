package com.skr.expensetrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.skr.AppController;

public class SetYourCurrencyActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_your_currency);
        if(getSupportActionBar() != null){getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_green)));}

        Button closeSYC = (Button)findViewById(R.id.closeSYC);
        closeSYC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });
        final Context context = this;
        final EditText editTextCurrencySaveEditText = (EditText)findViewById(R.id.editTextCurrencySaveEditText);
        SharedPreferences settings = getSharedPreferences(AppController.MY_APP_PREFERENCE, 0);
        String otherCurrencyString = settings.getString(AppController.OtherCurrencyString,"");
        editTextCurrencySaveEditText.setText(otherCurrencyString);
        Button save_settings_button = (Button)findViewById(R.id.save_settings_button);
        save_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cName = editTextCurrencySaveEditText.getText().toString();
                int length = cName.length();
                if(length > 3){

                    validationAlert(getResources().getString(R.string.validation_msg_set_currency));
                    return;
                }
                AppController.setOtherCurrencyStringToSharePreference(cName,context);
                if(!cName.isEmpty()){
                    AppController.setCurrencyStringToSharePreference(cName,context);
                }else{
                    AppController.setCurrencyStringToSharePreference("",context);

                }
                dismiss();
            }
        });
    }

    public void validationAlert(String msg){

        TextView error = (TextView)findViewById(R.id.error_message_dialog_currency);
        error.setText(msg);
        error.setVisibility(View.VISIBLE);

//        new CustomAlert.CustomBuilder(this,R.style.Base_Theme_AppCompat_Dialog,getLayoutInflater())
//                .setTitle(R.string.info)
//                .setMessage(msg).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//
//
//                // do nothing
//            }
//        }).setIcon(R.drawable.error_info)
//                .show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_your_currency, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void dismiss(){
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();

    }
    @Override
    public void onBackPressed() {
        dismiss();
    }
}
