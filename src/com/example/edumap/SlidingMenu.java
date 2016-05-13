package com.example.edumap;

import com.zhy.utils.ScreenUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class SlidingMenu extends ScrollView  
{  
    /** 
     * 屏幕宽度 
     */  
    private int mScreenHeight;  
    /** 
     * dp 
     */  
    private int mMenuRightPadding = 50;  
    /** 
     * 菜单的宽度 
     */  
    private int mMenuHeight;  
    private int mHalfMenuHeight;  
  
    private boolean once;  
  
    public SlidingMenu(Context context, AttributeSet attrs)  
    {  
        super(context, attrs);  
        mScreenHeight = ScreenUtils.getScreenHeight(context);  
    }  
  
    @Override  
    protected void onMeasure(int HeightMeasureSpec, int heightMeasureSpec)  
    {  
        /** 
         * 显示的设置一个宽度 
         */  
        if (!once)  
        {  
            LinearLayout wrapper = (LinearLayout) getChildAt(0);  
            ViewGroup menu = (ViewGroup) wrapper.getChildAt(0);  
            ViewGroup content = (ViewGroup) wrapper.getChildAt(1);  
            // dp to px  
            mMenuRightPadding = (int) TypedValue.applyDimension(  
                    TypedValue.COMPLEX_UNIT_DIP, mMenuRightPadding, content  
                            .getResources().getDisplayMetrics());  
  
            mMenuHeight = mScreenHeight - mMenuRightPadding;  
            mHalfMenuHeight = mMenuHeight / 2;  
            menu.getLayoutParams().height = mMenuHeight;  
            content.getLayoutParams().height = mScreenHeight;  
  
        }  
        super.onMeasure(HeightMeasureSpec, heightMeasureSpec);  
  
    }  
  
    @Override  
    protected void onLayout(boolean changed, int l, int t, int r, int b)  
    {  
        super.onLayout(changed, l, t, r, b);  
        if (changed)  
        {  
            // 将菜单隐藏  
            this.scrollTo(0 , mMenuHeight);  
            once = true;  
        }  
    }  
  
    @Override  
    public boolean onTouchEvent(MotionEvent ev)  
    {  
        int action = ev.getAction();  
        switch (action)  
        {  
        // Up时，进行判断，如果显示区域大于菜单宽度一半则完全显示，否则隐藏  
        case MotionEvent.ACTION_UP:  
            int scrollY = getScrollY();  
            if (scrollY > mHalfMenuHeight)  
                this.smoothScrollTo(0, mHalfMenuHeight);  
            else  
                this.smoothScrollTo(0, 0);  
            return true;  
        }  
        return super.onTouchEvent(ev);  
    }  
  
} 