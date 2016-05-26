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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.Toast;

public class SecondActivity extends Activity  implements LocationSource,
AMapLocationListener, OnDistrictSearchListener,OnMarkerClickListener {
    private static Button buttonSift;
    private AMap aMap;
	private OnLocationChangedListener mListener;
	private AMapLocationClient mlocationClient;
	private AMapLocationClientOption mLocationOption;
	private String DistrictResult;
	private Map SchoolDistrictResult;
	public static final int SHOW_DISTRICT = 1;
	public static final int SHOW_RESPONSE = 0;
	public int schoolDistricNumber;
	private DistrictColor[] districtColor;
	private Marker[] markers;
	private ArrayList<Map> result;
	
	public class DistrictColor{
		public int r;
		public int g;
		public int b;
		DistrictColor(int r1, int g1, int b1)
		{
			r = r1;
			g = g1;
			b = b1;
		}
		DistrictColor(){}
	}
	
	//���в�null
	  MapView mMapView = null;
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState); 
	    setContentView(R.layout.activity_second);
	    ActivityCollector.addActivity(this);
	    //��ȡ��ͼ�ؼ�����
	    mMapView = (MapView) findViewById(R.id.map);
	    //��activityִ��onCreateʱִ��mMapView.onCreate(savedInstanceState)��ʵ�ֵ�ͼ�������ڹ���
	    mMapView.onCreate(savedInstanceState);
	    
	    districtColor = new DistrictColor[5];
	    districtColor[0] = new DistrictColor(255,179,91);
	    districtColor[1] = new DistrictColor(204,136,186);
	    districtColor[2] = new DistrictColor(176,211,244);
	    districtColor[3] = new DistrictColor(235,176,244);
	    districtColor[4] = new DistrictColor(253,172,175);
	    
	    
	    if (aMap == null) {
			aMap = mMapView.getMap();
			setUpMap();
			
			Bundle bundle=getIntent().getExtras();
			DistrictResult = bundle.getString("districtResult");
			
			aMap.setOnMarkerClickListener(this);
			aMap.clear();
			DistrictSearch search = new DistrictSearch(getApplicationContext());
			DistrictSearchQuery query = new DistrictSearchQuery( );
	 		query.setKeywords(DistrictResult);
			query.setShowBoundary(true);
			search.setQuery(query);
			search.setOnDistrictSearchListener(this);

			search.searchDistrictAnsy();	
			
		}
	  }
	  
	  private void setUpMap() {
			// �Զ���ϵͳ��λС����
			MyLocationStyle myLocationStyle = new MyLocationStyle();
			myLocationStyle.myLocationIcon(BitmapDescriptorFactory
					.fromResource(R.drawable.location_marker));// ����С�����ͼ��
			myLocationStyle.strokeColor(Color.BLACK);// ����Բ�εı߿���ɫ
			myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// ����Բ�ε������ɫ
			// myLocationStyle.anchor(int,int)//����С�����ê��
			myLocationStyle.strokeWidth(1.0f);// ����Բ�εı߿��ϸ
			
			aMap.setMyLocationStyle(myLocationStyle);
			aMap.setLocationSource(this);// ���ö�λ����
			aMap.getUiSettings().setMyLocationButtonEnabled(true);// ����Ĭ�϶�λ��ť�Ƿ���ʾ
			aMap.setMyLocationEnabled(false);// ����Ϊtrue��ʾ��ʾ��λ�㲢�ɴ�����λ��false��ʾ���ض�λ�㲢���ɴ�����λ��Ĭ����false
		    //aMap.setMyLocationType()
		}
	  @Override
		public void onLocationChanged(AMapLocation amapLocation) {
			if (mListener != null && amapLocation != null) {
				if (amapLocation != null
						&& amapLocation.getErrorCode() == 0) {
					mListener.onLocationChanged(amapLocation);// ��ʾϵͳС����
				} else {
					String errText = "��λʧ��," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
					Log.e("AmapErr",errText);
				}
			}
		}

		/**
		 * ���λ
		 */
		@Override
		public void activate(OnLocationChangedListener listener) {
			mListener = listener;
			if (mlocationClient == null) {
				mlocationClient = new AMapLocationClient(this);
				mLocationOption = new AMapLocationClientOption();
				//���ö�λ����
				mlocationClient.setLocationListener(this);
				//����Ϊ�߾��ȶ�λģʽ
				mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
				//���ö�λ����
				mlocationClient.setLocationOption(mLocationOption);
				// �˷���Ϊÿ���̶�ʱ��ᷢ��һ�ζ�λ����Ϊ�˼��ٵ������Ļ������������ģ�
				// ע�����ú��ʵĶ�λʱ��ļ������С���֧��Ϊ2000ms���������ں���ʱ�����stopLocation()������ȡ����λ����
				// �ڶ�λ�������ں��ʵ��������ڵ���onDestroy()����
				// �ڵ��ζ�λ����£���λ���۳ɹ���񣬶��������stopLocation()�����Ƴ����󣬶�λsdk�ڲ����Ƴ�
				mlocationClient.startLocation();
			}
		}

		/**
		 * ֹͣ��λ
		 */
		@Override
		public void deactivate() {
			mListener = null;
			if (mlocationClient != null) {
				mlocationClient.stopLocation();
				mlocationClient.onDestroy();
			}
			mlocationClient = null;
		}
	  
		//��ʾ�����
		private Handler handler=new Handler(){
			public void handleMessage(Message msg){
				
				switch (msg.what) {
				case SHOW_DISTRICT:
					PolylineOptions polylineOption=(PolylineOptions) msg.obj;
					aMap.addPolyline(polylineOption);
					break;
				case SHOW_RESPONSE:
					result = (ArrayList<Map>) msg.obj;
					schoolDistricNumber = result.size();
					markers = new Marker[schoolDistricNumber];
					for(int i = 0; i < result.size(); i++) {
						 Gson gson = new Gson();
						 ArrayList<Map> saPoints = gson.fromJson(result.get(i).get("saPoints").toString(), new TypeToken<ArrayList<Map>>(){}.getType());
						 String saName = gson.fromJson(result.get(i).get("saName").toString(), new TypeToken<String>(){}.getType());
						 Map saMiddlepointTemp = gson.fromJson(result.get(i).get("saMiddlepoint").toString(), new TypeToken<Map>(){}.getType());
						 LatLng saMiddlepoint = new LatLng(Double.parseDouble(saMiddlepointTemp.get("lat").toString()), Double.parseDouble(saMiddlepointTemp.get("lng").toString()));
						 PolygonOptions schoolDistrictPolylineOption = new PolygonOptions();
						 for(int j = 0; j < saPoints.size(); j++) {
							 schoolDistrictPolylineOption.add(new LatLng(Double.parseDouble(saPoints.get(j).get("lat").toString()), Double.parseDouble(saPoints.get(j).get("lng").toString())));
								System.out.println(saPoints.get(j).get("lat").toString());
						 }
						 schoolDistrictPolylineOption.add(new LatLng(Double.parseDouble(saPoints.get(0).get("lat").toString()), Double.parseDouble(saPoints.get(0).get("lng").toString())));
						 schoolDistrictPolylineOption.fillColor(Color.argb(200, districtColor[i%5].r, districtColor[i%5].g, districtColor[i%5].b)).strokeWidth(0);	
						 aMap.addPolygon(schoolDistrictPolylineOption);
						 
						 Bitmap btm = TextConvert(saName);
						 
						 markers[i] = aMap.addMarker(new MarkerOptions().position(saMiddlepoint)
							.title(saName)
							.icon(BitmapDescriptorFactory.fromBitmap(btm)));
					 }
					break;
				}
			}
		};
		
		/**
		 * ��marker��ע������Ӧ�¼�
		 */
		@Override
		public boolean onMarkerClick(final Marker marker) {
			for(int i = 0; i < schoolDistricNumber; i++)
			{
				if (marker.equals(markers[i])) {
					if (aMap != null) {
					//	jumpPoint(marker);
					//Toast toast=Toast.makeText(getApplicationContext(), "��������" + marker.getTitle() , Toast.LENGTH_SHORT);
					//toast.show(); 
						
						Intent it = new Intent(SecondActivity.this, ThirdActivity.class);
						Bundle bundle=new Bundle();
						SchoolDistrictResult  = result.get(i);
						Serializable tmpmap= (Serializable) SchoolDistrictResult;
			            bundle.putSerializable("SchoolDistrictResult", tmpmap);  
						it.putExtras(bundle); 
						startActivity(it);
					
					}
					break;
				}
			}
			
			return true;
		}
		

		
		//����Json����
		private ArrayList<Map> parseJSONWithJOSNObject(String jsonData) {
			Gson gson = new Gson();
			Map map = gson.fromJson(jsonData, new TypeToken<Map>(){}.getType());
			ArrayList<Map> result = (ArrayList<Map>)map.get("result");
			return result;
		} 
		
		//��ȡ��������ѧ���������
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
	 		
	 		new Thread (new Runnable() {
				@Override
				public void run() {
					HttpURLConnection connection = null;
					try {
						HttpClient httpClient = new DefaultHttpClient();
						HttpGet httpGet = new HttpGet("http://202.112.88.61:8080/mapserver/api/schoolAreaList?districtName="+DistrictResult);
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
		
		
		//���ִ�ͼƬ
		public Bitmap TextConvert(final String text){
		    final Paint textPaint = new Paint() {
		        {
		            setColor(Color.WHITE);
		            setTextAlign(Paint.Align.LEFT);
		            setTextSize(25f);
		            setAntiAlias(true);
		        }
		    };
		    final Rect bounds = new Rect();
		    textPaint.getTextBounds(text, 0, text.length(), bounds);

		    final Bitmap bmp = Bitmap.createBitmap(bounds.width()+ 12, bounds.height()+ 16, Bitmap.Config.ARGB_8888); //use ARGB_8888 for better quality
		    final Canvas canvas = new Canvas(bmp);
		    canvas.drawARGB(1000, 255, 63, 63);
		    canvas.drawText(text, 5f, 26f, textPaint);
		    return bmp;
		    
		   // bmp.recycle();
		}
		
		
		
		
	  @Override
	  protected void onDestroy() {
	    super.onDestroy();
	    //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
	    mMapView.onDestroy();
    	ActivityCollector.removeActivity(this);
	  }
	 @Override
	 protected void onResume() {
	    super.onResume();
	    //��activityִ��onResumeʱִ��mMapView.onResume ()��ʵ�ֵ�ͼ�������ڹ���
	    mMapView.onResume();
	    }
	 @Override
	 protected void onPause() {
	    super.onPause();
	    //��activityִ��onPauseʱִ��mMapView.onPause ()��ʵ�ֵ�ͼ�������ڹ���
	    mMapView.onPause();
	    }
	 @Override
	 protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    //��activityִ��onSaveInstanceStateʱִ��mMapView.onSaveInstanceState (outState)��ʵ�ֵ�ͼ�������ڹ���
	    mMapView.onSaveInstanceState(outState);
	  } 
	}
