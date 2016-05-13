package com.example.edumap;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


@SuppressLint("NewApi")
public class MainTab04 extends Fragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View newsLayout = inflater.inflate(R.layout.main_tab_04, container, false);
		return newsLayout;
	}

}
