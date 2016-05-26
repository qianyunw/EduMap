package com.example.edumap;


import bin.classes.com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static int result;
    private static int x;
    private static int y;
    private static int imageWidth;
    private static int imageHeight;
    private static int bitmapWidth;
    private static int bitmapHeight;
    private static double scaleWidth;
    private static double scaleHeight;
    private static ImageView imageBeijing;
    private static ImageView alternative;
    private static Button[] buttonDistrict;
    private static int buttonDistrictResult;
    private static int bottomResult;
    private static int[] pic;
    private static Bitmap bitmap;  
    private static Bitmap[] district;
    private static TextView[] mBottoms;
    private static String[] stringDistricts;
    private static SlidingMenu menu;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		ActivityCollector.addActivity(this);
//
//        menu = new SlidingMenu(this);
//        menu.setMode(SlidingMenu.LEFT);
//        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//        menu.setShadowWidthRes(R.dimen.shadow_width);
//        menu.setShadowDrawable(R.drawable.shadow);
//        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
//        menu.setFadeDegree(0.35f);
//        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
//        menu.setMenu(R.layout.location);
		ImageView re = (ImageView)findViewById(R.id.img_return);
		re.setVisibility(View.INVISIBLE);
		bottomResult = -1;
		mBottoms = new TextView[4];
		mBottoms[0] = (TextView)findViewById(R.id.tv_tab_bottom_01);
		mBottoms[1] = (TextView)findViewById(R.id.tv_tab_bottom_02);
		mBottoms[2] = (TextView)findViewById(R.id.tv_tab_bottom_03);
		mBottoms[3] = (TextView)findViewById(R.id.tv_tab_bottom_04);
		for(int i = 0; i < 4; i++)
        {
        	mBottoms[i].setOnClickListener(new bottomClickListener(i));
        }
		
		imageBeijing = (ImageView) findViewById(R.id.image_beijing);  
		alternative = (ImageView) findViewById(R.id.alternative);  
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.beijing);
        result = -1;
        
        pic = new int [10];
        pic[0] = R.drawable.changping;
        pic[1] = R.drawable.daxing;
        pic[2] = R.drawable.fangshan;
        pic[3] = R.drawable.huairou;
        pic[4] = R.drawable.mentougou;
        pic[5] = R.drawable.miyun;
        pic[6] = R.drawable.pinggu;
        pic[7] = R.drawable.shunyi;
        pic[8] = R.drawable.tongzhou;
        pic[9] = R.drawable.yanqing;
        
        stringDistricts = new String[10];
        stringDistricts[0] = "昌平区";
        stringDistricts[1] = "大兴区";
        stringDistricts[2] = "房山区";
        stringDistricts[3] = "怀柔区";
        stringDistricts[4] = "门头沟区";
        stringDistricts[5] = "密云区";
        stringDistricts[6] = "平谷区";
        stringDistricts[7] = "顺义区";
        stringDistricts[8] = "通州区";
        stringDistricts[9] = "延庆区";
        
        
        district = new Bitmap [10];
        //top = imageBeijing.getBottom(); 注：必须写在oncreat后面才不为0
        //left = imageBeijing.getRight();
        for(int i = 0; i < 10; i++)
        {
        	alternative.setImageResource(pic[i]);
        	district[i] = BitmapFactory.decodeResource(getResources(), pic[i]);
        }
        imageBeijing.setOnClickListener(new clickListener());  
        imageBeijing.setOnTouchListener(new touchListener());  
        
        buttonDistrictResult = -1;
        buttonDistrict = new Button[6];
        buttonDistrict[0] = (Button) findViewById(R.id.chaoyang);
        buttonDistrict[1] = (Button) findViewById(R.id.dongcheng);
        buttonDistrict[2] = (Button) findViewById(R.id.fengtai);
        buttonDistrict[3] = (Button) findViewById(R.id.haidian);
        buttonDistrict[4] = (Button) findViewById(R.id.shijingshan);
        buttonDistrict[5] = (Button) findViewById(R.id.xicheng);  
        
        for(int i = 0; i < 6; i++)
        {
        	buttonDistrict[i].setOnClickListener(new buttonDistrictClickListener(i));
        } 

	}
	private class bottomClickListener implements OnClickListener{
		private int temp;
		
		public bottomClickListener(int i) {
			temp=i;
		}
		public void onClick(View v) {  
			if(bottomResult > -1)
				mBottoms[bottomResult].setTextColor(getResources().getColor(R.color.black));
			bottomResult = temp;
			mBottoms[bottomResult].setTextColor(getResources().getColor(R.color.blue));
			
//			Intent it = new Intent(MainActivity.this, Tab.class);
//			Bundle bundle=new Bundle();
//			bundle.putInt("bottom", bottomResult);
//			it.putExtras(bundle); 
//			startActivity(it);
//			

        }  
    }; 
	
	private class buttonDistrictClickListener implements OnClickListener{
		private int temp;
		
		public buttonDistrictClickListener(int i) {
			temp=i;
		}
		public void onClick(View v) {  
			alternative.setVisibility(View.INVISIBLE);
			if(buttonDistrictResult > -1)
				buttonDistrict[buttonDistrictResult].setBackgroundResource(R.drawable.greenbutton);
			buttonDistrict[temp].setBackgroundResource(R.drawable.bluebutton);
			buttonDistrictResult = temp;
			
			Intent it1 = new Intent(MainActivity.this, SecondActivity.class);
			Bundle bundle1=new Bundle();
			bundle1.putString("districtResult", (String) buttonDistrict[buttonDistrictResult].getText());
			it1.putExtras(bundle1); 
			startActivity(it1);
			
        }  
    };  
    
	public class clickListener implements OnClickListener{   
        public void onClick(View v) {  
        	for(int i = 0; i < 10; i++)
            {
            	if(district[i].getPixel((int)(x*scaleWidth),(int)((y+32)*scaleHeight))!=0)
            	{
            		result = i;
            		break;
            	}
            }
        	if(buttonDistrictResult > -1)
        	{
        		buttonDistrict[buttonDistrictResult].setBackgroundResource(R.drawable.greenbutton);
        		buttonDistrictResult = -1;
        	}
        	alternative.setImageResource(pic[result]);
        	alternative.setVisibility(View.VISIBLE);

			Intent it2 = new Intent(MainActivity.this, SecondActivity.class);
			Bundle bundle2=new Bundle();
			bundle2.putString("districtResult", stringDistricts[result]);
			it2.putExtras(bundle2); 
			startActivity(it2);
        }  
    };  
      
	public class touchListener implements OnTouchListener{   
		
        public boolean onTouch(View v, MotionEvent event)   
        {  
        	imageWidth = imageBeijing.getWidth(); 
        	imageHeight = imageBeijing.getHeight();
        	bitmapWidth = bitmap.getWidth();
        	bitmapHeight = bitmap.getHeight();
        	scaleWidth = (double)bitmapWidth/imageWidth;
        	scaleHeight = (double)bitmapHeight/imageHeight;
            x = (int) event.getX();
            y = (int) event.getY();
            
        	if(bitmap.getPixel((int)(x*scaleWidth),(int)((y+32)*scaleHeight))==0)  
            {  
                Log.i("touming", "透明区域");  
                return true;//透明区域返回true  
            }  
            else
            {
            	Log.i("x", String.valueOf(x));
            	Log.i("y", String.valueOf(y));
            	return false; 
            }
             
        }        
    };  
	
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	ActivityCollector.removeActivity(this);
    }
    
	
}
