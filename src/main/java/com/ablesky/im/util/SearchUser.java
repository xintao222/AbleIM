package com.ablesky.im.util;

//import com.ablesky.im.pojo.SimpleAccount;
//import com.ablesky.im.service.account.IAccountService;

/**
 * @author lhe E-mail:lhe@ablesky.com
 */
public class SearchUser {

//	public static SimpleAccount findAccountByName(String uname) {
//		IAccountService accountService = (IAccountService) BeanFactoryUtil.getBean("accountService");
//		return accountService.getSimpleAccountByUserName(uname);
//	}

	public static boolean checkIfIsGuest(String uname) {
		if (StringUtil.isNull(uname) || uname.equalsIgnoreCase(CommonStaticConst.GUEST)) {
			return true;
		}
		return false;
	}
}
