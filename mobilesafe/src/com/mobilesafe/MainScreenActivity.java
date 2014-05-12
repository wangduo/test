package com.mobilesafe;
import com.mobilesafe.adapter.MainGridViewAdapter;
import com.mobilesafe.util.IntentUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MainScreenActivity extends Activity implements OnItemClickListener {
	private static final String TAG = "MainScreenActivity";
	private GridView gv;
	private static final int DIALOG_EXIT = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		gv = (GridView) this.findViewById(R.id.gv_all);
		gv.setAdapter(new MainGridViewAdapter(this));
		gv.setOnItemClickListener(this);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater infalter = getMenuInflater();
		infalter.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
	
		case DIALOG_EXIT:
			return new AlertDialog.Builder(this)
			.setTitle(R.string.prompt_ts)
			.setMessage(R.string.ok_exit)
			.setPositiveButton(R.string.ok,new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			})
			.setNegativeButton(R.string.cancel, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

				}
			}).create();
			
		}
		return null;
	}
	/**
	 * open menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int itemId = item.getItemId();
		switch(itemId){
		case R.id.menu_update_version:
			new Thread(new CheckVersionTask(this)).start();
			break;
		case R.id.menu_exit:
			showDialog(DIALOG_EXIT);
			break;
			
		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * ÍË³öµ½Home´°¿Ú
	 */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			IntentUtils.sendActivity(this, LostProtectActivity.class);
			break;
		case 1:
			IntentUtils.sendActivity(this, CallSmsSafeActivity.class);
			break;
		case 2:
			IntentUtils.sendActivity(this, ShowAppActivity.class);
			break;
		case 3:
			IntentUtils.sendActivity(this, TaskManagerActivity.class);
			break;
		case 4:
			IntentUtils.sendActivity(this, SlidingdrawerActivity.class);
			break;
		case 5:
			break;
		case 6:
			break;
		case 7:
			IntentUtils.sendActivity(this, ToolsActivity.class);
			break;
		case 8:
			IntentUtils.sendActivity(this, SttingCenterActivity.class);
			break;
		}

	}


}
