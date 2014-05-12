package com.mobilesafe;

import com.mobilesafe.util.IntentUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ConfigSetup3Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configstep3);
	}
	public void finish(View view){
		IntentUtils.sendActivity(this,LostProtectSettingActivity.class);
		this.finish();
	}

}
