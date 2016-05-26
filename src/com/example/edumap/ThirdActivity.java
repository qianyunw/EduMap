package com.example.edumap;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearch.OnDistrictSearchListener;
import com.amap.api.services.district.DistrictSearchQuery;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.SyncStateContract.Constants;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ThirdActivity extends Activity  implements OnDistrictSearchListener, OnMarkerClickListener,
OnInfoWindowClickListener, InfoWindowAdapter {
    private static Button buttonSift;
    private AMap aMap;
	private Map SchoolDistrictResult;
	public static final int SHOW_DISTRICT = 1;
	public static final int SHOW_RESPONSE = 0;
	public int schoolNumber;
	private ArrayList<Map> result;
	
	/**
	 * 学区需要实现的功能：
	 */
	
	
	//现有不null
	  MapView mMapView = null;
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState); 
	    setContentView(R.layout.activity_second);
	    ActivityCollector.addActivity(this);
	    buttonSift = (Button) findViewById(R.id.button_sift);
	    buttonSift.setVisibility(View.VISIBLE);
	    //获取地图控件引用
	    mMapView = (MapView) findViewById(R.id.map);
	    //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
	    mMapView.onCreate(savedInstanceState);
	    
	    if (aMap == null) {
			aMap = mMapView.getMap();
			setUpMap();
		}
	  }
	  

	  
	  private void setUpMap() {
			
			Bundle bundle = getIntent().getExtras();  
	        Serializable serializableMap = (Serializable) bundle.get("SchoolDistrictResult");  
	        SchoolDistrictResult = (Map)serializableMap;
	        
	        Gson gson = new Gson();
			Map schoolDistrictMiddlepointTemp = gson.fromJson(SchoolDistrictResult.get("saMiddlepoint").toString(), new TypeToken<Map>(){}.getType());
			LatLng schoolDistrictMiddlepoint = new LatLng(Double.parseDouble(schoolDistrictMiddlepointTemp.get("lat").toString()), Double.parseDouble(schoolDistrictMiddlepointTemp.get("lng").toString()));
	        float saScaleparam = Float.parseFloat(SchoolDistrictResult.get("saScaleparam").toString());
	        
	        ArrayList<Map> saPoints = gson.fromJson(SchoolDistrictResult.get("saPoints").toString(), new TypeToken<ArrayList<Map>>(){}.getType());
	        PolylineOptions polylineOption = new PolylineOptions();
			 for(int j = 0; j < saPoints.size(); j++) {
				 polylineOption.add(new LatLng(Double.parseDouble(saPoints.get(j).get("lat").toString()), Double.parseDouble(saPoints.get(j).get("lng").toString())));
					System.out.println(saPoints.get(j).get("lat").toString());
			 }
			 polylineOption.add(new LatLng(Double.parseDouble(saPoints.get(0).get("lat").toString()), Double.parseDouble(saPoints.get(0).get("lng").toString())));	
			 aMap.addPolyline(polylineOption.setDottedLine(true).width(6).color(Color.BLUE));
	        
	        
	        
			aMap.moveCamera(
					 CameraUpdateFactory.newLatLngZoom(new LatLng(schoolDistrictMiddlepoint.latitude, schoolDistrictMiddlepoint.longitude),saScaleparam));

			aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
			aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
			aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
			
			new Thread (new Runnable() {
				@Override
				public void run() {
					HttpURLConnection connection = null;
					try {
						HttpClient httpClient = new DefaultHttpClient();
				        Gson gson = new Gson();
						String SchoolDistrictResultTemp = gson.fromJson(SchoolDistrictResult.get("saId").toString(), new TypeToken<String>(){}.getType());
						
						System.out.println(SchoolDistrictResultTemp);
						HttpGet httpGet = new HttpGet("http://202.112.88.61:8080/mapserver/api/schoolList?saId="+SchoolDistrictResultTemp);
						HttpResponse httpResponse = httpClient.execute(httpGet);

						if(httpResponse.getStatusLine().getStatusCode() == 200) {
							HttpEntity entity = httpResponse.getEntity();
							String response = EntityUtils.toString(entity, "utf-8");
							ArrayList<Map> result = parseJSONWithJOSNObject(response.toString());
							Message message = new Message();
							message.what = SHOW_RESPONSE;
							message.obj = result;
							handler.sendMessage(message);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(connection != null) {
							connection.disconnect();
						}
					}
				}
			}).start();
			
		}
	  
	  

		//显示学校
		private Handler handler=new Handler(){
			public void handleMessage(Message msg){
				
				switch (msg.what) {
				case SHOW_RESPONSE:
					result = (ArrayList<Map>) msg.obj;
					schoolNumber = result.size();
					
					for(int i = 0; i < result.size(); i++) {
						 Gson gson = new Gson();
						 String sName = gson.fromJson(result.get(i).get("sName").toString(), new TypeToken<String>(){}.getType());
						 Map saMiddlepointTemp = gson.fromJson(result.get(i).get("sMiddlepoint").toString(), new TypeToken<Map>(){}.getType());
						 LatLng sMiddlepoint = new LatLng(Double.parseDouble(saMiddlepointTemp.get("lat").toString()), Double.parseDouble(saMiddlepointTemp.get("lng").toString()));
						 String sType = gson.fromJson(result.get(i).get("sType").toString(), new TypeToken<String>(){}.getType());
						
						 Bitmap btm = TextConvert(sName,sType);
						 
						 aMap.addMarker(new MarkerOptions()
						 .position(sMiddlepoint)
						 .title(sName)
						 .snippet(String.valueOf(i))
						 .perspective(true)
						 .icon(BitmapDescriptorFactory.fromBitmap(btm)));
						 
					 }
					break;
				}
			}
		};
		
		@SuppressLint("NewApi")
		public Bitmap TextConvert(final String text, String Type){
			
			final int[] color = new int[3];
			switch(Type)
			{
				case "小学":
					color[0] = 22;
					color[1] = 134;
					color[2] = 14;
					break;
				case "初中":
					color[0] = 134;
					color[1] = 14;
					color[2] = 19;
			 		break;
				case "高中":
					color[0] = 82;
					color[1] = 14;
					color[2] = 134;
					break;
				default:
					color[0] = 67;
					color[1] = 67;
					color[2] = 69;
					break;
			};
			Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.school_icon);
			
		    final Paint textPaint = new Paint() {
		        {
		            setColor(Color.WHITE);
		            //setColor(Color.BLACK);
		            setTextAlign(Paint.Align.LEFT);
		            setTextSize(25f);
		            setAntiAlias(true);
		            setTypeface(Typeface.DEFAULT_BOLD); 
		        }
		    };
		    final Rect bounds = new Rect();
		    textPaint.getTextBounds(text, 0, text.length(), bounds);
		    
		    final Bitmap bmp = Bitmap.createBitmap(bounds.width()+ 16, bounds.height()+ 16, Bitmap.Config.ARGB_8888); //use ARGB_8888 for better quality
		    final Canvas canvas = new Canvas(bmp);
		    canvas.drawARGB(1000, color[0], color[1], color[2]);
		    //canvas.drawARGB(1000, 255, 255, 255);
//		    Paint iconPaint=new Paint();    
//		    iconPaint.setDither(true);//防抖动    
//		    iconPaint.setFilterBitmap(true);
//		    canvas.drawBitmap(icon, 3f, 3f, iconPaint);
//		    
		    final Paint rectPaint = new Paint();
		    rectPaint.setColor(Color.WHITE);
		    canvas.drawRect(2, 2, bounds.width()+ 14, bounds.height()+ 16 -2, rectPaint);
		    rectPaint.setColor(Color.rgb(color[0], color[1], color[2]));
		    canvas.drawRect(4, 4, bounds.width()+ 12, bounds.height()+ 16 -4, rectPaint);
		    canvas.drawText(text, 8f, 30f, textPaint);
		    
		    return bmp;
		    
		   // bmp.recycle();
		}
		
		
		
		//解析Json数据
		private ArrayList<Map> parseJSONWithJOSNObject(String jsonData) {
			Gson gson = new Gson();
			Map map = gson.fromJson(jsonData, new TypeToken<Map>(){}.getType());
			ArrayList<Map> result = (ArrayList<Map>)map.get("result");
			return result;
		} 
		
		//获取行政区、学区的坐标点
		@Override
		public void onDistrictSearched(DistrictResult districtResult) {
			
			if (districtResult == null|| districtResult.getDistrict()==null) {
				return;
			}
			final DistrictItem item = districtResult.getDistrict().get(0);

			if (item == null) {
				return;
			}
			LatLonPoint centerLatLng=item.getCenter();
			if(centerLatLng!=null){
				aMap.clear();
				//aMap.stopAnimation();
				 aMap.moveCamera(
						 CameraUpdateFactory.newLatLngZoom(new LatLng(centerLatLng.getLatitude(), centerLatLng.getLongitude()),10.6f));
			}
		
			
			new Thread() {
				public void run() {
	 
					String[] polyStr = item.districtBoundary();
					if (polyStr == null || polyStr.length == 0) {
						return;
					}
					for (String str : polyStr) {
						String[] lat = str.split(";");
						PolylineOptions polylineOption = new PolylineOptions();
						boolean isFirst=true;
						LatLng firstLatLng=null;
						for (String latstr : lat) {
							String[] lats = latstr.split(",");
							if(isFirst){
								isFirst=false;
								firstLatLng=new LatLng(Double
										.parseDouble(lats[1]), Double
										.parseDouble(lats[0]));
							}
							polylineOption.add(new LatLng(Double
									.parseDouble(lats[1]), Double
									.parseDouble(lats[0])));
						}
						if(firstLatLng!=null){
							polylineOption.add(firstLatLng);
						}
						
						 polylineOption.width(6).color(Color.BLUE);	 
						 Message message=handler.obtainMessage();
						 message.what = SHOW_DISTRICT;
						 message.obj=polylineOption;
						 handler.sendMessage(message);
					}
				}
	 		}.start();
	 		

		}


		
		/**
		 * 对marker标注点点击响应事件
		 */
		@Override
		public boolean onMarkerClick(final Marker marker) {
			if (aMap != null) {
			Toast toast=Toast.makeText(getApplicationContext(), "你点击的是" + marker.getTitle() , Toast.LENGTH_SHORT);
			toast.show(); 

			marker.showInfoWindow();
			}
			
			return false;
		}
		

		/**
		 * 监听点击infowindow窗口事件回调
		 */
		@Override
		public void onInfoWindowClick(Marker marker) {
			Toast toast=Toast.makeText(getApplicationContext(), "你点击了infoWindow窗口" + marker.getTitle() , Toast.LENGTH_SHORT);
			toast.show(); 
		}
		/**
		 * 监听自定义infowindow窗口的infocontents事件回调
		 */
		@Override
		public View getInfoContents(Marker marker) {
			View infoContent = getLayoutInflater().inflate(
					R.layout.custom_info_window, null);
			render(marker, infoContent);
			return infoContent;
		}

		/**
		 * 监听自定义infowindow窗口的infowindow事件回调
		 */
		@Override
		public View getInfoWindow(Marker marker) {
			View infoWindow = getLayoutInflater().inflate(
					R.layout.custom_info_window, null);

			render(marker, infoWindow);
			return infoWindow;
		}

		/**
		 * 自定义infowinfow窗口
		 */
		public void render(Marker marker, View view) {
			int i = Integer.parseInt(marker.getSnippet());
			Gson gson = new Gson();
			String sName = gson.fromJson(result.get(i).get("sName").toString(), new TypeToken<String>(){}.getType());
			String sCharacter = gson.fromJson(result.get(i).get("sCharacter").toString(), new TypeToken<String>(){}.getType());
			String sAddress = gson.fromJson(result.get(i).get("sAddress").toString(), new TypeToken<String>(){}.getType());
			String sType = gson.fromJson(result.get(i).get("sType").toString(), new TypeToken<String>(){}.getType());
			String sScore = gson.fromJson(result.get(i).get("sScore").toString(), new TypeToken<String>(){}.getType());
			
			if(sType != null)
			{
				switch(sType)
				{
					case "小学":
						view.setBackgroundResource(R.drawable.bubble_xiaoxue);
						break;
					case "初中":
						view.setBackgroundResource(R.drawable.bubble_chuzhong);
						break;
					case "高中":
						view.setBackgroundResource(R.drawable.bubble_gaozhong);
						break;
					default:
						break;
				
				};
			}
			
			TextView name = ((TextView) view.findViewById(R.id.name));
			if(sName != null)	name.setText(sName);
			else name.setText("");

			TextView character = ((TextView) view.findViewById(R.id.character));
			if(sCharacter != null)	character.setText(sCharacter);
			else character .setText("");
			
			TextView type = ((TextView) view.findViewById(R.id.type));
			if(sType != null)	type.setText(sType);
			else type.setText("");
			
			TextView score = ((TextView) view.findViewById(R.id.score));
			if(sScore != null)	score.setText(sScore+"分");
			else score.setText("0分");
			
			TextView address = ((TextView) view.findViewById(R.id.address));
			if(sAddress != null)	address.setText(sAddress);
			else address.setText("");
			
			TextView more = ((TextView) view.findViewById(R.id.more));
			
			ArrayList<ImageView> stars = new ArrayList<ImageView>();
			stars.add((ImageView) view.findViewById(R.id.star1));
			stars.add((ImageView) view.findViewById(R.id.star2));
			stars.add((ImageView) view.findViewById(R.id.star3));
			stars.add((ImageView) view.findViewById(R.id.star4));
			stars.add((ImageView) view.findViewById(R.id.star5));
			
			float starScore;
			if(sScore != null)	starScore = Float.parseFloat(sScore);
			else	starScore = 0;
			for(int j = 0; j < starScore/2; j++)
			{
				stars.get(j).setImageResource(R.drawable.score_full);
			}
			if(starScore%2 != 0)
				stars.get((int)starScore/2).setImageResource(R.drawable.score_half);
			else
				stars.get((int)starScore/2).setImageResource(R.drawable.score_empty);
			for(int j = (int)(starScore/2)+1 ; j < 5; j++)
			{
				stars.get(j).setImageResource(R.drawable.score_empty);
			}
			
		}
		
	  @Override
	  protected void onDestroy() {
	    super.onDestroy();
	    //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
	    mMapView.onDestroy();
    	ActivityCollector.removeActivity(this);
	  }
	 @Override
	 protected void onResume() {
	    super.onResume();
	    //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
	    mMapView.onResume();
	    }
	 @Override
	 protected void onPause() {
	    super.onPause();
	    //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
	    mMapView.onPause();
	    }
	 @Override
	 protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
	    mMapView.onSaveInstanceState(outState);
	  } 
	     
	}
