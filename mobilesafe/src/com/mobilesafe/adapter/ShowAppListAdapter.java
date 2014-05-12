package com.mobilesafe.adapter;

import java.util.ArrayList;
import com.mobilesafe.ApplicationInfo2;

import com.mobilesafe.R;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowAppListAdapter extends BaseAdapter {

	private ArrayList<ApplicationInfo2> appList;

	private LayoutInflater inflater;
	public ShowAppListAdapter(Context context,ArrayList<ApplicationInfo2> appList,
			PackageManager pm) {
		this.appList = appList;
		inflater = LayoutInflater.from(context);
	}


	public int getCount() {
		return appList.size();
	}


	public Object getItem(int position) {
		return appList.get(position);
	}


	public long getItemId(int position) {
		return position;
	}


	public View getView(int position, View convertView, ViewGroup parent) {
		final ApplicationInfo2 info = appList.get(position);
		ViewHolder holder = null;
		if(null == convertView){
			convertView = inflater.inflate(R.layout.app_list_item, null);
			holder = new ViewHolder();
			holder.lv_image = (ImageView) convertView.findViewById(R.id.lv_icon);
			holder.lv_name = (TextView) convertView.findViewById(R.id.lv_item_appname);
			holder.lv_packname = (TextView) convertView.findViewById(R.id.lv_item_packageame);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.lv_image.setImageDrawable(info.icon);
		final CharSequence name = info.title;
		final CharSequence packName = info.packageName;
		holder.lv_name.setText(name);
		holder.lv_packname.setText(packName);
		return convertView;
	}
	private final static  class ViewHolder{
		ImageView lv_image;
		 TextView lv_name;
		 TextView lv_packname;
	}

	
}
