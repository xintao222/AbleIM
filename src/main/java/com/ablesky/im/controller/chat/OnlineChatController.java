/**
 * 
 */
package com.ablesky.im.controller.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

//import com.ablesky.im.pojo.Account;
//import com.ablesky.im.pojo.SimpleAccount;
//import com.ablesky.im.service.accesscontrol.ChatAccessControllerToken;
//import com.ablesky.im.service.accesscontrol.InternalOrgThresholdAccessToken;
//import com.ablesky.im.service.account.IAccountService;
//import com.ablesky.im.service.buddy.IBuddyService;
//import com.ablesky.im.service.chat.IChatService;
//import com.ablesky.im.web.dto.AccountChatInfoDTO;
//import com.ablesky.im.web.dto.ChatDTO;
//import com.ablesky.im.web.notification.EventMessage;
//import com.ablesky.im.web.notification.NotifyUtil;
import com.ablesky.im.util.JSONHelperUtil;
import com.ablesky.im.util.LocalizationUtil;
import com.ablesky.im.util.ParamUtils;
//import com.ablesky.im.util.PhotoUtil;
import com.ablesky.im.util.SearchUser;
import com.ablesky.im.util.StringUtil;
import com.ablesky.im.util.WebUtil;
import com.ablesky.im.xmpp.AccountXmppDTO;
import com.ablesky.im.xmpp.ChatBoxState;
import com.ablesky.im.xmpp.ChatListener;
//import com.ablesky.im.xmpp.OnlineStatusCache;
import com.ablesky.im.xmpp.UserPresenceRecord;
import com.ablesky.im.xmpp.XMPPAdapter;
import com.ablesky.im.xmpp.XMPPCacheManager;
import com.ablesky.im.xmpp.XMPPChatMessage;

/**
 * @author lhe E-mail: lhe@ablesky.com
 */
