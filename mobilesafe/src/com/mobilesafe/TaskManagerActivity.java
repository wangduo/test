package com.mobilesafe;

import java.util.ArrayList;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilesafe.adapter.ProcessListAdapter;
import com.mobilesafe.bean.DetailProceess;
import com.mobilesafe.bean.PackagesInfo;

public class TaskManagerActivity extends Activity {
	private static final String TAG = "TaskManagerActivity";
	private TextView tv_memory_info;
	private ListView lv;
	private ActivityManager am;
	private ProcessListAdapter plAdapter;
	public static final int DIALOG_START_TASK = 0;
	private ArrayList<DetailProceess> avaiAppInfo;
	private static final int DELE_APP = 0;
	private static final int SHOW_APP_DETAIL = 1;
	private static final int START_APP = 2;
	private static final int UNINSTALL_APP = 3;
	
	private static final int HANDLER_REFRESH = 1;
	
	private static final int DIALOG_PROGRESSDIALOG = 1;
	
	
	private PackagesInfo packsInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.task_manager);
		tv_memory_info = (TextView) findViewById(R.id.memory_info);
		lv = (ListView) findViewById(R.id.process_list_view);
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		
		tv_memory_info.setText(getSystemAvaialbeMemorySize());
		lv.setOnItemClickListener(new TaskItemClickLinstener());	
	}
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			final int what = msg.what;
			switch(what){
			case HANDLER_REFRESH:
				lv.setAdapter(plAdapter);
				dismissDialog(DIALOG_PROGRESSDIALOG);
				break;
			}
			
		}
		
	};
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case DIALOG_PROGRESSDIALOG:
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Loading.....");
			return progressDialog;	
		}
		return null;
	}
	private void refresh() {
		showDialog(DIALOG_PROGRESSDIALOG);
		new Thread(new Runnable() {
			public void run() {
				getRunningProcess();
				handler.obtainMessage(HANDLER_REFRESH).sendToTarget();
			}

		}).start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		packsInfo = new PackagesInfo(TaskManagerActivity.this);
		refresh();
	}

	

	private String getSystemAvaialbeMemorySize() {
		// 获得MemoryInfo对象
		ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(outInfo);
		String availMem = Formatter.formatFileSize(TaskManagerActivity.this,
				outInfo.availMem);
		return availMem;
	}

	private final class TaskItemClickLinstener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> view, View arg1,
				final int position, long arg3) {
			final DetailProceess dp = (DetailProceess) view
					.getItemAtPosition(position);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					TaskManagerActivity.this);
			builder.setTitle(R.string.options);
			builder.setItems(R.array.taskchoice, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					final String processName = dp.getProcessName();
					switch (which) {
					case DELE_APP:
						am.restartPackage(dp.getProcessName());
						refresh();
						break;
					case SHOW_APP_DETAIL:
						try {
							showAppDetail(dp);
						} catch (Exception e) {
							Log.e(TAG, e.getMessage());
						}
						break;
					case START_APP:
						try {
							startApp(dp);
						} catch (Exception e) {
							Log.e(TAG, e.getMessage());
						}
						break;
					case UNINSTALL_APP:
						Intent deleteintent = new Intent();
						deleteintent.setAction(Intent.ACTION_DELETE);
						deleteintent.setData(Uri
								.parse("package:" + processName));
						startActivityForResult(deleteintent, position);
						break;
					}
					dialog.dismiss();
				}

			}).create().show();

		}

		private void startApp(DetailProceess dp) throws Exception {
			Intent intent = new Intent();
			intent.setComponent(new ComponentName(dp.getProcessName(), dp
					.getActivityName()));
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

		}

		private void showAppDetail(DetailProceess dp) throws Exception {
			Intent intent = new Intent(TaskManagerActivity.this,
					ShowAppDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("packagename", dp.getProcessName());
			bundle.putString("appversion", dp.getAppVersion());
			bundle.putString("appname", dp.getAppName());
			bundle.putStringArray("apppremissions", dp.getPermissions());
			intent.putExtras(bundle);
			startActivity(intent);
		}

	}

	public void getRunningProcess() {
		List<RunningAppProcessInfo> allRunning = am.getRunningAppProcesses();
		avaiAppInfo = new ArrayList<DetailProceess>();
		for (RunningAppProcessInfo info : allRunning) {
			String processName = info.processName;

			if ("system".equals(processName)
					|| "com.mobilesafe".equals(processName)
					|| "com.android.phone".equals(processName)
					|| "android.process.acore".equals(processName)
					|| "android.process.media".equals(processName)) {
				continue;
			}
			
			final DetailProceess dp = new DetailProceess(this, info);
			dp.fetchApplicationInfo(packsInfo);
			dp.getPackageInfo();
			if(dp.isGoodProcess()){
				avaiAppInfo.add(dp);	
			}
						
		}
		plAdapter = new ProcessListAdapter(this, avaiAppInfo,packsInfo);
	}

	/**
	 * 杀死进程
	 * 
	 * @param view
	 */
	public void killAllProcess(View view) {
		for (DetailProceess dp : avaiAppInfo) {
			am.killBackgroundProcesses(dp.getProcessName());
		}
		tv_memory_info.setText(getSystemAvaialbeMemorySize());
		refresh();
		
		
	}
}
