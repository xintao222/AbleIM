//package com.ablesky.im.controller.server;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.InetSocketAddress;
//import java.net.URLDecoder;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
//
//import com.ablesky.im.pojo.Account;
//import com.ablesky.im.pojo.Authority;
//import com.ablesky.im.service.account.IAccountService;
//import com.ablesky.im.web.cache.XmemcachedUtil;
//import com.ablesky.im.web.dto.MemcachedServerSlabDTO;
//import com.ablesky.im.web.dto.MemcachedServerStatsDTO;
//import com.ablesky.im.web.util.JSONHelperUtil;
//import com.ablesky.im.web.util.ParamUtils;
//import com.ablesky.im.web.util.StringUtil;
//import com.ablesky.im.web.util.WebUtil;
//import com.ablesky.passport.web.cache.passport.PassportMemcachedUtil;
//
//public class ServerController extends MultiActionController {
//	public static final String Type_TTServer = "web_ttserver";
//	public static final String Type_MemcachedServer = "web_memcached";
//	public static final String Type_Passport = "passport";
//
//	private IAccountService accountService;
//
//	public void setAccountService(IAccountService accountService) {
//		this.accountService = accountService;
//	}
//
//	private boolean getAccessController(HttpServletRequest request) {
//		String username = WebUtil.getUserId(request);
//		if (username == null || username.equalsIgnoreCase(WebUtil.GUEST)) {
//			return false;
//		}
//		Account account = accountService.findAccountByName(username);
//		if (account.getAccountAuths().contains(Authority.AUTH_ADMIN_SERVER)) {
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * 显示当前Web服务器的缓存服务器信息
//	 * 
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	public void seversStat(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		boolean access = this.getAccessController(request);
//		if (access == false) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, "无访问权限", null, response);
//		}
//		String type = request.getParameter("select");
//		if (type == null) {
//			type = Type_MemcachedServer;
//		}
//		MemcachedServerStatsDTO dto = null;
//		List<MemcachedServerStatsDTO> dtos = new ArrayList<MemcachedServerStatsDTO>();
//		Map<InetSocketAddress, Map<String, String>> param = null;
//		Map<String, String> statsMap = null;
//		String maxBytes;
//		String bytes;
//		float usage;
//		try {
//			if (type.equalsIgnoreCase(Type_Passport)) {
//				PassportMemcachedUtil cache = PassportMemcachedUtil.getInstance();
//				param = cache.getStats();
//				statsMap = new HashMap<String, String>();
//				for (Map.Entry<InetSocketAddress, Map<String, String>> entry : param.entrySet()) {
//					dto = new MemcachedServerStatsDTO();
//					dto.setServer("passport:" + entry.getKey().toString().substring(1));
//					statsMap = entry.getValue();
//					dto.setConnectionCnt(statsMap.get("curr_connections"));
//					bytes = statsMap.get("bytes");
//					maxBytes = statsMap.get("limit_maxbytes");
//					usage = Float.valueOf(bytes) / Float.valueOf(maxBytes);
//					DecimalFormat fmt = new DecimalFormat("#0.00%");
//					dto.setMemoryUsage(fmt.format((usage)));
//					dtos.add(dto);
//				}
//			} else if (type.equalsIgnoreCase(Type_MemcachedServer)) {
//				XmemcachedUtil webCache = XmemcachedUtil.getInstance();
//				param = webCache.getStats();
//				webCache.getAvaliableServers();
//				statsMap = new HashMap<String, String>();
//				for (Map.Entry<InetSocketAddress, Map<String, String>> entry : param.entrySet()) {
//					dto = new MemcachedServerStatsDTO();
//					dto.setServer(entry.getKey().toString().substring(1));
//					statsMap = entry.getValue();
//					dto.setConnectionCnt(statsMap.get("curr_connections"));
//					bytes = statsMap.get("bytes");
//					maxBytes = statsMap.get("limit_maxbytes");
//					usage = Float.valueOf(bytes) / Float.valueOf(maxBytes);
//					DecimalFormat fmt = new DecimalFormat("#0.00%");
//					dto.setMemoryUsage(fmt.format((usage)));
//					dtos.add(dto);
//				}
//			} else if (type.equalsIgnoreCase(Type_TTServer)) {
//				XmemcachedUtil webCache = XmemcachedUtil.getInstance();
//				param = webCache.getStatsForTTServer();
//				for (InetSocketAddress address : param.keySet()) {
//					dto = new MemcachedServerStatsDTO();
//					statsMap = param.get(address);
//					dto.setServer(address.toString().substring(1));
//					dto.setConnectionCnt(statsMap.get("curr_items"));
//					dto.setMemoryUsage(calculateSize(statsMap.get("bytes")));
//					dto.setIsAvailable(true);
//					dto.setIsTTServer(false);
//					dtos.add(dto);
//				}
//			}
//			JSONHelperUtil.outputDTOToJSON(dtos, param, response);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return;
//	}
//
//	/**
//	 * 查看Cache服务器状态信息 JS形式
//	 * 
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	public void toViewServerStats(HttpServletRequest request, HttpServletResponse response) {
//		boolean access = this.getAccessController(request);
//		if (access == false) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, "无访问权限", null, response);
//		}
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		String username = WebUtil.getUserId(request);
//		if (username.equalsIgnoreCase(WebUtil.GUEST)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, "访客身份", jsonp, response);
//			return;
//		}
//		Account account = accountService.findAccountByName(username);
//		// 管理员 管理server服务器
//		if (account == null || !account.getAccountAuths().contains(Authority.AUTH_ADMIN_SERVER)) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, "权限错误", jsonp, response);
//			return;
//		}
//
//		String serverAddress = ParamUtils.getParameter(request, "address");
//		if (StringUtil.isNull(serverAddress)) {
//			return;
//		}
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("address", serverAddress);
//		if (serverAddress.indexOf("passport") > -1) {
//			serverAddress = serverAddress.substring(serverAddress.indexOf("passport") + 9);
//		}
//		BufferedReader reader = null;
//		try {
//			String perlPath = URLDecoder.decode(ServerController.class.getResource("/").getFile()) + "memcached-tool";
//			String cmd = "perl " + perlPath + " " + serverAddress;
//			try {
//				String sCmd = cmd + " stats";
//				Process prc = Runtime.getRuntime().exec(sCmd);
//				InputStream in = prc.getInputStream();
//				reader = new BufferedReader(new InputStreamReader(in));
//				String aLine = reader.readLine();
//				String preLine = null;
//				String sufLine = null;
//				aLine = reader.readLine();
//				while (aLine != null) {
//					String nameUrl = aLine.trim();
//					preLine = nameUrl.substring(0, nameUrl.lastIndexOf(' ')).trim();
//					sufLine = nameUrl.substring(nameUrl.lastIndexOf(' ') + 1).trim();
//					if (preLine.equals("limit_maxbytes") || preLine.equals("bytes")) {
//						sufLine = calculateSize(sufLine);
//					}
//					map.put(preLine, sufLine);
//					aLine = reader.readLine();
//				}
//
//				prc = Runtime.getRuntime().exec(cmd);
//				in = prc.getInputStream();
//				reader = new BufferedReader(new InputStreamReader(in));
//				aLine = reader.readLine();
//				MemcachedServerSlabDTO dto = null;
//				List<MemcachedServerSlabDTO> dtos = new ArrayList<MemcachedServerSlabDTO>();
//				String[] stats = null;
//				aLine = reader.readLine();
//				while (aLine != null) {
//					String nameUrl = aLine.trim();
//					if (("").equals(nameUrl)) {
//						break;
//					}
//					stats = nameUrl.split("\\s+");
//					dto = new MemcachedServerSlabDTO();
//					dto.setClassNo(stats[0]);
//					dto.setChunkSize(stats[1]);
//					dto.setPageCnt(stats[3]);
//					dto.setItemCnt(stats[4]);
//					dtos.add(dto);
//					aLine = reader.readLine();
//				}
//				map.put("success", Boolean.TRUE);
//				JSONHelperUtil.outputDTOToJSON(dtos, map, response);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (reader != null) {
//					reader.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return;
//	}
//
//	/**
//	 * 显示当前服务器的缓存服务器连接数
//	 * 
//	 * @param request
//	 * @param response
//	 */
//	public void toShowWebServerProfile(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		boolean access = this.getAccessController(request);
//		if (access == false) {
//			JSONHelperUtil.generateJsonByRemoteCall(false, "无访问权限", null, response);
//		}
//		String select = ParamUtils.getParameter(request, "select");
//		String jsonp = ParamUtils.getParameter(request, "jsonp");
//		if (select == null) {
//			return;
//		}
//		int total = 0;
//		if (select.equalsIgnoreCase(Type_MemcachedServer)) {
//			XmemcachedUtil webCache = XmemcachedUtil.getInstance();
//			total = webCache.getAvaliableServers().size();
//		} else if (select.equalsIgnoreCase(Type_TTServer)) {
//			XmemcachedUtil webCache = XmemcachedUtil.getInstance();
//			total = webCache.getAvaliableServersOfTTSERVER().size();
//
//		} else if (select.equalsIgnoreCase(Type_Passport)) {
//			PassportMemcachedUtil cache = PassportMemcachedUtil.getInstance();
//			total = cache.getAvaliableServers().size();
//		}
//		Map<String, Object> metaData = new HashMap<String, Object>();
//		metaData.put("total", total);
//		JSONHelperUtil.generateJsonByRemoteCall(true, "", jsonp, metaData, response);
//		return;
//	}
//
//	private String calculateSize(String bytes) {
//		DecimalFormat dcmFmt = new DecimalFormat("0.00");
//		float size = Float.valueOf(bytes);
//		Boolean flag = Boolean.TRUE;
//		int unit = 0;
//		while (flag) {
//			if (size / 1024 < 1) {
//				break;
//			}
//			size = size / 1024;
//			unit++;
//			if (size <= 1024) {
//				flag = Boolean.FALSE;
//			}
//		}
//		switch (unit) {
//		case 0:
//			bytes = dcmFmt.format(size) + "B";
//			break;
//		case 1:
//			bytes = dcmFmt.format(size) + "KB";
//			break;
//		case 2:
//			bytes = dcmFmt.format(size) + "MB";
//			break;
//		case 3:
//			bytes = dcmFmt.format(size) + "GB";
//			break;
//		case 4:
//			bytes = dcmFmt.format(size) + "TB";
//			break;
//		}
//		return bytes;
//	}
//}
