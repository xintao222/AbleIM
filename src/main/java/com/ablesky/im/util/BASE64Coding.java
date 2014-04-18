package com.ablesky.im.util;

import java.io.IOException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class BASE64Coding {
	private static BASE64Encoder encoder = new BASE64Encoder();
	private static BASE64Decoder decoder = new BASE64Decoder();

	public BASE64Coding() {
	}

	public static String encode(String s) {
		return encoder.encode(s.getBytes());
	}

	public static String decode(String s) {
		try {
			byte[] temp = decoder.decodeBuffer(s);
			return new String(temp);
		} catch (IOException ioe) {
			// handler exception here
		}
		return null;
	}

	public static void main(String[] args) {
//		Date date = new Date();
//		String aString = "京师考研2011年西医综合冲刺班生化2";
////		try {
////			System.out.println(URLEncoder.encode(aString, "UTF-8"));
////		} catch (UnsupportedEncodingException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//		aString = BASE64Coding.encode(aString);
////		System.out.println(aString);
//		try {
//			System.out.println(URLEncoder.encode(aString, "UTF-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(BASE64Coding.decode(aString));
//
//		// System.out.println(BASE64Coding.decode("E105E222F89BEB19B7CD6798DE7C001C6D562B8416BA6129264C1EC81AD24049"));
		
		
		String host = "www.ablesky-a.com";
		String cookieDomain = host;
		if(host.lastIndexOf(".") != -1){			
			cookieDomain = host.substring(host.lastIndexOf("."));
			host = host.substring(0, host.lastIndexOf("."));
			if(host.lastIndexOf(".") != -1){
				cookieDomain = host.substring(host.lastIndexOf(".")) + cookieDomain;
				
			}	
		}		
		System.out.println(cookieDomain);
	}
}