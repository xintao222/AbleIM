/**
 * 
 */
package com.ablesky.im.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;


/**
 * @author lhe E-mail: lhe@ablesky.com
 */
public class AbleskyImConfig {
	private static InputStream in = null;
	public static Properties config = new Properties();
	public static InitialContext initialContext = null;
	
	/**
	 * 配置信息
	 */
	// xmpp
	public static String XMPPSERVER_ADDR = "xmpp1.bt1.ablesky.com";
	public static int XMPPSERVER_PORT = 5222;
	public static String XMPPSERVER_SEARCH = "jud.xmpp1.test.ablesky.com";
	public static String XMPPSERVER_DOMAIN = "xmpp1.test.ablesky.com";
	public static String XMPPSERVER_SERVICENAME = "ablesky";
	public static String WEB_PREFIX_LOGIN_RESOURCE = "AS-Web";
	public static String WEB_NODE = "BP-Web0";
	public static String CLINET_PREFIX_LOGIN_RESOURCE = "ABLESKY-CLIENT";
	public static long CONNECT_XMPP_TIMEOUT = 300000;
	public static boolean ALLOW_CREATE_ACCOUNT_IN_XMPP = true;
	public static boolean ALLOW_CHAT = true;
	public static boolean XMPP_SWITCH = true;
	
	public static String DEFAULT_GROUP_PHOTO_MEDIUM = "/images/init/defaultGroupPicture-64-64.png";	
	public static String DEFAULT_GROUP_PHOTO_SMALL = "/images/init/defaultGroupPicture-32-32.png";
	// mail
	public static Boolean MAIL_SWITCH = true;
	public static String MAIL_HOST_URL = "mailin.ablesky.com";
	public static String MAIL_SMTP_AUTH = "false";
	public static String MAIL_SMTP_TIMEOUT = "25000";
	public static String EMAIL_ADMIN = "admin@ablesky.com";
	public static String EMAIL_SUPPORT = "support@ablesky.com";
	public static String EMAIL_CONVERTER = "converter@ablesky.com";
	public static String EMAIL_INVITE = "invite@ablesky.com";
	public static String EMAIL_REGISTER = "register@ablesky.com";
	public static String EMAIL_ACCOUNT = "account@ablesky.com";
	public static String EMAIL_NOTICE = "notice@ablesky.com";
	public static String EMAIL_TRANSACTION = "transaction@ablesky.com";
	public static String EMAIL_PAYMENT = "payment@ablesky.com";
	public static String ABLESKY_EMAIL_PERSONAL_EN = "Ablesky";
	public static String ABLESKY_EMAIL_PERSONAL_CN = "能力天空";

	public static List<String> STATICSERVER_PIC_LOCATION_LIST = Collections.emptyList();
	
	// ad
	public static String AD_HTTP_SERVER = "";
	public static String OUT_AD_HTTP_SERVER = "";
	// search
	public static String SEARCH_HTTP_SERVER = "";
	// ssl
	public static String HTTP_SERVER = "";
	public static String HTTPS_SERVER = "";
	// main ip
	public static String HTTPS_MAIN_SERVER = "";
	public static String ABLESKY_DOMAIN = "";
	// CMS
	public static String CMS_SERVER = "";

	// 北京静态文件服务器
	public static String STATICSERVER_LOCATION = "";
	
	public static String STATICSERVER_PIC_LOCATION = "";
	
	// file system
	public static boolean IS_STATICSERVER_ACTIVATED = false;

	// default photo
	public static String DEFAULT_ACCOUNT_PHOTO = "/images/init/defaultPerson.gif";
	
	public static String DEFAULT_GROUP_PHOTO = "/images/init/defaultGroupPicture.gif";
	
	public static String DEFAULT_GROUP_LOGO = "/images/init/DefaultOrgLogo.gif";
	
	public static String DEFAULT_LOCAL_ALBUM_PHOTO = "/images/init/defaultAlbum.gif";
	
