package com.skr.expensetrack;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.skr.customviews.CustomAlert;
import com.skr.datahelper.Category;
import com.skr.datahelper.DBHelper;
import com.skr.datahelper.ExpenseIncome;


public class AddCategoryActivity extends ActionBarActivity {
    public static final String CategoryToBeUpdatedStatus =  "CategoryToBeUpdatedStatus";
    public static final String CategoryToBeUpdated =  "CategoryToBeUpdated";
    public static final String UpdatedCategory =  "UpdatedCategory";
    public static final String AddCategoryFromAddExpenceIncomeScreen =  "AddCategoryFromAddExpenceIncomeScreen";
    Boolean ifCategoryToBeUpdated = false;
    Category categoryUpdatedOrAdded = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);

        final Handler handler = new Handler();

        ifCategoryToBeUpdated = getIntent().getBooleanExtra(CategoryToBeUpdatedStatus,false);

        final EditText editTextNameAddCategory = (EditText)findViewById(R.id.editTextNameAddCategory);
        RadioButton radio_income_add_category = (RadioButton)findViewById(R.id.radio_income_add_category);

        final RadioButton radio_expense_add_category = (RadioButton)findViewById(R.id.radio_expense_add_category);
        final RadioGroup radio_group_add_category = (RadioGroup)findViewById(R.id.radio_group_add_category);
        final Button save_category_button = (Button)findViewById(R.id.save_category_button);

        if(ifCategoryToBeUpdated){
            Category category = getIntent().getParcelableExtra(CategoryToBeUpdated);
            this.setTitle(getResources().getString(R.string.action_edit_category_home));
            radio_group_add_category.setVisibility(View.INVISIBLE);
            if(category.getIFEXPENSE()){
                radio_income_add_category.setChecked(false);
                radio_expense_add_category.setChecked(true);
            }else{
                radio_income_add_category.setChecked(true);
                radio_expense_add_category.setChecked(false);
            }
            editTextNameAddCategory.setText(category.getCATEGORY_NAME());
            save_category_button.setText(getResources().getString(R.string.save));
        }

        final AddCategoryActivity addCategoryActivity = this;

        save_category_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String categoryName = editTextNameAddCategory.getText().toString();
                if(categoryName.equalsIgnoreCase("")){

                    validationAlert(getResources().getString(R.string.validation_msg_add_edit_expence_category));
                    return;
                }

                int length = categoryName.length();
                if(length >= 15){

                    validationAlert(getResources().getString(R.string.validation_msg_add_edit_expence_category));
                    return;
                }
                /*
                * TODO VALIDATION That which are necessary
                * string null
                * special carecter
                * if change is present and save enable for category update
                * */


                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        DBHelper dbHelper = DBHelper.getInstance(getApplication());
                        if(ifCategoryToBeUpdated) {
                            Category category = getIntent().getParcelableExtra(CategoryToBeUpdated);
                            if(-1 == dbHelper.updateCategory(category, new Category(-1, categoryName, radio_expense_add_category.isChecked()))){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        validationAlert(getResources().getString(R.string.already_category_name_exist_msg_add_category));
                                    }
                                });
                                return;
                            }

                            categoryUpdatedOrAdded = new Category(category.getCATEGORY_ID(), categoryName, radio_expense_add_category.isChecked());
                        }else{
                            Category category = new Category(-1, categoryName, radio_expense_add_category.isChecked());
                            Pair<Integer,Category> addValReturnPair = dbHelper.addNewCategory(category);

                            if(addValReturnPair.first == -1){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                    validationAlert(getResources().getString(R.string.already_category_name_exist_msg_add_category));
                                    }
                                });
                                return;

                            }
                            categoryUpdatedOrAdded = addValReturnPair.second;
                        }
                        final  Category finalCategory = categoryUpdatedOrAdded;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                String msg = "";
                                Intent intent = new Intent();

                                if(getIntent().getBooleanExtra(AddCategoryActivity.AddCategoryFromAddExpenceIncomeScreen,false)){
                                    intent.putExtra(AddCategoryActivity.AddCategoryFromAddExpenceIncomeScreen, getIntent().getBooleanExtra(AddCategoryActivity.AddCategoryFromAddExpenceIncomeScreen, false));
                                    intent.putExtra(ExpenceIncomeDetailActivity.editStatusFlag,getIntent().getBooleanExtra(ExpenceIncomeDetailActivity.editStatusFlag,false));


                                    ExpenseIncome ei = getIntent().getParcelableExtra(ListExpenceIncomeFragment.expenseIncome);
                                    if(ei != null){
                                        intent.putExtra(ListExpenceIncomeFragment.expenseIncome,ei);
                                    }

                                }else{

                                    intent.putExtra(UpdatedCategory,finalCategory);

                                }

                                setResult(RESULT_OK,intent);
                                finish();

                                if(ifCategoryToBeUpdated){



                                }else{

                                }
//                                new CustomAlert.Builder(addCategoryActivity)
//                                        .setTitle(R.string.success_title)
//                                        .setMessage(msg)
//                                        .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int which) {
//
//
//                                                // do nothing
//                                            }
//                                        }).setPositiveButton(R.string.return_to_list,new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        if(ifCategoryToBeUpdated) {
//
//
//
//                                        }else{
//
////                                            Intent intent = new Intent();
////                                            intent.putExtra(UpdatedCategory,finalCategory);
////                                            setResult(RESULT_OK,intent);
////                                            finish();
//
//                                        } //TODO: impleme retirn to the list
//
//                                    }
//                                }).setIcon(R.drawable.success_icon)
//                                        .show();
                            }
                        });

                    }
                }).start();


            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id ==  android.R.id.home) {
            finish();

        }

        return super.onOptionsItemSelected(item);
    }


    public void validationAlert(String msg){

        new CustomAlert.Builder(this)
                .setTitle(R.string.info)
                .setMessage(msg).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


                // do nothing
            }
        }).setIcon(R.drawable.error_info)
                .show();
    }

}
