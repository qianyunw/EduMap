package com.example.edumap;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearch.OnDistrictSearchListener;
import com.amap.api.services.district.DistrictSearchQuery;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SecondActivity extends Activity  implements LocationSource,
AMapLocationListener, OnDistrictSearchListener{
	

    private static Button buttonSift;
    private AMap aMap;
	private OnLocationChangedListener mListener;
	private AMapLocationClient mlocationClient;
	private AMapLocationClientOption mLocationOption;
	private String DistrictResult;
	
	//���в�null
	  MapView mMapView = null;
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState); 
	    setContentView(R.layout.activity_second);
	    buttonSift = (Button) findViewById(R.id.button_sift);
	    buttonSift.setVisibility(View.VISIBLE);
	    //��ȡ��ͼ�ؼ�����
	    mMapView = (MapView) findViewById(R.id.map);
	    //��activityִ��onCreateʱִ��mMapView.onCreate(savedInstanceState)��ʵ�ֵ�ͼ�������ڹ���
	    mMapView.onCreate(savedInstanceState);
	    if (aMap == null) {
			aMap = mMapView.getMap();
			setUpMap();
			
			Bundle bundle=getIntent().getExtras();
			DistrictResult = bundle.getString("districtResult");
			
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
	  
		
		
		private Handler handler=new Handler(){
			public void handleMessage(Message msg){
				 PolylineOptions polylineOption=(PolylineOptions) msg.obj;
				 aMap.addPolyline(polylineOption);
			}
		};

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
						 message.obj=polylineOption;
						 handler.sendMessage(message);
						 
					}
				}
	 		}.start();

		}
		
		
		
		
		
	  @Override
	  protected void onDestroy() {
	    super.onDestroy();
	    //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
	    mMapView.onDestroy();
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


/**
	 * �����Ѿ����Ը���ͬ�����������߽��ˣ����Ӷ�λ����
	 * ��λ�ᵼ�����ĵ�Ϊ�ҵ�λ�ã���ƫ����������λ��û�ж�λС�ꣻ
	 * �Ľ����������ҵ�λ�����������غ��򴥷�������λ������֮�󴥷���
	 * ��Ҫ��֯������ѧ�����򣨸�����ʽ���迼����ϣ����ߵ�������ʽ��ͬ����
	 */