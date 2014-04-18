///*
// * Copyright 2006-2008 AbleSky, Inc. All rights reserved. This program is an
// * unpublished work fully protected by the United States, P.R. China and
// * International copyright laws and is considered a trade secret belonging to
// * AbleSky, Inc. It is not to be divulged or used by parties who have not
// * received written authorization from AbleSky, Inc. AbleSky, Inc. 539 Chiquita
// * Ave Mountain View, CA 94041, USA http://www.ablesky.com Email:
// * support@ablesky.com Copyright 2006-2008 AbleSky, Inc. All rights reserved.
// */
//package com.ablesky.im.controller.buddy;
//
///**
// * @author lhe E-mail:lhe@ablesky.com
// */
//import java.io.BufferedReader;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.jivesoftware.smack.packet.Presence;
//import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
//
//import com.ablesky.im.pojo.Account;
//import com.ablesky.im.pojo.Authority;
//import com.ablesky.im.pojo.BuddyRelationsToDeal;
//import com.ablesky.im.pojo.SimpleAccount;
//import com.ablesky.im.service.accesscontrol.BuddyAccessControlToken;
//import com.ablesky.im.service.accesscontrol.InternalOrgThresholdAccessToken;
//import com.ablesky.im.service.account.IAccountService;
//import com.ablesky.im.service.buddy.IBuddyService;
//import com.ablesky.im.service.buddyRelations.IBuddyRelationsToDealService;
//import com.ablesky.im.web.cache.MemcachedNamespaceUtil;
//import com.ablesky.im.web.cache.XmemcachedUtil;
//import com.ablesky.im.web.dto.BuddyDTO;
//import com.ablesky.im.web.dto.BuddyRequestDTO;
//import com.ablesky.im.web.dto.BuddySimpleDTO;
//import com.ablesky.im.web.util.CommonStaticConst;
//import com.ablesky.im.web.util.JSONHelperUtil;
//import com.ablesky.im.web.util.LocalizationUtil;
//import com.ablesky.im.web.util.ParamUtils;
//import com.ablesky.im.web.util.SearchUser;
//import com.ablesky.im.web.util.StringUtil;
//import com.ablesky.im.web.util.WebUtil;
//import com.ablesky.im.xmpp.BlockedListCache;
//import com.ablesky.im.xmpp.SubscribeRequestEntry;
//import com.ablesky.im.xmpp.XMPPAdapter;
//import com.ablesky.im.xmpp.XMPPCacheManager;
//import com.ablesky.migration.sns.dto.RecommendBuddyDTO;
//
//public class BuddyController extends MultiActionController {
//	private IBuddyService buddyService;
//	private IAccountService accountService;
//	private IBuddyRelationsToDealService buddyRelationsToDealService;
//
//	public void setBuddyService(IBuddyService buddyService) {
//		this.buddyService = buddyService;
//	}
//
//	public void setAccountService(IAccountService accountService) {
//		this.accountService = accountService;
//	}
//	
//	public void setBuddyRelationsToDealService(IBuddyRelationsToDealService buddyRelationsToDealService) {
//		this.buddyRelationsToDealService = buddyRelationsToDealService;
//	}
//
//	private static final String START = "start";
//	private static final String LIMIT = "limit";
//	private static final String USERNAME = "userName";
//	private static final String BUDDYNAME = "buddyName";
//
//	private List<SubscribeRequestEntry> budddyRequestPageFilter(List<SubscribeRequestEntry> requests, int limit, int start) {
//		int startIndex = start;
//		List<SubscribeRequestEntry> retList = new ArrayList<SubscribeRequestEntry>();
//		int endIndex = Math.min(startIndex + limit, requests.size());
//		for (int i = startIndex; i < endIndex; i++) {
//			retList.add(requests.get(i));
//		}
//		return retList;
//	}
//
//	/**
//	 * List my buddies
//	 * 
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	@SuppressWarnings("unchecked")
//	public void listMyBuddies(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String jsonp = ParamUtils.getParameter(request, "callback");
//		String visitor = WebUtil.getUserId(request);
//		SimpleAccount account = accountService.getSimpleAccountByUserName(visitor);
//		if (account == null || SearchUser.checkIfIsGuest(visitor)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_2", request), jsonp, response);
//			return;
//		}
//		int start = ParamUtils.getIntParameter(request, START, 0);
//		int limit = ParamUtils.getIntParameter(request, LIMIT, Integer.MAX_VALUE);
//		boolean useSimple = ParamUtils.getBooleanParameter(request, "useSimple", false);
//		try {
//			// TODO 优化无需两次查询操作
//			if (useSimple) {
//				List<BuddySimpleDTO> dtos = buddyService.getBuddiesByAccount(account, start, limit);
//				int size = buddyService.getBuddiesCountByAccount(account, false);
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put(CommonStaticConst.TOTALCOUNT, size);
//				JSONHelperUtil.generateJsonByRemoteCall(dtos, map, jsonp, response);
//			} else {
//				Locale locale = LocalizationUtil.getLocale(request);
//				List<BuddyDTO> dtos = buddyService.getBuddiesByAccount(account, locale, start, limit);
//				int size = buddyService.getBuddiesCountByAccount(account, false);
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put(CommonStaticConst.TOTALCOUNT, size);
//				JSONHelperUtil.generateJsonByRemoteCall(dtos, map, jsonp, response);
//			}
//
//		} catch (Throwable e) {
//			JSONHelperUtil.outputExceptionToJSON(e, response);
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * List the requests which are that some people want to add you to buddy
//	 * list.
//	 * 
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	@SuppressWarnings("unchecked")
//	public void listMyBuddyRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String jsonp = ParamUtils.getParameter(request, "callback");
//		String userName = WebUtil.getUserId(request);
//		SimpleAccount account = accountService.getSimpleAccountByUserName(userName);
//		if (account == null || SearchUser.checkIfIsGuest(userName)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_2", request), jsonp, response);
//			return;
//		}
//		List<BuddyRequestDTO> dtos = new ArrayList<BuddyRequestDTO>();
//		int start = ParamUtils.getIntParameter(request, START, 0);
//		int limit = ParamUtils.getIntParameter(request, LIMIT, 0);
//		Locale locale = LocalizationUtil.getLocale(request);
//		try {
//			List<SubscribeRequestEntry> requests = buddyService.getBuddiesRequestFromXMPP(account);
//			// TODO 优化无需获取所有的好友请求
//			List<SubscribeRequestEntry> retList = this.budddyRequestPageFilter(requests, limit, start);
//			for (SubscribeRequestEntry brequest : retList) {
//				Account requester = accountService.findAccountByName(brequest.getFromUserName());
//				SimpleAccount simple_requester = new SimpleAccount();
//				simple_requester.setUsername(requester.getUsername());
//				if (requester != null) {
//					if (BlockedListCache.getInstance().checkIsBlocked(account.getUsername(), requester.getUsername())) {
//						buddyService.rejectBuddyRequestFromXMPP(account, simple_requester);
//					} else {
//						BuddyRequestDTO dto = requester.toBuddyRequestDTO(locale);
//						dtos.add(dto);
//					}
//				}
//			}
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put(CommonStaticConst.TOTALCOUNT, requests.size());
//			JSONHelperUtil.generateJsonByRemoteCall(dtos, map, jsonp, response);
//		} catch (Exception e) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, e.getMessage(), jsonp, response);
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Send a Buddy request
//	 * 
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	public void sendBuddyRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//
//		String buddyName = null;
//		BufferedReader reader = request.getReader();
//		if (ParamUtils.getParameter(request, "buddyName") != null){
//			buddyName = ParamUtils.getParameter(request, "buddyName");
//		}else{
//			String line = reader.readLine();
//			boolean readEnabled = false;
//			while (line != null) {
//				if (line.indexOf(BUDDYNAME) >= 0) {
//					readEnabled = true;
//					line = reader.readLine();
//					continue;
//				}
//				if (line.equalsIgnoreCase("")) {
//					line = reader.readLine();
//					continue;
//				}
//				if (readEnabled == true) {
//					buddyName = line.trim();
//					break;
//				}
//				line = reader.readLine();
//			}
//		}
//		
//
//		if (StringUtil.isNull(buddyName)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_14", request), jsonp, response);
//			return;
//		}
//		try {
//			SimpleAccount account = accountService.getSimpleAccountByUserName(WebUtil.getUserId(request));
//			// 游客不能加好友
//			if (account.getAccountAuths().contains(Authority.AUTH_GUEST)) {
//				JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Permission.deny", request), jsonp, response);
//				return;
//			}
//			// 内训用户不能加好友
//			if (account.isInternal()) {
//				JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Internal_Account_Permission.deny", request), jsonp, response);
//				return;
//			}
//
//			SimpleAccount buddy = accountService.getSimpleAccountByUserName(buddyName);
//			// 判断是否有加好友的权限
//			InternalOrgThresholdAccessToken iternalToken = accountService.getInternalOrgThresholdAccessToken(account, buddy, request);
//			if (!iternalToken.canAccess()) {
//				JSONHelperUtil.generateJsonByRemoteCall(false, iternalToken.getMessage(), jsonp, response);
//				return;
//			}
//
//			List<BuddyAccessControlToken.Type> type = new ArrayList<BuddyAccessControlToken.Type>();
//
//			type.add(BuddyAccessControlToken.Type.canSendBuddyRequest);
//
//			BuddyAccessControlToken token = buddyService.getAccessControlOfBuddy(account, buddy, type);
//			if (!token.getCanSendBuddyRequest()) {
//				JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_6", request), jsonp, response);
//				return;
//			}
//
//			buddyService.sendBuddyRequestToXMPP(request, account, buddy, true);
//			XmemcachedUtil cache = XmemcachedUtil.getInstance();
//			Object o = cache.get(MemcachedNamespaceUtil.RECOMMEND_BUDDYS + account.getUsername());
//			if (o != null) {
//				List<RecommendBuddyDTO> dtoList = (List<RecommendBuddyDTO>) o;
//				for (int i = 0; i < dtoList.size(); i++) {
//					if (buddyName.equals(dtoList.get(i).getUserName())) {
//						dtoList.remove(i);
//						break;
//					}
//				}
//				cache.set(MemcachedNamespaceUtil.RECOMMEND_BUDDYS + account.getUsername(), dtoList, MemcachedNamespaceUtil.RECOMMEND_BUDDYS_EXPIRY);
//			}
//			JSONHelperUtil.generateJsonByRemoteCall(true, LocalizationUtil.getClientString("Buddy_5", request), jsonp, response);
//		} catch (Exception e) {
//			JSONHelperUtil.outputOperationResultAsJSON(false, e.getMessage(), response);
//			e.printStackTrace();
//		} finally {
//			reader.close();
//		}
//
//	}
//
//	/**
//	 * agree a buddy request
//	 * 
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	public void receiveRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		String userName = request.getParameter(USERNAME);
//		if (StringUtil.isNull(userName)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_14", request), jsonp, response);
//			return;
//		}
//		SimpleAccount account = accountService.getSimpleAccountByUserName(WebUtil.getUserId(request));
//		SimpleAccount buddyAccount = accountService.getSimpleAccountByUserName(userName);
//		try {
//			List<BuddyAccessControlToken.Type> type = new ArrayList<BuddyAccessControlToken.Type>();
//			type.add(BuddyAccessControlToken.Type.canReceiveRequest);
//			BuddyAccessControlToken token = buddyService.getAccessControlOfBuddy(account, buddyAccount, type);
//			if (!token.getCanReceiveRequest()) {
//				JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_6", request), jsonp, response);
//				return;
//			}
//
//			buddyService.receiveBuddyRequestFromXMPP(request, account, buddyAccount, true);
//
//			buddyService.sendBuddyRequestToXMPP(request, account, buddyAccount, true);
//
//			JSONHelperUtil.generateJsonByRemoteCall(true, LocalizationUtil.getClientString("Buddy_7", request), jsonp, response);
//		} catch (Exception e) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, e.getMessage(), jsonp, response);
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * reject a buddy request
//	 * 
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	public void ignoreRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		String userName = request.getParameter(USERNAME);
//		if (StringUtil.isNull(userName)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_14", request), jsonp, response);
//			return;
//		}
//		SimpleAccount account = accountService.getSimpleAccountByUserName(WebUtil.getUserId(request));
//		SimpleAccount buddyAccount = accountService.getSimpleAccountByUserName(userName);
//		try {
//			List<BuddyAccessControlToken.Type> type = new ArrayList<BuddyAccessControlToken.Type>();
//			type.add(BuddyAccessControlToken.Type.canIgnoreRequest);
//			BuddyAccessControlToken token = buddyService.getAccessControlOfBuddy(account, buddyAccount, type);
//			if (token == null || !token.getCanIgnoreRequest()) {
//				JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_6", request), jsonp, response);
//				return;
//			}
//
//			buddyService.rejectBuddyRequestFromXMPP(account, buddyAccount);
//
//			JSONHelperUtil.generateJsonByRemoteCall(true, LocalizationUtil.getClientString("Buddy_8", request), jsonp, response);
//		} catch (Exception e) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, e.getMessage(), jsonp, response);
//			e.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * Ignore all buddy request
//	 * 
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	public void ignoreAllRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		SimpleAccount account = accountService.getSimpleAccountByUserName(WebUtil.getUserId(request));
//		if (account == null) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_4", request), jsonp, response);
//			return;
//		}
//		List<SubscribeRequestEntry> entryList = buddyService.getBuddiesRequestFromXMPP(account);
//		if (entryList == null || entryList.isEmpty()) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_15", request), jsonp, response);
//			return;
//		}
//
//		try {
//			for (SubscribeRequestEntry entry : entryList) {
//				SimpleAccount buddyAccount = accountService.getSimpleAccountByUserName(entry.getFromUserName());
//				if (buddyAccount == null) {
//					JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_14", request), jsonp, response);
//					continue;
//				}
//				buddyService.rejectBuddyRequestFromXMPP(account, buddyAccount);
//			}
//			JSONHelperUtil.generateJsonByRemoteCall(true, LocalizationUtil.getClientString("Buddy_8", request), jsonp, response);
//		} catch (Exception e) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, e.getMessage(), jsonp, response);
//			e.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * Remove my buddy.
//	 * 
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	public void removeBuddy(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		String buddyName = request.getParameter(BUDDYNAME);
//		if (StringUtil.isNull(buddyName)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_14", request), jsonp, response);
//			return;
//		}
//		SimpleAccount account = accountService.getSimpleAccountByUserName(WebUtil.getUserId(request));
//		SimpleAccount buddyAccount = accountService.getSimpleAccountByUserName(buddyName);
//		try {
//			List<BuddyAccessControlToken.Type> type = new ArrayList<BuddyAccessControlToken.Type>();
//			type.add(BuddyAccessControlToken.Type.canRemoveBuddy);
//			BuddyAccessControlToken token = buddyService.getAccessControlOfBuddy(account, buddyAccount, type);
//			if (token == null || !token.getCanRemoveBuddy()) {
//				JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_6", request), jsonp, response);
//				return;
//			}
//
//			buddyService.removeBuddyInXMPP(account, buddyAccount);
//
//			JSONHelperUtil.generateJsonByRemoteCall(true, LocalizationUtil.getClientString("Buddy_9", request), jsonp, response);
//		} catch (Exception e) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, e.getMessage(), jsonp, response);
//			e.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * Move my buddy to block users.
//	 * 
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	public void blockBuddy(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		String buddyName = request.getParameter(BUDDYNAME);
//		if (StringUtil.isNull(buddyName)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_14", request), jsonp, response);
//			return;
//		}
//		SimpleAccount account = accountService.getSimpleAccountByUserName(WebUtil.getUserId(request));
//		if (account.isInternal()) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Internal_Account_Permission", request), jsonp, response);
//			return;
//		}
//		SimpleAccount buddyAccount = accountService.getSimpleAccountByUserName(buddyName);
//		try {
//			List<BuddyAccessControlToken.Type> type = new ArrayList<BuddyAccessControlToken.Type>();
//			type.add(BuddyAccessControlToken.Type.canBlock);
//			BuddyAccessControlToken token = buddyService.getAccessControlOfBuddy(account, buddyAccount, type);
//			if (token == null || !token.getCanBlock()) {
//				JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_6", request), jsonp, response);
//				return;
//			}
//			buddyService.blockUser(account, buddyAccount);
//
//			JSONHelperUtil.generateJsonByRemoteCall(true, LocalizationUtil.getClientString("Buddy_10", request), jsonp, response);
//		} catch (Exception e) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, e.getMessage(), jsonp, response);
//			e.printStackTrace();
//		}
//	}
//
//	public void removeBlockedUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		String buddyName = request.getParameter(BUDDYNAME);
//		if (StringUtil.isNull(buddyName)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_14", request), jsonp, response);
//			return;
//		}
//		SimpleAccount account = accountService.getSimpleAccountByUserName(WebUtil.getUserId(request));
//		SimpleAccount buddyAccount = accountService.getSimpleAccountByUserName(buddyName);
//		try {
//			List<BuddyAccessControlToken.Type> type = new ArrayList<BuddyAccessControlToken.Type>();
//			type.add(BuddyAccessControlToken.Type.canRemoveBlock);
//			BuddyAccessControlToken token = buddyService.getAccessControlOfBuddy(account, buddyAccount, type);
//			if (!token.getCanRemoveBlock()) {
//				JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_6", request), jsonp, response);
//				return;
//			}
//			buddyService.deleteBlockedUser(account, buddyAccount);
//
//			JSONHelperUtil.generateJsonByRemoteCall(true, LocalizationUtil.getClientString("Buddy_10", request), jsonp, response);
//		} catch (Exception e) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, e.getMessage(), jsonp, response);
//			e.printStackTrace();
//		}
//	}
//
//	public void shareMyBuddy(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		String userName = WebUtil.getUserId(request);
//		Account account = accountService.findAccountByName(userName);
//		if (account == null || SearchUser.checkIfIsGuest(userName)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_2", request), jsonp, response);
//			return;
//		}
//		try {
//			JSONHelperUtil.generateJsonByRemoteCall(true, LocalizationUtil.getClientString("Buddy_11", request), jsonp, response);
//		} catch (Exception e) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, e.getMessage(), jsonp, response);
//			e.printStackTrace();
//		}
//	}
//
//	public void changePresence(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		SimpleAccount account = accountService.getSimpleAccountByUserName(WebUtil.getUserId(request));
//		String presenceMode = request.getParameter("presenceMode");
//		if (StringUtil.isNull(presenceMode)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_14", request), jsonp, response);
//			return;
//		}
//		try {
//			List<BuddyAccessControlToken.Type> type = new ArrayList<BuddyAccessControlToken.Type>();
//			type.add(BuddyAccessControlToken.Type.canChangePresence);
//			BuddyAccessControlToken token = buddyService.getAccessControlOfBuddy(account, account, type);
//			if (!token.getCanChangePresence()) {
//				JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_6", request), jsonp, response);
//				return;
//			}
//			buddyService.changePresence(account, Presence.Mode.valueOf(presenceMode), "");
//
//			JSONHelperUtil.generateJsonByRemoteCall(true, LocalizationUtil.getClientString("Buddy_12", request), jsonp, response);
//		} catch (Exception e) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, e.getMessage(), jsonp, response);
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Send more than one Buddy request
//	 * 
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	public void sendManyBuddyRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		String buddyNames = ParamUtils.getParameter(request, "uids");
//		String[] userNames = buddyNames.split(" ");
//
//		try {
//			Account account = accountService.findAccountByName(WebUtil.getUserId(request));
//			SimpleAccount simple_account = new SimpleAccount();
//			simple_account.setId(account.getId());
//			simple_account.setUsername(account.getUsername());
//			simple_account.setScreenName(account.getAdaptScreenName());
//			for (String buddyName : userNames) {
//				if (StringUtil.isNull(buddyName)) {
//					JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_14", request), jsonp, response);
//					return;
//				}
//				if (account.getAccountAuths().contains(Authority.AUTH_GUEST)) {
//					JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Permission.deny", request), jsonp, response);
//					return;
//				}
//				SimpleAccount buddy = accountService.getSimpleAccountByUserName(buddyName);
//				List<BuddyAccessControlToken.Type> type = new ArrayList<BuddyAccessControlToken.Type>();
//				type.add(BuddyAccessControlToken.Type.canSendBuddyRequest);
//				BuddyAccessControlToken token = buddyService.getAccessControlOfBuddy(simple_account, buddy, type);
//				if (!token.getCanSendBuddyRequest()) {
//					JSONHelperUtil.generateJsonByRemoteCall(false, LocalizationUtil.getClientString("Buddy_6", request), jsonp, response);
//					return;
//				}
//				buddyService.sendBuddyRequestToXMPP(request, simple_account, buddy, true);
//			}
//
//			JSONHelperUtil.generateJsonByRemoteCall(true, LocalizationUtil.getClientString("Buddy_5", request), jsonp, response);
//		} catch (Exception e) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, e.getMessage(), jsonp, response);
//			e.printStackTrace();
//		}
//
//	}
//	
//	/**
//	 * 根据BuddyRelationsToDeal(im_buddy_relations_to_deal_brtd)的信息批量添加好友
//	 */
//	public void batchDealBuddyRelations(HttpServletRequest request, HttpServletResponse response){
//		
//		Long id = ParamUtils.getLongParameter(request, "buddyRelationsId", 0);
//		if(id == 0){
//			JSONHelperUtil.outputOperationResultAsJSON(false, "参数有误", response);
//			return;
//		}
//		BuddyRelationsToDeal buddyRelations = buddyRelationsToDealService.getBuddyRelationsById(id);
//		if(buddyRelations == null){
//			JSONHelperUtil.outputOperationResultAsJSON(false, "未找到记录", response);
//			return;
//		}
//		String inviteeGroupName = buddyRelations.getGroupNameForInvitee();
//		String recipientGroupName = buddyRelations.getGroupNameForRecipient();
//		//-- to delete
//		if(StringUtil.isNull(inviteeGroupName)){
//			inviteeGroupName = ParamUtils.getParameter(request, "inviteeGroupName");
//		}
//		if(StringUtil.isNull(recipientGroupName)){
//			recipientGroupName = ParamUtils.getParameter(request, "recipientGroupName");
//		}
//		//--
//		if(!BuddyRelationsToDeal.STATUS_TODO.equals(buddyRelations.getStatus())){
//			JSONHelperUtil.outputOperationResultAsJSON(false, "不能对同一记录进行重复操作", response);
//			return;
//		}
//		List<String> inviteeUsernameList = buddyRelations.getInviteeUsernameList();
//		List<String> recipientUsernameList = buddyRelations.getRecipientUsernameList();
//		if(inviteeUsernameList == null || inviteeUsernameList.size() == 0
//				|| recipientUsernameList == null || recipientUsernameList.size() == 0) {
//			JSONHelperUtil.outputOperationResultAsJSON(false, "邀请人或接受人列表为空", response);
//			return;
//		}
//		// 获取双方的SimpleAccount
//		List<SimpleAccount> inviteeList = accountService.findSimpleAccountsByUsernameList(inviteeUsernameList);
//		if(inviteeList == null || inviteeList.size() == 0){
//			JSONHelperUtil.outputOperationResultAsJSON(false, "邀请人列表为空或数据有误", response);
//			return;
//		}
//		List<SimpleAccount> recipientList = accountService.findSimpleAccountsByUsernameList(recipientUsernameList);
//		if(recipientList == null || recipientList.size() == 0){
//			JSONHelperUtil.outputOperationResultAsJSON(false, "接受人列表为空或数据有误", response);
//			return;
//		}
//		// 记录加好友前双方通过本台tomcat在xmpp上的登录状态
//		XMPPCacheManager xmppCache = XMPPCacheManager.getInstance();
//		Map<String, Boolean> userOnlineStatusMap = new HashMap<String, Boolean>();
//		for(SimpleAccount invitee: inviteeList){
//			userOnlineStatusMap.put(invitee.getUsername(), xmppCache.contains(invitee.getUsername()));
//		}
//		for(SimpleAccount recipient: recipientList){
//			userOnlineStatusMap.put(recipient.getUsername(), xmppCache.contains(recipient.getUsername()));
//		}
//		// 获取双方的XMPPAdapter
//		Map<String, XMPPAdapter> inviteeAdapterMap = buddyService.getAdaptersBySimpleAccountList(inviteeList);
//		if(inviteeAdapterMap == null || inviteeAdapterMap.size() == 0){	//未连上xmpp的情形
//			JSONHelperUtil.outputOperationResultAsJSON(false, "xmpp适配器获取失败", response);
//			return;
//		} 
//		Map<String, XMPPAdapter> recipientAdapterMap =  buddyService.getAdaptersBySimpleAccountList(recipientList);
//		if(recipientAdapterMap == null || recipientAdapterMap.size() == 0){	//未连上xmpp的情形
//			JSONHelperUtil.outputOperationResultAsJSON(false, "xmpp适配器获取失败", response);
//			return;
//		}
//		// 批量添加好友，如未指定分组，则使用默认分组
//		try {
//			buddyService.batchSendBuddyRequestToXMPP(inviteeAdapterMap, recipientList, inviteeGroupName);
//			// 如果autoAccept == true, 则recipient自动接收请求并进行添加好友的反请求操作
//			if(buddyRelations.getAutoAccept()){
//				buddyService.batchSendBuddyRequestToXMPP(recipientAdapterMap, inviteeList, recipientGroupName);
//				buddyService.batchReceiveBuddyRequestFromXMPP(recipientList, recipientAdapterMap, inviteeList);
//			}
//			buddyRelations.setStatus(BuddyRelationsToDeal.STATUS_DONE);
//			buddyRelationsToDealService.saveOrUpdateBuddyRelations(buddyRelations);	//到底要不要在这里直接删掉buddyRelations
//			// 如果用户此前未通过当前tomcat登录xmpp服务器，则断开他的xmpp连接
//			for(Map.Entry<String, Boolean> entry: userOnlineStatusMap.entrySet()){
//				if(!entry.getValue()){
//					xmppCache.removeXmppAdapter(entry.getKey());
//				}
//			}
//			JSONHelperUtil.outputOperationResultAsJSON(true, "操作成功", response);
//			return;
//		} catch (Exception e){
//			e.printStackTrace();
//			JSONHelperUtil.outputOperationResultAsJSON(false, "操作失败", response);
//			return;
//		}
//	}
//	
//}
