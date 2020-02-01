package com.sadam.sadamlibarary.Layouts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

public class MyFramLayout extends FrameLayout {
    public MyFramLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public MyFramLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyFramLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyFramLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
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
