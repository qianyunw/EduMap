package com.example.edumap;

import java.util.ArrayList;
import java.util.List;

import com.viewpagerindicator.TabPageIndicator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ResourceAsColor")
public class Tab extends FragmentActivity  
{  
	public static final String[] TITLES = new String[] { "Ȩ������", "������Դ", "��ѧ����", "����������"};  
	private TabPageIndicator mIndicator ;    
    private ViewPager mViewPager;  
    private FragmentPagerAdapter mAdapter;  
    private List<Fragment> mFragments = new ArrayList<Fragment>();  
      
    /** 
     * �ײ��ĸ���ť 
     */   
    private int bottomResult;
  
    @Override  
    protected void onCreate(Bundle savedInstanceState)  
    {  
    	Bundle bundle=getIntent().getExtras();
    	bottomResult = bundle.getInt("bottom");
    	
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.tab);  
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);  
        mIndicator = (TabPageIndicator) findViewById(R.id.id_indicator);  
        
        initView();  
          
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())  
        {  

            @Override  
            public Fragment getItem(int arg0)  
            {  
            	return mFragments.get(arg0);
            }  
          
            @Override  
            public CharSequence getPageTitle(int position)  
            {  
                return TITLES[position % TITLES.length];  
            }  
          
            @Override  
            public int getCount()  
            {  
                return TITLES.length;  
            }  
          
        };  
        
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener()
        {  
            private int currentIndex;  
            public void onPageSelected(int position) {}  
            public void onPageScrolled(int arg0, float arg1, int arg2)  {}  
            public void onPageScrollStateChanged(int arg0)  {}  
        });
          
        mIndicator.setViewPager(mViewPager, bottomResult);   
        
    }  
  
    private void initView()  
    {  
        MainTab01 tab01 = new MainTab01();  
        MainTab02 tab02 = new MainTab02();  
        MainTab03 tab03 = new MainTab03();  
        MainTab04 tab04 = new MainTab04(); 
        
        mFragments.add(tab01);  
        mFragments.add(tab02);  
        mFragments.add(tab03);  
        mFragments.add(tab04);  
        
    }  
}

/*
����һ������һ�����SlidingMenu�Ļ����tab�Ŀ�ܶ��á�
����������tab�ĳ�һ�����֡�



*/