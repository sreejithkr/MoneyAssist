package com.skr.expensetrack;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.skr.datahelper.ExpenseIncome;

public class AddExpenceIncomeActivity extends ActionBarActivity {
    AddExpenseIncomeFragment fragment;
    public static final String ExpenceOrIncomeFlagKey =  "ExpenceOrIncomeFlagKey";
    public static final String ReturnToListFlagKey =  "ReturnToListFlagKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expence_income);
        if(getSupportActionBar() != null){getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_green)));}
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close_button);
        setupEditExpenceIncomeFragmetn(getIntent());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if(requestCode == ActivityResultIdentifies.AddCategoryFromAddExpencePage) {
            if (resultCode == RESULT_OK) {
                final Intent dataFinal = data;
                new Handler().post(new Runnable() {
                    public void run() {
                            setupEditExpenceIncomeFragmetn(dataFinal);
                    }
                });

                // ((AddExpenseIncomeFragment)fragment)
            }
        }
    }

    private void setupEditExpenceIncomeFragmetn(Intent data){
        if (fragment != null) {
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
        if(data.getBooleanExtra(ExpenceIncomeDetailActivity.editStatusFlag,false)){
            final ExpenseIncome ei = data.getParcelableExtra(ListExpenceIncomeFragment.expenseIncome);
            fragment = new AddExpenseIncomeFragment();
            fragment.isToEditFlag = true;
            fragment.expenseIncome = ei;
            if(ei.getIF_EXPENSE()){
                setTitle(getResources().getString(R.string.edit_expense));
            }else {

                setTitle(getResources().getString(R.string.edit_income));
            }

           fragment.setmListener(new AddExpenseIncomeFragment.OnFragmentInteractionListener() {
                @Override
                public void onFragmentInteraction(Uri uri) {

                }
                @Override
                public void onCreatedFragment(){
//                    state = CurrentPage.AddExpenceIncome;
//                    invalidateOptionsMenu();
                }
                @Override
                public void onClickedReturnExpense(Boolean ifExpense){
                    onClickedReturnExpenseToListClicked(ifExpense);
                }
                @Override
                public void addCategoryButtonCLicked(Boolean ifExpense){
                    Log.e("addCategoryButtonCLicked","addCategoryButtonCLicked"+ifExpense);
                    Intent addCategoryActivity = new Intent(getApplicationContext(), AddCategoryActivity.class);
                    addCategoryActivity.putExtra(AddCategoryActivity.AddCategoryFromAddExpenceIncomeScreen,true);
                    addCategoryActivity.putExtra(ExpenceIncomeDetailActivity.editStatusFlag,true);
                    addCategoryActivity.putExtra(AddCategoryActivity.ExpenseCategoryToBeAddedFlag,ifExpense);
                    addCategoryActivity.putExtra(ListExpenceIncomeFragment.expenseIncome,ei);
                    startActivityForResult(addCategoryActivity,ActivityResultIdentifies.AddCategoryFromAddExpencePage);
                }
            });
        }else{
            setupAddExpenceIncomeFragement(data);

        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame_AEIA, fragment).commit();

    }
    private void setupAddExpenceIncomeFragement(Intent data){
        Log.e("","*)*)*)*)*)*)*)**)*)*)*)*)*)*)*)*1234567890");
        fragment = new AddExpenseIncomeFragment();
        fragment.expenseFlag = data.getBooleanExtra(AddExpenseIncomeFragment.ExpenseFlagKey,true);

        fragment.setmListener(new AddExpenseIncomeFragment.OnFragmentInteractionListener() {
            @Override
            public void onFragmentInteraction(Uri uri) {

            }

            @Override
            public void onCreatedFragment() {

            }

            @Override
            public void onClickedReturnExpense(Boolean ifExpense) {
                onClickedReturnExpenseToListClicked(ifExpense);

            }

            @Override
            public void addCategoryButtonCLicked(Boolean ifExpense) {
                Log.e("addCategoryButtonCLicked","addCategoryButtonCLicked"+ifExpense);
                Intent addCategoryActivity = new Intent(getApplicationContext(), AddCategoryActivity.class);
                addCategoryActivity.putExtra(AddCategoryActivity.ExpenseCategoryToBeAddedFlag,ifExpense);

                startActivityForResult(addCategoryActivity, ActivityResultIdentifies.AddCategoryFromAddExpencePage);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_expence_income, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id ==  android.R.id.home) {
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickedReturnExpenseToListClicked(Boolean ifExpense){

        Intent intent = new Intent();
        intent.putExtra(ExpenceOrIncomeFlagKey,ifExpense);
        intent.putExtra(ReturnToListFlagKey,true);
        setResult(RESULT_OK,intent);
        finish();

    }
}
