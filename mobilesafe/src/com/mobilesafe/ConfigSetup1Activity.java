package com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ConfigSetup1Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.configstep1);	
	}
	
	public void configstep1next(View view){
		Intent intent = new Intent(this, ConfigSetup2Activity.class);
		startActivity(intent);
		finish();
	}

}
