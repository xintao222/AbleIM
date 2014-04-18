package com.ablesky.im.xmpp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ablesky.im.util.AbleskyImConfig;

/**
 * @author lhe E-mail: lhe@ablesky.com
 */
public class XMPPMessageManager {
	public static final Log log = LogFactory.getLog(XMPPMessageManager.class);
	private static XMPPMessageManager instance = new XMPPMessageManager();
	// 记录Web存储的消息信息
	private final Map<String, List<XMPPChatMessage>> newMessageCenter = new ConcurrentHashMap<String, List<XMPPChatMessage>>();

	private XMPPMessageManager() {
	};

	public static XMPPMessageManager getInstance() {
		return instance;
	}

	public synchronized void addRecvedMessageToCenter(XMPPChatMessage msg) {
		if (msg == null) {
			return;
		}
		// 初始化消息类型
		msg.setType(XMPPChatMessage.TYPE_NEW);
		String to = msg.getMessageTo().toLowerCase();
		String from = msg.getMessageFrom().toLowerCase();
		// 黑名单确认
//		boolean isInMyBlocked = BlockedListCache.getInstance().checkIsInMyBlocked(from, to);
//		if (isInMyBlocked) {
//			return;
//		}
//		boolean clientOnline = OnlineStatusCache.getInstance().queryClientOnlineUserByUsername(to);
//		boolean isBlocked = BlockedListCache.getInstance().checkIsInMyBlocked(to, from);

//		if (isBlocked) {
//			msg.setReceiverEnable(false);
//		} else {
//			// If only user login in client, his web online chat won’t receive
//			// message.
//			msg.setReceiverEnable(!clientOnline);
//		}
		if (msg.getFromResouce() != null && msg.getFromResouce().indexOf(AbleskyImConfig.WEB_PREFIX_LOGIN_RESOURCE) >= 0) {
			msg.setSenderEnable(true);
		} else if (msg.getFromResouce() != null && msg.getFromResouce().indexOf(AbleskyImConfig.CLINET_PREFIX_LOGIN_RESOURCE) >= 0) {
			msg.setSenderEnable(true);
		} else {
			msg.setSenderEnable(false);
		}

		boolean contain = newMessageCenter.containsKey(to + "_" + from);
		List<XMPPChatMessage> msgList = contain ? newMessageCenter.get(to + "_" + from) : newMessageCenter.get(from + "_" + to);
		if (msgList == null) {
			msgList = new ArrayList<XMPPChatMessage>();
			Collections.synchronizedList(msgList);
			msgList.add(msg);
			newMessageCenter.put(to + "_" + from, msgList);
//			if (!clientOnline && !isBlocked) {
//				ChatListener.notificationBuddyToChat(from, to);
//				ChatListener.notificationBuddyToChat(to, from);
//			}
		} else {
			boolean inMessageCenter = false;
			for (int i = 0; i < msgList.size(); i++) {
				if (msgList.get(i).getPacketId().equals(msg.getPacketId())) {
					inMessageCenter = true;
					break;
				}
			}
			if (!inMessageCenter) {
				msgList.add(msg);
//				if (!clientOnline && !isBlocked) {
//					ChatListener.notificationBuddyToChat(from, to);
//					ChatListener.notificationBuddyToChat(to, from);
//				}
			}
		}
	}

