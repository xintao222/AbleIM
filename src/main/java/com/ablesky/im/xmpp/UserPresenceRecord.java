package com.ablesky.im.xmpp;

/**
 * 好友状态改变记录信息
 * 
 * @author kbian
 */
public class UserPresenceRecord implements java.io.Serializable {
	public static String STATUS_ONLINE = "available";// 上线
	public static String STATUS_OFFLINE = "unavailable";// 下线
	public static String SRC_WEB = "web";
	public static String SRC_CLIENT = "client";
	public static String SRC_ALL = "all";
	public static String MODE_HIDING = "xa"; // 隐身

	private String from;
	private String fromScreenName;
	private String fromWWW; // www环境下的全称
	private String to;
	private String toScreenName;
	private Long time;
	private String status;
	private String src;// 状态改变的来源

	public void setFromWWW(String fromWWW) {
		this.fromWWW = fromWWW;
	}

	public String getFromWWW() {
		return this.fromWWW;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getSrc() {
		return this.src;
	}

	public void setFromScreenName(String fromScreenName) {
		this.fromScreenName = fromScreenName;
	}

	public String getFromScreenName() {
		return this.fromScreenName;
	}

	public void setToScreenName(String toScreenName) {
		this.toScreenName = toScreenName;
	}

	public String getToScreenName() {
		return toScreenName;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		return from;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTo() {
		return to;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Long getTime() {
		return time;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

}
