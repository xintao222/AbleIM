/**
 * 
 */
package com.ablesky.im.xmpp;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.XMPPException;

//import com.ablesky.im.pojo.Account;
//import com.ablesky.im.pojo.SimpleAccount;
import com.ablesky.im.util.AbleskyImConfig;
import com.ablesky.im.util.SearchUser;
import com.ablesky.im.util.StringUtil;

/**
 * @author lhe E-mail: lhe@ablesky.com
 */
public class XMPPCacheManager {
	private static Map<String, XMPPAdapter> xmppAdapterCache = new ConcurrentHashMap<String, XMPPAdapter>();
	private static XMPPCacheManager fgSingleton = new XMPPCacheManager();
	private final static Log log = LogFactory.getLog(XMPPCacheManager.class.getName());

	public static XMPPCacheManager getInstance() {
		return fgSingleton;
	}

	/**
	 * @return the xmppAdapterCache
	 */
	public Map<String, XMPPAdapter> getXmppAdapterCache() {
		return xmppAdapterCache;
	}

	public void removeXmppAdapter(String key) throws Exception {
		if (StringUtil.isNull(key)) {
			return;
		}
		key = key.toLowerCase();
		if (xmppAdapterCache.containsKey(key)) {
			XMPPAdapter adapter = xmppAdapterCache.remove(key);
			adapter.disconnect();
			// xmppAdapterCache.remove(key);
		}
	}

//	public XMPPAdapter getXmppAdapter(SimpleAccount account) {
//		if (!AbleskyImConfig.XMPP_SWITCH || account == null) {
//			return null;
//		}
//		return this.getXmppAdapter(account.getUsername().toLowerCase(), account.getPassword());
//	}

	public XMPPAdapter getXmppAdapter(String username, String password) {
		String flag = UUID.randomUUID().toString();

		XMPPAdapter adapter = null;
		username = username.toLowerCase();
		if (xmppAdapterCache.containsKey(username)) {
			adapter = xmppAdapterCache.get(username);
		}
		if (adapter != null) {
			if (adapter.isConnected()) {
				return adapter;
			}
		}
		return this.loginXmppServer(username, password, flag);
	}

	/**
	 * 登录xmpp 服务器 保证同一用户只能登录一次
	 * 
	 * @param username
	 * @param password
	 * @param flag
	 * @return
	 */
	public synchronized XMPPAdapter loginXmppServer(String username, String password, String flag) {
		if (!AbleskyImConfig.XMPP_SWITCH) {
			return null;
		}
		if (username == null) {
			return null;
		}
		log.debug(username + " logins in xmpp by flag " + flag);
		boolean loginXmpp = true;
		XMPPAdapter adapter = null;
		username = username.toLowerCase();
		if (xmppAdapterCache.containsKey(username)) {
			adapter = xmppAdapterCache.get(username);
		}
		if (adapter != null) {
			if (!adapter.isConnected()) {
				try {
					log.debug(username + " logins in by adapter.reconnect start ");
					adapter.connect(username, password);
					log.debug(username + " logins in by adapter.reconnect start ");
					if (!adapter.isConnected()) {
						removeXmppAdapter(username);
						adapter = new XMPPAdapter();
						adapter.connect(username, password);
						xmppAdapterCache.put(username, adapter);
					}
				} catch (Exception e) {
					log.error(username + " failed to connect xmpp");
					loginXmpp = false;
				}
			}
		} else {
			adapter = new XMPPAdapter();
			try {
				log.debug(username + " logins in by adapter.connect start ");
				adapter.connect(username, password);
				log.debug(username + " logins in by adapter.connect end");
			} catch (XMPPException e) {
				loginXmpp = false;
				log.error(username + " failed to connect xmpp");
				// e.printStackTrace();
			}
			xmppAdapterCache.put(username, adapter);
		}

		return loginXmpp == true ? adapter : null;
	}

//	public void sendMessageByAdmin(String msg, Account receiver) throws XMPPException {
//		SimpleAccount admin = SearchUser.findAccountByName("shoutboxAdmin");
//		if (!AbleskyImConfig.XMPP_SWITCH || admin == null) {
//			return;
//		}
//		XMPPAdapter adapter = XMPPCacheManager.getInstance().getXmppAdapter(admin);
//		if (adapter == null) {
//			adapter = new XMPPAdapter();
//			adapter.connect(admin.getUsername(), admin.getPassword());
//			xmppAdapterCache.put(admin.getUsername(), adapter);
//		}
//		if (!adapter.isConnected()) {
//			adapter.connect(admin.getUsername(), admin.getPassword());
//		}
//		adapter.sendChatMessage(msg, "", admin.getUsername() + "@" + AbleskyImConfig.XMPPSERVER_DOMAIN, receiver.getUsername() + "@" + AbleskyImConfig.XMPPSERVER_DOMAIN);
//
//	}
	
	public boolean contains(String username){
		if(username == null && "".equals(username)){
			return false;
		}
		return xmppAdapterCache.containsKey(username.toLowerCase());
	}

	public static void main(String[] arg) {
		XMPPAdapter adapter = new XMPPAdapter();
		try {
			adapter.connect("admin", "admin");
			adapter.disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
