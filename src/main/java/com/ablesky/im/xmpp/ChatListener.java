/**
 * 
 */
package com.ablesky.im.xmpp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ablesky.im.util.StringUtil;

/**
 * @author lhe E-mail: lhe@ablesky.com
 */
public class ChatListener {
	private static SortedMap<String, List<AccountXmppDTO>> chatListenerCenter;
	// lock for iterator reader and write
	private static Lock processLock = new ReentrantLock();
	static {
		SortedMap<String, List<AccountXmppDTO>> asynchronizedmap = new TreeMap<String, List<AccountXmppDTO>>();
		chatListenerCenter = Collections.synchronizedSortedMap(asynchronizedmap);
	}

	public static void addEntry(String calleeName, String callerName) {
		processLock.lock();
		try {
			if (StringUtil.isNull(calleeName) || StringUtil.isNull(callerName))
				return;
			if (chatListenerCenter.containsKey(calleeName.toLowerCase())) {
				List<AccountXmppDTO> list = chatListenerCenter.get(calleeName.toLowerCase());
				AccountXmppDTO dto = new AccountXmppDTO();
				dto.setUsername(callerName.toLowerCase());
				if (!list.contains(dto)) {
					list.add(dto);
				}
			} else {
				List<AccountXmppDTO> list = new ArrayList<AccountXmppDTO>();
				list = Collections.synchronizedList(list);
				AccountXmppDTO dto = new AccountXmppDTO();
				dto.setUsername(callerName.toLowerCase());
				list.add(dto);
				chatListenerCenter.put(calleeName.toLowerCase(), list);
			}
		} finally {
			processLock.unlock();
		}
	}

	public static void delEntry(String calleeName, String callerName) {
		processLock.lock();
		try {
			if (StringUtil.isNull(calleeName) || StringUtil.isNull(callerName))
				return;
			if (chatListenerCenter.containsKey(calleeName.toLowerCase())) {
				List<AccountXmppDTO> list = chatListenerCenter.get(calleeName.toLowerCase());
				AccountXmppDTO dto = new AccountXmppDTO();
				dto.setUsername(callerName.toLowerCase());
				if (list.contains(dto))
					list.remove(dto);
			}
		} finally {
			processLock.unlock();
		}
	}

	public static List<AccountXmppDTO> getCallerList(String calleeName) {

		try {
			List<AccountXmppDTO> retValue = new ArrayList<AccountXmppDTO>();
			List<AccountXmppDTO> callerList = chatListenerCenter.get(calleeName.toLowerCase());
			if (callerList == null)
				return retValue;
			for (AccountXmppDTO iter : callerList) {
				retValue.add(iter);
			}
			// callerList.clear();
			return retValue;
		} finally {
		}
	}

	public static void removeCallerList(String calleeName) {
		try {
			chatListenerCenter.remove(calleeName.toLowerCase());
		} catch (Exception e) {
		}
	}

	public static void removeChatListener(String calleeName, String callerName) {
		try {
			List<AccountXmppDTO> callerList = chatListenerCenter.get(calleeName.toLowerCase());
			if (callerList == null)
				return;
			AccountXmppDTO dto = new AccountXmppDTO();
			dto.setUsername(callerName.toLowerCase());
			callerList.remove(dto);
		} catch (Exception e) {
		}
	}

	public static void notificationBuddyToChat(String caller, String callee) {
		try {
			addEntry(callee, caller);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
