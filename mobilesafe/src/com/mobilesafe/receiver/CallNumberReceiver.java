package com.mobilesafe.receiver;

import com.mobilesafe.LostProtectSettingActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class CallNumberReceiver extends BroadcastReceiver {
	private static final String TAG = "CallNumberReceiver";
	private SharedPreferences sp;
	@Override
	public void onReceive(Context context, Intent intent) {
		//获得手机号码
		final String number = getResultData();
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		final String ipnumber = sp.getString("ipnumbers", "");
		/*IP拔号*/
		if(!"".equals(ipnumber)){
			setResultData(ipnumber + number);	
		}
		final String settingNumber = sp.getString("quickNumber", "");
		if(settingNumber.equals(number)){
			setResultData(null);
			Intent newIntent = new Intent(context,LostProtectSettingActivity.class);
			newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(newIntent);
		}

	}

}
