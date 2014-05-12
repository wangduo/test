package com.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobilesafe.db.DBOpenHelper;

public class BlackNumberDao {

	private static final String TAG = "BlackNumberDao";
	private DBOpenHelper dbOpenHelper;
	public BlackNumberDao(Context context){
		dbOpenHelper = new DBOpenHelper(context);
	}
	/**
	 * 获取所有的黑名单联系人信息
	 * @return
	 */
	public ArrayList<HashMap<String,Object>> getBlackNumbers(){
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		SQLiteDatabase  db = dbOpenHelper.getReadableDatabase();
		if(db.isOpen()){
			HashMap<String,Object> map ;
			Cursor cur = db.rawQuery("select _id,number from blacknumber order by _id desc ", null);
			while(cur.moveToNext()){
				map = new HashMap<String,Object>();
				map.put("id", cur.getString(0));
				map.put("number", cur.getString(1));
				list.add(map);
			}
			cur.close();
			db.close();
			map = null;
		}
		return list;
	} 
	/**
	 * 增加黑名单
	 * @param number
	 */
	public void add(String number){
		if(!isBlackNumber(number)){
			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
			if(db.isOpen()){
				db.execSQL("insert into blacknumber (number) values (?)", new String[]{number});
			}
			db.close();
		}
	}
	public void delete(String number){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if(db.isOpen()){
			db.execSQL("delete from blacknumber where number=?", new String[]{number});
		}
		db.close();
	}
	public boolean isBlackNumber(String number){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if(db.isOpen()){
			
			Cursor cur = db.rawQuery("select count(_id) from blacknumber where number = ? ", new String []{number});
		
			if(cur.moveToFirst()){
				if(cur.getInt(0) > 0){
					return true;
				}
			}
			cur.close();
			db.close();
			
			
		}
		return false;
	}
	
}
