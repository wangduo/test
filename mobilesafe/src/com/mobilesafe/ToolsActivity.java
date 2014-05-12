package com.mobilesafe;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobilesafe.service.AddressService;

public class ToolsActivity extends Activity {

	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tools);
		sp = getSharedPreferences("config", MODE_PRIVATE);
	}

	/**
	 * …Ë÷√IP∞Œ∫≈
	 * @param view
	 */
	public void ipdailSetting(View view){
		setContentView(R.layout.ipdailsetting);
		Button bt = (Button)findViewById(R.id.saveipnumber);
		final EditText et = (EditText)findViewById(R.id.ipnumber);
		bt.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String ipnumber = et.getText().toString();
				if("".equals(ipnumber)){
					Toast.makeText(ToolsActivity.this, "«Î ‰»ÎIP∫≈¬Î", Toast.LENGTH_LONG);
					return ;
				}
				Editor editor = sp.edit();
				editor.putString("ipnumbers", ipnumber);
				editor.commit();
				setContentView(R.layout.tools);	
			}
			
		});
		
	}
	/**
	 * ≤È—ØπÈ Ùµÿ
	 * @param view
	 */
	public void findAddress(View view){
		setContentView(R.layout.queryaddress);
		Button bt = (Button)findViewById(R.id.query_phone_button);
		final EditText et_number = (EditText) findViewById(R.id.query_phone_number);
		final AddressService service = new AddressService(ToolsActivity.this);
		bt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String number = et_number.getText().toString();
				String address = service.getAddress(number);
				Toast.makeText(ToolsActivity.this, address, 1);
				return ;
			}
		});
	}
	/**
	 * ∑µªÿ
	 * @param view
	 */
	public void returnMain(View view){
	    setContentView(R.layout.tools);
	}
	
	public void sttingNumber(View view){
		setContentView(R.layout.quick_setting);
		Button quickBtn = (Button) findViewById(R.id.quick_ok_btn);
		final EditText quickNumberEt = (EditText) findViewById(R.id.quick_number_et);
		quickBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				final String numberText = quickNumberEt.getText().toString();
				if(null == numberText || numberText.trim().length() == 0){
					Toast.makeText(ToolsActivity.this, "Ãÿ ‚∫≈¬Î≤ªƒ‹Œ™ø’", Toast.LENGTH_LONG).show();
					return ;
				}
				Editor editor = sp.edit();
				editor.putString("quickNumber", numberText);
				editor.commit();
				setContentView(R.layout.tools);	
			}
			
		});
	}
	
	
}
