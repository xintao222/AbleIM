/**
 * 
 */
package com.ablesky.im.xmpp;

/**
 * @author lhe E-mail: lhe@ablesky.com
 */
public class SubscribeRequestEntry {
	private String fromUserName;
	private String toUserName;
	private Boolean isOnline;

	/**
	 * @return the fromUserName
	 */
	public String getFromUserName() {
		return fromUserName;
	}

	/**
	 * @param fromUserName
	 *            the fromUserName to set
	 */
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	/**
	 * @return the toUserName
	 */
	public String getToUserName() {
		return toUserName;
	}

	/**
	 * @param toUserName
	 *            the toUserName to set
	 */
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	/**
	 * @return the isOnline
	 */
	public Boolean getIsOnline() {
		return isOnline;
	}

	/**
	 * @param isOnline
	 *            the isOnline to set
	 */
	public void setIsOnline(Boolean isOnline) {
		this.isOnline = isOnline;
	}
}
