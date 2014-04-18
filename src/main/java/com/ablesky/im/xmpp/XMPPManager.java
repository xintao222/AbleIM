package com.ablesky.im.xmpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.search.UserSearchManager;

import com.ablesky.im.util.AbleskyImConfig;

/**
 * @author lhe E-mail: lhe@ablesky.com
 */
public class XMPPManager implements PacketListener, RosterListener {
	public static final Log log = LogFactory.getLog(XMPPManager.class);
	private XMPPConnection conn = null;

	private boolean connectionClosed = false;

	public static final String ROSTERENTRYGROUP = "好友";

	/**
	 * @param serverName
	 * @param serverPort
	 * @param serviceName
	 * @throws Exception
	 */
	public XMPPManager(String serverName, int serverPort, String serviceName) {
		ConnectionConfiguration connConfig = new ConnectionConfiguration(serverName.trim(), serverPort, serviceName.trim());
		this.conn = new XMPPConnection(connConfig);
		try {
			this.conn.connect();
			this.conn.addPacketListener(this, new PacketFilter() {
				public boolean accept(Packet p) {
					return true;
				}
			});
		} catch (Exception e) {
			log.error("Fail to connect XMPP Server!");
		}
	}

	public void connect() {
		if (this.conn != null && !this.conn.isConnected()) {
			try {
				this.conn.connect();
			} catch (XMPPException e) {
				log.error("Fail to connect XMPP Server!");
			}
		}
	}

	/**
	 * connection established?
	 * 
	 * @return boolean
	 */
	public boolean isConnected() {
		if (this.conn != null && this.conn.isConnected() && this.conn.isAuthenticated())
			return true;
		else
			return false;
	}

	/**
	 * 
	 */
	public synchronized void disconnect() {
		if (!isClosed()) {
			this.conn.disconnect();
			this.connectionClosed = true;
		}
	}

	/**
	 * @return
	 */
	public boolean isClosed() {
		return this.connectionClosed;
	}

	/**
	 * @return
	 */
	public XMPPConnection getConnection() {
		return this.conn;
	}

	public void setRosterListener() {
		if (this.isConnected()) {
			getConnection().getRoster().addRosterListener(this);
		}
	}

	/**
	 * Adds the specified buddy to the specified group
	 * 
	 * @param rosterEntryName
	 * @param rosterEntryGroup
	 * @throws Exception
	 */
	public void addRosterEntry(String rosterEntryName, String rosterEntryNick, String rosterEntryGroup) throws XMPPException {
		if (this.isConnected()) {
			String[] group = new String[1];
			group[0] = rosterEntryGroup;
			getConnection().getRoster().createEntry(rosterEntryName, rosterEntryNick, group);
		}
	}

	/**
	 * Adds a group to the roster
	 * 
	 * @param rosterGroupName
	 * @return RosterGroup
	 */
	public void addRosterGroup(String rosterGroupName) {
		if (this.isConnected()) {
			getConnection().getRoster().createGroup(rosterGroupName);
		}
	}

	public RosterGroup getRosterGroup(String groupName) {
		RosterGroup rosterGroup = null;
		if (this.isConnected()) {
			rosterGroup = getConnection().getRoster().getGroup(groupName);
		}
		return rosterGroup;
	}

	/**
	 * Removes a buddy from the roster
	 * 
	 * @param rosterEntryUserName
	 */
	public void delRosterEntry(String rosterEntryUserName) throws XMPPException {
		if (this.isConnected()) {
			RosterEntry rosterEntry = getConnection().getRoster().getEntry(rosterEntryUserName);
			if (rosterEntry != null) {
				getConnection().getRoster().removeEntry(rosterEntry);
			}
		}
	}

	/**
	 * Deletes a rostergroup
	 * 
	 * @param rosterGroupName
	 */
	@SuppressWarnings("unchecked")
	public void delRosterGroup(String rosterGroupName) {
		if (this.isConnected()) {
			Iterator groupIterator = getConnection().getRoster().getGroups().iterator();
			if (groupIterator != null) {
				while (groupIterator.hasNext()) {
					RosterGroup rg = (RosterGroup) groupIterator.next();
					if (rg.getName() == rosterGroupName)
						groupIterator.remove();
				}
			}
		}
	}

