package com.mobilesafe;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilesafe.adapter.SelectContactListAdapter;
import com.mobilesafe.bean.ContactBean;
import com.mobilesafe.service.ContactInfoService;

public class SelectContactActivity extends Activity implements OnItemClickListener{
	private static final String TAG = "SelectContactActivity";
	private  ListView lv;
	
	private Runnable myRun = new Runnable(){
		public void run() {
			final ArrayList<ContactBean> peoples = ContactInfoService.getInstance(SelectContactActivity.this).getContact();
			lv.setAdapter(new SelectContactListAdapter(peoples,SelectContactActivity.this));
		}
		
	};
	
	@Override
	protected void onResume() {
		runOnUiThread(myRun);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectcontact);
		lv = (ListView) this.findViewById(R.id.select_contact_list);
		lv.setOnItemClickListener(this);
	}
	
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long arg3) {
			TextView tv = (TextView) view.findViewById(R.id.select_list_number);
			String number = tv.getText().toString();
			Intent intent = new Intent();
			intent.putExtra("number", number);
			setResult(0, intent);
			finish();
			
		}
}
