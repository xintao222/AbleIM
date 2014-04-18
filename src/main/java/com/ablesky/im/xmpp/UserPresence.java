/**
 * 
 */
package com.ablesky.im.xmpp;

/**
 * @author lhe E-mail: lhe@ablesky.com
 */
public class UserPresence {
	private String jid;

	private Object status;

	private String type;

	private String text;

	public UserPresence() {
		this("", "", "", "");
	}

	public UserPresence(String _jid, String _status, String _type, String _text) {
		this.jid = _jid;
		this.status = _status;
		this.text = _text;
		this.type = _type;
	}

	// setters
	public void setJid(String jid) {
		this.jid = jid;
	}

	public void setStatus(Object status) {
		this.status = status;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setType(String type) {
		this.type = type;
	}

	// getters
	public String getJid() {
		return this.jid;
	}

	public Object getStatus() {
		return this.status;
	}

	public String getText() {
		return this.text;
	}

	public String getType() {
		return this.type;
	}

	/**
	 * @override toString() Method so the object is more readable
	 */
	@Override
	public String toString() {
		return this.jid + " " + this.status + " " + this.text + " " + this.type;
	}
}
