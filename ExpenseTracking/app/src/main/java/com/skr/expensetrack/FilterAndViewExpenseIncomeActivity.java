package com.skr.expensetrack;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.skr.AppController;
import com.skr.customviews.CustomAlert;
import com.skr.customviews.CustomProgressDialog;
import com.skr.datahelper.CheckBoxListData;
import com.skr.datahelper.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;



public class FilterAndViewExpenseIncomeActivity extends ActionBarActivity {

    public static final String ifBothToBeShownKey =  "ifBothToBeShownKey";
    public static final String ifExpenseToBeShownKey =  "ifExpenseToBeShownKey";
    public static final String returned_filter_category_datas   =  "returnedFilterCategoryDatas";
    public static final String returned_filter_category_id_datas   =  "returned_filter_category_id_datas";
    public static final String  start_date_edit_text_key = "start_date_edit_text_key";
    public static final String  end_date_edit_text_key = "end_date_edit_text_key";
    public static final String  min_amt_edit_text_key = "min_amt_edit_text_key";
    public static final String  max_amt_edit_text_key = "max_amt_edit_text_key";
    HashMap<Integer,String> incomeCategory;
    HashMap<Integer,String> expenseCategory;
    ArrayList<Pair<CheckBoxListData,CheckBoxListData>> incomeCategoryPairList;
    ArrayList<Pair<CheckBoxListData,CheckBoxListData>> expenseCategoryPairList;
    CheckBoxListAdapter checkBoxListAdapter;
    Boolean ifBothToBeShown = false;
    Boolean ifExpenseToBeShown = false;
    private CustomProgressDialog progress;
    CheckBox select_all_checkbox;
    CheckBox select_none_checkbox;
    Boolean if_min_touched = false;
    Boolean if_max_touched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_and_view_expense_income);

        if(getSupportActionBar() != null){getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_green)));}

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);
        final Handler handler = new Handler();
        progress =  CustomProgressDialog.getInstance(this);
        progress.setMessage(getResources().getString(R.string.wait_message));
        ifBothToBeShown = getIntent().getBooleanExtra(FilterAndViewExpenseIncomeActivity.ifBothToBeShownKey,false);
        ifExpenseToBeShown = getIntent().getBooleanExtra(FilterAndViewExpenseIncomeActivity.ifExpenseToBeShownKey,false);
progress.show();
        final ListView checkBoxListForCategoryListing = (ListView)findViewById(R.id.checkBoxListForCategoryListing);
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper dbHelper = DBHelper.getInstance(getApplication());


                if(ifBothToBeShown || ifExpenseToBeShown){
                    expenseCategoryPairList = new ArrayList<>();
                    expenseCategory = dbHelper.getAllExpenceCategory();
                    ArrayList<CheckBoxListData> checkBoxListDatasExpense = new ArrayList<>();
                    Iterator it = expenseCategory.entrySet().iterator();
                    while (it.hasNext()) {
                        HashMap.Entry pair = (HashMap.Entry)it.next();
                        checkBoxListDatasExpense.add(new CheckBoxListData((String)pair.getValue(),true,(Integer)pair.getKey()));
                        it.remove(); // avoids a ConcurrentModificationException
                    }
                    int expenceArraySize = checkBoxListDatasExpense.size();
                    if(expenceArraySize%2 != 0){
                        checkBoxListDatasExpense.add(new CheckBoxListData());
                    }
                    for (int count=0;count<checkBoxListDatasExpense.size();count++){


                        expenseCategoryPairList.add(new Pair<>(checkBoxListDatasExpense.get(count),checkBoxListDatasExpense.get(count+1)));
                        count = count+1;
                    }
                    checkBoxListAdapter = new CheckBoxListAdapter(expenseCategoryPairList);
                }

                if(ifBothToBeShown || !ifExpenseToBeShown){
                    incomeCategoryPairList  = new ArrayList<>();
                    incomeCategory = dbHelper.getAllIncomeCategory();
                    Iterator it = incomeCategory.entrySet().iterator();
                    ArrayList<CheckBoxListData> checkBoxListDatas = new ArrayList<>();
                    while (it.hasNext()) {
                        HashMap.Entry pair = (HashMap.Entry)it.next();
                        checkBoxListDatas.add(new CheckBoxListData((String)pair.getValue(),true,(Integer)pair.getKey()));
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
                    checkBoxListAdapter = new CheckBoxListAdapter(incomeCategoryPairList);
                }




                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(progress.isShowing()) {
                            progress.dismiss();

                        }


                        checkBoxListForCategoryListing.setAdapter(checkBoxListAdapter);
                        setListViewHeightBasedOnChildren(checkBoxListForCategoryListing);
                    }
                });
            }
        }).start();

        checkBoxListForCategoryListing.setFocusable(true);
        final TextView start_date_checkbox = (TextView)findViewById(R.id.start_date_checkbox);
        final EditText start_date_edit_text = (EditText)findViewById(R.id.start_date_edit_text);
