package com.mobilesafe.util;

import android.content.Context;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

public class GPSInfoManager {
	private Context context;
	private String number;
	private static GPSInfoManager instance;
	public static GPSInfoManager getInstance(Context context, String number){
		if(null == instance){
			instance = new GPSInfoManager(context, number);
		}
		return instance;
	}

	
	public GPSInfoManager(Context context,String mumber){
		this.context = context;
		this.number = number;
	}
	public void getGPSInfo(){
		
		LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
		Criteria criteria = new Criteria(); 
		criteria.setAccuracy(Criteria.ACCURACY_FINE); //设置为最大精度
		criteria.setAltitudeRequired(false);	//不要求海拔信息
		criteria.setBearingRequired(false);		//不要求方位信息
		criteria.setCostAllowed(true); 		//是否允许付费
		criteria.setPowerRequirement(Criteria.POWER_LOW);	//对电量的要求,低功耗
		String provider = locationManager.getBestProvider(criteria, true);	//获取GIS信息
		locationManager.getLastKnownLocation(provider);		//获取上一次GIS的信息
		locationManager.requestLocationUpdates(provider, 0, 0, new GPSListener());
	}
	private class GPSListener implements LocationListener{


		public void onLocationChanged(Location location) {
			String longitude = "" + location.getLongitude(); //经度
			String latitude = "" + location.getLatitude();
			Log.i("GPSInfoManager",latitude + "," +longitude);//纬度
//			SmsManager smsManager = SmsManager.getDefault();
//			smsManager.sendTextMessage(number,null,longitude + "," +latitude,null,null);
			
			
			
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}


		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}


		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
