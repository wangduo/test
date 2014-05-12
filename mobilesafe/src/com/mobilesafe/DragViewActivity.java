package com.mobilesafe;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
public class DragViewActivity extends Activity {
	public static final String TAG = "DragViewActivity";
	private ImageButton imagebutton;
	private SharedPreferences sp ;
	private Editor editor;
	public static final int LOCATIONX = 160;
	public static final int LOCATIONY = 300;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	sp= getSharedPreferences("config", MODE_PRIVATE);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dragview);
        imagebutton = (ImageButton) this.findViewById(R.id.imagebutton);
        imagebutton.setOnTouchListener(new MyTouchListener());
    }
    
     public class MyTouchListener implements OnTouchListener{
    	 int StartX;
    	 int StartY;
		public boolean onTouch(View v, MotionEvent event) {
			//Logger.i(TAG,event.getAction()+"");
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			//	Logger.i(TAG,"按下去了");
				StartX = (int)event.getRawX();
				StartY = (int)event.getRawY();
				break;

			case MotionEvent.ACTION_MOVE:
			//	Logger.i(TAG,"手指在移动");
				final int X = (int) event.getRawX();
				final int Y = (int) event.getRawY();
				
				final int dx = X - StartX;
				final int dy = Y - StartY;
				
				final int top = v.getTop()+dy;
				final int buttom=  v.getBottom()+dy;
				final int left = v.getLeft()+dx;
				final int right = v.getRight()+dx;
				v.layout(left, top, right, buttom);
				StartX = (int) event.getRawX();
				StartY = (int) event.getRawY();
				break;
				
			case MotionEvent.ACTION_UP:
				final int LastX = (int)event.getRawX();
				final int LastY = (int)event.getRawY();
				final int shiftX = LastX - LOCATIONX;
				final int shiftY = LastY - LOCATIONY;
				editor = sp.edit();
				editor.putInt("shiftX", shiftX);
				editor.putInt("shiftY",shiftY);
				editor.commit();
				break;
			}
			return false;
		}
    }
    	
    
}