	/**
	 * Get messages by receiver. The receiver will get the messages which he
	 * sends and the messages which send to him. If he is the message sender and
	 * senderEnable is true or if he is the message receiver and receiverEnable
	 * is true, then he can get this message. If blocked user is true, then
	 * block all the message
	 * 
	 * @param receiver
	 * @param sender
	 * @param lastReceiveTime
	 * @return
	 */
	public synchronized List<XMPPChatMessage> getMessagesByReceiver(String receiverUsername, String senderUsername, Long lastReceiveTime) {
		List<XMPPChatMessage> retList = new ArrayList<XMPPChatMessage>();
		if (receiverUsername == null || senderUsername == null)
			return retList;
		String to = receiverUsername.toLowerCase();
		String from = senderUsername.toLowerCase();
		boolean contain = newMessageCenter.containsKey(to + "_" + from);
		List<XMPPChatMessage> msgList = contain ? newMessageCenter.get(to + "_" + from) : newMessageCenter.get(from + "_" + to);
		if (msgList != null) {
			for (int i = 0; i < msgList.size(); i++) {
				XMPPChatMessage msg = msgList.get(i);
				if (msg.getPostTime() > lastReceiveTime) {
					if ((to.equalsIgnoreCase(msg.getMessageTo()) && msg.getReceiverEnable()) || (to.equalsIgnoreCase(msg.getMessageFrom()) && msg.getSenderEnable())) {
						retList.add(msg);
					}
				}
			}
		}
		return retList;
	}

	/**
	 * @param msgList
	 * @param userName
	 * @param key
	 */
	private void checkRemoveMessage(List<XMPPChatMessage> msgList, String userName, String key) {
		if (msgList == null) {
			return;
		}
		int size = msgList.size();
		List<XMPPChatMessage> tmp = new ArrayList<XMPPChatMessage>();
		Collections.synchronizedList(tmp);
		for (int i = 0; i < size; i++) {
			XMPPChatMessage msg = msgList.get(i);
			if (userName.equalsIgnoreCase(msg.getMessageFrom())) {
				msg.setSenderEnable(false);
				if (msg.getReceiverEnable()) {
					tmp.add(msg);
				}
			} else {
				msg.setReceiverEnable(false);
				if (msg.getSenderEnable()) {
					tmp.add(msg);
				}
			}
		}
		if (tmp.size() == 0) {
			newMessageCenter.remove(key);
		} else {
			newMessageCenter.put(key, tmp);
		}
	}

	/**
	 * @param userName
	 */
	public synchronized void removeMessagesByUserName(String userName) {
		try {
			userName = userName.toLowerCase();
			Set<String> keys = newMessageCenter.keySet();
			for (String key : keys) {
				String prefix = key.substring(0, key.indexOf("_"));
				String postfix = key.substring(key.indexOf("_") + 1);
				if (userName.equalsIgnoreCase(prefix) || userName.equalsIgnoreCase(postfix)) {
					List<XMPPChatMessage> msgList = newMessageCenter.get(key);
					checkRemoveMessage(msgList, userName, key);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * @param host
	 * @param guest
	 */
	public synchronized void removeMessages(String host, String guest) {
		try {
			host = host.toLowerCase();
			guest = guest.toLowerCase();
			String prekey = host + "_" + guest;
			String poskey = guest + "_" + host;
			if (newMessageCenter.containsKey(prekey)) {
				List<XMPPChatMessage> msgList = newMessageCenter.get(prekey);
				checkRemoveMessage(msgList, host, prekey);
			}
			if (newMessageCenter.containsKey(poskey)) {
				List<XMPPChatMessage> msgList = newMessageCenter.get(poskey);
				checkRemoveMessage(msgList, host, poskey);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * 对于to用户，更新from用户发给to的消息为历史消息
	 * 
	 * @param from
	 * @param to
	 * @param time
	 */
	public synchronized Boolean updateXMPPChatMessageToHistory(String from, String to, Long time) {
		// 统一变成小写
		from = from.toLowerCase();
		to = to.toLowerCase();
		Boolean hasNew = false;
		boolean contain = newMessageCenter.containsKey(to + "_" + from);
		List<XMPPChatMessage> msgList = contain ? newMessageCenter.get(to + "_" + from) : newMessageCenter.get(from + "_" + to);
		if (msgList == null || msgList.size() == 0) {
			return hasNew;
		}
		if (time == null) {
			time = 0l;
		}
		for (XMPPChatMessage msg : msgList) {
			if (msg.getMessageTo().equalsIgnoreCase(to)) {
				if (msg.getPostTime() <= time) {
					msg.setType(XMPPChatMessage.TYPE_HISTORY);
				} else {
					hasNew = true;
				}
			}
		}

		return hasNew;
	}
}
