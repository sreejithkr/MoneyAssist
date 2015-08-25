package com.skr.expensetrack;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.skr.AppController;
import com.skr.customviews.CustomAlert;
import com.skr.customviews.CustomProgressDialog;
import com.skr.customviews.SegmentedControl;
import com.skr.customviews.SegmentedControlButton;
import com.skr.datahelper.CheckBoxListData;
import com.skr.datahelper.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CategorySettingActivity extends ActionBarActivity {

    public static final String CategorySettingActivityGetTotalFlag =  "CategorySettingActivityGetTotalFlag";
    public static final String CategorySettingActivityCategoryList =  "CategorySettingActivityCategoryList";
    public static final String CategorySettingActivityNoExpenseSelected =  "CategorySettingActivityNoExpenseSelected";

    public static final String CategorySettingActivityNoIncomeSelected =  "CategorySettingActivityNoIncomeSelected";

    public static final String CategorySettingStartDate =  "CategorySettingStartDate";
    public static final String CategorySettingEndDate =  "CategorySettingEndDate";
    public static final String IncomeSelectAllFlag = "IncomeSelectAllFlag";
    public static final String ExpenseSelectAllFlag = "ExpenseSelectAllFlag";
    CheckBox select_all_checkbox;
    CheckBox select_none_checkbox;
    HashMap<Integer,String> incomeCategory;
    HashMap<Integer,String> expenseCategory;
    Integer incomeCategorySize = 0;
    Integer expenseCategorySize= 0;
    Boolean noExpenceSelected = false;
    Boolean noIncomeSelected = false;
    ArrayList<Pair<CheckBoxListData,CheckBoxListData>> incomeCategoryPairList;
    ArrayList<Pair<CheckBoxListData,CheckBoxListData>> expenseCategoryPairList;
    CheckBoxListAdapter checkBoxListAdapter;
    Boolean ifExpenseToBeShown = true;
    Switch categorySectionTitle;
    private CustomProgressDialog progress;

    public Boolean ifGetTotalToBeCalculated = false;
    public Pair<Boolean,Boolean> incomeExpesePair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);
        ifGetTotalToBeCalculated = getIntent().getBooleanExtra(CategorySettingActivityGetTotalFlag,false);



        select_all_checkbox = (CheckBox)findViewById(R.id.select_all_checkbox);
        select_none_checkbox = (CheckBox)findViewById(R.id.select_none_checkbox);
        select_all_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(select_all_checkbox.isChecked()){
                    select_none_checkbox.setChecked(false);
                    reloadDataOFIncomeOrExpenseWithState(true);
                }else{
                    select_none_checkbox.setChecked(true);

                    select_all_checkbox.setChecked(false);
                    reloadDataOFIncomeOrExpenseWithState(false);
                }
            }
        });
        select_none_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(select_none_checkbox.isChecked()){
                    select_all_checkbox.setChecked(false);
                    reloadDataOFIncomeOrExpenseWithState(false);
                }else{
                    select_all_checkbox.setChecked(true);
                    select_none_checkbox.setChecked(false);
                    reloadDataOFIncomeOrExpenseWithState(true);

                }
            }
        });
        select_all_checkbox.setChecked(true);
        final Handler handler = new Handler();

        final SharedPreferences settings = getSharedPreferences(AppController.MY_APP_PREFERENCE, 0);

        final Button save_settings_button = (Button)findViewById(R.id.save_settings_button);
        save_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             saveSetting();
            }
        });
        final TextView start_date_edit_text = (TextView)findViewById(R.id.start_date_edit_text);
        start_date_edit_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                start_date_edit_text.setEnabled(false);

                FilterAndViewExpenseIncomeActivity.DatePickerFragment newFragment = new FilterAndViewExpenseIncomeActivity.DatePickerFragment();
                    newFragment.show(getFragmentManager(), "timePicker");
                newFragment.setDatePickerFragmentListener(new FilterAndViewExpenseIncomeActivity.DatePickerFragmentListener() {
                    @Override
                    public void onCancel() {
                        start_date_edit_text.setEnabled(true);

                    }

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String dateString = day+"-"+(month+1)+"-"+year;

                        start_date_edit_text.setText(AppController.pareseDate_in_DD_dash_MM_dash_YYYY_to_Month_comma_Day_space_Year(dateString));
                        start_date_edit_text.setEnabled(true);

                    }
                });


                return false;


            }
        });
        final Button clear_txt_strt = (Button)findViewById(R.id.clear_txt_strt);
        clear_txt_strt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_date_edit_text.setText("");
            }
        });

        final TextView end_date_edit_text = (TextView)findViewById(R.id.end_date_edit_text);

        end_date_edit_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                end_date_edit_text.setEnabled(false);
                FilterAndViewExpenseIncomeActivity.DatePickerFragment newFragment = new FilterAndViewExpenseIncomeActivity.DatePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
                newFragment.setDatePickerFragmentListener(new FilterAndViewExpenseIncomeActivity.DatePickerFragmentListener() {
                    @Override
                    public void onCancel() {
                        end_date_edit_text.setEnabled(true);

                    }

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String dateString = day + "-" + (month + 1) + "-" + year;
                        end_date_edit_text.setText(AppController.pareseDate_in_DD_dash_MM_dash_YYYY_to_Month_comma_Day_space_Year(dateString));
                        end_date_edit_text.setEnabled(true);


                    }
                });

                return false;


            }
        });
        final Button calc_clear_txt_end = (Button)findViewById(R.id.calc_clear_txt_end);
        calc_clear_txt_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end_date_edit_text.setText("");
            }
        });

        Boolean CategorySettingOnOROffKey_CHECKED = settings.getBoolean(SettingSharedPreferenceKeys.CategorySettingOnOROffKey,false);
        categorySectionTitle = (Switch) findViewById(R.id.categorySectionTitle);
        if(ifGetTotalToBeCalculated){
            CategorySettingOnOROffKey_CHECKED = true;
            categorySectionTitle.setVisibility(View.GONE);
            save_settings_button.setText(getResources().getString(R.string.getTotalButton));

        }
        categorySectionSwitch(!CategorySettingOnOROffKey_CHECKED);
        categorySectionTitle.setChecked(CategorySettingOnOROffKey_CHECKED);

        if(categorySectionTitle.isChecked() || ifGetTotalToBeCalculated) {

            save_settings_button.setVisibility(View.VISIBLE);
        }else{

            save_settings_button.setVisibility(View.GONE);
            categoryListForBothIncomeAndExpenseReload();

        }
        categorySectionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categorySectionTitle.isChecked()) {
                    categorySectionSwitch(false);
                    save_settings_button.setVisibility(View.VISIBLE);
                }else{
                    categorySectionSwitch(true);
                    save_settings_button.setVisibility(View.GONE);
                    categoryListForBothIncomeAndExpenseReload();

                }
            }
        });


        final ListView checkBoxListForCategoryListing = (ListView)findViewById(R.id.checkBoxListForCategoryListing);
        progress =  CustomProgressDialog.getInstance(this);
        progress.setMessage(getResources().getString(R.string.wait_message));
        progress.show();



        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                // Restore preferences
                HashSet<String> expenceIDStored = null;
                HashSet<String> incomeIDStored = null;

                if(!ifGetTotalToBeCalculated){
                expenceIDStored =  (HashSet)settings.getStringSet(SettingSharedPreferenceKeys.ExpenseCategorySettingOnOROffKey,null);
                incomeIDStored = (HashSet)settings.getStringSet(SettingSharedPreferenceKeys.IncomeCategorySettingOnOROffKey,null);
                }

                ArrayList<Integer> expenceIDStoredInteger = null;
                if(expenceIDStored != null) {
                    Iterator itEx = expenceIDStored.iterator();
                    expenceIDStoredInteger = new ArrayList<>();
                    while (itEx.hasNext()) {
                        expenceIDStoredInteger.add(Integer.parseInt(itEx.next().toString()));
                    }
                }

                ArrayList<Integer> incomeIDStoredInteger =null;

                if(incomeIDStored != null) {
                    Iterator itEx = incomeIDStored.iterator();
                    incomeIDStoredInteger = new ArrayList<>();
                    while (itEx.hasNext()) {
                        incomeIDStoredInteger.add(Integer.parseInt(itEx.next().toString()));
                    }
                }

                expenseCategoryPairList = new ArrayList<>();
                expenseCategory = dbHelper.getAllExpenceCategory();
                expenseCategorySize = expenseCategory.size();
                ArrayList<CheckBoxListData> checkBoxListDatasExpense = new ArrayList<>();
                Iterator itE = expenseCategory.entrySet().iterator();
                while (itE.hasNext()) {

                    HashMap.Entry pair = (HashMap.Entry)itE.next();
                    Boolean state = true;
                    if(expenceIDStoredInteger != null) {
                        state = expenceIDStoredInteger.contains((Integer)pair.getKey());
                    }


                    checkBoxListDatasExpense.add(new CheckBoxListData((String)pair.getValue(),state,(Integer)pair.getKey()));
                    itE.remove(); // avoids a ConcurrentModificationException
                }
                int expenceArraySize = checkBoxListDatasExpense.size();
                if(expenceArraySize%2 != 0){
                    checkBoxListDatasExpense.add(new CheckBoxListData());
                }
                for (int count=0;count<checkBoxListDatasExpense.size();count++){
                    expenseCategoryPairList.add(new Pair<>(checkBoxListDatasExpense.get(count),checkBoxListDatasExpense.get(count+1)));
                    count = count+1;
                }

                incomeCategoryPairList  = new ArrayList<>();
                incomeCategory = dbHelper.getAllIncomeCategory();
                incomeCategorySize = incomeCategory.size();
                Iterator it = incomeCategory.entrySet().iterator();
                ArrayList<CheckBoxListData> checkBoxListDatas = new ArrayList<>();
                while (it.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry)it.next();
                    Boolean state = true;
                    if(incomeIDStoredInteger != null) {
                        state = incomeIDStoredInteger.contains((Integer)pair.getKey());
                    }

                    checkBoxListDatas.add(new CheckBoxListData((String)pair.getValue(),state,(Integer)pair.getKey()));
                    it.remove(); // avoids a ConcurrentModificationException
                }
                int incomeArraySize = checkBoxListDatas.size();
                if(incomeArraySize%2 != 0){
                    checkBoxListDatas.add(new CheckBoxListData());
                }
                for (int count=0;count<checkBoxListDatas.size();count++){


                    incomeCategoryPairList.add(new Pair<>(checkBoxListDatas.get(count),checkBoxListDatas.get(count+1)));
                    count = count+1;
                }

                checkBoxListAdapter = new CheckBoxListAdapter(expenseCategoryPairList);
                checkBoxListForCategoryListing.setAdapter(checkBoxListAdapter);




                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(progress.isShowing()) {
                            progress.dismiss();
                        }
                        Boolean selectAll = true;
                        Boolean selectNone = true;
                        for (int count =0;count<checkBoxListAdapter.all.size();count++){
                            Pair<CheckBoxListData,CheckBoxListData> pair = checkBoxListAdapter.all.get(count);
                            selectAll = selectAll && pair.first.checkState;
                            selectNone = selectNone && !pair.first.checkState;
                            if(pair.second.ifCheckBoxRequired) {
                                selectAll = selectAll && pair.second.checkState;
                                selectNone = selectNone && !pair.second.checkState;
                            }
                        }
                        select_all_checkbox.setChecked(selectAll);
                        select_none_checkbox.setChecked(selectNone);
                        setListViewHeightBasedOnChildren(checkBoxListForCategoryListing);

                        final CheckBoxListAdapter checkBoxListAdapter = (CheckBoxListAdapter)checkBoxListForCategoryListing.getAdapter();
                        final SegmentedControlButton segmentedControlButtonExpence = (SegmentedControlButton)findViewById(R.id.segmented_control_expense_SF);
                        final SegmentedControlButton segmentedControlButtonIncome = (SegmentedControlButton)findViewById(R.id.segmented_control_income_SF);
                        segmentedControlButtonExpence.setChecked(true);
                        segmentedControlButtonExpence.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(ifExpenseToBeShown || !categorySectionTitle.isChecked()){
                                    return;
                                }
                                segmentedControlButtonIncome.setChecked(false);
                                ifExpenseToBeShown = true;
                                incomeCategoryPairList = new ArrayList<>();
                                incomeCategoryPairList.addAll(checkBoxListAdapter.all);
                                checkBoxListAdapter.reloadData(expenseCategoryPairList);
                                Boolean selectAll = true;
                                Boolean selectNone = true;
                                for (int count =0;count<checkBoxListAdapter.all.size();count++){
                                    Pair<CheckBoxListData,CheckBoxListData> pair = checkBoxListAdapter.all.get(count);
                                    selectAll = selectAll && pair.first.checkState;
                                    selectNone = selectNone && !pair.first.checkState;
                                    if(pair.second.ifCheckBoxRequired) {
                                        selectAll = selectAll && pair.second.checkState;
                                        selectNone = selectNone && !pair.second.checkState;
                                    }
                                }
                                select_all_checkbox.setChecked(selectAll);
                                select_none_checkbox.setChecked(selectNone);
                                setListViewHeightBasedOnChildren(checkBoxListForCategoryListing);
                            }
                        });
                        segmentedControlButtonIncome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!ifExpenseToBeShown  || !categorySectionTitle.isChecked()){
                                    return;
                                }
                                segmentedControlButtonExpence.setChecked(false);
                                ifExpenseToBeShown = false;
                                expenseCategoryPairList = new ArrayList<>();
                                expenseCategoryPairList.addAll(checkBoxListAdapter.all);
                                checkBoxListAdapter.reloadData(incomeCategoryPairList);
                                Boolean selectAll = true;
                                Boolean selectNone = true;
                                for (int count =0;count<checkBoxListAdapter.all.size();count++){
                                    Pair<CheckBoxListData,CheckBoxListData> pair = checkBoxListAdapter.all.get(count);
                                    selectAll = selectAll && pair.first.checkState;
                                    selectNone = selectNone && !pair.first.checkState;
                                    if(pair.second.ifCheckBoxRequired) {
                                        selectAll = selectAll && pair.second.checkState;
                                        selectNone = selectNone && !pair.second.checkState;
                                    }
                                }
                                select_all_checkbox.setChecked(selectAll);
                                select_none_checkbox.setChecked(selectNone);
                                setListViewHeightBasedOnChildren(checkBoxListForCategoryListing);
                            }
                        });
                    }
                });
            }
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_setting, menu);
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

    public void validationAlert(String msg){

        new CustomAlert.CustomBuilder(this,getLayoutInflater())
                .setTitle(R.string.info)
                .setMessage(msg).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


                // do nothing
            }
        }).setIcon(R.drawable.error_info)
                .show();
    }
    /*
    *
    *
    * */

    void saveSetting(){

        if(ifGetTotalToBeCalculated){
            TextView start_date_edit_text = (TextView)findViewById(R.id.start_date_edit_text);
            TextView end_date_edit_text = (TextView)findViewById(R.id.end_date_edit_text);
            Intent intent = new Intent();
            intent.putExtra(CategorySettingActivityCategoryList,getTotal());

            if(noIncomeSelected && noIncomeSelected){
                validationAlert(getString(R.string.validation_get_total_category_selection_activity));
                return;
            }
            String start_date_edit_textString = AppController.pareseDate_in_Month_comma_Day_space_Year_to_DD_dash_MM_dash_YYYY(start_date_edit_text.getText().toString());
            String end_date_edit_textString = AppController.pareseDate_in_Month_comma_Day_space_Year_to_DD_dash_MM_dash_YYYY(end_date_edit_text.getText().toString());
            if(!start_date_edit_textString.isEmpty() && !end_date_edit_textString.isEmpty() && (AppController.compareTwoDateString(start_date_edit_textString,end_date_edit_textString)>0)){
                validationAlert(getString(R.string.validation_get_total_date_selection_activity));
                return;
            }

            intent.putExtra(CategorySettingActivityNoExpenseSelected,noExpenceSelected);
            intent.putExtra(CategorySettingActivityNoIncomeSelected,noIncomeSelected);

            intent.putExtra(ExpenseSelectAllFlag,incomeExpesePair.first);
            intent.putExtra(IncomeSelectAllFlag,incomeExpesePair.second);


            if(!start_date_edit_textString.isEmpty()) {
                intent.putExtra(CategorySettingStartDate, start_date_edit_textString);
            }
            if(!end_date_edit_textString.isEmpty()) {
                intent.putExtra(CategorySettingEndDate,end_date_edit_textString);
            }
            setResult(RESULT_OK,intent);
            finish();

        }else {
            // Restore preferences
            SharedPreferences settings = getSharedPreferences(AppController.MY_APP_PREFERENCE, 0);
            SharedPreferences.Editor editor = settings.edit();
            final Switch categorySectionTitle = (Switch) findViewById(R.id.categorySectionTitle);

            if (categorySectionTitle.isChecked()) {
                // CheckBoxListAdapter checkBoxListAdapter = listV
                editor.remove(SettingSharedPreferenceKeys.ExpenseCategorySettingOnOROffKey);
                editor.remove(SettingSharedPreferenceKeys.IncomeCategorySettingOnOROffKey);
                HashSet<String> idCategorySetsExpence = new HashSet<>();
                for (int count = 0; count < expenseCategoryPairList.size(); count++) {
                    Pair<CheckBoxListData, CheckBoxListData> pair = expenseCategoryPairList.get(count);
                    if (pair.first.checkState) {
                        idCategorySetsExpence.add(pair.first.CATEGORY_ID.toString());
                    }
                    if (pair.second.ifCheckBoxRequired) {
                        if (pair.second.checkState) {
                            idCategorySetsExpence.add(pair.second.CATEGORY_ID.toString());
                        }
                    }

                }
                //idCategorySets.add();
                Set<String> idCategorySetsIncome = new HashSet<>();
                for (int count = 0; count < incomeCategoryPairList.size(); count++) {
                    Pair<CheckBoxListData, CheckBoxListData> pair = incomeCategoryPairList.get(count);
                    if (pair.first.checkState) {
                        idCategorySetsIncome.add(pair.first.CATEGORY_ID.toString());
                    }
                    if (pair.second.ifCheckBoxRequired) {
                        if (pair.second.checkState) {
                            idCategorySetsIncome.add(pair.second.CATEGORY_ID.toString());
                        }
                    }

                }
                editor.putBoolean(SettingSharedPreferenceKeys.CategorySettingOnOROffKey, true);
                editor.putStringSet(SettingSharedPreferenceKeys.ExpenseCategorySettingOnOROffKey, idCategorySetsExpence);
                editor.putStringSet(SettingSharedPreferenceKeys.IncomeCategorySettingOnOROffKey, idCategorySetsIncome);
            } else {
                editor.remove(SettingSharedPreferenceKeys.ExpenseCategorySettingOnOROffKey);
                editor.remove(SettingSharedPreferenceKeys.IncomeCategorySettingOnOROffKey);
                editor.putBoolean(SettingSharedPreferenceKeys.CategorySettingOnOROffKey, false);

            }
            // editor.putStringSet()
            editor.commit();
        }
    }


    ArrayList<CheckBoxListData>  getTotal(){

        ArrayList<CheckBoxListData> checkBoxListDataArray = new ArrayList<>();
        final SegmentedControlButton segmentedControlButtonExpence = (SegmentedControlButton)findViewById(R.id.segmented_control_expense_SF);
     //   if(segmentedControlButtonExpence.isChecked()){
        noExpenceSelected = false;
        noIncomeSelected = false;
        int maxExpenseIncomeCategoryList = expenseCategoryPairList.size();
            Boolean expenseSelectAll = false;
        int i = 0;
            for (int count = 0; count < maxExpenseIncomeCategoryList; count++) {
                Pair<CheckBoxListData, CheckBoxListData> pair = expenseCategoryPairList.get(count);
                if (pair.first.checkState) {
                    i++;
                    //category is checked
                    pair.first.ifExpence = true;
                    checkBoxListDataArray.add(pair.first);
                }
                if (pair.second.ifCheckBoxRequired) {
                    if (pair.second.checkState) {
                        i++;
                        pair.second.ifExpence = true;
                        //category is checked
                        checkBoxListDataArray.add(pair.second);
                    }
                }





            }
        if(i == 0){
          noExpenceSelected = true;
           // noIncomeSelected = false;
        }
        expenseSelectAll = (i == expenseCategorySize);

        Boolean incomeSelectAll = false;

        maxExpenseIncomeCategoryList = incomeCategoryPairList.size();
        i = 0;
            for (int count = 0; count < maxExpenseIncomeCategoryList; count++) {
                Pair<CheckBoxListData, CheckBoxListData> pair = incomeCategoryPairList.get(count);
                if (pair.first.checkState) {
                    i++;
                    //category is checked
                    pair.first.ifExpence = false;
                    checkBoxListDataArray.add(pair.first);
                }
                if (pair.second.ifCheckBoxRequired) {
                    if (pair.second.checkState) {
                        i++;
                        pair.second.ifExpence = false;
                        //category is checked
                        checkBoxListDataArray.add(pair.second);
                    }
                }




        }
if(i == 0){
 //   noExpenceSelected = false;
    noIncomeSelected = true;
}
        incomeSelectAll = (i == incomeCategorySize);
        incomeExpesePair = new Pair<>(expenseSelectAll,incomeSelectAll);
        return checkBoxListDataArray;
    }

    /*
    *
    * */

    void reloadDataOFIncomeOrExpenseWithState(Boolean state){

        final ListView checkBoxListForCategoryListing = (ListView)findViewById(R.id.checkBoxListForCategoryListing);
        if(ifExpenseToBeShown) {

            for (int count =0;count<expenseCategoryPairList.size();count++){

                Pair<CheckBoxListData,CheckBoxListData> pair = expenseCategoryPairList.get(count);
                Log.e("*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&"," "+pair.first.toString()+" *** "+pair.second.toString());
                pair.first.checkState = state ;
                if(pair.second.ifCheckBoxRequired) {

                    pair.second.checkState = state ;
                }
            }

            checkBoxListAdapter.reloadData(expenseCategoryPairList);
        }else{
            for (int count =0;count<incomeCategoryPairList.size();count++){
                Pair<CheckBoxListData,CheckBoxListData> pair = incomeCategoryPairList.get(count);
                Log.e("*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&"," "+pair.first.toString()+" *** "+pair.second.toString());
                pair.first.checkState = state;


                if(pair.second.ifCheckBoxRequired) {

                    pair.second.checkState = state;
                }
            }
            checkBoxListAdapter.reloadData(incomeCategoryPairList);
        }
        select_all_checkbox.setChecked(state);
        select_none_checkbox.setChecked(!state);
        setListViewHeightBasedOnChildren(checkBoxListForCategoryListing);
    }
    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public  void setListViewHeightBasedOnChildren(ListView listView) {
        CheckBoxListAdapter listAdapter = (CheckBoxListAdapter)listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))+20;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    void categoryListForBothIncomeAndExpenseReload(){
        Boolean state = true;
        final ListView checkBoxListForCategoryListing = (ListView)findViewById(R.id.checkBoxListForCategoryListing);


        for (int count =0;count<expenseCategoryPairList.size();count++){

            Pair<CheckBoxListData,CheckBoxListData> pair = expenseCategoryPairList.get(count);
            Log.e("*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&"," "+pair.first.toString()+" *** "+pair.second.toString());
            pair.first.checkState = state ;
            if(pair.second.ifCheckBoxRequired) {

                pair.second.checkState = state ;
            }
        }


        for (int count =0;count<incomeCategoryPairList.size();count++){
            Pair<CheckBoxListData,CheckBoxListData> pair = incomeCategoryPairList.get(count);
            Log.e("*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&"," "+pair.first.toString()+" *** "+pair.second.toString());
            pair.first.checkState = state;


            if(pair.second.ifCheckBoxRequired) {

                pair.second.checkState = state;
            }
        }
        checkBoxListAdapter.reloadData(expenseCategoryPairList);

        select_all_checkbox.setChecked(state);
        select_none_checkbox.setChecked(!state);
        setListViewHeightBasedOnChildren(checkBoxListForCategoryListing);
    }

    public void categorySectionSwitch(Boolean hideFlag){

        TextView checkboxListTitle = (TextView)findViewById(R.id.checkboxListTitle);
        SegmentedControl segmented_control_FAVEIA = (SegmentedControl)findViewById(R.id.segmented_control_FAVEIA);
        LinearLayout selectAllNoneCheckboxParent = (LinearLayout)findViewById(R.id.selectAllNoneCheckboxParent);
        ListView checkBoxListForCategoryListing = (ListView)findViewById(R.id.checkBoxListForCategoryListing);
        if(hideFlag) {
            checkboxListTitle.setVisibility(View.GONE);
            selectAllNoneCheckboxParent.setVisibility(View.GONE);
            checkBoxListForCategoryListing.setVisibility(View.GONE);
            segmented_control_FAVEIA.setVisibility(View.GONE);
        }else{
            checkboxListTitle.setVisibility(View.VISIBLE);
            selectAllNoneCheckboxParent.setVisibility(View.VISIBLE);
            checkBoxListForCategoryListing.setVisibility(View.VISIBLE);
            segmented_control_FAVEIA.setVisibility(View.VISIBLE);
        }



    }
    public class CheckBoxListAdapter extends BaseAdapter {
        ArrayList<Pair<CheckBoxListData,CheckBoxListData>> all;

        public ArrayList<Pair<CheckBoxListData, CheckBoxListData>> getAll() {
            return all;
        }



        CheckBoxListAdapter(ArrayList<Pair<CheckBoxListData,CheckBoxListData>> all){
            this.all = new ArrayList<>();
            this.all.addAll(all);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return all.size();
        }

        @Override
        public Pair<CheckBoxListData,CheckBoxListData>  getItem(int arg0) {
            // TODO Auto-generated method stub
            return all.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            // TODO Auto-generated method stub

            View res = arg1;
            if (res == null) res = getLayoutInflater().inflate(R.layout.check_box_list_item, null);
            Pair<CheckBoxListData,CheckBoxListData> checkBoxListDataPair = getItem(arg0);
            CheckBoxListData checkBoxListData1 = checkBoxListDataPair.first;
            CheckBoxListData checkBoxListData2 = checkBoxListDataPair.second;
            Log.e("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$", checkBoxListData1.toString() + " &&&&&& " + checkBoxListData2.toString());
            final CheckBox checkBox1 = (CheckBox)res.findViewById(R.id.category_setting_leftCB);
            checkBox1.setChecked(checkBoxListData1.checkState);
            checkBox1.setText(checkBoxListData1.name);
            final CheckBox checkBox2 = (CheckBox)res.findViewById(R.id.category_setting_rightCB);

            final int position = arg0;
            checkBox1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(checkBox1.isChecked()){
                        getItem(position).first.checkState = true;

                    }else{
                        getItem(position).first.checkState = false;

                    }

                    Boolean selectAll = true;
                    Boolean selectNone = true;


                    for (int count =0;count<all.size();count++){

                        Pair<CheckBoxListData,CheckBoxListData> pair = all.get(count);
                        Log.e("*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&"," "+pair.first.toString()+" *** "+pair.second.toString());


                        selectAll = selectAll && pair.first.checkState;
                        selectNone = selectNone && !pair.first.checkState;

                        if(pair.second.ifCheckBoxRequired) {


                            selectAll = selectAll && pair.second.checkState;
                            selectNone = selectNone && !pair.second.checkState;




                        }
                    }


                    select_all_checkbox.setChecked(selectAll);
                    select_none_checkbox.setChecked(selectNone);



                }
            });
            if(!checkBoxListData2.ifCheckBoxRequired){
                checkBox2.setVisibility(View.GONE);
            }else{
                checkBox2.setVisibility(View.VISIBLE);
                checkBox2.setText(checkBoxListData2.name);
                checkBox2.setChecked(checkBoxListData2.checkState);
                checkBox2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(checkBox2.isChecked()){
                            getItem(position).second.checkState = true;

                        }else{
                            getItem(position).second.checkState = false;

                        }


                        Boolean selectAll = true;
                        Boolean selectNone = true;


                        for (int count =0;count<all.size();count++){

                            Pair<CheckBoxListData,CheckBoxListData> pair = all.get(count);
                            Log.e("*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&"," "+pair.first.toString()+" *** "+pair.second.toString());

                            selectAll = selectAll && pair.first.checkState;
                            selectNone = selectNone && !pair.first.checkState;

                            if(pair.second.ifCheckBoxRequired) {


                                selectAll = selectAll && pair.second.checkState;
                                selectNone = selectNone && !pair.second.checkState;



                            }
                        }

                        select_all_checkbox.setChecked(selectAll);
                        select_none_checkbox.setChecked(selectNone);


                    }
                });

            }




            return res;
        }

        public void reloadData(ArrayList<Pair<CheckBoxListData,CheckBoxListData>> all){
            this.all = all;
            notifyDataSetChanged();

        }

    }


}