//        start_date_checkbox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(start_date_checkbox.isChecked()){
//                    start_date_edit_text.setEnabled(true);
//                }else{
//                    start_date_edit_text.setText("");
//                    start_date_edit_text.setEnabled(false);
//
//                }
//            }
//        });


        start_date_edit_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
              //  if(start_date_checkbox.isChecked()) {
                    start_date_edit_text.setEnabled(true);
                    final DatePickerFragment newFragment = new DatePickerFragment();
                    newFragment.show(getFragmentManager(), "timePicker");
                    start_date_edit_text.setEnabled(false);
                    newFragment.setDatePickerFragmentListener(new DatePickerFragment.DatePickerFragmentListener() {
                        @Override
                        public void onCancel() {
                            start_date_edit_text.setEnabled(true);
                            getFragmentManager().beginTransaction().remove(newFragment).commit();

                        }

                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            String dateString = day + "-" + (month + 1) + "-" + year;
                            start_date_edit_text.setText(AppController.pareseDate_in_DD_dash_MM_dash_YYYY_to_Month_comma_Day_space_Year(dateString));
                            start_date_edit_text.setEnabled(true);
                            getFragmentManager().beginTransaction().remove(newFragment).commit();


                        }
                    });

              //  }
                    return false;


            }
        });
        final TextView end_date_checkbox = (TextView)findViewById(R.id.end_date_checkbox);
        final EditText end_date_edit_text = (EditText)findViewById(R.id.end_date_edit_text);
        start_date_edit_text.setSelected(true);

        end_date_edit_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
              //  if(end_date_checkbox.isChecked()) {
                    end_date_edit_text.setEnabled(true);
                    final DatePickerFragment newFragment = new DatePickerFragment();
                    newFragment.show(getFragmentManager(), "timePicker");
                    end_date_edit_text.setEnabled(false);
                    newFragment.setDatePickerFragmentListener(new DatePickerFragment.DatePickerFragmentListener() {
                        @Override
                        public void onCancel() {
                            end_date_edit_text.setEnabled(true);
                            getFragmentManager().beginTransaction().remove(newFragment).commit();

                        }

                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            String dateString = day + "-" + (month + 1) + "-" + year;
                            end_date_edit_text.setText(AppController.pareseDate_in_DD_dash_MM_dash_YYYY_to_Month_comma_Day_space_Year(dateString));
                            end_date_edit_text.setEnabled(true);
                            getFragmentManager().beginTransaction().remove(newFragment).commit();


                        }
                    });

              //  }
                    return false;


            }
        });
        final TextView min_amount_checkbox = (TextView)findViewById(R.id.min_amount_checkbox);
        final EditText min_amount_edit_text = (EditText)findViewById(R.id.min_amount_edit_text);

        final TextView max_amount_checkbox = (TextView)findViewById(R.id.max_amount_checkbox);
        final EditText max_amount_edit_text = (EditText)findViewById(R.id.max_amount_edit_text);
        start_date_edit_text.setFocusable(false);
        end_date_edit_text.setFocusable(false);


        max_amount_edit_text.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        min_amount_edit_text.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

        min_amount_edit_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if_min_touched = true;
            }
        });

        max_amount_edit_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if_min_touched = false;
            }
        });

        select_all_checkbox = (CheckBox)findViewById(R.id.select_all_checkbox);
        select_none_checkbox = (CheckBox)findViewById(R.id.select_none_checkbox);
        select_all_checkbox.setChecked(true);
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


        final Button filterButton = (Button)findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Pair<CheckBoxListData,CheckBoxListData>> allListItem = checkBoxListAdapter.getAll();
                ArrayList<CheckBoxListData>checkBoxListDataArrayList = new ArrayList<>();
                ArrayList<Integer>checkBoxListCateGoryIDArrayList = new ArrayList<>();
                for (int count =0;count<allListItem.size();count++){

                    Pair<CheckBoxListData,CheckBoxListData> pair = allListItem.get(count);
                    Log.e("*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&"," "+pair.first.toString()+" *** "+pair.second.toString());
                    if(pair.first.checkState) {
                        checkBoxListDataArrayList.add(pair.first);
                        checkBoxListCateGoryIDArrayList.add(pair.first.CATEGORY_ID);
                    }
                    if(pair.second.ifCheckBoxRequired) {
                        if(pair.second.checkState) {
                            checkBoxListDataArrayList.add(pair.second);
                            checkBoxListCateGoryIDArrayList.add(pair.second.CATEGORY_ID);
                        }
                    }
                }


                Intent intent = new Intent();
                intent.putExtra(FilterAndViewExpenseIncomeActivity.returned_filter_category_datas,checkBoxListDataArrayList);
                intent.putExtra(FilterAndViewExpenseIncomeActivity.returned_filter_category_id_datas,checkBoxListCateGoryIDArrayList);

                /*
                * TODO Show alert for no category selected
                * */
               // if(start_date_checkbox.isChecked()){

                    String start_date_edit_text_val = start_date_edit_text.getText().toString();

                    /*
                    * TODO the required validations
                    *
                    * */

               // }
               // if(end_date_checkbox.isChecked()){

                    String end_date_edit_text_val = end_date_edit_text.getText().toString();

                    /*
                    * TODO the required validations
                    *
                    * */

                intent.putExtra(FilterAndViewExpenseIncomeActivity.start_date_edit_text_key,start_date_edit_text_val);
                intent.putExtra(FilterAndViewExpenseIncomeActivity.end_date_edit_text_key,end_date_edit_text_val);


                // }
             //   if(min_amount_checkbox.isChecked()){

                    String min_amount_edit_text_val = min_amount_edit_text.getText().toString();
                try{
                    Integer valueAmount = Integer.parseInt(min_amount_edit_text_val);
                    if(valueAmount >10000000){
                        validationAlert(getResources().getString(R.string.validation_msg_add_edit_expence_income_amount_limit_filter));
                        return;
                    }
                }catch(Exception e){

                }
                    /*
                    * TODO the required validations
                    *
                    * */

              //  }
               // if(max_amount_checkbox.isChecked()){

                    String max_amount_edit_text_val = max_amount_edit_text.getText().toString();
                try{
                    Integer valueAmount = Integer.parseInt(max_amount_edit_text_val);
                    if(valueAmount >10000000){
                        validationAlert(getResources().getString(R.string.validation_msg_add_edit_expence_income_amount_limit_filter));
                        return;
                    }
                }catch(Exception e){

                }
                    /*
                    * TODO the required validations
                    *
                    * */

