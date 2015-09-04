package com.skr.expensetrack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

interface CalenderHomeFragmentClickListener{
    public void onItemClickListener();
}

public class CalenderHomeFragment extends Fragment
{
    public String TAG ;
    public static final String MonthName = "MonthName";
    public static final String Year = "Year";
    public static final String ExpenceValue = "ExpenceValue";
    public static final String IncomeVal = "IncomeVal";
    String monthName ;
    String year ;
    Long expenceValue;
    Long incomeVal;

    public CalenderHomeFragmentClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(CalenderHomeFragmentClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    CalenderHomeFragmentClickListener onClickListener;


    @Override
    public String toString() {
        return TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_calender_home, container, false);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener != null){
                    onClickListener.onItemClickListener();
                }
            }
        });
         monthName = getArguments().getString(MonthName);
         year = getArguments().getString(Year);
        expenceValue = getArguments().getLong(ExpenceValue);
        incomeVal = getArguments().getLong(IncomeVal);
        TextView textViewFirst =(TextView)rootView.findViewById(R.id.textViewFirst);
        textViewFirst.setText(monthName);
        TextView textViewSecond =(TextView)rootView.findViewById(R.id.textViewSecond);
        textViewSecond.setText(year);
        TAG = monthName+" "+year;
     //   setItemLayout(64);
      //  rootView.findViewById(R.id.image).setBackgroundResource(getArguments().getInt("image"));
        return rootView;
    }

    public void setItemLayout(int dpVal){
        View rootView = getView();
        if(rootView != null) {
            LinearLayout.LayoutParams layout_description = new LinearLayout.LayoutParams(dpVal, dpVal
            );
            RelativeLayout parentLayoutCarouselViewHome = (RelativeLayout) rootView.findViewById(R.id.parentLayoutCarouselViewHome);
            parentLayoutCarouselViewHome.setLayoutParams(layout_description);

        }
    }
}