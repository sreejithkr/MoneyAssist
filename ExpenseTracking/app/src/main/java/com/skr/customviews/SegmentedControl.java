package com.skr.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.skr.expensetrack.R;

/**
 * 
 */
public class SegmentedControl
    extends RadioGroup {
    /**
     * 
     */
	public SegmentedControl( Context context ) {
		super( context );
		init();
	}

	/**
	 * 
	 */
	public SegmentedControl( Context context, AttributeSet attrs ) {
		super( context, attrs );
		init();
	}

	/**
	 * 
	 */
	private void init() {
		// Set background
		setBackgroundResource(R.drawable.segmented_control_bg );
	}

    /**
     * 
     */
    @Override
    public LayoutParams generateLayoutParams( AttributeSet attrs ) {
        return adjustLayoutParams( super.generateLayoutParams( attrs ) );
    }

    /**
     * 
     */
    @Override
    protected LinearLayout.LayoutParams generateDefaultLayoutParams() {
        return adjustLayoutParams( (LayoutParams)super.generateDefaultLayoutParams() );
    }

    /**
     *
     */
    private LayoutParams adjustLayoutParams( LayoutParams params ) {
        // Make all buttons the same size if the orientation is horizontal 
        if( getOrientation() == LinearLayout.HORIZONTAL ) {
            params.width  = 0;
            params.weight = 1;
        }

        return params;
    }
}
