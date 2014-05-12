package com.mobilesafe;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.mobilesafe.db.dao.BlackNumberDao;

public class CallSmsSafeActivity extends Activity {
	private static final String TAG = "CallSmsSafeActivity";
	private ListView lv;
	private BlackNumberDao dao;
	private ArrayList<HashMap<String,Object>> list;
	AlertDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_sms_safe);
		lv = (ListView) findViewById(R.id.black_number_list);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		lv.setAdapter(new MyListAdapter(this));
	}

	@Override
	protected void onStart() {
		super.onStart();
		loadList();
	}

	private void loadList(){
		dao = new BlackNumberDao(this);
		list = dao.getBlackNumbers();
	}
	/**
	 *  两种方式  onMenuItemSelected
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch(itemId){
		case 0:
			final AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("加入黑名单");
			LayoutInflater inflater = LayoutInflater.from(this);
			final View view = inflater.inflate(R.layout.add_blacknumber,null);
			final EditText edtext = (EditText) view.findViewById(R.id.addblackname);
			builder.setView(view);
			final Button button = (Button) view.findViewById(R.id.add_black);
			button.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				dao.add(edtext.getText().toString());
				list = dao.getBlackNumbers();
				lv.setAdapter(new MyListAdapter(CallSmsSafeActivity.this));
				dialog.dismiss();
			}
			
		});
		dialog = builder.create();
		dialog.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, 0, 0, "添加黑名单");
		return true;
	}
	
	private final class MyListAdapter extends BaseAdapter{
		private LayoutInflater inflater;
		public MyListAdapter(Context context){
			inflater = LayoutInflater.from(context);
		}
		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder  holder = null ; 
			if(null == convertView){
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.call_smssafe_item, null);
				holder.tv= (TextView) convertView.findViewById(R.id.smssafe_phone_number_name);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			final String number = (String) list.get(position).get("number");
			holder.tv.setText(number);
			return convertView;
		}
		
		
	}
	final static  class ViewHolder{
		TextView tv ;
	}
}
