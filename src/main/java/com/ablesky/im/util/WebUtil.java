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

import javax.servlet.http.HttpServletRequest;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import com.ablesky.im.pojo.Account;
//import com.ablesky.im.pojo.OrganizationDescendantUser;
//import com.ablesky.im.pojo.SimpleAccount;
//import com.ablesky.im.service.organization.IOrganizationService;

public class WebUtil {
	public static final String GUEST = "GUEST";
	private static Log log = LogFactory.getLog(WebUtil.class);

	protected static Log logger = LogFactory.getLog(WebUtil.class);

	public static String getUserId(HttpServletRequest request) {
		// FIXME fix it further
		String userName = GUEST;
		SecurityContext ctx = SecurityContextHolder.getContext();
		log.debug("ctx:" + ctx);
		if (ctx != null) {
			Authentication auth = ctx.getAuthentication();
			log.debug("auth:" + auth);
			if (auth != null) {
				Object principal = auth.getPrincipal();
				if (principal instanceof UserDetails) {
					userName = ((UserDetails) principal).getUsername();
				}
			}
		}
		log.debug("username:" + userName);
		return userName;
	}

	public static boolean isGuest() {
		return getUserId(null).equalsIgnoreCase(GUEST);
	}

//	public static String getScreenName(String userName) {
//		SimpleAccount account = SearchUser.findAccountByName(userName);
//		if (account != null) {
//			String screenName = account.getAdaptScreenName();
//			if (screenName == null || screenName.trim().equals("")) {
//				screenName = userName;
//			}
//			return screenName;
//		} else
//			return userName;
//	}
//
//	/**
//	 * return organization id which the account belong to,if the account don't
//	 * belong to any organization,return 0.
//	 * 
//	 * @param account
//	 * @return
//	 */
//	public static long getOrganizationId(SimpleAccount account) {
//		IOrganizationService organizationService = (IOrganizationService) BeanFactoryUtil.getBean("organizationService");
//		OrganizationDescendantUser descendantUser = organizationService.getOrganizationDescendantUserByAccountId(account.getId());
//		if (descendantUser != null)
//			return descendantUser.getOrganization().getId();
//		else
//			return 0;
//	}

}
