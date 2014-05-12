package com.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ShowAppDetailActivity extends Activity {

	private TextView tv_appname;
	private TextView tv_appversion;
	private TextView tv_packagename;
	private TextView tv_permission;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_detial);
		tv_appname = (TextView) findViewById(R.id.detail_app_name);
		tv_appversion = (TextView) findViewById(R.id.detail_app_version);
		tv_packagename = (TextView) findViewById(R.id.detail_app_packname);
		tv_permission = (TextView) findViewById(R.id.detail_app_permissions);
		Bundle bundle = this.getIntent().getExtras();
		String packagename=  bundle.getString("packagename");
		String appversion = bundle.getString("appversion");
		String appname = bundle.getString("appname");
		String[] appPremissions = bundle.getStringArray("apppremissions");
		StringBuilder sb = new StringBuilder();
		for(String s : appPremissions){
			sb.append(s);
			sb.append("\n");
		}
		tv_appname.setText(appname);
		tv_appversion.setText(appversion);
		tv_packagename.setText(packagename);
		tv_permission.setText(sb.toString());
	}
}
