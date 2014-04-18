//package com.ablesky.im.xmpp;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.jivesoftware.smackx.packet.DiscoverItems;
//
//import com.ablesky.im.cache.MemcachedNamespaceUtil;
//import com.ablesky.im.cache.XmemcachedUtil;
//import com.ablesky.im.util.AbleskyImConfig;
//
///**
// * @author lhe E-mail: lhe@ablesky.com
// */
//public class OnlineStatusCache {
//	private final static Log log = LogFactory.getLog(OnlineStatusCache.class.getName());
//
//	/**
//	 * username and sessionId mapping
//	 */
//	protected Map<String, String> userSessionIdMapping = new HashMap<String, String>();
//
//	private static OnlineStatusCache instance = null;
//
//	protected OnlineStatusCache() {
//	};
//
//	/**
//	 * 单例对象的初始化同步
//	 */
//	private static synchronized void syncInit() {
//		if (instance == null) {
//			instance = new OnlineStatusChecker();
//		}
//	}
//
//	public static OnlineStatusCache getInstance() {
//		if (instance == null) {
//			syncInit();
//		}
//		return instance;
//	}
//
//	public List<String> getListOfOnlineEntityID() {
//		XmemcachedUtil util = XmemcachedUtil.getInstance();
//		List<String> listOfOnlineEntityID = (List<String>) util.get(MemcachedNamespaceUtil.Online_Entity_ID_List);
//
//		return listOfOnlineEntityID;
//	}
//
//	public List<String> getListOfOnlineUsers() {
//		XmemcachedUtil util = XmemcachedUtil.getInstance();
//		List<String> listOfOnlineUsers = (List<String>) util.get(MemcachedNamespaceUtil.Online_User_List);
//
//		return listOfOnlineUsers;
//	}
//
//	/**
//	 * 清空xmpp 在线用户列表
//	 */
//	public void clear() {
//		XmemcachedUtil util = XmemcachedUtil.getInstance();
//		util.delete(MemcachedNamespaceUtil.Online_User_List);
//		util.delete(MemcachedNamespaceUtil.Online_Entity_ID_List);
//	}
//
//	/**
//	 * 同步来自于xmpp server。 listOfOnlineUsers listOfOnlineEntityID
//	 * 
//	 * @param discoItems
//	 */
//	@Deprecated
//	public int updateCache(DiscoverItems discoItems) {
//		clear();
//		if (discoItems != null) {
//			Iterator<DiscoverItems.Item> it = discoItems.getItems();
//			List<String> listOfOnlineUsers = new ArrayList<String>();
//			List<String> listOfOnlineEntityID = new ArrayList<String>();
//
//			if (it != null) {
//				DiscoverItems.Item item = null;
//				while (it.hasNext()) {
//					item = (DiscoverItems.Item) it.next();
//
//					listOfOnlineUsers.add(item.getName());
//					listOfOnlineEntityID.add(item.getEntityID());
//
//				}
//			}
//			XmemcachedUtil util = XmemcachedUtil.getInstance();
//			util.set(MemcachedNamespaceUtil.Online_User_List, listOfOnlineUsers, MemcachedNamespaceUtil.Online_User_List_Expiry);
//			util.set(MemcachedNamespaceUtil.Online_Entity_ID_List, listOfOnlineEntityID, MemcachedNamespaceUtil.Online_Entity_ID_List_Expiry);
//			return listOfOnlineUsers.size();
//		}
//		return 0;
//	}
//
//	/**
//	 * Query online user by username 首先查询与xmpp同步的在线用户列表，如果不存在则查询本地在线用户列表
//	 * 
//	 * @param username
//	 * @return
//	 */
//	public boolean queryOnlineUserByUsername(String username) {
//		if (username == null) {
//			return false;
//		}
//		String jid = username + "@" + AbleskyImConfig.XMPPSERVER_DOMAIN;
//		XmemcachedUtil util = XmemcachedUtil.getInstance();
//		List<String> listOfOnlineUsers = (List<String>) util.get(MemcachedNamespaceUtil.Online_User_List);
//		if(listOfOnlineUsers == null || listOfOnlineUsers.size() == 0){
//			return false;
//		}
//		if (listOfOnlineUsers.contains(jid.toLowerCase())) {
//			// online
//			return true;
//		} else {
//			// offline
//			return false;
//		}
//	}
//
//	/**
//	 * Batch query client online users by username
//	 * 
//	 * @param username
//	 * @return List
//	 */
//	public List<String> queryOnlineUserByListUsername(List<String> usernames) {
//		List<String> onlineUserNames = new ArrayList<String>();
//		if (usernames != null) {
//			XmemcachedUtil util = XmemcachedUtil.getInstance();
//			List<String> listOfOnlineUsers = (List<String>) util.get(MemcachedNamespaceUtil.Online_User_List);
//			for (int i = 0; i < usernames.size(); i++) {
//				String jid = usernames.get(i).toLowerCase() + "@" + AbleskyImConfig.XMPPSERVER_DOMAIN;
//				if (listOfOnlineUsers.contains(jid.toLowerCase())) {
//					onlineUserNames.add(usernames.get(i));
//				}
//			}
//		}
//		return onlineUserNames;
//
//	}
//
//	/**
//	 * Query client online user by username
//	 * 
//	 * @param username
//	 * @return
//	 */
//	public boolean queryClientOnlineUserByUsername(String username) {
//		if (username == null) {
//			return false;
//		}
//		String entityId = username.toLowerCase() + "@" + AbleskyImConfig.XMPPSERVER_DOMAIN + "/" + AbleskyImConfig.CLINET_PREFIX_LOGIN_RESOURCE;
//		XmemcachedUtil util = XmemcachedUtil.getInstance();
//		List<String> listOfOnlineEntityID = (List<String>) util.get(MemcachedNamespaceUtil.Online_Entity_ID_List);
//		if (listOfOnlineEntityID.contains(entityId)) {
//			log.debug(username + " is online");
//			return true;
//		} else {
//			log.debug(username + " is offline");
//			return false;
//		}
//	}
//
//	/**
//	 * Batch query client online users by username
//	 * 
//	 * @param username
//	 * @return List
//	 */
//	public List<String> queryClientOnlineUserByListUsername(List<String> usernames) {
//		List<String> onlineUserNames = new ArrayList<String>();
//		if (usernames != null) {
//			XmemcachedUtil util = XmemcachedUtil.getInstance();
//			List<String> listOfOnlineEntityID = (List<String>) util.get(MemcachedNamespaceUtil.Online_Entity_ID_List);
//			for (int i = 0; i < usernames.size(); i++) {
//				String entityId = usernames.get(i).toLowerCase() + "@" + AbleskyImConfig.XMPPSERVER_DOMAIN + "/" + AbleskyImConfig.CLINET_PREFIX_LOGIN_RESOURCE;
//				if (listOfOnlineEntityID.contains(entityId)) {
//					onlineUserNames.add(usernames.get(i));
//				}
//			}
//		}
//		return onlineUserNames;
//
//	}
//
//	/**
//	 * Remove website online user by username
//	 * 
//	 * @param username
//	 * @return
//	 */
//	public void removeWebsiteOnlineUserByUsername(String username) {
//		if (username == null) {
//			return;
//		}
//		String user = username.toLowerCase() + "@" + AbleskyImConfig.XMPPSERVER_DOMAIN + "/" + AbleskyImConfig.WEB_PREFIX_LOGIN_RESOURCE;
//		XmemcachedUtil util = XmemcachedUtil.getInstance();
//		List<String> listOfOnlineEntityID = (List<String>) util.get(MemcachedNamespaceUtil.Online_Entity_ID_List);
//		if(listOfOnlineEntityID == null || listOfOnlineEntityID.size() == 0){
//			return;
//		}
//		for (String entityId : listOfOnlineEntityID) {
//			if (entityId.indexOf(user) != -1) {
//				listOfOnlineEntityID.remove(entityId);
//				return;
//			}
//		}
//
//		String jid = username.toLowerCase() + "@" + AbleskyImConfig.XMPPSERVER_DOMAIN;
//		List<String> listOfOnlineUsers = (List<String>) util.get(MemcachedNamespaceUtil.Online_User_List);
//
//		for (String entityId : listOfOnlineUsers) {
//			if (entityId.indexOf(jid) != -1) {
//				listOfOnlineUsers.remove(jid);
//				return;
//			}
//		}
//	}
//
//	/**
//	 * Query website online user by username 首先查询本地在线用户列表，如果不存在再查询xmpp在线用户列表
//	 * 
//	 * @param username
//	 * @return
//	 */
//	public boolean queryWebsiteOnlineUserByUsername(String username) {
//		if (username == null) {
//			return false;
//		}
//		String queryUser = username.toLowerCase() + "@" + AbleskyImConfig.XMPPSERVER_DOMAIN + "/" + AbleskyImConfig.WEB_PREFIX_LOGIN_RESOURCE;
//
//		XmemcachedUtil util = XmemcachedUtil.getInstance();
//		List<String> listOfOnlineEntityID = (List<String>) util.get(MemcachedNamespaceUtil.Online_Entity_ID_List);
//
//		for (String entityId : listOfOnlineEntityID) {
//			if (entityId.indexOf(queryUser) != -1) {
//				log.debug(username + " is online");
//				return true;
//			}
//		}
//		log.debug(username + " is offline");
//		return false;
//	}
//
//	/**
//	 * Batch query website online users by username
//	 * 
//	 * @param username
//	 * @return List
//	 */
//	public List<String> queryWebsiteOnlineUserByListUsername(List<String> usernames) {
//		List<String> onlineUserNames = new ArrayList<String>();
//		if (usernames != null) {
//
//			XmemcachedUtil util = XmemcachedUtil.getInstance();
//			List<String> listOfOnlineEntityID = (List<String>) util.get(MemcachedNamespaceUtil.Online_Entity_ID_List);
//
//			for (int i = 0; i < usernames.size(); i++) {
//				String entityId = usernames.get(i).toLowerCase() + "@" + AbleskyImConfig.XMPPSERVER_DOMAIN + "/" + AbleskyImConfig.WEB_PREFIX_LOGIN_RESOURCE;
//				if (listOfOnlineEntityID.contains(entityId)) {
//					onlineUserNames.add(usernames.get(i));
//				}
//			}
//		}
//		return onlineUserNames;
//
//	}
//
//	/**
//	 * Get the number of client online user .
//	 * 
//	 * @return
//	 */
//	public int clientOnlineUserNumber() {
//		int count = 0;
//		List<String> onlineUserlist = getListOfOnlineEntityID();
//		for (int i = 0; i < onlineUserlist.size(); i++) {
//			if (onlineUserlist.get(i).indexOf(AbleskyImConfig.CLINET_PREFIX_LOGIN_RESOURCE) != -1) {
//				count++;
//			}
//		}
//		return count;
//	}
//
//	/**
//	 * Get the number of website online user .
//	 * 
//	 * @return
//	 */
//	public int websiteOnlineUserNumber() {
//		int count = 0;
//		XmemcachedUtil util = XmemcachedUtil.getInstance();
//		List<String> listOfOnlineEntityID = (List<String>) util.get(MemcachedNamespaceUtil.Online_Entity_ID_List);
//
//		for (int i = 0; i < listOfOnlineEntityID.size(); i++) {
//			if (listOfOnlineEntityID.get(i).indexOf(AbleskyImConfig.WEB_PREFIX_LOGIN_RESOURCE) != -1) {
//				count++;
//			}
//		}
//		return count;
//	}
//
//	/**
//	 * Get all online user
//	 * 
//	 * @return
//	 */
//	public List<String> getAllOnlineUser() {
//		XmemcachedUtil util = XmemcachedUtil.getInstance();
//		List<String> listOfOnlineEntityID = (List<String>) util.get(MemcachedNamespaceUtil.Online_Entity_ID_List);
//
//		return listOfOnlineEntityID;
//	}
//
//	/**
//	 * Get all online user
//	 * 
//	 * @return
//	 */
//	public List<String> getAllOnlineUserNames() {
//		List<String> onlineUserlist = getListOfOnlineUsers();
//		List<String> onlineUserNames = new ArrayList<String>();
//		for (String jid : onlineUserlist) {
//			String username = jid.substring(0, jid.indexOf("@"));
//			if (!onlineUserNames.contains(username)) {
//				onlineUserNames.add(username);
//			}
//		}
//		return onlineUserNames;
//	}
//
//	/**
//	 * Add UserSessionIdObject
//	 * 
//	 * @param username
//	 * @return
//	 */
//	public void addUserSessionIdObject(String sessionId, String username) {
//		if (username == null || username.equalsIgnoreCase("guest") || sessionId == null) {
//			return;
//		}
//		String value = userSessionIdMapping.get(sessionId);
//		// 如果同一session登录了不同的用户，则清除第一个用户登录信息
//		if (value != null && !value.equals(username)) {
//			userSessionIdMapping.remove(sessionId);
//			ClearUserStatus.clear(value);
//		}
//		userSessionIdMapping.put(sessionId, username);
//	}
//
//	/**
//	 * Remove UserSessionIdObject by username
//	 * 
//	 * @param username
//	 * @return
//	 */
//	public void removeUserSessionIdObject(String sessionId) {
//		if (sessionId == null) {
//			return;
//		}
//		if (userSessionIdMapping.containsKey(sessionId)) {
//			userSessionIdMapping.remove(sessionId);
//		}
//	}
//
//	/**
//	 * get username by sessionId
//	 * 
//	 * @param username
//	 * @return
//	 */
//	public String getUsernameBySessionId(String sessionId) {
//		if (sessionId == null) {
//			return null;
//		}
//		if (userSessionIdMapping.containsKey(sessionId)) {
//			return userSessionIdMapping.get(sessionId);
//		} else {
//			return null;
//		}
//	}
//
//	/**
//	 * check username in userSessionIdMapping
//	 * 
//	 * @param username
//	 * @return
//	 */
//	public boolean containsUsernameInUserSessionIdMapping(String username) {
//		if (username == null) {
//			return false;
//		}
//		return userSessionIdMapping.containsValue(username.toLowerCase());
//	}
//
//	public Map<String, String> getUserSessionIdMapping() {
//		return userSessionIdMapping;
//	}
//
//}
