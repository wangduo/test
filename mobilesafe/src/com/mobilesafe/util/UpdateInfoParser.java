package com.mobilesafe.util;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.mobilesafe.bean.UpdateInfo;

public class UpdateInfoParser {

	private static UpdateInfoParser instance;
	public static UpdateInfoParser getInstance(){
		if(null == instance){
			instance = new UpdateInfoParser();
		}
		return instance;
	}
	public UpdateInfo getUpdateInfo(InputStream is) throws Exception{
		UpdateInfo info = new UpdateInfo();
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "UTF-8");
		int type = parser.getEventType();
		while(type != XmlPullParser.END_DOCUMENT){
			switch(type){
			case XmlPullParser.START_TAG:
				if("version".equals(parser.getName())){
					info.setVersoin(parser.nextText());
				}
				if("url".equals(parser.getName())){
					info.setUrl(parser.nextText());
				}
				if("descriptoin".equals(parser.getName())){
					info.setDescription(parser.nextText());
				}
			}
			type = parser.next();
		}
		is.close();
		is = null;
		return info;
	}
}
