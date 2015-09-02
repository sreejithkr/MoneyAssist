package com.skr.expensetrack;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.skr.AppController;
import com.skr.customviews.ViewCarousel;
import com.skr.customviews.ViewCarousel.ViewCarouselAdapter;
import com.skr.customviews.ViewCarousel.ViewCarouselTransformer;
import com.skr.datahelper.CheckBoxListData;
import com.skr.datahelper.DBHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeExpenceTrackFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeExpenceTrackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeExpenceTrackFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String expenceTotalHome = "expenceTotalHome";
    public static final String incomeTotalHome = "incomeTotalHome";
    public static final String saveValuesForHomeCalledOnceFlag = "saveValuesForHomeCalledOnceFlag";
    public Boolean getTotalFlag = false;

    HashMap<String,String> messageMap;
    public static final String timePeriodKey = "timePeriodKey";
    public static final String expenseMessageKey = "expenseMessageKey";
    public static final String incomeMessageKey = "incomeMessageKey";
    String timePeriod = "";

    String expenseMessage ="";
    String incomeMessage ="";
    PieChart pieChart;
    public Boolean ifGetTotalToBeShow = false;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Boolean toBottomFlag = true;
    ImageView mTopImage;
    ImageView mBottomImage;
    Bitmap mBmp1;// = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), splitYCoord);
    Bitmap mBmp2;// = Bitmap.createBitmap(bmp, 0, splitYCoord, bmp.getWidth(), bmp.getHeight() - splitYCoord);
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
     * @return A new instance of fragment HomeExpenceTrackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeExpenceTrackFragment newInstance(String param1, String param2) {
        HomeExpenceTrackFragment fragment = new HomeExpenceTrackFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeExpenceTrackFragment() {
        // Required empty public constructor
    }

    public void animateToShowQuickAdd(){
//        final float scale = getActivity().getResources().getDisplayMetrics().density;
//        final int minWidth =  (int)(50 * scale + 0.5f);
//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        final int height = displaymetrics.heightPixels;
//        final int width = displaymetrics.widthPixels;
//        RelativeLayout summaryParentHomeFragment = (RelativeLayout)getView().findViewById(R.id.mainParentHomeFragment);
//        RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) summaryParentHomeFragment.getLayoutParams();
//        mParams.topMargin = 400;
//       // summaryParentHomeFragment.setLayoutParams(mParams);
//
//        TranslateAnimation downAnimation = makeAnimation(toBottomFlag);
//        toBottomFlag = !toBottomFlag;
//        summaryParentHomeFragment.startAnimation(downAnimation );

//        View root = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
//        root.setDrawingCacheEnabled(true);
//        Bitmap bmp = root.getDrawingCache();
//
//        int splitYCoord = bmp.getHeight() / 2;//(splitYCoord != -1 ? splitYCoord : bmp.getHeight() / 2);
//        if (splitYCoord > bmp.getHeight())
//            throw new IllegalArgumentException("Split Y coordinate [" + splitYCoord + "] exceeds the activity's height [" + bmp.getHeight() + "]");
//         mBmp1 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), splitYCoord);
//         mBmp2 = Bitmap.createBitmap(bmp, 0, splitYCoord, bmp.getWidth(), bmp.getHeight() - splitYCoord);
//          int[] mLoc1;
//          int[] mLoc2;
//        mLoc1 = new int[]{0, root.getTop()};
//        mLoc2 = new int[]{0, root.getTop() + splitYCoord};
//
//        mTopImage = createImageView(getActivity(), mBmp1, mLoc1);
//        mBottomImage = createImageView(getActivity(), mBmp2, mLoc2);
//
//        AnimatorSet mSetAnim = new AnimatorSet();
//        mTopImage.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        mBottomImage.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        mSetAnim.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation){
//
//            }
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                clean(getActivity());
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//                clean(getActivity());
//            }
//            @Override
//            public void onAnimationRepeat(Animator animation){
//
//            }
//
//        });
//
//        Animator anim1 = ObjectAnimator.ofFloat(mTopImage, "translationY", 50);
//        Animator anim2 = ObjectAnimator.ofFloat(mBottomImage,"translationY",30);
//
//        mSetAnim.setDuration(500);
//        mSetAnim.playTogether(anim1, anim2);
//        mSetAnim.start();



    }

    private static ImageView createImageView(Activity destActivity, Bitmap bmp, int loc[]) {
        ImageView imageView = new ImageView(destActivity);
        imageView.setImageBitmap(bmp);

        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP;
        windowParams.x = loc[0];
        windowParams.y = loc[1];
        windowParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        windowParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        windowParams.flags =
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
        destActivity.getWindowManager().addView(imageView, windowParams);

        return imageView;
    }

    private void clean(Activity activity) {
        if (mTopImage != null) {
            mTopImage.setLayerType(View.LAYER_TYPE_NONE, null);
            try {
                activity.getWindowManager().removeViewImmediate(mBottomImage);
            } catch (Exception ignored) {}
        }
        if (mBottomImage != null) {
            mBottomImage.setLayerType(View.LAYER_TYPE_NONE, null);
            try {
                activity.getWindowManager().removeViewImmediate(mTopImage);
            } catch (Exception ignored) {}
        }

        mBmp1 = null;
        mBmp2 = null;
    }


//    private void setBottomMargin(View view, int bottomMarginInDips)
//    {
//        ViewGroup.MarginLayoutParams layoutParams =
//                (ViewGroup.MarginLayoutParams)view.getLayoutParams();
//        layoutParams.topMargin = dipsToPixels(bottomMarginInDips);
//        view.requestLayout();
//    }
//
//    private int dipsToPixels(int dips)
//    {
//        final float scale = getResources().getDisplayMetrics().density;
//        return (int)(dips * scale + 0.5f);
//    }
    private TranslateAnimation makeAnimation(final Boolean toBottomFlag)
    {
        TranslateAnimation animation = toBottomFlag ?
                new TranslateAnimation(0, 0, 0, 400) : new TranslateAnimation(0, 0, 400,0) ;
        animation.setDuration(400);
        animation.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                RelativeLayout summaryParentHomeFragment = (RelativeLayout)getView().findViewById(R.id.mainParentHomeFragment);

                // Cancel the animation to stop the menu from popping back.
                summaryParentHomeFragment.clearAnimation();

                RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) summaryParentHomeFragment.getLayoutParams();
                if(toBottomFlag) {
                    mParams.topMargin = 400;
                }else{
                    mParams.topMargin = 0;
                }
                 summaryParentHomeFragment.setLayoutParams(mParams);
            }

            public void onAnimationStart(Animation animation) {}

            public void onAnimationRepeat(Animation animation) {}
        });
        return animation;
    }


    public void reloadValuesForTotal(Long expence,Long income,ArrayList<CheckBoxListData> checkBoxListDataArrayList,Pair<Boolean,Boolean> allSlectedFlagPair, Pair<String,String> startEndDatePair,Pair<Boolean,Boolean> noIncomeExpenceSelectedPair){
//        TextView expenseAmountHomeText = (TextView)getView().findViewById(R.id.expenseAmountHomeText);
//        TextView incomeAmountHomeText = (TextView)getView().findViewById(R.id.incomeAmountHomeText);
//        expenseAmountHomeText.setText(expence);
//        incomeAmountHomeText.setText(income);income
        RelativeLayout grapWithDataParent = (RelativeLayout)getView().findViewById(R.id.grapWithDataParent);
        RelativeLayout nodataParent = (RelativeLayout)getView().findViewById(R.id.nodataParent);

        Boolean noExpenceCategorySelected = true;
        Boolean noIncomeCategorySelected = true;
        if(expence== 0 && income == 0){
            grapWithDataParent.setVisibility(View.INVISIBLE);
            nodataParent.setVisibility(View.VISIBLE);
        }else{
            grapWithDataParent.setVisibility(View.VISIBLE);
            nodataParent.setVisibility(View.GONE);
            pieChart = configureChart(pieChart);
            pieChart = setData(pieChart,income,expence);
            pieChart.animateXY(1500, 1500);
            Legend l = pieChart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(5f);
            l.setTextSize(12);
            l.setTextColor(getResources().getColor(R.color.app_green));
        }
        String expenseCategoryString = "";
        String incomeCategoryString = "";

        for(int count =0;count<checkBoxListDataArrayList.size();count++){
            CheckBoxListData checkBoxListData = checkBoxListDataArrayList.get(count);
            if(checkBoxListData.ifExpence) {
                noExpenceCategorySelected = false;
            }

            if(!checkBoxListData.ifExpence){
                noIncomeCategorySelected = false;
            }
            if(checkBoxListData.ifExpence){

                expenseCategoryString = expenseCategoryString + checkBoxListData.name + ",";
            }else{
                incomeCategoryString = incomeCategoryString + checkBoxListData.name + ",";
            }
        }

        String startDate = startEndDatePair.first;
        String endDate = startEndDatePair.second;

        if(startDate == null || startDate.isEmpty()){
            startDate = null;
        }
        if(endDate == null || endDate.isEmpty()){
            endDate = null;
        }
        String timePeriodHeadingMessage = "";
        if(startDate != null && endDate != null) {
            startDate = AppController.pareseDate_in_DD_dash_MM_dash_YYYY_to_Month_comma_Day_space_Year(startDate);
            endDate = AppController.pareseDate_in_DD_dash_MM_dash_YYYY_to_Month_comma_Day_space_Year(endDate);
            timePeriodHeadingMessage = "From "+startDate+" till "+endDate;
            timePeriodHeadingMessage.replace("!*dr1*!",startDate);
            timePeriodHeadingMessage.replace("!*dr2*!",endDate);

        }else if(startDate != null   && endDate == null){
            startDate = AppController.pareseDate_in_DD_dash_MM_dash_YYYY_to_Month_comma_Day_space_Year(startDate);

            timePeriodHeadingMessage = "From "+startDate+" till date";


        }else if(startDate == null   && endDate != null){
            endDate = AppController.pareseDate_in_DD_dash_MM_dash_YYYY_to_Month_comma_Day_space_Year(endDate);

            timePeriodHeadingMessage = "From start till "+endDate;
        }else {



        }
        messageMap = new HashMap<>();
        String noCategory = getResources().getString(R.string.no_category_default_msg_show_total_details);

        String allCategory = getResources().getString(R.string.allCategory_default_msg_show_total_details);
         timePeriod =  getResources().getString(R.string.timePeriod_default_msg_show_total_details);

         expenseMessage ="";
         incomeMessage ="";

        if(allSlectedFlagPair.first){
            //expence incurren for all category


                expenseMessage = allCategory;



        }else{

            expenseMessage =  expenseCategoryString;
            if(expenseMessage.endsWith(","))
            {
                expenseMessage = expenseMessage.substring(0,expenseMessage.length() - 1);
            }

        }

        if(allSlectedFlagPair.second){
            //income received from all category
                incomeMessage = allCategory;
        }else{
            incomeMessage = incomeCategoryString;
            if(incomeMessage.endsWith(","))
            {
                incomeMessage = incomeMessage.substring(0,incomeMessage.length() - 1);
            }


        }
        if(!timePeriodHeadingMessage.isEmpty()){

            timePeriod = timePeriodHeadingMessage;

        }

        if(noIncomeExpenceSelectedPair.first){
            expenseMessage = noCategory;

        }
        if(noIncomeExpenceSelectedPair.second){
            incomeMessage = noCategory;
        }
        messageMap.put(expenseMessageKey,expenseMessage);

        messageMap.put(incomeMessageKey,incomeMessage);
        messageMap.put(timePeriodKey,timePeriod);



        LinearLayout pagerParent = (LinearLayout)getView().findViewById(R.id.pagerParent);
        pagerParent.setVisibility(View.GONE);

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
        if (mListener != null) {
            mListener.onCreatedFragment();
        }

        View rootView = inflater.inflate(R.layout.fragment_home_expence_track, container, false);
RelativeLayout grapWithDataParent = (RelativeLayout)rootView.findViewById(R.id.grapWithDataParent);
         pieChart = (PieChart) rootView.findViewById(R.id.chart);

        LinearLayout myLayout = (LinearLayout)rootView.findViewById(R.id.pagerParent);
        Button calc_new_summary_button = (Button)rootView.findViewById(R.id.calc_new_summary_button);
        Button more_info_Link = (Button)rootView.findViewById(R.id.more_info_Link);
        more_info_Link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMoreInfoButtonClick(messageMap);
                }
            }
        });


        rootView.findViewById(R.id.more_info_LinkParent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMoreInfoButtonClick(messageMap);
                }
            }
        });
        rootView.findViewById(R.id.more_info_LinkImgView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMoreInfoButtonClick(messageMap);
                }
            }
        });

        ///Add an Expnse Or income functioality button lcick
        rootView.findViewById(R.id.addExpenceIncomeHomeParent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onAddExpenceOrIncome();
                }
            }
        });
        rootView.findViewById(R.id.addExpenceIncomeHomeImgView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onAddExpenceOrIncome();
                }
            }
        });
        rootView.findViewById(R.id.addExpenceIncomeHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onAddExpenceOrIncome();
                }
            }
        });
        if(getTotalFlag){
            rootView.findViewById(R.id.swipeIndicator).setVisibility(View.GONE);
            calc_new_summary_button.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.more_info_LinkParent).setVisibility(View.VISIBLE);

            myLayout.setVisibility(View.GONE);
            calc_new_summary_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onCalculateTotalButtonClick();
                    }
                }
            });


        }else{
            rootView.findViewById(R.id.more_info_LinkParent).setVisibility(View.GONE);

//            messageMap = new HashMap<>();
//            String allCategory = getResources().getString(R.string.allCategory_default_msg_show_total_details);
//
//            messageMap.put(expenseMessageKey,allCategory);
//
//            messageMap.put(incomeMessageKey,allCategory);
//            messageMap.put(timePeriodKey,getResources().getString(R.string.timePeriod_default_msg_show_total_details));
            calc_new_summary_button.setVisibility(View.GONE);
            myLayout.setVisibility(View.VISIBLE);

            List<android.support.v4.app.Fragment> fragments = new ArrayList<>();
          //  int[] images = {R.drawable.field, R.drawable.landscape, R.drawable.leaf, R.drawable.roof, R.drawable.valley};

            Date today = new Date();

            Calendar cal = Calendar.getInstance();
            cal.setTime(today) ;
          //  cal.get(Calendar.DATE);
            Integer month = cal.get(Calendar.MONTH)+1;
            Integer year = cal.get(Calendar.YEAR);


            SharedPreferences settings = getActivity().getSharedPreferences(AppController.MY_APP_PREFERENCE, 0);
            SharedPreferences.Editor editor = settings.edit();
           if(settings.getBoolean(saveValuesForHomeCalledOnceFlag,true)){
               DBHelper.getInstance(getActivity()).saveValuesForHome();
           }
            final TextView expenseAmountHomeText = (TextView)rootView.findViewById(R.id.expenseAmountHomeText);
            final TextView incomeAmountHomeText = (TextView)rootView.findViewById(R.id.incomeAmountHomeText);
            Long expenceValue0 = settings.getLong(expenceTotalHome+0,0);
            Long incomeVal0 = settings.getLong(incomeTotalHome+0,0);
            RelativeLayout nodataParent = (RelativeLayout)rootView.findViewById(R.id.nodataParent);


            if(expenceValue0 == 0 && incomeVal0 == 0){
                grapWithDataParent.setVisibility(View.INVISIBLE);
                nodataParent.setVisibility(View.VISIBLE);
            }else{
                grapWithDataParent.setVisibility(View.VISIBLE);
                nodataParent.setVisibility(View.GONE);
                pieChart = configureChart(pieChart);
                pieChart = setData(pieChart,incomeVal0.longValue(),expenceValue0.longValue());
                pieChart.animateXY(1500, 1500);
                Legend l = pieChart.getLegend();
                l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
                l.setXEntrySpace(7f);
                l.setYEntrySpace(5f);
                l.setTextSize(12);
                l.setTextColor(getResources().getColor(R.color.app_green));
            }

//            expenseAmountHomeText.setText(expenceValue0+" "+AppController.getCurrencyString().trim());
//            incomeAmountHomeText.setText(incomeVal0+" "+AppController.getCurrencyString().trim());
            for (int i=0; i<6; i++)
            {
                long expenceValue = settings.getLong(expenceTotalHome+i,0);
                long incomeVal = settings.getLong(incomeTotalHome+i,0);
                Bundle b = new Bundle();
                b.putString(CalenderHomeFragment.MonthName, AppController.stringFromInt(month));
                b.putString(CalenderHomeFragment.Year, year+"");
                b.putLong(CalenderHomeFragment.ExpenceValue,expenceValue);
                b.putLong(CalenderHomeFragment.IncomeVal,incomeVal);
                CalenderHomeFragment f = new CalenderHomeFragment();
                f.setArguments(b);
                fragments.add(f);
                month = month - 1;
                if(month == 0){
                    month = 12;
                    year = year-1;
                }
            }
            Log.e("inflater.inflate(R.layout.fragment_home_expence_track, container, false);","");


            //  android.support.v4.view.ViewPager pager = (ViewPager) rootView.findViewById(R.id.viewPager);
            final android.support.v4.view.ViewPager pager = new ViewPager(getActivity());
            pager.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));

            pager.setId(R.id.pagerIdentifier);
            myLayout.addView(pager);
            pager.setAdapter(new ViewCarouselAdapter(this,  getActivity().getSupportFragmentManager(), fragments));
            pager.setPageTransformer(true, new ViewCarouselTransformer());
            ViewCarousel vc = new ViewCarousel(this, getActivity().getSupportFragmentManager(), fragments,rootView,pager);
            vc.setChildrenWidth(160);
            pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    CalenderHomeFragment fragment = (CalenderHomeFragment)((ViewCarouselAdapter)pager.getAdapter()).getItem(position);


                }

                @Override
                public void onPageSelected(int position) {

                    ViewCarouselAdapter adapter = (ViewCarouselAdapter)pager.getAdapter();
                    CalenderHomeFragment fragment = (CalenderHomeFragment)adapter.getItem(position);
                   // expenseAmountHomeText.setText(fragment.expenceValue+" "+AppController.getCurrencyString().trim());
                   // incomeAmountHomeText.setText(fragment.incomeVal+" "+AppController.getCurrencyString().trim());
                    RelativeLayout grapWithDataParent = (RelativeLayout)getView().findViewById(R.id.grapWithDataParent);
                    RelativeLayout nodataParent = (RelativeLayout)getView().findViewById(R.id.nodataParent);


                    if(fragment.incomeVal == 0 && fragment.expenceValue == 0){
                        grapWithDataParent.setVisibility(View.INVISIBLE);
                        nodataParent.setVisibility(View.VISIBLE);
                    }else{
                        grapWithDataParent.setVisibility(View.VISIBLE);
                        nodataParent.setVisibility(View.GONE);
                        pieChart = configureChart(pieChart);
                        pieChart = setData(pieChart,fragment.incomeVal.longValue(),fragment.expenceValue.longValue());
                        pieChart.animateXY(1500, 1500);
                        Legend l = pieChart.getLegend();
                        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
                        l.setXEntrySpace(7f);
                        l.setYEntrySpace(5f);
                        l.setTextSize(12);
                        l.setTextColor(getResources().getColor(R.color.app_green));
                    }

                    //  adapter.correctLayout(position);
                    Log.e("CalenderHomeFragment fragment ",fragment.toString());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }


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
        public void onCalculateTotalButtonClick();
        public void onMoreInfoButtonClick(HashMap<String,String> message);
        public void onAddExpenceOrIncome();
    }

    public PieChart configureChart(PieChart chart) {
        chart.setHoleColor(getResources().getColor(android.R.color.background_dark));
        chart.setHoleRadius(50f);
        chart.setHoleColor(getResources().getColor(R.color.backgroud_white));
        chart.setDescription("");
        chart.setTransparentCircleRadius(5f);
        chart.setDrawYValues(true);
        chart.setDrawCenterText(true);
        chart.setDrawHoleEnabled(true);
        chart.setRotationAngle(0);
        chart.setDrawXValues(false);
        chart.setRotationEnabled(false);
        chart.setUsePercentValues(false);
        chart.setTouchEnabled(false);

        chart.setValueFormatter(new ValueFormatter()
        {
            @Override
            public String getFormattedValue(float value)
            {
                return value + " " + AppController.getCurrencyString();
            }
        });
//        String title = getString(R.string.expence_and_income);
//
//        if(!AppController.getCurrencyString().isEmpty()){
//            title = title + "\n in \n" + AppController.getCurrencyString();
//        }


        return chart;
    }
    private PieChart setData(PieChart chart,Long income,Long expence) {

     //   l.setYOffset(0f);
        ArrayList<Entry> yVals1 = new ArrayList<>();
        yVals1.add(new Entry(income, 0));
        yVals1.add(new Entry(expence, 1));
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add(getString(R.string.income));
        xVals.add(getString(R.string.expense));
        PieDataSet set1 = new PieDataSet(yVals1, "");
        set1.setSliceSpace(0f);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.light_green));
        colors.add(getResources().getColor(R.color.light_red));
        set1.setColors(colors);
        PieData data = new PieData(xVals, set1);
        chart.setData(data);
        chart.highlightValues(null);
        chart.invalidate();
        return chart;
    }


}
