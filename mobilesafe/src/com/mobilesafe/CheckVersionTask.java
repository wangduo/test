package com.mobilesafe;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesafe.bean.UpdateInfo;
import com.mobilesafe.net.download.DownloadProgressListener;
import com.mobilesafe.net.download.FileDownloader;
import com.mobilesafe.util.UpdateInfoParser;

public class CheckVersionTask implements Runnable {
	private static final String TAG = "CheckVersionTask";
	private Context context;
	private UpdateInfo info;
	private ProgressBar pb;
	private TextView resultView;
	private Thread downloadThread;
	private AlertDialog dialog;
	public CheckVersionTask(Context context){
		this.context = context;
	}
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				CreateDialog(1);
				break;
			case 1:
				final int size = msg.getData().getInt("size");
				pb.setProgress(size);
				int progress = pb.getProgress();
				int max = pb.getMax();
				float num = (float)progress / (float)max;
				int result = (int)(num * 100);
				resultView.setText(result+ "%");
				if(progress==max){
					dialog.dismiss();
					handler.removeCallbacks(downloadThread);
					install();	
				}
				break;
			case 2:
				CreateDialog(2);
				break;
			case 3:
				CreateDialog(4);
			case -1:
				Toast.makeText(context, R.string.error, 1).show();
				break;
			}
		}
	};
	/*调用系统安装界面*/
	private void install() {
		File file = new File(Environment.getExternalStorageDirectory(),"mobilesafe.apk");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}
	
	
	private void CreateDialog(int id){
		switch(id){
		case 1:	//最新版本，无需更新
			 new  AlertDialog.Builder(context)
			.setTitle(R.string.prompt_ts)
			.setMessage(R.string.ok_version)
			.setPositiveButton(R.string.ok, null)
			.create().show();
			 break;
		case 2:	//版本不同，需要更新
			new AlertDialog.Builder(context)
			.setTitle(R.string.please_update)
			.setMessage(info.getDescription())
			.setPositiveButton(R.string.ok, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					CreateDialog(3);
					download();
				}
			})
			.setNegativeButton(R.string.cancel,
					new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
			.create()
			.show();
			break;
		case 3:		//更新进度条
			LayoutInflater infalter = LayoutInflater.from(context);
        	View view = infalter.inflate(R.layout.dialog, null);
        	pb = (ProgressBar) view.findViewById(R.id.dilaog_download_pb);
        	resultView = (TextView)view.findViewById(R.id.dialog_begin_tv);
        	AlertDialog.Builder builder = new AlertDialog.Builder(context);
        	builder.setTitle(R.string.dowload);
        	builder.setView(view);
        	dialog = builder.create();
        	dialog.show();
        	break;
		case 4:
			new AlertDialog.Builder(context)
			.setTitle(R.string.prompt_ts).setMessage(R.string.err_server)
			.setPositiveButton(R.string.ok,null)
			.create()
			.show();
		}
		
			
	}
	 private void download(){
	    	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
	    		try {
					download(info.getUrl(),Environment.getExternalStorageDirectory());
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
	    	}else {
	    		Toast.makeText(context, "下载失败", Toast.LENGTH_LONG).show();
	    	}	
	  }
	 /**
	     * 下载apk文件
	     * @param path
	     * @param saveFile
	     * @throws IOException
	     */
	    public void download(final String path,final File saveFile) throws IOException{
	    	downloadThread = new Thread(new Runnable() {			
				public void run() {
					FileDownloader loader = new FileDownloader(context, path, saveFile, 3);
					pb.setMax(loader.getFileSize());//设置进度条的最大刻度为文件的长度
					try {
						loader.download(new DownloadProgressListener() {
							public void onDownloadSize(int size) {//实时获知文件已经下载的数据长度
								Message msg = new Message();
								msg.what = 1;
								msg.getData().putInt("size", size);
								handler.sendMessage(msg);//发送消息
							}
						});
					} catch (Exception e) {
						handler.obtainMessage(-1).sendToTarget();
					}
				}
			});
	    	downloadThread.start();

	    }
	public void run() {
		//更新服务器路径
		String path = context.getApplicationContext().getResources().getString(
				R.string.server_url);
		try {

			final URL url = new URL(path);
			final HttpURLConnection conn = (HttpURLConnection) url
					.openConnection();
			conn.setConnectTimeout(5000);
			final InputStream is = conn.getInputStream();
			if (null == is) {
				return;
			}
			info = UpdateInfoParser.getInstance().getUpdateInfo(is);
			conn.disconnect();
			is.close();
			final PackageManager pm = context.getPackageManager();
			final PackageInfo packInfo = pm.getPackageInfo("com.mobilesafe",
					PackageManager.GET_ACTIVITIES);
			final String version = packInfo.versionName;
			/*版本相同无需下载*/
			if (version.equals(info.getVersoin())) {
				handler.obtainMessage(0).sendToTarget();
			} else {
				Log.i(TAG, "版本不同");
				
				handler.obtainMessage(2).sendToTarget();
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			handler.obtainMessage(3).sendToTarget();
			return;
		}


	}

}
