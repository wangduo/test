package com.mobilesafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.mobilesafe.adapter.ShowAppListAdapter;
import com.mobilesafe.ui.MyToast;

public class ShowAppActivity extends Activity {
	private static final String TAG = "ShowAppActivity";
	private PackageManager pm;
	private ArrayList<ApplicationInfo2> appInfos;
	private boolean flag = true;
	private ListView lv_app;
	private static final int DIALOG_LOADING = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_list);
		pm = getPackageManager();
		lv_app = (ListView) findViewById(R.id.app_list_view);
		lv_app.setOnItemClickListener(new AppDetailLinster());
		showDialog(DIALOG_LOADING);
	}

	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case DIALOG_LOADING:
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("Loading.....");
			return dialog;
		}
		return null;
	}

	private void loadAnimation() {
		AnimationSet set = new AnimationSet(false);
		Animation animation = new TranslateAnimation(1, 13, 10, 50);
		animation.setDuration(300);
		set.addAnimation(animation);
		animation = new RotateAnimation(30, 10);
		animation.setDuration(300);
		set.addAnimation(animation);
		// lv.startAnimation(set);
		LayoutAnimationController controller = new LayoutAnimationController(
				set, 1);
		controller
				.setInterpolator(this, android.R.anim.accelerate_interpolator);
		controller.setAnimation(set);
		lv_app.setLayoutAnimation(controller);
		lv_app.startLayoutAnimation();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		loadAnimation();
	}

	@Override
	protected void onStart() {
		super.onStart();
		new Thread(runable).start();
	}

	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			lv_app.setAdapter(new ShowAppListAdapter(ShowAppActivity.this,
					appInfos, pm));
			dismissDialog(DIALOG_LOADING);
			
		}

	};
	private final Runnable runable = new Runnable() {

		public void run() {
			loadApplications();
			myHandler.obtainMessage().sendToTarget();
		}

	};

	private void loadApplications() {
		PackageManager manager = this.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final List<ResolveInfo> apps = manager.queryIntentActivities(
				mainIntent, 0);
		Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
		if (apps != null) {
			final int count = apps.size();

			if (appInfos == null) {
				appInfos = new ArrayList<ApplicationInfo2>(count);
			}
			appInfos.clear();

			for (int i = 0; i < count; i++) {
				ApplicationInfo2 application = new ApplicationInfo2();
				ResolveInfo info = apps.get(i);
				application.title = info.loadLabel(manager);
				application.setActivity(new ComponentName(
						info.activityInfo.applicationInfo.packageName,
						info.activityInfo.name), Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				application.flags = info.activityInfo.applicationInfo.flags;

				application.icon = info.activityInfo.loadIcon(manager);
				application.packageName = info.activityInfo.applicationInfo.packageName;
				appInfos.add(application);
			}
		}
	}

	public final class AppDetailLinster implements OnItemClickListener {

		AlertDialog dialog;

		public void onItemClick(AdapterView<?> view, View arg1,
				final int position, long arg3) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					ShowAppActivity.this);
			builder.setTitle("选项");
			builder.setItems(R.array.choice, new OnClickListener() {

		
				public void onClick(DialogInterface dialog, int which) {

					final ApplicationInfo2 appInfo = appInfos.get(position);
					switch (which) {
					case 0: // 启动程序
						try {
							startApp(appInfo);
						} catch (Exception e) {
							Log.e(TAG, e.getMessage());
						}
						break;
					case 1: // 详细信息
						try {
							showAppDetail(appInfo);
						} catch (Exception e) {
							Log.e(TAG, e.getMessage());
						}
						break;
					case 2: // 卸载程序
						final String packagename = appInfo.packageName
								.toString();
						Intent deleteIntent = new Intent();
						deleteIntent.setAction(Intent.ACTION_DELETE);
						deleteIntent.setData(Uri
								.parse("package:" + packagename));
						startActivityForResult(deleteIntent, position);
						break;
					}
					dialog.dismiss();
				}

				private void showAppDetail(ApplicationInfo2 appInfo)
						throws Exception {
					final String packName = appInfo.packageName.toString();
					final PackageInfo packInfo = getAppPackinfo(packName);
					final String versionName = packInfo.versionName;
					final String[] apppremissions = packInfo.requestedPermissions;
					final String appName = appInfo.title.toString();
					Intent showDetailIntent = new Intent(ShowAppActivity.this,
							ShowAppDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("packagename", packName);
					bundle.putString("appversion", versionName);
					bundle.putStringArray("apppremissions", apppremissions);
					bundle.putString("appname", appName);
					showDetailIntent.putExtras(bundle);
					startActivity(showDetailIntent);

				}

				private void startApp(ApplicationInfo2 appInfo)
						throws Exception {
					final String packName = appInfo.packageName.toString();
					final String activityName = getActivityName(packName);
					if (null == activityName) {
						Toast.makeText(ShowAppActivity.this, "程序无法启动",
								Toast.LENGTH_SHORT);
						return;
					}
					Intent intent = new Intent();
					intent.setComponent(new ComponentName(packName,
							activityName));
					startActivity(intent);
				}

			});
			dialog = builder.create();
			dialog.show();

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		appInfos.remove(requestCode);
		lv_app.setAdapter(new ShowAppListAdapter(this, appInfos, pm));
	}

	public PackageInfo getAppPackinfo(String packName) throws Exception {
		return pm.getPackageInfo(packName, PackageManager.GET_ACTIVITIES
				| PackageManager.GET_PERMISSIONS);
	}

	public String getActivityName(String packName) throws Exception {
		final PackageInfo packInfo = pm.getPackageInfo(packName,
				PackageManager.GET_ACTIVITIES);
		final ActivityInfo[] activitys = packInfo.activities;
		if (null == activitys || activitys.length <= 0) {
			return null;
		}
		return activitys[0].name;
	}

}