	/**
	 * Renames a buddy
	 * 
	 * @param rosterEntryName
	 * @param newName
	 */
	public void renameRosterEntry(String rosterEntryName, String newName) {
		if (this.isConnected()) {
			RosterEntry rosterEntry = getConnection().getRoster().getEntry(rosterEntryName);
			if (rosterEntry != null) {
				rosterEntry.setName(newName);
			}
		}
	}

	/**
	 * Rename a rostergroup
	 * 
	 * @param rosterGroupName
	 * @param newName
	 */
	public void renameRosterGroup(String rosterGroupName, String newName) {
		if (this.isConnected()) {
			RosterGroup rosterGroup = getConnection().getRoster().getGroup(rosterGroupName);
			if (rosterGroup != null) {
				rosterGroup.setName(newName);
			}
		}
	}

	/**
	 * Switches a buddy into another group
	 * 
	 * @param rosterEntryName
	 * @param newRosterGroupName
	 * @param oldRosterGroupName
	 */
	public void switchUserGroup(String rosterEntryName, String newRosterGroupName, String oldRosterGroupName) {
		if (this.isConnected()) {
			RosterEntry rosterEntry = getConnection().getRoster().getEntry(rosterEntryName);
			RosterGroup ng = getConnection().getRoster().getGroup(newRosterGroupName);
			RosterGroup og = getConnection().getRoster().getGroup(oldRosterGroupName);
			if (rosterEntry == null || ng == null || og == null) {
				return;
			}
			if (ng.getClass() != RosterGroup.class) {
				addRosterGroup(newRosterGroupName);
				ng = getConnection().getRoster().getGroup(newRosterGroupName);
			}
			try {
				ng.addEntry(rosterEntry);
				og.removeEntry(rosterEntry);
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Send a message to a buddy
	 * 
	 * @param message
	 * @return
	 * @throws XMPPException
	 */
	public String sendChatMessage(XMPPChatMessage message) throws XMPPException {
		Message m = new Message();
		if (this.isConnected()) {
			m.setTo(message.getMessageTo());
			m.setBody(message.getMessageBody());
			m.setSubject(message.getMessageSubject());
			m.setType(Message.Type.chat);
			getConnection().sendPacket(m);
		}
		return m.getPacketID();
	}

	/**
	 * Set the user's presence
	 * 
	 * @param p
	 * @throws XMPPException
	 */
	public void sendPresence(Presence p) throws XMPPException {
		if (this.isConnected()) {
			getConnection().sendPacket(p);
		}
	}

	/**
	 * sends a subscription type to a user
	 * 
	 * @see org.jivesoftware.smack.packet.Presence
	 * @param UserPresence
	 * @return
	 * @throws Exception
	 */
	public void sendSubscribe(Presence presence) throws XMPPException {
		if (this.isConnected()) {
			presence.setFrom(getConnection().getUser());
			getConnection().sendPacket(presence);
			if (presence.getType() == Presence.Type.subscribed) {
				Presence t = new Presence(Presence.Type.available);
				t.setTo(presence.getTo());
				t.setFrom(getConnection().getUser());
				getConnection().sendPacket(t);
			}
		}
	}

	/**
	 * Incoming packets
	 * 
	 * @see PacketListener
	 */
	@SuppressWarnings("unchecked")
	public synchronized void processPacket(Packet packet) {
		if (packet.getClass() == Message.class) { // incoming message
			Message msg = (Message) packet;
			if (msg.getBody() != null) {
				String to = msg.getTo().substring(0, msg.getTo().indexOf("@"));
				String from = msg.getFrom().substring(0, msg.getFrom().indexOf("@"));
				String fromRescourse = msg.getFrom().substring(msg.getFrom().indexOf("/"));
				XMPPMessageManager.getInstance().addRecvedMessageToCenter(new XMPPChatMessage(msg.getBody(), msg.getSubject(), from, fromRescourse, to, System.currentTimeMillis(), msg.getPacketID()));
				log.info(from + " chat to " + to);
			}
		} else if (packet.getClass() == Presence.class) {
//			Presence p = (Presence) packet;
//			if (p.getType() == Presence.Type.subscribe) {
//				/**
//				 * Buddy request
//				 */
//				XMPPPresenceManager.getInstance().addPresenceToCenter(p);
//				log.debug(p.getFrom() + " is " + p.getType().name() + " to " + p.getTo());
//			} else if (p.getType() == Presence.Type.subscribed) {
//				/**
//				 * Receive buddy request
//				 */
//				XMPPPresenceManager.getInstance().deletePresenceFromCenterForReceive(p);
//				log.debug(p.getFrom() + " is " + p.getType().name() + " to " + p.getTo());
//			} else if (p.getType() == Presence.Type.unsubscribed) {// some one
//				/**
//				 * Reject buddy request
//				 */
//				XMPPPresenceManager.getInstance().deletePresenceFromCenterForReject(p);
//				log.debug(p.getFrom() + " is " + p.getType().name() + " to " + p.getTo());
//			} else if (p.getType() == Presence.Type.available) {
//				XMPPPresenceManager.getInstance().saveOrUpdatePresenceCenterForStatusChange(p, System.currentTimeMillis());
//				log.debug(p.getFrom() + " is " + p.getType().name() + " and " + p.getStatus() + " to " + p.getTo());
//			} else if (p.getType() == Presence.Type.unavailable) {
//				XMPPPresenceManager.getInstance().saveOrUpdatePresenceCenterForStatusChange(p, System.currentTimeMillis());
//				log.debug(p.getFrom() + " " + p.getType().name() + " " + p.getTo());
//			}
		} else {
			// System.out.println("others:-------->>>>>"+packet.toString());
		}
		// System.out.println("others:-------->>>>>"+packet.toXML());
	}

	/**
	 * @see RosterListener is called if a buddy changes its presence
	 */
	@SuppressWarnings("unchecked")
	public void presenceChanged(Presence p) {

	}

	/**
	 * @see RosterListener is called if a buddy is updated
	 */
	@SuppressWarnings("unchecked")
	public void entriesUpdated(Collection addresses) {
	}

	/**
	 * @see RosterListener is called if a buddy is deleted
	 */
	@SuppressWarnings("unchecked")
	public void entriesDeleted(Collection addresses) {
		if (isConnected()) {
			for (Iterator it = addresses.iterator(); it.hasNext();) {
				String address = (String) it.next();
				RosterEntry entry = getConnection().getRoster().getEntry(address);
				if (entry != null) {
					Presence response = new Presence(Presence.Type.unsubscribed);
					response.setTo(entry.getUser());
					getConnection().sendPacket(response);
				}
			}
		}
	}

	/**
	 * @see RosterListener is called if a buddy is added, adds this buddy to the
	 *      roster
	 */
	@SuppressWarnings("unchecked")
	public void entriesAdded(Collection addresses) {
	}

	/**
	 * return the roster
	 * 
	 * @return JameContact[]
	 * @throws Exception
	 */
	public List<JabberContact> getRoster() throws XMPPException {
		List<JabberContact> contacts = new ArrayList<JabberContact>();
		if (isConnected()) {
			Roster roster = getConnection().getRoster();
			if (roster != null) {
				for (RosterEntry re : roster.getEntries()) {
					JabberContact contact = new JabberContact();
					contact.setJabberID(re.getUser().toString());
					contact.setName(re.getName() != null ? re.getName().toString() : null);

					contact.setGroups(re.getGroups());
					contact.setPresenceType(re.getType().toString());
					Presence p = roster.getPresence(re.getUser());
					if (p.getType() != null) {
						contact.setPresenceMode(p.getType().toString());
					} else {
						if (p.getMode() != null)
							contact.setPresenceMode(p.getMode().toString());
					}
					if (p.getStatus() != null)
						contact.setPresenceMessage(p.getStatus().toString());

					contacts.add(contact);
				}
			}
		}
		return contacts;

	}

	/**
	 * deletes a logged account
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public void deleteLoggedAccount() throws XMPPException {
		if (isConnected()) {
			AccountManager accountManager = getConnection().getAccountManager();
			accountManager.deleteAccount();
		}
	}

	/**
	 * Create a new account
	 * 
	 * @param userName
	 * @param password
	 */
	public boolean createNewAccount(String userName, String password) {
		boolean ret = false;
		if (this.conn != null && this.conn.isConnected()) {
			AccountManager accountManager = getConnection().getAccountManager();
			if (accountManager != null) {
				try {
					accountManager.createAccount(userName, password);
					ret = true;
					log.info("Create " + userName + " successful in xmpp server!");
				} catch (Exception e) {
					log.info("Create " + userName + " failed in xmpp server!");
					e.printStackTrace();
				} finally {
					// this.disconnect();
				}
			}
		}
		return ret;
	}

	/**
	 * Deletes the currently logged-in account from the server
	 */
	public void deleteAccount() throws XMPPException {
		if (this.conn != null && this.conn.isConnected()) {
			AccountManager accountManager = getConnection().getAccountManager();
			if (accountManager != null) {
				accountManager.deleteAccount();
			}
		}
	}

	/**
	 * @param vCard
	 * @throws Exception
	 */
	public void createAccountVCard(VCard vCard) throws XMPPException {
		vCard.save(this.conn);
	}

	public VCard loadAccountVCard() throws XMPPException {
		VCard vCard = new VCard();
		vCard.load(this.conn);
		return vCard;
	}

	/**
	 * Logs in to the server using the strongest authentication mode supported
	 * by the server, then sets presence to available.
	 * 
	 * @param userName
	 * @param password
	 * @throws XMPPException
	 */
	public void login(String userName, String password) throws XMPPException {
		if (this.conn != null && this.conn.isConnected()) {
			try {
				getConnection().login(userName, password);
				setRosterListener();
				getConnection().getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual);
			} catch (Exception e) {
				log.error(userName + ": " + e.getMessage());
				this.disconnect();
			}
		}
	}

	/**
	 * Logs in to the server using the strongest authentication mode supported
	 * by the server, then sets presence to available.
	 * 
	 * @param userName
	 * @param password
	 * @param resource
	 * @throws XMPPException
	 */
	public void login(String userName, String password, String resource) throws XMPPException {
		if (this.conn != null && this.conn.isConnected()) {
			try {
				getConnection().login(userName, password, resource);
				setRosterListener();
				getConnection().getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual);
			} catch (Exception e) {
				log.error(userName + ": " + e.getMessage());
				this.disconnect();
			}

		}
	}

	/**
	 * Change the users password
	 * 
	 * @param password
	 * @throws XMPPException
	 */
	public void changePassword(String password) throws XMPPException {
		if (isConnected()) {
			AccountManager accountManager = getConnection().getAccountManager();
			accountManager.changePassword(password);
		}
	}

	/**
	 * @return
	 */
	public String getUser() {
		if (this.conn != null && this.conn.isConnected()) {
			return this.conn.getUser();
		}
		return null;
	}

	/**
	 * TODO 客户端提供
	 * 
	 * @param username
	 * @return
	 * @throws XMPPException
	 */
	public boolean userIsExist(String username) throws XMPPException {
		if (this.conn != null && this.conn.isConnected()) {
			UserSearchManager search = new UserSearchManager(this.conn);
			Form searchForm = search.getSearchForm(AbleskyImConfig.XMPPSERVER_SEARCH);
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("user", username);
			ReportedData data = search.getSearchResults(answerForm, AbleskyImConfig.XMPPSERVER_SEARCH);
			Iterator<Row> rows = data.getRows();
			while (rows.hasNext()) {
				Row row = rows.next();
				Iterator<String> jids = row.getValues("jid");
				while (jids.hasNext()) {
					return true;
				}
			}
		}
		return false;
	}

}