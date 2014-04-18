package com.ablesky.im.controller.online;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

//import com.ablesky.im.pojo.Authority;
//import com.ablesky.im.pojo.SimpleAccount;
//import com.ablesky.im.service.account.IAccountService;
//import com.ablesky.im.dto.UserDTO;
import com.ablesky.im.util.JSONHelperUtil;
import com.ablesky.im.util.ParamUtils;
import com.ablesky.im.util.WebUtil;
//import com.ablesky.im.xmpp.OnlineStatusCache;

/**
 * @author lhe E-mail: lhe@ablesky.com
 */
@Controller
@RequestMapping("Online.do")
public class OnlineController {
//	private IAccountService accountService;
//
//	public IAccountService getAccountService() {
//		return accountService;
//	}
//
//	public void setAccountService(IAccountService accountService) {
//		this.accountService = accountService;
//	}
//
//	public ModelAndView listOnlineUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		// 权限控制
//		String visitor = WebUtil.getUserId(request);
//		SimpleAccount account = accountService.getSimpleAccountByUserName(visitor);
//		if (account == null || !account.getAccountAuths().contains(Authority.AUTH_ADMIN)) {
//			return null;
//		}
//		// 所有用户
//		List<String> onlineUserList = OnlineStatusCache.getInstance().getAllOnlineUser();
//		List<UserDTO> dtos = new ArrayList<UserDTO>();
//		for (int i = 0; i < onlineUserList.size(); i++) {
//			dtos.add(new UserDTO(onlineUserList.get(i)));
//		}
//
//		// 本地session 与登录用户
//		Map<String, String> sessionMap = OnlineStatusCache.getInstance().getUserSessionIdMapping();
//		List<UserDTO> localSessionDtos = new ArrayList<UserDTO>();
//		for (String key : sessionMap.keySet()) {
//			localSessionDtos.add(new UserDTO(sessionMap.get(key), key));
//		}
//
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("onlineUsers", dtos);
//		map.put("onlineUsersSize", dtos.size());
//
//		map.put("localSession", localSessionDtos);
//		map.put("localSessionSize", localSessionDtos.size());
//		return new ModelAndView("online/list", map);
//	}
//
//	public void getCurrentUserClientOnline(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String userName = WebUtil.getUserId(request);
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		try {
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("success", OnlineStatusCache.getInstance().queryClientOnlineUserByUsername(userName));
//			JSONHelperUtil.outputMapToJSON(map, response);
//			JSONHelperUtil.generateJsonByRemoteCall(true, "", jsonp, map, response);
//		} catch (Exception e) {
//			JSONHelperUtil.outputExceptionToJSON(e, response);
//			e.printStackTrace();
//		}
//	}
//
//	public void checkClientOnlineByUserName(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		String userName = ParamUtils.getParameter(request, "userName");
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("success", OnlineStatusCache.getInstance().queryClientOnlineUserByUsername(userName));
//		JSONHelperUtil.generateJsonByRemoteCall(true, "", jsonp, map, response);
//	}
//
//	public void checkWebOnlineByUserName(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		String userName = ParamUtils.getParameter(request, "userName");
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("success", OnlineStatusCache.getInstance().queryWebsiteOnlineUserByUsername(userName));
//		JSONHelperUtil.outputMapToJSON(map, response);
//		JSONHelperUtil.generateJsonByRemoteCall(true, "", jsonp, map, response);
//	}

}
