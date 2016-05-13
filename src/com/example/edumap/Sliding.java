package com.example.edumap;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class Sliding  extends Activity
{
	private SlidingMenu mMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sliding);
		mMenu = (SlidingMenu) findViewById(R.id.id_menu);
	}
}