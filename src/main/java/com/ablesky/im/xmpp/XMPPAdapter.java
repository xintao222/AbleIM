/**
 * 
 */
package com.ablesky.im.xmpp;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.VCard;

import com.ablesky.im.util.AbleskyImConfig;
import com.ablesky.im.util.CommonStaticConst;
import com.ablesky.im.util.WebUtil;

/**
 * @author lhe E-mail: lhe@ablesky.com
 */
public class XMPPAdapter {
	public static final Log log = LogFactory.getLog(XMPPAdapter.class);
	public static long CONNECT_XMPP_TIMEOUT = 300000;

	private XMPPManager xmppManager = null;

	// xmpp连接监听器，一定时间没有活动就断开连接
	// private Timer timer = null;

	public XMPPAdapter() {
		if (xmppManager == null) {
			xmppManager = new XMPPManager(AbleskyImConfig.XMPPSERVER_ADDR, AbleskyImConfig.XMPPSERVER_PORT, AbleskyImConfig.XMPPSERVER_DOMAIN);
		}
	}

	public XMPPManager getXmppManager() {
		return xmppManager;
	}

	public boolean isConnected() {
		return xmppManager != null && xmppManager.isConnected();
	}

	/**
	 * Add the specified buddy to the specified group
	 * 
	 * @param rosterEntryUserName
	 * @param rosterEntryGroup
	 * @throws XMPPException
	 */
	public void addRosterEntry(String rosterEntryUserName, String rosterEntryNick, String rosterEntryGroup) throws XMPPException {
		xmppManager.addRosterEntry(rosterEntryUserName, rosterEntryNick, rosterEntryGroup);
	}

	/**
	 * Change the users password
	 * 
	 * @param newPassword
	 * @throws XMPPException
	 */
	public void changePassword(String newPassword) throws XMPPException {
		xmppManager.changePassword(newPassword);
	}

	public void connect() throws XMPPException {
		if (xmppManager == null) {
			xmppManager = new XMPPManager(AbleskyImConfig.XMPPSERVER_ADDR, AbleskyImConfig.XMPPSERVER_PORT, AbleskyImConfig.XMPPSERVER_SERVICENAME);
		}
	}

	public void connect(String userName, String password) throws XMPPException {
		log.debug(userName + " logins in by password " + password);

		String resource = AbleskyImConfig.WEB_PREFIX_LOGIN_RESOURCE + "_" + AbleskyImConfig.WEB_NODE + "_" + new Date().getTime();

		log.debug(userName + " logins in by resource " + resource);
		login(userName, password, resource);
	}

	/**
	 * login
	 * 
	 * @param userName
	 * @param password
	 * @throws Exception
	 */
	public void login(String userName, String password) throws XMPPException {
		if (userName != null && password != null) {
			xmppManager.login(userName, password);
		}
	}

	/**
	 * Use a given resourse to login
	 * 
	 * @param userName
	 * @param password
	 * @param resource
	 * @throws XMPPException
	 */
	public void login(String userName, String password, String resource) throws XMPPException {
		if (userName != null && password != null && resource != null) {
			xmppManager.login(userName, password, resource);
		}
	}

	/**
	 * Try to create a new user account on the server
	 * 
	 * @param userName
	 * @param password
	 */
	public boolean createNewAccount(String userName, String password) {
		if (userName != null && password != null) {
			return xmppManager.createNewAccount(userName, password);
		}
		return false;
	}

	/**
	 * Deletes the currently logged-in account from the server
	 */
	public void deleteAccount() throws XMPPException {
		xmppManager.deleteAccount();
	}

	public void createAccountVCard(VCard vCard) throws XMPPException {
		xmppManager.createAccountVCard(vCard);
	}

	public VCard loadAccountVCard() throws Exception {
		return xmppManager.loadAccountVCard();
	}

	/**
	 * deletes a buddy
	 * 
	 * @param rosterEntryName
	 */
	public void delRosterEntry(String rosterEntryName) throws XMPPException {
		if (rosterEntryName != null) {
			xmppManager.delRosterEntry(rosterEntryName);
		}
	}

	/**
	 * Delete a rostergroup
	 * 
	 * @param rosterGroupName
	 */
	public void delRosterGroup(String rosterGroupName) {
		if (rosterGroupName != null) {
			xmppManager.delRosterGroup(rosterGroupName);
		}
	}

	/**
	 * Delete the users account
	 */
	public void deleteLoggedAccount() throws XMPPException {
		xmppManager.deleteLoggedAccount();
	}

