package com.example.edumap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LocationLayout extends LinearLayout{
	
	public LocationLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.location, this);
		

		ImageView re = (ImageView)findViewById(R.id.img_return);
		re.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				ActivityCollector.removeActivity();
			}
		});
		
	}
}
