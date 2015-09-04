package com.skr.expensetrack;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skr.AppController;
import com.skr.customviews.CustomAlert;
import com.skr.customviews.CustomProgressDialog;
import com.skr.datahelper.DBHelper;
import com.skr.datahelper.ExpenseIncome;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListExpenceIncomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListExpenceIncomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListExpenceIncomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String expenseIncome = "expenseIncome";
    public static final String categoryName = "categoryName";
    private CustomProgressDialog progress;
    ArrayList<ExpenseIncome> expenseIncomesOriginal;
    ArrayList<ExpenseIncome> expenseIncomes;
    HashMap<Integer,String> categoryIdName;
    private ExpenseIncome positionDeleted;
    TextView nodataText;
    String nodataTextString;
    Boolean groupByDateFlag = false;

public Boolean isExpenseFlag = true;
    public Boolean searchButtonIsCollapsed = true;
    private Boolean isSearchViewHidden = true;
    ListView listView;
    LinearLayout cancelDeleteParentFLEI;
     RelativeLayout searchViewParent;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public Boolean hideRefreshIcon = true;

    public OnFragmentInteractionListener getmListener() {
        return mListener;
    }

    public void setmListener(OnFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListExpenceIncomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListExpenceIncomeFragment newInstance(String param1, String param2) {
        ListExpenceIncomeFragment fragment = new ListExpenceIncomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ListExpenceIncomeFragment() {
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
        // Inflate the layout for this fragment
        if (mListener != null) {
            mListener.onCreatedFragment();
        }
        AppController.getInstance().listExpenseIncomeItemClicked = false;

        progress =  CustomProgressDialog.getInstance(getActivity());
        progress.setMessage(getResources().getString(R.string.wait_message));
        final Handler handler = new Handler();


       final View rootView = (View)inflater.inflate(R.layout.fragment_list_expence_income, container, false);

        rootView.findViewById(R.id.addExpenceIncomeHomeParent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.addExpenceIncome();
                }

            }
        });
        rootView.findViewById(R.id.addExpenceIncomeHomeImgView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.addExpenceIncome();
                }
            }
        });
        rootView.findViewById(R.id.addExpenceIncomeHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.addExpenceIncome();
                }
            }
        });
        cancelDeleteParentFLEI = (LinearLayout)rootView.findViewById(R.id.cancelDeleteParentFLEI);
        cancelDeleteParentFLEI.setVisibility(View.GONE);

        cancelDeleteParentFLEI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ExpenceIncomeListAdapter)listView.getAdapter()).reloadDataForDeleteStatus(false);
                getActivity().invalidateOptionsMenu();
            }
        });
        rootView.findViewById(R.id.cancelDeleteButtonFLEI).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ExpenceIncomeListAdapter)listView.getAdapter()).reloadDataForDeleteStatus(false);
                getActivity().invalidateOptionsMenu();
            }
        });
        rootView.findViewById(R.id.cancelDeleteImgViewFLEI).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ExpenceIncomeListAdapter)listView.getAdapter()).reloadDataForDeleteStatus(false);
                getActivity().invalidateOptionsMenu();
            }
        });
        nodataText = (TextView)rootView.findViewById(R.id.nodataText);
          searchViewParent = (RelativeLayout)rootView.findViewById(R.id.searchViewParent);
      //  final Button search_drag = (Button)rootView.findViewById(R.id.search_drag_button);
        final EditText editTextSearchItems = (EditText)rootView.findViewById(R.id.editTextSearchItems);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) searchViewParent.getLayoutParams();
        final float scale = getActivity().getResources().getDisplayMetrics().density;
        editTextSearchItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((ExpenceIncomeListAdapter)listView.getAdapter()).filter(editTextSearchItems.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // convert the DP into pixel
//        final int minWidth =  (int)(50 * scale + 0.5f);
//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        final int height = displaymetrics.heightPixels;
//        final int width = displaymetrics.widthPixels;
//        layoutParams.leftMargin = width - minWidth;
      //  Log.e("rootView.getWidth() - 50;", "" + minWidth);
   //     searchViewParent.setLayoutParams(layoutParams);
    //    searchViewParent.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//        search_drag.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) searchViewParent.getLayoutParams();
//                if(searchButtonIsCollapsed) {
//                    mParams.leftMargin = 10;
//                    search_drag.setBackgroundDrawable(getResources().getDrawable(R.drawable.search_drag_in));
//                    searchViewParent.setBackgroundColor(getResources().getColor(R.color.backgroud_white));
//                }else{
//                    mParams.leftMargin = width - minWidth;
//                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(editTextSearchItems.getWindowToken(), 0);
//                    editTextSearchItems.setText("");
//                    editTextSearchItems.clearFocus();
//                    ((ExpenceIncomeListAdapter)listView.getAdapter()).filter("");
//                    searchViewParent.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//                    search_drag.setBackgroundDrawable(getResources().getDrawable(R.drawable.search_drag_out));
//                }
//                searchViewParent.setLayoutParams(mParams);
//                searchButtonIsCollapsed = !searchButtonIsCollapsed;
//            }
//        });
                listView = (ListView)rootView.findViewById(R.id.expenseOrIncomeList);
                listView.setEmptyView(rootView.findViewById(R.id.nodataParentLEIF));


                progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper dbHelper = DBHelper.getInstance(getActivity());
                if(isExpenseFlag) {
                    nodataTextString = getString(R.string.no_data_expense_income_message).replace(AppController.ei,getString(R.string.expense));


                    expenseIncomes = dbHelper.getAllExpense();
                    categoryIdName =dbHelper.getAllExpenceCategory();
                }else{
                    nodataTextString = getString(R.string.no_data_expense_income_message).replace(AppController.ei,getString(R.string.income));

                    expenseIncomes = dbHelper.getAllIncome();
                    categoryIdName =dbHelper.getAllIncomeCategory();
                }

                Collections.sort(expenseIncomes);
                expenseIncomesOriginal = new ArrayList<>();
                expenseIncomesOriginal.addAll(expenseIncomes);
               // final ListView listView = (ListView) getView().findViewById(R.id.expenseOrIncomeList);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        nodataText.setText(nodataTextString);
                        if(expenseIncomesOriginal.size()<=0){

                        if(mListener != null){
                            mListener.invalidateMenu(true);
                            searchViewParent.setVisibility(View.GONE);
                        }
                        }else{
                            if(mListener != null){
                                mListener.invalidateMenu(false);
                                searchViewParent.setVisibility(View.VISIBLE);
                            }

                        }
                        listView.setAdapter(new ExpenceIncomeListAdapter(new ArrayList<Pair<String, ArrayList<ExpenseIncome>>>()));
                        resetAndGroupByDate();
                    }
                });

            }
        }).start();

        return rootView;
    }

    public Boolean toggleDelete(){
        final ListView listView = (ListView) getView().findViewById(R.id.expenseOrIncomeList);
        ExpenceIncomeListAdapter expenceIncomeListAdapter = (ExpenceIncomeListAdapter)listView.getAdapter();
        expenceIncomeListAdapter.reloadDataForDeleteStatus(!expenceIncomeListAdapter.deleteFlag);
        return expenceIncomeListAdapter.deleteFlag;
    }
    public void cancelDelete(){
        final ListView listView = (ListView) getView().findViewById(R.id.expenseOrIncomeList);
        ExpenceIncomeListAdapter expenceIncomeListAdapter = (ExpenceIncomeListAdapter)listView.getAdapter();
        expenceIncomeListAdapter.reloadDataForDeleteStatus(false);
    }

    public void showAlertForDeletedExpenceIncome(final  ExpenseIncome expenseIncomeFunctionParam){
        String dilogHeading = getResources().getString(R.string.delete_dialog_title_message);
        String confirmDilogMessage = getResources().getString(R.string.delete_dialog_description_message);



        new CustomAlert.CustomBuilder(getActivity(),getActivity().getLayoutInflater(),dilogHeading)
                .setTitle(dilogHeading)
                .setMessage(confirmDilogMessage).setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AppController.getInstance().listExpenseIncomeItemClicked = false;

                DBHelper dbHelper = DBHelper.getInstance(getActivity());
                showAlert(dbHelper.deleteExpenseIncome(expenseIncomeFunctionParam) != 0);
                // do nothing
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
                AppController.getInstance().listExpenseIncomeItemClicked = false;


            }
        }).setIcon(R.drawable.delete_alert)
                .show();


    }
    public void showAlert(final Boolean sucessFlag){

        String confirmDilogMessage = sucessFlag ? getResources().getString(R.string.delete_dialog_des_sucess_message) :getResources().getString(R.string.delete_dialog_des_failed_message);



        new CustomAlert.CustomBuilder(getActivity(),getActivity().getLayoutInflater(),getString(R.string.info))
                .setTitle(R.string.info)
                .setMessage(confirmDilogMessage).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (sucessFlag) {
                    expenseIncomesOriginal.remove(positionDeleted);
                    if (expenseIncomesOriginal.size() <= 0) {
                        searchViewParent.setVisibility(View.GONE);
                    }
                    resetAndGroupByDate();
                    //groupByDate();

                }

                // do nothing
            }
        }).setIcon(R.drawable.delete_alert)
                .show();


    }
    public void groupByCategory(){
        cancelDelete();
        final ListView listView = (ListView) getView().findViewById(R.id.expenseOrIncomeList);
        ExpenceIncomeListAdapter expenceIncomeListAdapter = (ExpenceIncomeListAdapter)listView.getAdapter();

        expenceIncomeListAdapter.ifOrderedByCategoryFlag = true;
        final ArrayList<Pair<String, ArrayList<ExpenseIncome>>> all = new ArrayList<>();
        HashMap<Integer,ArrayList<ExpenseIncome>> temp = new HashMap<>();
        for(int count =0;count<expenseIncomes.size();count++){
            ExpenseIncome expenseIncomeLocal = expenseIncomes.get(count);
            if(temp.containsKey(expenseIncomeLocal.getCATEGORY_ID())){
                temp.get(expenseIncomeLocal.getCATEGORY_ID()).add(expenseIncomeLocal);
            }else{
                temp.put(expenseIncomeLocal.getCATEGORY_ID(),new ArrayList<ExpenseIncome>());
                temp.get(expenseIncomeLocal.getCATEGORY_ID()).add(expenseIncomeLocal);
            }

        }

        for (Map.Entry<Integer, ArrayList<ExpenseIncome>> entry : temp.entrySet())
        {
            Integer key = entry.getKey();
            all.add(new Pair<>(categoryIdName.get(key),entry.getValue()));
        }
        expenceIncomeListAdapter.reloadData(all);
        groupByDateFlag = false;
        hideRefreshIcon = false;
        getActivity().invalidateOptionsMenu();

    }
    public void groupByMonths(){
        cancelDelete();
        final ListView listView = (ListView) getView().findViewById(R.id.expenseOrIncomeList);
        ExpenceIncomeListAdapter expenceIncomeListAdapter = (ExpenceIncomeListAdapter)listView.getAdapter();

        expenceIncomeListAdapter.ifOrderedByCategoryFlag = false;
        final ArrayList<Pair<String, ArrayList<ExpenseIncome>>> all = new ArrayList<>();
        HashMap<String,ArrayList<ExpenseIncome>> temp = new HashMap<>();
        for(int count =0;count<expenseIncomes.size();count++){
            ExpenseIncome expenseIncomeLocal = expenseIncomes.get(count);
            if(temp.containsKey(expenseIncomeLocal.getMonthYearString())){
                temp.get(expenseIncomeLocal.getMonthYearString()).add(expenseIncomeLocal);
            }else{
                temp.put(expenseIncomeLocal.getMonthYearString(),new ArrayList<ExpenseIncome>());
                temp.get(expenseIncomeLocal.getMonthYearString()).add(expenseIncomeLocal);
            }
        }
        for (Map.Entry<String, ArrayList<ExpenseIncome>> entry : temp.entrySet())
        {
            String key = entry.getKey();
            all.add(new Pair<String, ArrayList<ExpenseIncome>>(key,entry.getValue()));
        }
        expenceIncomeListAdapter.reloadData(all);
        groupByDateFlag = false;
        hideRefreshIcon = false;
        getActivity().invalidateOptionsMenu();
    }
    public void resetAndGroupByDate(){
        groupByDateFlag = true;
        expenseIncomes = new ArrayList<>();
        expenseIncomes.addAll(expenseIncomesOriginal);
        groupByDate();
    }
    public void groupByDate(){
        cancelDelete();
        final ListView listView = (ListView) getView().findViewById(R.id.expenseOrIncomeList);
        ExpenceIncomeListAdapter expenceIncomeListAdapter = (ExpenceIncomeListAdapter)listView.getAdapter();
        final ArrayList<Pair<String, ArrayList<ExpenseIncome>>> all = new ArrayList<>();

        expenceIncomeListAdapter.ifOrderedByCategoryFlag = false;
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(AppController.DateDashMonthDashYearFormat);

       String fortmattedDateString =  sdf.format(currentDate);
        Pair<String, ArrayList<ExpenseIncome>> future = new Pair<>(getResources().getString(R.string.future),new ArrayList<ExpenseIncome>());
        Pair<String, ArrayList<ExpenseIncome>> today = new Pair<>(getResources().getString(R.string.today),new ArrayList<ExpenseIncome>());
        Pair<String, ArrayList<ExpenseIncome>> withinAMonth = new Pair<>(getResources().getString(R.string.within_a_month),new ArrayList<ExpenseIncome>());
        Pair<String, ArrayList<ExpenseIncome>> withinAWeek = new Pair<>(getResources().getString(R.string.within_a_week),new ArrayList<ExpenseIncome>());
        Pair<String, ArrayList<ExpenseIncome>> older = new Pair<>(getResources().getString(R.string.older),new ArrayList<ExpenseIncome>());
        for(int count =0;count<expenseIncomes.size();count++) {
            ExpenseIncome expenceIncomeLcl = expenseIncomes.get(count);
            if (AppController.compareTwoDateString(fortmattedDateString,expenceIncomeLcl.getDateString())==0){
                today.second.add(expenceIncomeLcl);
            }else if(AppController.compareTwoDateString(fortmattedDateString,expenceIncomeLcl.getDateString())==-1){
                future.second.add(expenceIncomeLcl);
            }else if(AppController.getDifferenceDaysFromDateString(fortmattedDateString,expenceIncomeLcl.getDateString())<=7){
                withinAWeek.second.add(expenceIncomeLcl);
            }else if(AppController.getDifferenceDaysFromDateString(fortmattedDateString,expenceIncomeLcl.getDateString())<=30){
                withinAMonth.second.add(expenceIncomeLcl);
            }else{
                older.second.add(expenceIncomeLcl);
            }
        }
        if(future.second.size()>0){
            all.add(future);
        }
        if(today.second.size()>0){
            all.add(today);
        }
        if(withinAWeek.second.size()>0){
            all.add(withinAWeek);
        }
        if(withinAMonth.second.size()>0){
            all.add(withinAMonth);
        }
        if(older.second.size()>0){
            all.add(older);
        }
        expenceIncomeListAdapter.reloadData(all);
        hideRefreshIcon = true;
        getActivity().invalidateOptionsMenu();
    }

    public void filterWithParameters(Intent data){
        ArrayList<Integer>checkBoxListCateGoryIDArrayList = data.getIntegerArrayListExtra(FilterAndViewExpenseIncomeActivity.returned_filter_category_id_datas);
        String start_date_edit_text = data.getStringExtra(FilterAndViewExpenseIncomeActivity.start_date_edit_text_key);
        String end_date_edit_text = data.getStringExtra(FilterAndViewExpenseIncomeActivity.end_date_edit_text_key);
        String min_amt_edit_text = data.getStringExtra(FilterAndViewExpenseIncomeActivity.min_amt_edit_text_key);
        String max_amt_edit_text = data.getStringExtra(FilterAndViewExpenseIncomeActivity.max_amt_edit_text_key);
        if(!start_date_edit_text.isEmpty() && !end_date_edit_text.isEmpty() ){
            if(AppController.compareTwoDateStringInMMM_DD_YYYY(start_date_edit_text, end_date_edit_text) > 0){
                String tempStringA = start_date_edit_text;
                start_date_edit_text = end_date_edit_text;
                end_date_edit_text = tempStringA;

            }
        }
        ArrayList<ExpenseIncome> tempExpenceIncomeList = new ArrayList<>();
        for(int count =0;count<expenseIncomesOriginal.size();count++) {
            ExpenseIncome expenceIncomeLcl = expenseIncomesOriginal.get(count);
            Boolean ifGreaterThanStartDate = true;
            Boolean ifSmallerThanStartDate = true;
            Boolean ifGreaterThanMinAmount= true;
            Boolean ifSmallerThanMaxAmount = true;
            if(start_date_edit_text != null) {
                if(!start_date_edit_text.isEmpty()){
                    ifGreaterThanStartDate = (AppController.compareTwoDateStringInMMM_DD_YYYY(start_date_edit_text, expenceIncomeLcl.getDateinMMM_DD_YYYY()) <= 0);

                }
            }
            if(end_date_edit_text != null){
                if(!end_date_edit_text.isEmpty()) {
                    ifSmallerThanStartDate =  (AppController.compareTwoDateStringInMMM_DD_YYYY(end_date_edit_text, expenceIncomeLcl.getDateinMMM_DD_YYYY()) >= 0);

                }
            }
            if(min_amt_edit_text != null){
                if(!min_amt_edit_text.isEmpty()) {
                    ifGreaterThanMinAmount = (Integer.parseInt(min_amt_edit_text) <= expenceIncomeLcl.getAMOUNT());
                }
            }
            if(max_amt_edit_text != null){
                if(!max_amt_edit_text.isEmpty()) {
                    ifSmallerThanMaxAmount = (Integer.parseInt(max_amt_edit_text) >= expenceIncomeLcl.getAMOUNT());
                }
            }
            if(ifGreaterThanStartDate && ifSmallerThanStartDate && ifGreaterThanMinAmount && ifSmallerThanMaxAmount && checkBoxListCateGoryIDArrayList.contains(expenceIncomeLcl.getCATEGORY_ID())){
                tempExpenceIncomeList.add(expenceIncomeLcl);
            }
        }
        if(expenseIncomesOriginal.size() != tempExpenceIncomeList.size()) {

            expenseIncomes.clear();
            expenseIncomes.addAll(tempExpenceIncomeList);
            groupByDate();
            hideRefreshIcon = false;
            getActivity().invalidateOptionsMenu();
        }

    }

    public  void resetToOriginal(){

        cancelDelete();
        expenseIncomes.clear();
        expenseIncomes.addAll(expenseIncomesOriginal);
        groupByDate();
        if(groupByDateFlag == true) {
            hideRefreshIcon = true;
            getActivity().invalidateOptionsMenu();
        }
        resetSerachView();
        View v = getActivity().getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void resetSerachView(){
        if(getView() != null) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) searchViewParent.getLayoutParams();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            final EditText editTextSearchItems = (EditText) getView().findViewById(R.id.editTextSearchItems);
            editTextSearchItems.setText("");
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editTextSearchItems.getWindowToken(), 0);
            editTextSearchItems.setText("");
            editTextSearchItems.clearFocus();
            ((ExpenceIncomeListAdapter)listView.getAdapter()).filter("");
            searchViewParent.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            searchButtonIsCollapsed = true;

        }
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
        public void onItemClick(ExpenseIncome expenseIncome, int position,HashMap<Integer,String> categoryIdName);
        public void onItemClickDelete(AdapterView<?> parent, int position);
        public void invalidateMenu(Boolean flag);
        public void addExpenceIncome();

    }

    public class ExpenceIncomeCollapsibleListAdapter extends BaseAdapter {
        private ArrayList<ExpenseIncome> all ;
        public Boolean ifOrderedByCategoryFlag = false;
        public Boolean deleteFlag = false;
        public Long sectionTotal ;

        public ExpenceIncomeCollapsibleListAdapter(ArrayList<ExpenseIncome> all,Boolean ifOrderedByCategoryFlag,Boolean deleteFlag,Long sectionTotal){
            this.all = all;
            this.ifOrderedByCategoryFlag = ifOrderedByCategoryFlag;
            this.deleteFlag = deleteFlag;
            this.sectionTotal = sectionTotal;
            for(int count =0; count < all.size();count++){
                this.sectionTotal = this.sectionTotal + all.get(count).getAMOUNT();
            }
        }
        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return all.size();
        }
        @Override
        public ExpenseIncome getItem(int position){
            return all.get(position);
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {

            View res = arg1;
            if (res == null) res = getActivity().getLayoutInflater().inflate(R.layout.expence_income_list_item, null);

            final int positionFinal = arg0;

            final RelativeLayout parentListItemWithoutHeader = (RelativeLayout)res.findViewById(R.id.parentListItemWithoutHeader);
            parentListItemWithoutHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(AppController.getInstance().listExpenseIncomeItemClicked == true){

                        return;
                    }
                    AppController.getInstance().listExpenseIncomeItemClicked = true;
                    if(deleteFlag){

                        mListener.onItemClickDelete(listView,positionFinal);
                        positionDeleted = getItem(positionFinal);
                        showAlertForDeletedExpenceIncome(positionDeleted);
                    }else{
                        mListener.onItemClick(getItem(positionFinal),positionFinal,categoryIdName);
                    }

                }
            });

            TextView itemNameExpenceOrIncome = (TextView) res.findViewById(R.id.itemNameExpenceOrIncome);
            TextView itemDescriptionExpenceOrIncome = (TextView) res.findViewById(R.id.itemDescriptionExpenceOrIncome);
            TextView itemAmountExpenceOrIncome = (TextView) res.findViewById(R.id.itemAmountExpenceOrIncome);
            TextView itemDateTextViewExpenceOrIncome = (TextView) res.findViewById(R.id.itemDateTextViewExpenceOrIncome);

            ExpenseIncome expenseOrIncome = getItem(positionFinal);
            itemNameExpenceOrIncome.setText(categoryIdName.get(expenseOrIncome.getCATEGORY_ID()));
            itemAmountExpenceOrIncome.setText(expenseOrIncome.getAMOUNTWithCurrency());
            Date date = expenseOrIncome.getDateFromDateString();
            DateFormat format = new SimpleDateFormat(AppController.MonthSpaceDateSpaceYearFormat, Locale.ENGLISH);
            itemDateTextViewExpenceOrIncome.setText(format.format(date));
            if(expenseOrIncome.getDESCRIPTION().equalsIgnoreCase(getString(R.string.no_desc))) {
                itemDescriptionExpenceOrIncome.setVisibility(View.GONE);
            }else{
                itemDescriptionExpenceOrIncome.setText(expenseOrIncome.getDESCRIPTION());
                itemDescriptionExpenceOrIncome.setVisibility(View.VISIBLE);
            }

            if(ifOrderedByCategoryFlag){
                itemNameExpenceOrIncome.setVisibility(View.GONE);
            }else{
                itemNameExpenceOrIncome.setVisibility(View.VISIBLE);

            }

            ImageView deleteImage = (ImageView)res.findViewById(R.id.deleteIconView);
            if(deleteFlag) {
                deleteImage.setVisibility(View.VISIBLE);
            }else {
                deleteImage.setVisibility(View.GONE);
            }
            //    RelativeLayout parentListItemWithoutHeader =(RelativeLayout)res.findViewById(R.id.parentListItemWithoutHeader);
          //  ListItemDetails listItemDetails = positionOfItemInSection(expenseOrIncome);

            TextView totalLabelExpenceIncomeItem = (TextView)res.findViewById(R.id.totalLabelExpenceIncomeItem);


                if(positionFinal == (all.size() -1)){
                    totalLabelExpenceIncomeItem.setText("TOTAL: "+ sectionTotal + AppController.getCurrencyString());
                    totalLabelExpenceIncomeItem.setVisibility(View.VISIBLE);
                }else{
                    totalLabelExpenceIncomeItem.setVisibility(View.GONE);
                }




            return res;

        }
    }

    public class ExpenceIncomeListAdapter extends BaseAdapter {
        private ArrayList<Pair<String, ArrayList<ExpenseIncome>>> all ;
        private ArrayList<Pair<String, ArrayList<ExpenseIncome>>> originalAll ;
       // private ArrayList<String> expandedAllGroups ;
        public Boolean ifOrderedByCategoryFlag = false;
        public Boolean deleteFlag = false;
        public ExpenceIncomeListAdapter(ArrayList<Pair<String, ArrayList<ExpenseIncome>>> all){
            this.all = new ArrayList<>();
            for(int count = 0;count<all.size();count++){
                if(all.get(count).second.size() >0) {
                    this.all.add(all.get(count));
                }
            }

           // this.all.addAll(all);
            originalAll = new ArrayList<>();
            originalAll.addAll(this.all);
//            this.expandedAllGroups = new ArrayList<>();
//            this.expandedAllGroups.addAll(expandedAllGroups);

            
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return all.size();
        }



        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {

            View res = arg1;
            if (res == null) res = getActivity().getLayoutInflater().inflate(R.layout.expence_income_list_collapsible_item, null);

            TextView headerText = (TextView)res.findViewById(R.id.header_list_income_expense);
            headerText.setText(all.get(arg0).first);
            final ListView collapsibleListView = (ListView)res.findViewById(R.id.collapsibleInsideList);

            collapsibleListView.setAdapter( new ExpenceIncomeCollapsibleListAdapter(all.get(arg0).second,ifOrderedByCategoryFlag,deleteFlag,Long.decode("0")));
            final Button disclosureIndicator = (Button)res.findViewById(R.id.disclosureIndicator);

            res.findViewById(R.id.parentFrameHeader).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(collapsibleListView.getVisibility() == View.GONE){
                        disclosureIndicator.setBackgroundDrawable(getResources().getDrawable(R.drawable.disclosure_list_expanded));
                        collapsibleListView.setVisibility(View.VISIBLE);
                    }else{
                        disclosureIndicator.setBackgroundDrawable(getResources().getDrawable(R.drawable.disclosure_list_collapsed));
                        collapsibleListView.setVisibility(View.GONE);
                    }

                }
            });


            setListViewHeightBasedOnChildren(collapsibleListView);
            return res;

        }

        @Override
        public Pair<String, ArrayList<ExpenseIncome>> getItem(int position) {
            //TODO

             return all.get(position);

        }



        public String getItemGroup(int position) {
            int c = 0;
            for (int i = 0; i < all.size(); i++) {
                if (position >= c && position < c + all.get(i).second.size()) {
                    return all.get(i).first;
                }
                c += all.get(i).second.size();
            }
            return null;
        }





        public void  reloadData(ArrayList<Pair<String, ArrayList<ExpenseIncome>>> all){
            this.originalAll.clear();
            this.all.clear();
            for(int count = 0;count<all.size();count++){
                if(all.get(count).second.size() >0) {
                    this.all.add(all.get(count));
                }
            }
            this.originalAll.addAll(this.all);
            notifyDataSetChanged();
        }
        public void  reloadDataForDeleteStatus(Boolean flag){
            if(this.deleteFlag == flag){
                return;
            }
            this.deleteFlag = flag;
            if(deleteFlag){
                searchViewParent.setVisibility(View.GONE);
                cancelDeleteParentFLEI.setVisibility(View.VISIBLE);
            }else{
                searchViewParent.setVisibility(View.VISIBLE);
                cancelDeleteParentFLEI.setVisibility(View.GONE);
            }
            notifyDataSetChanged();
        }


        private class ListItemDetails{
            public int positionInSection;
            public String sectionName;
            public Long sectionTotal;
            Boolean ifLastItem = false;

            private ListItemDetails() {

            }
        }

        // Filter Class
        public void filter(String charText) {
            
            //        private ArrayList<Pair<String, ArrayList<ExpenseIncome>>> all ;

            charText = charText.toLowerCase(Locale.getDefault());
            all.clear();
            if (charText.length() == 0) {
                if(groupByDateFlag == true) {

                    hideRefreshIcon = true;
                    getActivity().invalidateOptionsMenu();
                }
                nodataText.setText(nodataTextString);
                all.addAll(originalAll);
            }
            else 
            {
                if(hideRefreshIcon != false) {
                    hideRefreshIcon = false;
                    getActivity().invalidateOptionsMenu();
                }
                if(isExpenseFlag) {
                    nodataText.setText(getString(R.string.search_no_data_expense_income_message).replace(AppController.ei, getString(R.string.income)));

                }else{
                    nodataText.setText(getString(R.string.search_no_data_expense_income_message).replace(AppController.ei, getString(R.string.income)));

                }
                nodataText.setText(getString(R.string.search_no_data_expense_income_message).replace(AppController.ei, getString(R.string.income)));

                for ( Pair<String, ArrayList<ExpenseIncome>> pair : originalAll)
                {
                    if (pair.first.toLowerCase(Locale.getDefault()).contains(charText.toLowerCase()))
                    {
                        all.add(pair);
                    }else{

                        ArrayList<ExpenseIncome> expenseIncomeArrayList =  pair.second;
                        ArrayList<ExpenseIncome> filteredDataFromPair = new ArrayList<>();
                        for(int count = 0; count < expenseIncomeArrayList.size();count++){
                            ExpenseIncome expenseIncome = expenseIncomeArrayList.get(count);
                            Log.e("expenseIncome.getDateinMMMM_DD_YYYY()",expenseIncome.getDateinMMMM_DD_YYYY());
                            if (expenseIncome.getAMOUNT().toString().toLowerCase(Locale.getDefault()).contains(charText.toLowerCase()) || expenseIncome.getAMOUNT().toString().toLowerCase(Locale.getDefault()).contains(charText.toLowerCase()) || expenseIncome.getDateinMMMM_DD_YYYY().toLowerCase(Locale.getDefault()).contains(charText.toLowerCase()) || expenseIncome.getDESCRIPTION().toString().toLowerCase(Locale.getDefault()).contains(charText.toLowerCase()) )
                            {
                                filteredDataFromPair.add(expenseIncome);
                            }
                        }

                        if(filteredDataFromPair.size()>0) {
                            all.add(new Pair<>(pair.first, filteredDataFromPair));
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }

    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public  void setListViewHeightBasedOnChildren(ListView listView) {
        ExpenceIncomeCollapsibleListAdapter listAdapter = (ExpenceIncomeCollapsibleListAdapter)listView.getAdapter();
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

}
