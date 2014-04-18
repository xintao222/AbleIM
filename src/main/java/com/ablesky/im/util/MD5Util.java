package com.ablesky.im.util;

import java.security.MessageDigest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author lhe E-mail:lhe@ablesky.com
 */
public class MD5Util {
	private final static Log log = LogFactory.getLog(MD5Util.class.getName());

	/**
	 * Use MD5 to encoding message.
	 * 
	 * @param message
	 * @return
	 */
	public static String getMD5(String message) {
		String md5 = "";
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] array = messageDigest.digest(message.getBytes("utf-8"));
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; i++) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).toLowerCase().substring(1, 3));
			}
			md5 = sb.toString();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return md5;
	}

	public static void main(String[] args) {
		log.info("asdf");
		String src = "childhua@126.com";
		String md = getMD5("d8806804830d2e3ec65f76c1812f86ae"+1281608532);
		System.out.println(getMD5("1234"));
//		System.out.println(md);
//		Date time = new Date(1281608532000l);
//		System.out.println(DateUtil.deSerialize(time, null));
//		System.out.println(DateUtil.deSerialize(new Date(0), null));
		//System.out.println("md5:"+md+",length="+md.length());
		 //System.out.println(getMD5("support1"));
		// System.out.println((Math.random()*(10)));
		//System.out.println(new Date(0).toLocaleString());
//		String a = "ab_c";
//		System.out.println(a.substring(0, a.indexOf("_")));

	
//		Float f = 0.3f;
//		double d = Double.parseDouble(f.toString());
//		int num = 15;
//		System.out.println(new Double((Math.random()*4)).intValue());	
//		
//		System.out.println(Integer.rotateLeft(num, new Integer(num).toString().length()));
//		
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(new Date());
//		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
//		
//		System.out.println(DateUtil.deSerialize(calendar.getTime(), null));
		
//		int x=4,y=25,z=2;
//		System.out.println(y--);
//		System.out.println(y);
//		System.out.println(++x);
//		System.out.println(y/x);
//		z=(y/x)*z--;
//		System.out.println(z);
	}
}