//package com.ablesky.im.xmpp;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.jivesoftware.smackx.packet.DiscoverItems;
//
//import com.ablesky.im.cache.MemcachedNamespaceUtil;
//import com.ablesky.im.cache.XmemcachedUtil;
//import com.ablesky.im.service.account.IOnlineStatusService;
//import com.ablesky.im.web.util.AbleskyImConfig;
//import com.ablesky.im.web.util.BeanFactoryUtil;
//import com.ablesky.im.web.util.StringUtil;
//
//public class OnlineStatusChecker extends OnlineStatusCache {
//	
//	private final static Log log = LogFactory.getLog(OnlineStatusChecker.class.getName());
//	
//	private IOnlineStatusService onlineStatusService;
//	
//	private IOnlineStatusService getOnlineStatusLazy(){
//		if(onlineStatusService == null) {
//			onlineStatusService = BeanFactoryUtil.getBean(IOnlineStatusService.class);
//		}
//		return onlineStatusService;
//	}
//	
//	/**
//	 * 要保证是单例呀
//	 */
//	protected OnlineStatusChecker(){}
//	
//	@Override
//	@Deprecated
//	public List<String> getListOfOnlineEntityID() {
//		return Collections.emptyList();
//	}
//
//	@Override
//	@Deprecated
//	public List<String> getListOfOnlineUsers() {
//		return Collections.emptyList();
//	}
//
//	/**
//	 * 清空xmpp 在线用户列表
//	 */
//	@Override
//	@Deprecated
//	public void clear() {
//		return;
//	}
//	
//	public void clearClientOnlineCache(){
//		XmemcachedUtil util = XmemcachedUtil.getInstance();
//		util.delete(MemcachedNamespaceUtil.Client_Online_User_MAP);
//		util.delete(MemcachedNamespaceUtil.Client_Online_Entity_ID_MAP);
//	}
//
//	/**
//	 * 同步来自于xmpp server。 listOfOnlineUsers listOfOnlineEntityID
//	 * 
//	 * @param discoItems
//	 */
//	@Deprecated
//	public int updateCache(DiscoverItems discoItems) {
//		return 0;
//	}
//	
//	/**
//	 * 只同步客户端在线用户信息
//	 */
//	public int updateClientOnlineCache() {
//		clearClientOnlineCache();
//		
//		File file = null;
//		if(StringUtil.isNull(AbleskyImConfig.CLIENT_ONLINE_STATUS_RECORD_LOCATION) 
//				||! (file = new File(AbleskyImConfig.CLIENT_ONLINE_STATUS_RECORD_LOCATION)).exists()) {
//			log.error("clientOnlineStatus record file does not exist! please check the corresponding scheduled task on the xmpp server!");
//			return 0;
//		}
//		
//		Map<String, String> clientOnlineUserMap = new HashMap<String, String>();
//		Map<String, String> clientOnlineEntityIdMap = new HashMap<String, String>();
//		
//		BufferedReader reader = null;
//		try {
//			reader = new BufferedReader(new FileReader(file));
//			String line = "";
//			while((line = reader.readLine()) != null) {
//				int usernameEndPos = line.indexOf("@");
//				int bareJidEndPos = line.indexOf("/");
//				if(usernameEndPos == -1) {
//					continue;
//				}
//				String username = line.substring(0, usernameEndPos);
//				String bareJid = bareJidEndPos == -1? line: line.substring(0, bareJidEndPos);
//				String entireJid = line;
//				clientOnlineUserMap.put(username, bareJid);
//				clientOnlineEntityIdMap.put(username, entireJid);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if(reader != null) {
//				try {
//					reader.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				reader = null;
//			}
//		}
//		
//		XmemcachedUtil util = XmemcachedUtil.getInstance();
//		util.set(MemcachedNamespaceUtil.Client_Online_User_MAP, clientOnlineUserMap, MemcachedNamespaceUtil.Client_Online_User_MAP_Expiry);
//		util.set(MemcachedNamespaceUtil.Client_Online_Entity_ID_MAP, clientOnlineEntityIdMap, MemcachedNamespaceUtil.Client_Online_Entity_ID_MAP_Expiry);
//		
//		return clientOnlineUserMap.size();
//	}
//	
//
//	/**
//	 * Query online user by username 首先查询与xmpp同步的在线用户列表，如果不存在则查询本地在线用户列表
//	 * 
//	 * @param username
//	 * @return
//	 */
//	@Override
//	public boolean queryOnlineUserByUsername(String username) {
//		if (StringUtil.isNull(username)) {
//			return false;
//		}
//		return queryWebsiteOnlineUserByUsername(username) || queryClientOnlineUserByUsername(username);
//	}
//
//	/**
//	 * Batch query client online users by username
//	 * 
//	 * @param username
//	 * @return List
//	 */
//	@Override
//	public List<String> queryOnlineUserByListUsername(List<String> usernames) {
//		if(usernames == null || usernames.size() == 0) {
//			return Collections.emptyList();
//		}
//		List<String> onlineUsernameList = queryWebsiteOnlineUserByListUsername(usernames);
//		onlineUsernameList.addAll(queryClientOnlineUserByListUsername(usernames));
//		return onlineUsernameList;
//	}
//
//	/**
//	 * Query client online user by username
//	 * 
//	 * @param username
//	 * @return
//	 */
//	@Override
//	public boolean queryClientOnlineUserByUsername(String username) {
//		if(StringUtil.isNull(username)){
//			return false;
//		}
//		return getClientOnlineUserMapFromMemcached().containsKey(username.toLowerCase());
//	}
//
//	/**
//	 * Batch query client online users by username
//	 * @param username
//	 * @return List
//	 */
//	@Override
//	public List<String> queryClientOnlineUserByListUsername(List<String> usernames) {
//		if(usernames == null || usernames.size() == 0) {
//			return Collections.emptyList();
//		}
//		Map<String, String> clientOnlineUserMap = getClientOnlineUserMapFromMemcached();
//		List<String> clientOnlineUserNames = new ArrayList<String>();
//		for(String username: usernames) {
//			if(clientOnlineUserMap.containsKey(username.toLowerCase())){
//				clientOnlineUserNames.add(username);
//			}
//		}
//		return clientOnlineUserNames;
//	}
//
//	/**
//	 * Remove website online user by username
//	 * @param username
//	 * @return
//	 */
//	@Override
//	@Deprecated
//	public void removeWebsiteOnlineUserByUsername(String username) {
//		return;
//	}
//
//	/**
//	 * Query website online user by username 首先查询本地在线用户列表，如果不存在再查询xmpp在线用户列表
//	 * 
//	 * @param username
//	 * @return
//	 */
//	@Override
//	public boolean queryWebsiteOnlineUserByUsername(String username) {
//		if (StringUtil.isNull(username)) {
//			return false;
//		}
//		return getOnlineStatusLazy().isUserOnline(username);
//	}
//
//	/**
//	 * Batch query website online users by username
//	 * 
//	 * @param username
//	 * @return List
//	 */
//	@Override
//	public List<String> queryWebsiteOnlineUserByListUsername(List<String> usernames) {
//		if(usernames == null || usernames.size() == 0) {
//			return Collections.emptyList();
//		}
//		return getOnlineStatusLazy().queryOnlineUserByListUsername(usernames);
//
//	}
//
//	/**
//	 * Get the number of client online user .
//	 * 
//	 * @return
//	 */
//	@Override
//	public int clientOnlineUserNumber() {
//		return getClientOnlineUserMapFromMemcached().size();
//	}
//
//	/**
//	 * Get the number of website online user .
//	 */
//	@Override
//	public int websiteOnlineUserNumber() {
//		return getOnlineStatusLazy().getOnlineUserNumber();
//	}
//
//	/**
//	 * Get all online user
//	 * 本应返回的是entityId，但webOnline这边无法返回这种数据
//	 */
//	@Override
//	@Deprecated
//	public List<String> getAllOnlineUser() {
//		return getAllOnlineUserNames();
//	}
//
//	/**
//	 * Get all online user
//	 */
//	public List<String> getAllOnlineUserNames() {
//		List<String> resultList = new ArrayList<String>();
//		resultList.addAll(getAllWebsiteOnlineUserNames());
//		resultList.addAll(getAllClientOnlineUserNames());
//		return resultList;
//	}
//	
//	public List<String> getAllWebsiteOnlineUserNames() {
//		return getOnlineStatusLazy().getAllOnlineUser();
//	}
//	
//	public List<String> getAllClientOnlineUserNames(){
//		return new ArrayList<String>(getClientOnlineUserMapFromMemcached().keySet());
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
//	private Map<String, String> getClientOnlineUserMapFromMemcached(){
//		XmemcachedUtil util = XmemcachedUtil.getInstance();
//		Map<String, String> map = (Map<String, String>) util.get(MemcachedNamespaceUtil.Client_Online_User_MAP);
//		return map == null? Collections.<String, String>emptyMap(): map;
//	}
//}
