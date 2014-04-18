package com.ablesky.im.xmpp;


public class AccountXmppDTO {
	private String username;
	private String screenname;

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setScreenname(String screenname) {
		this.screenname = screenname;
	}

	public String getScreenname() {
		return this.screenname;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		final AccountXmppDTO other = (AccountXmppDTO) obj;
		if (username == null) {
			if (other.getUsername() != null)
				return false;
		} else if (username.equals(other.getUsername()))
			return true;

		return false;
	}
}
