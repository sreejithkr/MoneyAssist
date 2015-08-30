package com.skr.expensetrack;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.skr.datahelper.Category;
import com.skr.datahelper.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryDeleteConfirmationActivity extends ActionBarActivity {
public final static String AddCategoryFlag =  "AddCategoryFlag";
    HashMap<Integer,String> categoriesHashMap;
    HashMap<String,Integer> categoriesHashMapWithNameKey;
    List<String> categoryArray;
    String spinnerSelectedItem;
    Boolean isLastItemInCategory = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_delete_confirmation);
        if(getSupportActionBar() != null){getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_green)));}

        final Category category = getIntent().getParcelableExtra(CategoryListFragment.CategoryToBeDeletedOrUpdated);
        final String expenseorIncome = getIntent().getStringExtra(CategoryListFragment.ExpenseOrIncome);
        final TextView expenceIncomeMessageCGDA = (TextView)findViewById(R.id.expenceIncomeMessageCGDA);
        final TextView error_select_category = (TextView)findViewById(R.id.error_select_category);
        error_select_category.setVisibility(View.GONE);
        final RadioGroup parentRadioButton = (RadioGroup)findViewById(R.id.radio_group_category);
        final RadioButton move_category = (RadioButton)findViewById(R.id.move_category);
        final RadioButton delete_all_items_category = (RadioButton)findViewById(R.id.delete_all_items_category);
        final Spinner categoryListOthers = (Spinner)findViewById(R.id.categoryListOthers);
        final Button clear_txt_max = (Button)findViewById(R.id.clear_txt_max);
        clear_txt_max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Button proceed_button = (Button)findViewById(R.id.proceed_button);


        move_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryListOthers.setVisibility(View.VISIBLE);
            }
        });
        delete_all_items_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryListOthers.setVisibility(View.GONE);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {

                final DBHelper dbHelper = DBHelper.getInstance(getApplication());

                String expenseorIncomeTitle = category.getIFEXPENSE()?  getResources().getString(R.string.expense):getResources().getString(R.string.income);
                categoriesHashMap =  category.getIFEXPENSE()? dbHelper.getAllExpenceCategory(): dbHelper.getAllIncomeCategory();
                if(categoriesHashMap.size()>1){
                    isLastItemInCategory = false;
                }else{
                    isLastItemInCategory = true;
                }

                if(isLastItemInCategory){
                    // category.getIFEXPENSE()
                    String messageText = getResources().getString(R.string.last_item_delete_category);
                    messageText = messageText.replace(CategoryListFragment.ei,expenseorIncomeTitle);

                    expenceIncomeMessageCGDA.setText(messageText);

                    parentRadioButton.setVisibility(View.GONE);
                    categoryListOthers.setVisibility(View.GONE);
                    proceed_button.setText(getString(R.string.action_add_category_home));
                    proceed_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent();
                            intent.putExtra(AddCategoryFlag,true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                    return;
                }

                if(dbHelper.getAllExpenseIncomeWithCategory(category) == 0){

                    String messageText = getResources().getString(R.string.category_delete_message_no_items);
                    expenceIncomeMessageCGDA.setText(messageText);
                    categoryListOthers.setVisibility(View.GONE);

                    parentRadioButton.setVisibility(View.GONE);

                    proceed_button.setText(getString(R.string.action_delete_category));

                    proceed_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e(" dbHelper.deleteCategory(category)",""+category.toString());
                            dbHelper.deleteCategory(category);

                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });


                }else{

                        String messageText = getResources().getString(R.string.category_delete_message_with_items);
                        messageText = messageText.replace(CategoryListFragment.ei,expenseorIncomeTitle);
                        String radio2Message = getResources().getString(R.string.category_delete_all_items);
                        radio2Message = radio2Message.replace(CategoryListFragment.c,category.getCATEGORY_NAME());
                        categoriesHashMapWithNameKey = new HashMap<>();
                        categoryArray = new ArrayList<>();
                        for (Map.Entry<Integer, String> e : categoriesHashMap.entrySet()) {
                            Integer key = e.getKey();
                            if(key != category.getCATEGORY_ID()) {
                                categoriesHashMapWithNameKey.put(e.getValue().toString(), key);
                                categoryArray.add(e.getValue().toString());
                            }

                        }
                        // Creating adapter for spinner
                        final ArrayAdapter<String> dataAdapterIncome = new ArrayAdapter<>(getApplication(),
                                R.layout.popup_spinner, categoryArray);
                        dataAdapterIncome
                                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        categoryListOthers.setAdapter(dataAdapterIncome);
                        categoryListOthers.setOnItemSelectedListener(new MyOnItemSelectedListener());

                        expenceIncomeMessageCGDA.setText(messageText);
                        String radio1Message = getResources().getString(R.string.category_move_the_items);
                        radio1Message = radio1Message.replace(CategoryListFragment.c,category.getCATEGORY_NAME());
                        move_category.setText(radio1Message);


                        delete_all_items_category.setText(radio2Message);
                        proceed_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                if(move_category.isChecked()){
                                    if(spinnerSelectedItem == null){
                                        error_select_category.setVisibility(View.VISIBLE);
                                        return;
                                    }
                                    dbHelper.updateExpenceIncomeBasedToCategory(category,categoriesHashMapWithNameKey.get(spinnerSelectedItem));



                                }else{
                                    dbHelper.deleteExpenceIncomeBasedOnCategory(category);
                                }
                                dbHelper.deleteCategory(category);
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });

                    }



            }
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_delete_confirmation, menu);
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

    public  class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            spinnerSelectedItem = ((ArrayAdapter<String>)parent.getAdapter()).getItem(pos);
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }
}
