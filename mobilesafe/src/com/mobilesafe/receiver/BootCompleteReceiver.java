package com.mobilesafe.receiver;

import com.mobilesafe.MainScreenActivity;


import com.mobilesafe.R;
import com.mobilesafe.service.ShowAddressService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {
	
	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final TelephonyManager tel = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		final NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		SharedPreferences sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		final String newimsi = tel.getSubscriberId()== null ? " " : tel.getSubscriberId();
		final String oldimsi = sp.getString("imsi", " ");
		if(sp.getBoolean("showaddress", false)){
			if(intent.getAction().equals(ACTION)){
				
			}
			Intent service = new Intent(context,ShowAddressService.class);
			service.setAction("mobilesafe.service.BootService");
			context.startService(service);
			
		} 
		//����ʱ�������ֻ���������
		if(sp.getBoolean("isprotecting", false)){
			Notification notification = new Notification(R.drawable.notification,"�ֻ���ʿ",System.currentTimeMillis());
			Intent startintent = new Intent(context, MainScreenActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, startintent, 0);
			notification.setLatestEventInfo(context, "�ֻ���ʿ", "������", pendingIntent);
			notificationManager.notify(0, notification);
			if(newimsi.equals(oldimsi)){
				Log.i("BootCompleteReceiver", "imsi��ͬ");
			}else {
				SmsManager sms = SmsManager.getDefault();
				final String message = sp.getString("safemessage", "sim��������");
				final String number = sp.getString("safenumber", "0");
				sms.sendTextMessage(number, null, message, null, null);
			}
		}
		else {
			Log.i("BootCompleteReceiver","��������û�п���");
		}
	}

}
