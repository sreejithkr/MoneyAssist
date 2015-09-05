package com.skr.customviews;

import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

import com.skr.expensetrack.CalenderHomeFragment;
import com.skr.expensetrack.HomeExpenceTrackFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewCarousel
{
    /** parent activity */
    private HomeExpenceTrackFragment activity;

    private ViewPager pager;

    private ViewCarouselAdapter adapter;

    /** width of the screen */
    private int screenWidth;

    /** height of the screen */
    private int screenHeight;

    /** used to convert between px and dp */
    private DisplayMetrics displayMetrics;

    private int childrenWidth;

    private int padding;

    private int spacing;

    public ViewCarousel(HomeExpenceTrackFragment activity, FragmentManager fm, List<Fragment> fragments,View rootView,ViewPager pager)
    {
        this.activity = activity;
        this.pager = pager;
        displayMetrics = activity.getResources().getDisplayMetrics();
        getScreenDimensions();


    }

    public void setChildrenWidth(int width)
    {
        this.childrenWidth = width;
        padding = 10;//screenWidth / 2 - dpToPx(childrenWidth / 2);
       // pager.setPadding(padding, 10, padding, 10);
        pager.setClipToPadding(false);
    }

    public void setSpacing(int spacing)
    {
        this.spacing = spacing;
        pager.setPageMargin(spacing);
    }


    /** get the width and height of the screen in pixels */
    private void getScreenDimensions()
    {
        Display display = activity.getActivity().getWindowManager().getDefaultDisplay();
        Point screen = new Point();
        display.getSize(screen);
        screenWidth = screen.x;
        screenHeight = screen.y;
    }

    /** convert dp to px */
    public int dpToPx(int dp)
    {
        return dp * (displayMetrics.densityDpi / 160);
    }

    /** convert px to dp */
    public int pxToDp(int px)
    {
        return px / (displayMetrics.densityDpi / 160);
    }

    public static class ViewCarouselAdapter extends FragmentStatePagerAdapter
    {
        private List<Fragment> fragments;

        public ViewCarouselAdapter(HomeExpenceTrackFragment activity, FragmentManager fm, List<Fragment> fragments)
        {

            super(fm);
            this.fragments = new ArrayList<>();
            this.fragments.addAll(fragments);

        }

        @Override
        public Fragment getItem(int position)
        {
            return fragments.get(position);
        }

        @Override
        public int getCount()
        {
            return fragments.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void correctLayout(int position)
        {

            for(int i =0;i<fragments.size();i++){
                CalenderHomeFragment fragment = (CalenderHomeFragment)this.getItem(i);
                if(i==position){
                    fragment.setItemLayout(96);
                }else{
                    fragment.setItemLayout(64);
                }
            }





        }

    }

    public static class ViewCarouselTransformer implements ViewPager.PageTransformer
    {
        private int transformation;

        public ViewCarouselTransformer()
        {

        }


        @Override
        public void transformPage(View view, float position)
        {
            ViewPager pager = (ViewPager) view.getParent();
            position -= pager.getPaddingRight() / (float) view.getWidth();
            view.setRotationY(position * 15);

        }



    }

}