/*
 * Copyright 2006-2008 AbleSky, Inc. All rights reserved. This program is an
 * unpublished work fully protected by the United States, P.R. China and
 * International copyright laws and is considered a trade secret belonging to
 * AbleSky, Inc. It is not to be divulged or used by parties who have not
 * received written authorization from AbleSky, Inc. AbleSky, Inc. 539 Chiquita
 * Ave Mountain View, CA 94041, USA http://www.ablesky.com Email:
 * support@ablesky.com Copyright 2006-2008 AbleSky, Inc. All rights reserved.
 */
package com.ablesky.im.util;

//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReadWriteLock;
//import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import com.ablesky.im.xmpp.ClearUserStatus;
//import com.ablesky.im.xmpp.OnlineStatusCache;

public class CountOnlineUser implements HttpSessionListener {
	private static long count = 0;
//	private static final ReadWriteLock lock = new ReentrantReadWriteLock();
//	private static final Lock readLock = lock.readLock();
//	private static final Lock writeLock = lock.writeLock();
	public static final Log log = LogFactory.getLog(CountOnlineUser.class);

	public void sessionCreated(HttpSessionEvent e) {
		count++;
		log.debug("Session create: sessionId=" + e.getSession().getId() + ", the session number is increased to " + count);
	}

	public void sessionDestroyed(HttpSessionEvent e) {
//		count--;
//		log.debug("Session destroy: sessionId=" + e.getSession().getId() + ", the session number is decreased to " + count);
//		String userName = OnlineStatusCache.getInstance().getUsernameBySessionId(e.getSession().getId());
//		if (userName != null && !userName.equalsIgnoreCase("guest")) {
//			OnlineStatusCache.getInstance().removeUserSessionIdObject(e.getSession().getId());
//			ClearUserStatus.clear(userName);
//		}
	}

	public static long getSessionCount() {
		return count;
	}

//	public static long getOnlineUserCount() {
////		return count + OnlineStatusCache.getInstance().clientOnlineUserNumber();
//	}
}