public class OnlineChatController extends MultiActionController {
//	private IAccountService accountService;
//	private IChatService chatService;
//	private IBuddyService buddyService;

//	/**
//	 * 发送消息给好友 send a message to a buddy
//	 * 
//	 * @param request
//	 * @param response
//	 */
//	public void sendChatMessage(HttpServletRequest request, HttpServletResponse response) {
//		String caller = WebUtil.getUserId(request);
//		String callee = request.getParameter("callee");
//		String message = request.getParameter("chatMessage");
//		String jsonp = request.getParameter("jsonp");
//		Map<String, Object> map = new HashMap<String, Object>();
//		if (jsonp == null) {
//			JSONHelperUtil.outputOperationResultAsJSON(false, LocalizationUtil.getClientString("Chat_4", request), response);
//			return;
//		}
//		if (SearchUser.checkIfIsGuest(caller) || StringUtil.isNull(callee)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Chat_4", request), jsonp, response);
//			return;
//		}
//		try {
////			SimpleAccount sender = accountService.getSimpleAccountByUserName(caller);
////			SimpleAccount receiver = accountService.getSimpleAccountByUserName(callee);
////
////			InternalOrgThresholdAccessToken iternalToken = accountService.getInternalOrgThresholdAccessToken(sender, receiver, request);
////			if (!iternalToken.canAccess()) {
////				JSONHelperUtil.generateJsonByRemoteCall(false, iternalToken.getMessage(), jsonp, response);
////				return;
////			}
////
////			ChatAccessControllerToken token = chatService.getAccessControlOfChat(sender, receiver);
////			if (!token.getCanChat()) {
////				JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Chat_5", request), jsonp, response);
////				return;
////			}
////			// 允许发离线消息
////			Boolean isOnline = OnlineStatusCache.getInstance().queryOnlineUserByUsername(callee);
////
////			map.put("isOnline", isOnline);
////			String packedId = chatService.sendMessage(message, sender, receiver);
////			map.put("id", packedId);
//			JSONHelperUtil.generateJsonByRemoteCall(true, message, jsonp, map, response);
//			return;
//		} catch (Exception e) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, e.getMessage(), jsonp, response);
//			e.printStackTrace();
//			return;
//		}
//	}
//
//	/**
//	 * 获取与好友聊天的消息记录 get some messages from a buddy
//	 * 
//	 * @deprecated
//	 * @param request
//	 * @param response
//	 */
//	public void getChatMessage(HttpServletRequest request, HttpServletResponse response) {
//		String caller = WebUtil.getUserId(request);
//		String jsonp = request.getParameter("jsonp");
//		String callee = request.getParameter("callee");
//		if (SearchUser.checkIfIsGuest(caller) || StringUtil.isNull(callee)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Chat_4", request), jsonp, response);
//			return;
//		}
//		long lastReceiveTime = ParamUtils.getLongParameter(request, "lastReceiveTime", 0);
//
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("lastReceiveTime", System.currentTimeMillis());
//		List<XMPPChatMessage> chatMessages = new ArrayList<XMPPChatMessage>();
//		try {
//			Account callerAccount = accountService.findAccountByName(caller);
//			Account calleeAccount = accountService.findAccountByName(callee);
//			if (callerAccount == null && calleeAccount == null) {
//				map.put("success", false);
//				JSONHelperUtil.generateJsonByRemoteCall(chatMessages, map, jsonp, response);
//				return;
//			}
//			chatMessages = chatService.getMessage(callerAccount.getUsername(), calleeAccount.getUsername(), lastReceiveTime);
//			map.put("success", true);
//			JSONHelperUtil.generateJsonByRemoteCall(chatMessages, map, jsonp, response);
//		} catch (Exception e) {
//			map.put("success", false);
//			JSONHelperUtil.generateJsonByRemoteCall(chatMessages, map, jsonp, response);
//			return;
//		}
//
//	}
//
//	/**
//	 * 通知好友参与私聊 notify a buddy to chat with me
//	 * 
//	 * @param request
//	 * @param response
//	 */
//	public void notificationBuddyToChat(HttpServletRequest request, HttpServletResponse response) {
//		if (WebUtil.getUserId(request).equalsIgnoreCase(WebUtil.GUEST)) {
//			return;
//		}
//		String jsonp = request.getParameter("jsonp");
//		try {
//			String caller = request.getParameter("caller");
//			String callee = request.getParameter("callee");
//			if (StringUtil.isNull(caller) || StringUtil.isNull(callee)) {
//				return;
//			}
//			ChatListener.addEntry(callee, caller);
//		} catch (Exception e) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, e.getMessage(), jsonp, response);
//			e.printStackTrace();
//			return;
//		}
//		JSONHelperUtil.generateJsonByRemoteCall(true, LocalizationUtil.getClientString("Chat_1", request), jsonp, response);
//	}
//
//	/**
//	 * 获取当前与自己聊天的好友列表 get list of buddies who want to chat with me
//	 * 获取当前与自己聊天的好友信息记录 get list of messages whick we are talked about
//	 * 获取当前自己好友的当前状态变化 get list of changes which are about my buddies
//	 * 
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	public void checkIfChatWithMe(HttpServletRequest request, HttpServletResponse response) throws Exception {
////		if (WebUtil.getUserId(request).equalsIgnoreCase(WebUtil.GUEST)) {
////			return;
////		}
//		Map<String, Object> map = new HashMap<String, Object>();
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		AccountChatInfoDTO dto = new AccountChatInfoDTO();
//		try {
//			String callee = WebUtil.getUserId(request);
//			// 被踢出，同一帐户，多人登录问题
//			String isOut = (String) request.getAttribute("isOut");
//			if (isOut != null || WebUtil.isGuest()) {
//				isOut = "out";
//				map.put("isOut", true);
//			} else {
//				map.put("isOut", false);
//			}
//			if (SearchUser.checkIfIsGuest(callee)) {
//				if (isOut != null) {
//					JSONHelperUtil.generateJsonByRemoteCall(true, null, jsonp, map, response);
//				} else {
//					JSONHelperUtil.generateJsonByRemoteCall(false, null, jsonp, map, response);
//				}
//				return;
//			}
//			map.put("success", true);
//			// 与用户聊天的当前好友username
//			String current_buddy = ParamUtils.getParameter(request, "current_buddy");
//			// 消息的下限时间，获取与重置
//			Long lastReceiveTime = ParamUtils.getLongParameter(request, "lastReceiveTime", 0);
//			List<AccountXmppDTO> responseCallerList = ChatListener.getCallerList(callee);
//			List<ChatDTO> chaters = new ArrayList<ChatDTO>();
//			List<XMPPChatMessage> messages = null;
//			List<String> userofnewmessage = new ArrayList<String>();
//			Boolean hasNew = false;
//			// Account visiter = accountService.findAccountByName(callee);
//			SimpleAccount visiter = new SimpleAccount();
//			visiter.setUsername(callee);
//			int newMessageSize = 0;
//			Boolean isCurrentBuddy = false;
//			Long nextLastReceiveTime = lastReceiveTime;
//			SimpleAccount caller = null;
//			AccountXmppDTO responseCallerDto = null;
//			ChatDTO chater = null;
//			for (int i = 0; i < responseCallerList.size(); i++) {
//				chater = new ChatDTO();
//				chater.setCallee(callee);
//				// TEMP MODIFY
//				responseCallerDto = responseCallerList.get(i);
//				String callerUserName = responseCallerDto.getUsername();
//				String callerScreenName = responseCallerDto.getScreenname();
//				if (callerScreenName == null) {
//					caller = accountService.getSimpleAccountByUserName(callerUserName);
//					callerScreenName = caller.getAdaptScreenName();
//					responseCallerDto.setScreenname(callerScreenName);
//				}
//				if (isCurrentBuddy == false && current_buddy != null && !current_buddy.trim().equalsIgnoreCase("") && current_buddy.toLowerCase().equalsIgnoreCase(callerUserName)) {
//					isCurrentBuddy = true;
//				}
//				if (callerUserName != null) {
//					chater.setCaller(callerUserName);
//					chater.setCallerScreenName(callerScreenName);
//				}
//				if (callerUserName != null && visiter != null) {
//					messages = chatService.getMessage(callerUserName, callee, lastReceiveTime);
//					chater.setMessages(messages);
//					chaters.add(chater);
//					if (messages != null && messages.size() > 0) {
//						for (XMPPChatMessage message : messages) {
//							if (message.getPostTime() > nextLastReceiveTime) {
//								nextLastReceiveTime = message.getPostTime();
//							}
//							if (message.getType() != null && XMPPChatMessage.TYPE_NEW.equalsIgnoreCase(message.getType())) {
//								if (message.getMessageTo().trim().equalsIgnoreCase(callee)) {
//									if (isCurrentBuddy == true) {
//										message.setType(XMPPChatMessage.TYPE_HISTORY);
//									}
//									++newMessageSize;
//									if (hasNew == false) {
//										hasNew = true;
//									}
//								}
//							}
//						}
//					}
//					if (hasNew == true) {
//						userofnewmessage.add(chater.getCaller());
//					}
//				}
//			}
//			// 最后一个消息的时间，和当前的时间不同步问题
//			map.put("lastReceiveTime", nextLastReceiveTime);
//			// 新的消息数
//			map.put("ns", newMessageSize);
//			// 有新消息给用户的好友名列表
//			dto.setBuddiesOfNewMessage(userofnewmessage);
//			// 好友在线状态改变
//			List<UserPresenceRecord> recordList = chatService.getUserPresenceRecordList(visiter.getUsername(), lastReceiveTime);
//			if (recordList != null && recordList.size() > 0) {
//				SimpleAccount from_account = null;
//				String from = null;
//				for (UserPresenceRecord record : recordList) {
//					if (record.getFromScreenName() != null) {
//						continue;
//					}
//					from = record.getFrom();
//					from_account = accountService.getSimpleAccountByUserName(from);
//					if (from_account != null) {
//						record.setFromScreenName(from_account.getAdaptScreenName());
//					}
//				}
//			}
//			dto.setChaters(chaters);
//			dto.setRecordList(recordList);
//
//			/**
//			 * 同步聊天状态
//			 */
//			ChatBoxState state = chatService.getChatBoxState(callee);
//			dto.setChatBoxState(state);
//			/**
//			 * 群发消息机制
//			 */
//			String groupmessage = null;
//			groupmessage = NotifyUtil.getMessage(WebUtil.getUserId(request), EventMessage.MessageType.share);
//			if (groupmessage == null) {
//				long organizationId = ParamUtils.getLongParameter(request, "organizationId", 0l);
//				if (organizationId != 0 && NotifyUtil.judgeforbidNotifyByOrgId(organizationId) == false) {
//					/**
//					 * 替换机构id
//					 */
//					groupmessage = NotifyUtil.getMessage(WebUtil.getUserId(request), EventMessage.MessageType.taobaoCard);
//					if(groupmessage != null){
//						groupmessage = groupmessage.replace("机构ID", String.valueOf(organizationId));
//					}
//				}
//			}
//			map.put("groupmessage", groupmessage == null ? "" : groupmessage);
//			map.put("popGM", groupmessage == null ? false : true);
//
//			JSONHelperUtil.generateJsonByRemoteCall(dto, map, jsonp, response);
//		} catch (Exception e) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, null, jsonp, response);
//			e.printStackTrace();
//			return;
//		}
//	}
//
//	/**
//	 * 更新消息状态为历史记录
//	 * 
//	 * @param request
//	 * @param response
//	 */
//	public void updateChatHistoryRecord(HttpServletRequest request, HttpServletResponse response) {
//		if (WebUtil.getUserId(request).equalsIgnoreCase(WebUtil.GUEST)) {
//			return;
//		}
//		String buddyname = ParamUtils.getParameter(request, "username");
//		Long time = ParamUtils.getLongParameter(request, "time", 0);
//		String visitorname = WebUtil.getUserId(request);
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		if (buddyname == null || buddyname.equalsIgnoreCase("") || visitorname.equalsIgnoreCase(WebUtil.GUEST)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, null, jsonp, response);
//			return;
//		}
//		Boolean hasNew = false;
//		hasNew = chatService.updateXMPPChatMessageToHistory(buddyname, visitorname, time);
//		if (hasNew == false) {
//			ChatListener.delEntry(visitorname, buddyname);
//		}
//		JSONHelperUtil.generateJsonByRemoteCall(true, null, jsonp, response);
//
//		return;
//	}
//
//	/**
//	 * 批量更新消息状态为历史记录
//	 * 
//	 * @param request
//	 * @param response
//	 */
//	public void batchUdateChatHistoryRecord(HttpServletRequest request, HttpServletResponse response) {
//		if (WebUtil.getUserId(request).equalsIgnoreCase(WebUtil.GUEST)) {
//			return;
//		}
//		Long time = ParamUtils.getLongParameter(request, "time", 0);
//		String visitorname = WebUtil.getUserId(request);
//		if (visitorname.equalsIgnoreCase(WebUtil.GUEST)) {
//			return;
//		}
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		// 1,更新历史记录；2，更新监听列表
//		List<AccountXmppDTO> userlist = ChatListener.getCallerList(visitorname);
//		if (userlist == null || userlist.size() == 0) {
//			return;
//		}
//		Boolean hasNew = false;
//		for (AccountXmppDTO dto : userlist) {
//			hasNew = chatService.updateXMPPChatMessageToHistory(dto.getUsername(), visitorname, time);
//			if (hasNew == false) {
//				ChatListener.delEntry(visitorname, dto.getUsername());
//			}
//		}
//		JSONHelperUtil.generateJsonByRemoteCall(true, null, jsonp, response);
//
//		return;
//	}
//
//	/**
//	 * 同步聊天框状态
//	 * 
//	 * @param request
//	 * @param response
//	 */
//	public void processCommand(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		if (WebUtil.getUserId(request).equalsIgnoreCase(WebUtil.GUEST)) {
//			return;
//		}
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		String target = ParamUtils.getParameter(request, "target");
//		String state = ParamUtils.getParameter(request, "state");
//		String visitorname = WebUtil.getUserId(request);
//		if (target == null || state == null || visitorname.equalsIgnoreCase(WebUtil.GUEST)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, null, jsonp, response);
//			return;
//		}
//		chatService.processCommand(visitorname, target, state);
//		JSONHelperUtil.generateJsonByRemoteCall(true, null, jsonp, response);
//
//		return;
//
//	}
//
//	/**
//	 * When some one close one of his chat window, the server will remove his
//	 * listener.
//	 * 
//	 * @param request
//	 * @param response
//	 */
//	public void removeChatListener(HttpServletRequest request, HttpServletResponse response) {
//		if (WebUtil.getUserId(request).equalsIgnoreCase(WebUtil.GUEST)) {
//			return;
//		}
//		String jsonp = request.getParameter("jsonp");
//		try {
//			String host = WebUtil.getUserId(request);
//			String guest = request.getParameter("guest");
//			if (SearchUser.checkIfIsGuest(host) || StringUtil.isNull(guest)) {
//				return;
//			}
//			chatService.removeChatListener(host, guest);
//		} catch (Exception e) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, e.getMessage(), jsonp, response);
//			e.printStackTrace();
//			return;
//		}
//		JSONHelperUtil.generateJsonByRemoteCall(true, LocalizationUtil.getClientString("Chat_2", request), jsonp, response);
//	}
//
//	/**
//	 * @param request
//	 * @param response
//	 */
//	public void clearChatMessage(HttpServletRequest request, HttpServletResponse response) {
//		if (WebUtil.getUserId(request).equalsIgnoreCase(WebUtil.GUEST)) {
//			return;
//		}
//		String jsonp = request.getParameter("jsonp");
//		try {
//			String host = WebUtil.getUserId(request);
//			String guest = request.getParameter("guest");
//			if (SearchUser.checkIfIsGuest(host) || StringUtil.isNull(guest)) {
//				return;
//			}
//			chatService.clearChatMessage(host, guest);
//		} catch (Exception e) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, e.getMessage(), jsonp, response);
//			e.printStackTrace();
//			return;
//		}
//		JSONHelperUtil.generateJsonByRemoteCall(true, LocalizationUtil.getClientString("Chat_2", request), jsonp, response);
//	}
//
//	/**
//	 * 判断是否是自己的好友
//	 * 
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	public void isMyBuddy(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		if (WebUtil.getUserId(request).equalsIgnoreCase(WebUtil.GUEST)) {
//			return;
//		}
//		boolean isMyBuddy = false;
//		boolean isOnline = false;
//		Map<String, Object> param = new HashMap<String, Object>();
//		String jsonp = request.getParameter("jsonp");
//		try {
//			String caller = WebUtil.getUserId(request);
//			String callee = request.getParameter("callee");
//			if (SearchUser.checkIfIsGuest(caller) || StringUtil.isNull(callee)) {
//				JSONHelperUtil.generateJsonByRemoteCall(false, null, jsonp, response);
//				return;
//			}
//
//			SimpleAccount sender = accountService.getSimpleAccountByUserName(caller);
//			SimpleAccount receiver = accountService.getSimpleAccountByUserName(callee);
//
//			isMyBuddy = buddyService.isBuddy(sender, receiver);
//			isOnline = OnlineStatusCache.getInstance().queryOnlineUserByUsername(callee);
//		} catch (Exception e) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, e.getMessage(), jsonp, response);
//			e.printStackTrace();
//			return;
//		}
//		param.put("isMyBuddy", isMyBuddy);
//		param.put("isOnline", isOnline);
//		JSONHelperUtil.generateJsonByRemoteCall(true, LocalizationUtil.getClientString("Chat_3", request), jsonp, param, response);
//	}
//
//	/**
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	public void kickUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		if (WebUtil.getUserId(request).equalsIgnoreCase(WebUtil.GUEST)) {
//			return;
//		}
//		String jsonp = request.getParameter("jsonp");
//		String userName = ParamUtils.getParameter(request, "userName");
//		if (StringUtil.isNull(userName)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, userName + " is not exist!", jsonp, response);
//			return;
//		}
//		XMPPCacheManager.getInstance().removeXmppAdapter(userName);
//		JSONHelperUtil.generateJsonByRemoteCall(true, userName + " is kicked !", jsonp, response);
//	}
//
//	/**
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	public void listCacheUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		if (WebUtil.getUserId(request).equalsIgnoreCase(WebUtil.GUEST)) {
//			return;
//		}
//		String jsonp = request.getParameter("jsonp");
//		List<String> list = new ArrayList<String>();
//		Map<String, XMPPAdapter> xmppAdapterCache = XMPPCacheManager.getInstance().getXmppAdapterCache();
//		for (String key : xmppAdapterCache.keySet()) {
//			list.add(key);
//		}
//		JSONHelperUtil.generateJsonByRemoteCall(list, null, jsonp, response);
//	}
//
//	/**
//	 * @param chatService
//	 *            获取用户图片
//	 */
//	public void getPhotoUrl(HttpServletRequest request, HttpServletResponse response) {
//		if (WebUtil.getUserId(request).equalsIgnoreCase(WebUtil.GUEST)) {
//			return;
//		}
//		String jsonp = request.getParameter("jsonp");
//		String userNames = request.getParameter("userNames");
//		if (userNames == null) {
//			return;
//		}
//		String[] userNameList = userNames.split(",");
//		String size = request.getParameter("size");
//		if (size == null) {
//			size = "small";
//		}
//		Map<String, Object> param = new HashMap<String, Object>();
//		StringBuffer urls = null;
//		for (String userName : userNameList) {
//			Account account = accountService.findAccountByName(userName);
//			if (urls == null) {
//				urls = new StringBuffer();
//				urls.append(PhotoUtil.getAccountPhotoPath(account, size));
//			} else {
//				urls.append("," + PhotoUtil.getAccountPhotoPath(account, size));
//			}
//
//		}
//		param.put("urls", urls);
//		JSONHelperUtil.generateJsonByRemoteCall(true, "", jsonp, param, response);
//	}
//
//	public void setChatService(IChatService chatService) {
//		this.chatService = chatService;
//	}
//
//	public void setAccountService(IAccountService accountService) {
//		this.accountService = accountService;
//	}
//
//	public void setBuddyService(IBuddyService buddyService) {
//		this.buddyService = buddyService;
//	}
}
