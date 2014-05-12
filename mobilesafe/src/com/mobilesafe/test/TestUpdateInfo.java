package com.mobilesafe.test;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.mobilesafe.bean.UpdateInfo;
import com.mobilesafe.util.UpdateInfoParser;

import android.test.AndroidTestCase;

public class TestUpdateInfo extends AndroidTestCase {

	public void testGetUpdateInfo()throws Exception{
		
		String paht = "http://192.168.1.101:8080/update.xml";
		URL url = new URL(paht);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		InputStream is = conn.getInputStream();
		UpdateInfo info = UpdateInfoParser.getInstance().getUpdateInfo(is);
		assertEquals("2.0", info.getVersoin());
	}
}
