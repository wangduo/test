package com.mobilesafe;

import com.mobilesafe.service.ShowAddressService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SttingCenterActivity extends Activity {
	

	private static final String TAG = "SttingCenterActivity";
	private CheckBox cb ;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stting_center);
		sp = getSharedPreferences("config",MODE_PRIVATE);
		cb = (CheckBox) this.findViewById(R.id.show_address_checkbox);
		
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				
				Editor editor = sp.edit();
				editor.putBoolean("showaddress", isChecked);
				editor.commit();
				
				if(isChecked){
					startService();
					cb.setText(R.string.address_service_open);
				}else {
					cb.setText(R.string.address_service_closed);
					stopService();
				}
			}
			
		});
		cb.invalidate();
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		boolean checked = sp.getBoolean("showaddress", false);
		cb.setChecked(checked);
		if(checked){
			cb.setText(R.string.address_service_open);
		}
		else 
		{
			cb.setText(R.string.address_service_closed);
		}
	}
	public void startService(){
		Intent service = new  Intent(this,ShowAddressService.class);
		startService(service);
	}
	public void stopService(){
		Intent service = new  Intent(this,ShowAddressService.class);
		stopService(service);
		
	}
	
	public void changeViewPosition(View view){
		Intent intent = new Intent(this,DragViewActivity.class);
		startActivity(intent);
	}
	

}
