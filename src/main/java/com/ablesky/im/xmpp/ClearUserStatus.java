//package com.ablesky.im.xmpp;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
////import com.ablesky.im.pojo.Account;
////import com.ablesky.im.service.account.IAccountService;
////import com.ablesky.im.web.util.BeanFactoryUtil;
//
///**
// * @author lhe E-mail:lhe@ablesky.com
// */
//public class ClearUserStatus {
////	private static IAccountService accountService;
////	static {
////		accountService = (IAccountService) BeanFactoryUtil.getBean("accountService");
////	}
//	public static final Log log = LogFactory.getLog(ClearUserStatus.class.getName());
//
//	/**
//	 * 清除用户状态
//	 * 
//	 * @param userName
//	 */
//	public static void clear(String userName) {
//		if (userName == null || userName.equalsIgnoreCase("guest")) {
//			return;
//		}
//		log.info("clear " + userName);
//		try {
//			/**
//			 * 如果还存在该用户的session则不清除xmpp登录状态
//			 */
//			if (!OnlineStatusCache.getInstance().containsUsernameInUserSessionIdMapping(userName)) {
//				XMPPCacheManager.getInstance().removeXmppAdapter(userName);
//				OnlineStatusCache.getInstance().removeWebsiteOnlineUserByUsername(userName);
//			}
//
//			// 清除聊天信息
//			XMPPMessageManager.getInstance().removeMessagesByUserName(userName);
//			ChatListener.removeCallerList(userName);
//
//			// 删除在线信息
//			XMPPPresenceManager.getInstance().removeFromPresenceCenter(userName);
//			XMPPPresenceManager.getInstance().removePresenceCenterForStatusChange(userName);
//
//			// 删除聊天框显示状态信息
//			ChatBoxStateManager.getInstance().removeChatBoxState(userName);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//}
