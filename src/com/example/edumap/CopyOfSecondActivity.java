package com.example.edumap;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class CopyOfSecondActivity extends Activity implements LocationSource,
AMapLocationListener {
	  
	  	private AMap aMap;
		private MapView mapView;
		private OnLocationChangedListener mListener;
		private AMapLocationClient mlocationClient;
		private AMapLocationClientOption mLocationOption;
	    private static Button buttonSift;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		    setContentView(R.layout.activity_second);
		    buttonSift = (Button) findViewById(R.id.button_sift);
		    buttonSift.setVisibility(View.VISIBLE);
		    //��ȡ��ͼ�ؼ�����
		    mapView = (MapView) findViewById(R.id.map);
			init();
		}

		/**
		 * ��ʼ��AMap����
		 */
		private void init() {
			if (aMap == null) {
				aMap = mapView.getMap();
				setUpMap();
			}
		}

		/**
		 * ����һЩamap������
		 */
		private void setUpMap() {
			// �Զ���ϵͳ��λС����
			MyLocationStyle myLocationStyle = new MyLocationStyle();
			myLocationStyle.myLocationIcon(BitmapDescriptorFactory
					.fromResource(R.drawable.location));// ����С�����ͼ��
			myLocationStyle.strokeColor(Color.BLACK);// ����Բ�εı߿���ɫ
			myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// ����Բ�ε������ɫ
			// myLocationStyle.anchor(int,int)//����С�����ê��
			myLocationStyle.strokeWidth(1.0f);// ����Բ�εı߿��ϸ
			aMap.setMyLocationStyle(myLocationStyle);
			aMap.setLocationSource(this);// ���ö�λ����
			aMap.getUiSettings().setMyLocationButtonEnabled(true);// ����Ĭ�϶�λ��ť�Ƿ���ʾ
			aMap.setMyLocationEnabled(true);// ����Ϊtrue��ʾ��ʾ��λ�㲢�ɴ�����λ��false��ʾ���ض�λ�㲢���ɴ�����λ��Ĭ����false
		   // aMap.setMyLocationType()
		}

		/**
		 * ����������д
		 */
		@Override
		protected void onResume() {
			super.onResume();
			mapView.onResume();
		}

		/**
		 * ����������д
		 */
		@Override
		protected void onPause() {
			super.onPause();
			mapView.onPause();
			deactivate();
		}

		/**
		 * ����������д
		 */
		@Override
		protected void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			mapView.onSaveInstanceState(outState);
		}

		/**
		 * ����������д
		 */
		@Override
		protected void onDestroy() {
			super.onDestroy();
			mapView.onDestroy();
		}

		/**
		 * ��λ�ɹ���ص�����
		 */
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

	     
	}