//package com.ablesky.im.controller.server;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.io.RandomAccessFile;
//import java.net.InetSocketAddress;
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
//import com.ablesky.im.web.util.JSONHelperUtil;
//import com.ablesky.im.web.util.ParamUtils;
//import com.ablesky.passport.web.cache.passport.PassportMemcachedUtil;
//
///**
// * 添加memcached服务器
// * 
// * @author kbian
// */
//public class UpdateServerController extends MultiActionController {
//	public static String SERVER_TYPE_PASSPORT = "MServerForPassport";
//	public static String SERVER_TYPE_WEB = "MServerForWeb";
//	// 木有在配置文件中注册
//	private IAccountService accountService;
//
//	public void setAccountService(IAccountService accountService) {
//		this.accountService = accountService;
//	}
//
//	/**
//	 * 代码木有逻辑问题了，木有测试，木有些参数错误后的处理代码
//	 * 
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	public void addMemcachedServer(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		/**
//		 * 身份验证 必须是管理员 需要加入安全机制
//		 */
//		String username = ParamUtils.getParameter(request, "username");
//		String password = ParamUtils.getParameter(request, "password");
//		String type = ParamUtils.getParameter(request, "type");
//		if (username == null || password == null || type == null) {
//			JSONHelperUtil.outputOperationResultAsJSON(false, "用户名，密码或操作类型为空", response);
//			return;
//		}
//		Account account = accountService.findAccountByName(username);
//		// 管理员 管理server服务器
//		if (account == null || !account.getPassword().equalsIgnoreCase(password) || !account.getAccountAuths().contains(Authority.AUTH_ADMIN_SERVER)) {
//			JSONHelperUtil.outputOperationResultAsJSON(false, "权限错误", response);
//			return;
//		}
//		/**
//		 * 添加服务器
//		 */
//		String ip = ParamUtils.getParameter(request, "ip");
//		int port = ParamUtils.getIntParameter(request, "port", 0);
//		if (ip == null || port == 0) {
//			JSONHelperUtil.outputOperationResultAsJSON(false, "参数为空", response);
//			return;
//		}
//		PrintWriter writer = response.getWriter();
//		try {
//			if (SERVER_TYPE_PASSPORT.equals(type)){
//				PassportMemcachedUtil cache = PassportMemcachedUtil.getInstance();
//				cache.addServer(new InetSocketAddress(ip, port));
//				// 输出日志
//				this.writeLog(request, response, ip, port);
//			}else if (SERVER_TYPE_WEB.equals(type)){
//				XmemcachedUtil cache = new XmemcachedUtil();
//				cache.addServer(new InetSocketAddress(ip, port));
//				// 输出日志
//				this.writeAddLogForWeb(ip, port, Boolean.FALSE);
//			} else {
//				XmemcachedUtil cache = new XmemcachedUtil();
//				cache.addServer(new InetSocketAddress(ip, port), Boolean.TRUE);
//				// 输出日志
//				this.writeAddLogForWeb(ip, port, Boolean.TRUE);
//			}
//			// 操作成功 TODO 只考虑由于异常问题导致的失败，1.0未考虑内部机制问题
//			writer.write("操作成功");
//		} catch (Exception e) {
//			// 操作失败
//			e.printStackTrace();
//			writer.write("操作失败");
//		} finally {
//			writer.close();
//		}
//		// JSONHelperUtil.outputOperationResultAsJSON(true, "操作成功", response);
//		return;
//	}
//
//	/**
//	 * 删除server
//	 */
//	public void deleteMemcachedServer(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		/**
//		 * 身份验证 必须是管理员 需要加入安全机制
//		 */
//		String username = ParamUtils.getParameter(request, "username");
//		String password = ParamUtils.getParameter(request, "password");
//		String type = ParamUtils.getParameter(request, "type");
//		if (username == null || password == null || type == null) {
//			JSONHelperUtil.outputOperationResultAsJSON(false, "用户名，密码或操作类型为空", response);
//			return;
//		}
//		Account account = accountService.findAccountByName(username);
//		// 管理员 管理server服务器
//		if (account == null || !account.getPassword().equalsIgnoreCase(password) || !account.getAccountAuths().contains(Authority.AUTH_ADMIN_SERVER)) {
//			JSONHelperUtil.outputOperationResultAsJSON(false, "权限错误", response);
//			return;
//		}
//		/**
//		 * 删除服务器
//		 */
//		String ip = ParamUtils.getParameter(request, "ip");
//		int port = ParamUtils.getIntParameter(request, "port", 0);
//		if (ip == null || port == 0) {
//			JSONHelperUtil.outputOperationResultAsJSON(false, "参数为空", response);
//			return;
//		}
//		PrintWriter writer = response.getWriter();
//		try {
//			if (SERVER_TYPE_PASSPORT.equals(type)){
//				PassportMemcachedUtil cache = PassportMemcachedUtil.getInstance();
//				cache.deleteServer(ip + ":" + port);
//			} else if (SERVER_TYPE_WEB.equals(type)){
//				XmemcachedUtil cache = new XmemcachedUtil();
//				cache.deleteServer(ip + ":" + port);
//			} else {
//				XmemcachedUtil cache = new XmemcachedUtil();
//				cache.deleteServer(ip + ":" + port, Boolean.TRUE);
//			}
//			// 输出日志
//			this.writeLogForDelete(ip, port, type);
//			// 操作成功 TODO 只考虑由于异常问题导致的失败，1.0未考虑内部机制问题
//			writer.write("操作成功");
//		} catch (Exception e) {
//			// 操作失败
//			e.printStackTrace();
//			writer.write("操作失败");
//		} finally {
//			writer.close();
//		}
//		// JSONHelperUtil.outputOperationResultAsJSON(true, "操作成功", response);
//		return;
//	}
//
//	// 写入日志中
//	private void writeLog(HttpServletRequest request, HttpServletResponse response, String ip, int port) throws Exception {
//		String osName = System.getProperty("os.name");
//		File out_file = null;
//		File in_file = null;
//		File in_file_bak = null;
//		RandomAccessFile raf = null;
//		BufferedWriter in_bw = null;
//		if (osName != null && osName.toLowerCase().indexOf("linux") >= 0) {
//			try {
//				/**
//				 * 初始化文件
//				 */
//				out_file = new File(System.getProperty("catalina.home") + "/logs/" + "monitoring_passport_memcached_servers.txt");
//				if (out_file.exists() == false) {
//					out_file.createNewFile();
//				}
//				raf = new RandomAccessFile(out_file, "rw");
//				in_file = new File(System.getProperty("catalina.home") + "/conf/" + "passportMemcachedServer.properties");
//				in_file_bak = new File(System.getProperty("catalina.home") + "/conf/" + "passportMemcachedServer_bak.properties");
//				if (in_file.exists() == false) {
//					throw new Exception("the in_file is null!");
//				}
//				if (in_file_bak.exists() == true) {
//					in_file_bak.delete();
//				}
//				in_file_bak.createNewFile();
//				/**
//				 * 对out_file进行输入操作；对in_file进行更新操作
//				 */
//				BufferedReader in_br = new BufferedReader(new FileReader(in_file));
//				in_bw = new BufferedWriter(new FileWriter(in_file_bak));
//				String server = ip + ":" + port;
//				String line = null;
//				while ((line = in_br.readLine()) != null) {
//					if (line.indexOf("PASSPORT_MEMCACHED_SERVERS=") >= 0) {
//						if (line.indexOf(",") >= 0) {
//							if (line.indexOf("," + server) == -1 && line.indexOf(server + ",") == -1) {
//								in_bw.write(line + ',' + server);
//								in_bw.newLine();
//								raf.seek(raf.length());
//								raf.writeBytes("\n" + line + ',' + server);
//								break;
//							}
//						} else {
//							if (line.indexOf(server) == -1) {
//								in_bw.write(line + ',' + server);
//								in_bw.newLine();
//								raf.seek(raf.length());
//								raf.writeBytes("\n" + line + ',' + server);
//								break;
//							}
//						}
//					}
//					in_bw.write(line);
//					in_bw.newLine();
//				}
//				while ((line = in_br.readLine()) != null) {
//					in_bw.write(line);
//					in_bw.newLine();
//				}
//
//			} catch (Exception e) {
//
//			} finally {
//				if (raf != null) {
//					raf.close();
//				}
//				if (in_bw != null) {
//					in_bw.flush();
//					in_bw.close();
//				}
//				if (in_file.exists() && in_file_bak.exists()) {
//					in_file.delete();
//					in_file_bak.renameTo(new File(System.getProperty("catalina.home") + "/conf/" + "passportMemcachedServer.properties"));
//				}
//			}
//		} else {
//			// windows环境下不做操作
//		}
//	}
//	
//	// 添加网站服务器写入日志中
//	private void writeAddLogForWeb(String ip, int port, Boolean useTokyoTyrant) throws Exception {
//		String osName = System.getProperty("os.name");
//		File out_file = null;
//		File in_file = null;
//		RandomAccessFile raf = null;
//		PrintWriter pw = null;
//		if (osName != null && osName.toLowerCase().indexOf("linux") >= 0) {
//			try {
//				/**
//				 * 初始化文件
//				 */
//				out_file = new File(System.getProperty("catalina.home") + "/logs/" + "monitoring_memcached_servers.txt");
//				if (out_file.exists() == false) {
//					out_file.createNewFile();
//				}
//				raf = new RandomAccessFile(out_file, "rw");
//				in_file = new File(System.getProperty("catalina.home") + "/conf/" + "AbleSkyImConfig.properties");
//				if (in_file.exists() == false) {
//					throw new Exception("the in_file is null!");
//				}
//				FileInputStream fis = new FileInputStream(in_file);
//				InputStreamReader isr = new InputStreamReader(fis);
//				BufferedReader br = new BufferedReader(isr);
//				StringBuffer buf = new StringBuffer();
//				String serverName = "MEMCACHED_SERVER=";
//				if (useTokyoTyrant){
//					serverName = "TOKYO_TYRANT_SERVER=";
//				} 
//				// 保存该行前面的内容
//				String server = ip + ":" + port;
//				String line = null;
//				for (int j = 1; (line = br.readLine()) != null && line.indexOf(serverName) < 0; j++) {
//					buf = buf.append(line);
//					buf = buf.append(System.getProperty("line.separator"));
//				}
//				
//				// 添加server
//				if (line.indexOf(",") >= 0) {
//					if (line.indexOf("," + server) == -1 && line.indexOf(server + ",") == -1) {
//						buf = buf.append(line + ',' + server);
//						raf.seek(raf.length());
//						raf.writeBytes("\n\r" + line + " add " + server);
//					}
//				} else {
//					if (line.indexOf(server) == -1) {
//						buf = buf.append(line + ',' + server);
//						raf.seek(raf.length());
//						raf.writeBytes("\n\r" + line + " add " + server);
//					}
//				}
//				
//				// 保存该行后面的内容
//				while ((line = br.readLine()) != null) {
//					buf = buf.append(System.getProperty("line.separator"));
//					buf = buf.append(line);
//				}
//				
//				br.close();
//				FileOutputStream fos = new FileOutputStream(in_file);
//				pw = new PrintWriter(fos);
//				pw.write(buf.toString().toCharArray());
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (raf != null) {
//					raf.close();
//				}
//				if (pw != null) {
//					pw.flush();
//					pw.close();
//				}
//			}
//		} else {
//			// windows环境下不做操作
//		}
//	}
//	
//	private void writeLogForDelete(String ip, int port, String type) throws Exception {
//		String osName = System.getProperty("os.name");
//		File out_file = null;
//		File in_file = null;
//		RandomAccessFile raf = null;
//		PrintWriter pw = null;
//		if (osName != null && osName.toLowerCase().indexOf("linux") >= 0) {
//			try {
//				/**
//				 * 初始化文件
//				 */
//				out_file = new File(System.getProperty("catalina.home") + "/logs/" + "monitoring_memcached_servers.txt");
//				if (out_file.exists() == false) {
//					out_file.createNewFile();
//				}
//				raf = new RandomAccessFile(out_file, "rw");
//				// 保存该行前面的内容
//				String server = ip + ":" + port;
//				String serverName;
//				if (SERVER_TYPE_PASSPORT.equals(type)){
//					in_file = new File(System.getProperty("catalina.home") + "/conf/" + "passportMemcachedServer.properties");
//					serverName = "PASSPORT_MEMCACHED_SERVERS=";
//				} else {
//					in_file = new File(System.getProperty("catalina.home") + "/conf/" + "AbleSkyImConfig.properties");
//					serverName = "MEMCACHED_SERVER=";
//					if ("TTServer".equals(type)){
//						serverName = "TOKYO_TYRANT_SERVER=";
//					}
//				}
//				if (in_file.exists() == false) {
//					throw new Exception("the in_file is null!");
//				}
//				FileInputStream fis = new FileInputStream(in_file);
//				InputStreamReader isr = new InputStreamReader(fis);
//				BufferedReader br = new BufferedReader(isr);
//				StringBuffer buf = new StringBuffer();
//				String line = null;
//				for (int j = 1; (line = br.readLine()) != null && line.indexOf(serverName) < 0; j++) {
//					buf = buf.append(line);
//					buf = buf.append(System.getProperty("line.separator"));
//				}
//				
//				// 删除server
//				if (line.indexOf(server + ",") > -1) {
//					buf = buf.append(line.replace(server + ",", ""));
//					raf.seek(raf.length());
//					raf.writeBytes("\n\r" + line + " delete " + server);
//				} else if (line.indexOf(server) > -1){
//					String tempLine = line.replace(server, "");
//					char c = tempLine.charAt(tempLine.length() - 1);
//					if (c == ',') {
//						tempLine = tempLine.substring(0, tempLine.length() - 1);
//					}
//					buf = buf.append(tempLine);
//					raf.seek(raf.length());
//					raf.writeBytes("\n\r" + line + " delete "  + server);
//				} else {
//					buf = buf.append("\n\r" + line);
//				}
//				
//				// 保存该行后面的内容
//				while ((line = br.readLine()) != null) {
//					buf = buf.append(System.getProperty("line.separator"));
//					buf = buf.append(line);
//				}
//				
//				br.close();
//				FileOutputStream fos = new FileOutputStream(in_file);
//				pw = new PrintWriter(fos);
//				pw.write(buf.toString().toCharArray());
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (raf != null) {
//					raf.close();
//				}
//				if (pw != null) {
//					pw.flush();
//					pw.close();
//				}
//			}
//		} else {
//			// windows环境下不做操作
//		}
//	}
//}
