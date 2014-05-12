package com.mobilesafe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ConfigSetup2Activity extends Activity {

	private EditText et_safenumber;
	private EditText et_message;
	private SharedPreferences sp;
	private TelephonyManager tel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configstep2);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		et_safenumber = (EditText) findViewById(R.id.safenumber);
		et_message = (EditText) findViewById(R.id.safemessage);
		
		et_safenumber.setText(sp.getString("safenumber", ""));
		et_message.setText(sp.getString("safemessage", ""));
		tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
	}

	public void selectContacts(View view) {
		Intent intent = new Intent(this,SelectContactActivity.class);
		startActivityForResult(intent,0);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(null!=data){
			String number = data.getStringExtra("number");
			et_safenumber.setText(number);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	public void configstep2pre(View view) {
		Intent intent = new Intent(this,ConfigSetup1Activity.class);
		startActivity(intent);
		finish();
	}

	public void configstep2next(View view) {
		String number = et_safenumber.getText().toString();
		String safemessage = et_message.getText().toString();
		if("".equals(number) ){
			Toast.makeText(this, "号码不能为空", Toast.LENGTH_SHORT);
			return ;
		}
		else if( "".equals(safemessage)){
			Toast.makeText(this, "短信内容不能为空", Toast.LENGTH_SHORT);
			return ;
		}
		Editor editor =  sp.edit();
		editor.putString("safenumber", number);
		editor.putString("safemessage", safemessage);
		editor.putBoolean("isConfiged", true);
		//保存用户唯一标识，比如GSM网络的IMSI编号
		editor.putString("imsi", tel.getSubscriberId());	
		editor.commit();
		Intent intent = new Intent(this,ConfigSetup3Activity.class);
		startActivity(intent);
		finish();
	}

}
