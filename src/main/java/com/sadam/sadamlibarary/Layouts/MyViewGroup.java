package com.sadam.sadamlibarary.Layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

public class MyViewGroup extends ViewGroup {
    public MyViewGroup(Context context) {
        super(context);
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    public void init() {
        setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                requestApplyInsets();
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {

            }
        });
    }

    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        for (int index = 0; index < getChildCount(); index++) {
            getChildAt(index).dispatchApplyWindowInsets(insets);
        }
        return insets;
    }
}
