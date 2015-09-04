package com.skr.expensetrack;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.skr.AppController;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CurrencyListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CurrencyListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrencyListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MenuItem searchItem;
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
     * @return A new instance of fragment CurrencyListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrencyListFragment newInstance(String param1, String param2) {
        CurrencyListFragment fragment = new CurrencyListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CurrencyListFragment() {
        // Required empty public constructor
    }

    public void filter(String query){
        ListView listview = (ListView)getView().findViewById(R.id.currencyList);
        CurrencyListAdapter currencyListAdapter = ((CurrencyListAdapter)listview.getAdapter());

        currencyListAdapter.filter(query);

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void reloadData(){
        loadDataForList((ListView)getView().findViewById(R.id.currencyList));
    }
    private void loadDataForList(ListView listview){
        if(searchItem != null){
            try {

            }catch (Exception e){

            }
            View v = getActivity().getWindow().getCurrentFocus();
            if (v != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            MenuItemCompat.collapseActionView(searchItem);
        }
        ArrayList<CurrencyItem> currencyListString  = new ArrayList();
        SharedPreferences settings = getActivity().getSharedPreferences(AppController.MY_APP_PREFERENCE, 0);
        String otherCurrencyString = settings.getString(AppController.OtherCurrencyString,"");

        String[] defaultCacurrencyArray
                = getActivity().getResources().getStringArray(R.array.currency_list);
        String nocurrency = getString(R.string.nocurrency);
        String othercurrency = getString(R.string.othercurrency);
        currencyListString.add(new CurrencyItem(new Pair<>(nocurrency,nocurrency),AppController.getCurrencyString().trim().isEmpty()));

        currencyListString.add(new CurrencyItem(new Pair<>(otherCurrencyString,othercurrency),((!otherCurrencyString.isEmpty()) && otherCurrencyString.equalsIgnoreCase(AppController.getCurrencyString().trim()))));
        for(int count=0;count< defaultCacurrencyArray.length;count++){
            String currentString  = defaultCacurrencyArray[count];

            if(!currentString.equalsIgnoreCase(nocurrency) && !currentString.equalsIgnoreCase(othercurrency)) {
                String currencyStringVal = currentString.substring(0, 3);
                currencyListString.add(new CurrencyItem(new Pair<>(currencyStringVal, currentString.substring(8, currentString.length())),otherCurrencyString.isEmpty() && currencyStringVal.equalsIgnoreCase(AppController.getCurrencyString())));
            }


        }
        CurrencyListAdapter currencyListAdapter = ((CurrencyListAdapter)listview.getAdapter());
        currencyListAdapter.reloadData(currencyListString);
        currencyListAdapter.notifyDataSetChanged();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mListener != null){
            mListener.onCreatedFragment();
        }
        View rootView = inflater.inflate(R.layout.fragment_currency_list, container, false);
        final ListView currencyList = (ListView)rootView.findViewById(R.id.currencyList);
         ArrayList<CurrencyItem> currencyListString  = new ArrayList();

        currencyList.setAdapter(new CurrencyListAdapter(currencyListString));
        loadDataForList(currencyList);
        currencyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final CurrencyListAdapter currencyListAdapter = (CurrencyListAdapter)parent.getAdapter();

                Pair<String,String> itemVal =  currencyListAdapter.getItem(position).testDescriptionPair;
                String currencyString = null;


                if(itemVal.first.equalsIgnoreCase(getString(R.string.nocurrency))){
                    currencyString = "";
                    AppController.setOtherCurrencyStringToSharePreference("",getActivity());


                }else if(itemVal.second.equalsIgnoreCase(getString(R.string.othercurrency))){
                   if(mListener != null){
                       mListener.editOtherCurrencyActivity();
                   }

                }else {

                    currencyString = itemVal.first;
                    AppController.setOtherCurrencyStringToSharePreference("",getActivity());

                }

                if(currencyString != null) {
                    String othercurrency = getString(R.string.othercurrency);

                    AppController.setCurrencyStringToSharePreference(currencyString, getActivity());
                    ArrayList<CurrencyItem> currencyListStringReplace  = new ArrayList();

                    for (int count = 0;count<currencyListAdapter.all.size();count++){
                        CurrencyItem currencyItemVal = currencyListAdapter.all.get(count);

                        if(count == position) {
                            currencyItemVal.checkedStatus = true;

                        }else{
                            currencyItemVal.checkedStatus = false;

                        }

                        if(othercurrency.equalsIgnoreCase(currencyItemVal.testDescriptionPair.second)){
                            SharedPreferences settings = getActivity().getSharedPreferences(AppController.MY_APP_PREFERENCE, 0);
                            String otherCurrencyString = settings.getString(AppController.OtherCurrencyString,"");
                            currencyItemVal.testDescriptionPair  = new Pair<>(otherCurrencyString,othercurrency);
                        }
                        currencyListStringReplace.add(currencyItemVal);

                    }

                    currencyListAdapter.reloadData(currencyListStringReplace);
                }



            }
        });

        // Inflate the layout for this fragment
        return rootView;
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
        public void editOtherCurrencyActivity();

    }

    public class CurrencyListAdapter extends BaseAdapter {
        public ArrayList<CurrencyItem> all;
         ArrayList<CurrencyItem> originalAll;

        CurrencyListAdapter(ArrayList<CurrencyItem> all) {
            this.all = all;
            this.originalAll = new ArrayList<>();
            this.originalAll.addAll(all);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return all.size();
        }

        @Override
        public CurrencyItem getItem(int arg0) {
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
            if (res == null) res = getActivity().getLayoutInflater().inflate(R.layout.list_currenccy_item, null);
            CurrencyItem currencyItem = getItem(arg0);
            Pair<String,String> listItemValue = currencyItem.testDescriptionPair;
            String currencyTextVal = listItemValue.first;
            TextView currencyText  = (TextView)res.findViewById(R.id.currencyText);
            currencyText.setVisibility(View.VISIBLE);

            TextView currencyTextDescription  = (TextView)res.findViewById(R.id.currencyTextDescription);
            currencyText.setText(currencyTextVal);

            ImageView currencySelectedIndicator  = (ImageView)res.findViewById(R.id.currencySelectedIndicator);

            currencyTextDescription.setText(listItemValue.second);
            TextView currencyTextDummy  = (TextView)res.findViewById(R.id.currencyTextDummy);
            currencyTextDummy.setVisibility(View.GONE);
            if(listItemValue.first.equalsIgnoreCase(getString(R.string.nocurrency))){
                currencyText.setVisibility(View.INVISIBLE);
                currencyTextDummy.setVisibility(View.VISIBLE);
                currencyTextDescription.setVisibility(View.INVISIBLE);
            }else{
                currencyText.setVisibility(View.VISIBLE);
                currencyTextDescription.setVisibility(View.VISIBLE);
            }

            if (listItemValue.second.equalsIgnoreCase(getString(R.string.othercurrency))){
                if(listItemValue.first.isEmpty()){
                    currencyText.setText("(EMPTY)");
                    currencyTextDescription.setText(listItemValue.second.toUpperCase());
                }

            }
            if(currencyItem.checkedStatus){
                currencySelectedIndicator.setVisibility(View.VISIBLE);

            }else {
                currencySelectedIndicator.setVisibility(View.GONE);
            }
//           if(AppController.getInstance().getCurrencyString().isEmpty() && listItemValue.first.equalsIgnoreCase(getString(R.string.nocurrency))){
//               currencySelectedIndicator.setVisibility(View.VISIBLE);
//           }else if(AppController.getInstance().getCurrencyString().equalsIgnoreCase(listItemValue.first) && (!listItemValue.first.isEmpty())){
//               currencySelectedIndicator.setVisibility(View.VISIBLE);
//
//           }

            return res;
        }
        public void reloadData(ArrayList<CurrencyItem> all){
            this.all.clear();
            this.originalAll.clear();
            this.all = all;
            this.originalAll.addAll(all);

            notifyDataSetChanged();
        }
        public void filter(String charText) {

            //        private ArrayList<Pair<String, ArrayList<ExpenseIncome>>> all ;

            charText = charText.toLowerCase(Locale.getDefault());
            all.clear();
            if (charText.length() == 0) {

                all.addAll(originalAll);
            }
            else
            {

                for (int count = 0;count<originalAll.size();count++ )
                {
                    CurrencyItem currencyItem = originalAll.get(count);

                    if(currencyItem.testDescriptionPair.first.equalsIgnoreCase(getString(R.string.nocurrency)) || currencyItem.testDescriptionPair.second.equalsIgnoreCase(getString(R.string.othercurrency))){
                        all.add(currencyItem);
                    }
                    else if (currencyItem.testDescriptionPair.second.toLowerCase(Locale.getDefault()).contains(charText.toLowerCase()) || currencyItem.testDescriptionPair.first.toLowerCase(Locale.getDefault()).contains(charText.toLowerCase()))
                    {
                        all.add(currencyItem);
                    }
                }
            }
            notifyDataSetChanged();
        }


    }
    class CurrencyItem{
        public Pair<String,String> testDescriptionPair;
        public Boolean checkedStatus;

        public CurrencyItem(Pair<String, String> testDescriptionPair, Boolean checkedStatus) {
            this.testDescriptionPair = testDescriptionPair;
            this.checkedStatus = checkedStatus;
        }
    }

}
