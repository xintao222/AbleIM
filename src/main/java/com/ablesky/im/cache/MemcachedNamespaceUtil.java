package com.ablesky.im.cache;

/**
 * memcached 命名空间 key 1.普通的key：{model}_{object}
 * 2.需要国际化的key：{model}_{object}_Locale expiry(单位毫秒) {key}_Expiry
 */
public class MemcachedNamespaceUtil {
	/**
	 * 高并发环境下，考虑事务特性以及并发冲突。 中低并发环境下，适度考虑事务特性以及并发冲突。
	 */
	/**
	 * TODO 对于memcached协议而言，单条记录不能超过1M的上限，对于理论上可以无限扩容的用户数据，需要考虑可伸缩性
	 */

	// -------------------------------------并发登录不受影响-------------------------------------
	// 好友请求 
	public static String Buddy_Request_NO = "Buddy_Request_NO_";
	public static long Buddy_Request_NO_Expiry = 0;

	public static String Buddy_Request = "Buddy_Request_";
	public static long Buddy_Request_Expiry = 0;

	// 线上用户 上限不能超过1M，存在同时在线人数上线，需要测试验证 TODO 测试集群式水平切分
	public static final String Online_User_List = "Online_User_List_";
	public static final long Online_User_List_Expiry = 24 * 3600 * 1000;
	
	// 只针对客户端用户
	public static final String Client_Online_User_MAP = "Client_Online_User_MAP_";
	public static final long Client_Online_User_MAP_Expiry = 24 * 3600 * 1000;

	public static final String Online_Entity_ID_List = "Online_Entity_ID_List_";
	public static final long Online_Entity_ID_List_Expiry = 24 * 3600 * 1000;
	
	// 只针对客户端用户
	public static final String Client_Online_Entity_ID_MAP = "Client_Online_Entity_ID_List_";
	public static final long Client_Online_Entity_ID_MAP_Expiry = 24 * 3600 * 1000;

	// 黑名单机制
	public static final String Blocked_User_List = "Blocked_User_List_";// 使用TTserver
	public static final long Blocked_User_List_Expiry = 0;
	
	// 用户图片缓存机制
	public static final String Account_Photo_Url = "Account_Photo_Url_";
	public static final long Account_Photo_Url_Expiry = 15 * 60 * 1000; // 15分钟
	
	// 消息通知接收者,10-15分钟内只能收到1条
	public static final String NOTIFY_RECEIVER = "Notify_Receiver_";
	public static long getNotifyReceiverExpiry(){
		return (int)(10 + 5 * Math.random()) * 60 * 1000; // 10-15分钟的随机数
	}
	
	// 消息通知,有效期1天
	public static final String NOTIFY_TYPE = "Notify_Type_";
	public static final long NOTIFY_TYPE_EXPIRY = 24 * 60 * 60 * 1000; // 1天
	
	//群发消息 淘宝充课卡 禁止机构
	public static final String NOTIFY_FORBID_ORGANIZATIONID = "Notify_Forbid_OrganizationId";
	public static final long NOTIFY_FORBID_ORGANIZATIONID_EXPIRY = 24 * 60 * 60 * 1000; // 1天
	
	//个人首页推荐好友
	public static String RECOMMEND_BUDDYS = "RECOMMEND_BUDDYS_";
	public static long RECOMMEND_BUDDYS_EXPIRY = 0;//1MONTH
}
