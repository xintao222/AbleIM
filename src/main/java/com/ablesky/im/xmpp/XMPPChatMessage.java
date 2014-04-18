/**
 * 
 */
package com.ablesky.im.xmpp;

import com.ablesky.im.util.WebUtil;

/**
 * @author lhe E-mail: lhe@ablesky.com
 */
public class XMPPChatMessage {
	private String messageBody;
	private String messageSubject;
	private String messageFrom;
	private String fromResouce;
	private String fromScreenName;
	private String messageTo;
	private String toScreenName;
	private Long postTime;
	private String packetId;
	private Boolean senderEnable;
	private Boolean receiverEnable;
	private String type;

	public static String TYPE_NEW = "new";// 新消息
	public static String TYPE_HISTORY = "history";// 历史消息

	/**
	 * 
	 */
	public XMPPChatMessage() {
		// this("", "", "", "",new Long(System.currentTimeMillis()));
	}

	/**
	 * @param messageBody
	 * @param messageSubject
	 * @param messageFrom
	 * @param messageTo
	 * @param postTime
	 */
	public XMPPChatMessage(String messageBody, String messageSubject, String messageFrom, String fromResouce, String messageTo, Long postTime, String packetId) {
		super();
		this.messageBody = messageBody;
		this.messageSubject = messageSubject;
		this.messageFrom = messageFrom;
		this.fromResouce = fromResouce;
		this.messageTo = messageTo;
		this.postTime = postTime;
		this.packetId = packetId;
//		this.fromScreenName = WebUtil.getScreenName(messageFrom);
	}

	/**
	 * @return the messageBody
	 */
	public String getMessageBody() {
		return messageBody;
	}

	/**
	 * @param messageBody
	 *            the messageBody to set
	 */
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	/**
	 * @return the messageSubject
	 */
	public String getMessageSubject() {
		return messageSubject;
	}

	/**
	 * @param messageSubject
	 *            the messageSubject to set
	 */
	public void setMessageSubject(String messageSubject) {
		this.messageSubject = messageSubject;
	}

	public String getFromResouce() {
		return fromResouce;
	}

	public void setFromResouce(String fromResouce) {
		this.fromResouce = fromResouce;
	}

	/**
	 * @return the messageFrom
	 */
	public String getMessageFrom() {
		return messageFrom;
	}

	/**
	 * @param messageFrom
	 *            the messageFrom to set
	 */
	public void setMessageFrom(String messageFrom) {
		this.messageFrom = messageFrom;
	}

	/**
	 * @return the messageTo
	 */
	public String getMessageTo() {
		return messageTo;
	}

	/**
	 * @param messageTo
	 *            the messageTo to set
	 */
	public void setMessageTo(String messageTo) {
		this.messageTo = messageTo;
	}

	/**
	 * @return the postTime
	 */
	public Long getPostTime() {
		return postTime;
	}

	/**
	 * @param postTime
	 *            the postTime to set
	 */
	public void setPostTime(Long postTime) {
		this.postTime = postTime;
	}

	public String getPacketId() {
		return packetId;
	}

	public void setPacketId(String packetId) {
		this.packetId = packetId;
	}

	public Boolean getSenderEnable() {
		return senderEnable;
	}

	public void setSenderEnable(Boolean senderEnable) {
		this.senderEnable = senderEnable;
	}

	public Boolean getReceiverEnable() {
		return receiverEnable;
	}

	public void setReceiverEnable(Boolean receiverEnable) {
		this.receiverEnable = receiverEnable;
	}

	public String getFromScreenName() {
		return fromScreenName;
	}

	public void setFromScreenName(String fromScreenName) {
		this.fromScreenName = fromScreenName;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getToScreenName(){
		return this.toScreenName;
	}
	
	public void setToScreenName(String toScreenName){
		this.toScreenName = toScreenName;
	}

}