	/**
	 * Disconnect the user from the xmpp server
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public void disconnect() {
		try {
			if (xmppManager != null) {
				if (xmppManager.getUser() != null) {
					log.info(xmppManager.getUser() + " disconnected");
				} else {
					log.info("xmpp disconnected");
				}
				xmppManager.disconnect();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * Return all incoming messages
	 * 
	 * @return
	 * @throws XMPPException
	 */
	// public List<XMPPChatMessage> getChatMessages() throws XMPPException {
	// timeTrigger("getChatMessages");
	// return xmppManager.getChatMessages();
	// }
	//
	// /**
	// * Return all incoming presences
	// *
	// * @return
	// * @throws XMPPException
	// */
	// public List<UserPresence> getPresences() throws XMPPException {
	// timeTrigger("getPresences");
	// return xmppManager.getPresences();
	// }
	/**
	 * Return the size of the rosterlist
	 * 
	 * @return
	 * @throws XMPPException
	 */
	public int getContactsCount() throws XMPPException {
		return xmppManager.getConnection().getRoster().getEntryCount();
	}

	/**
	 * Return the roster
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<JabberContact> getRoster() throws XMPPException {
		return xmppManager.getRoster();
	}

	/**
	 * Send a message to a buddy
	 * 
	 * @param messageBody
	 * @param messageSubject
	 * @param messageFrom
	 * @param messageTo
	 * @return
	 * @throws Exception
	 */
	public String sendChatMessage(String messageBody, String messageSubject, String messageFrom, String messageTo) throws XMPPException {
		XMPPChatMessage m = new XMPPChatMessage();
		m.setMessageBody(messageBody);
		m.setMessageFrom(messageFrom);
		m.setMessageSubject(messageSubject);
		m.setMessageTo(messageTo);
//		m.setFromScreenName(WebUtil.getScreenName(messageFrom.substring(0, messageFrom.indexOf('@'))));
		return xmppManager.sendChatMessage(m);
	}

	/**
	 * Set the users presence
	 * 
	 * @param presenceMode
	 * @param presenceMessage
	 * @throws XMPPException
	 */
	public void sendPresence(Presence.Mode presenceMode, String presenceMessage) throws XMPPException {
		Presence p = new Presence(Presence.Type.available);
		p.setMode(presenceMode);
		p.setStatus(presenceMessage);
		xmppManager.sendPresence(p);
	}

	/**
	 * Send a subscription type to a user
	 * 
	 * @param rosterEntryName
	 * @param type
	 * @throws XMPPException
	 */
	public void sendSubscribe(String rosterEntryName, Presence.Type type) throws XMPPException {
		Presence presence = new Presence(type);
		presence.setTo(rosterEntryName);
		xmppManager.sendSubscribe(presence);
	}

	/**
	 * Return the roster groups
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Collection getGroups() throws XMPPException {
		return xmppManager.getConnection().getRoster().getGroups();
	}

	/**
	 * returns the rostergroup matching the parameter
	 * 
	 * @param rosterGroupName
	 * @return RosterGroup
	 * @throws Exception
	 */
	public void addRosterGroup(String rosterGroupName) {
		xmppManager.addRosterGroup(rosterGroupName);
	}

	public RosterGroup getRosterGroup(String groupName) {
		return xmppManager.getRosterGroup(groupName);
	}

	/**
	 * Rename a rostergroup
	 * 
	 * @param rosterGroupName
	 * @param newName
	 * @throws XMPPException
	 */
	public void renameRosterGroup(String rosterGroupName, String newName) throws XMPPException {
		xmppManager.renameRosterGroup(rosterGroupName, newName);
	}

	/**
	 * Switche a user into another group
	 * 
	 * @param rosterEntryName
	 * @param newRosterGroupName
	 * @param oldRosterGroupName
	 * @throws XMPPException
	 */
	public void switchUserGroup(String rosterEntryName, String newRosterGroupName, String oldRosterGroupName) throws XMPPException {
		xmppManager.switchUserGroup(rosterEntryName, newRosterGroupName, oldRosterGroupName);
	}

	/**
	 * renames a buddy
	 * 
	 * @param rosterEntryName
	 * @param newName
	 * @return JameContact[]
	 * @throws Exception
	 */
	public void renameRosterEntry(String rosterEntryName, String newName) throws XMPPException {
		xmppManager.renameRosterEntry(rosterEntryName, newName);
	}

	public String getUser() {
		return xmppManager.getUser();
	}

	public boolean userIsExist(String username) throws XMPPException {
		return xmppManager.userIsExist(username);
	}

//	/**
//	 * for admin to query online users
//	 */
//	@Deprecated
//	public synchronized int queryOnlineStatus() {
//		String currentUser = xmppManager.getUser();
//		if (currentUser == null || currentUser.indexOf(CommonStaticConst.ADMIN + "@" + AbleskyImConfig.XMPPSERVER_DOMAIN) == -1) {
//			return -1;
//		}
//		XMPPConnection conn = xmppManager.getConnection();
//		int query = 0;
//		if (conn != null && conn.isConnected()) {
//			ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(conn);
//			DiscoverItems discoItems = null;
//			try {
//				discoItems = discoManager.discoverItems(AbleskyImConfig.XMPPSERVER_DOMAIN, "online users");
//			} catch (XMPPException e) {
//				query = -1;
//				e.printStackTrace();
//			}
//			query = OnlineStatusCache.getInstance().updateCache(discoItems);
//		} else {
//			query = -1;
//		}
//		return query;
//	}
}
