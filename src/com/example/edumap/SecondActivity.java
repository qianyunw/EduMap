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
	
	//现有不null
	  MapView mMapView = null;
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState); 
	    setContentView(R.layout.activity_second);
	    buttonSift = (Button) findViewById(R.id.button_sift);
	    buttonSift.setVisibility(View.VISIBLE);
	    //获取地图控件引用
	    mMapView = (MapView) findViewById(R.id.map);
	    //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
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
			// 自定义系统定位小蓝点
			MyLocationStyle myLocationStyle = new MyLocationStyle();
			myLocationStyle.myLocationIcon(BitmapDescriptorFactory
					.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
			myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
			myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
			// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
			myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
			
			aMap.setMyLocationStyle(myLocationStyle);
			aMap.setLocationSource(this);// 设置定位监听
			aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
			aMap.setMyLocationEnabled(false);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		    //aMap.setMyLocationType()
		}
	  @Override
		public void onLocationChanged(AMapLocation amapLocation) {
			if (mListener != null && amapLocation != null) {
				if (amapLocation != null
						&& amapLocation.getErrorCode() == 0) {
					mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				} else {
					String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
					Log.e("AmapErr",errText);
				}
			}
		}

		/**
		 * 激活定位
		 */
		@Override
		public void activate(OnLocationChangedListener listener) {
			mListener = listener;
			if (mlocationClient == null) {
				mlocationClient = new AMapLocationClient(this);
				mLocationOption = new AMapLocationClientOption();
				//设置定位监听
				mlocationClient.setLocationListener(this);
				//设置为高精度定位模式
				mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
				//设置定位参数
				mlocationClient.setLocationOption(mLocationOption);
				// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
				// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
				// 在定位结束后，在合适的生命周期调用onDestroy()方法
				// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
				mlocationClient.startLocation();
			}
		}

		/**
		 * 停止定位
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
	    //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
	    mMapView.onDestroy();
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


/**
	 * 现在已经可以给不同的行政区划边界了（不加定位）；
	 * 定位会导致中心点为我的位置（跑偏），但不定位就没有定位小标；
	 * 改进方案：若我的位置与行政区重合则触发，否则定位在搜索之后触发；
	 * 需要组织给定的学区区域（给定格式还需考量，希望与高德所给格式相同）；
	 */