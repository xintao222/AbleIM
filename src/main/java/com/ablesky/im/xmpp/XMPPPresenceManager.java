///**
// * 
// */
//package com.ablesky.im.xmpp;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import net.rubyeye.xmemcached.GetsResponse;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.jivesoftware.smack.packet.Presence;
//
//import com.ablesky.im.cache.MemcachedNamespaceUtil;
//import com.ablesky.im.cache.XmemcachedUtil;
////import com.ablesky.im.pojo.SimpleAccount;
////import com.ablesky.im.service.buddy.IBuddyService;
////import com.ablesky.im.service.buddyInvite.IBuddyInviteService;
//import com.ablesky.im.util.BeanFactoryUtil;
//import com.ablesky.im.util.SearchUser;
//
///**
// * @author lhe E-mail: lhe@ablesky.com modified by
// * @author kbian
// */
//public class XMPPPresenceManager {
//	public static final Log log = LogFactory.getLog(XMPPManager.class);
//
//	private static XMPPPresenceManager fgSingleton = new XMPPPresenceManager();
//	// 存储Web所有用户，在线好友请求信息
//	// private final Map<String, List<Presence>> presenceCenter = new
//	// ConcurrentHashMap<String, List<Presence>>();
//
//	// 存储Web所有用户，在线好友状态改变信息
//	private final Map<String, ConcurrentHashMap<String, UserPresenceRecord>> presenceCenterForStatusChange = new ConcurrentHashMap<String, ConcurrentHashMap<String, UserPresenceRecord>>();
//
//	// private IBuddyService buddyService;
//
//	private XMPPPresenceManager() {
//	};
//
//	public static XMPPPresenceManager getInstance() {
//		return fgSingleton;
//	}
//
////	/**
////	 * 向xmpp服务器发送请求，接受好友邀请
////	 * 
////	 * @param p
////	 */
////	private void defaultReceiveBuddyRequest(Presence p) {
////		String receiverName = p.getTo().substring(0, p.getTo().indexOf("@")).toLowerCase();
////		String senderName = p.getFrom().substring(0, p.getFrom().indexOf("@")).toLowerCase();
////		SimpleAccount receiver = SearchUser.findAccountByName(receiverName);
////		SimpleAccount sender = SearchUser.findAccountByName(senderName);
////		IBuddyService buddyService = (IBuddyService) BeanFactoryUtil.getBean("buddyService");
////		boolean isToBuddy = buddyService.isToBuddy(receiver, sender);
////		// 如果sender是receiver的好友，则处理
////		if (isToBuddy) {
////			try {
////				buddyService.receiveBuddyRequestFromXMPP(null, receiver, sender, true);
////			} catch (Exception e) {
////				e.printStackTrace();
////			}
////		}
////		// 如果是好友邀请，则处理
////		IBuddyInviteService buddyInviteService = (IBuddyInviteService) BeanFactoryUtil.getBean("buddyInviteService");
////		boolean isInvitedBuddy = buddyInviteService.isInvited(receiver, sender);
////		if (isInvitedBuddy) {
////			try {
////				buddyService.receiveBuddyRequestFromXMPP(null, receiver, sender, false);
////				buddyService.sendBuddyRequestToXMPP(null, receiver, sender, false);
////			} catch (Exception e) {
////				e.printStackTrace();
////			}
////		}
////	}
//
//	/**
//	 * 往缓存中增加p
//	 * 
//	 * @param p
//	 */
//	public void addPresenceToCenter(Presence p) {
//		String receiverName = p.getTo().substring(0, p.getTo().indexOf("@")).toLowerCase();
//		String senderName = p.getFrom().substring(0, p.getFrom().indexOf("@")).toLowerCase();
//		/**
//		 * 确认是否在黑名单列表中 make sure whether it's in blocked list
//		 */
//		if (BlockedListCache.getInstance().checkIsBlocked(receiverName, senderName)) {
//			return;
//		}
//		XmemcachedUtil cache = XmemcachedUtil.getInstance();
//		if (cache == null) {
//			return;
//		}
//		/**
//		 * 无需同步关键词synchronized,该操作相关的方法单服务器只会串行调用
//		 */
//		/**
//		 * 多服务器环境下，可保证更新的一致性，创建时的一致性无法保证 TODO
//		 */
//
//		GetsResponse<Object> response = (GetsResponse<Object>) cache.gets(MemcachedNamespaceUtil.Buddy_Request + receiverName, null, false);
//		if (response == null) {
//			List<PresenceDTO> pList = new ArrayList<PresenceDTO>();
//			PresenceDTO dto = PresenceDTO.toDTO(p);
//			pList.add(dto);
//			cache.set(MemcachedNamespaceUtil.Buddy_Request_NO + receiverName, pList.size(), MemcachedNamespaceUtil.Buddy_Request_NO_Expiry);
//			cache.set(MemcachedNamespaceUtil.Buddy_Request + receiverName, pList, MemcachedNamespaceUtil.Buddy_Request_Expiry);
//			defaultReceiveBuddyRequest(p);
//		} else {
//			boolean isIn = false;
//			List<PresenceDTO> pList = (List<PresenceDTO>) response.getValue();
//			for (PresenceDTO PresenceDTO : pList) {
//				if (!PresenceDTO.getType().equalsIgnoreCase(Presence.Type.subscribe.name())) {
//					pList.remove(PresenceDTO);
//					continue;
//				}
//				String oldFrom = PresenceDTO.getFrom().substring(0, PresenceDTO.getFrom().indexOf("@"));
//				String from = p.getFrom().substring(0, p.getFrom().indexOf("@"));
//				if (from.equalsIgnoreCase(oldFrom)) {
//					isIn = true;
//					break;
//				}
//			}
//			if (!isIn) {
//				PresenceDTO dto = PresenceDTO.toDTO(p);
//				pList.add(dto);
//				boolean result = cache.cas(MemcachedNamespaceUtil.Buddy_Request + receiverName, pList, MemcachedNamespaceUtil.Buddy_Request_Expiry, response.getCas(), null, false);
//				if (result == false) {
//					int try_times = 10;
//					while (result == false && try_times-- > 0) {
//						response = (GetsResponse<Object>) cache.gets(MemcachedNamespaceUtil.Buddy_Request + receiverName, null, false);
//						if (response == null) {
//							break;
//						} else {
//							pList = (List<PresenceDTO>) response.getValue();
//							for (PresenceDTO PresenceDTO : pList) {
//								if (!PresenceDTO.getType().equalsIgnoreCase(Presence.Type.subscribe.name())) {
//									pList.remove(PresenceDTO);
//									continue;
//								}
//								String oldFrom = PresenceDTO.getFrom().substring(0, PresenceDTO.getFrom().indexOf("@"));
//								String from = p.getFrom().substring(0, p.getFrom().indexOf("@"));
//								if (from.equalsIgnoreCase(oldFrom)) {
//									isIn = true;
//									break;
//								}
//							}
//							if (!isIn) {
//								pList.add(dto);
//								result = cache.cas(MemcachedNamespaceUtil.Buddy_Request + receiverName, pList, MemcachedNamespaceUtil.Buddy_Request_Expiry, response.getCas(), null, false);
//							}
//						}
//					}
//				}
//				if (result == true) {
//					cache.set(MemcachedNamespaceUtil.Buddy_Request_NO + receiverName, pList.size(), MemcachedNamespaceUtil.Buddy_Request_NO_Expiry);
//				}
//				defaultReceiveBuddyRequest(p);
//			}
//		}
//		log.debug("Account" + receiverName + " add buddy request. And buddy_request is " + (cache.get(MemcachedNamespaceUtil.Buddy_Request + receiverName) != null) + "And buddy_request_no is " + (Integer) cache.get(MemcachedNamespaceUtil.Buddy_Request_NO + receiverName));
//	}
//
//	/**
//	 * 接受好友请求，并从缓存中删除p
//	 * 
//	 * @param p
//	 */
//	public void deletePresenceFromCenterForReceive(Presence p) {
//		String receiverName = p.getTo().substring(0, p.getTo().indexOf("@")).toLowerCase();
//		XmemcachedUtil cache = XmemcachedUtil.getInstance();
//		if (cache == null) {
//			return;
//		}
//		/**
//		 * 无需同步关键词synchronized,该操作相关的方法单服务器只会串行调用
//		 */
//		GetsResponse<Object> response = (GetsResponse<Object>) cache.gets(MemcachedNamespaceUtil.Buddy_Request + receiverName, null, false);
//		if (response == null || response.getValue() == null) {
//			return;
//		} else {
//			List<PresenceDTO> pList = (List<PresenceDTO>) response.getValue();
//			for (PresenceDTO presenceDTO : pList) {
//				String oldFrom = presenceDTO.getFrom().substring(0, presenceDTO.getFrom().indexOf("@"));
//				String from = p.getFrom().substring(0, p.getFrom().indexOf("@"));
//				if (from.equalsIgnoreCase(oldFrom)) {
//					pList.remove(presenceDTO);
//					boolean result = cache.cas(MemcachedNamespaceUtil.Buddy_Request + receiverName, pList, MemcachedNamespaceUtil.Buddy_Request_Expiry, response.getCas(), null, false);
//					if (result == false) {
//						int try_times = 10;
//						while (result == false && try_times-- > 0) {
//							response = (GetsResponse<Object>) cache.gets(MemcachedNamespaceUtil.Buddy_Request + receiverName, null, false);
//							if (response == null) {
//								break;
//							}
//							pList = (List<PresenceDTO>) response.getValue();
//							for (PresenceDTO dto : pList) {
//								if (from.equalsIgnoreCase(oldFrom)) {
//									pList.remove(presenceDTO);
//									result = cache.cas(MemcachedNamespaceUtil.Buddy_Request + receiverName, pList, MemcachedNamespaceUtil.Buddy_Request_Expiry, response.getCas(), null, false);
//								}
//							}
//
//						}
//					}
//					if (result == true) {
//						cache.set(MemcachedNamespaceUtil.Buddy_Request_NO + receiverName, pList.size(), MemcachedNamespaceUtil.Buddy_Request_NO_Expiry);
//					}
//
//					cache.set(MemcachedNamespaceUtil.Buddy_Request + receiverName, pList, MemcachedNamespaceUtil.Buddy_Request_Expiry);
//					break;
//				}
//			}
//		}
//		log.debug("Account" + receiverName + " receive buddy request. And buddy_request is " + (cache.get(MemcachedNamespaceUtil.Buddy_Request + receiverName) != null) + "And buddy_request_no is " + (Integer) cache.get(MemcachedNamespaceUtil.Buddy_Request_NO + receiverName));
//	}
//
//	/**
//	 * 拒绝好友请求，并删除本地好友请求
//	 * 
//	 * @param p
//	 */
//	public void deletePresenceFromCenterForReject(Presence p) {
//		String receiverName = p.getTo().substring(0, p.getTo().indexOf("@")).toLowerCase();
//		XmemcachedUtil cache = XmemcachedUtil.getInstance();
//		if (cache == null) {
//			return;
//		}
//		/**
//		 * 无需同步关键词synchronized,该操作相关的方法单服务器只会串行调用
//		 */
//		GetsResponse<Object> response = (GetsResponse<Object>) cache.gets(MemcachedNamespaceUtil.Buddy_Request + receiverName, null, false);
//		if (response == null || response.getValue() == null) {
//			return;
//		} else {
//			List<PresenceDTO> pList = (List<PresenceDTO>) response.getValue();
//			for (PresenceDTO presenceDTO : pList) {
//				String oldFrom = presenceDTO.getFrom().substring(0, presenceDTO.getFrom().indexOf("@"));
//				String from = p.getFrom().substring(0, p.getFrom().indexOf("@"));
//				if (from.equalsIgnoreCase(oldFrom)) {
//					pList.remove(presenceDTO);
//					boolean result = cache.cas(MemcachedNamespaceUtil.Buddy_Request + receiverName, pList, MemcachedNamespaceUtil.Buddy_Request_Expiry, response.getCas(), null, false);
//					if (result == false) {
//						int try_times = 10;
//						while (result == false && try_times-- > 0) {
//							response = (GetsResponse<Object>) cache.gets(MemcachedNamespaceUtil.Buddy_Request + receiverName, null, false);
//							if (response == null) {
//								break;
//							}
//							pList = (List<PresenceDTO>) response.getValue();
//							for (PresenceDTO dto : pList) {
//								if (from.equalsIgnoreCase(oldFrom)) {
//									pList.remove(presenceDTO);
//									result = cache.cas(MemcachedNamespaceUtil.Buddy_Request + receiverName, pList, MemcachedNamespaceUtil.Buddy_Request_Expiry, response.getCas(), null, false);
//								}
//							}
//
//						}
//					}
//					if (result == true) {
//						cache.set(MemcachedNamespaceUtil.Buddy_Request_NO + receiverName, pList.size(), MemcachedNamespaceUtil.Buddy_Request_NO_Expiry);
//					}
//
//					cache.set(MemcachedNamespaceUtil.Buddy_Request + receiverName, pList, MemcachedNamespaceUtil.Buddy_Request_Expiry);
//					break;
//				}
//			}
//		}
//		log.debug("Account" + receiverName + " reject buddy request. And buddy_request is " + (cache.get(MemcachedNamespaceUtil.Buddy_Request + receiverName) != null) + "And buddy_request_no is " + (Integer) cache.get(MemcachedNamespaceUtil.Buddy_Request_NO + receiverName));
//	}
//
//	public List<PresenceDTO> getPresencesByReceiver(SimpleAccount receiver) {
//		if (receiver == null)
//			return null;
//		XmemcachedUtil cache = XmemcachedUtil.getInstance();
//		if (cache == null) {
//			return null;
//		}
//		List<PresenceDTO> pList = (List<PresenceDTO>) cache.get(MemcachedNamespaceUtil.Buddy_Request + receiver.getUsername().toLowerCase());
//		return pList;
//	}
//
//	public int getPresencesCountByReceiver(SimpleAccount receiver) {
//		if (receiver == null)
//			return 0;
//		XmemcachedUtil cache = XmemcachedUtil.getInstance();
//		if (cache == null) {
//			return 0;
//		}
//		return (Integer) cache.get(MemcachedNamespaceUtil.Buddy_Request_NO + receiver.getUsername());
//	}
//
//	public void removePresenceFromCenter(SimpleAccount fromAccount, SimpleAccount toAccount, Presence.Type type) {
//		List<PresenceDTO> pList = XMPPPresenceManager.getInstance().getPresencesByReceiver(toAccount);
//		XmemcachedUtil cache = XmemcachedUtil.getInstance();
//		if (cache == null) {
//			return;
//		}
//		synchronized (this) {
//			if (pList != null) {
//				for (int i = 0; i < pList.size(); i++) {
//					PresenceDTO p = pList.get(i);
//					String from = p.getFrom().substring(0, p.getFrom().indexOf("@"));
//					if (p.getType().equalsIgnoreCase(type.name())) {
//						if (from.equalsIgnoreCase(fromAccount.getUsername())) {
//							pList.remove(i);
//							int no = pList.size();
//							if (no == 0) {
//								cache.delete(MemcachedNamespaceUtil.Buddy_Request_NO + toAccount.getUsername());
//								cache.delete(MemcachedNamespaceUtil.Buddy_Request + toAccount.getUsername());
//							} else {
//								cache.set(MemcachedNamespaceUtil.Buddy_Request_NO + toAccount.getUsername(), no, MemcachedNamespaceUtil.Buddy_Request_NO_Expiry);
//								cache.set(MemcachedNamespaceUtil.Buddy_Request + toAccount.getUsername(), pList, MemcachedNamespaceUtil.Buddy_Request_Expiry);
//							}
//						}
//						break;
//					}
//				}
//			}
//		}
//
//	}
//
//	public void removeFromPresenceCenter(String uname) {
//		XmemcachedUtil cache = XmemcachedUtil.getInstance();
//		if (cache == null) {
//			return;
//		}
//		synchronized (this) {
//			cache.delete(MemcachedNamespaceUtil.Buddy_Request_NO + uname);
//			cache.delete(MemcachedNamespaceUtil.Buddy_Request + uname);
//		}
//	}
//
//	// ---------------------------------------------------------------------------------------------------------------------------------------------------
//
//	/**
//	 * 保存或更新好友状态信息
//	 * 
//	 * @param p
//	 * @param time
//	 */
//	public void saveOrUpdatePresenceCenterForStatusChange(Presence p, Long time) {
//		String receiverName = p.getTo().substring(0, p.getTo().indexOf("@")).toLowerCase();
//		String senderName = p.getFrom().substring(0, p.getFrom().indexOf("@")).toLowerCase();
//		Boolean isWeb = p.getFrom().indexOf("AS-Web") >= 0;// true表示改变网站的状态；false表示改变客户端的状态
//		Boolean isHiding = p.getMode() == null ? false : p.getMode().name().equals(UserPresenceRecord.MODE_HIDING);// TODO
//		ConcurrentHashMap<String, UserPresenceRecord> map = presenceCenterForStatusChange.get(receiverName.toLowerCase().trim());
//		if (map == null) {
//			map = new ConcurrentHashMap<String, UserPresenceRecord>();
//			presenceCenterForStatusChange.put(receiverName.toLowerCase().trim(), map);
//		}
//		UserPresenceRecord record = map.get(senderName);
//		if (record == null) {
//			record = new UserPresenceRecord();
//			record.setFrom(senderName);
//			record.setTo(receiverName);
//			record.setTime(time);
//			record.setStatus(p.getType().name());
//			if (isWeb == true) {
//				// 来自网站
//				record.setSrc(UserPresenceRecord.SRC_WEB);
//				record.setFromWWW(p.getFrom());
//			} else {
//				// 来自客户端
//				record.setSrc(UserPresenceRecord.SRC_CLIENT);
//			}
//			// 修正隐身状态为不可见
//			if (isHiding == true) {
//				record.setStatus(UserPresenceRecord.STATUS_OFFLINE);
//			}
//		} else {
//			record.setTime(time);
//			if (isWeb == true) {
//				// 网站上线
//				if (p.getType().name().equalsIgnoreCase(UserPresenceRecord.STATUS_ONLINE)) {
//					record.setFromWWW(p.getFrom());
//					if (record.getStatus().equalsIgnoreCase(UserPresenceRecord.STATUS_ONLINE)) {
//						// 当前状态为上线状态
//						if (record.getSrc().equalsIgnoreCase(UserPresenceRecord.SRC_CLIENT)) {
//							record.setSrc(UserPresenceRecord.SRC_ALL);
//						}
//					} else {
//						// 当前状态为下线状态
//						record.setSrc(UserPresenceRecord.SRC_WEB);
//						record.setStatus(p.getType().name());
//					}
//				} else {
//					// 网站下线,当前状态为下线状态
//					if (record.getStatus().equalsIgnoreCase(UserPresenceRecord.STATUS_OFFLINE)) {
//						if (record.getSrc().equalsIgnoreCase(UserPresenceRecord.SRC_CLIENT)) {
//							record.setSrc(UserPresenceRecord.SRC_ALL);
//						}
//					} else {
//						// 当前状态为上线状态
//						if (record.getSrc().equalsIgnoreCase(UserPresenceRecord.SRC_CLIENT)) {
//							;
//						} else if (record.getSrc().equalsIgnoreCase(UserPresenceRecord.SRC_WEB)) {
//							String fromWWW = record.getFromWWW();
//							if (fromWWW == null || fromWWW.equalsIgnoreCase(p.getFrom())) {
//								record.setStatus(p.getType().name());
//							}
//						} else {
//							record.setSrc(UserPresenceRecord.SRC_CLIENT);
//						}
//					}
//				}
//			} else {
//				if (p.getType().name().equalsIgnoreCase(UserPresenceRecord.STATUS_ONLINE)) {
//					// 客户端上线
//					if (record.getStatus().equalsIgnoreCase(UserPresenceRecord.STATUS_ONLINE)) {
//						// 当前状态为上线状态
//						if (record.getSrc().equalsIgnoreCase(UserPresenceRecord.SRC_WEB) && isHiding == false) {
//							record.setSrc(UserPresenceRecord.SRC_ALL);
//						} else if (record.getSrc().equalsIgnoreCase(UserPresenceRecord.SRC_CLIENT) && isHiding == true) {
//							record.setStatus(UserPresenceRecord.STATUS_OFFLINE);
//						} else if (record.getSrc().equalsIgnoreCase(UserPresenceRecord.SRC_ALL) && isHiding == true) {
//							record.setSrc(UserPresenceRecord.SRC_WEB);
//						}
//					} else if (isHiding == false) {
//						// 当前状态为下线状态
//						record.setSrc(UserPresenceRecord.SRC_CLIENT);
//						record.setStatus(p.getType().name());
//					} else if (isHiding == true) {
//						// 当前状态为下线状态
//						if (record.getSrc().equalsIgnoreCase(UserPresenceRecord.SRC_WEB)) {
//							record.setSrc(UserPresenceRecord.SRC_ALL);
//						}
//					}
//				} else {
//					// 客户端下线
//					if (record.getStatus().equalsIgnoreCase(UserPresenceRecord.STATUS_OFFLINE)) {
//						// 当前状态为下线状态
//						if (record.getSrc().equalsIgnoreCase(UserPresenceRecord.SRC_WEB)) {
//							record.setSrc(UserPresenceRecord.SRC_ALL);
//						}
//					} else {
//						// 当前状态为上线状态
//						if (record.getSrc().equalsIgnoreCase(UserPresenceRecord.SRC_WEB)) {
//							;
//						} else if (record.getSrc().equalsIgnoreCase(UserPresenceRecord.SRC_CLIENT)) {
//							record.setStatus(p.getType().name());
//						} else {
//							record.setSrc(UserPresenceRecord.SRC_WEB);
//						}
//					}
//				}
//			}
//		}
//		map.put(senderName, record);
//		// log.info(record.getFrom() + " " + record.getStatus() + " " +
//		// record.getTo() + " src: " + record.getSrc());
//		return;
//	}
//
//	public ConcurrentHashMap<String, UserPresenceRecord> getpresenceCenterForStatusChange(String receiverUsername) {
//		if (receiverUsername == null)
//			return null;
//		return presenceCenterForStatusChange.get(receiverUsername.toLowerCase().trim());
//	}
//
//	public void removePresenceCenterForStatusChange(String uname) {
//		presenceCenterForStatusChange.remove(uname);
//	}
//}
