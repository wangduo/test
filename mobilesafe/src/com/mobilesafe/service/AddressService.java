package com.mobilesafe.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.mobilesafe.R;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressService {

	private Context context;
	public static final String DBNAME= "address.db";
	
	private static AddressService instance;
	
	public static AddressService getInstance(Context context){
		if(null == instance){
			instance = new AddressService(context);
		}
		return instance;
	}
	public AddressService(Context context){
		this.context = context;
	}
	
	/**
	 * 判断数据库是否存在
	 * @return
	 */
	public boolean isExist(){
		final File file = new File(context.getFilesDir(), DBNAME);
		return file.exists();
	}
	/**
	 * 拷贝数据库到系统的/data/data/<包名>/下
	 * @throws Exception
	 */
	public void copyDateBase() throws Exception{
		final InputStream is = this.getClass().getClassLoader().getResourceAsStream(DBNAME);
		final File file = new File(context.getFilesDir(),DBNAME);
		final FileOutputStream fos = new FileOutputStream(file);
		byte [] buffer = new byte[1024];
		int len ;
		while((len = is.read(buffer))!= -1){
			fos.write(buffer, 0, len);
		}
		buffer = null;
		fos.close();
		is.close();
	}
	
	public String getAddress(String number){
		String address = number ;
		File file = new File(context.getFilesDir(),DBNAME);
		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		Cursor cur = null;
		if(db.isOpen()){
			if(number.matches("^1[358]\\d{9}$")){	//查询手机号
				number = number.substring(0,7);
				cur = db.rawQuery("select city from info where mobileprefix=?", new String []{number});
				if(cur.moveToFirst()){
					address = cur.getString(0);
				}
				cur.close();
				cur = null;
			}
			else {
				switch(number.length()){
				case 7:
					address = context.getApplicationContext().getResources().getString(R.string.local);
					break;
				case 8:
					address = context.getApplicationContext().getResources().getString(R.string.local);
					break;
				case 10:
					number = number.substring(0,3);
					cur = db.rawQuery("select city from info where area=?", new String[]{number});
					if(cur.moveToFirst()){
						address = cur.getString(0);
					}
					cur.close();
					cur = null;
					break;
				case 11:
					final String numberprefix_3 = number.substring(0, 3);
					final String numberprefix_4 = number.substring(0, 4);
					cur = db.rawQuery("slect city from info where area=? or area=?", new String[]{numberprefix_3,numberprefix_4});
					if(cur.moveToFirst()){
						address = cur.getString(0);
					}
					cur.close();
					cur = null;
					break;
				case 12:
					number = number.substring(0,4);
					cur = db.rawQuery("select city from info where area=?", new String[]{number});
					if(cur.moveToFirst()){
						address = cur.getString(0);
					}
					cur.close();
					cur = null;
					break;
				case 4:	//模拟器
					address = context.getApplicationContext().getResources().getString(R.string.local);
					break;
				}
			}
		}
		db.close();
		return address;
	} 
}
