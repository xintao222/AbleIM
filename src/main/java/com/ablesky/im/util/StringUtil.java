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

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

public final class StringUtil {
	protected static Log logger = LogFactory.getLog(StringUtil.class);
	public static final int EXPERIENCE_DISPLAY_LENGTH = 80;
	private static final Pattern ipPattern = Pattern.compile("^\\D*([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}).*");
	private static final Pattern domainPattern = Pattern.compile("(?<=.*?)(\\.[^\\.]*+\\.)(com|net|org|gov|cc|biz|info|cn|co|edu)(\\.(cn|hk|uk|jp|tw))*");

	public static String encodingFileName(String fileName) {
		String returnFileName = "";
		try {
			returnFileName = URLEncoder.encode(fileName, "UTF-8");
			returnFileName = StringUtils.replace(returnFileName, "+", "%20");
			returnFileName = new String(fileName.getBytes("GB2312"), "ISO8859-1");
			returnFileName = StringUtils.replace(returnFileName, " ", "%20");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			if (logger.isWarnEnabled()) {
				logger.info("Don't support this encoding ...");
			}
		}
		return returnFileName;
	}

	public static int parseInteger(String str, int defaultNum) {
		if (isNumeric(str)) {
			int num = defaultNum;
			try {
				num = Integer.parseInt(str);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultNum;
		}
	}

	public static long parseLong(String arg, long defaultNum) {
		if (isNumeric(arg)) {
			long num = defaultNum;
			try {
				num = Long.parseLong(arg);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultNum;
		}
	}

	public static boolean parseBoolean(String arg, boolean defaultNum) {
		if (arg == null) {
			return defaultNum;
		} else if (arg.equalsIgnoreCase("true")) {
			return true;
		} else if (arg.equalsIgnoreCase("false")) {
			return false;
		} else {
			return defaultNum;
		}
	}

	public static boolean isNumeric(String str) {
		if (isNull(str))
			return false;
		Pattern pattern = Pattern.compile("[0-9]+");
		return pattern.matcher(str).matches();
	}

	public static boolean batchValidateStr(List<String> strList) {
		for (int i = 0; i < strList.size(); i++) {
			if (!isNumeric(strList.get(i)))
				return false;
		}
		return true;
	}

	public static boolean batchValidateStr(String str[]) {
		for (int i = 0; i < str.length; i++) {
			if (!isNumeric(str[i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNull(String str) {
		return (str == null) || (str.trim().length() == 0);
	}

	/**
	 * 判断邮箱是否合法
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		Pattern pattern = Pattern.compile("^(([0-9a-zA-Z]+)|([0-9a-zA-Z]+[_.0-9a-zA-Z-]*[0-9a-zA-Z]+))@([a-zA-Z0-9-]+[.])+([a-zA-Z]{2}|net|com|gov|mil|org|edu|int)$");
		Matcher matcher = pattern.matcher(email);
		if (matcher.matches()) {
			return true;
		} else
			return false;
	}

	/**
	 * 判断QQ是否合法
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isQQ(String QQ) {
		Pattern pattern = Pattern.compile("[1-9][0-9]{4,19}");
		Matcher matcher = pattern.matcher(QQ);
		if (matcher.matches()) {
			return true;
		} else
			return false;
	}

	/**
	 * 判断身份证是否合�?
	 * 
	 * @param IDNo
	 * @return
	 */
	public static boolean isIDNo(String IDNo) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9]{1,30}");
		Matcher matcher = pattern.matcher(IDNo);
		if (matcher.matches()) {
			return true;
		} else
			return false;
	}

	/**
	 * 判断昵称是否合法
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isRealName(String realName) {
		if (realName.length() > 20)
			return false;
		Pattern pattern = Pattern.compile("([[a-zA-Z0-9\u4E00-\u9FA5]{1}][a-zA-Z0-9_\\-\u4E00-\u9FA5]{0,19})|(\\-{1}[a-zA-Z0-9_\\-\u4E00-\u9FA5]*[a-zA-Z0-9\u4E00-\u9FA5]+[a-zA-Z0-9_\\-\u4E00-\u9FA5]*)");
		Matcher matcher = pattern.matcher(realName);
		if (matcher.matches()) {
			return true;
		} else
			return false;
	}

	/**
	 * 判断地址是否合法
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isAddress(String address) {
		Pattern pattern = Pattern.compile("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#�?…�?&*（）—�?+|{}【�?‘；：�?“�?。，、？]");
		Matcher matcher = pattern.matcher(address);
		return !matcher.find();
	}

	/**
	 * 判断邮编是否合法
	 * 
	 * @param postcode
	 * @return
	 */
	public static boolean isPostcode(String postcode) {
		Pattern pattern = Pattern.compile("[0-9]{6}");
		Matcher matcher = pattern.matcher(postcode);
		if (matcher.matches()) {
			return true;
		} else
			return false;
	}

	/**
	 * 判断电话号码是否合法
	 * 
	 * @param postcode
	 * @return
	 */
	public static boolean isPhoneNumber(String phonenumber) {
		Pattern pattern = Pattern.compile("((^(13[0-9]|15[0-3]|15[5-9]|18[0-9])[0-9]{8}$)|(^0[1,2]{1}\\d{1}(-|_)?\\d{8}$)|(^0[3-9]{1}\\d{2}(-|_)?\\d{7,8}$)|(^0[1,2]{1}\\d{1}(-|_)?\\d{8}(-|_)(\\d{1,4})$)|(^0[3-9]{1}\\d{2}(-|_)?\\d{7,8}(-|_)(\\d{1,4})$))");
		Matcher matcher = pattern.matcher(phonenumber);
		if (matcher.matches()) {
			return true;
		} else
			return false;
	}

	/**
	 * 是否是默认输�?
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isDefaultInput(String s) {
		Pattern pattern = Pattern.compile("\\-+");
		Matcher matcher = pattern.matcher(s);
		if (matcher.matches()) {
			return true;
		} else
			return false;
	}

	public static String getFileMimeType(String fileName) {
		if (isNull(fileName) || !fileName.contains(".")) {
			return "";
		} else {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		}
	}

	public static String getShortFileName(String fileName) {
		if (!isNull(fileName)) {
			String oldFileName = new String(fileName);
			fileName = fileName.replace('\\', '/');
			if (fileName.endsWith("/")) {
				int idx = fileName.indexOf('/');
				if (idx == -1 || idx == fileName.length() - 1) {
					return oldFileName;
				} else {
					return oldFileName.substring(idx + 1, fileName.length() - 1);
				}

			}
			if (fileName.lastIndexOf("/") > 0) {
				fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
			}

			return fileName;
		}
		return "";
	}

	/*
	 * public static String getPrefixFileName(String fileName) { if
	 * (!isNull(fileName)) { if (fileName.lastIndexOf(".") > 0) { fileName =
	 * fileName.substring(0, fileName.lastIndexOf(".")); } return fileName; }
	 * return ""; }
	 */
	public static String getPrefixFileName(String fileName) {
		String sub = "";
		String subSon = "";
		if (!isNull(fileName)) {
			if (fileName.lastIndexOf(".") > 0) {
				sub = fileName.substring(0, fileName.lastIndexOf("."));
				if (sub.lastIndexOf(".") < 0) {
					fileName = sub;
				} else {
					String suffix = fileName.substring(sub.lastIndexOf("."));
					if (suffix.equalsIgnoreCase(".asc.flv") || suffix.equalsIgnoreCase(".audioc.flv")) {
						subSon = sub.substring(0, sub.lastIndexOf("."));
						if (subSon.lastIndexOf(".") > 0) {
							subSon = subSon.substring(0, subSon.lastIndexOf("."));
							fileName = subSon;
						} else {
							fileName = subSon;
						}
					} else if (suffix.contains(".flv")) {
						subSon = sub.substring(0, sub.lastIndexOf("."));
						fileName = subSon;
					} else {
						fileName = sub;
					}
				}
			}
			return fileName;
		}
		return "";
	}

	public static String getDirectoryName(String filePath) {
		if (isNull(filePath)) {
			return "";
		}
		return filePath.substring(0, filePath.lastIndexOf("/"));
	}

	public static String getOriginalFilePath(String filePath) {
		String fileName = getShortFileName(filePath);
		if (!isNull(fileName)) {
			int pos = fileName.lastIndexOf(".");
			if (fileName.lastIndexOf(".") > 0) {
				return getDirectoryName(filePath) + "/" + fileName.substring(0, pos) + "_raw" + fileName.substring(pos);
			}
		}
		return filePath;
	}

	public static String getRandomString(final int size) {
		char[] charset = { '2', '3', '4', '5', '6', '7', '8', '9', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'z', 'x', 'c', 'v', 'b', 'n', 'm' };
		Random random = new Random();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < size; i++) {
			buffer.append(charset[Math.abs(random.nextInt()) % charset.length]);
		}
		return buffer.toString();
	}

	public static Map<String, Object> getFileSize(final long originalSize) {
		float currentSize = originalSize;
		int times = 0;
		Map<String, Object> map = new HashMap<String, Object>();
		String[] currentUnit = { "B", "KB", "MB", "GB" };
		while (currentSize > 1000 && times < 4) {
			times++;
			currentSize = currentSize / 1024;
		}
		map.put("size", currentSize);
		map.put("unit", currentUnit[times]);
		return map;
	}

	public static String fromLongListToStringWithComma(List<Long> list) {
		StringBuffer sb = new StringBuffer();
		if (list != null && list.size() > 0) {
			for (Long id : list) {
				sb.append(id);
				sb.append(",");
			}
			return sb.toString().substring(0, sb.toString().length() - 1);
		} else {
			return null;
		}
	}

	public static String escapeSql(String str) {
		str = StringEscapeUtils.escapeSql(str);
		if (str.contains("_")) {
			str = str.replace("_", "\\_");
		}
		if (str.contains("%")) {
			str = str.replace("%", "\\%");
		}

		return str != null ? str.trim() : "";
	}

	public static String filterSpecialCharacter(String s) {
		String regex = "&";
		return s.replaceAll("&", " ");
	}

	public static String transferSpecialCharacter(String s) {
		if (s.contains("&amp;")) {
			s = s.replaceAll("&amp;", "&");
		}
		if (s.contains("&ldquo;")) {
			s = s.replaceAll("&ldquo;", "\"");
		}
		return s;
	}

	public static boolean checkEmail(String emails) {
		if (StringUtil.isNull(emails)) {
			return false;
		}
		String[] emailsArr = emails.split(",");
		for (int i = 0; i < emailsArr.length; i++) {
			if (!StringUtil.isEmail(emailsArr[i].trim())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断手机号phone是否正确格式
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean checkMobilePhone(String phone) {
		if (isNull(phone)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^(13[0-9]|15[0-3]|15[5-9]|18[6-9])[0-9]{8}$");
		Matcher matcher = pattern.matcher(phone);

		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	public static String filteHtmlTag(String originalStr) {
		return originalStr.replaceAll("<[^<]+?>", "");
	}

	public static String getRequestIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip != null) {
			Matcher m = ipPattern.matcher(ip);
			if (m.find()) {
				ip = m.group(1);
			}
		}
		return ip;
	}

	public static String getRequestLink(HttpServletRequest request) {
		String reqeustLink = request.getRequestURL().toString();
		if (request.getQueryString() != null) {
			reqeustLink += "?" + request.getQueryString();
		}
		return reqeustLink;
	}

	/**
	 * 获取�?��域名
	 * 
	 * @param link
	 * @return
	 */
	public static String getMainDomain(String link) {
		URL url = null;
		try {
			url = new URL(link);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (url == null) {
			return "";
		}
		String host = url.getHost();
		String cookieDomain = "";

		Matcher m = domainPattern.matcher(host);
		if (m.find()) {
			cookieDomain = m.group(1) + m.group(2) + (m.group(3) == null ? "" : m.group(3));
		}
		return cookieDomain;
	}

	/**
	 * get link path
	 * 
	 * @param link
	 * @return
	 */
	public static String getLinkPath(String link) {
		if (link == null) {
			return "/";
		}
		URL url = null;
		try {
			url = new URL(link);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (url == null) {
			return "/";
		}
		String[] path = url.getPath().split("/");
		// if (path.length() > 1 && path.endsWith("/")) {
		// path = path.substring(0, path.length() - 1);
		// }
		if (path.length > 2) {
			return "/" + path[1];
		} else {
			return "/";
		}

	}

	/**
	 * 去除注释标签
	 * 
	 * @param originalStr
	 * @return
	 */
	public static String formatString(String originalStr) {
		if (isNull(originalStr)) {
			return "";
		}
		originalStr = transferSpecialCharacter(originalStr);
		Pattern contentPattern = Pattern.compile("<\\!\\-\\-([\\s\\S]*)\\-\\->", Pattern.CASE_INSENSITIVE);
		Matcher contentMatcher = contentPattern.matcher(originalStr);
		String abstractContent = contentMatcher.replaceAll("");
		return abstractContent;
	}

	public static int getStringBytesLength(String name) {
		int l = 0;
		if (!isNull(name)) {
			byte[] b = name.getBytes();
			l = b.length;
		}
		return l;
	}

	/**
	 * 获取链接中的参数
	 */
	public static String getParameterFromLink(String link, String key) {
		if (link == null) {
			return null;
		}
		Map<String, String> param = new HashMap<String, String>();
		if (link.indexOf("?") > 0) {
			link = link.substring(link.indexOf("?") + 1);
			String[] paramArr = link.split("&");
			if (paramArr != null) {
				String p = "";
				String v = "";
				for (int i = 0; i < paramArr.length; i++) {
					int index = paramArr[i].indexOf("=");
					if (index > 0) {
						p = paramArr[i].substring(0, paramArr[i].indexOf("="));
						v = paramArr[i].substring(paramArr[i].indexOf("=") + 1);
						param.put(p, v);
					} else {
						index = paramArr[i].indexOf("%3D");
						if (index > 0) {
							p = paramArr[i].substring(0, paramArr[i].indexOf("%3D"));
							v = paramArr[i].substring(paramArr[i].indexOf("%3D") + 3);
							param.put(p, v);
						}
					}

				}
			}
		}
		return param.get(key);
	}

	public static String reverseString(String string) {
		StringBuffer reverse = new StringBuffer(string);
		return reverse.reverse().toString();
	}

	/**
	 * 判断�?��字符串是不是�?��中文单字
	 * 
	 * @author lhe
	 * @param word
	 * @return
	 */
	public static boolean isOneChineseWord(String word) {
		Matcher matcher = Pattern.compile("[\u4e00-\u9fa5]").matcher(word);
		if (word.getBytes().length == 1 || word.getBytes().length == 2) {
			if (matcher.find())
				return true;
			else
				return false;
		} else
			return false;
	}

	/**
	 * 过滤字符串中的\r\n
	 * 
	 * @param index
	 * @return
	 */
	public static String filterDefaultIndex(String index) {
		if (index == null) {
			return null;
		}
		char[] ca = index.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (char c : ca) {
			if (c != '\r' && c != '\n') {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 过滤字符串中的\r\n
	 * 
	 * @param str
	 * @return
	 */
	public static String filterString(String str) {
		Pattern p = Pattern.compile("(\\r)?\\n");
		Matcher matcher = p.matcher(str);
		str = matcher.replaceAll("");
		return str;
	}

	public static String removeTag(String s) {
		Pattern p = Pattern.compile("<\\s*p\\s*>|<\\s*/p\\s*>", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(s);
		s = m.replaceAll("");
		return s;
	}

	/**
	 * 对能改变html结构的字符（&�?�?�?）做转义
	 * 
	 * @param str
	 * @return
	 */
	public static String escapeSpecialHTMLchar(String str) {
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("\"", "&quot;");
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		return str;
	}

	/**
	 * 把html表示�?&amp;"�?&quot;"�?&lt;"�?&gt;"转化�?�?�?�?
	 * 
	 * @param str
	 * @return
	 */
	public static String deEscapeSpecialHTMLchar(String str) {
		str = str.replaceAll("&lt;", "<");
		str = str.replaceAll("&gt;", ">");
		str = str.replaceAll("&amp;", "&");
		str = str.replaceAll("&quot;", "\"");
		return str;
	}

	/**
	 * <p>
	 * Repeat a String <code>repeat</code> times to form a new String.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.repeat(null, 2) = null
	 * StringUtils.repeat(&quot;&quot;, 0) = &quot;&quot;
	 * StringUtils.repeat(&quot;&quot;, 2) = &quot;&quot;
	 * StringUtils.repeat(&quot;a&quot;, 3) = &quot;aaa&quot;
	 * StringUtils.repeat(&quot;ab&quot;, 2) = &quot;abab&quot;
	 * StringUtils.repeat(&quot;a&quot;, -2) = &quot;&quot;
	 * </pre>
	 * 
	 * @param str
	 *            the String to repeat, may be null
	 * @param repeat
	 *            number of times to repeat str, negative treated as zero
	 * @return a new String consisting of the original String repeated,
	 *         <code>null</code> if null String input
	 */
	public static String repeat(String str, int repeat) {
		if (str == null) {
			return null;
		}
		if (repeat <= 0) {
			return "";
		}
		int inputLength = str.length();
		if (repeat == 1 || inputLength == 0) {
			return str;
		}

		int outputLength = inputLength * repeat;
		switch (inputLength) {
		case 1:
			char ch = str.charAt(0);
			char[] output1 = new char[outputLength];
			for (int i = repeat - 1; i >= 0; i--) {
				output1[i] = ch;
			}
			return new String(output1);
		case 2:
			char ch0 = str.charAt(0);
			char ch1 = str.charAt(1);
			char[] output2 = new char[outputLength];
			for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
				output2[i] = ch0;
				output2[i + 1] = ch1;
			}
			return new String(output2);
		default:
			StringBuffer buf = new StringBuffer(outputLength);
			for (int i = 0; i < repeat; i++) {
				buf.append(str);
			}
			return buf.toString();
		}
	}

	/**
	 * <p>
	 * Gets the rightmost <code>len</code> characters of a String.
	 * </p>
	 * <p>
	 * If <code>len</code> characters are not available, or the String is
	 * <code>null</code>, the String will be returned without an an
	 * exception. An exception is thrown if len is negative.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.right(null, *) = null
	 * StringUtils.right(*, -ve) = &quot;&quot;
	 * StringUtils.right(&quot;&quot;, *) = &quot;&quot;
	 * StringUtils.right(&quot;abc&quot;, 0) = &quot;&quot;
	 * StringUtils.right(&quot;abc&quot;, 2) = &quot;bc&quot;
	 * StringUtils.right(&quot;abc&quot;, 4) = &quot;abc&quot;
	 * </pre>
	 * 
	 * @param str
	 *            the String to get the rightmost characters from, may be null
	 * @param len
	 *            the length of the required String, must be zero or positive
	 * @return the rightmost characters, <code>null</code> if null String
	 *         input
	 */
	public static String right(String str, int len) {
		if (str == null) {
			return null;
		}
		if (len < 0) {
			return "";
		}
		if (str.length() <= len) {
			return str;
		} else {
			return str.substring(str.length() - len);
		}
	}

	/**
	 * 标题中的关键字匹配并置红
	 * 
	 * @param keyWords
	 * @param title
	 * @return
	 */
	public static String replaceAllKeyWords(String keyWords, String title) {
		keyWords = keyWords.replaceAll("[\\[\\]\\^\\$\\.\\?\\*\\+\\(\\\\|\\{\\}%;]", "");
		Pattern pattern = Pattern.compile(keyWords, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(title);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) { // 查找符合pattern的字符串
			matcher.appendReplacement(sb, "<em style=\'color: #F00;\'>" + title.substring(matcher.start(), matcher.end()) + "</em>");
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	public static String convertSize(long size) {
		return convertSize(size, 1);
	}

	public static String convertSize(long size, int scale) {
		String retStr = "";
		if (size >= 1073741824) {
			retStr = round(size * 1.0 / 1073741824, scale, BigDecimal.ROUND_HALF_EVEN) + "GB";
		} else if (size >= 1048576) {
			retStr = round(size * 1.0 / 1048576, scale, BigDecimal.ROUND_HALF_EVEN) + "MB";
		} else if (size >= 1024) {
			retStr = round(size * 1.0 / 1024, scale, BigDecimal.ROUND_HALF_EVEN) + "KB";
		} else {
			retStr = round(size, scale, BigDecimal.ROUND_HALF_EVEN) + "B";
		}
		return retStr;
	}

	private static double round(double value, int scale, int roundingMode) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, roundingMode);
		double retValue = bd.doubleValue();
		bd = null;
		return retValue;
	}

	public static void main(String[] args) {
		System.out.println(convertSize(11122L, 2));
	}

	/**
	 * 如果是ablesky域名，则返回一级域名；否则返回全域名
	 * 
	 * @param link
	 * @return
	 */
	public static String getCookieMainDomain(String link) {
		URL url = null;
		try {
			url = new URL(link);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (url == null) {
			return "";
		}
		String mainDomain = AbleskyImConfig.ABLESKY_DOMAIN;
		String host = url.getHost();
		if (host.indexOf(mainDomain) == -1) {
			return host;
		}
		return mainDomain;
	}

	/**
	 * 返回当前访问域名
	 * 
	 * @param request
	 * @return
	 */
	public static String getCurrentDomain(HttpServletRequest request) {
		if (request == null) {
			return AbleskyImConfig.HTTP_SERVER;
		}
		String path = request.getContextPath();
		int port = request.getServerPort();
		String myDomain = request.getScheme() + "://" + request.getServerName();
		if (port != 80) {
			myDomain += ":" + request.getServerPort();
		}
		myDomain += path + "/";
		return myDomain;
	}
}
