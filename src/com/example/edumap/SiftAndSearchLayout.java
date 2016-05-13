package com.example.edumap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class SiftAndSearchLayout extends LinearLayout{
	
	public SiftAndSearchLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.sift_and_search, this);
	}
}
