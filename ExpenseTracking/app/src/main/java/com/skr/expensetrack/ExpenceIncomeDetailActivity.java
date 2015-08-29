package com.skr.expensetrack;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.skr.datahelper.ExpenseIncome;

public class ExpenceIncomeDetailActivity extends ActionBarActivity {

    public static final String editStatusFlag = "editStatusFlag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expence_income_detail);

        final ExpenseIncome expenseIncome =  getIntent().getParcelableExtra(ListExpenceIncomeFragment.expenseIncome);

        FragmentManager fragmentManager = getFragmentManager();
        ExpenceIncomeDetailFragment fragment = new ExpenceIncomeDetailFragment();
        fragment.expenseIncome = expenseIncome;
        fragment.categoryName = getIntent().getStringExtra(ListExpenceIncomeFragment.categoryName);
        fragment.setmListener(new ExpenceIncomeDetailFragment.OnFragmentInteractionListener(){
            public void onFragmentInteraction(Uri uri){

            }
            public void editButtonClicked(){
                Log.e("*()*)*)*)*)*)*)*)", "teatea");
                Intent intent = new Intent();
                intent.putExtra(ExpenceIncomeDetailActivity.editStatusFlag,true);
                intent.putExtra(ListExpenceIncomeFragment.expenseIncome,expenseIncome);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        fragmentManager.beginTransaction().replace(R.id.content_frame_EIDA, fragment).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_expence_income_detail, menu);
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
    private void selectItem(int position,Fragment fragment,int contentFrame) {



    }
}
