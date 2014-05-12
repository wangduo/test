package com.mobilesafe.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.bean.DetailProceess;
import com.mobilesafe.bean.PackagesInfo;

public final class ProcessListAdapter extends BaseAdapter {
	private static final String TAG = "ProcessListAdapter";
	private ArrayList<DetailProceess> lists;
	private LayoutInflater inflater;
	private PackageManager pm;
	private PackagesInfo packsInfo;

	public ProcessListAdapter(Context context, ArrayList<DetailProceess> _lists,PackagesInfo packInfo) {
		inflater = LayoutInflater.from(context);
		this.lists = _lists;
		this.pm = context.getPackageManager();
		this.packsInfo = packInfo;
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


	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (null == view) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.task_list_item, null);
			holder.iv_icon = (ImageView) view.findViewById(R.id.task_lv_icon);
			holder.tv_name = (TextView) view
					.findViewById(R.id.task_item_appname);
			holder.tv_size = (TextView) view
					.findViewById(R.id.task_item_memory);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		final DetailProceess dp = lists.get(position);
		dp.fetchApplicationInfo(packsInfo);
		holder.tv_size.setText(dp.getMemory() + "KB");
		holder.iv_icon.setImageDrawable(dp.getAppinfo().loadIcon(pm));
		try {
			holder.tv_name.setText(dp.getAppName());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		return view;
	}

	private final static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_size;
	}

}
