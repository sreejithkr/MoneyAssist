package com.skr.expensetrack;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.skr.AppController;
import com.skr.datahelper.ExpenseIncome;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExpenceIncomeDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExpenceIncomeDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenceIncomeDetailFragment extends Fragment {


    public ExpenseIncome expenseIncome;
    public String categoryName;




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

     * @return A new instance of fragment ExpenceIncomeDetailFragment.
     */

    public static ExpenceIncomeDetailFragment newInstance() {
        ExpenceIncomeDetailFragment fragment = new ExpenceIncomeDetailFragment();

        return fragment;
    }

    public ExpenceIncomeDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_expence_income_detail, container, false);
        TextView expenceIncomeMessageFEID = (TextView)rootView.findViewById(R.id.expenceIncomeMessageFEID);
//        TextView expenceIncomeDescriptionTitleFEID = (TextView)rootView.findViewById(R.id.expenceIncomeDescriptionTitleFEID);
        TextView expenceIncomeDescriptionFEID = (TextView)rootView.findViewById(R.id.expenceIncomeDescriptionFEID);
       // RelativeLayout editRelativeParent = (RelativeLayout)rootView.findViewById(R.id.editRelativeParent);

        String messageSting = getActivity().getResources().getString(R.string.expense_message);
        if(!expenseIncome.getIF_EXPENSE()){
            messageSting = getActivity().getResources().getString(R.string.income_message);
        }
        Log.e("*************&&&&&&&&&&&***************"+categoryName,expenseIncome.toString());
        messageSting = messageSting.replace("!*a*!",expenseIncome.getAMOUNTWithCurrency());
        messageSting = messageSting.replace("!*c*!",categoryName);
        Date date = expenseIncome.getDateFromDateString();
        DateFormat format = new SimpleDateFormat(AppController.MonthSpaceDateSpaceYearFormat, Locale.ENGLISH);
        messageSting = messageSting.replace("!*d*!",format.format(date));
        expenceIncomeMessageFEID.setText(messageSting);
        expenceIncomeDescriptionFEID.setText(expenseIncome.getDESCRIPTION());
//        ImageView editRelativeParentImageViewChild = (ImageView)rootView.findViewById(R.id.editRelativeParentImgViewFEID);
//        editRelativeParentImageViewChild.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mListener.editButtonClicked();
//            }
//        });
        Button editRelativeParentButtonChild = (Button)rootView.findViewById(R.id.editRelativeParentBtnViewFEID);
        editRelativeParentButtonChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.editButtonClicked();
            }
        });
//        editRelativeParent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mListener.editButtonClicked();
//            }
//        });
        Button closeFEID = (Button)rootView.findViewById(R.id.closeFEID);
        closeFEID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return rootView;
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
        public void onFragmentInteraction(Uri uri);
        public void editButtonClicked();
    }

}
