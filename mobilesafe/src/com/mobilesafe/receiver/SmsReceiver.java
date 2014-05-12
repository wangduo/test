package com.mobilesafe.receiver;

import com.mobilesafe.util.GPSInfoManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * ¼àÌýÖ¸¶¨¶ÌÐÅ
 * @author user
 *
 */
public class SmsReceiver extends BroadcastReceiver {
	private static final String TAG = "SmsReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		final SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		final Object [] pdus = (Object[]) intent.getExtras().get("pdus");
		for(Object pdu : pdus){
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
			final String number = smsMessage.getOriginatingAddress();
			final String message = smsMessage.getMessageBody();
			final String settingNumber = sp.getString("quickNumber", "");
			if(settingNumber.equals(message)){
				abortBroadcast();
				GPSInfoManager.getInstance(context, number).getGPSInfo();
			}
		}
	}

}