	public static String DEFAULT_ALBUM_PHOTO = "/images/init/defaultAlbum.gif";

	// memcached server

	// 192.168.3.41:11211,192.168.3.42:11211,192.168.3.43:11211
	public static String MEMCACHED_SERVER = "";
	// 1,3,2
	public static String MEMCACHED_WEIGHT = "";
	public static String CONNECTION_POOLSIZE = "";
	public static String MAX_TRY_TIMES = "";	
	public static String TOKYO_TYRANT_SERVER = "";
	public static String TTSERVER_WEIGHT = "";
	public static boolean USE_NEW_PASSWORD_SYSTEM = true;
	
	public static String CLIENT_ONLINE_STATUS_RECORD_LOCATION = "";


	static {
		try {
			File file = new File(System.getProperty("catalina.home") + "/conf/AbleSkyImConfig.properties");
			if (file.exists()) {
				in = new FileInputStream(file);
			} else {
				/**
				 * use default config
				 */
				in = AbleskyImConfig.class.getClassLoader().getResourceAsStream("AbleSkyImConfig.properties");
			}
			config.load(in);

			// xmpp
			XMPPSERVER_ADDR = config.getProperty("XMPPSERVER_ADDR").trim();
			XMPPSERVER_DOMAIN = config.getProperty("XMPPSERVER_DOMAIN").trim();
			XMPPSERVER_SEARCH = config.getProperty("XMPPSERVER_SEARCH").trim();
			XMPPSERVER_PORT = Integer.parseInt(config.getProperty("XMPPSERVER_PORT").trim());
			XMPPSERVER_SERVICENAME = config.getProperty("XMPPSERVER_SERVICENAME").trim();
			WEB_PREFIX_LOGIN_RESOURCE = config.getProperty("WEB_PREFIX_LOGIN_RESOURCE").trim();
			WEB_NODE = config.getProperty("WEB_NODE").trim();
			CLINET_PREFIX_LOGIN_RESOURCE = config.getProperty("CLINET_PREFIX_LOGIN_RESOURCE").trim();
			CONNECT_XMPP_TIMEOUT = Long.parseLong(config.getProperty("CONNECT_XMPP_TIMEOUT").trim());
			ALLOW_CREATE_ACCOUNT_IN_XMPP = new Boolean(config.getProperty("ALLOW_CREATE_ACCOUNT_IN_XMPP").trim());
			ALLOW_CHAT = new Boolean(config.getProperty("ALLOW_CHAT"));
			XMPP_SWITCH = new Boolean(config.getProperty("XMPP_SWITCH"));

			USE_NEW_PASSWORD_SYSTEM = new Boolean(config.getProperty("USE_NEW_PASSWORD_SYSTEM"));

			MEMCACHED_SERVER = config.getProperty("MEMCACHED_SERVER").trim();
			MEMCACHED_WEIGHT = config.getProperty("MEMCACHED_WEIGHT").trim();
			CONNECTION_POOLSIZE = config.getProperty("CONNECTION_POOLSIZE").trim();
			MAX_TRY_TIMES = config.getProperty("MAX_TRY_TIMES").trim();
			TOKYO_TYRANT_SERVER = config.getProperty("TOKYO_TYRANT_SERVER").trim();
			TTSERVER_WEIGHT = config.getProperty("TTSERVER_WEIGHT").trim();

			// file system
			IS_STATICSERVER_ACTIVATED = new Boolean(config.getProperty("IS_STATICSERVER_ACTIVATED").trim());
			STATICSERVER_LOCATION = config.getProperty("STATICSERVER_LOCATION").trim();
			STATICSERVER_PIC_LOCATION = config.getProperty("STATICSERVER_PIC_LOCATION").trim();
			CLIENT_ONLINE_STATUS_RECORD_LOCATION = config.getProperty("CLIENT_ONLINE_STATUS_RECORD_LOCATION").trim();
			// default photo
			DEFAULT_ACCOUNT_PHOTO = config.getProperty("DEFAULT_ACCOUNT_PHOTO");
			DEFAULT_GROUP_PHOTO = config.getProperty("DEFAULT_GROUP_PHOTO");
			DEFAULT_LOCAL_ALBUM_PHOTO = config.getProperty("DEFAULT_LOCAL_ALBUM_PHOTO");
			DEFAULT_ALBUM_PHOTO = config.getProperty("DEFAULT_ALBUM_PHOTO");
			DEFAULT_GROUP_LOGO= config.getProperty("DEFAULT_GROUP_LOGO");
			
			HTTP_SERVER = config.getProperty("HTTP_SERVER").trim();
			HTTPS_SERVER = config.getProperty("HTTPS_SERVER").trim();
			SEARCH_HTTP_SERVER = config.getProperty("SEARCH_HTTP_SERVER").trim();
			AD_HTTP_SERVER = config.getProperty("AD_HTTP_SERVER").trim();
			OUT_AD_HTTP_SERVER = config.getProperty("OUT_AD_HTTP_SERVER").trim();
			HTTPS_MAIN_SERVER = config.getProperty("HTTPS_MAIN_SERVER").trim();
			CMS_SERVER= config.getProperty("CMS_SERVER").trim();
			
			ABLESKY_DOMAIN = StringUtil.getMainDomain(HTTPS_SERVER).trim();
			
			// mail
			MAIL_SWITCH = new Boolean(config.getProperty("MAIL_SWITCH"));
			MAIL_HOST_URL = config.getProperty("MAIL_HOST");
			MAIL_SMTP_AUTH = config.getProperty("MAIL_SMTP_AUTH");
			MAIL_SMTP_TIMEOUT = config.getProperty("MAIL_SMTP_TIMEOUT");
			EMAIL_ADMIN = config.getProperty("EMAIL_ADMIN").trim();
			EMAIL_SUPPORT = config.getProperty("EMAIL_SUPPORT").trim();
			EMAIL_CONVERTER = config.getProperty("EMAIL_CONVERTER").trim();
			EMAIL_INVITE = config.getProperty("EMAIL_INVITE").trim();
			EMAIL_REGISTER = config.getProperty("EMAIL_REGISTER").trim();
			EMAIL_ACCOUNT = config.getProperty("EMAIL_ACCOUNT").trim();
			EMAIL_NOTICE = config.getProperty("EMAIL_NOTICE").trim();
			EMAIL_TRANSACTION = config.getProperty("EMAIL_TRANSACTION").trim();
			EMAIL_PAYMENT = config.getProperty("EMAIL_PAYMENT").trim();

			ABLESKY_EMAIL_PERSONAL_EN = config.getProperty("ABLESKY_EMAIL_PERSONAL_EN").trim();
			ABLESKY_EMAIL_PERSONAL_CN = config.getProperty("ABLESKY_EMAIL_PERSONAL_CN").trim();
			
			// 静态服务器的hostname列表，供随机选取，若没有，则使用STATICSERVER_PIC_LOCATION
			String staticServerPicLocationListStr = config.getProperty("STATICSERVER_PIC_LOCATION_LIST").trim();
			if(!StringUtil.isNull(staticServerPicLocationListStr)){
				String[] locationArr = staticServerPicLocationListStr.split(",");
				List<String> locationList = new ArrayList<String>(locationArr.length);
				for(String loc: locationArr) {
					if(StringUtil.isNull(loc)) {
						continue;
					}
					locationList.add(loc.trim());
				}
				STATICSERVER_PIC_LOCATION_LIST = Collections.unmodifiableList(locationList);
			} else {
				STATICSERVER_PIC_LOCATION_LIST = Collections.unmodifiableList(Arrays.asList(new String[]{STATICSERVER_PIC_LOCATION}));
			}
			
			initialContext = new InitialContext();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
