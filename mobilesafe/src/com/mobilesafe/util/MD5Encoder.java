package com.mobilesafe.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encoder {

	
	private static MD5Encoder instance;
	public static MD5Encoder getInstance(){
		if(null == instance){
			instance = new MD5Encoder();
		}
		return instance;
	}
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F' };
	
	public String toHexString(byte[] b){
		StringBuilder sb = new StringBuilder();
		for(byte obj : b){
			sb.append(HEX_DIGITS[(obj & 0xf0) >>> 4 ]);
			sb.append(HEX_DIGITS[obj & 0x0f]);
		}
		return sb.toString();
	}
	public String encode(String message) throws NoSuchAlgorithmException{
		MessageDigest md5 =  MessageDigest.getInstance("MD5") ;
		byte [] result = md5.digest(message.getBytes());
		return toHexString(result);
		
	}
}
