package com.mobilesafe.adapter;

import com.mobilesafe.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainGridViewAdapter extends BaseAdapter {
	private static final int COUNT = 9;
	String[] names ={"手机防盗","通讯卫士","软件管理","任务管理","流量查看","手机杀毒","系统优化"," 常用工具","设置中心"};
	int[] icons={R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings};
	private LayoutInflater inflater;
	public MainGridViewAdapter(Context context){
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return COUNT;
	}


	public Object getItem(int position) {
		return null;
	}


	public long getItemId(int position) {
		return position;
	}


	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.main_gv_item, null);
		ImageView iv = (ImageView) view.findViewById(R.id.main_gv_item_icon);
		iv.setBackgroundResource(icons[position]);
		TextView tv = (TextView)view.findViewById(R.id.main_gv_item_name);
		tv.setText(names[position]);
		return view;
	}

}