Integer minVal = null;
                Integer maxVal = null;

                try{
                    minVal = Integer.parseInt(min_amount_edit_text_val);
                }catch(Exception e){

                }

                try{
maxVal = Integer.parseInt(max_amount_edit_text_val);
                }catch(Exception e){

                }
                if(maxVal != null && minVal != null) {
                    if (Integer.parseInt(min_amount_edit_text_val) > Integer.parseInt(max_amount_edit_text_val)) {
                        intent.putExtra(FilterAndViewExpenseIncomeActivity.min_amt_edit_text_key, max_amount_edit_text_val);

                        intent.putExtra(FilterAndViewExpenseIncomeActivity.max_amt_edit_text_key, min_amount_edit_text_val);
                    } else {
                        intent.putExtra(FilterAndViewExpenseIncomeActivity.min_amt_edit_text_key, min_amount_edit_text_val);

                        intent.putExtra(FilterAndViewExpenseIncomeActivity.max_amt_edit_text_key, max_amount_edit_text_val);
                    }
                }else{
                    if(minVal != null) {
                        intent.putExtra(FilterAndViewExpenseIncomeActivity.min_amt_edit_text_key, min_amount_edit_text_val);
                    }
                    if(maxVal != null) {
                        intent.putExtra(FilterAndViewExpenseIncomeActivity.max_amt_edit_text_key, max_amount_edit_text_val);
                    }

                }
               // }
                setResult(RESULT_OK, intent);
                finish();

            }
        });


    }
    public void validationAlert(String msg){

        new CustomAlert.CustomBuilder(this,getLayoutInflater(),getString(R.string.info)).setTitle(R.string.info)
                .setMessage(msg).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


                // do nothing
            }
        }).setIcon(R.drawable.error_info)
                .show();
    }
void reloadDataOFIncomeOrExpenseWithState(Boolean state){
    if(ifExpenseToBeShown) {

        for (int count =0;count<expenseCategoryPairList.size();count++){

            Pair<CheckBoxListData,CheckBoxListData> pair = expenseCategoryPairList.get(count);
            Log.e("*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&"," "+pair.first.toString()+" *** "+pair.second.toString());

            pair.first.checkState = state;
            if(pair.second.ifCheckBoxRequired) {
                pair.second.checkState = state;
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
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter_and_view_expense_income, menu);

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
            Log.e("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$",checkBoxListData1.toString()+" &&&&&& "+checkBoxListData2.toString());
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
