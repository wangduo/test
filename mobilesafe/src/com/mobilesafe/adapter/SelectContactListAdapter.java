package com.mobilesafe.adapter;

import java.util.ArrayList;

import com.mobilesafe.R;
import com.mobilesafe.bean.ContactBean;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SelectContactListAdapter extends BaseAdapter {
	private static final String TAG = "SelectContactListAdapter";
	private ArrayList<ContactBean> peoples ;
	private LayoutInflater inflater;
	public SelectContactListAdapter(ArrayList<ContactBean> peoples,
			Context context) {
		this.peoples = peoples;
		inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
	}


	public int getCount() {
		return peoples.size();
	}


	public Object getItem(int position) {
		return peoples.get(position);
	}


	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null ;
		if(null == convertView){
			convertView = inflater.inflate(R.layout.contact_list_item, null);
			holder = new ViewHolder();
			holder.tv_name = (TextView) convertView.findViewById(R.id.select_list_name);
			holder.tv_number = (TextView) convertView.findViewById(R.id.select_list_number);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_name.setText(peoples.get(position).getContactName());
		final String number = peoples.get(position).getContactPhone(); // null
		if(null == number || "".equals(number)){
			holder.tv_number.setText(peoples.get(position).getContactHomePhone());
		}else {
			holder.tv_number.setText(number);
		}
		return convertView;
	}
	final static class ViewHolder{
		 TextView tv_name;
    	 TextView tv_number;
	}
}
