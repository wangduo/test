package com.mobilesafe;

import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mobilesafe.bean.UpdateInfo;
import com.mobilesafe.net.download.DownloadProgressListener;
import com.mobilesafe.net.download.FileDownloader;
import com.mobilesafe.service.AddressService;
import com.mobilesafe.util.UpdateInfoParser;



public class SplashActivity extends Activity {
	private static final String TAG = "SplashActivity";
	private static final int CONNECT_SERVER_ERROR = 0;
	private static final int NEED_UPDATE = 1;
	private static final int DIALOG_DOWNLOAD = 2;
	private static final int SHOW_MESSAGE = 3;
	private static final int HIDE_MESSAGE = 4;
	private static final int HANDLER_DOWENLOAD = 5;
	private UpdateInfo info;
	private TextView tvCopydb;
	private AddressService addressService;
	private PackageManager pm;
	private ProgressBar pb;
	private TextView resultView;
	private Thread downloadThread;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case NEED_UPDATE:
				showDialog(NEED_UPDATE);
				break;
			case CONNECT_SERVER_ERROR:
				showDialog(CONNECT_SERVER_ERROR);
				break;
			case SHOW_MESSAGE:
				tvCopydb.setVisibility(View.VISIBLE);
				break;
			case HIDE_MESSAGE:
				tvCopydb.setVisibility(View.INVISIBLE);
				break;
			case HANDLER_DOWENLOAD:
				final int size = msg.getData().getInt("size");
				pb.setProgress(size);
				int progress = pb.getProgress();
				int max = pb.getMax();
				float num = (float)progress / (float)max;
				int result = (int)(num * 100);
				resultView.setText(result+ "%");
				if(progress==max){
					dismissDialog(DIALOG_DOWNLOAD);
					handler.removeCallbacks(downloadThread);
					install();	
				}
				break;

			case -1:
				Toast.makeText(SplashActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash_activity);
		tvCopydb = (TextView) findViewById(R.id.copydatabase);
		addressService = new AddressService(this);
		boolean isexist = addressService.isExist();
		if (isexist) {
			new Thread(new CheckVersionTask()).start();
		} else {
			new Thread(new CopyDBTask()).start();
		}
		AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(5000);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl_splash_id);
		rl.setAnimation(animation);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case CONNECT_SERVER_ERROR:	//连接出错
			return new AlertDialog.Builder(SplashActivity.this)
					.setTitle(R.string.prompt_ts).setMessage(R.string.err_server)
					.setPositiveButton(R.string.ok, new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							sendActivity();
							dialog.dismiss();
							SplashActivity.this.finish();
						}
					}).create();
		
		case NEED_UPDATE:	//版本更新
			return new AlertDialog.Builder(SplashActivity.this)
			.setTitle(R.string.please_update)
			.setMessage(info.getDescription())
			.setPositiveButton(R.string.ok, new OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					showDialog(DIALOG_DOWNLOAD);
					download();
					
				}
			})
			.setNegativeButton(R.string.cancel,
					new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							finish();
							sendActivity();
						}
					})
			.create();
			
		case DIALOG_DOWNLOAD:	//更新进度条
			LayoutInflater infalter = LayoutInflater.from(SplashActivity.this);
        	View dialog = infalter.inflate(R.layout.dialog, null);
        	pb = (ProgressBar) dialog.findViewById(R.id.dilaog_download_pb);
        	resultView = (TextView) dialog.findViewById(R.id.dialog_begin_tv);
        	return  new AlertDialog.Builder(this)
        	.setTitle(R.string.dowload)
        	.setView(dialog)
        	.create();
		}
		return null;
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
				FileDownloader loader = new FileDownloader(SplashActivity.this, path, saveFile, 3);
				pb.setMax(loader.getFileSize());//设置进度条的最大刻度为文件的长度
				try {
					loader.download(new DownloadProgressListener() {

						public void onDownloadSize(int size) {//实时获知文件已经下载的数据长度
							Message msg = new Message();
							msg.what = HANDLER_DOWENLOAD;
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
    private void download(){
    	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
    		try {
				download(info.getUrl(),Environment.getExternalStorageDirectory());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
    	}else {
    		Toast.makeText(this, "下载失败", Toast.LENGTH_LONG).show();
    	}	
    }
	/*调用系统安装界面*/
	private void install() {
		File file = new File(Environment.getExternalStorageDirectory(),"mobilesafe.apk");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		finish();
		startActivity(intent);
	}
	private void sendActivity() {
		Intent intent = new Intent(this, MainScreenActivity.class);
		startActivity(intent);
		this.finish();
	}

	private void sendMessage(int _what) {
		Message message = new Message();
		message.what = _what;
		handler.sendMessage(message);
	}

	

	private class CopyDBTask implements Runnable {
		public void run() {

			try {
				sendMessage(SHOW_MESSAGE);
				addressService.copyDateBase();
				sendMessage(HIDE_MESSAGE);
				new Thread(new CheckVersionTask()).start();
			} catch (Exception e) {
			}
		}
	}

	private class CheckVersionTask implements Runnable {
		public void run() {
			//更新服务器路径
			String path = getApplicationContext().getResources().getString(
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
				pm = SplashActivity.this.getPackageManager();
				final PackageInfo packInfo = pm.getPackageInfo("com.mobilesafe",
						PackageManager.GET_ACTIVITIES);
				final String version = packInfo.versionName;
				/*版本相同无需下载*/
				if (version.equals(info.getVersoin())) {
					Log.i(TAG, "版本相同");
					sendActivity();
					return ;
				} else {
					Log.i(TAG, "版本不同");
					sendMessage(NEED_UPDATE);
					return ;
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				sendMessage(CONNECT_SERVER_ERROR);
				return;
			}

		}

	}

}
