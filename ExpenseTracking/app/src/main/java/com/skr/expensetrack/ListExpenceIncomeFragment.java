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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skr.AppController;
import com.skr.customviews.AmazingAdapter;
import com.skr.customviews.AmazingListView;
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
     AmazingListView listView;
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
        final Button search_drag = (Button)rootView.findViewById(R.id.search_drag_button);
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
        final int minWidth =  (int)(50 * scale + 0.5f);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int height = displaymetrics.heightPixels;
        final int width = displaymetrics.widthPixels;
        layoutParams.leftMargin = width - minWidth;
        Log.e("rootView.getWidth() - 50;", "" + minWidth);
        searchViewParent.setLayoutParams(layoutParams);
        searchViewParent.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        search_drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) searchViewParent.getLayoutParams();
                if(searchButtonIsCollapsed) {
                    mParams.leftMargin = 10;
                    search_drag.setBackgroundDrawable(getResources().getDrawable(R.drawable.search_drag_in));
                    searchViewParent.setBackgroundColor(getResources().getColor(R.color.backgroud_white));
                }else{
                    mParams.leftMargin = width - minWidth;
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextSearchItems.getWindowToken(), 0);
                    editTextSearchItems.setText("");
                    editTextSearchItems.clearFocus();
                    ((ExpenceIncomeListAdapter)listView.getAdapter()).filter("");
                    searchViewParent.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    search_drag.setBackgroundDrawable(getResources().getDrawable(R.drawable.search_drag_out));
                }
                searchViewParent.setLayoutParams(mParams);
                searchButtonIsCollapsed = !searchButtonIsCollapsed;
            }
        });
                listView = (AmazingListView)rootView.findViewById(R.id.expenseOrIncomeList);
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
                final AmazingListView listView = (AmazingListView) getView().findViewById(R.id.expenseOrIncomeList);

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
        final AmazingListView listView = (AmazingListView) getView().findViewById(R.id.expenseOrIncomeList);
        ExpenceIncomeListAdapter expenceIncomeListAdapter = (ExpenceIncomeListAdapter)listView.getAdapter();
        expenceIncomeListAdapter.reloadDataForDeleteStatus(!expenceIncomeListAdapter.deleteFlag);
        return expenceIncomeListAdapter.deleteFlag;
    }
    public void cancelDelete(){
        final AmazingListView listView = (AmazingListView) getView().findViewById(R.id.expenseOrIncomeList);
        ExpenceIncomeListAdapter expenceIncomeListAdapter = (ExpenceIncomeListAdapter)listView.getAdapter();
        expenceIncomeListAdapter.reloadDataForDeleteStatus(false);
    }

    public void showAlertForDeletedExpenceIncome(final  ExpenseIncome expenseIncomeFunctionParam){
        String dilogHeading = getResources().getString(R.string.delete_dialog_title_message);
        String confirmDilogMessage = getResources().getString(R.string.delete_dialog_description_message);



        new CustomAlert.CustomBuilder(getActivity(),getActivity().getLayoutInflater())
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



        new CustomAlert.CustomBuilder(getActivity(),getActivity().getLayoutInflater())
                .setTitle(R.string.info)
                .setMessage(confirmDilogMessage).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (sucessFlag) {
                    expenseIncomes.remove(positionDeleted);
                    groupByDate();

                }

                // do nothing
            }
        }).setIcon(R.drawable.delete_alert)
                .show();


    }
    public void groupByCategory(){
        cancelDelete();
        final AmazingListView listView = (AmazingListView) getView().findViewById(R.id.expenseOrIncomeList);
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
            all.add(new Pair<String, ArrayList<ExpenseIncome>>(categoryIdName.get(key),entry.getValue()));
        }
        expenceIncomeListAdapter.reloadData(all);
        groupByDateFlag = false;
        hideRefreshIcon = false;
        getActivity().invalidateOptionsMenu();

    }
    public void groupByMonths(){
        cancelDelete();
        final AmazingListView listView = (AmazingListView) getView().findViewById(R.id.expenseOrIncomeList);
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
        expenseIncomes.clear();
        expenseIncomes.addAll(expenseIncomesOriginal);
        groupByDate();
    }
    public void groupByDate(){
        cancelDelete();
        final AmazingListView listView = (AmazingListView) getView().findViewById(R.id.expenseOrIncomeList);
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
            final int width = displaymetrics.widthPixels;
            final Button search_drag = (Button)getView().findViewById(R.id.search_drag_button);
            editTextSearchItems.setText("");
            final float scale = getActivity().getResources().getDisplayMetrics().density;
            final int minWidth =  (int)(50 * scale + 0.5f);
            mParams.leftMargin = width - minWidth;
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editTextSearchItems.getWindowToken(), 0);
            editTextSearchItems.setText("");
            editTextSearchItems.clearFocus();
            ((ExpenceIncomeListAdapter)listView.getAdapter()).filter("");
            searchViewParent.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            search_drag.setBackgroundDrawable(getResources().getDrawable(R.drawable.search_drag_out));
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
        public void onItemClick(AdapterView<?> parent, int position,HashMap<Integer,String> categoryIdName);
        public void onItemClickDelete(AdapterView<?> parent, int position);
        public void invalidateMenu(Boolean flag);
        public void addExpenceIncome();

    }

    public class ExpenceIncomeListAdapter extends AmazingAdapter {
        private ArrayList<Pair<String, ArrayList<ExpenseIncome>>> all ;
        private ArrayList<Pair<String, ArrayList<ExpenseIncome>>> originalAll ;
        private ArrayList<String> expandedAllGroups ;
        public Boolean ifOrderedByCategoryFlag = false;
        public Boolean deleteFlag = false;
        public ExpenceIncomeListAdapter(ArrayList<Pair<String, ArrayList<ExpenseIncome>>> all){
            this.all = new ArrayList<>();
            this.all.addAll(all);
            originalAll = new ArrayList<>();
            originalAll.addAll(all);
            this.expandedAllGroups = new ArrayList<>();
            this.expandedAllGroups.addAll(expandedAllGroups);

            
        }

        @Override
        public ExpenseIncome getItem(int position) {
            int c = 0;
            for (int i = 0; i < all.size(); i++) {
                if (position >= c && position < c + all.get(i).second.size()) {
                    return all.get(i).second.get(position - c);
                }
                c += all.get(i).second.size();
            }
            return null;
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


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        protected void onNextPageRequested(int page) {
        }

        @Override
        protected void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
            if (displaySectionHeader) {

                final View rootView = view;
                final FrameLayout parentFrameHeader = (FrameLayout) view.findViewById(R.id.parentFrameHeader);
                parentFrameHeader.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                final TextView lSectionTitle = (TextView) view.findViewById(R.id.header_list_income_expense);
                lSectionTitle.setText(getSections()[getSectionForPosition(position)]);
                Button disclosureIndicator = (Button) view.findViewById(R.id.disclosureIndicator);

                if(!expandedAllGroups.contains(getItemGroup(position))){
                    disclosureIndicator.setBackgroundDrawable(getResources().getDrawable(R.drawable.disclosure_list_collapsed));

                }else{

                    disclosureIndicator.setBackgroundDrawable(getResources().getDrawable(R.drawable.disclosure_list_expanded));

                }
                final String headerTitle = lSectionTitle.getText().toString();
                parentFrameHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      if(expandedAllGroups.contains(headerTitle)) {
                          expandedAllGroups.remove(headerTitle);
                      }else {

                          expandedAllGroups.add(headerTitle);
                      }
                        notifyDataSetChanged();
                    }
                });


            } else {
                final FrameLayout parentFrameHeader = (FrameLayout) view.findViewById(R.id.parentFrameHeader);
                parentFrameHeader.setVisibility(View.GONE);
            }
        }

        @Override
        public View getAmazingView(int position, View convertView, ViewGroup parent) {
            View res = convertView;
            if (res == null) res = getActivity().getLayoutInflater().inflate(R.layout.expence_income_list_item, null);

            final int positionFinal = position;

            final RelativeLayout parentListItemWithoutHeader = (RelativeLayout)res.findViewById(R.id.parentListItemWithoutHeader);
            parentListItemWithoutHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(AppController.getInstance().listExpenseIncomeItemClicked == true){

                        return;
                    }
                    AppController.getInstance().listExpenseIncomeItemClicked = true;
                    if(((ExpenceIncomeListAdapter)listView.getAdapter()).deleteFlag){

                        mListener.onItemClickDelete(listView,positionFinal);
                        positionDeleted = ((ExpenceIncomeListAdapter)listView.getAdapter()).getItem(positionFinal);
                        showAlertForDeletedExpenceIncome(positionDeleted);
                    }else{
                        mListener.onItemClick(listView,positionFinal,categoryIdName);
                    }
                }
            });

            TextView itemNameExpenceOrIncome = (TextView) res.findViewById(R.id.itemNameExpenceOrIncome);
            TextView itemDescriptionExpenceOrIncome = (TextView) res.findViewById(R.id.itemDescriptionExpenceOrIncome);
            TextView itemAmountExpenceOrIncome = (TextView) res.findViewById(R.id.itemAmountExpenceOrIncome);
            TextView itemDateTextViewExpenceOrIncome = (TextView) res.findViewById(R.id.itemDateTextViewExpenceOrIncome);

            ExpenseIncome expenseOrIncome = getItem(position);
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
            ListItemDetails listItemDetails = positionOfItemInSection(expenseOrIncome);

            TextView totalLabelExpenceIncomeItem = (TextView)res.findViewById(R.id.totalLabelExpenceIncomeItem);

            if(!expandedAllGroups.contains(getItemGroup(position))){
               parentListItemWithoutHeader.setVisibility(View.GONE);
                totalLabelExpenceIncomeItem.setVisibility(View.GONE);

            }else{
                if(listItemDetails.ifLastItem){
                    totalLabelExpenceIncomeItem.setText("TOTAL: "+ listItemDetails.sectionTotal);
                    totalLabelExpenceIncomeItem.setVisibility(View.VISIBLE);
                }else{
                    totalLabelExpenceIncomeItem.setVisibility(View.GONE);
                }
                parentListItemWithoutHeader.setVisibility(View.VISIBLE);
            }



            return res;
        }

        @Override
        public void configurePinnedHeader(View header, int position, int alpha) {
            TextView lSectionHeader = (TextView)header.findViewById(R.id.header_list_income_expense);
            lSectionHeader.setText(getSections()[getSectionForPosition(position)]);
            header.setVisibility(View.VISIBLE);
        }
        @Override
        public int getCount() {
            int res = 0;
            for (int i = 0; i < all.size(); i++) {
                res += all.get(i).second.size();
            }
            return res;
        }
        @Override
        public int getPositionForSection(int section) {
            if (section < 0) section = 0;
            if (section >= all.size()) section = all.size() - 1;
            int c = 0;
            for (int i = 0; i < all.size(); i++) {
                if (section == i) {
                    return c;
                }
                c += all.get(i).second.size();
            }
            return 0;
        }




        public ListItemDetails positionOfItemInSection(ExpenseIncome expenseIncome) {
            ListItemDetails listItemDetails =  new ListItemDetails();
            int totalSizeI = all.size();
            for (int i = 0; i < totalSizeI; i++) {
                Boolean exitILoop = false;
                listItemDetails.sectionTotal = 0;
                int totalSizeJ = all.get(i).second.size();
                for (int j = 0; j < totalSizeJ; j++) {
                    Log.e("all.get(i).second.get(j).getAMOUNT()","i "+i+" j "+j+all.get(i).second.get(j).toString());
                    listItemDetails.sectionTotal = listItemDetails.sectionTotal + all.get(i).second.get(j).getAMOUNT();

                    if(all.get(i).second.get(j).getEXPENCE_INCOME_ID() == expenseIncome.getEXPENCE_INCOME_ID()){
                        if(j==totalSizeJ-1){
                            listItemDetails.ifLastItem = true;
                        }
                        listItemDetails.positionInSection = j;
                        listItemDetails.sectionName = all.get(i).first;
                       // j = totalSizeJ;
                        exitILoop = true;

                    }
                }
                if(exitILoop){
                    i = totalSizeI;
                    continue;
                }
            }



            return listItemDetails;
        }

        @Override
        public int getSectionForPosition(int position) {
            int c = 0;
            for (int i = 0; i < all.size(); i++) {
                if (position >= c && position < c + all.get(i).second.size()) {
                    return i;
                }
                c += all.get(i).second.size();
            }
            return -1;
        }

        @Override
        public String[] getSections() {
            String[] res = new String[all.size()];
            for (int i = 0; i < all.size(); i++) {
                res[i] = all.get(i).first;
            }
            return res;
        }

        public void  reloadData(ArrayList<Pair<String, ArrayList<ExpenseIncome>>> all){
            this.originalAll.clear();
            this.all = all;
            this.originalAll.addAll(all);
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
        public void  reloadDataRemovingItemAtIndex(int position){
            this.all.remove(position);
            notifyDataSetChanged();
        }

        private class ListItemDetails{
            public int positionInSection;
            public String sectionName;
            public Integer sectionTotal;
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

}
