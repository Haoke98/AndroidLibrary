package com.sadam.sadamlibarary.MyView.ButtonLikedHeart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

import com.sadam.sadamlibarary.R;


@SuppressLint("AppCompatCustomView")
public class ButtonLikeHeart extends Button {
    private Drawable drawable_like;
    private Drawable drawable_unlike;
    private Boolean liked = false;
    private OnLikedStateChangeListener onLikedStateChangeListener;


    public ButtonLikeHeart(Context context) {
        super(context);
        init(null);
    }

    public ButtonLikeHeart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ButtonLikeHeart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public ButtonLikeHeart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    private void init(AttributeSet attributeSet) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.ButtonLikeHeart);
        drawable_like = typedArray.getDrawable(R.styleable.ButtonLikeHeart_liked_icon);
        drawable_unlike = typedArray.getDrawable(R.styleable.ButtonLikeHeart_unLiked_icon);
        liked = typedArray.getBoolean(R.styleable.ButtonLikeHeart_liked, false);
    }

    @Override
    public boolean performClick() {
        changeLikeState(!liked);
        return super.performClick();
    }

    @Override
    protected void onAttachedToWindow() {
        changeLikeState(liked);
        super.onAttachedToWindow();
    }

    private void changeLikeState(Boolean state) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (state) {
                setForeground(drawable_like);
            } else {
                setForeground(drawable_unlike);
            }
        }
        liked = state;
        if (onLikedStateChangeListener != null) {
            onLikedStateChangeListener.onLikedStateChange(state);
        }
    }

    public void setOnLikedStateChangeListener(OnLikedStateChangeListener onLikedStateChangeListener) {
        this.onLikedStateChangeListener = onLikedStateChangeListener;
    }
}
