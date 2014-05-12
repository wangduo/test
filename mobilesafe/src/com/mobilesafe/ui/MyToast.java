package com.mobilesafe.ui;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyToast {

	public static void showToast(Context context,String text,int id){
		Toast toast = new Toast(context);
		toast.setGravity(Gravity.BOTTOM, 0, -20);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setDuration(Toast.LENGTH_SHORT);
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		ImageView iv = new ImageView(context);
		TextView tv = new TextView(context);
		tv.setText(text);
		iv.setImageResource(id);
		linearLayout.addView(iv);
		linearLayout.addView(tv);	
		toast.setView(linearLayout);
		toast.show();
		
	}
}
