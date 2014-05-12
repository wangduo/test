package com.mobilesafe.service;

import java.lang.reflect.Method;


import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.mobilesafe.R;
import com.mobilesafe.db.dao.BlackNumberDao;

public class ShowAddressService extends Service {

	private BlackNumberDao blackDao;
	private static final String TAG = "ShowAddressService";
	private TelephonyManager tel;
	private WindowManager wm;
	private WindowManager.LayoutParams params;
	private View view;
	private LayoutInflater inflater;
	private CallStateListener callStateListenr;
	private SharedPreferences sp ;
	private Editor editor;
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
		tel.listen(callStateListenr, PhoneStateListener.LISTEN_NONE);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		callStateListenr = new CallStateListener();
		blackDao = new BlackNumberDao(this);
		inflater = LayoutInflater.from(getApplicationContext());
		wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
		tel = (TelephonyManager) getApplicationContext().getSystemService(
				TELEPHONY_SERVICE);
		tel.listen(callStateListenr, PhoneStateListener.LISTEN_CALL_STATE);
	}

	public class CallStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING: // 电话铃响
				Log.i(TAG, incomingNumber);
				if (blackDao.isBlackNumber(incomingNumber)) {
					endCall(incomingNumber);
					return;
				}
				AddressService addressService = new AddressService(
						getApplicationContext());
				String address = addressService.getAddress(incomingNumber);
				showAddress(address, incomingNumber);
				break;

			case TelephonyManager.CALL_STATE_OFFHOOK: // 电话挂起
				Log.i(TAG, "CALL_STATE_OFFHOOK");
				break;

			case TelephonyManager.CALL_STATE_IDLE:
				Log.i(TAG, "TelephonyManager.CALL_STATE_IDLE");
				removeView();

				break;
			}

		}

		private void endCall(String incomingNumber) {
			try {
				Method method = Class.forName("android.os.ServiceManager")
						.getMethod("getService", String.class);
				IBinder binger = (IBinder) method.invoke(null,
						new Object[] { TELEPHONY_SERVICE });
				ITelephony telephony = ITelephony.Stub.asInterface(binger);
				telephony.endCall();
				// 清除黑名单电话打来的电话记录
				getApplicationContext().getContentResolver()
						.registerContentObserver(
								CallLog.Calls.CONTENT_URI,
								true,
								new CallLogChangeLinster(new Handler(),
										incomingNumber));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private class CallLogChangeLinster extends ContentObserver {
			private String number;

			public CallLogChangeLinster(Handler handler, String incomingNumber) {
				super(handler);
				this.number = incomingNumber;
			}

			@Override
			public void onChange(boolean selfChange) {
				deleteLog(number);
			}

			private void deleteLog(String number2) {
				ContentResolver resolver = getApplicationContext()
						.getContentResolver();
				Cursor cur = resolver.query(CallLog.Calls.CONTENT_URI,
						new String[] { "_id" }, "number = ?",
						new String[] { number2 }, "_id desc");
				if (cur.moveToFirst()) {
					int id = cur.getInt(0);
					resolver.delete(CallLog.Calls.CONTENT_URI, "_id=" + id,
							null);
				}
				cur.close();
			}

		}

		

		
		private void removeView() {
			if (null != view) {
				Log.i(TAG, "removeView");
				wm.removeView(view);
			}

		}
	}
	/**
	 * 根据电话号码获得联系人中的电话联系人
	 * 
	 * @param number
	 * @return
	 */
	private String getContactNameByNumber(String number) {
		String name = number;
		Uri uri = Uri
				.parse("content://com.android.contacts/data/phones/filter/"
						+ number);
		ContentResolver resolver = getApplicationContext()
				.getContentResolver();
		Cursor cur = resolver.query(uri, new String[] { "display_name" },
				null, null, null);
		if (cur.moveToFirst()) {
			name = cur.getString(0);
		}
		return name;
	}
	private void showAddress(String address, String incomingNumber) {
		Log.i(TAG, address);
		view = inflater.inflate(R.layout.show_phone_address, null);
		TextView phoneAddress = (TextView) view
				.findViewById(R.id.phone_address);
		TextView phoneName = (TextView) view.findViewById(R.id.phone_name);
		phoneAddress.setText(address);
		phoneName.setText(getContactNameByNumber(incomingNumber));

		params = new LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		// 设置Window flag
		params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;
		
		params.type = LayoutParams.TYPE_PHONE;
		params.gravity = Gravity.CENTER | Gravity.TOP;
		
        int dx = sp.getInt("shiftX", 0);
        int dy = sp.getInt("shiftY", 0);
        params.x+=dx;
		params.y += dy;
		wm.addView(view, params);
		
		
		view.setOnTouchListener(new View.OnTouchListener() {
			int StartX;
	    	int StartY;
	    	int X,Y,dx,dy,top,buttom,left,right;

			public boolean onTouch(View v, MotionEvent event) {
				int eventaction = event.getAction();

				switch (eventaction) {
				case MotionEvent.ACTION_DOWN: // touch down so check if the
					StartX = (int)event.getRawX();
					StartY = (int)event.getRawY();
					break;

				case MotionEvent.ACTION_MOVE: // touch drag with the ball
					X = (int) event.getRawX();
					Y = (int) event.getRawY();
					
					dx = X- StartX;
					dy = Y - StartY;
					
					top = v.getTop()+dy;
					buttom=  v.getBottom()+dy;
					left = v.getLeft()+dx;
					right = v.getRight()+dx;
					v.layout(left, top, right, buttom);
					StartX = (int) event.getRawX();
					StartY = (int) event.getRawY();
					params.x += left;
					params.y += top;
					wm.updateViewLayout(v, params);
					break;
				case MotionEvent.ACTION_UP:
					editor = sp.edit();
					editor.putInt("shiftX", params.x);
					editor.putInt("shiftY",params.y);
					editor.commit();
					break;
				}
				return false;
			}

		});
	}


}
