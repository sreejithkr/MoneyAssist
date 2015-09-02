package com.skr.expensetrack;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.skr.AppController;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    CheckBox select_all_checkbox;
    CheckBox select_none_checkbox;
    HashMap<Integer,String> incomeCategory;
    HashMap<Integer,String> expenseCategory;
    ArrayList<Pair<CheckBoxListData,CheckBoxListData>> incomeCategoryPairList;
    ArrayList<Pair<CheckBoxListData,CheckBoxListData>> expenseCategoryPairList;
    CheckBoxListAdapter checkBoxListAdapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private CustomProgressDialog progress;
    Boolean ifExpenseToBeShown = true;
    private ArrayList<Integer> expenceId;
    private ArrayList<Integer> incomeId;
    private OnFragmentInteractionListener mListener;

    public OnFragmentInteractionListener getmListener() {
        return mListener;
    }

    public void setmListener(OnFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView =inflater.inflate(R.layout.fragment_setting, container, false);
        if(mListener != null){
          //  mListener.onCreatedFragment();
        }



        expenceId = new ArrayList<>();
        incomeId = new ArrayList<>();
        final Handler handler = new Handler();
        select_all_checkbox = (CheckBox)rootView.findViewById(R.id.select_all_checkbox);
        select_none_checkbox = (CheckBox)rootView.findViewById(R.id.select_none_checkbox);
        select_all_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(select_all_checkbox.isChecked()){
                    select_none_checkbox.setChecked(false);
                    reloadDataOFIncomeOrExpenseWithState(true);
                }
            }
        });
        select_none_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(select_none_checkbox.isChecked()){
                    select_all_checkbox.setChecked(false);
                   reloadDataOFIncomeOrExpenseWithState(false);
                }
            }
        });
        select_all_checkbox.setChecked(true);
        final SharedPreferences settings = getActivity().getSharedPreferences(AppController.MY_APP_PREFERENCE, 0);

        Boolean CategorySettingOnOROffKey_HIDE_STATUS = !settings.getBoolean(SettingSharedPreferenceKeys.CategorySettingOnOROffKey,false);
        Boolean DurationSettingOnOROffKey_HIDE_STATUS = !settings.getBoolean(SettingSharedPreferenceKeys.DurationSettingOnOROffKey,false);
        Boolean AmountSettingOnOROffKey_HIDE_STATUS = !settings.getBoolean(SettingSharedPreferenceKeys.AmountSettingOnOROffKey,false);

        currencySectionSwitch(true,rootView);

        categorySectionSwitch(CategorySettingOnOROffKey_HIDE_STATUS,rootView);
        populateDutrationSettingFromSharedPreference(rootView);
        durationSectionSwitch(DurationSettingOnOROffKey_HIDE_STATUS,rootView);
        amountSectionSwitch(AmountSettingOnOROffKey_HIDE_STATUS,rootView);
        configureDurationRadionOnclicks(rootView);

        final Switch currencySectionTitle = (Switch) rootView.findViewById(R.id.currencySectionTitle);
        currencySectionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currencySectionTitle.isChecked()){
                    currencySectionSwitch(false, getView());
                }else{
                    currencySectionSwitch(true, getView());

                }
            }
        });
        final Switch categorySectionTitle = (Switch) rootView.findViewById(R.id.categorySectionTitle);
        categorySectionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categorySectionTitle.isChecked()){
                    categorySectionSwitch(false,getView());
                }else{
                    categorySectionSwitch(true,getView());
                    categoryListForBothIncomeAndExpenseReload();

                }
            }
        });
        categorySectionTitle.setChecked(!CategorySettingOnOROffKey_HIDE_STATUS);
        final Switch durationSectionTitle = (Switch) rootView.findViewById(R.id.durationSectionTitle);
        durationSectionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(durationSectionTitle.isChecked()){
                    durationSectionSwitch(false, getView());

                }else{
                    durationSectionSwitch(true, getView());
                    resetDutrationSetting(rootView);
                }
            }
        });
        durationSectionTitle.setChecked(!DurationSettingOnOROffKey_HIDE_STATUS);
        final Switch amountSectionTitle = (Switch) rootView.findViewById(R.id.amountSectionTitle);
        amountSectionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(amountSectionTitle.isChecked()){
                    amountSectionSwitch(false, getView());
                }else{
                    amountSectionSwitch(true, getView());
                    resetAmountSetting(rootView);

                }
            }
        });

        amountSectionTitle.setChecked(!AmountSettingOnOROffKey_HIDE_STATUS);

        progress =  CustomProgressDialog.getInstance(getActivity());
        progress.setMessage(getResources().getString(R.string.wait_message));
        progress.show();

        final ListView checkBoxListForCategoryListing = (ListView)rootView.findViewById(R.id.checkBoxListForCategoryListing);
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper dbHelper = DBHelper.getInstance(getActivity());
                // Restore preferences

                HashSet<String> expenceIDStored =  (HashSet)settings.getStringSet(SettingSharedPreferenceKeys.ExpenseCategorySettingOnOROffKey,null);
                HashSet<String> incomeIDStored = (HashSet)settings.getStringSet(SettingSharedPreferenceKeys.IncomeCategorySettingOnOROffKey,null);
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
                        final SegmentedControlButton segmentedControlButtonExpence = (SegmentedControlButton)rootView.findViewById(R.id.segmented_control_expense_SF);
                        final SegmentedControlButton segmentedControlButtonIncome = (SegmentedControlButton)rootView.findViewById(R.id.segmented_control_income_SF);
                        segmentedControlButtonExpence.setChecked(true);
                        segmentedControlButtonExpence.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(ifExpenseToBeShown){
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
                            }
                        });
                        segmentedControlButtonIncome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!ifExpenseToBeShown){
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
                            }
                        });
                    }
                });
            }
        }).start();

        final CheckBox start_date_checkbox = (CheckBox)rootView.findViewById(R.id.start_date_checkbox);
        final EditText start_date_edit_text = (EditText)rootView.findViewById(R.id.start_date_edit_text);
        start_date_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start_date_checkbox.isChecked()){
                    start_date_edit_text.setEnabled(true);
                }else{
                    start_date_edit_text.setText("");
                    start_date_edit_text.setEnabled(false);
                }
            }
        });
        start_date_edit_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(start_date_checkbox.isChecked()) {
                    start_date_edit_text.setEnabled(true);
                   final DatePickerFragment newFragment = new DatePickerFragment();
                    newFragment.show(getFragmentManager(), "timePicker");
                    newFragment.setDatePickerFragmentListener( new DatePickerFragment.DatePickerFragmentListener() {
                        @Override
                        public void onCancel() {

                                start_date_edit_text.setEnabled(true);
                            getActivity().getFragmentManager().beginTransaction().remove(newFragment).commit();

                        }

                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            // Do something with the date chosen by the user
                            String dateString = day+"-"+(month+1)+"-"+year;


                            start_date_edit_text.setText(dateString);
                            start_date_edit_text.setEnabled(true);
                            getActivity().getFragmentManager().beginTransaction().remove(newFragment).commit();

                        }
                    });
                    start_date_edit_text.setEnabled(false);
                }
                return false;
            }
        });

        final CheckBox end_date_checkbox = (CheckBox)rootView.findViewById(R.id.end_date_checkbox);
        final EditText end_date_edit_text = (EditText)rootView.findViewById(R.id.end_date_edit_text);
        end_date_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(end_date_checkbox.isChecked()){
                    end_date_edit_text.setEnabled(true);
                }else{
                    end_date_edit_text.setText("");
                    end_date_edit_text.setEnabled(false);
                }
            }
        });
        end_date_edit_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(end_date_checkbox.isChecked()) {
                    end_date_edit_text.setEnabled(true);
                    final DatePickerFragment newFragment = new DatePickerFragment();
                    newFragment.show(getFragmentManager(), "timePicker");
                    newFragment.setDatePickerFragmentListener( new DatePickerFragment.DatePickerFragmentListener() {
                        @Override
                        public void onCancel() {

                            end_date_edit_text.setEnabled(true);
                            getActivity().getFragmentManager().beginTransaction().remove(newFragment).commit();

                        }

                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            // Do something with the date chosen by the user
                            String dateString = day+"-"+(month+1)+"-"+year;


                            end_date_edit_text.setText(dateString);
                            end_date_edit_text.setEnabled(true);
                            getActivity().getFragmentManager().beginTransaction().remove(newFragment).commit();

                        }
                    });
                    end_date_edit_text.setEnabled(false);
                }
                return false;
            }
        });
        final CheckBox min_amount_checkbox = (CheckBox)rootView.findViewById(R.id.min_amount_checkbox);
        final EditText min_amount_edit_text = (EditText)rootView.findViewById(R.id.min_amount_edit_text);
        min_amount_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(min_amount_checkbox.isChecked()){
                    min_amount_edit_text.setEnabled(true);
                }else{
                    min_amount_edit_text.setText("");
                    min_amount_edit_text.setEnabled(false);
                }
            }
        });
        final CheckBox max_amount_checkbox = (CheckBox)rootView.findViewById(R.id.max_amount_checkbox);
        final EditText max_amount_edit_text = (EditText)rootView.findViewById(R.id.max_amount_edit_text);
        max_amount_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(max_amount_checkbox.isChecked()){
                    max_amount_edit_text.setEnabled(true);
                }else{
                    max_amount_edit_text.setText("");
                    max_amount_edit_text.setEnabled(false);
                }
            }
        });
        Button save_settings_button = (Button)rootView.findViewById(R.id.save_settings_button);
        save_settings_button.setOnClickListener(new View.OnClickListener() {
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
                 /*
                * TODO Show alert for no category selected
                * */

                if(start_date_checkbox.isChecked()){

                    String text = start_date_edit_text.getText().toString();

                    /*
                    * TODO the required validations
                    *
                    * */

                //    intent.putExtra(FilterAndViewExpenseIncomeActivity.start_date_edit_text_key,text);
                }

                if(end_date_checkbox.isChecked()){

                    String text = end_date_edit_text.getText().toString();

                    /*
                    * TODO the required validations
                    *
                    * */

                 //   intent.putExtra(FilterAndViewExpenseIncomeActivity.end_date_edit_text_key,text);
                }
                if(min_amount_checkbox.isChecked()){

                    String text = min_amount_edit_text.getText().toString();

                    /*
                    * TODO the required validations
                    *
                    * */

                  //  intent.putExtra(FilterAndViewExpenseIncomeActivity.min_amt_edit_text_key,text);
                }
                if(max_amount_checkbox.isChecked()){

                    String text = max_amount_edit_text.getText().toString();

                    /*
                    * TODO the required validations
                    *
                    * */

                   // intent.putExtra(FilterAndViewExpenseIncomeActivity.max_amt_edit_text_key,text);
                }



                saveToSharedPReference();


            }
        });

        return rootView;
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
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        public void setListViewHeightBasedOnChildren();
    }


    void reloadDataOFIncomeOrExpenseWithState(Boolean state){

        final ListView checkBoxListForCategoryListing = (ListView)getView().findViewById(R.id.checkBoxListForCategoryListing);
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
            if (res == null) res = getActivity().getLayoutInflater().inflate(R.layout.check_box_list_item, null);
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


    public void currencySectionSwitch(Boolean hideFlag,View res){

    }
    public void categorySectionSwitch(Boolean hideFlag,View res){

        TextView checkboxListTitle = (TextView)res.findViewById(R.id.checkboxListTitle);
        SegmentedControl segmented_control_FAVEIA = (SegmentedControl)res.findViewById(R.id.segmented_control_FAVEIA);
        LinearLayout selectAllNoneCheckboxParent = (LinearLayout)res.findViewById(R.id.selectAllNoneCheckboxParent);
        ListView checkBoxListForCategoryListing = (ListView)res.findViewById(R.id.checkBoxListForCategoryListing);
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
    public void durationSectionSwitch(Boolean hideFlag,View res){
        RadioGroup radio_group_deration_setting_options = (RadioGroup)res.findViewById(R.id.radio_group_deration_setting_options);
        RadioGroup radio_group_d_m_y = (RadioGroup)res.findViewById(R.id.radio_group_d_m_y);
        EditText duration_day_month_year_value =(EditText)res.findViewById(R.id.duration_day_month_year_value);
TextView duration_setting_title =(TextView)res.findViewById(R.id.duration_setting_title);
        LinearLayout enableDateCriteria = (LinearLayout)res.findViewById(R.id.enableDateCriteria);
        LinearLayout parentForFromAndToDateTextBox = (LinearLayout)res.findViewById(R.id.parentForFromAndToDateTextBox);
        final RadioButton titleRadioDurationMontsDayYears = (RadioButton)res.findViewById(R.id.titleRadioDurationMontsDayYears);
        final Switch durationSectionTitle = (Switch) res.findViewById(R.id.durationSectionTitle);

        if(hideFlag) {
            radio_group_deration_setting_options.setVisibility(View.GONE);
            enableDateCriteria.setVisibility(View.GONE);
            parentForFromAndToDateTextBox.setVisibility(View.GONE);
            radio_group_d_m_y.setVisibility(View.GONE);
            duration_day_month_year_value.setVisibility(View.GONE);
            duration_setting_title.setVisibility(View.GONE);
        }else{
            radio_group_deration_setting_options.setVisibility(View.VISIBLE);
            duration_setting_title.setVisibility(View.VISIBLE);
          //  if(!durationSectionTitle.isChecked()) return;
            if(titleRadioDurationMontsDayYears.isChecked()){


                    radio_group_d_m_y.setVisibility(View.VISIBLE);
                    duration_day_month_year_value.setVisibility(View.VISIBLE);
                    enableDateCriteria.setVisibility(View.GONE);
                    parentForFromAndToDateTextBox.setVisibility(View.GONE);

            }else{
                radio_group_d_m_y.setVisibility(View.GONE);
                duration_day_month_year_value.setVisibility(View.GONE);
                enableDateCriteria.setVisibility(View.VISIBLE);
                parentForFromAndToDateTextBox.setVisibility(View.VISIBLE);
            }
        }



    }
    public void amountSectionSwitch(Boolean hideFlag,View res){

        TextView parentForMinMaxAmountTitle = (TextView)res.findViewById(R.id.parentForMinMaxAmountTitle);
        LinearLayout enableAmountCriteriaParent = (LinearLayout)res.findViewById(R.id.enableAmountCriteriaParent);
        LinearLayout parentForMAxMinAmountTextBox = (LinearLayout)res.findViewById(R.id.parentForMAxMinAmountTextBox);

        if(hideFlag) {
            parentForMinMaxAmountTitle.setVisibility(View.GONE);
            parentForMAxMinAmountTextBox.setVisibility(View.GONE);
            enableAmountCriteriaParent.setVisibility(View.GONE);
        }else{
            parentForMinMaxAmountTitle.setVisibility(View.VISIBLE);
            parentForMAxMinAmountTextBox.setVisibility(View.VISIBLE);
            enableAmountCriteriaParent.setVisibility(View.VISIBLE);
        }

    }

    private void configureDurationRadionOnclicks(View res){
        final Switch durationSectionTitle = (Switch) res.findViewById(R.id.durationSectionTitle);
       // final RadioGroup radio_group_deration_setting_options = (RadioGroup)res.findViewById(R.id.radio_group_deration_setting_options);
        final RadioGroup radio_group_d_m_y = (RadioGroup)res.findViewById(R.id.radio_group_d_m_y);
        final EditText duration_day_month_year_value =(EditText)res.findViewById(R.id.duration_day_month_year_value);

        final LinearLayout enableDateCriteria = (LinearLayout)res.findViewById(R.id.enableDateCriteria);
        final LinearLayout parentForFromAndToDateTextBox = (LinearLayout)res.findViewById(R.id.parentForFromAndToDateTextBox);

        final RadioButton titleRadioDurationMontsDayYears = (RadioButton)res.findViewById(R.id.titleRadioDurationMontsDayYears);
        final RadioButton fromAndToDateTextBoxTitle = (RadioButton)res.findViewById(R.id.fromAndToDateTextBoxTitle);
        radio_group_d_m_y.setVisibility(View.GONE);
        duration_day_month_year_value.setVisibility(View.GONE);
        enableDateCriteria.setVisibility(View.GONE);
        parentForFromAndToDateTextBox.setVisibility(View.GONE);
        titleRadioDurationMontsDayYears.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!durationSectionTitle.isChecked()) return;
                if(titleRadioDurationMontsDayYears.isChecked()){
                    radio_group_d_m_y.setVisibility(View.VISIBLE);
                    duration_day_month_year_value.setVisibility(View.VISIBLE);
                    enableDateCriteria.setVisibility(View.GONE);
                    parentForFromAndToDateTextBox.setVisibility(View.GONE);
                }else{
                    radio_group_d_m_y.setVisibility(View.GONE);
                    duration_day_month_year_value.setVisibility(View.GONE);
                    enableDateCriteria.setVisibility(View.VISIBLE);
                    parentForFromAndToDateTextBox.setVisibility(View.VISIBLE);
                }
            }
        });
        fromAndToDateTextBoxTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!durationSectionTitle.isChecked()) return;
                if(fromAndToDateTextBoxTitle.isChecked()){
                    radio_group_d_m_y.setVisibility(View.GONE);
                    duration_day_month_year_value.setVisibility(View.GONE);
                    enableDateCriteria.setVisibility(View.VISIBLE);
                    parentForFromAndToDateTextBox.setVisibility(View.VISIBLE);
                }else{
                    radio_group_d_m_y.setVisibility(View.VISIBLE);
                    duration_day_month_year_value.setVisibility(View.VISIBLE);
                    enableDateCriteria.setVisibility(View.GONE);
                    parentForFromAndToDateTextBox.setVisibility(View.GONE);
                }
            }
        });

    }

    private void saveToSharedPReference(){

        View res = getView();


        //TODO VALIDATION ARE TO BE DONE

        final Switch currencySectionTitle = (Switch) res.findViewById(R.id.currencySectionTitle);
        final Switch categorySectionTitle = (Switch) res.findViewById(R.id.categorySectionTitle);
        final Switch durationSectionTitle = (Switch) res.findViewById(R.id.durationSectionTitle);
        final Switch amountSectionTitle = (Switch) res.findViewById(R.id.amountSectionTitle);

        RadioGroup radio_group_duration_setting_options = (RadioGroup)res.findViewById(R.id.radio_group_deration_setting_options);
        TextView duration_setting_title =(TextView)res.findViewById(R.id.duration_setting_title);
        TextView checkboxListTitle = (TextView)res.findViewById(R.id.checkboxListTitle);
        SegmentedControl segmented_control_FAVEIA = (SegmentedControl)res.findViewById(R.id.segmented_control_FAVEIA);
        LinearLayout selectAllNoneCheckboxParent = (LinearLayout)res.findViewById(R.id.selectAllNoneCheckboxParent);

        final ListView checkBoxListForCategoryListing = (ListView)res.findViewById(R.id.checkBoxListForCategoryListing);
        final RadioGroup radio_group_d_m_y = (RadioGroup)res.findViewById(R.id.radio_group_d_m_y);
        final EditText duration_day_month_year_value =(EditText)res.findViewById(R.id.duration_day_month_year_value);

        final LinearLayout enableDateCriteria = (LinearLayout)res.findViewById(R.id.enableDateCriteria);
        final LinearLayout parentForFromAndToDateTextBox = (LinearLayout)res.findViewById(R.id.parentForFromAndToDateTextBox);

        final RadioButton fromAndToDateTextBoxTitle = (RadioButton)res.findViewById(R.id.fromAndToDateTextBoxTitle);
        
        TextView parentForMinMaxAmountTitle = (TextView)res.findViewById(R.id.parentForMinMaxAmountTitle);
        LinearLayout enableAmountCriteriaParent = (LinearLayout)res.findViewById(R.id.enableAmountCriteriaParent);
        LinearLayout parentForMAxMinAmountTextBox = (LinearLayout)res.findViewById(R.id.parentForMAxMinAmountTextBox);

        final CheckBox start_date_checkbox = (CheckBox)res.findViewById(R.id.start_date_checkbox);
        final EditText start_date_edit_text =(EditText)res.findViewById(R.id.start_date_edit_text);

        final CheckBox end_date_checkbox = (CheckBox)res.findViewById(R.id.end_date_checkbox);
        final EditText end_date_edit_text =(EditText)res.findViewById(R.id.end_date_edit_text);

        final CheckBox min_amount_checkbox = (CheckBox)res.findViewById(R.id.min_amount_checkbox);
        final EditText min_amount_edit_text =(EditText)res.findViewById(R.id.min_amount_edit_text);

        final CheckBox max_amount_checkbox = (CheckBox)res.findViewById(R.id.max_amount_checkbox);
        final EditText max_amount_edit_text =(EditText)res.findViewById(R.id.max_amount_edit_text);

        final RadioButton titleRadioDurationMontsDayYears = (RadioButton) res.findViewById(R.id.titleRadioDurationMontsDayYears);

        final RadioButton days_Setting = (RadioButton) res.findViewById(R.id.days_Setting);
        final RadioButton MonthsSetting = (RadioButton) res.findViewById(R.id.MonthsSetting);
        final RadioButton years_setting = (RadioButton) res.findViewById(R.id.years_setting);
        // Restore preferences
        SharedPreferences settings = getActivity().getSharedPreferences(AppController.MY_APP_PREFERENCE, 0);



        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean(SettingSharedPreferenceKeys.CurrencySettingOnOROffKey,currencySectionTitle.isChecked());
        editor.putBoolean(SettingSharedPreferenceKeys.CategorySettingOnOROffKey,categorySectionTitle.isChecked());
        editor.putBoolean(SettingSharedPreferenceKeys.DurationSettingOnOROffKey,durationSectionTitle.isChecked());
        editor.putBoolean(SettingSharedPreferenceKeys.AmountSettingOnOROffKey,amountSectionTitle.isChecked());
        if(currencySectionTitle.isChecked()) {
        }

        if(categorySectionTitle.isChecked()) {
           // CheckBoxListAdapter checkBoxListAdapter = listV
            editor.remove(SettingSharedPreferenceKeys.ExpenseCategorySettingOnOROffKey);
            editor.remove(SettingSharedPreferenceKeys.IncomeCategorySettingOnOROffKey);
            HashSet<String> idCategorySetsExpence = new HashSet<>();
            for (int count=0;count< expenseCategoryPairList.size();count++){
                Pair<CheckBoxListData,CheckBoxListData> pair = expenseCategoryPairList.get(count);
                if(pair.first.checkState) {
                    idCategorySetsExpence.add(pair.first.CATEGORY_ID.toString());
                }
                    if(pair.second.ifCheckBoxRequired) {
                        if(pair.second.checkState) {
                            idCategorySetsExpence.add(pair.second.CATEGORY_ID.toString());
                        }
                    }

            }
            //idCategorySets.add();
            Set<String> idCategorySetsIncome = new HashSet<>();
            for (int count=0;count< incomeCategoryPairList.size();count++){
                Pair<CheckBoxListData,CheckBoxListData> pair = incomeCategoryPairList.get(count);
                if(pair.first.checkState) {
                    idCategorySetsIncome.add(pair.first.CATEGORY_ID.toString());
                }
                if(pair.second.ifCheckBoxRequired) {
                    if(pair.second.checkState) {
                        idCategorySetsIncome.add(pair.second.CATEGORY_ID.toString());
                    }
                }

            }
            //idCategorySets.add();
            editor.putBoolean(SettingSharedPreferenceKeys.CategorySettingOnOROffKey,true);
            editor.putStringSet(SettingSharedPreferenceKeys.ExpenseCategorySettingOnOROffKey,idCategorySetsExpence);
            editor.putStringSet(SettingSharedPreferenceKeys.IncomeCategorySettingOnOROffKey,idCategorySetsIncome);
        }else{
            editor.remove(SettingSharedPreferenceKeys.ExpenseCategorySettingOnOROffKey);
            editor.remove(SettingSharedPreferenceKeys.IncomeCategorySettingOnOROffKey);
            editor.putBoolean(SettingSharedPreferenceKeys.CategorySettingOnOROffKey,false);

        }

        if(durationSectionTitle.isChecked()) {
            editor.putBoolean(SettingSharedPreferenceKeys.DurationSettingOnOROffKey,true);
            if(titleRadioDurationMontsDayYears.isChecked()){
                editor.putBoolean(SettingSharedPreferenceKeys.FirstOptionDurationSettingOnOROffKey,true);

                editor.putLong(SettingSharedPreferenceKeys.ValueFirstOptionDurationSettingOnOROffKey,Integer.parseInt(duration_day_month_year_value.getText().toString()));
                DurationPeriod durationPeriod;
                if(days_Setting.isChecked()){
                    durationPeriod = DurationPeriod.Days;
                }else if(MonthsSetting.isChecked()){
                    durationPeriod = DurationPeriod.Months;
                }else{
                    durationPeriod = DurationPeriod.Years;
                }
                editor.putInt(SettingSharedPreferenceKeys.PeriodValueFirstOptionDurationSettingOnOROffKey,durationPeriod.getValue());

            }else{
                editor.putBoolean(SettingSharedPreferenceKeys.FirstOptionDurationSettingOnOROffKey,false);
                if(start_date_checkbox.isChecked()){
                    editor.putBoolean(SettingSharedPreferenceKeys.StartDateSelectedSecondOptionDurationSettingOnOROffKey,true);
                    editor.putString(SettingSharedPreferenceKeys.ValueStartDateSelectedSecondOptionDurationSettingOnOROffKey,start_date_edit_text.getText().toString());

                    //TODO validation for the text box

                }else{
                    editor.putBoolean(SettingSharedPreferenceKeys.StartDateSelectedSecondOptionDurationSettingOnOROffKey,false);
                    editor.remove(SettingSharedPreferenceKeys.ValueStartDateSelectedSecondOptionDurationSettingOnOROffKey);

                }

                if(end_date_checkbox.isChecked()){
                    editor.putBoolean(SettingSharedPreferenceKeys.EndDateSelectedSecondOptionDurationSettingOnOROffKey,true);
                    //TODO validation for the text box
                    editor.putString(SettingSharedPreferenceKeys.ValueEndDateSelectedSecondOptionDurationSettingOnOROffKey,end_date_edit_text.getText().toString());


                }else{
                    editor.putBoolean(SettingSharedPreferenceKeys.EndDateSelectedSecondOptionDurationSettingOnOROffKey,false);
                    editor.remove(SettingSharedPreferenceKeys.ValueEndDateSelectedSecondOptionDurationSettingOnOROffKey);


                }

            }



        }
        else{
            editor.putBoolean(SettingSharedPreferenceKeys.DurationSettingOnOROffKey,false);
            editor.remove(SettingSharedPreferenceKeys.FirstOptionDurationSettingOnOROffKey);
            editor.remove(SettingSharedPreferenceKeys.ValueFirstOptionDurationSettingOnOROffKey);
            editor.remove(SettingSharedPreferenceKeys.PeriodValueFirstOptionDurationSettingOnOROffKey);
            editor.remove(SettingSharedPreferenceKeys.StartDateSelectedSecondOptionDurationSettingOnOROffKey);
            editor.remove(SettingSharedPreferenceKeys.ValueStartDateSelectedSecondOptionDurationSettingOnOROffKey);
            editor.remove(SettingSharedPreferenceKeys.EndDateSelectedSecondOptionDurationSettingOnOROffKey);
            editor.remove(SettingSharedPreferenceKeys.ValueEndDateSelectedSecondOptionDurationSettingOnOROffKey);
        }

        if(amountSectionTitle.isChecked()) {
            editor.putBoolean(SettingSharedPreferenceKeys.AmountSettingOnOROffKey,true);
            if(min_amount_checkbox.isChecked()){
                editor.putBoolean(SettingSharedPreferenceKeys.MinSelectedAmountSettingOnOROffKey,true);
                editor.putString(SettingSharedPreferenceKeys.ValueMinSelectedAmountSettingOnOROffKey,min_amount_edit_text.getText().toString());

                //TODO validation for the text box

            }else{
                editor.putBoolean(SettingSharedPreferenceKeys.MinSelectedAmountSettingOnOROffKey,false);
                editor.remove(SettingSharedPreferenceKeys.ValueMinSelectedAmountSettingOnOROffKey);

            }

            if(max_amount_checkbox.isChecked()){
                editor.putBoolean(SettingSharedPreferenceKeys.MaxSelectedAmountSettingOnOROffKey,true);
                //TODO validation for the text box
                editor.putString(SettingSharedPreferenceKeys.ValueMaxSelectedAmountSettingOnOROffKey,max_amount_edit_text.getText().toString());


            }else{
                editor.putBoolean(SettingSharedPreferenceKeys.MaxSelectedAmountSettingOnOROffKey,false);
                editor.remove(SettingSharedPreferenceKeys.ValueMaxSelectedAmountSettingOnOROffKey);


            }


        }else{
            editor.putBoolean(SettingSharedPreferenceKeys.AmountSettingOnOROffKey,false);
            editor.remove(SettingSharedPreferenceKeys.MinSelectedAmountSettingOnOROffKey);
            editor.remove(SettingSharedPreferenceKeys.ValueMinSelectedAmountSettingOnOROffKey);
            editor.remove(SettingSharedPreferenceKeys.MaxSelectedAmountSettingOnOROffKey);
            editor.remove(SettingSharedPreferenceKeys.ValueMaxSelectedAmountSettingOnOROffKey);
        }

        // editor.putStringSet()
        editor.commit();

    }
    void populateDutrationSettingFromSharedPreference(View res) {
        final RadioButton days_Setting = (RadioButton) res.findViewById(R.id.days_Setting);
        final RadioButton MonthsSetting = (RadioButton) res.findViewById(R.id.MonthsSetting);
        final RadioButton years_setting = (RadioButton) res.findViewById(R.id.years_setting);
        final RadioButton titleRadioDurationMontsDayYears = (RadioButton) res.findViewById(R.id.titleRadioDurationMontsDayYears);
        final RadioButton fromAndToDateTextBoxTitle = (RadioButton) res.findViewById(R.id.fromAndToDateTextBoxTitle);

        final EditText duration_day_month_year_value = (EditText) res.findViewById(R.id.duration_day_month_year_value);

        final CheckBox start_date_checkbox = (CheckBox) res.findViewById(R.id.start_date_checkbox);
        final EditText start_date_edit_text = (EditText) res.findViewById(R.id.start_date_edit_text);

        final CheckBox end_date_checkbox = (CheckBox) res.findViewById(R.id.end_date_checkbox);
        final EditText end_date_edit_text = (EditText) res.findViewById(R.id.end_date_edit_text);

        final Switch durationSectionTitle = (Switch) res.findViewById(R.id.durationSectionTitle);

        // Restore preferences
        SharedPreferences settings = getActivity().getSharedPreferences(AppController.MY_APP_PREFERENCE, 0);
        Boolean durationSettingOnOROffFlag = settings.getBoolean(SettingSharedPreferenceKeys.DurationSettingOnOROffKey, false);
        durationSectionTitle.setChecked(durationSettingOnOROffFlag);
        if (durationSettingOnOROffFlag) {

            if (settings.getBoolean(SettingSharedPreferenceKeys.FirstOptionDurationSettingOnOROffKey, false)) {
                titleRadioDurationMontsDayYears.setChecked(true);
                fromAndToDateTextBoxTitle.setChecked(false);
                long duration = settings.getLong(SettingSharedPreferenceKeys.ValueFirstOptionDurationSettingOnOROffKey, 0);


                duration_day_month_year_value.setText(((duration == 0) ? "":duration+""));


                switch (settings.getInt(SettingSharedPreferenceKeys.PeriodValueFirstOptionDurationSettingOnOROffKey, 0)) {
                    case 0:
                        days_Setting.setChecked(true);
                        break;
                    case 1:
                        MonthsSetting.setChecked(true);
                        break;
                    case 2:
                        years_setting.setChecked(true);
                        break;
                }
            } else {
                titleRadioDurationMontsDayYears.setChecked(false);
                fromAndToDateTextBoxTitle.setChecked(true);

                if (settings.getBoolean(SettingSharedPreferenceKeys.StartDateSelectedSecondOptionDurationSettingOnOROffKey, false)){
                    start_date_checkbox.setChecked(true);
                    start_date_edit_text.setText(settings.getString(SettingSharedPreferenceKeys.ValueStartDateSelectedSecondOptionDurationSettingOnOROffKey,""));
                }else{
                    start_date_checkbox.setChecked(false);
                    start_date_edit_text.setText("");
                }

                if (settings.getBoolean(SettingSharedPreferenceKeys.EndDateSelectedSecondOptionDurationSettingOnOROffKey, false)){
                    end_date_checkbox.setChecked(true);
                    end_date_edit_text.setText(settings.getString(SettingSharedPreferenceKeys.ValueEndDateSelectedSecondOptionDurationSettingOnOROffKey,""));
                }else{
                    end_date_checkbox.setChecked(false);
                    end_date_edit_text.setText("");
                }

            }


        } else {

            start_date_checkbox.setChecked(false);
            end_date_checkbox.setChecked(false);
            start_date_edit_text.setText("");
            start_date_edit_text.setEnabled(false);
            end_date_edit_text.setText("");
            end_date_edit_text.setEnabled(false);

        }

        /*if(durationSectionTitle.isChecked()) {
            editor.putBoolean(SettingSharedPreferenceKeys.DurationSettingOnOROffKey,true);
            if(R.id.titleRadioDurationMontsDayYears == radio_group_d_m_y.getCheckedRadioButtonId()){
                editor.putBoolean(SettingSharedPreferenceKeys.FirstOptionDurationSettingOnOROffKey,true);

                editor.putLong(SettingSharedPreferenceKeys.ValueFirstOptionDurationSettingOnOROffKey,Integer.parseInt(duration_day_month_year_value.getText().toString()));
                DurationPeriod durationPeriod;
                if(R.id.days_Setting == radio_group_duration_setting_options.getCheckedRadioButtonId()){
                    durationPeriod = DurationPeriod.Days;
                }else if(R.id.MonthsSetting == radio_group_duration_setting_options.getCheckedRadioButtonId()){
                    durationPeriod = DurationPeriod.Months;
                }else{
                    durationPeriod = DurationPeriod.Years;
                }
                editor.putInt(SettingSharedPreferenceKeys.ValueFirstOptionDurationSettingOnOROffKey,durationPeriod.getValue());

            }else{
                editor.putBoolean(SettingSharedPreferenceKeys.FirstOptionDurationSettingOnOROffKey,false);
                 if(start_date_checkbox.isChecked()){
                    editor.putBoolean(SettingSharedPreferenceKeys.StartDateSelectedSecondOptionDurationSettingOnOROffKey,true);
                    editor.putString(SettingSharedPreferenceKeys.ValueStartDateSelectedSecondOptionDurationSettingOnOROffKey,start_date_edit_text.getText().toString());

                    //TODO validation for the text box

                }else{
                    editor.putBoolean(SettingSharedPreferenceKeys.StartDateSelectedSecondOptionDurationSettingOnOROffKey,false);
                    editor.remove(SettingSharedPreferenceKeys.ValueStartDateSelectedSecondOptionDurationSettingOnOROffKey);

                }

                if(end_date_checkbox.isChecked()){
                    editor.putBoolean(SettingSharedPreferenceKeys.EndDateSelectedSecondOptionDurationSettingOnOROffKey,true);
                    //TODO validation for the text box
                    editor.putString(SettingSharedPreferenceKeys.ValueEndDateSelectedSecondOptionDurationSettingOnOROffKey,end_date_edit_text.getText().toString());


                }else{
                    editor.putBoolean(SettingSharedPreferenceKeys.EndDateSelectedSecondOptionDurationSettingOnOROffKey,false);
                    editor.remove(SettingSharedPreferenceKeys.ValueEndDateSelectedSecondOptionDurationSettingOnOROffKey);


                }

            }*/
    }
    /*
    *
    *   ExpenseCategorySettingOnOROffKey = "ExpenseCategorySettingOnOROffKey";
     IncomeCategorySettingOnOROffKey = "IncomeCategorySettingOnOROffKey";
     FirstOptionDurationSettingOnOROffKey = "FirstOptionDurationSettingOnOROffKey";
     ValueFirstOptionDurationSettingOnOROffKey = "ValueFirstOptionDurationSettingOnOROffKey";
     PeriodValueFirstOptionDurationSettingOnOROffKey = "PeriodValueFirstOptionDurationSettingOnOROffKey";
     StartDateSelectedSecondOptionDurationSettingOnOROffKey = "StartDateSelectedSecondOptionDurationSettingOnOROffKey";
     ValueStartDateSelectedSecondOptionDurationSettingOnOROffKey = "ValueStartDateSelectedSecondOptionDurationSettingOnOROffKey";
     EndDateSelectedSecondOptionDurationSettingOnOROffKey = "EndDateSelectedSecondOptionDurationSettingOnOROffKey";
     ValueEndDateSelectedSecondOptionDurationSettingOnOROffKey = "ValueEndDateSelectedSecondOptionDurationSettingOnOROffKey";
     MinSelectedAmountSettingOnOROffKey = "MinSelectedAmountSettingOnOROffKey";
     MaxSelectedAmountSettingOnOROffKey = "MaxSelectedAmountSettingOnOROffKey";
     ValueMinSelectedAmountSettingOnOROffKey = "ValueMinSelectedAmountSettingOnOROffKey";
     ValueMaxSelectedAmountSettingOnOROffKey = "ValueMaxSelectedAmountSettingOnOROffKey";
    *
    *
    *
    * */

    void resetAmountSetting(View res){
        final CheckBox min_amount_checkbox = (CheckBox)res.findViewById(R.id.min_amount_checkbox);


        final CheckBox max_amount_checkbox = (CheckBox)res.findViewById(R.id.max_amount_checkbox);
        final EditText max_amount_edit_text =(EditText)res.findViewById(R.id.max_amount_edit_text);
        final EditText min_amount_edit_text =(EditText)res.findViewById(R.id.min_amount_edit_text);
        min_amount_checkbox.setChecked(false);
        max_amount_checkbox.setChecked(false);
        min_amount_edit_text.setText("");
        min_amount_edit_text.setEnabled(false);
        max_amount_edit_text.setText("");
        max_amount_edit_text.setEnabled(false);

    }
    void resetDutrationSetting(View res){

        final RadioButton days_Setting = (RadioButton)res.findViewById(R.id.days_Setting);
        final RadioButton titleRadioDurationMontsDayYears = (RadioButton)res.findViewById(R.id.titleRadioDurationMontsDayYears);

        final EditText duration_day_month_year_value  = (EditText)res.findViewById(R.id.duration_day_month_year_value);
        final CheckBox start_date_checkbox = (CheckBox)res.findViewById(R.id.start_date_checkbox);
        final EditText start_date_edit_text =(EditText)res.findViewById(R.id.start_date_edit_text);

        final CheckBox end_date_checkbox = (CheckBox)res.findViewById(R.id.end_date_checkbox);
        final EditText end_date_edit_text =(EditText)res.findViewById(R.id.end_date_edit_text);
        start_date_checkbox.setChecked(false);
        end_date_checkbox.setChecked(false);
        start_date_edit_text.setText("");
        start_date_edit_text.setEnabled(false);
        end_date_edit_text.setText("");
        end_date_edit_text.setEnabled(false);
        duration_day_month_year_value.setText("");
        titleRadioDurationMontsDayYears.setChecked(true);
        days_Setting.setChecked(true);



    }




    void categoryListForBothIncomeAndExpenseReload(){
        Boolean state = true;
        final ListView checkBoxListForCategoryListing = (ListView)getView().findViewById(R.id.checkBoxListForCategoryListing);


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
    } }
