package com.skr.expensetrack;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShowTotalDetailsActivity extends ActionBarActivity {
    public static final String MessageString = "MessageString";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_total_details);
        Button closeST = (Button)findViewById(R.id.closeST);
        closeST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });


//        SpannableStringBuilder sb = new SpannableStringBuilder("You have chosen " + name + " as your contact.");
//
//// create a bold StyleSpan to be used on the SpannableStringBuilder
//        StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);

        TextView messageExpenseST = (TextView)findViewById(R.id.messageExpenseST);
        messageExpenseST.setText(getIntent().getStringExtra(HomeExpenceTrackFragment.expenseMessageKey));
        TextView messageIncomeST = (TextView)findViewById(R.id.messageIncomeST);
        messageIncomeST.setText(getIntent().getStringExtra(HomeExpenceTrackFragment.incomeMessageKey));
        TextView messagedurationST = (TextView)findViewById(R.id.messagedurationST);
        messagedurationST.setText(getIntent().getStringExtra(HomeExpenceTrackFragment.timePeriodKey));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_total_details, menu);
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
}
