package com.skr.expensetrack;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.skr.customviews.CustomProgressDialog;
import com.skr.customviews.SegmentedControlButton;
import com.skr.datahelper.Category;
import com.skr.datahelper.DBHelper;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoryListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CategoryListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryListFragment extends Fragment {
    
    CategoryWithDeleteFlag categoryDeletedRefference = null;
    public static final String CategoryToBeDeletedOrUpdated =  "CategoryToBeDeletedOrUpdated";
    public static final String ExpenseOrIncome =  "ExpenseOrIncome";
    public static final String ei=  "!*ei*!";
    public static final String c=  "!*c*!";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    LinearLayout cancelDeleteParentFCL;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String[] defaultCategoryArray;
    private CustomProgressDialog progress;
    ArrayList<CategoryWithDeleteFlag> expenceCategories;
    ArrayList<CategoryWithDeleteFlag> incomeCategories;
    ListView categorListView;
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
     * @return A new instance of fragment CategoryListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryListFragment newInstance(String param1, String param2) {
        CategoryListFragment fragment = new CategoryListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CategoryListFragment() {
        // Required empty public constructor


    }

    
    public void removeCategoryFromListAfterSuccessfulDelete(){
        if(expenceCategories.contains(categoryDeletedRefference)){
            expenceCategories.remove(categoryDeletedRefference);
            ((CategoryListAdapter)categorListView.getAdapter()).reloadData(expenceCategories);
        }
        else if(incomeCategories.contains(categoryDeletedRefference)){
            incomeCategories.remove(categoryDeletedRefference);
            ((CategoryListAdapter)categorListView.getAdapter()).reloadData(incomeCategories);
        }
    }

    public void reloadCategoryFromListAfterSuccessfulUpdate(Category category){
        int index = 0;
        final SegmentedControlButton segmentedControlButtonExpence = (SegmentedControlButton)getView().findViewById(R.id.segmented_control_expense);
        final SegmentedControlButton segmentedControlButtonIncome = (SegmentedControlButton)getView().findViewById(R.id.segmented_control_income);

        if(expenceCategories.contains(categoryDeletedRefference)){
            index = expenceCategories.indexOf(categoryDeletedRefference);
            expenceCategories.remove(categoryDeletedRefference);
        }
        if(incomeCategories.contains(categoryDeletedRefference)){
            index = incomeCategories.indexOf(categoryDeletedRefference);
            incomeCategories.remove(categoryDeletedRefference);
        }
        if(category.getIFEXPENSE()) {
            segmentedControlButtonExpence.setChecked(true);
            segmentedControlButtonIncome.setChecked(false);
            expenceCategories.add(index, new CategoryWithDeleteFlag(category));
            ((CategoryListAdapter)categorListView.getAdapter()).reloadData(expenceCategories);
        }else {
            segmentedControlButtonExpence.setChecked(false);
            segmentedControlButtonIncome.setChecked(true);
            incomeCategories.add(index, new CategoryWithDeleteFlag(category));
            ((CategoryListAdapter)categorListView.getAdapter()).reloadData(incomeCategories);
        }
    }
    public void reloadAddingCategorytoList(Category category){

        final SegmentedControlButton segmentedControlButtonExpence = (SegmentedControlButton)getView().findViewById(R.id.segmented_control_expense);
        final SegmentedControlButton segmentedControlButtonIncome = (SegmentedControlButton)getView().findViewById(R.id.segmented_control_income);
        if(category.getIFEXPENSE()) {
            segmentedControlButtonExpence.setChecked(true);
            segmentedControlButtonIncome.setChecked(false);
            expenceCategories.add(new CategoryWithDeleteFlag(category));
            ((CategoryListAdapter)categorListView.getAdapter()).reloadData(expenceCategories);
        }else {
            segmentedControlButtonExpence.setChecked(false);
            segmentedControlButtonIncome.setChecked(true);
            incomeCategories.add(new CategoryWithDeleteFlag(category));
            ((CategoryListAdapter)categorListView.getAdapter()).reloadData(incomeCategories);
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mListener != null) {
            mListener.onCreatedFragment();
        }
        progress =  CustomProgressDialog.getInstance(getActivity());
        progress.setMessage(getResources().getString(R.string.wait_message));
        final Handler handler = new Handler();
        final View rootView = inflater.inflate(R.layout.fragment_category_list, container, false);


        // Restore preferences


        cancelDeleteParentFCL = (LinearLayout)rootView.findViewById(R.id.cancelDeleteParentFCL);
       // cancelDeleteParentFCL.setText(cancelDeleteParentFCL.getText().toString().toUpperCase());
        cancelDeleteParentFCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEnabled();
                getActivity().invalidateOptionsMenu();
            }
        });
        rootView.findViewById(R.id.cancelDeleteButtonFCL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEnabled();
                getActivity().invalidateOptionsMenu();
            }
        });
        rootView.findViewById(R.id.cancelDeleteImgViewFCL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEnabled();
                getActivity().invalidateOptionsMenu();
            }
        });
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                DBHelper dbHelper = DBHelper.getInstance(getActivity());
                ArrayList<com.skr.datahelper.Category> categories = dbHelper.getAllCategory();
                expenceCategories = new ArrayList<>();
                incomeCategories = new ArrayList<>();
                for(int count =0;count<categories.size();count++){
                    com.skr.datahelper.Category category = categories.get(count);
                    if(category.getIFEXPENSE()){
                        expenceCategories.add(new CategoryWithDeleteFlag(category));
                    }else {
                        incomeCategories.add(new CategoryWithDeleteFlag(category));
                    }

                }
                                handler.post(new Runnable(){
                    @Override
                    public void run(){
                        ListView categorList = (ListView)rootView.findViewById(R.id.category_list_view);
                        categorList.setAdapter(new CategoryListAdapter(expenceCategories));
                        if(progress.isShowing()){
                            progress.dismiss();
                        }
                    }

                });

            }
        }).start();

        final SegmentedControlButton segmentedControlButtonExpence = (SegmentedControlButton)rootView.findViewById(R.id.segmented_control_expense);
        final SegmentedControlButton segmentedControlButtonIncome = (SegmentedControlButton)rootView.findViewById(R.id.segmented_control_income);
        segmentedControlButtonExpence.setChecked(true);
        segmentedControlButtonExpence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                segmentedControlButtonIncome.setChecked(false);
                ListView categorList = (ListView)rootView.findViewById(R.id.category_list_view);
                CategoryListAdapter categoryListAdapter = (CategoryListAdapter)categorList.getAdapter();
                categoryListAdapter.reloadData(expenceCategories);
            }
        });
        segmentedControlButtonIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                segmentedControlButtonExpence.setChecked(false);
                ListView categorList = (ListView)rootView.findViewById(R.id.category_list_view);
                CategoryListAdapter categoryListAdapter = (CategoryListAdapter)categorList.getAdapter();
                categoryListAdapter.reloadData(incomeCategories);
            }
        });
        // Inflate the layout for this fragment
        categorListView = (ListView)rootView.findViewById(R.id.category_list_view);
        categorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mListener != null){
                    if(segmentedControlButtonExpence.isChecked()) {
                        categoryDeletedRefference =  expenceCategories.get(position);
                        mListener.onItemClick(parent,view,position,id,segmentedControlButtonExpence.isChecked(),categoryDeletedRefference.category);
                    }else{
                        categoryDeletedRefference = incomeCategories.get(position);
                        mListener.onItemClick(parent,view,position,id,segmentedControlButtonExpence.isChecked(),categoryDeletedRefference.category);

                    }


                }

            }
        });
        return rootView;
    }

    public void deleteEnabled(){
        ((CategoryListAdapter)categorListView.getAdapter()).enableDelete();

    }
    public void cancelDelete(){
        ((CategoryListAdapter)categorListView.getAdapter()).cancelDelete();

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
//                    + " must implement OnFragmentInteractionListener.");
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
        public void onItemClick(AdapterView<?> parent, View view, int position, long id,Boolean segmentedCheckStatus,Category category);
    }


    public class CategoryListAdapter extends BaseAdapter {
        ArrayList<CategoryWithDeleteFlag> all;
        Boolean toBeDeletedFlag = false;
        CategoryListAdapter(ArrayList<CategoryWithDeleteFlag> all){
            this.all = all;

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return all.size();
        }

        @Override
        public CategoryWithDeleteFlag getItem(int arg0) {
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
            if (res == null) res = getActivity().getLayoutInflater().inflate(R.layout.category_list_layout, null);
            CategoryWithDeleteFlag category = getItem(arg0);
            ImageView deleteImageView = (ImageView)res.findViewById(R.id.deleteIconView);
            TextView categoryDeleteMessageConfimationView = (TextView)res.findViewById(R.id.categoryDeleteMessageConfimationView);

            if(this.toBeDeletedFlag){
                deleteImageView.setVisibility(View.VISIBLE);
            }else {
                deleteImageView.setVisibility(View.INVISIBLE);
            }
            if (category.toBeDeletedFlag){
                categoryDeleteMessageConfimationView.setVisibility(View.VISIBLE);

            }else {
                categoryDeleteMessageConfimationView.setVisibility(View.GONE);
            }
            TextView categoryNameView = (TextView)res.findViewById(R.id.categoryNameView);
            categoryNameView.setText(category.getCATEGORY_NAME());

            ImageView imageView = (ImageView)res.findViewById(R.id.categoryIconView);

            imageView.setVisibility(View.GONE);

            return res;
        }

        public void reloadData(ArrayList<CategoryWithDeleteFlag> all){
            this.all = all;
            notifyDataSetChanged();

        }
        public void enableDelete(){
            this.toBeDeletedFlag = !this.toBeDeletedFlag;
            if(toBeDeletedFlag){
                cancelDeleteParentFCL.setVisibility(View.VISIBLE);
            }else{
                cancelDeleteParentFCL.setVisibility(View.GONE);
            }
            notifyDataSetChanged();

        }
        public void cancelDelete(){

            if(!this.toBeDeletedFlag){
                return;
            }
            this.toBeDeletedFlag = false;
            cancelDeleteParentFCL.setVisibility(View.GONE);


            notifyDataSetChanged();

        }

    }
    private class CategoryWithDeleteFlag extends com.skr.datahelper.Category{

        public Boolean toBeDeletedFlag = false;
        public Category category;
        CategoryWithDeleteFlag(com.skr.datahelper.Category category){
            this.category = category;
            this.CATEGORY_ID = category.getCATEGORY_ID();
            this.CATEGORY_NAME = category.getCATEGORY_NAME();
            this.IFEXPENSE = category.getIFEXPENSE();
        }
    }

}
