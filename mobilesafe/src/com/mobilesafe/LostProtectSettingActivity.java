package com.mobilesafe;

import com.mobilesafe.util.IntentUtils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;


public class LostProtectSettingActivity extends Activity {

	private NotificationManager notificationManager;
	private SharedPreferences sp;
	private CheckBox cb ;
	private ImageView iv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		setContentView(R.layout.lost_protect_setting);
		cb = (CheckBox) findViewById(R.id.protect_checkbox);
		iv = (ImageView) findViewById(R.id.protect_image);
		cb.setOnCheckedChangeListener(new CheckBoxChangeListener());
	}

	@Override
	protected void onResume() {
		super.onResume();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean checked = sp.getBoolean("isprotecting", false);
		cb.setChecked(checked);
		if(checked){
			iv.setImageResource(R.drawable.protecting);
		}else{
			iv.setImageResource(R.drawable.noprotecting);
		}
	}

	public void loadConfigSetup(View view){
		 IntentUtils.sendActivity(this,ConfigSetup1Activity.class);
		    this.finish();
	}
	
	private class CheckBoxChangeListener implements OnCheckedChangeListener{

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			Editor editor = sp.edit();
			if(isChecked){
				cb.setText("正在保护");
				iv.setImageResource(R.drawable.protecting);
				editor.putBoolean("isprotecting", true);
				editor.commit();
				Notification notification = new Notification(R.drawable.notification,"手机卫士",System.currentTimeMillis());
				Intent startIntent = new Intent(LostProtectSettingActivity.this,MainScreenActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(LostProtectSettingActivity.this, 0, startIntent, 0);
				notification.setLatestEventInfo(LostProtectSettingActivity.this, "手机卫士", "保护中", pendingIntent);
				notificationManager.notify(0, notification);
			}else {
				cb.setText("暂停保护");
				iv.setImageResource(R.drawable.noprotecting);
				editor.putBoolean("isprotecting", false);
				editor.commit();
			}
		}
		
	}
}
