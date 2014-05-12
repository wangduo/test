package com.mobilesafe;

import java.security.NoSuchAlgorithmException;

import com.mobilesafe.util.IntentUtils;
import com.mobilesafe.util.MD5Encoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LostProtectActivity extends Activity {

	private EditText edName ;
	private EditText edPass ;
	private EditText edPassConfim ;
	private SharedPreferences sp;
	private AlertDialog alertDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean isfirst = sp.getBoolean("isFrist", true);
		if(isfirst){
			showFristEntryDialog();
		}else {
			showNormalDialog();
		}
	}
	private void showFristEntryDialog() {
		final LayoutInflater inflater = LayoutInflater.from(this);
		final View view = inflater.inflate(R.layout.firstentrydialog, null);
		edName = (EditText) view.findViewById(R.id.first_dialog_name);
		edPass = (EditText) view.findViewById(R.id.first_dialog_pass);
		edPassConfim = (EditText) view.findViewById(R.id.first_dialog_pass_confim);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("创建用户");
		builder.setView(view);
		alertDialog = builder.create();
		alertDialog.show();
		
	}
	private void showNormalDialog() {
		
		final LayoutInflater inflater = LayoutInflater.from(this);
		final View view = inflater.inflate(R.layout.normalentrydialog, null);
		edName = (EditText) view.findViewById(R.id.normal_dialog_name);
		edPass = (EditText) view.findViewById(R.id.normal_dialog_pass);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("用户登陆");
		builder.setView(view);
		alertDialog = builder.create();
		alertDialog.show();
	}
	/**
	 * firstentryDialog.xml
	 * @param view
	 */
	public void fristEntryConfirm(View view){
		String name = edName.getText().toString();
		String pwd = edPass.getText().toString();
		String pwdConfim = edPassConfim.getText().toString();
		Editor editor = sp.edit();
		if("".equals(name) || "".equals(pwd) || "".equals(pwdConfim)){
			Toast.makeText(this, "输入错误", 0).show();
		}else 
		{
			if(pwd.equals(pwdConfim)){
				editor.putString("name", name);
				try {
					pwd = MD5Encoder.getInstance().encode(pwd);	//加密密码
					editor.putString("password", pwd);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
				editor.putBoolean("isFrist", false);
				editor.commit();
				alertDialog.dismiss();
				loadMainUI();
			}else{
				Toast.makeText(this, "密码不一致", 0).show();	
			}
		}
		
	}

	private void loadMainUI() {
		boolean isConfiged = sp.getBoolean("isConfiged", false);
		if(!isConfiged){
			IntentUtils.sendActivity(this, ConfigSetup1Activity.class);
		}else {
			IntentUtils.sendActivity(this, LostProtectSettingActivity.class);
			finish();
		}	
	}
	
	/**
	 * firstentryDialog.xml
	 * @param view
	 */
	public void fristEntryCancel(View view){
		alertDialog.dismiss();
		this.finish();
	}
	/**
	 * normalentryDialog.xml
	 * @param view
	 */
	public void normalEntryConfirm(View view){
		String name = edName.getText().toString().trim();
		String pwd = edPass.getText().toString().trim();
		String spName = sp.getString("name", "");
		String spPwd = sp.getString("password", "admin123");
		try {
			pwd = MD5Encoder.getInstance().encode(pwd);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if(name.equals(spName) && pwd.equals(spPwd)){
			alertDialog.dismiss();
			setContentView(R.layout.main);
			loadMainUI();
		}else{
			Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_LONG).show();
		}
		
	}
	/**
	 * ormalentryDialog.xml
	 * @param view
	 */
	public void normalEntryCancel(View view){
		alertDialog.dismiss();
		this.finish();
	}
	
	
}
