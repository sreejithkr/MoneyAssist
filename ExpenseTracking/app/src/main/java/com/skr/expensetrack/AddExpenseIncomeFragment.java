package com.skr.expensetrack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.skr.AppController;
import com.skr.customviews.CustomAlert;
import com.skr.customviews.CustomProgressDialog;
import com.skr.datahelper.Category;
import com.skr.datahelper.DBHelper;
import com.skr.datahelper.ExpenseIncome;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddExpenseIncomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddExpenseIncomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddExpenseIncomeFragment extends Fragment {
    public static final String ExpenceIncomeToBeUpdatedStatus =  "ExpenceIncomeToBeUpdatedStatus";
    public static final String ExpenceIncomeToBeUpdated =  "ExpenceIncomeToBeUpdated";
    public static final String DonotShowNoDescriptionFlag =  "DonotShowNoDescriptionFlag";
    public static final String ExpenseFlagKey = "ExpenseFlagKey";
   // public Boolean ifExpenceToBeUpdated = false;
    HashMap <String,Integer> incomeCategory ;
    HashMap <String,Integer> expenseCategory ;
    List<String> incomeCategoryArray  ;
    List<String>  expenseCategoryArray ;
    String spinnerSelectedItem;
    private CustomProgressDialog progress;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public Boolean expenseFlag = true;
    public Boolean isToEditFlag = false;
    public ExpenseIncome expenseIncome;
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
     * @return A new instance of fragment AddExpenseIncomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddExpenseIncomeFragment newInstance(String param1, String param2) {
        AddExpenseIncomeFragment fragment = new AddExpenseIncomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AddExpenseIncomeFragment() {
        // Required empty public constructor
    }

    public void reloadCategoryValues(){

        if(this.isToEditFlag){


        }else{


        }

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
        // Inflate the layout for this fragment
        if (mListener != null) {
            mListener.onCreatedFragment();
        }
        progress =  CustomProgressDialog.getInstance(getActivity());
        progress.setMessage(getResources().getString(R.string.wait_message));
        progress.show();
    final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {

                DBHelper dbHelper = DBHelper.getInstance(getActivity());
                ArrayList<Category> categories = dbHelper.getAllCategory();
                incomeCategory = new HashMap<>();
                expenseCategory = new HashMap<>();
                incomeCategoryArray = new ArrayList<>();
                expenseCategoryArray = new ArrayList<>();
                for(int count = 0;count<categories.size();count++){
                    Category category = categories.get(count);

                    if(category.getIFEXPENSE()){
                        expenseCategory.put(category.getCATEGORY_NAME(),category.getCATEGORY_ID());
                        expenseCategoryArray.add(category.getCATEGORY_NAME());
                    }else{
                        incomeCategory.put(category.getCATEGORY_NAME(),category.getCATEGORY_ID());
                        incomeCategoryArray.add(category.getCATEGORY_NAME());
                    }
                }
                Log.e("((((((((((((+++++++++++++++))))))))))))",incomeCategoryArray.size()+"");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                    }
                });
            }
        }).start();


        final View rootView = inflater.inflate(R.layout.fragment_add_expense_income, container, false);


        rootView.findViewById(R.id.add_category_button_AEI).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.addCategoryButtonCLicked();
                }
            }
        });
        final Spinner spinner = (Spinner) rootView.findViewById(R.id.editTextNameAddIncomeExpense);
        spinner.setSelection(0);

        // Creating adapter for spinner
        final ArrayAdapter<String> dataAdapterIncome = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, incomeCategoryArray);
        dataAdapterIncome
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter<String> dataAdapterExpense = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, expenseCategoryArray);
        dataAdapterExpense
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner

        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        final EditText editText = (EditText) rootView.findViewById(R.id.editTextDateAddIncomeExpense);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                enableOrDisableTextFiled(false);
                final DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
                newFragment.setDatePickerFragmentListener(new DatePickerFragment.DatePickerFragmentListener(){
                    public void onCancel(){
                        enableOrDisableTextFiled(true);
                        newFragment.getDialog().dismiss();
                    }
                    public void onDateSet(DatePicker view, int year, int month, int day ){
                        enableOrDisableTextFiled(true);
                        final EditText editTextDateAddIncomeExpense = (EditText) getView().findViewById(R.id.editTextDateAddIncomeExpense);

                        // Do something with the date chosen by the user
                        String dateString = day+"-"+(month+1)+"-"+year;

                        //"MMMM d, yyyy"
                        Date date = null;
                        DateFormat format = new SimpleDateFormat(AppController.DateDashMonthDashYearFormat, Locale.ENGLISH);
                        try {

                            date = format.parse(dateString);

                        }catch(Exception e){

                        }
                        format = new SimpleDateFormat(AppController.MonthSpaceDateSpaceYearFormat, Locale.ENGLISH);
                        editTextDateAddIncomeExpense.setText(format.format(date));
                        newFragment.getDialog().dismiss();

                    }
                });

                return false;
            }
        });

        final RadioButton radioExpense = (RadioButton)rootView.findViewById(R.id.radio_expense);
        final RadioButton radioIncome = (RadioButton)rootView.findViewById(R.id.radio_income);
        final CheckBox investmentAddIncomeExpense = (CheckBox)rootView.findViewById(R.id.investmentAddIncomeExpense);

        radioExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataAdapterExpense
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                spinner.setAdapter(dataAdapterExpense);

                radioIncome.setChecked(false);
            }
        });

        radioIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Drop down layout style - list view with radio button
                dataAdapterIncome
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                spinner.setAdapter(dataAdapterIncome);
                radioExpense.setChecked(false);
            }
        });
        Button buttonAdd = (Button)rootView.findViewById(R.id.addToDb);
        final EditText editTextAmountAddIncomeExpense = (EditText) rootView.findViewById(R.id.editTextAmountAddIncomeExpense);

        if(this.isToEditFlag){
            final EditText editTextDescriptionAddIncomeExpense = (EditText) rootView.findViewById(R.id.editTextDescriptionAddIncomeExpense);

            radioExpense.setChecked(expenseIncome.getIF_EXPENSE());
            radioIncome.setChecked(!expenseIncome.getIF_EXPENSE());
            investmentAddIncomeExpense.setChecked(expenseIncome.getIS_AN_INVESTMENT());
            Date date = expenseIncome.getDateFromDateString();
            DateFormat format = new SimpleDateFormat(AppController.MonthSpaceDateSpaceYearFormat, Locale.ENGLISH);
            editText.setText(format.format(date));
            editTextDescriptionAddIncomeExpense.setText(expenseIncome.getDESCRIPTION());
            editTextAmountAddIncomeExpense.setText(expenseIncome.getAMOUNTWithCurrency().toString());
            Integer position = 0;
            if(expenseIncome.getIF_EXPENSE()) {
                String selectedCategory = null;
                for (Map.Entry<String, Integer> e : expenseCategory.entrySet()) {
                    String key = e.getKey();
                    if(expenseIncome.getCATEGORY_ID() == e.getValue()){
                        selectedCategory = key;
                    }
                }
                if(selectedCategory != null) {
                    position = expenseCategoryArray.indexOf(selectedCategory);
                }
                dataAdapterExpense
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapterExpense);
            }else {
                String selectedCategory = null;
                for (Map.Entry<String, Integer> e : incomeCategory.entrySet()) {
                    String key = e.getKey();
                    if(expenseIncome.getCATEGORY_ID() == e.getValue()){
                        selectedCategory = key;
                    }
                }
                if(selectedCategory != null) {
                    position = incomeCategoryArray.indexOf(selectedCategory);
                }
                // Drop down layout style - list view with radio button
                dataAdapterIncome
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                spinner.setAdapter(dataAdapterIncome);
            }
            spinner.setSelection(position);
          //  buttonAdd.setVisibility(View.GONE);
        }else{

            if(expenseFlag) {

                dataAdapterExpense
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                spinner.setAdapter(dataAdapterExpense);

                radioIncome.setChecked(false);
                radioExpense.setChecked(true);
            }else{
                // Drop down layout style - list view with radio button
                dataAdapterIncome
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                spinner.setAdapter(dataAdapterIncome);
                radioExpense.setChecked(false);
                spinner.setAdapter(dataAdapterIncome);
                radioIncome.setChecked(true);
            }

        }



        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*TODO VALIDATION NEED TO BE PERFORMED
                *    Integer EXPENCE_INCOME_ID  ;
    Integer CATEGORY_ID ;
    Integer AMOUNT ;
    String DESCRIPTION ;
    Boolean IF_EXPENSE ;
    Boolean IS_AN_INVESTMENT;
    String dateString;
                * */
                final EditText editTextAmountAddIncomeExpense = (EditText) rootView.findViewById(R.id.editTextAmountAddIncomeExpense);
                final EditText editTextDescriptionAddIncomeExpense = (EditText) rootView.findViewById(R.id.editTextDescriptionAddIncomeExpense);
                String amountString  =  editTextAmountAddIncomeExpense.getText().toString();
                amountString =  amountString.replace(AppController.getInstance().getCurrencyString().trim(),"");
                amountString = amountString.trim();
                if(amountString == null ||amountString.equalsIgnoreCase("")){
                    validationAlert(getResources().getString(R.string.validation_msg_add_edit_expence_income_amount));
                    return;
                }

                try{
                    Integer valueAmount = Integer.parseInt(amountString);
                    if(valueAmount >10000000){
                        validationAlert(getResources().getString(R.string.validation_msg_add_edit_expence_income_amount_limit));
                        return;
                    }
                }catch(Exception e){
                    validationAlert(getResources().getString(R.string.validation_msg_add_edit_expence_income_amount_limit));
                    return;
                }
                final EditText editTextDateAddIncomeExpense = (EditText) rootView.findViewById(R.id.editTextDateAddIncomeExpense);
                String dateString = editTextDateAddIncomeExpense.getText().toString();
                if(dateString == null ||dateString.equalsIgnoreCase("")){
                    validationAlert(getResources().getString(R.string.validation_msg_add_edit_expence_income_date));
                    return;
                }
                String descString  =  editTextDescriptionAddIncomeExpense.getText().toString();
                if(descString == null ||descString.equalsIgnoreCase("")){

                    final SharedPreferences settings = getActivity().getSharedPreferences(AppController.MY_APP_PREFERENCE, 0);
                    if(settings.getBoolean(DonotShowNoDescriptionFlag,false)){
                        editTextDescriptionAddIncomeExpense.setText(getString(R.string.no_desc));
                        progress.show();
                        onAddButtonPressedSave(rootView, radioIncome, handler);
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    // Get the layout inflater
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    final View view = inflater.inflate(R.layout.dialog_add_expence_income_without_desc_confirmation, null);
                    final TextView error_message_dialog = (TextView)view.findViewById(R.id.error_message_dialog);
                    final CheckBox donotShowCheckBox = (CheckBox)view.findViewById(R.id.donotShowCheckBox);

                    error_message_dialog.setText(getString(R.string.validation_msg_add_edit_expence_income_description));
                    builder.setView(view)
                            // Add action buttons
                            .setPositiveButton(R.string.continue_button_String, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    if (donotShowCheckBox.isChecked()) {

                                        SharedPreferences.Editor editor = settings.edit();

                                        editor.putBoolean(DonotShowNoDescriptionFlag, true);
                                        editor.commit();
                                    }
                                    editTextDescriptionAddIncomeExpense.setText(getString(R.string.no_desc));
                                    progress.show();
                                    onAddButtonPressedSave(rootView, radioIncome, handler);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    final AlertDialog dialog = builder.create();

                    dialog.show();

                    return;
                }
                progress.show();
                onAddButtonPressedSave(rootView, radioIncome, handler);
            }
        });
        return rootView;
    }


    private void onAddButtonPressedSave(View rootView,RadioButton radioIncome,final Handler handler){
        final EditText editTextAmountAddIncomeExpense = (EditText) rootView.findViewById(R.id.editTextAmountAddIncomeExpense);
        final EditText editTextDescriptionAddIncomeExpense = (EditText) rootView.findViewById(R.id.editTextDescriptionAddIncomeExpense);
        final EditText editTextDateAddIncomeExpense = (EditText) rootView.findViewById(R.id.editTextDateAddIncomeExpense);
        final CheckBox investmentAddIncomeExpense = (CheckBox) rootView.findViewById(R.id.investmentAddIncomeExpense);

        Integer EXPENCE_INCOME_ID = -1  ;
        Integer CATEGORY_ID ;
        Integer AMOUNT ;
        String DESCRIPTION ;
        Boolean IF_EXPENSE ;
        Boolean IS_AN_INVESTMENT;
        String dateString;
        if(radioIncome.isChecked()){
            IF_EXPENSE = false;
            CATEGORY_ID = incomeCategory.get(spinnerSelectedItem);
        }else{
            IF_EXPENSE = true;
            CATEGORY_ID = expenseCategory.get(spinnerSelectedItem);
        }
        String amountString  =  editTextAmountAddIncomeExpense.getText().toString();
        amountString =  amountString.replace(AppController.getInstance().getCurrencyString().trim(),"");
        amountString = amountString.trim();

        AMOUNT = Integer.parseInt(amountString);
        DESCRIPTION =  editTextDescriptionAddIncomeExpense.getText().toString();
        IS_AN_INVESTMENT = investmentAddIncomeExpense.isChecked();
        dateString = ExpenseIncome.getDateStringInMonthSpaceDateSpaceYearFormatFromString(editTextDateAddIncomeExpense.getText().toString());
        final ExpenseIncome expenseIncomeNew = new ExpenseIncome(EXPENCE_INCOME_ID,CATEGORY_ID,AMOUNT,DESCRIPTION,IF_EXPENSE,IS_AN_INVESTMENT,dateString);



        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper dbHelper = DBHelper.getInstance(getActivity());
                if(isToEditFlag){
                    //TODO VALIDATION oldExpenceIncome should not be null
                    dbHelper.updateExpenseIncome(expenseIncome,expenseIncomeNew);

                }else {
                    dbHelper.addExpenseIncome(expenseIncomeNew);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(progress.isShowing()){
                            progress.dismiss();
                        }

                        String msg = null;
                        if(isToEditFlag){

                            msg = getResources().getString(R.string.success_msg_edit_expence_income);
                        }else{
                            resetTheValue();
                            msg = getResources().getString(R.string.success_msg_add_expence_income);
                        }

                        new CustomAlert.CustomBuilder(getActivity(),getActivity().getLayoutInflater())
                                .setTitle(R.string.success_title_add_edit_expence_income)
                                .setMessage(msg)
                                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {


                                        // do nothing
                                    }
                                }).setPositiveButton(R.string.return_to_list,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
if(mListener != null){
    mListener.onClickedReturnExpense(expenseIncomeNew.getIF_EXPENSE());
}
                                //TODO: impleme retirn to the list

                            }
                        }).setIcon(R.drawable.success_icon)
                                .show();
                    }
                });
            }
        }).start();
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
        public void onCreatedFragment();
        public void onClickedReturnExpense(Boolean ifExpense);
        public void addCategoryButtonCLicked();
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

    public void enableOrDisableTextFiled(Boolean flag){
        final EditText editTextAmountAddIncomeExpense = ((EditText)getView().findViewById(R.id.editTextAmountAddIncomeExpense));
        final EditText editTextDateAddIncomeExpense = (EditText) getView().findViewById(R.id.editTextDateAddIncomeExpense);
        editTextAmountAddIncomeExpense.setEnabled(flag);
        editTextDateAddIncomeExpense.setEnabled(flag);


    }

    public void validationAlert(String msg){

    new CustomAlert.CustomBuilder(getActivity(),getActivity().getLayoutInflater()).setTitle(R.string.info)
                .setMessage(msg).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        // do nothing
                    }
                }).setIcon(R.drawable.error_info)
                .show();
    }

    public void resetTheValue(){
        View rootView = getView();
        final EditText editTextAmountAddIncomeExpense = (EditText) rootView.findViewById(R.id.editTextAmountAddIncomeExpense);
        final EditText editTextDescriptionAddIncomeExpense = (EditText) rootView.findViewById(R.id.editTextDescriptionAddIncomeExpense);
        final EditText editTextDateAddIncomeExpense = (EditText) rootView.findViewById(R.id.editTextDateAddIncomeExpense);
        final Spinner spinner = (Spinner) rootView.findViewById(R.id.editTextNameAddIncomeExpense);
        editTextAmountAddIncomeExpense.setText("");
        editTextDateAddIncomeExpense.setText("");
        editTextDescriptionAddIncomeExpense.setText("");
        spinner.setSelection(0);
    }

    public void onCreateDialog(String message) {



    }

}
