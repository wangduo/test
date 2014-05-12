package com.mobilesafe;

import java.util.ArrayList;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.mobilesafe.util.TrafficDataUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SlidingdrawerActivity extends Activity {

	private static final String TAG = "SlidingdrawerActivity";
	
	private  ListView lv;
	private  MyAdapter madapter;
	private  Timer timer;
	
	 private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			madapter.notifyDataSetChanged();
		}
		 
	 }; 
	
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newwork_traffic);
		lv = (ListView) findViewById(R.id.nt_lv_content);
		TextView tvgprs = (TextView) findViewById(R.id.gprs);
		TextView tvwifi = (TextView)findViewById(R.id.wifi);
		tvgprs.setText(TrafficStats.getMobileRxPackets() + TrafficStats.getMobileTxPackets() + " °ü");
		tvwifi.setText(TrafficStats.getTotalRxPackets() + TrafficStats.getTotalTxPackets() + " °ü");	
	}
	@Override
	protected void onStart() {
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				Message msg = new Message();
				handler.sendMessage(msg);
			}
		};
		timer = new Timer();
		timer.schedule(task, 1000, 1000);
		super.onStart();
	}
	private final Runnable runnable = new Runnable(){
		public void run() {
			final PackageManager pm = SlidingdrawerActivity.this.getPackageManager();
			ArrayList<ResolveInfo>  trafficlists = new ArrayList<ResolveInfo>();
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.LAUNCHER");
			final List<ResolveInfo> lists = pm.queryIntentActivities(intent, 0);
			for(ResolveInfo info : lists){
				if(TrafficStats.getUidRxBytes(info.activityInfo.applicationInfo.uid) != -1 
				&& TrafficStats.getUidTxBytes(info.activityInfo.applicationInfo.uid) != -1){
					trafficlists.add(info);
				}
			}
			madapter = new MyAdapter(SlidingdrawerActivity.this,trafficlists);
			lv.setAdapter(madapter);
		}
		
	} ;
	
	@Override
	protected void onResume() {
		super.onResume();
		runOnUiThread(runnable);
	}
	@Override
	protected void onStop() {
		super.onStop();
		timer.cancel();
	}
	private  class MyAdapter extends BaseAdapter{

		private  LayoutInflater inflater;
		private ArrayList<ResolveInfo> lists;
		public MyAdapter(Context context,ArrayList<ResolveInfo> _lists){
			inflater = LayoutInflater.from(context);
			this.lists = _lists;
		}

		public int getCount() {
			return lists.size();
		}


		public Object getItem(int position) {
			return lists.get(position);
		}


		public long getItemId(int position) {
			return position;
		}


		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(null == convertView){
				convertView = inflater.inflate(R.layout.traffic_item, null);
				holder = new ViewHolder();
				holder.iv = (ImageView) convertView.findViewById(R.id.ItemImage);
				holder.tv1 = (TextView) convertView.findViewById(R.id.ItemText);
				holder.tv2 = (TextView) convertView.findViewById(R.id.ItemText01);
				holder.tv3 = (TextView) convertView.findViewById(R.id.ItemText02);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			final ResolveInfo info = lists.get(position);
			holder.iv.setImageDrawable(info.loadIcon(getPackageManager()));
			holder.tv1.setText(info.loadLabel(getPackageManager()));
			final long totalTx = TrafficStats.getUidTxBytes(info.activityInfo.applicationInfo.uid);
			final long totalRx = TrafficStats.getUidRxBytes(info.activityInfo.applicationInfo.uid);
			holder.tv2.setText(TrafficDataUtil.getTrafficData(totalTx));
			holder.tv3.setText(TrafficDataUtil.getTrafficData(totalRx));
			return convertView;
		}
		
		
	}
	final static class ViewHolder{
		ImageView iv;
		TextView tv1;
		TextView tv2;
		TextView tv3;
		TextView tv4;
	} 

}
